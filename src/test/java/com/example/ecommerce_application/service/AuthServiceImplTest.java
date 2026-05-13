package com.example.ecommerce_application.service;

import com.example.ecommerce_application.dto.AuthDtos.LoginRequest;
import com.example.ecommerce_application.dto.AuthDtos.RegisterCustomerRequest;
import com.example.ecommerce_application.dto.AuthDtos.AuthResponse;
import com.example.ecommerce_application.dto.CustomerDtos.CustomerResponse;
import com.example.ecommerce_application.entity.AppUser;
import com.example.ecommerce_application.entity.Role;
import com.example.ecommerce_application.notification.EmailNotificationService;
import com.example.ecommerce_application.repository.AppUserRepository;
import com.example.ecommerce_application.security.JwtService;
import com.example.ecommerce_application.service.impl.AuthServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomerService customerService;

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void loginShouldReturnJwtResponse() {
        LoginRequest request = new LoginRequest("admin", "admin123");
        AppUser appUser = new AppUser();
        appUser.setUsername("admin");
        appUser.setRole(Role.ADMIN);
        UserDetails userDetails = User.withUsername("admin").password("pwd").roles("ADMIN").build();

        when(appUserRepository.findByUsername("admin")).thenReturn(Optional.of(appUser));
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails, Role.ADMIN)).thenReturn("jwt-token");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(mock(Authentication.class));

        AuthResponse response = authService.login(request);

        Assertions.assertEquals("jwt-token", response.token());
        Assertions.assertEquals("admin", response.username());
        Assertions.assertEquals(Role.ADMIN, response.role());
    }

    @Test
    void registerCustomerShouldCreateUserAndReturnToken() {
        RegisterCustomerRequest request = new RegisterCustomerRequest("John", "Doe", "john@example.com", "9999999999", "secret");
        CustomerResponse customerResponse = new CustomerResponse(1L, "John", "Doe", "john@example.com", "9999999999");
        AppUser savedUser = new AppUser();
        savedUser.setUsername("john@example.com");
        savedUser.setRole(Role.CUSTOMER);
        UserDetails userDetails = User.withUsername("john@example.com").password("pwd").roles("CUSTOMER").build();

        when(appUserRepository.existsByUsername("john@example.com")).thenReturn(false);
        when(customerService.create(any())).thenReturn(customerResponse);
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(appUserRepository.save(any(AppUser.class))).thenReturn(savedUser);
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails, Role.CUSTOMER)).thenReturn("jwt-token");

        AuthResponse response = authService.registerCustomer(request);

        Assertions.assertEquals("jwt-token", response.token());
        Assertions.assertEquals("john@example.com", response.username());
        Assertions.assertEquals(Role.CUSTOMER, response.role());
    }
}
