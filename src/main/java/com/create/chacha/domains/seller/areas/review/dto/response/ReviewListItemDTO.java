package com.create.chacha.domains.seller.areas.review.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListItemDTO {
    private Long reviewId;
    private LocalDateTime reviewCreatedAt;
    private String productThumbnailUrl;
    private String productName;

    private Long authorId;
    private String authorName;

    private String content;
    private LocalDateTime productCreatedAt;
    private String productRating;   // "4.5/5.0"
    private Integer likeCount;
    private LocalDateTime reviewUpdatedAt;
}