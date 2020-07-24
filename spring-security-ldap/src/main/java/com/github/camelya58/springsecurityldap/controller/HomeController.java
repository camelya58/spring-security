package com.github.camelya58.springsecurityldap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class HomeController is a simple REST-controller.
 *
 * @author Kamila Meshcheryakova
 * created 23.07.2020
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "Welcome to Home page";
    }
}
