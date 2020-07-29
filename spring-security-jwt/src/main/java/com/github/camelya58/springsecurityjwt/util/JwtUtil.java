package com.github.camelya58.springsecurityjwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Class JwtUtil creates and validates a token.
 *
 * @author Kamila Meshcheryakova
 * created 27.07.2020
 */
@Service
public class JwtUtil {

    private final String SECRET_KEY = "secret";

    /**
     * Generates token for user using user details.
     * Creates empty claims and add them to token.
     *
     * @param userDetails the information about user
     * @return token calling the method createToken(Map<String, Object> claims, String username)
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates token with claims and user name using HS256 algorithm and secret key.
     * Sets token iat as current time and token expiration through 1 hour.
     *
     * @param claims map of claims
     * @param username the user name
     * @return token
     */
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    /**
     * Extracts claims from token by calling the method extractAllClaims(String token).
     *
     * @param token JWT
     * @param claimsResolver figures out what the claims are
     * @param <T> any class - the function result
     * @return the object of Class<T>
     */
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims using parser and secret key.
     *
     * @param token JWT
     * @return the object of class Claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    /**
     * Extracts user name from token.
     *
     * @param token JWT
     * @return user name calling the method extractClaims(String token, Function<Claims, T> claimsResolver)
     */
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Extracts token expiration from token.
     *
     * @param token JWT
     * @return expiration calling the method extractClaims(String token, Function<Claims, T> claimsResolver)
     */
    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    /**
     * Checks token expiration.
     *
     * @param token JWT
     * @return true if token has not expired or false if token has expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates token by comparing username from the token with username from  the user data
     * and checking if the token has not expired.
     *
     * @param token JWT
     * @param userDetails the information about user
     * @return true or false
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
