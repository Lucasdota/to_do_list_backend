package com.lucasdota.todolist.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucasdota.todolist.entities.Todo;
import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.repositories.TodoRepository;
import com.lucasdota.todolist.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TodoService {
	
    @Autowired
    TodoRepository todoRepository;
    @Autowired
    UserRepository userRepository;

    /**
     * Creates a todo setting the received user
     *
     * @param userId to get user from the repo
     * @param name of the todo
     * @param description of the todo
     */
    public void create(Long userId, String name, String description) {
        Optional<User> user = Optional.ofNullable(userRepository.getUserById(userId));
        if (user.isEmpty()) throw new EntityNotFoundException("User not found with id: " + userId);
        Todo newTodo = new Todo(name, description);
        newTodo.setUser(user.get());
        todoRepository.save(newTodo);
    }

    /**
     * Toggle the attribute done in the given todo
     *
     * @param todoId the todo id to look in the repo
     * @throws EntityNotFoundException if todo not found with id
     */
    public void update(Long todoId) {
        Optional<Todo> todo = todoRepository.findById(todoId);
        if (todo.isEmpty()) throw new EntityNotFoundException("Todo not found with id: " + todoId);
        todo.get().toggleDone();
        todoRepository.save(todo.get());
    }

    /**
     * delete the todo found by the id
     *
     * @param todoId to find in the repo
     * @throws EntityNotFoundException if todo not found with id
     */
    public void delete(Long todoId) {
        Optional<Todo> todo = todoRepository.findById(todoId);
        if (todo.isEmpty()) throw new EntityNotFoundException("Todo not found with id: " + todoId);
        todoRepository.delete(todo.get());
    }
}
