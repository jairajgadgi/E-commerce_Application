package com.example.ecommerce_application.controller;

import com.example.ecommerce_application.dto.CategoryDtos.CategoryResponse;
import com.example.ecommerce_application.dto.ProductDtos.ProductResponse;
import com.example.ecommerce_application.security.JwtService;
import com.example.ecommerce_application.service.ProductService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void getByIdShouldReturnProduct() throws Exception {
        ProductResponse response = new ProductResponse(
                1L,
                "SKU-1",
                "Laptop",
                "Test laptop",
                new BigDecimal("99999.00"),
                5,
                true,
                new CategoryResponse(10L, "Electronics", "Electronics category", true));
        when(productService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/products/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-1"))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }
}
