package com.lucasdota.todolist.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.lucasdota.todolist.dtos.TodoDTO;
import com.lucasdota.todolist.entities.Todo;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.services.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.util.InternalException;
import org.hibernate.query.sqm.ParsingException;

@RestController
@RequestMapping("user")
public class UserController {
	protected final Log logger = LogFactory.getLog(getClass());
	private final UserService userService;
	@Value("${jwt.secret}")
	private String secret;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	private String getEmailFromJwt(String token) {
		logger.warn("Token on UserController:" + token);
		try {
			Claims claims = Jwts.parser()
							.setSigningKey(secret)
							.parseClaimsJws(token)
							.getBody();
			return claims.getSubject();
		} catch (ExpiredJwtException e) {
			logger.warn("JWT token is expired: " + e.getMessage());
			throw new TokenExpiredException("JWT token is expired", null);
		} catch (Exception e) {
			logger.error("Error parsing JWT token: " + e.getMessage());
			throw new ParsingException("Error Parsing JWT");
		}
}

	@GetMapping
	public ResponseEntity<UserDetails> getUser(@CookieValue(value = "JWT", required = false) String jwt) {
		if (jwt == null) {
			logger.warn("JWT token is null");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String userEmail;
		try {
			userEmail = getEmailFromJwt(jwt);
		} catch (InternalException | TokenExpiredException e) {
			logger.warn("Failed to extract email from JWT: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		logger.info("Extracted userEmail: " + userEmail);
		UserDetails userDetails = userService.findByEmail(userEmail);
    logger.info("userDetails: " + userDetails);
    if (userDetails != null) {
      return ResponseEntity.ok(userDetails);
    } else {
      logger.warn("User not found with email: " + userEmail);
      return ResponseEntity.notFound().build();
    }
	}

	@PostMapping("/create-to-do")
	public ResponseEntity<String> createTodo(@RequestBody @Valid TodoDTO data) {
		logger.warn("userId: " + data.userId());
		User user = userService.getUserById(data.userId());	
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		Todo todo = new Todo(data.name(), data.description());
		userService.createTodoForUser(user, todo);
		return ResponseEntity.status(HttpStatus.CREATED).body("Todo created successfully");
	}
}