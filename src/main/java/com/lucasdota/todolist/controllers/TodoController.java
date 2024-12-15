package com.lucasdota.todolist.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucasdota.todolist.dtos.CreateTodoDTO;
import com.lucasdota.todolist.dtos.TodoDTO;
import com.lucasdota.todolist.entities.Todo;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.services.TodoService;
import com.lucasdota.todolist.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("todo")
public class TodoController {
	protected final Log logger = LogFactory.getLog(getClass());
	private final TodoService todoService;
	private final UserService userService;

	public TodoController(TodoService todoService, UserService userService) {
		this.todoService = todoService;
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<String> createTodo(@RequestBody @Valid CreateTodoDTO data) {
    try {
        logger.warn("userId: " + data.userId());
        User user = userService.getUserById(data.userId());
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        Todo todo = new Todo(data.name(), data.description());
        todoService.create(user, todo);
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Todo created successfully");
    } catch (Exception e) {
        logger.error("Error creating todo: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the todo");
    }
	}

	@PutMapping
	public ResponseEntity<String> toggleDone(@RequestBody @Valid TodoDTO data) {
		Todo todo = todoService.update(data.todoId());
		if (todo == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.status(HttpStatus.OK).body("Todo updated successfully");
	}

	@DeleteMapping
	public ResponseEntity<String> deleteTodo(@RequestBody @Valid TodoDTO data) {
		todoService.delete(data.todoId());
		return ResponseEntity.status(HttpStatus.OK).body("Todo deleted successfully");
	}
}
