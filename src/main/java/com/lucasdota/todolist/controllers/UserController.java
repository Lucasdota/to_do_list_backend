package com.lucasdota.todolist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucasdota.todolist.dtos.UserResponseDTO;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.services.TokenService;
import com.lucasdota.todolist.services.UserService;

import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("user")
public class UserController {
	
    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;

    @GetMapping
    public ResponseEntity<UserResponseDTO> getUser(@CookieValue(value = "JWT", required = false) String jwt) {
        if (jwt == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Long userId = tokenService.getUserIdFromToken(jwt);
        User user = userService.getUserById(userId);
        UserResponseDTO response = new UserResponseDTO(user.getId(), user.getEmail(), user.getTodos());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@CookieValue(value = "JWT", required = false) String jwt, HttpServletResponse response) {
        if (jwt == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Long userId = tokenService.getUserIdFromToken(jwt);
        userService.delete(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}