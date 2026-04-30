package se.voizter.felparkering.api.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        UserDetailDto user = authService.login(request);
        return ResponseEntity.ok(Map.of("user", user, "message", Message.LOGIN.toString()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        UserDetailDto user = authService.register(request);
        return ResponseEntity.ok(Map.of("user", user, "message", Message.REGISTER.toString()));
    }
}
