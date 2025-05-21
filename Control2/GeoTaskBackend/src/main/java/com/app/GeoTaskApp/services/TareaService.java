package com.app.GeoTaskApp.services;

import com.app.GeoTaskApp.Models.Tarea;
import com.app.GeoTaskApp.respositories.JdbcTareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TareaService {
    @Autowired
    private JdbcTareaRepository tareaRepository;

    public List<Tarea> getAllTareas() {
        return tareaRepository.findAll();
    }

    public Tarea getTareaById(Long id) {
        return tareaRepository.findById(id);
    }

    public boolean createTarea(Tarea tarea) {
        return tareaRepository.save(tarea) > 0;
    }

    public boolean updateTarea(Long id, Tarea tarea) {
        tarea.setIdTarea(id);
        return tareaRepository.update(tarea) > 0;
    }

    public boolean deleteTarea(Long id) {
        return tareaRepository.delete(id) > 0;
    }
}
