package com.github.camelya58.springsecurityjwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Class HomeResource is a simple controller for home page.
 *
 * @author Kamila Meshcheryakova
 * created 27.07.2020
 */
@Controller
public class HomeResource {

    @RequestMapping({"/hello"})
    public String hello() {
        return "Hello World!";
    }
}
