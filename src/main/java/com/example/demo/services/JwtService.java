package com.example.demo.services;


import com.example.demo.model.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class  JwtService{

    private static final String SECRET = "BD0B1B4F3F3684118D63AA68AD06CE7F6802CCA300A04BAFFEA930126E185B1CD536FEF20068C484700544D25849229EF8C826160B68E9FC8609079A5C3893B3";
    public String generateToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "LeagueOpinion");
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(60*30)))
                .signWith(generateKey())
                .compact();
    }

    private SecretKey generateKey(){
        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
         return Keys.hmacShaKeyFor(decodedKey);
    }

    public String extractUsername(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getSubject();
    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public String isTokenValidAndRenew(String jwt) {
        Claims claims = getClaims(jwt);
        if (claims.getExpiration().after(Date.from(Instant.now()))) {
            return renewToken(claims);
        }
        return null;
    }

    private String renewToken(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(claims.getSubject())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(60 * 30)))
                .signWith(generateKey())
                .compact();
    }
}
