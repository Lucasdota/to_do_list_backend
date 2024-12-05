package com.lucasdota.todolist.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.lucasdota.todolist.entities.Todo;
import com.lucasdota.todolist.repositories.TodoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TodoService {
	private final TodoRepository todoRepository;

	public TodoService(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}
	
	public Todo create(String name, String desc) {

		Todo todo = new Todo(name, desc);

		// Check if a task with the same name already exists
		if (todoRepository.findByName(todo.getName()).isPresent()) {
			throw new IllegalArgumentException("A task with the name '" + todo.getName() + "' already exists.");
		}

		return todoRepository.save(todo);
	}

	public List<Todo> list() {
		Sort sort = Sort.by("id");
		return todoRepository.findAll(sort);
	}

	public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

	public Optional<Todo> update(Long id, Todo todo) {
		if (!todoRepository.existsById(id)) {
        	return Optional.empty();
		}
		todo.setId(id);
		return Optional.of(todoRepository.save(todo));
	}

	public void delete(Long id) {
		if (!todoRepository.existsById(id)) {
			throw new EntityNotFoundException("Todo not found with id: " + id);
		}
		todoRepository.deleteById(id);
	}
}
