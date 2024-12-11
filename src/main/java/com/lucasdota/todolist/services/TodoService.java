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

		// Remove the Todo from the User's collection
		//user.getTodos().remove(todo); // This is crucial to maintain the relationship
		//userRepository.save(user); // Save the updated User entity to reflect the change

		todoRepository.delete(todo);
	}
}
