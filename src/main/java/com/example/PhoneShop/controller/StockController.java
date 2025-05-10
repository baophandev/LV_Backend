package com.example.PhoneShop.controller;

import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.StockRequest.CreateStockRequest;
import com.example.PhoneShop.dto.response.StockHistoryResponse;
import com.example.PhoneShop.dto.response.StockResponse;
import com.example.PhoneShop.service.StockService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StockController {
    StockService stockService;

    @PostMapping
    StockHistoryResponse createStock(@RequestBody @Valid CreateStockRequest request){
        return  stockService.create(request);
    }

    @GetMapping
    CustomPageResponse<StockHistoryResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return stockService.getAll(pageable);
    }

    @GetMapping("/productId")
    CustomPageResponse<StockHistoryResponse> getStockById(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam String productId
    ){
        Pageable pageable = PageRequest.of(page, size);
        return stockService.getStockById(pageable, productId);
    }

    @GetMapping("/product")
    CustomPageResponse<StockResponse> getStockOfProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return stockService.getAllStock(pageable);
    }
}
