package com.example.ecommerce_application.service;

import com.example.ecommerce_application.dto.CategoryDtos.CategoryRequest;
import com.example.ecommerce_application.entity.Category;
import com.example.ecommerce_application.exception.DuplicateResourceException;
import com.example.ecommerce_application.repository.CategoryRepository;
import com.example.ecommerce_application.service.impl.CategoryServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createShouldThrowWhenCategoryAlreadyExists() {
        when(categoryRepository.findByNameIgnoreCase("Electronics")).thenReturn(Optional.of(new Category()));

        Assertions.assertThrows(DuplicateResourceException.class,
                () -> categoryService.create(new CategoryRequest("Electronics", "desc", true)));
    }
}
