package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.dto.RepartidorRutNombreDTO;
import com.app.DeliveryApp.models.Repartidor;
import com.app.DeliveryApp.services.RepartidorService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/repartidores")
@CrossOrigin(origins="http://localhost:3000")
public class RepartidorController {

    private final RepartidorService repartidorService;
    private final GeometryFactory geometryFactory = new GeometryFactory();

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

    @GetMapping("/RutYnombres")
    public ResponseEntity<List<RepartidorRutNombreDTO>> ObtenerRutYnombresRepartidores(){
        List<RepartidorRutNombreDTO> repartidores = repartidorService.ObtenerRutYnombresRepartidor();
        return ResponseEntity.ok(repartidores);
    }

    @PutMapping("/{rut}")
    public ResponseEntity<Repartidor> actualizarRepartidor(@PathVariable String rut, @RequestBody Repartidor repartidor) {
        return repartidorService.actualizarRepartidor(rut, repartidor)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> eliminarRepartidor(@PathVariable String rut) {
        boolean eliminado = repartidorService.eliminarRepartidor(rut);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // actualizar ubicación de un repartidor
    @PutMapping("/{rut}/ubicacion")
    public ResponseEntity<Repartidor> actualizarUbicacionRepartidor(
            @PathVariable String rut, 
            @RequestBody Map<String, Double> coordenadas) {
        try {
            Double longitud = coordenadas.get("longitud");
            Double latitud = coordenadas.get("latitud");
            
            if (longitud == null || latitud == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Point nuevaUbicacion = geometryFactory.createPoint(new Coordinate(longitud, latitud));
            nuevaUbicacion.setSRID(4326);
            
            return repartidorService.actualizarUbicacionRepartidor(rut, nuevaUbicacion)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // actualizar distancia recorrida de un repartidor
    @PutMapping("/{rut}/distancia")
    public ResponseEntity<Repartidor> actualizarDistanciaRecorrida(
            @PathVariable String rut, 
            @RequestBody Map<String, Double> datos) {
        try {
            Double nuevaDistancia = datos.get("distancia");
            
            if (nuevaDistancia == null) {
                return ResponseEntity.badRequest().build();
            }
            
            return repartidorService.actualizarDistanciaRecorrida(rut, nuevaDistancia)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}