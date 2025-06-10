package com.example.PhoneShop.dto.response;

import com.example.PhoneShop.entities.ReviewImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    String id;
    String prdId;
    String displayName;
    String productName;
    String color;
    String comment;
    Double rating;
    LocalDateTime createdAt;
    List<ReviewImage> images = new ArrayList<>();
}
