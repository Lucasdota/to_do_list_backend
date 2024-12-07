package com.lucasdota.todolist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucasdota.todolist.dtos.AuthDTO;
import com.lucasdota.todolist.dtos.LoginResponseDTO;
import com.lucasdota.todolist.dtos.RegisterDTO;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.repositories.UserRepository;
import com.lucasdota.todolist.services.TokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private UserRepository repository;
	@Autowired
	private TokenService tokenService;

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody @Valid AuthDTO data, @CookieValue(value = "JWT", required = false) String jwtCookie, HttpServletResponse response) {
		// check if the JWT cookie exists and is valid
		if (jwtCookie != null) {
				if (tokenService.validateToken(jwtCookie) != null) {
						return ResponseEntity.ok("Login successful with existing token");
				} else {
						return ResponseEntity.badRequest().body("Invalid JWT token");
				}
		}

		var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
		var auth = this.authManager.authenticate(usernamePassword);
		var token = tokenService.generateToken((User ) auth.getPrincipal());

		Cookie cookie = new Cookie("JWT", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(86400);
		response.addCookie(cookie);

		return ResponseEntity.ok("Login successful");
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO data, HttpServletResponse response) {
		if (this.repository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().body("An account already exists with this email");
		String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
		User newUser  = new User(data.email(), encryptedPassword);
		this.repository.save(newUser);
		
		var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
		var auth = this.authManager.authenticate(usernamePassword);
		var token = tokenService.generateToken((User) auth.getPrincipal());

		Cookie cookie = new Cookie("JWT", token);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setMaxAge(86400);
    response.addCookie(cookie);

		return ResponseEntity.ok("Account create successful");
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("JWT", null);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		
		return ResponseEntity.ok("Logout successful");
	}
}
