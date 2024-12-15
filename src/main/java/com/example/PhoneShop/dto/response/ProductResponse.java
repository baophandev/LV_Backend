package com.example.PhoneShop.dto.response;


import com.example.PhoneShop.entities.Category;
import com.example.PhoneShop.entities.Image;
import com.example.PhoneShop.enums.ProductStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductResponse {
    String id;
    String name;
    String description;
    Double rating;
    Integer price;
    Integer sold;
    ProductStatus status;
    List<Image> images = new ArrayList<>();
    Category category;
}
