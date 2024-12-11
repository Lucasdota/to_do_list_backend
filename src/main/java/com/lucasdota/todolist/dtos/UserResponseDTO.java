package com.lucasdota.todolist.dtos;

import java.util.List;

import com.lucasdota.todolist.entities.Todo;

public record UserResponseDTO(Long id, String email, List<Todo> todos) {
}
