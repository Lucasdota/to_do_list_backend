package com.lucasdota.todolist.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.lucasdota.todolist.filter.JwtAuthenticationFilter;

import jakarta.servlet.http.Cookie;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Value("${jwt.cookie.name}")
  private String jwtCookieName;

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
  private UserDetailsService userDetailsService;

  @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			http
					.csrf(csrf -> csrf
							.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Configure CSRF
					)
					.cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configure CORS
					.sessionManagement(session -> session
							.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless session
					)
					.authorizeHttpRequests(authorize -> authorize
							.anyRequest().authenticated() // Require authentication for all requests
					)
					.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter
					.logout(logout -> logout
							.addLogoutHandler((request, response, authentication) -> {
									Cookie cookie = new Cookie(jwtCookieName, null);
									cookie.setMaxAge(0);
									cookie.setPath("/"); // Set the path to match your cookie
									response.addCookie(cookie);
							})
					);

			return http.build(); // Build the SecurityFilterChain
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Allow frontend URL
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP methods
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Allow cookies (JWT or session tokens)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
