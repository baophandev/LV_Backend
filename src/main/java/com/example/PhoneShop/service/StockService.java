package com.example.PhoneShop.service;

import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.StockRequest.CreateStockRequest;
import com.example.PhoneShop.dto.response.ProductDetailResponse;
import com.example.PhoneShop.dto.response.ProductResponse;
import com.example.PhoneShop.dto.response.StockHistoryResponse;
import com.example.PhoneShop.dto.response.StockResponse;
import com.example.PhoneShop.entities.*;
import com.example.PhoneShop.exception.AppException;
import com.example.PhoneShop.mapper.ProductMapper;
import com.example.PhoneShop.repository.ProductRepository;
import com.example.PhoneShop.repository.ProductVariantRepository;
import com.example.PhoneShop.repository.StockRepository;
import com.example.PhoneShop.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockService {

    ProductRepository productRepository;
    ProductVariantRepository productVariantRepository;
    StockRepository stockRepository;
    UserRepository userRepository;
    ProductMapper productMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public StockHistoryResponse create(CreateStockRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User does not exist!"));

        Stock stock = Stock.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        List<StockItem> stockItems = new ArrayList<>();

        for (CreateStockRequest.StockItemRequestDTO itemRequestDTO : request.getStockItemRequestDTO()){
            Product product = productRepository.findById(itemRequestDTO.getProductId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product does not exist!"));

            ProductVariant productVariant = productVariantRepository.findById(itemRequestDTO.getVariantId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Product variant does not exist!"));

            StockItem stockItem = StockItem.builder()
                    .priceAtStock(itemRequestDTO.getPriceAtStock())
                    .variant(productVariant)
                    .product(product)
                    .stock(stock)
                    .quantity(itemRequestDTO.getQuantity())
                    .build();

            stockItems.add(stockItem);

            int variantStockQuantity = productVariant.getStock() + itemRequestDTO.getQuantity();

            productVariant.setStock(variantStockQuantity);
            productVariantRepository.save(productVariant);
        }

        stock.setItems(stockItems);
        stockRepository.save(stock);

        List<StockHistoryResponse.StockItemResponseDTO> stockItemResponseDTO = stockItems.stream()
                .map(item -> StockHistoryResponse.StockItemResponseDTO.builder()
                        .id(item.getId())
                        .productName(item.getProduct().getName())
                        .variantName(item.getVariant().getColor())
                        .quantity(item.getQuantity())
                        .priceAtStock(item.getPriceAtStock())
                        .build()
                ).toList();

        return StockHistoryResponse.builder()
                .stockId(stock.getId())
                .createdAt(stock.getCreatedAt())
                .employeeName(stock.getUser().getDisplayName())
                .stockItemResponseDTO(stockItemResponseDTO)
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public CustomPageResponse<StockHistoryResponse> getAll(Pageable pageable){
        Pageable sortedByCreatedAt = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());

        Page<Stock> stocks = stockRepository.findAll(sortedByCreatedAt);

        List<StockHistoryResponse> stockHistoryRespons = stocks.stream().map(
                stock -> StockHistoryResponse.builder()
                        .stockId(stock.getId())
                        .createdAt(stock.getCreatedAt())
                        .employeeName(stock.getUser().getDisplayName())
                        .stockItemResponseDTO(
                                stock.getItems().stream()
                                        .map(item -> StockHistoryResponse.StockItemResponseDTO.builder()
                                                .id(item.getId())
                                                .productName(item.getProduct().getName())
                                                .variantName(item.getVariant().getColor())
                                                .quantity(item.getQuantity())
                                                .priceAtStock(item.getPriceAtStock())
                                                .price(item.getVariant().getPrice())
                                                .stock(item.getVariant().getStock())
                                                .build()).toList()
                        )
                        .build()
        ).toList();

        return CustomPageResponse.<StockHistoryResponse>builder()
                .pageSize(stocks.getSize())
                .pageNumber(stocks.getNumber())
                .totalElements(stocks.getTotalElements())
                .totalPages(stocks.getTotalPages())
                .content(stockHistoryRespons)
                .build();
    }

    public CustomPageResponse<StockHistoryResponse> getStockById (Pageable pageable, String productId){
        Pageable sortedByCreatedAt = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());

        Page<Stock> stocks = stockRepository.findAll(sortedByCreatedAt);

        List<StockHistoryResponse> filteredStockHistoryResponse = stocks.stream()
                .map(stock -> {
                    List<StockHistoryResponse.StockItemResponseDTO> filteredItems = stock.getItems().stream()
                            .filter(item -> item.getProduct().getId().equals(productId))
                            .map(item -> StockHistoryResponse.StockItemResponseDTO.builder()
                                    .id(item.getId())
                                    .productName(item.getProduct().getName())
                                    .variantName(item.getVariant().getColor())
                                    .quantity(item.getQuantity())
                                    .priceAtStock(item.getPriceAtStock())
                                    .price(item.getVariant().getPrice())
                                    .stock(item.getVariant().getStock())
                                    .build()
                            ).toList();

                    if(filteredItems.isEmpty()) return null;

                    return StockHistoryResponse.builder()
                            .stockId(stock.getId())
                            .createdAt(stock.getCreatedAt())
                            .employeeName(stock.getUser().getDisplayName())
                            .stockItemResponseDTO(filteredItems)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        return  CustomPageResponse.<StockHistoryResponse>builder()
                .pageSize(pageable.getPageSize())
                .pageNumber(pageable.getPageNumber())
                .totalElements(filteredStockHistoryResponse.size())
                .totalPages((int) Math.ceil((double) filteredStockHistoryResponse.size() / pageable.getPageSize()))
                .content(filteredStockHistoryResponse)
                .build();
    }

    public CustomPageResponse<StockResponse> getAllStock(Pageable pageable){
        Page<Product> products = productRepository.findAll(pageable);

        List <StockResponse> stockResponses = products.getContent().stream()
                .map(product -> {
                    List<StockResponse.VariantDTO> variantDTOList = product.getVariants().stream()
                            .map(variant -> StockResponse.VariantDTO.builder()
                                    .id(variant.getId())
                                    .color(variant.getColor())
                                    .price(variant.getPrice())
                                    .stock(variant.getStock())
                                    .build()
                            ).toList();
                    if (variantDTOList.isEmpty()) return null;

                    return StockResponse.builder()
                            .prdId(product.getId())
                            .variantDTOS(variantDTOList)
                            .productName(product.getName())
                            .categoryName(product.getCategory().getName())
                            .build();
                        })
                .filter(Objects::nonNull)
                .toList();


        return CustomPageResponse.<StockResponse>builder()
                .pageSize(pageable.getPageSize())
                .pageNumber(pageable.getPageNumber())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .content(stockResponses)
                .build();
    }
}
