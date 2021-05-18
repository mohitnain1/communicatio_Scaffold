package com.scaffold.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.scaffold.chat.domains.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
	
	@Value("${jwt.secret.key}")
	private String secretKey;
	
	private static final long HOUR_MILLIS = 1000 * 60 * 60; 
	
	public String generateToken(String chatRoomId) {
		return Jwts.builder()
					.setSubject(chatRoomId)
					.signWith(SignatureAlgorithm.HS256, secretKey)
					.compact();
	}
	
	private Claims getAllClaims(String accessToken) {
		return Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(accessToken).getBody();
	}
	
	private <T> T getCustomClaims(String token, String claim) {
		return (T) getAllClaims(token).get(claim);
	}
	
	public <T> T getClaimFromToken(String token , Function<Claims, T> claimResolver) {
		final Claims claims = getAllClaims(token);			
		return claimResolver.apply(claims);
	}
	
	public String generateAuthorizationToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", user.getRoles());
		claims.put("username", user.getUsername());
		claims.put("email", user.getEmail());
		claims.put("userId", user.getUserId());
		return Jwts.builder()
				.setSubject(user.getId())
				.addClaims(claims)
				.signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
				.setExpiration(new Date(System.currentTimeMillis() + HOUR_MILLIS * 8))
				.setIssuer("Communication Scaffold")
				.compact();
	}
	
	public boolean isValidSignedToken(String token) {
		return Jwts.parser().setSigningKey(secretKey.getBytes()).isSigned(token);
	}
	
	public boolean isTokenExpired(String token) {
		Date expirationTime = getClaimFromToken(token, Claims::getExpiration);
		if(expirationTime.before(new Date(System.currentTimeMillis()))) {
			return true;
		} else {
			return false;
		}
	}
	
	public org.springframework.security.core.userdetails.User getUserFromJWT(String token) {
		String email = getCustomClaims(token, "email");
		List<String> roles = getCustomClaims(token , "roles");
		Set<GrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role))
					.collect(Collectors.toSet());
		return new org.springframework.security.core.userdetails.User(email, "", authorities);
	}
}
