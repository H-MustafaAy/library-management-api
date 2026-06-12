package com.mustafaay.library_management_api.controller;

import com.mustafaay.library_management_api.dto.request.LoginRequest;
import com.mustafaay.library_management_api.dto.response.LoginResponse;
import com.mustafaay.library_management_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // kullanıcı email  ve şifresi ile token almak için
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}