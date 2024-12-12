package com.example.PhoneShop.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomPageResponse<T> {
    private int pageNumber;
    private int pageSize;
    private long totalElements ;
    private int totalPages;
    private List<T> content;
}
