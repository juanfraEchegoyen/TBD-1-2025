package com.app.GeoTaskApp.controllers;

import com.app.GeoTaskApp.Dto.UsuarioDTO;
import com.app.GeoTaskApp.respositories.JdbcUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private JdbcUsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerUsuarios() {
        List<UsuarioDTO> usuarios = usuarioRepository.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
}
