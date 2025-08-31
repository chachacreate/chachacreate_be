package com.create.chacha.domains.seller.areas.products.productcrud.service.serviceimpl;

import com.create.chacha.common.util.S3Uploader;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.request.ProductCreateRequestDTO;
import com.create.chacha.domains.seller.areas.products.productcrud.repository.DownCategoryRepository;
import com.create.chacha.domains.seller.areas.products.productcrud.repository.ProductImageRepository;
import com.create.chacha.domains.seller.areas.products.productcrud.repository.ProductRepository;
import com.create.chacha.domains.seller.areas.products.productcrud.repository.UpCategoryRepository;
import com.create.chacha.domains.seller.areas.products.productcrud.service.SellerProductService;
import com.create.chacha.domains.shared.constants.ImageStatusEnum;
import com.create.chacha.domains.shared.entity.category.DownCategoryEntity;
import com.create.chacha.domains.shared.entity.category.UpCategoryEntity;
import com.create.chacha.domains.shared.entity.product.ProductEntity;
import com.create.chacha.domains.shared.entity.product.ProductImageEntity;
import com.create.chacha.domains.shared.entity.seller.SellerEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerProductServiceImpl implements SellerProductService {

    private final ProductRepository productRepo;
    private final ProductImageRepository imageRepo;
    private final UpCategoryRepository upRepo;
    private final DownCategoryRepository downRepo;
    private final S3Uploader s3;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public List<Long> createProducts(String storeUrl, List<ProductCreateRequestDTO> reqs) {
        if (reqs == null || reqs.isEmpty()) {
            throw new IllegalArgumentException("등록할 상품이 없습니다.");
        }

        // storeUrl -> sellerId만 조회 (SellerEntity 전체 로딩 금지: AES 컨버터 이슈 회피)
        Long sellerId = em.createQuery(
                "select s.id from StoreEntity st join st.seller s where st.url = :url", Long.class)
                .setParameter("url", storeUrl)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 storeUrl에 매핑된 판매자가 없습니다: " + storeUrl));

        // 실제 연관은 프록시로만 세팅
        SellerEntity sellerProxy = em.getReference(SellerEntity.class, sellerId);

        List<Long> ids = new ArrayList<>();
        for (ProductCreateRequestDTO req : reqs) {
            ids.add(createOne(req, sellerProxy));
        }
        return ids;
    }

    /** 단일 생성(내부 전용) */
    @Transactional
    protected Long createOne(ProductCreateRequestDTO req, SellerEntity seller) {
        var p = req.getProduct();
        if (p == null) throw new IllegalArgumentException("product(JSON) is required.");

        // 본문 검증
        if (p.getName() == null || p.getName().isBlank()) throw new IllegalArgumentException("name은 필수입니다.");
        if (p.getPrice() == null || p.getPrice() < 0)     throw new IllegalArgumentException("price는 0 이상이어야 합니다.");
        if (p.getStock() == null || p.getStock() < 0)     throw new IllegalArgumentException("stock은 0 이상이어야 합니다.");

        // 카테고리 검증
        UpCategoryEntity up = upRepo.findById(p.getUpCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대분류입니다."));
        DownCategoryEntity down = downRepo.findById(p.getDownCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소분류입니다."));
        if (!down.getUpCategory().getId().equals(up.getId())) {
            throw new IllegalArgumentException("소분류가 대분류에 속하지 않습니다.");
        }

        // 상품 저장
        ProductEntity entity = ProductEntity.builder()
                .seller(seller)
                .downCategory(down)
                .name(p.getName())
                .price(p.getPrice())
                .detail(p.getDetail())
                .stock(p.getStock())
                .isFlagship(Boolean.TRUE.equals(p.getIsFlagship()))
                .build();
        entity.setIsDeleted(false);

        ProductEntity saved = productRepo.save(entity);

        // 이미지 업로드/저장
        List<String> uploadedKeys = new ArrayList<>();
        try {
            // 썸네일 3장 필수
            MultipartFile[] thumbs = req.getThumbnails();
            if (thumbs == null || thumbs.length != 3) {
                throw new IllegalArgumentException("썸네일 이미지는 정확히 3장이어야 합니다.");
            }
            int seq = 1;
            List<ProductImageEntity> toSave = new ArrayList<>();

            for (int i = 0; i < thumbs.length; i++) {
                MultipartFile f = thumbs[i];
                if (f == null || f.isEmpty()) {
                    throw new IllegalArgumentException("썸네일 파일이 비어있습니다. index=" + i);
                }
                String originKey = s3.uploadImage(f);
                uploadedKeys.add(originKey);
                String thumbUrl = s3.getThumbnailUrl(originKey);

                ProductImageEntity e = ProductImageEntity.builder()
                        .product(saved)
                        .status(ImageStatusEnum.THUMBNAIL)
                        .imageSequence(seq++)
                        .isDeleted(false)
                        .build();
                e.setUrl(thumbUrl);
                toSave.add(e);
            }

            // 설명 N장 (선택)
            MultipartFile[] descs = req.getDescriptions();
            if (descs != null && descs.length > 0) {
                int dseq = 1;
                for (int i = 0; i < descs.length; i++) {
                    MultipartFile f = descs[i];
                    if (f == null || f.isEmpty()) continue;

                    String originKey = s3.uploadImage(f);
                    uploadedKeys.add(originKey);
                    String originUrl = s3.getFullUrl(originKey);

                    ProductImageEntity e = ProductImageEntity.builder()
                            .product(saved)
                            .status(ImageStatusEnum.DESCRIPTION)
                            .imageSequence(dseq++)
                            .isDeleted(false)
                            .build();
                    e.setUrl(originUrl);
                    toSave.add(e);
                }
            }

            if (!toSave.isEmpty()) imageRepo.saveAll(toSave);
            return saved.getId();

        } catch (Exception e) {
            // 업로드 일부 성공 후 예외 시 S3 정리(최대한)
            for (String key : uploadedKeys) {
                try { s3.delete(key); } catch (Exception ignore) { }
            }
            throw new RuntimeException("상품 등록 처리 중 오류: " + e.getMessage(), e);
        }
    }
}
