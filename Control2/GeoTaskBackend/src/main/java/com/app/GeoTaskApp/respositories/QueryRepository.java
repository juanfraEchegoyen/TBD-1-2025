package com.app.GeoTaskApp.respositories;

import com.app.GeoTaskApp.Dto.*;
import java.util.List;

public interface QueryRepository {
    // 1. ¿Cuántas tareas ha hecho el usuario por sector?
    List<TareaPorSectorDTO> getTareasPorSector(Long idUsuario);

    // 2. ¿Cuál es la tarea más cercana al usuario (que esté pendiente)?
    TareaCercanaDTO getTareaPendienteMasCercana(Long idUsuario);

    // 3. ¿Cuál es el sector con más tareas completadas en un radio de 2 kilómetros del usuario?
    TareaPorSectorDTO getSectorConMasTareasCompletadasEn2Km(Long idUsuario);

    // 4. ¿Cuál es el promedio de distancia de las tareas completadas respecto a la ubicación del usuario?
    //9.¿Cuál es el promedio de distancia entre las tareas completadas y el punto registrado del usuario?
    DistanciaPromedioDTO getPromedioDistanciaTareasCompletadas(Long idUsuario);

    // 5. ¿En qué sectores geográficos se concentran la mayoría de las tareas pendientes?
    List<SectorDTO> getSectoresConMasTareasPendientes();

    // 6. ¿Cuál es la tarea pendiente más cercana a la ubicación del usuario?
    TareaCercanaDTO getTareaPendienteMasCercanaV2(Long idUsuario);

    // 7. ¿Cuántas tareas ha realizado cada usuario por sector?
    List<UsuarioSectorDTO> getCantidadTareasPorUsuarioPorSector();

    // 8. ¿Cuál es el sector con más tareas completadas dentro de un radio de 5 km desde la ubicación del usuario?
    TareaPorSectorDTO getSectorConMasTareasCompletadasEn5Km(Long idUsuario);



}
