package com.create.chacha.domains.seller.areas.store.custom.service.serviceimpl;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerStoreCustomServiceImpl implements SellerStoreCustomService {

    private final StoreCustomRepository storeCustomRepository;
    private final StoreFontRepository storeFontRepository;
    private final StoreIconRepository storeIconRepository;

    private static final Pattern HEX_3_6 = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    // 스토어 커스텀 수정
    @Override
    @Transactional
    public StoreCustomGetResponseDTO updateStoreCustom(String storeUrl, StoreCustomUpdateRequestDTO req) {
        // 없으면 null 반환 → 컨트롤러에서 404 처리
        StoreCustomEntity sc = storeCustomRepository.findByStore_Url(storeUrl).orElse(null);
        if (sc == null) return null;

        // 폰트/아이콘 유효성 체크
        if (req.getFontId() != null) {
            StoreFontEntity font = storeFontRepository.findById(req.getFontId()).orElse(null);
            if (font == null) return null; // 잘못된 요청은 null → 400
            sc.setFont(font);
        }
        if (req.getIconId() != null) {
            StoreIconEntity icon = storeIconRepository.findById(req.getIconId()).orElse(null);
            if (icon == null) return null; // 잘못된 요청은 null → 400
            sc.setIcon(icon);
        }

        // 색상 적용
        applyHexIfPresent(req.getFontColor(), sc::setFontColor);
        applyHexIfPresent(req.getHeaderFooterColor(), sc::setHeaderFooterColor);
        applyHexIfPresent(req.getNoticeColor(), sc::setNoticeColor);
        applyHexIfPresent(req.getDescriptionColor(), sc::setDescriptionColor);
        applyHexIfPresent(req.getPopularColor(), sc::setPopularColor);

        sc.setUpdatedAt(LocalDateTime.now());
        return toDto(storeCustomRepository.save(sc));
    }

    private void applyHexIfPresent(String value, java.util.function.Consumer<String> setter) {
        if (value == null) return;
        String v = value.trim();
        if (v.isEmpty()) return;
        if (!HEX_3_6.matcher(v).matches()) return;

        setter.accept(v.toUpperCase());
    }

    // 조회
    @Override
    @Transactional
    public StoreCustomGetResponseDTO getStoreCustom(String storeUrl) {
        return storeCustomRepository.findByStore_Url(storeUrl)
                .map(SellerStoreCustomServiceImpl::toDto)
                .orElse(null);
    }

    private static StoreCustomGetResponseDTO toDto(StoreCustomEntity sc) {
        StoreFontEntity font = sc.getFont();
        StoreIconEntity icon = sc.getIcon();

        return StoreCustomGetResponseDTO.builder()
                .storeId(sc.getStore().getId())
                .font(font == null ? null : new FontDTO(font.getId(), font.getName(), font.getStyle(), font.getUrl()))
                .icon(icon == null ? null : new IconDTO(icon.getId(), icon.getName(), icon.getContent(), icon.getUrl()))
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
