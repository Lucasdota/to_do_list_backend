package com.lucasdota.todolist.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.lucasdota.todolist.entities.User;
import com.lucasdota.todolist.repositories.UserRepository;

@SpringBootTest
public class UserServiceTest {

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private UserService userService;

	private User testUser;

	@BeforeEach
  public void setUp() {
    testUser = new User("test@example.com", "Password123!");
    userRepository.save(testUser);
  }

	@Test
	void getUserById() {
		Long userId = testUser.getId();
		Mockito.when(userService.getUserById(userId)).thenReturn(testUser);
		User foundUser = userService.getUserById(userId);
		assertThat(testUser.getId()).isEqualTo(foundUser.getId());
	}

	@Test
	void getUserByEmail() {
		String email = testUser.getEmail();
		Mockito.when(userService.findUserByEmail(email)).thenReturn(testUser);
		UserDetails foundUser = userService.findUserByEmail(email);
		assertThat(testUser.getEmail()).isEqualTo(foundUser.getUsername());
	}

	@Test
	void deleteUser() {
		Long userId = testUser.getId();
		userService.delete(userId);
		User foundUser = userService.getUserById(userId);
		assertThat(foundUser).isNull();
	}

}
