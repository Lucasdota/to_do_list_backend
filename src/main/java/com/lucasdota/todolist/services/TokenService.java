package com.lucasdota.todolist.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lucasdota.todolist.entities.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
	@Value("${jwt.secret}")
	private String secret;

	public String generateToken(User user) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			String token = JWT.create()
											.withIssuer("auth-api")
											.withSubject(user.getEmail())
											.withExpiresAt(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")))
											.sign(algorithm);
			return token;								
		} catch (JWTCreationException e) {
			throw new RuntimeException("Error while generating token", e);	
		}
	}

	public String validateToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			return JWT.require(algorithm)
							.withIssuer("auth-api")
							.build()
							.verify(token)
							.getSubject();
		} catch (JWTVerificationException e) {
			// spring security verifies that there's no user returned, so it automatically throws en unauthorized response
			return "";
		}
	}
}