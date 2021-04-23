package com.scaffold.security.jwt;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
	
	@Value("${jwt.secret.key}")
	private String secretKey;
	
	public String generateToken(String chatRoomId) {
		return Jwts.builder()
					.setSubject(chatRoomId)
					.signWith(SignatureAlgorithm.HS256, secretKey)
					.compact();
	}
	
	private Claims getAllClaims(String accessToken) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
	}
	
	public <T> T getClaimFromToken(String token , Function<Claims, T> claimResolver) {
		final Claims claims = getAllClaims(token);			
		return claimResolver.apply(claims);
	}
}
