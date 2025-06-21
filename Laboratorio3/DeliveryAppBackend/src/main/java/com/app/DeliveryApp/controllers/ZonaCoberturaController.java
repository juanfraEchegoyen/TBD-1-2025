package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.ZonaCobertura;
import com.app.DeliveryApp.services.ZonaCoberturaService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.LinearRing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/zonas-cobertura")
@CrossOrigin(origins="http://localhost:3000")
public class ZonaCoberturaController {

    private final ZonaCoberturaService zonaCoberturaService;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    public ZonaCoberturaController(ZonaCoberturaService zonaCoberturaService) {
        this.zonaCoberturaService = zonaCoberturaService;
    }

    @PostMapping
    public ResponseEntity<?> crearZonaCobertura(@RequestBody ZonaCobertura zonaCobertura) {
        try {
            ZonaCobertura nuevaZona = zonaCoberturaService.crearZonaCobertura(zonaCobertura);
            return new ResponseEntity<>(nuevaZona, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear zona de cobertura");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZonaCobertura> obtenerZonaCoberturaPorId(@PathVariable Long id) {
        return zonaCoberturaService.obtenerZonaCoberturaPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ZonaCobertura>> obtenerTodasLasZonasCobertura() {
        List<ZonaCobertura> zonas = zonaCoberturaService.obtenerTodasLasZonasCobertura();
        return ResponseEntity.ok(zonas);
    }

    @GetMapping("/empresa/{rutEmpresa}")
    public ResponseEntity<List<ZonaCobertura>> obtenerZonasCoberturaByEmpresa(@PathVariable String rutEmpresa) {
        List<ZonaCobertura> zonas = zonaCoberturaService.obtenerZonasCoberturaByEmpresa(rutEmpresa);
        return ResponseEntity.ok(zonas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZonaCobertura> actualizarZonaCobertura(
            @PathVariable Long id, 
            @RequestBody ZonaCobertura zonaActualizada) {
        return zonaCoberturaService.actualizarZonaCobertura(id, zonaActualizada)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarZonaCobertura(@PathVariable Long id) {
        boolean eliminado = zonaCoberturaService.eliminarZonaCobertura(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/crear-con-coordenadas")
    public ResponseEntity<?> crearZonaCoberturaConCoordenadas(@RequestBody Map<String, Object> datos) {
        try {
            String nombre = (String) datos.get("nombre");
            String rutEmpresa = (String) datos.get("rutEmpresa");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Double>> coordenadas = (List<Map<String, Double>>) datos.get("coordenadas");
            
            if (coordenadas == null || coordenadas.size() < 3) {
                return ResponseEntity.badRequest().body("Se requieren al menos 3 coordenadas para formar un polígono");
            }

            if (!coordenadas.get(0).equals(coordenadas.get(coordenadas.size() - 1))) {
                coordenadas.add(coordenadas.get(0));
            }
            
            Coordinate[] coordinates = new Coordinate[coordenadas.size()];
            for (int i = 0; i < coordenadas.size(); i++) {
                Map<String, Double> coord = coordenadas.get(i);
                coordinates[i] = new Coordinate(coord.get("longitud"), coord.get("latitud"));
            }
            
            LinearRing shell = geometryFactory.createLinearRing(coordinates);
            org.locationtech.jts.geom.Polygon polygon = geometryFactory.createPolygon(shell);
            MultiPolygon areaCobertura = geometryFactory.createMultiPolygon(new org.locationtech.jts.geom.Polygon[] { polygon });
            areaCobertura.setSRID(4326);
            
            ZonaCobertura zonaCobertura = new ZonaCobertura();
            zonaCobertura.setNombre(nombre);
            zonaCobertura.setRutEmpresa(rutEmpresa);
            zonaCobertura.setAreaCobertura(areaCobertura);
            
            ZonaCobertura nuevaZona = zonaCoberturaService.crearZonaCobertura(zonaCobertura);
            return new ResponseEntity<>(nuevaZona, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear zona de cobertura: " + e.getMessage());
        }
    }

    @GetMapping("/contiene-punto")
    public ResponseEntity<List<ZonaCobertura>> obtenerZonasQueContienenPunto(
            @RequestParam Double longitud,
            @RequestParam Double latitud) {
        try {
            Point punto = geometryFactory.createPoint(new Coordinate(longitud, latitud));
            punto.setSRID(4326);
            
            List<ZonaCobertura> zonas = zonaCoberturaService.obtenerZonasQueContienenPunto(punto);
            return ResponseEntity.ok(zonas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/verificar-cobertura")
    public ResponseEntity<Map<String, Boolean>> verificarCobertura(
            @RequestParam String rutEmpresa,
            @RequestParam Double longitud,
            @RequestParam Double latitud) {
        try {
            Point puntoEntrega = geometryFactory.createPoint(new Coordinate(longitud, latitud));
            puntoEntrega.setSRID(4326);
            
            boolean enZonaCobertura = zonaCoberturaService.estaEnZonaCobertura(rutEmpresa, puntoEntrega);
            return ResponseEntity.ok(Map.of("enZonaCobertura", enZonaCobertura));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
