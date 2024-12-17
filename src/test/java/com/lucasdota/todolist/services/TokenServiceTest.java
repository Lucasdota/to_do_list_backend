package com.lucasdota.todolist.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.lucasdota.todolist.entities.User;

@SpringBootTest
public class TokenServiceTest {

	@MockitoBean
	private TokenService tokenService;

	@Value("${jwt.secret}")
	private String secret;

	@BeforeEach
  public void setUp() {
    tokenService = new TokenService();
    tokenService.secret = secret;
  }

	@Test
  public void testGenerateToken() {
    User user = new User("test@example.com", "Password123!");
    user.setId(1L);
    String token = tokenService.generateToken(user);
    assertNotNull(token);
    assertTrue(token.startsWith("ey")); // JWT tokens start with "ey"
  }

	@Test
  public void testValidateToken_ValidToken() {
		User user = new User("test@example.com", "Password123!");
		user.setId(1L);
		String token = tokenService.generateToken(user);
		String subject = tokenService.validateToken(token);
		assertEquals(user.getEmail(), subject);
  }

	@Test
  public void testValidateToken_InvalidToken() {
    String invalidToken = "invalid.token.here";
    String subject = tokenService.validateToken(invalidToken);
    assertEquals("", subject);
  }

	@Test
  public void testGenerateToken_Exception() {
		tokenService.secret = "invalid_secret";
		User user = new User("test@example.com", "Password123!");
		user.setId(1L);
		assertThrows(RuntimeException.class, () -> {
				tokenService.generateToken(user);
		});
  }
	
}
