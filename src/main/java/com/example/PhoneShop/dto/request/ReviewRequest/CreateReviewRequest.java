package com.example.PhoneShop.dto.request.ReviewRequest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateReviewRequest {
    String prdId;
    String userId;
    String comment;
    Double rating;
    Long orderItemId;
}
