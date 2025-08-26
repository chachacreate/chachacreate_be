package com.create.chacha.domains.shared.classes.vo;

import lombok.Getter;
import lombok.ToString;

/**
 * 클래스 카드 목록 VO (읽기 전용)
 */
@Getter
@ToString
public class ClassCardVO {
    private final Long id;               		// 클래스 ID
    private final String title;          		// 클래스명
    private final String thumbnailUrl;   	// 썸네일 이미지
    private final String storeName;      	// 스토어명
    private final String locationDetail; 	// 상세 위치
    private final Integer price;         		// 가격
    private final Long remainSeat;    	// 여석 (정원 - 예약 가능 인원)

    public ClassCardVO(Long id, String title, String thumbnailUrl,
                       String storeName, String locationDetail, Integer price, Long remainSeat) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.storeName = storeName;
        this.locationDetail = locationDetail;
        this.price = price;
        this.remainSeat = remainSeat;
    }
}
