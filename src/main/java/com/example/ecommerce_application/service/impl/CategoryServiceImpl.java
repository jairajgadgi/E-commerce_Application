package com.example.ecommerce_application.service.impl;

import com.example.ecommerce_application.dto.CategoryDtos.CategoryRequest;
import com.example.ecommerce_application.dto.CategoryDtos.CategoryResponse;
import com.example.ecommerce_application.entity.Category;
import com.example.ecommerce_application.exception.DuplicateResourceException;
import com.example.ecommerce_application.exception.NotFoundException;
import com.example.ecommerce_application.repository.CategoryRepository;
import com.example.ecommerce_application.service.CategoryService;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @CacheEvict(cacheNames = {"categories"}, allEntries = true)
    public CategoryResponse create(CategoryRequest request) {
        categoryRepository.findByNameIgnoreCase(request.name()).ifPresent(category -> {
            throw new DuplicateResourceException("Category already exists: " + request.name());
        });

        Category category = new Category();
        category.setName(request.name().trim());
        category.setDescription(request.description());
        category.setActive(request.active() == null || request.active());
        return toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "categories", key = "#id")
    public CategoryResponse getById(Long id) {
        return toResponse(findCategory(id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "categories", key = "'all'")
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription(), category.isActive());
    }
}
