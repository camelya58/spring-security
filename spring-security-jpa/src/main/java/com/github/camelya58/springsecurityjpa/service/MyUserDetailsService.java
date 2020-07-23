package com.github.camelya58.springsecurityjpa.service;

import com.github.camelya58.springsecurityjpa.model.MyUserDetails;
import com.github.camelya58.springsecurityjpa.model.User;
import com.github.camelya58.springsecurityjpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Class MyUserDetailsService implements interface UserDetailsService and overrides a single method.
 *
 * @author Kamila Meshcheryakova
 * created 23.07.2020
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).
                orElseThrow(()-> new UsernameNotFoundException("Not found: " + username));
        return new MyUserDetails(user);
    }
}
