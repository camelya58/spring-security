package com.github.camelya58.springsecurityfacebookauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;

@SpringBootApplication
@EnableOAuth2Sso
public class SpringSecurityFacebookAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityFacebookAuthApplication.class, args);
    }

}
