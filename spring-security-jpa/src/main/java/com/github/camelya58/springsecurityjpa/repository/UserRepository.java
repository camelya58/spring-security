package com.github.camelya58.springsecurityjpa.repository;

import com.github.camelya58.springsecurityjpa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interface UserRepository connects to database.
 *
 * @author Kamila Meshcheryakova
 * created 23.07.2020
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
}
