package com.create.chacha.domains.buyer.areas.classes.classdetail.service.serviceimpl;

import static com.create.chacha.domains.shared.constants.ImageStatusEnum.DESCRIPTION;
import static com.create.chacha.domains.shared.constants.ImageStatusEnum.THUMBNAIL;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassImagesResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassScheduleResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassSummaryResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.service.ClassDetailService;
import com.create.chacha.domains.buyer.areas.classes.classlist.repository.ClassInfoRepository;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.ClassImageRepository;
import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;
import com.create.chacha.domains.shared.repository.ClassScheduleRepository;

import lombok.RequiredArgsConstructor;;
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClassDetailServiceImpl implements ClassDetailService {

    private final ClassInfoRepository classInfoRepository;
    private final ClassImageRepository classImageRepository;
    private final ClassScheduleRepository classScheduleRepository;
    
    private final LegacyAPIUtil legacyAPIUtil;

    /**
     * 프론트로 내보낼 이미지 도메인(CDN 또는 S3 퍼블릭 도메인)
     * 예: https://chachacreate-images.s3.amazonaws.com/
     * 마지막 슬래시는 없어도 되고, 아래에서 보정한다.
     */
    @Value("${app.cdn-base-url:https://chachacreate-images.s3.amazonaws.com/}")
    private String cdnBaseUrl;

    @Override
    public ClassSummaryResponseDTO getSummary(Long classId) {
    	ClassInfoEntity ci = classInfoRepository.findById(classId)   // JpaRepository 기본 메서드
    		    .orElseThrow(() -> new NoSuchElementException("클래스를 찾을 수 없습니다: " + classId));

        // ⚠️ StoreEntity 전체를 로딩하지 않고, FK(id)만 안전하게 꺼냄
        Long storeId = null;
        if (ci.getStore() != null) {
            // Hibernate 프록시여도 getId() 호출은 추가 로딩 없이 동작(테이블 접근 안 함)
            storeId = ci.getStore().getId();
        }

        // 레거시에서 스토어 정보 조회 (DB의 store 테이블이 없어도 OK)
        String storeName = null;
        String storeContent = null;
        try {
            if (storeId != null) {
                var legacyStore = legacyAPIUtil.getLegacyStoreDataById(storeId);
                if (legacyStore != null) {
                    storeName = legacyStore.getStoreName();
                    storeContent = legacyStore.getStoreDetail();
                }
            }
        } catch (Exception e) {
            // 레거시 연동 실패해도 요약 자체는 내려가게만 함 (필요시 warn 로그)
            // log.warn("레거시 스토어 조회 실패 storeId={}", storeId, e);
        }

        return ClassSummaryResponseDTO.builder()
                .classId(ci.getId())
                .title(ci.getTitle())
                .description(ci.getDetail())
                .price(ci.getPrice())
                .guideline(ci.getGuideline())
                .postNum(ci.getPostNum())
                .addressRoad(ci.getAddressRoad())
                .addressDetail(ci.getAddressDetail())
                .addressExtra(ci.getAddressExtra())
                .storeId(storeId)           // ← FK 그대로 사용(Long)
                .storeName(storeName)       // ← 레거시에서 채움(없으면 null)
                .storeContent(storeContent) // ← 레거시에서 채움(없으면 null)
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

        var rows = classImageRepository.findImagesForDetail(classId, THUMBNAIL, DESCRIPTION);

        return ClassImagesResponseDTO.builder()
            .classId(classId)
            .images(
                rows.stream()
                    .map(img -> {
                        String key = toKey(img.getUrl());
                        return ClassImagesResponseDTO.Image.builder()
                                .url(fullUrl(base, key))
                                .thumbnailUrl(thumbnailUrlFromKey(base, key))
                                .sequence(img.getImageSequence())
                                .build();
                    })
                    .toList()
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
