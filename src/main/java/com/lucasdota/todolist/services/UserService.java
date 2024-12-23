package com.lucasdota.todolist.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lucasdota.todolist.entities.Todo;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.repositories.TodoRepository;
import com.lucasdota.todolist.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	
    @Autowired
    UserRepository userRepository;
    @Autowired
    TodoRepository todoRepository;

    /**
     * Creates a user by their email and password.
     *
     * @param email the email of the user
     * @param encryptedPassword the encrypted password of the user
     */
    public void create(String email, String encryptedPassword) {
        User newUser = new User(email, encryptedPassword);
        userRepository.save(newUser);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return an Optional containing the user if found, or an empty Optional if not found
     */
    public User getUserById(Long id) {
        User user = userRepository.getUserById(id);
        if (user == null) throw new EntityNotFoundException("User not found with id: " + id);
        return user;
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return an Optional containing the user details if found, or an empty Optional if not found
     */
    public Optional<UserDetails> getUserByEmail(String email) {
        return Optional.of(userRepository.findByEmail(email));
    }

    /**
     * Deletes a user and all their associated todos.
     *
     * @param userId the ID of the user to delete
     * @throws IllegalArgumentException if the userId is null
     */
    @Transactional
    public void delete(Long userId) {
        List<Todo> todos = todoRepository.findByUserId(userId);
        todoRepository.deleteAll(todos);
        userRepository.deleteById(userId);
    }
	
}
