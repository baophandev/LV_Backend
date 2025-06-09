package com.example.PhoneShop.controller;

import com.example.PhoneShop.dto.api.ApiResponse;
import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.ReviewRequest.CreateReviewRequest;
import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.dto.response.ReviewResponse;
import com.example.PhoneShop.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReviewController {
    ReviewService reviewService;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<ReviewResponse> createReview(
            @RequestPart("data") @Valid CreateReviewRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
            ) throws IOException {
        ReviewResponse reviewResponse = reviewService.create(request, files);

        return ApiResponse.<ReviewResponse>builder()
                .message("Create review successfully!")
                .code("rvw-s-01")
                .data(reviewResponse)
                .build();
    }

    @GetMapping
    CustomPageResponse<ReviewResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam String productId
    ){
        Pageable pageable = PageRequest.of(page, size);
        return reviewService.getAllByPrdId(productId, pageable);
    }

    @GetMapping("/getAll")
    public ResponseEntity<CustomPageResponse<ReviewResponse>> getAllReviewsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "none") String sort // "asc", "desc", or "none"
    ) {
        CustomPageResponse<ReviewResponse> response = reviewService.getAllReviewsForAdmin(page, size, sort);
        return ResponseEntity.ok(response);
    }


}
