package com.scaffold.chat.security;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import com.scaffold.security.jwt.JwtUtil;

public class PerRequestFilter extends OncePerRequestFilter {

	@Autowired JwtUtil jwtUtil;
	
	public PerRequestFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String accessToken = request.getHeader("Authorization");
		if(Objects.nonNull(accessToken) && !accessToken.equals("") && accessToken.startsWith("Bearer")) {
			String filteredToken = accessToken.replace("Bearer", "");
			if(jwtUtil.isValidSignedToken(filteredToken) && !jwtUtil.isTokenExpired(filteredToken)) {
				User details = jwtUtil.getUserFromJWT(filteredToken);
				SecurityContextHolder.getContext().setAuthentication(getAuthentication(details));
			}
			filterChain.doFilter(request, response);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private Authentication getAuthentication(User details) {
		return new UsernamePasswordAuthenticationToken(details, "", details.getAuthorities());
	}

}
