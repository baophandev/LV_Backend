package com.example.PhoneShop.dto.response;


import com.example.PhoneShop.entities.CategoryImage;
import com.example.PhoneShop.entities.Image;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryResponse {
    private String id;
    private String name;
    List<CategoryImage> categoryImages = new ArrayList<>();
}
