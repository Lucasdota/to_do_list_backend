package com.lucasdota.todolist.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import com.lucasdota.todolist.entities.User;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	private User testUser;

	@BeforeEach
  public void setUp() {
    testUser  = new User("test@example.com", "Password123!");
    userRepository.save(testUser);
  }

	@Test
  public void testFindByEmail() {
    UserDetails foundUser  = userRepository.findByEmail(testUser.getEmail());
    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getUsername()).isEqualTo(testUser.getEmail());
  }

}
