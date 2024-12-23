package com.lucasdota.todolist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucasdota.todolist.dtos.CreateTodoDTO;
import com.lucasdota.todolist.dtos.TodoDTO;
import com.lucasdota.todolist.services.TodoService;

@RestController
@RequestMapping("todo")
public class TodoController {
	    @Autowired
    TodoService todoService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Validated CreateTodoDTO data) {
        todoService.create(data.userId(), data.name(), data.description());
        return ResponseEntity.status(HttpStatus.CREATED).body("Todo created successfully");
    }

    @PutMapping
    public ResponseEntity<String> toggleDone(@RequestBody @Validated TodoDTO data) {
        todoService.update(data.todoId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Todo updated successfully");
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestBody @Validated TodoDTO data) {
        todoService.delete(data.todoId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Todo deleted successfully");
    }
}
