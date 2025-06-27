
package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.mongo.OpinionCliente;
import com.app.DeliveryApp.repositories.mongo.OpinionClienteRepo;
import com.app.DeliveryApp.repositories.mongo.LogPedidoRepo;
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

    public List<OpinionCliente> getOpinionesConDemoraOError() {
        return opinionClienteRepo.getOpinionesConDemoraOError();
    }

    public List<Map> getOpinionesAgrupadasPorHora() {
        return opinionClienteRepo.getOpinionesAgrupadasPorHora();
    }
    @Autowired
    private LogPedidoRepo logPedidoRepo;

    public List<Map<String, Object>> getPedidosConMasDe3CambiosEn10Min() {
        return logPedidoRepo.findPedidosConMasDe3CambiosEn10Min();
    }
}