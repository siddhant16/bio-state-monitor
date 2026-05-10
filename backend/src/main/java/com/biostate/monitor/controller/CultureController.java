package com.biostate.monitor.controller;

import com.biostate.monitor.model.Culture;
import com.biostate.monitor.model.User;
import com.biostate.monitor.service.CultureService;
import com.biostate.monitor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cultures")
public class CultureController {

    @Autowired
    private CultureService cultureService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createCulture(@RequestBody Map<String, String> request, Authentication auth) {
        String username = auth.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        User user = userOpt.get();
        String name = request.get("name");
        String type = request.get("type");
        Culture culture = cultureService.createCulture(name, type, user);
        return ResponseEntity.ok(culture);
    }

    @GetMapping
    public ResponseEntity<List<Culture>> getCultures(Authentication auth) {
        String username = auth.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = userOpt.get();
        List<Culture> cultures = cultureService.getCulturesByUser(user);
        return ResponseEntity.ok(cultures);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCulture(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        Optional<Culture> cultureOpt = cultureService.getCultureById(id);
        if (cultureOpt.isEmpty() || !cultureOpt.get().getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Culture not found or not owned by user"));
        }
        cultureService.deleteCulture(id);
        return ResponseEntity.ok(Map.of("message", "Culture deleted"));
    }
}