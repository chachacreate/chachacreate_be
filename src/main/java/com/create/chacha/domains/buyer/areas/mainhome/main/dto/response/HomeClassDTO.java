package com.create.chacha.domains.buyer.areas.mainhome.main.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

//HomeClassDTO.java
@Getter @Setter @ToString @NoArgsConstructor
public class HomeClassDTO {
 private Long id;
 private String title;
 private Integer price;
 private String thumbnailUrl;
 private Long storeId;
 private String storeName;
 private LocalDateTime startDate;
 private LocalDateTime endDate;
 private Integer timeInterval;
 private Long remainSeat;


 private Integer participant;          // 정원
 private List<String> availableTimes;  // 오늘 가능한 시간(HH:mm)

 // JPQL 프로젝션용 생성자
 public HomeClassDTO(Long id, String title, Integer price, String thumbnailUrl,
                     Long storeId, String storeName, LocalDateTime startDate,
                     LocalDateTime endDate, Integer timeInterval, Long remainSeat,
                     Integer participant) {          // ⬅️ participant 추가
     this.id = id;
     this.title = title;
     this.price = price;
     this.thumbnailUrl = thumbnailUrl;
     this.storeId = storeId;
     this.storeName = storeName;
     this.startDate = startDate;
     this.endDate = endDate;
     this.timeInterval = timeInterval;
     this.remainSeat = remainSeat;
     this.participant = participant;
 }
}
