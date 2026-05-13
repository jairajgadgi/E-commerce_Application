package com.example.ecommerce_application.service;

import com.example.ecommerce_application.dto.CategoryDtos.CategoryRequest;
import com.example.ecommerce_application.dto.CategoryDtos.CategoryResponse;
import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);

    CategoryResponse getById(Long id);

    List<CategoryResponse> getAll();
}
