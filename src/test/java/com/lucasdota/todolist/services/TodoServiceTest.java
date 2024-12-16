package com.lucasdota.todolist.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.lucasdota.todolist.entities.Todo;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.repositories.TodoRepository;
import com.lucasdota.todolist.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
public class TodoServiceTest {
	
	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private TodoRepository todoRepository;

	@MockitoBean
	private TodoService todoService;

	private User testUser;
	private Todo testTodo;

	@BeforeEach
	public void setUp() {
    testUser = new User("test@example.com", "Password123!");
		testTodo = new Todo("task 1", "do the dishes");
		testTodo.setId(1L); 
		userRepository.save(testUser);
		todoRepository.save(testTodo);
  }

	@Test
	void createTodo() {
		todoService.create(testUser, testTodo);
		Mockito.when(todoRepository.findByName(testTodo.getName())).thenReturn(Optional.of(testTodo));
		Optional<Todo> foundTodo = todoRepository.findByName(testTodo.getName());
		assertThat(foundTodo).isPresent();
	}

	@Test
	void deleteTodo() {
		todoService.create(testUser, testTodo);	
		Mockito.when(todoRepository.findByName(testTodo.getName())).thenReturn(Optional.of(testTodo));
		Optional<Todo> foundTodo = todoRepository.findByName(testTodo.getName());
		assertThat(foundTodo.get().getId()).isEqualTo(testTodo.getId());
		todoService.delete(foundTodo.get().getId());
		Optional<Todo> deletedTodo = todoRepository.findByName(testTodo.getName());
		assertThat(deletedTodo).isEmpty();
	}

}
