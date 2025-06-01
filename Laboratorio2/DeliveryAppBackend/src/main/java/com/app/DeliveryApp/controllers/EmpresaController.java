package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.Empresa;
import com.app.DeliveryApp.services.EmpresaService;
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
@RequestMapping("/api/v1/empresas")
@CrossOrigin(origins="http://localhost:3000")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    public ResponseEntity<?> crearEmpresa(@RequestBody Empresa empresa) {
        try {
            Empresa nuevaEmpresa = empresaService.crearEmpresa(empresa);
            return new ResponseEntity<>(nuevaEmpresa, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear empresa");
        }
    }

    @GetMapping("/{rut}")
    public ResponseEntity<Empresa> obtenerEmpresaPorRut(@PathVariable String rut) {
        return empresaService.obtenerEmpresaPorRut(rut)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Empresa>> obtenerTodasLasEmpresas() {
        List<Empresa> empresas = empresaService.obtenerTodasLasEmpresas();
        return ResponseEntity.ok(empresas);
    }

    @PutMapping("/{rut}")
    public ResponseEntity<Empresa> actualizarEmpresa(@PathVariable String rut, @RequestBody Empresa empresa) {
        return empresaService.actualizarEmpresa(rut, empresa)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable String rut) {
        boolean eliminado = empresaService.eliminarEmpresa(rut);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // actualizar ubicación de una empresa
    @PutMapping("/{rut}/ubicacion")
    public ResponseEntity<Empresa> actualizarUbicacionEmpresa(
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
            
            return empresaService.actualizarUbicacionEmpresa(rut, nuevaUbicacion)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // obtener empresas en un radio específico
    @GetMapping("/radio")
    public ResponseEntity<List<Empresa>> obtenerEmpresasEnRadio(
            @RequestParam Double longitud,
            @RequestParam Double latitud,
            @RequestParam Double radioMetros) {
        try {
            Point centro = geometryFactory.createPoint(new Coordinate(longitud, latitud));
            centro.setSRID(4326);
            
            List<Empresa> empresas = empresaService.obtenerEmpresasEnRadio(centro, radioMetros);
            return ResponseEntity.ok(empresas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // obtener empresa más cercana a una ubicación
    @GetMapping("/mas-cercana")
    public ResponseEntity<Empresa> obtenerEmpresaMasCercana(
            @RequestParam Double longitud,
            @RequestParam Double latitud) {
        try {
            Point ubicacion = geometryFactory.createPoint(new Coordinate(longitud, latitud));
            ubicacion.setSRID(4326);
            
            Empresa empresa = empresaService.obtenerEmpresaMasCercana(ubicacion);
            
            if (empresa != null) {
                return ResponseEntity.ok(empresa);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}