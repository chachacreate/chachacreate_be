package com.create.chacha.domains.seller.areas.settlement.service.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.config.app.database.AESConverter;
import com.create.chacha.domains.seller.areas.settlement.dto.response.SettlementResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.repository.SettlementResponseProjection;
import com.create.chacha.domains.seller.areas.settlement.service.SellerSettlementService;
import com.create.chacha.domains.shared.repository.SellerClassSettlementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 판매자 정산 조회 서비스 구현
 * - Native/JPQL Projection -> DTO 매핑
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
        if (memberId == null) throw new IllegalArgumentException("memberId must not be null");
        log.info("판매자 전체 정산 조회 Service 실행, memberId={}", memberId);

        List<SettlementResponseProjection> rows = repository.findSettlementsByMemberId(memberId);
        return rows.stream().map(this::toDto).toList();
    }

    @Override
    public List<SettlementResponseDTO> getSettlementsByMemberAndClass(Long memberId, Long classId) {
        if (memberId == null) throw new IllegalArgumentException("memberId must not be null");
        if (classId == null) { // classId 미지정 시 전체로 위임
            return getSettlementsByMemberId(memberId);
        }
        log.info("특정 클래스 정산 조회 Service 실행, memberId={}, classId={}", memberId, classId);

        List<SettlementResponseProjection> rows =
                repository.findSettlementsByMemberIdAndClassId(memberId, classId);
        return rows.stream().map(this::toDto).toList();
    }

    private SettlementResponseDTO toDto(SettlementResponseProjection p) {
        return SettlementResponseDTO.builder()
            .settlementDate(p.getSettlementDate())
            .amount(p.getAmount() == null ? 0L : p.getAmount())
            .account(decryptSafe(p.getAccount()))
            .bank(decryptSafe(p.getBank()))
            .name(decryptSafe(p.getName()))
            .status(p.getStatus() == null ? 0 : p.getStatus())
            .updateAt(p.getUpdateAt())
            .build();
    }

    /** AES 복호화 (실패 시 원문 유지) */
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
