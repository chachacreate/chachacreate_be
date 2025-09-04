package com.create.chacha.domains.shared.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassDailySettlementResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassOptionResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.StoreMonthlySettlementItemDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

/**
 * - ERD 기준으로 class_info / class_reservation / class_image / seller_settlement만 사용
 * - Legacy store/seller는 Service에서 LegacyAPIUtil로 조회 후 Long ID로 전달
 */
@Repository
@RequiredArgsConstructor
public class SellerClassSettlementRepository {

    @PersistenceContext
    private final EntityManager em;

    /**
     * [드롭다운]
     */
    @SuppressWarnings("unchecked")
    public List<ClassOptionResponseDTO> findClassOptionByStore(Long storeId) {
        String sql = """
            SELECT
              ci.id    AS class_id,
              ci.title AS class_name
            FROM class_info ci
            WHERE ci.store_id = :storeId
            ORDER BY ci.id DESC
        """;

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("storeId", storeId)
                .getResultList();

        List<ClassOptionResponseDTO> list = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            Long classId = ((Number) r[0]).longValue();
            String className = (String) r[1];

            list.add(ClassOptionResponseDTO.builder()
                    .id(classId)
                    .name(className)
                    .build());
        }
        return list;
    }

    /**
     *  특정 클래스의 일별 정산 조회
     *  - 대표이미지: class_image.status='THUMBNAIL' AND image_sequence=1 AND is_deleted=0
     *  - 일별 금액 집계: cr.status='ORDER_OK'만 합산, DATE(cr.created_at) 단위로 SUM(ci.price)
     */
    @SuppressWarnings("unchecked")
    public ClassDailySettlementResponseDTO findClassDailySettlement(Long storeId, Long classId) {
        // 1) 클래스명 
        String titleSql = """
            SELECT ci.title
            FROM class_info ci
            WHERE ci.id = :classId
              AND ci.store_id = :storeId
            LIMIT 1
        """;
        Object titleObj = em.createNativeQuery(titleSql)
                .setParameter("classId", classId)
                .setParameter("storeId", storeId)
                .getSingleResult();
        if (titleObj == null) return null;
        String className = String.valueOf(titleObj);

        // 2) 대표 썸네일 1개
        String thumbSql = """
            SELECT ci2.url
            FROM class_image ci2
            WHERE ci2.class_info_id = :classId
              AND ci2.image_sequence = 1
              AND ci2.is_deleted = 0
              AND ci2.status = 'THUMBNAIL'
            LIMIT 1
        """;
        String thumbnail = (String) em.createNativeQuery(thumbSql)
                .setParameter("classId", classId)
                .getResultStream().findFirst().orElse(null);

        // 3) 일별 결제금액 집계
        String dailySql = """
            SELECT
                DATE(cr.created_at) AS ymd,             -- YYYY-MM-DD
                SUM(ci.price)       AS amt              -- 해당일 결제 금액 합계 (ORDER_OK만)
            FROM class_info ci
            JOIN class_reservation cr
              ON cr.class_info_id = ci.id
             AND cr.status = 'ORDER_OK'
            WHERE ci.id = :classId
              AND ci.store_id = :storeId
            GROUP BY DATE(cr.created_at)
            ORDER BY ymd ASC
        """;
        List<Object[]> rows = em.createNativeQuery(dailySql)
                .setParameter("classId", classId)
                .setParameter("storeId", storeId)
                .getResultList();

        var daily = new java.util.ArrayList<ClassDailySettlementResponseDTO.DailyEntry>(rows.size());
        for (Object[] r : rows) {
            String ymd = String.valueOf(r[0]);                   // "YYYY-MM-DD"
            Integer amount = r[1] == null ? 0 : ((Number) r[1]).intValue();
            daily.add(ClassDailySettlementResponseDTO.DailyEntry.builder()
                    .date(ymd)
                    .amount(amount)
                    .build());
        }

        return ClassDailySettlementResponseDTO.builder()
                .classId(classId)
                .className(className)
                .thumbnailUrl(thumbnail)
                .daily(daily)
                .build();
    }

    /**
     * 스토어의 전체 클래스들에 대한 월별 정산 조회
     * - class_reservation.created_at 기준 월("YYYY-MM")로 집계
     * - 조건: storeId 일치, cr.status='ORDER_OK'만 합산
     * - last_updated: 해당 월 범위 내 class_reservation.MAX(updated_at)
     * - 조인은 class_info만 (store_id 필터링 목적)
     */
    @SuppressWarnings("unchecked")
    public List<StoreMonthlySettlementItemDTO> findStoreMonthlySettlements(Long storeId) {
        String sql = """
            SELECT
              DATE_FORMAT(cr.created_at, '%Y-%m') AS ym,
              SUM(CASE WHEN cr.status = 'ORDER_OK' THEN ci.price ELSE 0 END) AS total_amt,
              MAX(cr.updated_at) AS last_updated
            FROM class_reservation cr
            JOIN class_info ci ON ci.id = cr.class_info_id
            WHERE ci.store_id = :storeId
            GROUP BY DATE_FORMAT(cr.created_at, '%Y-%m')
            ORDER BY ym DESC
        """;

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("storeId", storeId)
                .getResultList();

        List<StoreMonthlySettlementItemDTO> list = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            String ym = (String) r[0];                 // "YYYY-MM"
            Long amount = r[1] == null ? 0L : ((Number) r[1]).longValue();
            Timestamp last = (Timestamp) r[2];

            // "YYYY-MM" → 해당 월의 1일 00:00:00 로 LocalDateTime 구성
            LocalDateTime settlementDate = YearMonth.parse(ym).atDay(1).atStartOfDay();
            LocalDateTime updateAt = (last == null) ? null : last.toLocalDateTime();

            list.add(StoreMonthlySettlementItemDTO.builder()
                    .settlementDate(settlementDate)
                    .amount(amount)
                    .account(null)   // Service에서 LegacySeller.account로 채움
                    .bank(null)      // Service에서 LegacySeller.accountBank로 채움
                    .name(null)      // Service에서 토큰 이름으로 채움
                    .status(null)    // Service에서 월별 최신 상태로 채움
                    .updateAt(updateAt)
                    .build());
        }
        return list;
    }

    /**
     * 월별 정산 상태 조회
     * - seller_settlement에서 sellerId별로 월 키("YYYY-MM")를 만들고,
     *   updated_at DESC 기준 가장 최신 status를 조회
     */
    @SuppressWarnings("unchecked")
    public Map<String, Integer> findSellerMonthlyLatestStatus(Long sellerId) {
        String sql = """
            SELECT
              DATE_FORMAT(ss.updated_at, '%Y-%m') AS ym,
              SUBSTRING_INDEX(GROUP_CONCAT(ss.status ORDER BY ss.updated_at DESC), ',', 1) AS latest_status
            FROM seller_settlement ss
            WHERE ss.seller_id = :sellerId
            GROUP BY DATE_FORMAT(ss.updated_at, '%Y-%m')
        """;

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("sellerId", sellerId)
                .getResultList();

        Map<String, Integer> map = new HashMap<>(rows.size());
        for (Object[] r : rows) {
            String ym = (String) r[0];
            String statusStr = r[1] == null ? null : String.valueOf(r[1]);
            Integer status = (statusStr == null) ? null : Integer.valueOf(statusStr);
            map.put(ym, status);
        }
        return map;
    }
}
