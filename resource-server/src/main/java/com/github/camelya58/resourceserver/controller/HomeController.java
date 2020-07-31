package com.github.camelya58.resourceserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class HomeController
 *
 * @author Kamila Meshcheryakova
 * created 30.07.2020
 */
@RestController
public class HomeController {

    @GetMapping("/api")
    public String home() {
        return "Welcome!";
    }
}
