package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.PuntoInteres;
import com.app.DeliveryApp.services.PuntoInteresService;
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
@RequestMapping("/api/v1/puntos-interes")
@CrossOrigin(origins="http://localhost:3000")
public class PuntoInteresController {

    private final PuntoInteresService puntoInteresService;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    public PuntoInteresController(PuntoInteresService puntoInteresService) {
        this.puntoInteresService = puntoInteresService;
    }

    @PostMapping
    public ResponseEntity<?> crearPuntoInteres(@RequestBody PuntoInteres puntoInteres) {
        try {
            PuntoInteres nuevoPunto = puntoInteresService.crearPuntoInteres(puntoInteres);
            return new ResponseEntity<>(nuevoPunto, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear punto de interés");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuntoInteres> obtenerPuntoInteresPorId(@PathVariable Long id) {
        return puntoInteresService.obtenerPuntoInteresPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PuntoInteres>> obtenerTodosLosPuntosInteres() {
        List<PuntoInteres> puntos = puntoInteresService.obtenerTodosLosPuntosInteres();
        return ResponseEntity.ok(puntos);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<PuntoInteres>> obtenerPuntosInteresPorTipo(@PathVariable String tipo) {
        List<PuntoInteres> puntos = puntoInteresService.obtenerPuntosInteresPorTipo(tipo);
        return ResponseEntity.ok(puntos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PuntoInteres> actualizarPuntoInteres(
            @PathVariable Long id, 
            @RequestBody PuntoInteres puntoActualizado) {
        return puntoInteresService.actualizarPuntoInteres(id, puntoActualizado)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPuntoInteres(@PathVariable Long id) {
        boolean eliminado = puntoInteresService.eliminarPuntoInteres(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Endpoints geoespaciales
    
    @PostMapping("/crear-con-coordenadas")
    public ResponseEntity<?> crearPuntoInteresConCoordenadas(@RequestBody Map<String, Object> datos) {
        try {
            String nombre = (String) datos.get("nombre");
            String tipo = (String) datos.get("tipo");
            String descripcion = (String) datos.get("descripcion");
            Double longitud = (Double) datos.get("longitud");
            Double latitud = (Double) datos.get("latitud");
            
            if (longitud == null || latitud == null) {
                return ResponseEntity.badRequest().body("Se requieren coordenadas válidas");
            }
            
            Point ubicacion = geometryFactory.createPoint(new Coordinate(longitud, latitud));
            ubicacion.setSRID(4326);
            
            PuntoInteres puntoInteres = new PuntoInteres();
            puntoInteres.setNombre(nombre);
            puntoInteres.setTipo(tipo);
            puntoInteres.setDescripcion(descripcion);
            puntoInteres.setUbicacion(ubicacion);
            
            PuntoInteres nuevoPunto = puntoInteresService.crearPuntoInteres(puntoInteres);
            return new ResponseEntity<>(nuevoPunto, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear punto de interés: " + e.getMessage());
        }
    }

    @GetMapping("/en-radio")
    public ResponseEntity<List<PuntoInteres>> obtenerPuntosInteresEnRadio(
            @RequestParam Double longitud,
            @RequestParam Double latitud,
            @RequestParam Double radioMetros) {
        try {
            Point centro = geometryFactory.createPoint(new Coordinate(longitud, latitud));
            centro.setSRID(4326);
            
            List<PuntoInteres> puntos = puntoInteresService.obtenerPuntosInteresEnRadio(centro, radioMetros);
            return ResponseEntity.ok(puntos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/mas-cercano")
    public ResponseEntity<PuntoInteres> obtenerPuntoInteresmasCercano(
            @RequestParam Double longitud,
            @RequestParam Double latitud,
            @RequestParam String tipo) {
        try {
            Point ubicacion = geometryFactory.createPoint(new Coordinate(longitud, latitud));
            ubicacion.setSRID(4326);
            
            PuntoInteres punto = puntoInteresService.obtenerPuntoInteresmasCercano(ubicacion, tipo);
            
            if (punto != null) {
                return ResponseEntity.ok(punto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
