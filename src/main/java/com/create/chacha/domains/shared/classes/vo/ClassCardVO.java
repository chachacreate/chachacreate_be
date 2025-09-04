package com.create.chacha.domains.shared.classes.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

/**
 * 클래스 카드 목록 VO
 */
@Getter
@Setter
@ToString
public class ClassCardVO {
	 private final Long id;
	    private final String title;
	    private final String thumbnailUrl;
        private final Long storeId;
	    private String storeName;
	    private final String addressRoad;   // 주소
	    private final Integer price;
	    private final Long remainSeat;      // 정원 - 예약수 (COUNT)
	    private final LocalDateTime startDate; // 클래스 시작일시
	    private final LocalDateTime endDate;   // 클래스 종료일시

    public ClassCardVO(Long id, String title, String thumbnailUrl,
                       Long storeId, String addressRoad, Integer price, Long remainSeat,
                       LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.storeId = storeId;
        this.addressRoad = addressRoad;
        this.price = price;
        this.remainSeat = remainSeat;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
