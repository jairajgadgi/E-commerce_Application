package com.example.ecommerce_application.service.impl;

import com.example.ecommerce_application.dto.AuthDtos.AuthResponse;
import com.example.ecommerce_application.dto.AuthDtos.LoginRequest;
import com.example.ecommerce_application.dto.AuthDtos.RegisterCustomerRequest;
import com.example.ecommerce_application.dto.CustomerDtos.CustomerRequest;
import com.example.ecommerce_application.entity.AppUser;
import com.example.ecommerce_application.entity.Role;
import com.example.ecommerce_application.exception.DuplicateResourceException;
import com.example.ecommerce_application.exception.NotFoundException;
import com.example.ecommerce_application.repository.AppUserRepository;
import com.example.ecommerce_application.service.AuthService;
import com.example.ecommerce_application.service.CustomerService;
import com.example.ecommerce_application.security.JwtService;
import com.example.ecommerce_application.notification.EmailNotificationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;
    private final EmailNotificationService emailNotificationService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserDetailsService userDetailsService,
                           JwtService jwtService,
                           AppUserRepository appUserRepository,
                           PasswordEncoder passwordEncoder,
                           CustomerService customerService,
                           EmailNotificationService emailNotificationService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerService = customerService;
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        AppUser appUser = appUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.username()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(appUser.getUsername());
        return new AuthResponse(jwtService.generateToken(userDetails, appUser.getRole()), appUser.getUsername(), appUser.getRole());
    }

    @Override
    public AuthResponse registerCustomer(RegisterCustomerRequest request) {
        String username = request.email().trim().toLowerCase();
        if (appUserRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists: " + username);
        }

        var customer = customerService.create(new CustomerRequest(request.firstName(), request.lastName(), request.email(), request.phone()));
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(request.password()));
        appUser.setRole(Role.CUSTOMER);
        appUserRepository.save(appUser);
        emailNotificationService.sendWelcomeEmail(customer.email(), customer.firstName());
        UserDetails userDetails = userDetailsService.loadUserByUsername(appUser.getUsername());
        return new AuthResponse(jwtService.generateToken(userDetails, appUser.getRole()), appUser.getUsername(), appUser.getRole());
    }
}
