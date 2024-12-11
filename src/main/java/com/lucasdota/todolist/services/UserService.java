package com.lucasdota.todolist.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.lucasdota.todolist.entities.Todo;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.repositories.TodoRepository;
import com.lucasdota.todolist.repositories.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
  private final TodoRepository todoRepository;

  public UserService(UserRepository userRepository, TodoRepository todoRepository) {
    this.userRepository = userRepository;
    this.todoRepository = todoRepository;
  }

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
					.map(user -> user)
					.orElse(null);
	}

	public UserDetails findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public void delete(Long id) {
		// First, delete all Todos associated with the user
    List<Todo> todos = todoRepository.findByUserId(id);
		
    todoRepository.deleteAll(todos);
		userRepository.deleteById(id);
	}
	
}
