package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.Puntuacion;
import com.app.DeliveryApp.services.PuntuacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins="http://localhost:3000")
public class PuntuacionController {

    private final PuntuacionService puntuacionService;

    @Autowired
    public PuntuacionController(PuntuacionService puntuacionService) {
        this.puntuacionService = puntuacionService;
    }

    @PostMapping("/puntuaciones")
    public ResponseEntity<?> crearPuntuacion(@RequestBody Puntuacion puntuacion) {
        try {
            Puntuacion nuevaPuntuacion = puntuacionService.crearPuntuacion(puntuacion);
            return new ResponseEntity<>(nuevaPuntuacion, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la puntuacion");
        }
    }

    @GetMapping("/puntuaciones/{id}")
    public ResponseEntity<Puntuacion> obtenerPuntuacionPorId(@PathVariable Long id) {
        return puntuacionService.obtenerPuntuacionPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/puntuaciones")
    public ResponseEntity<List<Puntuacion>> obtenerTodasLasPuntuaciones() {
        List<Puntuacion> puntuaciones = puntuacionService.obtenerTodasLasPuntuaciones();
        return ResponseEntity.ok(puntuaciones);
    }

    @GetMapping("/repartidores/{rutRepartidor}/puntuaciones")
    public ResponseEntity<List<Puntuacion>> obtenerPuntuacionesPorRepartidor(@PathVariable String rutRepartidor) {
        // TODO: validar que el repartidor exista antes de llamar al servicio
        List<Puntuacion> puntuaciones = puntuacionService.obtenerPuntuacionesPorRepartidor(rutRepartidor);
        return ResponseEntity.ok(puntuaciones);
    }


    @PutMapping("/puntuaciones/{id}")
    public ResponseEntity<?> actualizarPuntuacion(@PathVariable Long id, @RequestBody Puntuacion puntuacion) {
        try {
            return puntuacionService.actualizarPuntuacion(id, puntuacion)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al  actualizar la puntuacion");
        }
    }

    @DeleteMapping("/puntuaciones/{id}")
    public ResponseEntity<Void> eliminarPuntuacion(@PathVariable Long id) {
        boolean eliminado = puntuacionService.eliminarPuntuacion(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}