package com.github.camelya58.jwtresourceserver.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class HelloController
 *
 * @author Kamila Meshcheryakova
 * created 31.07.2020
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    @ApiOperation(value = "hello", authorizations = {@Authorization(value = "JWT")})
    public String hello() {
        return "Hello!";
    }
}

