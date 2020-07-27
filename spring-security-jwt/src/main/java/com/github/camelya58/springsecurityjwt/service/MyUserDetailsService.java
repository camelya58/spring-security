package com.github.camelya58.springsecurityjwt.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Class MyUserDetailsService allows to create own way to load user.
 *
 * @author Kamila Meshcheryakova
 * created 27.07.2020
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User("user", "password", new ArrayList<>());
    }
}
