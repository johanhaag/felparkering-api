package se.voizter.felparkering.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import se.voizter.felparkering.api.dto.ApiResponse;
import se.voizter.felparkering.api.dto.LoginRequest;
import se.voizter.felparkering.api.dto.RegisterRequest;
import se.voizter.felparkering.api.dto.UserDetailDto;
import se.voizter.felparkering.api.enums.Message;
import se.voizter.felparkering.api.service.AuthService;

@RestController
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDetailDto>> login(@Valid @RequestBody LoginRequest request) {
        UserDetailDto user = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(user, Message.LOGIN.toString()));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDetailDto>> register(@Valid @RequestBody RegisterRequest request) {
        UserDetailDto user = authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(user, Message.REGISTER.toString()));
    }
}
