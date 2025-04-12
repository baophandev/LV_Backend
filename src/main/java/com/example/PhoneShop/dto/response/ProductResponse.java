package com.example.PhoneShop.dto.response;


import com.example.PhoneShop.entities.Category;
import com.example.PhoneShop.entities.Image;
import com.example.PhoneShop.entities.ProductAvatar;
import com.example.PhoneShop.entities.ProductVariant;
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
    Integer sold;
    Integer discountDisplayed;
    Integer firstVariantPrice;
    ProductStatus status;
    ProductAvatar productAvatar = new ProductAvatar();
    List<String> related_id = new ArrayList<>();
    Category category;
}
