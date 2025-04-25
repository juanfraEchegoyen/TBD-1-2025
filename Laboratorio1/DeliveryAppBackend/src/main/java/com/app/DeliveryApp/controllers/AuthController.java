package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.Usuario;
import com.app.DeliveryApp.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> register(@RequestBody Usuario user) {
        if(user.getNombre() == null || user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body("Faltan datos obligatorios");
        }
        try {
            authService.registro(user);
            return ResponseEntity.ok("Usuario registrado con éxito");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login/{nombre}/{password}")
    public ResponseEntity<String> login(@PathVariable String nombre, @PathVariable String password) {
        if (nombre == null || password == null) {
            return ResponseEntity.badRequest().body("Faltan datos obligatorios");
        }
        try {
            String token = authService.login(nombre, password);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
