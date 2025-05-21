package com.app.GeoTaskApp.services;

import com.app.GeoTaskApp.Models.Ubicacion;
import com.app.GeoTaskApp.respositories.JdbcUbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UbicacionService {
    @Autowired
    private JdbcUbicacionRepository ubicacionRepository;

    public List<Ubicacion> getAllUbicaciones() {
        return ubicacionRepository.findAll();
    }

    public Ubicacion getUbicacionById(Long id) {
        return ubicacionRepository.findById(id);
    }

    public boolean createUbicacion(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion) > 0;
    }

    public boolean updateUbicacion(Long id, Ubicacion ubicacion) {
        ubicacion.setIdUbicacion(id);
        return ubicacionRepository.update(ubicacion) > 0;
    }

    public boolean deleteUbicacion(Long id) {
        return ubicacionRepository.delete(id) > 0;
    }
}

