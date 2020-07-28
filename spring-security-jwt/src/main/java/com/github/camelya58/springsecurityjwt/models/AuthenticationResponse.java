package com.github.camelya58.springsecurityjwt.models;

/**
 * Class AuthenticationResponse represents an outgoing DTO for receiving a token.
 *
 * @author Kamila Meshcheryakova
 * created 28.07.2020
 */
@SuppressWarnings("unused")
public class AuthenticationResponse {

    private final String token;

    public AuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
