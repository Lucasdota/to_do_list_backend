package com.lucasdota.todolist.services;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lucasdota.todolist.entities.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

@Service
public class TokenService {
	protected final Log logger = LogFactory.getLog(getClass());
	@Value("${jwt.secret}")
	public String secret;

	private String decodeSecret(String encodedSecret) {
    byte[] decodedBytes = Base64.getDecoder().decode(encodedSecret);
    return new String(decodedBytes);
  }

	public String generateToken(User user) {
		try {
			String decodedSecret = decodeSecret(secret);
      Algorithm algorithm = Algorithm.HMAC256(decodedSecret);
			return JWT.create()
											.withIssuer("auth-api")										
											.withSubject(user.getEmail())
											.withClaim("userId", user.getId())
											.withExpiresAt(LocalDateTime.now().plusHours(100).toInstant(ZoneOffset.of("-03:00")))						
											.sign(algorithm);							
		} catch (JWTCreationException e) {
			throw new RuntimeException("Error while generating token", e);	
		}
	}

	public String validateToken(String token) {		
		logger.warn("validateToken: " + token);
		try {
			String decodedSecret = decodeSecret(secret);
			Algorithm algorithm = Algorithm.HMAC256(decodedSecret);
			return JWT.require(algorithm)
							.withIssuer("auth-api")
							.build()
							.verify(token)
							.getSubject();
		} catch (JWTVerificationException e) {
			logger.error("Token verification failed: " + e.getMessage());
			// spring security verifies that there's no user returned, so it automatically throws en unauthorized response
			return "";
		}
	}
}