package com.github.camelya58.springsecurityjwt.models;

/**
 * Class AuthenticationRequest represents an incoming DTO for user authentication.
 *
 * @author Kamila Meshcheryakova
 * created 28.07.2020
 */
@SuppressWarnings("unused")
public class AuthenticationRequest {

    private String username;
    private String password;

    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AuthenticationRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
