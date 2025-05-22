package com.app.GeoTaskApp.services;

import com.app.GeoTaskApp.Dto.TareaRequestDTO;
import com.app.GeoTaskApp.Models.Sector;
import com.app.GeoTaskApp.Models.Tarea;
import com.app.GeoTaskApp.respositories.JdbcSectorRepository;
import com.app.GeoTaskApp.respositories.JdbcTareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TareaService {
    @Autowired
    private JdbcTareaRepository tareaRepository;
    @Autowired
    JdbcSectorRepository sectorRepository;

    public List<Tarea> getAllTareas() {
        return tareaRepository.findAll();
    }

    public Tarea getTareaById(Long id) {
        return tareaRepository.findById(id);
    }

    public boolean createTarea(TareaRequestDTO tareaRequestDTO) {
        Tarea tarea = tareaRequestDTO.toTarea();
        Sector sector = tareaRequestDTO.toSector();

        if(sectorRepository.save(sector) != 0) {
            tarea.setIdSector(sector.getIdSector());
            return tareaRepository.save(tarea) > 0;
        }
        return false; // Sector not found
    }

    public boolean updateTarea(Long id, Tarea tarea) {
        tarea.setIdTarea(id);
        return tareaRepository.update(tarea) > 0;
    }

    public boolean deleteTarea(Long id) {
        return tareaRepository.delete(id) > 0;
    }
}
