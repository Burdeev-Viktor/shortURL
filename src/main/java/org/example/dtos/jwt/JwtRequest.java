package org.example.dtos.jwt;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
