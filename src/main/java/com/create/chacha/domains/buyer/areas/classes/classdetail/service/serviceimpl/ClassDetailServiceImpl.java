package com.create.chacha.domains.buyer.areas.classes.classdetail.service.serviceimpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassImagesResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassScheduleResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassSummaryResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.service.ClassDetailService;
import com.create.chacha.domains.buyer.areas.classes.classlist.repository.ClassInfoRepository;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.ClassImageRepository;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.StoreRepository;
import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;
import com.create.chacha.domains.shared.entity.store.StoreEntity;
import com.create.chacha.domains.shared.repository.ClassScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClassDetailServiceImpl implements ClassDetailService {

    private final ClassInfoRepository classInfoRepository;
    private final StoreRepository storeRepository;
    private final ClassImageRepository classImageRepository;
    private final ClassScheduleRepository classScheduleRepository;

    /**
     * 프론트로 내보낼 이미지 도메인(CDN 또는 S3 퍼블릭 도메인)
     * 예: https://chachacreate-images.s3.amazonaws.com/
     * 마지막 슬래시는 없어도 되고, 아래에서 보정한다.
     */
    @Value("${app.cdn-base-url:https://chachacreate-images.s3.amazonaws.com/}")
    private String cdnBaseUrl;

    @Override
    public ClassSummaryResponseDTO getSummary(Long classId) {
        ClassInfoEntity ci = classInfoRepository.findByClassId(classId)
                .orElseThrow(() -> new NoSuchElementException("클래스를 찾을 수 없습니다: " + classId));

        StoreEntity store = storeRepository.findById(ci.getStore().getId())
                .orElseThrow(() -> new NoSuchElementException("스토어를 찾을 수 없습니다: " + ci.getStore().getId()));

        return ClassSummaryResponseDTO.builder()
                .classId(ci.getId())
                .title(ci.getTitle())
                .description(ci.getDetail())
                .price(ci.getPrice())
                .postNum(ci.getPostNum())
                .addressRoad(ci.getAddressRoad())
                .addressDetail(ci.getAddressDetail())
                .addressExtra(ci.getAddressExtra())
                .storeId(store.getId())
                .storeName(store.getName())
                .storeContent(store.getContent())
                .build();
    }

    /**
     * ✅ 이 메서드만 수정하면 된다.
     * - DB에 "키"와 "절대 URL"이 섞여 있어도 안전
     * - 항상 "키"로 정규화(toKey) → full URL/thumbnail URL 생성
     * - S3Uploader는 건드리지 않음
     */
    @Override
    public ClassImagesResponseDTO getImages(Long classId) {
        final String base = ensureTrailingSlash(cdnBaseUrl);

        return ClassImagesResponseDTO.builder()
            .classId(classId)
            .images(
                classImageRepository.findByClassInfo_Id(classId)
                    .stream()
                    .map(img -> {
                        // img.getUrl() 이 "키"든 "절대 URL"이든 안전하게 키로 정규화
                        String key = toKey(img.getUrl());

                        String url = fullUrl(base, key);              // 원본 full URL
                        String thumbnailUrl = thumbnailUrlFromKey(base, key); // 썸네일 full URL

                        return ClassImagesResponseDTO.Image.builder()
                                .url(url)
                                .thumbnailUrl(thumbnailUrl)
                                .sequence(img.getImageSequence())
                                .build();
                    })
                    .collect(Collectors.toList())
            )
            .build();
    }

    @Override
    public List<ClassScheduleResponseDTO> getSchedule(Long classId) {
        List<Object[]> results = classScheduleRepository.findClassSchedule(classId);
        return results.stream()
                .map(ClassScheduleResponseDTO::of)
                .toList();
    }

    /* ===================== 아래는 서비스 내부 헬퍼 ===================== */

    /** 절대 URL 여부 */
    private boolean isAbsoluteUrl(String s) {
        if (s == null) return false;
        return s.startsWith("http://") || s.startsWith("https://");
    }

    /** 앞의 슬래시 제거 */
    private String stripLeadingSlash(String s) {
        if (s == null) return "";
        return s.startsWith("/") ? s.substring(1) : s;
    }

    /**
     * "키" 혹은 "절대 URL"이 들어와도 항상 "키"만 돌려준다.
     * 예)
     *   - images/original/abc.webp → images/original/abc.webp
     *   - /images/original/abc.webp → images/original/abc.webp
     *   - https://bucket.s3.amazonaws.com/images/original/abc.webp → images/original/abc.webp
     */
    private String toKey(String keyOrUrl) {
        if (keyOrUrl == null || keyOrUrl.isBlank()) return "";
        String s = keyOrUrl.trim();
        if (!isAbsoluteUrl(s)) {
            return stripLeadingSlash(s);
        }
        try {
            java.net.URI u = java.net.URI.create(s);
            String path = u.getPath(); // "/images/original/abc.webp"
            return stripLeadingSlash(path != null ? path : "");
        } catch (Exception e) {
            // 비상 처리: 프로토콜/도메인만 제거
            String tmp = s.replaceFirst("^https?://[^/]+/", "");
            return stripLeadingSlash(tmp);
        }
    }

    /** base가 슬래시로 끝나도록 보정 */
    private String ensureTrailingSlash(String base) {
        if (base == null || base.isBlank()) return "";
        return base.endsWith("/") ? base : (base + "/");
    }

    /** 원본 키 → full URL */
    private String fullUrl(String base, String key) {
        if (key == null || key.isBlank()) return "";
        return base + stripLeadingSlash(key);
    }

    /**
     * 원본 키 → 썸네일 키 규칙 → full URL
     * - images/original/ → images/thumbnail/
     * - .webp → _thumb.webp
     * - 이미 썸네일 키면 그대로 사용
     */
    private String thumbnailUrlFromKey(String base, String originalKey) {
        if (originalKey == null || originalKey.isBlank()) return "";

        String key = stripLeadingSlash(originalKey);

        // 이미 썸네일 키면 그대로
        if (key.contains("/images/thumbnail/") || key.endsWith("_thumb.webp")) {
            return fullUrl(base, key);
        }

        String thumbKey = key;
        if (thumbKey.startsWith("images/original/")) {
            thumbKey = thumbKey.replaceFirst("^images/original/", "images/thumbnail/");
        } else if (!thumbKey.startsWith("images/thumbnail/")) {
            thumbKey = "images/thumbnail/" + thumbKey;
        }

        if (thumbKey.endsWith(".webp")) {
            thumbKey = thumbKey.substring(0, thumbKey.length() - 5) + "_thumb.webp";
        } else if (!thumbKey.endsWith("_thumb.webp")) {
            thumbKey = thumbKey + "_thumb.webp";
        }

        return fullUrl(base, thumbKey);
    }
}
