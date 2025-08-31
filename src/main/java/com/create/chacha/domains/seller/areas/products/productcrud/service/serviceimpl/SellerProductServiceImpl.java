package com.create.chacha.domains.seller.areas.products.productcrud.service.serviceimpl;

import com.create.chacha.common.util.S3Uploader;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.StoreRepository;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.request.FlagshipUpdateRequest;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.request.ProductCreateRequestDTO;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.response.FlagshipUpdateResponse;
import com.create.chacha.domains.seller.areas.products.productcrud.dto.response.ProductListItemDTO;
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
	
	private final StoreRepository storeRepo;
    private final ProductRepository productRepo;
    private final ProductImageRepository imageRepo;
    private final UpCategoryRepository upRepo;
    private final DownCategoryRepository downRepo;
    private final S3Uploader s3;

    @PersistenceContext
    private EntityManager em;
    
    private Long sellerIdByStoreUrl(String storeUrl) {
        return em.createQuery(
                "select s.id from StoreEntity st join st.seller s where st.url = :url", Long.class)
            .setParameter("url", storeUrl)
            .getSingleResult();
    }
    
    // 대표 상품 설정 / 해제
    @Transactional
    public FlagshipUpdateResponse toggleFlagship(String storeUrl, FlagshipUpdateRequest req) {
        var ids = (req == null ? null : req.getProductIds());
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("productIds는 최소 1개 이상이어야 합니다.");
        }

        // storeUrl -> sellerId (엔티티 로딩 X)
        Long sellerId = em.createQuery(
                "select s.id from StoreEntity st join st.seller s where st.url = :url", Long.class)
                .setParameter("url", storeUrl)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 storeUrl에 매핑된 판매자가 없습니다: " + storeUrl));

        // 소유권 검증 포함 조회
        List<ProductEntity> products = productRepo.findBySellerIdAndIdIn(sellerId, ids);
        if (products.size() != ids.size()) {
            throw new IllegalArgumentException("존재하지 않거나 해당 스토어 소유가 아닌 상품 id가 포함되어 있습니다.");
        }

        // 현재 스토어의 (삭제되지 않은) 대표상품 수
        long currentActive = productRepo.countActiveFlagshipBySellerId(sellerId);

        // 이번 요청에서 꺼질 수(1->0) / 켜질 수(0->1) 계산
        long willTurnOff = products.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsFlagship()))
                .count();

        // 켜질 대상: 지금 false이고, 삭제되지 않은 것만 허용
        List<ProductEntity> willTurnOnList = products.stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsFlagship()))   // 현재 false
                .toList();

        boolean hasDeletedToTurnOn = willTurnOnList.stream()
                .anyMatch(p -> Boolean.TRUE.equals(p.getIsDeleted())); // 삭제된 상품을 켜려는가?

        if (hasDeletedToTurnOn) {
            throw new IllegalStateException("삭제된 상품은 대표상품으로 설정할 수 없습니다.");
        }

        long willTurnOn = willTurnOnList.stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted())) // 안전하게 한 번 더
                .count();

        long after = currentActive - willTurnOff + willTurnOn;
        if (after > 3) {
            throw new IllegalStateException(
                    "대표상품은 스토어당 최대 3개까지 설정 가능합니다. " +
                    "(현재: " + currentActive + ", 켜짐 예정: " + willTurnOn + ", 꺼짐 예정: " + willTurnOff + ", 결과: " + after + ")");
        }

        // 조건 만족 → 실제 토글 적용
        for (ProductEntity p : products) {
            boolean now = Boolean.TRUE.equals(p.getIsFlagship());
            // 삭제된 상품은 켜기 금지이므로, now=false && isDeleted=true 인 케이스는 여기 도달하지 않게 이미 검증됨
            p.setIsFlagship(!now);
        }

        // flush는 @Transactional 종료 시 자동
        return FlagshipUpdateResponse.builder()
                .totalAfter((int) after)
                .affectedIds(products.stream().map(ProductEntity::getId).toList())
                .build();
    }

    
    // 상품 조회
    @Override
    @Transactional
    public List<ProductListItemDTO> getProductsByStoreUrl(String storeUrl) {
        Long sellerId = storeRepo.findSellerIdByUrl(storeUrl)
                .orElseThrow(() -> new IllegalArgumentException("해당 storeUrl에 매핑된 판매자가 없습니다: " + storeUrl));

        return productRepo.findListBySellerIdForStore(sellerId, ImageStatusEnum.THUMBNAIL);
    }
    
    // 상품 등록
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
                .isFlagship(false)
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
