package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.MedioPago;
import com.app.DeliveryApp.services.MedioPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MedioPagoController {

    private final MedioPagoService medioPagoService;

    @Autowired
    public MedioPagoController(MedioPagoService medioPagoService) {
        this.medioPagoService = medioPagoService;
    }

    @PostMapping("/medios-pago")
    public ResponseEntity<?> crearMedioPago(@RequestBody MedioPago medioPago) {
        try {
            MedioPago nuevoMedioPago = medioPagoService.crearMedioPago(medioPago);
            return new ResponseEntity<>(nuevoMedioPago, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear medio de pago");
        }
    }

    @GetMapping("/medios-pago/{id}")
    public ResponseEntity<MedioPago> obtenerMedioPagoPorId(@PathVariable Long id) {
        return medioPagoService.obtenerMedioPagoPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/medios-pago")
    public ResponseEntity<List<MedioPago>> obtenerTodosLosMediosPago() {
        List<MedioPago> mediosPago = medioPagoService.obtenerTodosLosMediosPago();
        return ResponseEntity.ok(mediosPago);
    }

    @GetMapping("/clientes/{rutCliente}/medios-pago")
    public ResponseEntity<List<MedioPago>> obtenerMediosPagoPorCliente(@PathVariable String rutCliente) {
        List<MedioPago> mediosPago = medioPagoService.obtenerMediosPagoPorCliente(rutCliente);
        return ResponseEntity.ok(mediosPago);
    }

    @PutMapping("/medios-pago/{id}")
    public ResponseEntity<?> actualizarMedioPago(@PathVariable Long id, @RequestBody MedioPago medioPago) {
        try {
            return medioPagoService.actualizarMedioPago(id, medioPago)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar metodo de pago");
        }
    }

    @DeleteMapping("/medios-pago/{id}")
    public ResponseEntity<Void> eliminarMedioPago(@PathVariable Long id) {
        boolean eliminado = medioPagoService.eliminarMedioPago(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}