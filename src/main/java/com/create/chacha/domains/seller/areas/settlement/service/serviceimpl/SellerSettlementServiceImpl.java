package com.create.chacha.domains.seller.areas.settlement.service.serviceimpl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.seller.areas.settlement.dto.response.SettlementResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.service.SellerSettlementService;
import com.create.chacha.domains.shared.repository.SellerClassSettlementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 판매자 정산 조회 서비스 구현
 * - 네이티브 Projection -> DTO 매핑
 * - 민감정보(AES) 복호화 안정 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerSettlementServiceImpl implements SellerSettlementService {

    private final SellerClassSettlementRepository repository;

    private final AESConverter aesConverter;

    @Override
    public List<SettlementResponseDTO> getSettlementsByMemberId(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId must not be null");
        }

        var rows = repository.findSettlementsByMemberId(memberId);

        return rows.stream()
            .map(p -> SettlementResponseDTO.builder()
                .settlementDate(p.getSettlementDate())
                .amount(p.getAmount() == null ? 0L : p.getAmount())
                .account(decryptSafe(p.getAccount()))
                .bank(decryptSafe(p.getBank()))
                .name(decryptSafe(p.getName()))
                .status(p.getStatus() == null ? 0 : p.getStatus())
                .updateAt(p.getUpdateAt())
                .build())
            .toList();
    }

    /**
     * AES 복호화 (실패시 원문 유지)
     * - 헤더/DB 데이터가 혼재하거나 마이그레이션 단계에서 안전
     */
    private String decryptSafe(String enc) {
        if (enc == null || enc.isBlank()) return enc;
        try {
            return aesConverter.convertToEntityAttribute(enc);
        } catch (Exception e) {
            log.warn("AES decrypt failed, return as-is. msg={}", e.getMessage());
            return enc;
        }
    }
}
