package com.github.camelya58.authresourceserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class HomeController is a simple REST-controller for main page.
 *
 * @author Kamila Meshcheryakova
 * created 03.08.2020
 */
@RestController
public class HomeController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello!";
    }
}
