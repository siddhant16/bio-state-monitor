package com.biostate.monitor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("{\"message\": \"Bio-State Fermentation Monitor API is running. Use /api/fermentation/analyze to analyze cultures.\"}");
    }
}