package com.lucasdota.todolist.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucasdota.todolist.entities.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

	Optional<Todo> findByName(String name);
	List<Todo> findByUserId(Long id);
	
}
