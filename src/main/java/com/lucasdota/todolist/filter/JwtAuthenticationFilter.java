package com.lucasdota.todolist.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
			String jwt = extractJwtFromCookies(request);
			if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					try {
							String email = getUsernameFromToken(jwt); // Get email from token
							UserDetails userDetails = userDetailsService.loadUserByUsername(email); // Load user by email
							if (userDetails != null) {
									// Set the authentication in the context
									UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
													userDetails, null, userDetails.getAuthorities());
									authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
									SecurityContextHolder.getContext().setAuthentication(authentication);
							}
					} catch (ExpiredJwtException e) {
							logger.error("JWT Token is expired: {}", e.getMessage());
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is expired");
					} catch (UnsupportedJwtException e) {
							logger.error("JWT Token is unsupported: {}", e.getMessage());
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is unsupported");
					} catch (Exception e) {
							logger.error("JWT Token is invalid: {}", e.getMessage());
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is invalid");
					}
			}
			chain.doFilter(request, response);
    }

    private String extractJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) { // Replace with your cookie name
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // This should return the email
    }
}