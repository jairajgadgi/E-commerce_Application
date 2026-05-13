package com.example.ecommerce_application.service;

import com.example.ecommerce_application.dto.ProductDtos.ProductRequest;
import com.example.ecommerce_application.dto.ProductDtos.ProductResponse;
import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    ProductResponse getById(Long id);

    List<ProductResponse> getAll();

    List<ProductResponse> search(String searchTerm);

    List<ProductResponse> getByCategory(Long categoryId);
}
