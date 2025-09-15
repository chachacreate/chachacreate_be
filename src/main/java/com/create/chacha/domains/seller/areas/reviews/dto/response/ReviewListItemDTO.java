package com.create.chacha.domains.seller.areas.reviews.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListItemDTO {
    private Long reviewId;
    private Long productId;
    private LocalDateTime reviewCreatedAt;
    private String productThumbnailUrl;   
    private String productName;          

    private Long authorId;
    private String authorName;

    private String content;
    private LocalDateTime productCreatedAt;      
    private String productRating;         // "4.50/5.0" 
    private Integer likeCount;            
    private LocalDateTime reviewUpdatedAt;
}
