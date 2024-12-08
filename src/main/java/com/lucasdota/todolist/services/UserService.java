package com.lucasdota.todolist.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lucasdota.todolist.entities.Todo;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.repositories.TodoRepository;
import com.lucasdota.todolist.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	private final UserRepository userRepository;
  private final TodoRepository todoRepository;

  public UserService(UserRepository userRepository, TodoRepository todoRepository) {
    this.userRepository = userRepository;
    this.todoRepository = todoRepository;
  }

	public User create(User user) {
		return userRepository.save(user);
	}

	public List<User> list() {
		return userRepository.findAll();
	}

	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	public User update(Long id, User user) {
		if (!userRepository.existsById(id)) {
			throw new EntityNotFoundException("User not found with id " + id);
		}
		user.setId(id);
		return userRepository.save(user);
	}

	public void delete(Long id) {
		if (!userRepository.existsById(id)) {
			throw new EntityNotFoundException("User not found with id " + id);
		}
		// First, delete all Todos associated with the user
    List<Todo> todos = todoRepository.findByUserId(id);
    todoRepository.deleteAll(todos);
		userRepository.deleteById(id);
	}

	public Todo createTodoForUser(User user, Todo todo) {
		todo.setUser(user);
		return todoRepository.save(todo);
	}

	public List<Todo> getTodosForUser(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new EntityNotFoundException("User not found with id: " + userId);
		}
		return todoRepository.findByUserId(userId);
    }

    public Todo updateTodoForUser(Long userId, Long todoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Todo does not belong to User with id: " + userId);
        }

        // Update the Todo status
        todo.toggleDone();
        return todoRepository.save(todo);
    }

	public void deleteTodoForUser(Long userId, Long todoId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

		Todo todo = todoRepository.findById(todoId)
				.orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));

		if (!todo.getUser ().getId().equals(user.getId())) {
			throw new EntityNotFoundException("Todo does not belong to User with id: " + userId);
		}

		// Remove the Todo from the User's collection
		user.getTodos().remove(todo); // This is crucial to maintain the relationship
		userRepository.save(user); // Save the updated User entity to reflect the change

		// Finally, delete the Todo
		todoRepository.delete(todo);
	}
}
