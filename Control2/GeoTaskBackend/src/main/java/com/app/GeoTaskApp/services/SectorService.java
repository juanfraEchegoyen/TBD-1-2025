package com.app.GeoTaskApp.services;

import com.app.GeoTaskApp.models.Sector;
import com.app.GeoTaskApp.respositories.JdbcSectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectorService {
    @Autowired
    private JdbcSectorRepository sectorRepository;

    public List<Sector> getAllSectores() {
        return sectorRepository.findAll();
    }

    public Sector getSectorById(Long id) {
        return sectorRepository.findById(id);
    }

    public boolean createSector(Sector sector) {
        return sectorRepository.save(sector) > 0;
    }

    public boolean updateSector(Long id, Sector sector) {
        sector.setIdSector(id);
        return sectorRepository.update(sector) > 0;
    }

    public boolean deleteSector(Long id) {
        return sectorRepository.delete(id) > 0;
    }
}
