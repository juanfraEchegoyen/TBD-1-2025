package com.app.GeoTaskApp.services;

import com.app.GeoTaskApp.Dto.TareaRequestDTO;
import com.app.GeoTaskApp.Models.Sector;
import com.app.GeoTaskApp.Models.Tarea;
import com.app.GeoTaskApp.respositories.JdbcSectorRepository;
import com.app.GeoTaskApp.respositories.JdbcTareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TareaService {
    @Autowired
    private JdbcTareaRepository tareaRepository;
    @Autowired
    private JdbcSectorRepository sectorRepository;

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

        public boolean updateTarea(Long id, TareaRequestDTO tareaRequestDTO) {
            Tarea tareaExistente = tareaRepository.findById(id);

            if (tareaExistente == null) {
                throw new IllegalArgumentException("No se puede actualizar la tarea, no existe.");
            }

            // Convertir el DTO a entidad Tarea
            Tarea tarea = tareaRequestDTO.toTarea();

            // Actualizar solo los campos no nulos
            if (!tarea.getTitulo().isEmpty()) {
                tareaExistente.setTitulo(tarea.getTitulo());
            }

            if (tarea.getCategoria() != null && !tarea.getCategoria().isEmpty()) {
                tareaExistente.setCategoria(tarea.getCategoria());
            }

            if (!tarea.getDescripcion().isEmpty()) {
                tareaExistente.setDescripcion(tarea.getDescripcion());
            }
            if (tarea.getFechaVencimiento() != null) {
                tareaExistente.setFechaVencimiento(tarea.getFechaVencimiento());
            }
            if (!tarea.getEstado().isEmpty()) {
                tareaExistente.setEstado(tarea.getEstado());
            }

            // Si no se ingresa una ubicación nueva, se mantiene la existente
            if (!tareaRequestDTO.getUbicacion().isEmpty()) {
                Sector sector = tareaRequestDTO.toSector();
                Sector sectorExistente = sectorRepository.findById(tareaExistente.getIdSector());
                if (sector.getAsignacion().isEmpty()){
                    sector.setAsignacion(sectorExistente.getAsignacion());
                }
                if (sector.getComuna().isEmpty()){
                    sector.setComuna(sectorExistente.getComuna());
                }
                if (sector.getCalle().isEmpty()){
                    sector.setCalle(sectorExistente.getCalle());
                }
                if (sectorRepository.save(sector) != 0) {
                    tareaExistente.setIdSector(sector.getIdSector());
                }
            }
            // Actualizar la tarea
            return tareaRepository.update(tareaExistente) > 0;
        }

    public boolean deleteTarea(Long id) {
        return tareaRepository.delete(id) > 0;
    }

    public boolean completarTarea(Long id) {
        Tarea tareaExistente = tareaRepository.findById(id);

        if (tareaExistente == null) {
            return false;
        }

        return tareaRepository.actualizarEstado(id, "completado") > 0;
    }
}
