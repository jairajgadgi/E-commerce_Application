package com.example.ecommerce_application.controller;

import com.example.ecommerce_application.dto.ProductDtos.ProductResponse;
import com.example.ecommerce_application.service.ProductService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam String query) {
        return ResponseEntity.ok(productService.search(query));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getByCategory(categoryId));
    }
}
