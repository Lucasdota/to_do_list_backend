package com.lucasdota.todolist.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.lucasdota.todolist.entities.User;

@SpringBootTest
public class UserRepositoryTest {

	@MockitoBean
	private UserRepository userRepository;

	private User testUser;

	@BeforeEach
  public void setUp() {
    testUser  = new User("test@example.com", "Password123!");
    userRepository.save(testUser);
  }

	@Test
  public void testFindByEmailSuccess() {
		Mockito.when(userRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
    User foundUser = userRepository.findByEmail(testUser.getEmail());      
    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getUsername()).isEqualTo(testUser.getEmail());
  }

	@Test
  public void testFindByEmailFail() {
    User foundUser = userRepository.findByEmail("failemail@gmail.com");      
    assertThat(foundUser).isNull();
  }

	@Test
	public void testFindByIdSuccess() {
		Mockito.when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
		Optional<User> foundUser = userRepository.findById(testUser.getId());
		assertThat(foundUser).isNotNull();
	}

	@Test
	public void testFindByIdFail() {
		Optional<User> foundUser = userRepository.findById(2L);
		assertThat(foundUser).isEmpty();
	}

	@Test
	public void testDeleteByIdSuccess() {
		Optional<User> foundUser  = userRepository.findById(testUser.getId());
    assertThat(foundUser).isNotNull();

    userRepository.deleteById(testUser.getId());

    Optional<User> deletedUser  = userRepository.findById(testUser.getId());
    assertThat(deletedUser ).isEmpty();
	}

	@Test
	public void testDeleteByIdFail() {
		Optional<User> foundUser = userRepository.findById(testUser.getId());
		assertThat(foundUser).isNotNull();

		userRepository.deleteById(2L);

		Optional<User> deletedUser = userRepository.findById(testUser.getId());
		assertThat(deletedUser).isNotNull();
	}

}
