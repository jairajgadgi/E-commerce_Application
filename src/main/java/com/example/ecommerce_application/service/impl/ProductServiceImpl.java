package com.example.ecommerce_application.service.impl;

import com.example.ecommerce_application.dto.CategoryDtos.CategoryResponse;
import com.example.ecommerce_application.dto.ProductDtos.ProductRequest;
import com.example.ecommerce_application.dto.ProductDtos.ProductResponse;
import com.example.ecommerce_application.entity.Category;
import com.example.ecommerce_application.entity.Product;
import com.example.ecommerce_application.exception.DuplicateResourceException;
import com.example.ecommerce_application.exception.NotFoundException;
import com.example.ecommerce_application.repository.CategoryRepository;
import com.example.ecommerce_application.repository.ProductRepository;
import com.example.ecommerce_application.service.ProductService;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @CacheEvict(cacheNames = {"products"}, allEntries = true)
    public ProductResponse create(ProductRequest request) {
        productRepository.findBySku(request.sku()).ifPresent(product -> {
            throw new DuplicateResourceException("Product SKU already exists: " + request.sku());
        });

        Category category = findCategory(request.categoryId());
        Product product = new Product();
        product.setSku(request.sku().trim());
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setActive(request.active() == null || request.active());
        product.setCategory(category);
        return toResponse(productRepository.save(product));
    }

    @Override
    @CacheEvict(cacheNames = {"products"}, allEntries = true)
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProduct(id);
        productRepository.findBySku(request.sku()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("Product SKU already exists: " + request.sku());
            }
        });

        Category category = findCategory(request.categoryId());
        product.setSku(request.sku().trim());
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setActive(request.active() == null || request.active());
        product.setCategory(category);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "products", key = "#id")
    public ProductResponse getById(Long id) {
        return toResponse(findProduct(id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "products", key = "'all'")
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "products", key = "'search:' + #searchTerm")
    public List<ProductResponse> search(String searchTerm) {
        return productRepository.findByNameContainingIgnoreCase(searchTerm).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "products", key = "'category:' + #categoryId")
    public List<ProductResponse> getByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream().map(this::toResponse).toList();
    }

    public Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }

    private ProductResponse toResponse(Product product) {
        Category category = product.getCategory();
        CategoryResponse categoryResponse = new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive());
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.isActive(),
                categoryResponse);
    }
}
