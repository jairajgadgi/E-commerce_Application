package com.example.ecommerce_application.config;

import com.example.ecommerce_application.entity.Cart;
import com.example.ecommerce_application.entity.CartItem;
import com.example.ecommerce_application.entity.AppUser;
import com.example.ecommerce_application.entity.Category;
import com.example.ecommerce_application.entity.Customer;
import com.example.ecommerce_application.entity.CustomerAddress;
import com.example.ecommerce_application.entity.Role;
import com.example.ecommerce_application.entity.Product;
import com.example.ecommerce_application.repository.AppUserRepository;
import com.example.ecommerce_application.repository.CartItemRepository;
import com.example.ecommerce_application.repository.CartRepository;
import com.example.ecommerce_application.repository.CategoryRepository;
import com.example.ecommerce_application.repository.CustomerAddressRepository;
import com.example.ecommerce_application.repository.CustomerRepository;
import com.example.ecommerce_application.repository.ProductRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               CustomerRepository customerRepository,
                               CustomerAddressRepository customerAddressRepository,
                               CartRepository cartRepository,
                               CartItemRepository cartItemRepository,
                               AppUserRepository appUserRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (!appUserRepository.existsByUsername("admin")) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                appUserRepository.save(admin);
            }

            if (!appUserRepository.existsByUsername("customer")) {
                AppUser customerUser = new AppUser();
                customerUser.setUsername("customer");
                customerUser.setPassword(passwordEncoder.encode("customer123"));
                customerUser.setRole(Role.CUSTOMER);
                appUserRepository.save(customerUser);
            }

            Category electronics = categoryRepository.findByNameIgnoreCase("Electronics")
                    .orElseGet(() -> {
                        Category category = new Category();
                        category.setName("Electronics");
                        category.setDescription("Smartphones, laptops, accessories and gadgets");
                        return categoryRepository.save(category);
                    });

            Category fashion = categoryRepository.findByNameIgnoreCase("Fashion")
                    .orElseGet(() -> {
                        Category category = new Category();
                        category.setName("Fashion");
                        category.setDescription("Trendy clothing and accessories");
                        return categoryRepository.save(category);
                    });

            Category home = categoryRepository.findByNameIgnoreCase("Home")
                    .orElseGet(() -> {
                        Category category = new Category();
                        category.setName("Home");
                        category.setDescription("Furniture and lifestyle essentials");
                        return categoryRepository.save(category);
                    });

            Product laptop = productRepository.findBySku("SKU-LAP-001")
                    .orElseGet(() -> {
                        Product product = new Product();
                        product.setSku("SKU-LAP-001");
                        product.setName("Business Laptop Pro");
                        product.setDescription("14-inch productivity laptop with 16GB RAM and 512GB SSD");
                        product.setPrice(new BigDecimal("89999.00"));
                        product.setStockQuantity(25);
                        product.setCategory(electronics);
                        return productRepository.save(product);
                    });

            productRepository.findBySku("SKU-TSH-101").orElseGet(() -> {
                Product product = new Product();
                product.setSku("SKU-TSH-101");
                product.setName("Cotton Crew T-Shirt");
                product.setDescription("Premium cotton everyday wear");
                product.setPrice(new BigDecimal("799.00"));
                product.setStockQuantity(150);
                product.setCategory(fashion);
                return productRepository.save(product);
            });

            productRepository.findBySku("SKU-LAMP-501").orElseGet(() -> {
                Product product = new Product();
                product.setSku("SKU-LAMP-501");
                product.setName("Desk Lamp Elite");
                product.setDescription("Modern LED desk lamp with adjustable brightness");
                product.setPrice(new BigDecimal("2499.00"));
                product.setStockQuantity(60);
                product.setCategory(home);
                return productRepository.save(product);
            });

            Customer customer = customerRepository.findByEmailIgnoreCase("john.doe@example.com")
                    .orElseGet(() -> {
                        Customer newCustomer = new Customer();
                        newCustomer.setFirstName("John");
                        newCustomer.setLastName("Doe");
                        newCustomer.setEmail("john.doe@example.com");
                        newCustomer.setPhone("9999999999");
                        return customerRepository.save(newCustomer);
                    });

            if (!customerAddressRepository.existsByCustomerId(customer.getId())) {
                CustomerAddress address = new CustomerAddress();
                address.setCustomer(customer);
                address.setLabel("Home");
                address.setLine1("221B Baker Street");
                address.setLine2("Marylebone");
                address.setCity("London");
                address.setState("London");
                address.setZipCode("NW1 6XE");
                address.setCountry("UK");
                address.setDefaultAddress(true);
                customerAddressRepository.save(address);
            }

            Cart cart = cartRepository.findByCustomerId(customer.getId())
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setCustomer(customer);
                        return cartRepository.save(newCart);
                    });

            if (cartItemRepository.findByCartId(cart.getId()).isEmpty()) {
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProduct(laptop);
                cartItem.setQuantity(1);
                cartItem.setUnitPrice(laptop.getPrice());
                cartItemRepository.save(cartItem);
            }
        };
    }
}
