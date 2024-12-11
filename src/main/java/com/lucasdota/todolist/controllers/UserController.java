package com.lucasdota.todolist.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.lucasdota.todolist.dtos.UserDTO;
import com.lucasdota.todolist.dtos.UserResponseDTO;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.services.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
		logger.warn("{getEmailFromJwt} Token on UserController:" + token);
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

	private Long getIdFromJwt(String token) {
		try {
			Claims claims = Jwts.parser()
							.setSigningKey(secret)
							.parseClaimsJws(token)
							.getBody();
			Long userId = claims.get("userId", Long.class);	
			return userId;			
		}	catch (ExpiredJwtException e) {
			logger.warn("JWT token is expired: " + e.getMessage());
			throw new TokenExpiredException("JWT token is expired", null);
		} catch (Exception e) {
			logger.error("Error parsing JWT token: " + e.getMessage());
			throw new ParsingException("Error Parsing JWT");
		}
	}

	@GetMapping
	public ResponseEntity<UserResponseDTO> getUser(@CookieValue(value = "JWT", required = false) String jwt) {
		if (jwt == null) {
			logger.warn("{getUser} JWT token is null");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String userEmail;
		try {
			userEmail = getEmailFromJwt(jwt);
		} catch (InternalException | TokenExpiredException e) {
			logger.warn("{getUser} Failed to extract email from JWT: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		logger.info("{getUser} Extracted userEmail: " + userEmail);
		User user = (User) userService.findUserByEmail(userEmail);
    if (user != null) {
      UserResponseDTO response = new UserResponseDTO(user.getId(), user.getEmail(), user.getTodos());
        return ResponseEntity.ok(response);
    } else {
      logger.warn("{getUser} User not found with email: " + userEmail);
      return ResponseEntity.notFound().build();
    }
	}

	@DeleteMapping
	public ResponseEntity<String> deleteUser(@CookieValue(value = "JWT", required = false) String jwt) {
		Long userId = getIdFromJwt(jwt);
		logger.info("extracted USERID: "+userId);
		userService.delete(userId);
		return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
	}
}