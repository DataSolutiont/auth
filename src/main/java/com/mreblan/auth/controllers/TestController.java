package com.mreblan.auth.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/test")
public class TestController {
    
    @GetMapping("/name")
    public String getUsername() {
        return "TEST NAME";
    }
}
