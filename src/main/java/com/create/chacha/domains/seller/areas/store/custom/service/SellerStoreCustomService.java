package com.create.chacha.domains.seller.areas.store.custom.service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.create.chacha.domains.seller.areas.classes.classcrud.repository.StoreRepository;
import com.create.chacha.domains.seller.areas.store.custom.dto.request.StoreCustomUpdateRequestDTO;
import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO;
import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO.FontDTO;
import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO.IconDTO;
import com.create.chacha.domains.seller.areas.store.custom.repository.StoreCustomRepository;
import com.create.chacha.domains.seller.areas.store.custom.repository.StoreFontRepository;
import com.create.chacha.domains.seller.areas.store.custom.repository.StoreIconRepository;
import com.create.chacha.domains.seller.areas.store.custom.service.serviceimpl.SellerStoreCustomServiceImpl;
import com.create.chacha.domains.shared.entity.store.StoreCustomEntity;
import com.create.chacha.domains.shared.entity.store.StoreFontEntity;
import com.create.chacha.domains.shared.entity.store.StoreIconEntity;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerStoreCustomService implements SellerStoreCustomServiceImpl {

    private final StoreCustomRepository storeCustomRepository;
    private final StoreFontRepository storeFontRepository;
    private final StoreIconRepository storeIconRepository;
    
    private static final Pattern HEX_3_6 = Pattern.compile("^#[0-9A-Fa-f]{6}$");
    
    // 스토어 커스텀 수정
    @Override
    @Transactional
    public StoreCustomGetResponseDTO updateStoreCustom(String storeUrl, StoreCustomUpdateRequestDTO req) {
        // ❗없으면 생성하지 않고 404
        StoreCustomEntity sc = storeCustomRepository.findByStore_Url(storeUrl)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "커스텀 설정이 없습니다: " + storeUrl));

        // 폰트/아이콘: id가 전달된 경우에만 교체
        if (req.getFontId() != null) {
            StoreFontEntity font = storeFontRepository.findById(req.getFontId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 폰트 ID: " + req.getFontId()));
            sc.setFont(font);
        }
        if (req.getIconId() != null) {
            StoreIconEntity icon = storeIconRepository.findById(req.getIconId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 아이콘 ID: " + req.getIconId()));
            sc.setIcon(icon);
        }

        // 색상: 전달된 키만 반영, 잘못된 형식은 '스킵'(요청 전체 실패시키지 않음)
        applyHexIfPresent(req.getFontColor(), sc::setFontColor, "fontColor");
        applyHexIfPresent(req.getHeaderFooterColor(), sc::setHeaderFooterColor, "headerFooterColor");
        applyHexIfPresent(req.getNoticeColor(), sc::setNoticeColor, "noticeColor");
        applyHexIfPresent(req.getDescriptionColor(), sc::setDescriptionColor, "descriptionColor");
        applyHexIfPresent(req.getPopularColor(), sc::setPopularColor, "popularColor");

        sc.setUpdatedAt(LocalDateTime.now());

        return toDto(storeCustomRepository.save(sc));
    }

    private void applyHexIfPresent(String value, java.util.function.Consumer<String> setter, String field) {
        if (value == null) return;             // 부분 업데이트: 누락 키는 무시
        String v = value.trim();
        if (v.isEmpty()) return;               // 빈 문자열도 무시(원하면 삭제 의미로 바꿔도 됨)
        if (!HEX_3_6.matcher(v).matches()) {
            // 잘못된 형식은 스킵(엄격 모드로 바꾸려면 여기서 예외 던지기)
            return;
        }
        if (v.length() == 4) { // #RGB -> #RRGGBB
            v = "#" + v.substring(1, 2).repeat(2)
                   + v.substring(2, 3).repeat(2)
                   + v.substring(3, 4).repeat(2);
        }
        setter.accept(v.toUpperCase());
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
    
    // 스토어 커스텀 조회
    @Override
    @Transactional
    public StoreCustomGetResponseDTO getStoreCustom(String storeUrl) {
        StoreCustomEntity sc = storeCustomRepository.findByStore_Url(storeUrl)
                .orElseThrow(() -> new IllegalArgumentException("커스텀 설정이 없습니다: " + storeUrl));

        StoreFontEntity font = sc.getFont();
        StoreIconEntity icon = sc.getIcon();

        return StoreCustomGetResponseDTO.builder()
                .storeId(sc.getStore().getId())
                .font(font == null ? null : new FontDTO(
                        font.getId(), font.getName(), font.getStyle(), font.getUrl()))
                .icon(icon == null ? null : new IconDTO(
                        icon.getId(), icon.getName(), icon.getContent(), icon.getUrl()))
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
