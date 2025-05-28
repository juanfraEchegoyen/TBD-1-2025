package com.app.GeoTaskApp.services;

import com.app.GeoTaskApp.Dto.*;
import com.app.GeoTaskApp.respositories.JdbcQuerysRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QueryService {
    @Autowired
    private JdbcQuerysRepository queryRepository;

    public List<TareaPorSectorDTO> getTareasPorSector(Long idUsuario) {
        return queryRepository.getTareasPorSector(idUsuario);
    }

    public TareaCercanaDTO getTareaPendienteMasCercana(Long idUsuario) {
        return queryRepository.getTareaPendienteMasCercana(idUsuario);
    }

    public TareaPorSectorDTO getSectorConMasTareasCompletadasEn2Km(Long idUsuario) {
        return queryRepository.getSectorConMasTareasCompletadasEn2Km(idUsuario);
    }

    public DistanciaPromedioDTO getPromedioDistanciaTareasCompletadas(Long idUsuario) {
        return queryRepository.getPromedioDistanciaTareasCompletadas(idUsuario);
    }

    public List<SectorDTO> getSectoresConMasTareasPendientes() {
        return queryRepository.getSectoresConMasTareasPendientes();
    }

    public TareaCercanaDTO getTareaPendienteMasCercanaV2(Long idUsuario) {
        return queryRepository.getTareaPendienteMasCercanaV2(idUsuario);
    }

    public List<UsuarioSectorDTO> getCantidadTareasPorUsuarioPorSector() {
        return queryRepository.getCantidadTareasPorUsuarioPorSector();
    }

    public TareaPorSectorDTO getSectorConMasTareasCompletadasEn5Km(Long idUsuario) {
        return queryRepository.getSectorConMasTareasCompletadasEn5Km(idUsuario);
    }
}