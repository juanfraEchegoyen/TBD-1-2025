package com.app.GeoTaskApp.controllers;

import com.app.GeoTaskApp.Dto.SectorDTO;
import com.app.GeoTaskApp.Models.Sector;
import com.app.GeoTaskApp.services.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sectores")
public class SectorController {
    @Autowired
    private SectorService sectorService;

    @GetMapping
    public List<Sector> getAllSectores() {
        return sectorService.getAllSectores();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSectorById(@PathVariable Long id) {
        Sector sector = sectorService.getSectorById(id);
        if (sector == null) {
            return ResponseEntity.notFound().build();
        }

        SectorDTO sectorDTO = new SectorDTO();
        sectorDTO.setIdSector(sector.getIdSector());
        sectorDTO.setAsignacion(sector.getAsignacion());
        sectorDTO.setCalle(sector.getCalle());
        sectorDTO.setComuna(sector.getComuna());

        // Extraer longitud y latitud del punto
        if (sector.getUbicacion() != null) {
            sectorDTO.setLongitud(sector.getUbicacion().getX());
            sectorDTO.setLatitud(sector.getUbicacion().getY());
        }

        return ResponseEntity.ok(sectorDTO);
    }

    @PostMapping
    public ResponseEntity<String> createSector(@RequestBody Sector sector) {
        boolean result = sectorService.createSector(sector);
        if (result) {
            return ResponseEntity.ok("Sector creado correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo crear el sector");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSector(@PathVariable Long id, @RequestBody Sector sector) {
        boolean result = sectorService.updateSector(id, sector);
        if (result) {
            return ResponseEntity.ok("Sector actualizado correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo actualizar el sector");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSector(@PathVariable Long id) {
        boolean result = sectorService.deleteSector(id);
        if (result) {
            return ResponseEntity.ok("Sector eliminado correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo eliminar el sector");
        }
    }
}
