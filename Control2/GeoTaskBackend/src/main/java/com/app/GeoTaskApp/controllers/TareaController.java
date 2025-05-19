package com.app.GeoTaskApp.controllers;

import com.app.GeoTaskApp.models.Tarea;
import com.app.GeoTaskApp.respositories.JdbcTareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {
    @Autowired
    private JdbcTareaRepository tareaRepository;

    @GetMapping
    public List<Tarea> getAllTareas() {
        return tareaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarea> getTareaById(@PathVariable Long id) {
        Tarea tarea = tareaRepository.findById(id);
        if (tarea == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tarea);
    }

    @PostMapping
    public ResponseEntity<String> createTarea(@RequestBody Tarea tarea) {
        int result = tareaRepository.save(tarea);
        if (result > 0) {
            return ResponseEntity.ok("Tarea creada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo crear la tarea");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTarea(@PathVariable Long id, @RequestBody Tarea tarea) {
        tarea.setIdTarea(id);
        int result = tareaRepository.update(tarea);
        if (result > 0) {
            return ResponseEntity.ok("Tarea actualizada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo actualizar la tarea");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTarea(@PathVariable Long id) {
        int result = tareaRepository.delete(id);
        if (result > 0) {
            return ResponseEntity.ok("Tarea eliminada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo eliminar la tarea");
        }
    }
}
