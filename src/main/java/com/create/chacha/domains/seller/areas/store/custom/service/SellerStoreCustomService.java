package com.create.chacha.domains.seller.areas.store.custom.service;

import org.springframework.stereotype.Service;

import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO;
import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO.FontDTO;
import com.create.chacha.domains.seller.areas.store.custom.dto.response.StoreCustomGetResponseDTO.IconDTO;
import com.create.chacha.domains.seller.areas.store.custom.repository.StoreCustomRepository;
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
