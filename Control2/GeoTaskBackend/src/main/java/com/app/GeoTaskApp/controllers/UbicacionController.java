package com.app.GeoTaskApp.controllers;

import com.app.GeoTaskApp.Models.Ubicacion;
import com.app.GeoTaskApp.services.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciónes")
public class UbicacionController {
    @Autowired
    private UbicacionService ubicacionService;

    @GetMapping
    public List<Ubicacion> getAllUbicaciones() {
        return ubicacionService.getAllUbicaciones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ubicacion> getUbicacionById(@PathVariable Long id) {
        Ubicacion ubicacion = ubicacionService.getUbicacionById(id);
        if (ubicacion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ubicacion);
    }

    @PostMapping
    public ResponseEntity<String> createUbicacion(@RequestBody Ubicacion ubicacion) {
        boolean result = ubicacionService.createUbicacion(ubicacion);
        if (result) {
            return ResponseEntity.ok("ubicación creada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo crear la ubicación");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUbicacion(@PathVariable Long id, @RequestBody Ubicacion ubicacion) {
        boolean result = ubicacionService.updateUbicacion(id, ubicacion);
        if (result) {
            return ResponseEntity.ok("ubicación actualizada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo actualizar la ubicación");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUbicacion(@PathVariable Long id) {
        boolean result = ubicacionService.deleteUbicacion(id);
        if (result) {
            return ResponseEntity.ok("ubicación eliminada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo eliminar la ubicación");
        }
    }
}
