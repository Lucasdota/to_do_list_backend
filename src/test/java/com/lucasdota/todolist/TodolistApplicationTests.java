package com.lucasdota.todolist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.lucasdota.todolist.entities.Todo;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TodolistApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    private String getBasicAuthHeader() {
        String email = "email";
        String password = "password";
        String auth = email + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void testCreateTodoSuccess() {
        var todo = new Todo("todo 1", "desc todo 1");
        
        webTestClient
            .post()
            .uri("/todos")
            .header("Authorization", getBasicAuthHeader()) // Add Basic Auth header
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
			.uri("/todos")
			.header("Authorization", getBasicAuthHeader())
			.bodyValue(
				new Todo("", ""))
			.exchange()
			.expectStatus().isBadRequest();
    }
}