package user_service.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import user_service.models.User;
import user_service.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) throws JsonProcessingException,  InterruptedException{
        try {
            String token = authService.registerUser(user);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String login, @RequestParam String password) throws InterruptedException{
        try {
            String token = authService.authenticate(login, password);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }



}
