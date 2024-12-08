package com.lucasdota.todolist.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lucasdota.todolist.entities.Todo;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.services.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("user")
public class UserController {
	private final UserService userService;
	@Value("${jwt.secret}")
	private String jwtSecret;

	public UserController(UserService userService) {
			this.userService = userService;
	}

	private Long getUserIdFromJwt(String token) {
		try {
			Claims claims = Jwts.parser()
							.setSigningKey(jwtSecret)
							.parseClaimsJws(token)
							.getBody();
			return Long.parseLong(claims.getSubject());
		} catch (Exception e) {
			throw new EntityNotFoundException("User not found with this token");
		}
	}

	@GetMapping
	public ResponseEntity<User> listUser(@CookieValue(value = "JWT", required = false) String jwt) {
		if (jwt == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
		Long userId = getUserIdFromJwt(jwt);
		if (userId == null) {
			return ResponseEntity.notFound().build();
		}
		return userService.findById(userId)
							.map(user -> ResponseEntity.ok(user))
							.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser (@PathVariable Long userId, @CookieValue(value = "jwt", required = false) String jwt) {
			if (jwt == null) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			Long userIdFromToken = getUserIdFromJwt(jwt);
			if (!userId.equals(userIdFromToken)) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			try {
					userService.delete(userId);
					return ResponseEntity.noContent().build();
			} catch (EntityNotFoundException e) {
					return ResponseEntity.notFound().build();
			}
	}

	@PostMapping("/{userId}/todos")
	public ResponseEntity<Todo> createTodoForUser(@PathVariable Long userId, @Valid @RequestBody Todo todo) {
			try {
					User user = userService.findById(userId)
									.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
					Todo createdTodo = userService.createTodoForUser(user, todo);
					return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
			} catch (EntityNotFoundException e) {
					return ResponseEntity.notFound().build();
			}
	}

	@GetMapping("/{userId}/todos")
	public ResponseEntity<List<Todo>> getTodosForUser(@PathVariable Long userId) {
			try {
					List<Todo> todos = userService.getTodosForUser(userId);
					return ResponseEntity.ok(todos);
			} catch (EntityNotFoundException e) {
					return ResponseEntity.notFound().build();
			}
	}

	@PutMapping("/{userId}/todos/{todoId}")
	public ResponseEntity<Todo> updateTodoForUser(@PathVariable Long userId, @PathVariable Long todoId) {
			try {
					Todo todo = userService.updateTodoForUser(userId, todoId);
					return ResponseEntity.ok(todo);
			} catch (EntityNotFoundException e) {
					return ResponseEntity.notFound().build();
			}
	}

	@DeleteMapping("/{userId}/todos/{todoId}")
	public ResponseEntity<Void> deleteTodoForUser(@PathVariable Long userId, @PathVariable Long todoId) {
			try {
					userService.deleteTodoForUser(userId, todoId);
					return ResponseEntity.noContent().build();
			} catch (EntityNotFoundException e) {
					return ResponseEntity.notFound().build();
			}
	}
}