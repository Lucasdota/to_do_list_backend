package com.lucasdota.todolist.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucasdota.todolist.entities.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
	List<Todo> findByUserId(Long userId);
}
