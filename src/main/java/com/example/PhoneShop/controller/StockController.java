package com.example.PhoneShop.controller;

import com.example.PhoneShop.dto.request.StockRequest.CreateStockRequest;
import com.example.PhoneShop.dto.response.StockResponse;
import com.example.PhoneShop.service.StockService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
