package com.lucasdota.todolist.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import com.lucasdota.todolist.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	User findByEmail(String email);
}
