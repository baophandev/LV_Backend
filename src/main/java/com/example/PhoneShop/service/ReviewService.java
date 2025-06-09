package com.example.PhoneShop.service;

import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.ReviewRequest.CreateReviewRequest;
import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.dto.response.ReviewResponse;
import com.example.PhoneShop.entities.*;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.ReviewMapper;
import com.example.PhoneShop.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ProductRepository productRepository;
    ReviewRepository reviewRepository;
    OrderItemRepository orderItemRepository;
    ReviewMapper reviewMapper;
    UserRepository userRepository;

    @PreAuthorize("hasAnyRole('USER')")
    public ReviewResponse create(CreateReviewRequest request, List<MultipartFile> files) throws IOException {

        Product product = productRepository.findById(request.getPrdId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product does not exist!"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User does not exist!"));

        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow( () -> new AppException(HttpStatus.NOT_FOUND, "OrderItem does not exist!"));

        Review review = new Review();
        review.setComment(request.getComment().trim());
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setUser(user);
        review.setCreatedAt(LocalDateTime.now());
        orderItem.setIsReviewed(true);
        orderItemRepository.save(orderItem);

        List<ReviewImage> images = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && file.getSize() > 0) {
                    ReviewImage image = ReviewImage.builder()
                            .imageType(file.getContentType())
                            .data(file.getBytes())
                            .review(review)
                            .build();
                    images.add(image);
                }
            }
            review.setImages(images);
        }

        reviewRepository.save(review);

        List<Review> reviews = reviewRepository.findByProductId(product.getId());

        double avgRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0);

        product.setRating(avgRating);
        productRepository.save(product);

        return ReviewResponse.builder()
                .id(review.getId())
                .prdId(review.getProduct().getId())
                .displayName(review.getUser().getDisplayName())
                .comment(review.getComment())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .images(review.getImages())
                .build();
    }

    public CustomPageResponse<ReviewResponse> getAllByPrdId(String productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);

        List<ReviewResponse> reviewResponses = reviews.getContent()
                .stream().map(reviewMapper::toReviewResponse).toList();

        return CustomPageResponse.<ReviewResponse>builder()
                .pageNumber(reviews.getNumber())
                .pageSize(reviews.getSize())
                .totalElements(reviews.getTotalElements())
                .totalPages(reviews.getTotalPages())
                .content(reviewResponses)
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public CustomPageResponse<ReviewResponse> getAllReviewsForAdmin(int page, int size, String sortDirection) {
        Sort sort;

        if ("asc".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by("createdAt").ascending();
        } else if ("desc".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by("createdAt").descending();
        } else {
            sort = Sort.unsorted();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviews = reviewRepository.findAll(pageable);

        List<ReviewResponse> reviewResponses = reviews.getContent().stream()
                .map(reviewMapper::toReviewResponse)
                .toList();

        return CustomPageResponse.<ReviewResponse>builder()
                .pageNumber(reviews.getNumber())
                .pageSize(reviews.getSize())
                .totalElements(reviews.getTotalElements())
                .totalPages(reviews.getTotalPages())
                .content(reviewResponses)
                .build();
    }


}
