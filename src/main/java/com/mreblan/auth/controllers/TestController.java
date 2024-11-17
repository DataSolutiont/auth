package com.mreblan.auth.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {
    
    @Deprecated
    @GetMapping("/name")
    public String getUsername() {
        return "TEST NAME";
    }
}
