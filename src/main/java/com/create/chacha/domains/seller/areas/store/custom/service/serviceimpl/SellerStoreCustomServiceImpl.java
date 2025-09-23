// com.create.chacha.domains.seller.areas.store.custom.service.serviceimpl.SellerStoreCustomServiceImpl.java
package com.create.chacha.domains.seller.areas.store.custom.service.serviceimpl;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.domains.seller.areas.store.custom.dto.request.StoreCustomUpdateRequestDTO;
import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO;
import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO.FontDTO;
import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO.IconDTO;
import com.create.chacha.domains.seller.areas.store.custom.repository.StoreCustomRepository;
import com.create.chacha.domains.seller.areas.store.custom.repository.StoreFontRepository;
import com.create.chacha.domains.seller.areas.store.custom.repository.StoreIconRepository;
import com.create.chacha.domains.seller.areas.store.custom.service.SellerStoreCustomService;
import com.create.chacha.domains.shared.entity.store.StoreCustomEntity;
import com.create.chacha.domains.shared.entity.store.StoreFontEntity;
import com.create.chacha.domains.shared.entity.store.StoreIconEntity;
import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.common.util.dto.LegacyStoreDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerStoreCustomServiceImpl implements SellerStoreCustomService {

    private final StoreCustomRepository storeCustomRepository;
    private final StoreFontRepository storeFontRepository;
    private final StoreIconRepository storeIconRepository;
    private final LegacyAPIUtil legacyAPIUtil; // storeUrl -> storeId

    private static final Pattern HEX6 = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    // 프로젝트 기본값
    private static final Long DEFAULT_FONT_ID = 1L;
    private static final Long DEFAULT_ICON_ID = 1L;

    private static final String DEFAULT_FONT_COLOR = "#000000";
    private static final String DEFAULT_HEADER_FOOTER_COLOR = "#676F58";
    private static final String DEFAULT_NOTICE_COLOR = "#FFF7DB";
    private static final String DEFAULT_DESCRIPTION_COLOR = "#FFF6EE";
    private static final String DEFAULT_POPULAR_COLOR = "#FFF7DB";

    @Override
    @Transactional
    public UpsertResult patchUpsert(String storeUrl, StoreCustomUpdateRequestDTO req) {
        Long storeId = resolveStoreIdFromLegacy(storeUrl);  // ★ 항상 여기서 변환

        StoreCustomEntity sc = storeCustomRepository.findById(storeId).orElse(null);
        boolean created = false;

        if (sc == null) {
            StoreFontEntity font = resolveFont(firstNonNull(req.getFontId(), DEFAULT_FONT_ID));
            StoreIconEntity icon = resolveIcon(firstNonNull(req.getIconId(), DEFAULT_ICON_ID));
            sc = StoreCustomEntity.builder()
                    .storeId(storeId)
                    .font(font)
                    .icon(icon)
                    .fontColor(DEFAULT_FONT_COLOR)
                    .headerFooterColor(DEFAULT_HEADER_FOOTER_COLOR)
                    .noticeColor(DEFAULT_NOTICE_COLOR)
                    .descriptionColor(DEFAULT_DESCRIPTION_COLOR)
                    .popularColor(DEFAULT_POPULAR_COLOR)
                    .build();
            created = true;
        }

        if (req.getFontId() != null) sc.setFont(resolveFont(req.getFontId()));
        if (req.getIconId() != null) sc.setIcon(resolveIcon(req.getIconId()));

        applyHexIfPresent(req.getFontColor(), sc::setFontColor);
        applyHexIfPresent(req.getHeaderFooterColor(), sc::setHeaderFooterColor);
        applyHexIfPresent(req.getNoticeColor(), sc::setNoticeColor);
        applyHexIfPresent(req.getDescriptionColor(), sc::setDescriptionColor);
        applyHexIfPresent(req.getPopularColor(), sc::setPopularColor);

        StoreCustomEntity saved = storeCustomRepository.save(sc);
        return new UpsertResult(created, toDto(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public StoreCustomGetResponseDTO getStoreCustom(String storeUrl) {
        Long storeId = resolveStoreIdFromLegacy(storeUrl);  // ★ 여기서도 변환
        return storeCustomRepository.findById(storeId)
                .map(SellerStoreCustomServiceImpl::toDto)
                .orElse(null);
    }
    
    private Long resolveStoreIdFromLegacy(String storeUrl) {
        LegacyStoreDTO legacy = legacyAPIUtil.getLegacyStoreData(storeUrl);
        if (legacy == null) throw new IllegalArgumentException("STORE_NOT_FOUND");

        // 프로젝트 DTO에 맞게 한 줄로 바꿔도 됨 (예: legacy.getStoreId())
        Long storeId = null;
        try {
            Object v = legacy.getClass().getMethod("getStoreId").invoke(legacy);
            if (v instanceof Integer i) storeId = i.longValue();
            else if (v instanceof Long l) storeId = l;
        } catch (Exception ignore) {}
        if (storeId == null) {
            try {
                Object v = legacy.getClass().getMethod("getId").invoke(legacy);
                if (v instanceof Integer i) storeId = i.longValue();
                else if (v instanceof Long l) storeId = l;
            } catch (Exception ignore) {}
        }
        if (storeId == null) throw new IllegalArgumentException("STORE_NOT_FOUND");
        return storeId;
    }

    private StoreFontEntity resolveFont(Long id) {
        return storeFontRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FONT_NOT_FOUND"));
    }

    private StoreIconEntity resolveIcon(Long id) {
        return storeIconRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ICON_NOT_FOUND"));
    }

    private void applyHexIfPresent(String value, java.util.function.Consumer<String> setter) {
        if (value == null || value.isBlank()) return;
        String v = value.trim();
        if (!HEX6.matcher(v).matches()) throw new IllegalArgumentException("INVALID_HEX");
        setter.accept(v.toUpperCase());
    }

    private static <T> T firstNonNull(T a, T b) { return a != null ? a : b; }

    private static StoreCustomGetResponseDTO toDto(StoreCustomEntity sc) {
        StoreFontEntity font = sc.getFont();
        StoreIconEntity icon = sc.getIcon();
        return StoreCustomGetResponseDTO.builder()
                .storeId(sc.getStoreId())
                .font(font == null ? null :
                        new FontDTO(font.getId(), font.getName(), font.getStyle(), font.getUrl()))
                .icon(icon == null ? null :
                        new IconDTO(icon.getId(), icon.getName(), icon.getContent(), icon.getUrl()))
                .fontColor(sc.getFontColor())
                .headerFooterColor(sc.getHeaderFooterColor())
                .noticeColor(sc.getNoticeColor())
                .descriptionColor(sc.getDescriptionColor())
                .popularColor(sc.getPopularColor())
                .createdAt(sc.getCreatedAt())
                .updatedAt(sc.getUpdatedAt())
                .build();
    }
}
