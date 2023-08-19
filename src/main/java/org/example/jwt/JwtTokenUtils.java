package org.example.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtils {
    private UserService userService;
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private long jwtLifeTime;
    @Value("${jwt.refreshtime}")
    private long jwtRefreshTime;

    public JwtTokenUtils(UserService userService) {
        this.userService = userService;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifeTime);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
    public String getUsernameWhitsBearer(String token){
        return this.getUsername(token.substring(7));
    }

    public boolean isValidToken(String authorization) {
        String username = null;
        String jwt = null;
        if (authorization.startsWith("Bearer_")) {
            jwt = authorization.substring(7);
            try {
                username = getUsername(jwt);
            } catch (ExpiredJwtException e) {
                return true;
            }catch (Exception e){
                return false;
            }
            return true;
        }
        return false;
    }
    public String refreshToken(String authorization){
        String token = authorization.substring(7);
        long issuedTime;
        String username = null;
        try {
            issuedTime = getAllClaimsFromToken(token).getIssuedAt().getTime();
            username = getUsername(token);
        }catch (ExpiredJwtException e){
           issuedTime =  e.getClaims().getIssuedAt().getTime();
           username = e.getClaims().getSubject();
        }
        if((new Date().getTime() - issuedTime) > jwtRefreshTime){
            return null;
        }
        return generateToken(userService.loadUserByUsername(username));
    }
}
