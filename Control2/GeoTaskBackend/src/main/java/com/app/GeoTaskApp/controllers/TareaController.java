package com.app.GeoTaskApp.controllers;

import com.app.GeoTaskApp.Dto.TareaRequestDTO;
import com.app.GeoTaskApp.Models.Sector;
import com.app.GeoTaskApp.Models.Tarea;

import com.app.GeoTaskApp.services.SectorService;
import com.app.GeoTaskApp.services.TareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {
    @Autowired
    private TareaService tareaService;
    @Autowired
    SectorService sectorService;

    @GetMapping
    public List<Tarea> getAllTareas() {
        return tareaService.getAllTareas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarea> getTareaById(@PathVariable Long id) {
        Tarea tarea = tareaService.getTareaById(id);
        if (tarea == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tarea);
    }

    @PostMapping
    public ResponseEntity<String> createTarea(@RequestBody TareaRequestDTO tareaRequestDTO) {
    boolean result2 = tareaService.createTarea(tareaRequestDTO);
        if (result2) {
            return ResponseEntity.ok("Tarea creada correctamente");
        }

    return ResponseEntity.badRequest().body("No se pudo crear la tarea");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTarea(@PathVariable Long id, @RequestBody TareaRequestDTO tareaRequestDTO) {
        boolean result = tareaService.updateTarea(id, tareaRequestDTO);
        if (result) {
            return ResponseEntity.ok("Tarea actualizada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo actualizar la tarea");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTarea(@PathVariable Long id) {
        boolean result = tareaService.deleteTarea(id);
        if (result) {
            return ResponseEntity.ok("Tarea eliminada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo eliminar la tarea");
        }
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<String> completarTarea(@PathVariable Long id) {
        boolean result = tareaService.completarTarea(id);
        if (result) {
            return ResponseEntity.ok("Tarea completada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo completar la tarea");
        }
    }
}
