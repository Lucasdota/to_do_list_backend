package com.lucasdota.todolist.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lucasdota.todolist.repositories.UserRepository;
import com.lucasdota.todolist.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

	@Autowired
	TokenService tokenService;

	@Autowired
	UserRepository repository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
					throws ServletException, IOException {
			String token = recoverToken(request);
			if (token != null) {
					String email = tokenService.validateToken(token);
					if (email != null) { // check if the token is valid and email is retrieved
							UserDetails user = repository.findByEmail(email);
							if (user != null) {
									var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
									SecurityContextHolder.getContext().setAuthentication(auth);
							} else {
									logger.warn("User not found for email: " + email);
							}
					} else {
							logger.warn("Invalid token: " + token);
					}
			}
			filterChain.doFilter(request, response);
	}

	private String recoverToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				return null;
		}
		return authHeader.substring(7); // extract the token value
	}
}
