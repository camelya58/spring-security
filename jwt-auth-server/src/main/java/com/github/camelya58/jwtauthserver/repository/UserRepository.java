package com.github.camelya58.jwtauthserver.repository;

import com.github.camelya58.jwtauthserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Класс UserRepository
 *
 * @author Kamila Meshcheryakova
 * created 01.09.2020
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findUserByUsername(String username);
}