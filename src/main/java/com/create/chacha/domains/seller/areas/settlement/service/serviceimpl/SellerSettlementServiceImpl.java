package com.create.chacha.domains.seller.areas.settlement.service.serviceimpl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.common.util.dto.LegacySellerDTO;
import com.create.chacha.common.util.dto.LegacyStoreDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassDailySettlementResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassOptionResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.StoreMonthlySettlementItemDTO;
import com.create.chacha.domains.seller.areas.settlement.service.SellerSettlementService;
import com.create.chacha.domains.shared.repository.SellerClassSettlementRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * - Legacy 연동: storeUrl → LegacyStoreDTO/LegacySellerDTO 조회 (Legacy Integer ID → Long 변환)
 * - Repository: 현재(MySQL DB만 조회 (class_info / class_reservation / class_image / seller_settlement)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerSettlementServiceImpl implements SellerSettlementService {

    private final SellerClassSettlementRepository repository;
    private final LegacyAPIUtil legacyAPI; 

    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyy-MM");

 // SellerSettlementServiceImpl.java

    @Override
    public List<ClassOptionResponseDTO> getClassOptionsByStore(String storeUrl) {
        LegacyStoreDTO legacyStore = legacyAPI.getLegacyStoreData(storeUrl);
        if (legacyStore == null || legacyStore.getStoreId() == null) {
            log.warn("[class-list] legacy store not found. storeUrl={}", storeUrl);
            return List.of();
        }
        Long storeId = legacyStore.getStoreId().longValue();
        log.info("[class-list] resolved storeUrl='{}' -> legacyStoreId={}", storeUrl, storeId);

        List<ClassOptionResponseDTO> list = repository.findClassOptionByStore(storeId);
        log.info("[class-list] result size={} for storeId={}", (list == null ? -1 : list.size()), storeId);
        return list;
    }


    @Override
    public ClassDailySettlementResponseDTO getDailySettlementByClass(String storeUrl, Long classId) {
        // Legacy에서 storeId 조회
        LegacyStoreDTO legacyStore = legacyAPI.getLegacyStoreData(storeUrl);
        if (legacyStore == null || legacyStore.getStoreId() == null) return null;
        Long storeId = legacyStore.getStoreId().longValue();

        // 2) 현재 DB에서 소속 검증(storeId + classId) + 일별 합계/썸네일/클래스명 조회
        return repository.findClassDailySettlement(storeId, classId);
    }

    @Override
    public List<StoreMonthlySettlementItemDTO> getMonthlySettlementsByStore(String storeUrl, String accountHolderName) {
        // Legacy에서 storeId + sellerId + 계좌/은행 메타 조회
        LegacyStoreDTO legacyStore = legacyAPI.getLegacyStoreData(storeUrl);
        LegacySellerDTO legacySeller = legacyAPI.getLegacySellerData(storeUrl);
        if (legacyStore == null || legacyStore.getStoreId() == null ||
            legacySeller == null || legacySeller.getSellerId() == null) {
            return List.of();
        }
        Long storeId  = legacyStore.getStoreId().longValue();   // Integer → Long
        Long sellerId = legacySeller.getSellerId().longValue(); // Integer → Long

        //  월별 금액/최근수정일 
        List<StoreMonthlySettlementItemDTO> base = repository.findStoreMonthlySettlements(storeId);

        // 월별 최신 상태
        Map<String, Integer> latestStatusByMonth = repository.findSellerMonthlyLatestStatus(sellerId);

        // 계좌/은행 메타
        final String account = legacySeller.getAccount();
        final String bank    = legacySeller.getAccountBank();

        // 예금주명: 토큰의 이름 사용
        final String holderName = accountHolderName;

        // 상태/계좌/은행/예금주명 보강하여 반환
        return base.stream()
                .map(it -> {
                    final String ymKey = YM.format(it.getSettlementDate()); // LocalDateTime → "yyyy-MM"
                    return StoreMonthlySettlementItemDTO.builder()
                            .settlementDate(it.getSettlementDate())
                            .amount(it.getAmount())
                            .account(account)
                            .bank(bank)
                            .name(holderName)
                            .status(latestStatusByMonth.get(ymKey)) // 월별 최신 상태 매핑
                            .updateAt(it.getUpdateAt())
                            .build();
                })
                .toList();
    }
}
