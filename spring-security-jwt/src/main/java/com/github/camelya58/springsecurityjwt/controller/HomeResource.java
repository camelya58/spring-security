package com.github.camelya58.springsecurityjwt.controller;

import com.github.camelya58.springsecurityjwt.models.AuthenticationRequest;
import com.github.camelya58.springsecurityjwt.models.AuthenticationResponse;
import com.github.camelya58.springsecurityjwt.service.MyUserDetailsService;
import com.github.camelya58.springsecurityjwt.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Class HomeResource is a simple REST-controller for home page.
 *
 * @author Kamila Meshcheryakova
 * created 27.07.2020
 */
@Api(tags = "/auth")
@RestController
public class HomeResource {

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @ApiOperation(value = "Say Hello", authorizations = {@Authorization(value = "JWT")})
    @RequestMapping({"/hello"})
    public String hello() {
        return "Hello World!";
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.
                    authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
