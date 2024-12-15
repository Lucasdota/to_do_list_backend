package com.lucasdota.todolist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.lucasdota.todolist.entities.Todo;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TodolistApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCreateTodoSuccess() {
        var todo = new Todo("todo 1", "desc todo 1");
        
        webTestClient
            .post()
            .uri("/todo")
            .bodyValue(todo)
            .exchange()
            .expectStatus().isCreated() // Expect 201 Created
            .expectBody()
            .jsonPath("$.name").isEqualTo(todo.getName())
            .jsonPath("$.description").isEqualTo(todo.getDescription());
    }

    @Test
    void testCreateTodoFailure() {
        webTestClient
            .post()
            .uri("/todo")
            .bodyValue(new Todo("", ""))
            .exchange()
            .expectStatus().isBadRequest();
    }
}