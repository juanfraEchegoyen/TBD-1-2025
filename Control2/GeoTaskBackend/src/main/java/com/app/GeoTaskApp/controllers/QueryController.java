package com.app.GeoTaskApp.controllers;

import com.app.GeoTaskApp.Dto.*;
import com.app.GeoTaskApp.services.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/querys")
public class QueryController {

    @Autowired
    private QueryService queryService;

    // 1. ¿Cuántas tareas ha hecho el usuario por sector?
    @GetMapping("/tareas-por-sector/{idUsuario}")
    public ResponseEntity<List<TareaPorSectorDTO>> getTareasPorSector(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(queryService.getTareasPorSector(idUsuario));
    }

    // 2. ¿Cuál es la tarea más cercana al usuario (que esté pendiente)?
    @GetMapping("/tarea-pendiente-mas-cercana/{idUsuario}")
    public ResponseEntity<TareaCercanaDTO> getTareaPendienteMasCercana(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(queryService.getTareaPendienteMasCercana(idUsuario));
    }

    // 3. ¿Cuál es el sector con más tareas completadas en un radio de 2 km?
    @GetMapping("/sector-mas-completadas-2km/{idUsuario}")
    public ResponseEntity<TareaPorSectorDTO> getSectorConMasTareasCompletadasEn2Km(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(queryService.getSectorConMasTareasCompletadasEn2Km(idUsuario));
    }

    // 4. ¿Cuál es el promedio de distancia de tareas completadas respecto al usuario?
    @GetMapping("/distancia-promedio-completadas/{idUsuario}")
    public ResponseEntity<DistanciaPromedioDTO> getPromedioDistancia(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(queryService.getPromedioDistanciaTareasCompletadas(idUsuario));
    }

    // 5. ¿En qué sectores se concentran más tareas pendientes?
    @GetMapping("/sectores-mas-pendientes")
    public ResponseEntity<List<SectorDTO>> getSectoresConMasTareasPendientes() {
        List<SectorDTO> sectores = queryService.getSectoresConMasTareasPendientes();
        return ResponseEntity.ok(sectores);
    }

    // 6. ¿Cuál es la tarea pendiente más cercana a la ubicación del usuario?
    @GetMapping("/tarea-pendiente-mas-cercana-v2/{idUsuario}")
    public ResponseEntity<TareaCercanaDTO> getTareaPendienteMasCercanaV2(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(queryService.getTareaPendienteMasCercanaV2(idUsuario));
    }

    // 7. ¿Cuántas tareas ha realizado cada usuario por sector?
    @GetMapping("/tareas-usuario-por-sector")
    public ResponseEntity<List<UsuarioSectorDTO>> getCantidadTareasPorUsuarioPorSector() {
        return ResponseEntity.ok(queryService.getCantidadTareasPorUsuarioPorSector());
    }

    // 8. ¿Cuál es el sector con más tareas completadas dentro de 5 km del usuario?
    @GetMapping("/sector-mas-completadas-5km/{idUsuario}")
    public ResponseEntity<TareaPorSectorDTO> getSectorConMasTareasCompletadasEn5Km(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(queryService.getSectorConMasTareasCompletadasEn5Km(idUsuario));
    }
}
