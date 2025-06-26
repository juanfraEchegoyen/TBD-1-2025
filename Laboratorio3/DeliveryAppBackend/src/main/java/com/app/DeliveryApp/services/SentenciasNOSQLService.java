
package com.app.DeliveryApp.services;

import com.app.DeliveryApp.repositories.mongo.OpinionClienteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SentenciasNOSQLService {

    @Autowired
    private OpinionClienteRepo opinionClienteRepo;

    public List<Map> getPromedioPuntuacionPorEmpresa() {

        return opinionClienteRepo.getPromedioPuntuacionPorEmpresa();
    }
}