package com.lucasdota.todolist.services;

import org.springframework.stereotype.Service;

import com.lucasdota.todolist.entities.Todo;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.repositories.TodoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TodoService {
	private final TodoRepository todoRepository;

	public TodoService(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	public Todo create(User user, Todo todo) {
		todo.setUser(user);
		return todoRepository.save(todo);
	}

	public Todo update(Long todoId) {
		Todo todo = todoRepository.findById(todoId)
							.orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));
		todo.toggleDone();
		return todoRepository.save(todo);
	}

	public void delete(Long todoId) {
		Todo todo = todoRepository.findById(todoId)
				.orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));
		todoRepository.delete(todo);
	}
}
