package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.Repartidor;
import com.app.DeliveryApp.services.RepartidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repartidores")
@CrossOrigin(origins="http://localhost:3000")
public class RepartidorController {

    private final RepartidorService repartidorService;

    @Autowired
    public RepartidorController(RepartidorService repartidorService) {
        this.repartidorService = repartidorService;
    }

    @PostMapping
    public ResponseEntity<?> crearRepartidor(@RequestBody Repartidor repartidor) {
        try {
            Repartidor nuevoRepartidor = repartidorService.crearRepartidor(repartidor);
            return new ResponseEntity<>(nuevoRepartidor, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear un repartidor");
        }
    }

    @GetMapping("/{rut}")
    public ResponseEntity<Repartidor> obtenerRepartidorPorRut(@PathVariable String rut) {
        return repartidorService.obtenerRepartidorPorRut(rut)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Repartidor>> obtenerTodosLosRepartidores() {
        List<Repartidor> repartidores = repartidorService.obtenerTodosLosRepartidores();
        return ResponseEntity.ok(repartidores);
    }

    @PutMapping("/{rut}")
    public ResponseEntity<Repartidor> actualizarRepartidor(@PathVariable String rut, @RequestBody Repartidor repartidor) {
        return repartidorService.actualizarRepartidor(rut, repartidor)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> eliminarRepartidor(@PathVariable String rut) {
        boolean eliminado = repartidorService.eliminarRepartidor(rut);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}