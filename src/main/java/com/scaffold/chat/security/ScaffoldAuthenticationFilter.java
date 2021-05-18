package com.scaffold.chat.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.chat.domains.User;
import com.scaffold.chat.service.impl.UserServiceImpl;
import com.scaffold.security.jwt.JwtUtil;

public class ScaffoldAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private final AuthenticationManager authenticationManager;
	private final UserServiceImpl userService;
	private final JwtUtil jwtUtil;
	
	private static final Logger log = LoggerFactory.getLogger(ScaffoldAuthenticationFilter.class);
	
	public ScaffoldAuthenticationFilter(AuthenticationManager authenticationManager, UserServiceImpl userService, 
			JwtUtil jwtUtil) {
		super.setFilterProcessesUrl("/chat/auth/authorize");
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		if(Objects.isNull(username) || Objects.isNull(password)) {
			if(log.isDebugEnabled()) {
				log.debug("Unable to perform authentication as credentials are null.");
			}
			return null;
		}
		return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

	@Override
	protected String obtainPassword(HttpServletRequest request) {
		return request.getParameter("password").trim();
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		return request.getParameter("username").trim();
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		if(log.isDebugEnabled()) {
			log.debug("Authentication successfull.");
		}
		String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername();
		User user = userService.loadUserByEmail(username);
		String jwtToken = jwtUtil.generateAuthorizationToken(user);
		Map<String, Object> res = new HashMap<>();
		res.put("access_token", jwtToken);
		res.put("val_hrs", 1000 * 60 * 60 * 8);
		response.setContentType("application/json");
		new ObjectMapper().writeValue(response.getWriter(), res);
	}
}
