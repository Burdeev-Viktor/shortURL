package org.example.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.example.dtos.jwt.JwtRequest;
import org.example.dtos.user.RegistrationUserDto;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return authService.createAuthToken(authRequest);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        return authService.createNewUser(registrationUserDto);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader String authorization) {
        return authService.refreshToken(authorization);
    }

}