package com.example.PhoneShop.controller;

import com.example.PhoneShop.dto.api.CustomPageResponse;
import com.example.PhoneShop.dto.request.StockRequest.CreateStockRequest;
import com.example.PhoneShop.dto.response.StockResponse;
import com.example.PhoneShop.entities.Stock;
import com.example.PhoneShop.service.StockService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StockController {
    StockService stockService;

    @PostMapping
    StockResponse createStock(@RequestBody @Valid CreateStockRequest request){
        return  stockService.create(request);
    }

    @GetMapping
    CustomPageResponse<StockResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return stockService.getAll(pageable);
    }
}
