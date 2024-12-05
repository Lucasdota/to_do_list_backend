package com.lucasdota.todolist.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lucasdota.todolist.entities.Todo;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.services.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create user
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        User createdUser  = userService.create(user);
        return new ResponseEntity<>(createdUser , HttpStatus.CREATED);
    }

    // Get user
    @GetMapping("/{userId}")
    public ResponseEntity<User> listUser(@PathVariable Long userId) {
        return userService.findById(userId)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    // Update user
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @Valid @RequestBody User updatedUser ) {
        try {
            User user = userService.update(userId, updatedUser );
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

	// Delete user
	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
		try {
			userService.delete(userId);
			return ResponseEntity.noContent().build();
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

    // Create a new Todo for a specific user
    @PostMapping("/{userId}/todos")
    public ResponseEntity<Todo> createTodoForUser(@PathVariable Long userId, @Valid @RequestBody Todo todo) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User  not found with id: " + userId));
            Todo createdTodo = userService.createTodoForUser (user, todo);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get all Todos for a specific User
    @GetMapping("/{userId}/todos")
    public ResponseEntity<List<Todo>> getTodosForUser(@PathVariable Long userId) {
        try {
            List<Todo> todos = userService.getTodosForUser (userId);
            return ResponseEntity.ok(todos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update a specific Todo for a specific User by userId and todoId
    @PutMapping("/{userId}/todos/{todoId}")
    public ResponseEntity<Todo> updateTodoForUser(@PathVariable Long userId, @PathVariable Long todoId, @Valid @RequestBody Todo updatedTodo) {
        try {
            Todo todo = userService.updateTodoForUser (userId, todoId, updatedTodo);
            return ResponseEntity.ok(todo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/todos/{todoId}")
    public ResponseEntity<Void> deleteTodoForUser(@PathVariable Long userId, @PathVariable Long todoId) {
        try {
            userService.deleteTodoForUser (userId, todoId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}