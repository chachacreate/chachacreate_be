package com.create.chacha.domains.shared.classes.vo;

import lombok.Getter;
import lombok.ToString;

/**
 * 클래스 카드 목록 VO (읽기 전용)
 */
@Getter
@ToString
public class ClassCardVO {
    private final Long id;
    private final String title;          
    private final String thumbnailUrl;   
    private final String storeName;      
    private final String locationDetail;  
    private final Integer price;        

    public ClassCardVO(Long id, String title, String thumbnailUrl,
                       String storeName, String locationDetail, Integer price) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.storeName = storeName;
        this.locationDetail = locationDetail;
        this.price = price;
    }
}
