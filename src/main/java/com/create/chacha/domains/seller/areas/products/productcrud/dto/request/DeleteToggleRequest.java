package com.create.chacha.domains.seller.areas.products.productcrud.dto.request;

import java.util.List;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeleteToggleRequest {
    private List<Long> productIds;
}