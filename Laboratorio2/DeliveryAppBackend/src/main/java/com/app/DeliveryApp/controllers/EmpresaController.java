package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.Empresa;
import com.app.DeliveryApp.services.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empresas")
@CrossOrigin(origins="http://localhost:3000")
public class EmpresaController {

    private final EmpresaService empresaService;

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
    }

    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable String rut) {
        boolean eliminado = empresaService.eliminarEmpresa(rut);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}