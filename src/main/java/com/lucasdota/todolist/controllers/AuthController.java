package com.lucasdota.todolist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.lucasdota.todolist.dtos.AuthRequest;
import com.lucasdota.todolist.dtos.RegisterRequest;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.repositories.UserRepository;
import com.lucasdota.todolist.services.JwtTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private UserDetailsService userDetailsService;
		@Autowired
		private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid AuthRequest authRequest) {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

            // Generate JWT token
            String jwtToken = jwtTokenService.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(jwtToken);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest registerRequest)	{
		if (this.userRepository.findByEmail(registerRequest.getEmail()) != null) return ResponseEntity.badRequest().build();

		String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequest.getPassword());
		User newUser = new User(registerRequest.getEmail(), encryptedPassword);

		this.userRepository.save(newUser);
		return ResponseEntity.ok().build();
	}
}