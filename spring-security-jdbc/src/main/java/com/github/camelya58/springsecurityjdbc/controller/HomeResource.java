package com.github.camelya58.springsecurityjdbc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Class HomeResource is a simple Rest-controller.
 *
 * @author Kamila Meshcheryakova
 * created 22.07.2020
 */
@RestController
public class HomeResource {
    @GetMapping("/")
    public String home() {
        return ("<h1>Welcome</hi> ");
    }

    @GetMapping("/user")
    public String user(Principal principal) {
        return ("<h1>Welcome</hi>, ") + principal.getName();
    }

    @GetMapping("/admin")
    public String home(Principal principal) {
        return ("<h1>Welcome</hi>, ") + principal.getName();
    }
}
