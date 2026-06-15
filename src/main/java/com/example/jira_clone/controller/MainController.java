package com.example.jira_clone.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
public class MainController {

    @GetMapping({"/", "/api/v1", "/api/v1/"})
    public String home() {
        return "Jira Clone is running";
    }
    
}
