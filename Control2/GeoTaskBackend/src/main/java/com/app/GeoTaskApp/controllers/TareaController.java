package com.app.GeoTaskApp.controllers;

import com.app.GeoTaskApp.models.Tarea;
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
    public ResponseEntity<String> createTarea(@RequestBody Tarea tarea) {
        boolean result = tareaService.createTarea(tarea);
        if (result) {
            return ResponseEntity.ok("Tarea creada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo crear la tarea");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTarea(@PathVariable Long id, @RequestBody Tarea tarea) {
        boolean result = tareaService.updateTarea(id, tarea);
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
}
