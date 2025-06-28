package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.mongo.OpinionCliente;
import com.app.DeliveryApp.services.SentenciasNOSQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sentenciasnosql")
@CrossOrigin(origins="http://localhost:3000")
public class SentenciasNOSQLController {

    @Autowired
    private SentenciasNOSQLService sentenciasNOSQLService;

    //consulta 1
    @GetMapping("/promedio-por-empresa")
    public List<Map> getPromedioPuntuacionPorEmpresa() {
        return sentenciasNOSQLService.getPromedioPuntuacionPorEmpresa();
    }

    //consulta 2
    @GetMapping("/OpinionesConDemoraOError")
    public List<OpinionCliente> getOpinionesConDemoraOError() {
        return sentenciasNOSQLService.getOpinionesConDemoraOError();
    }

    //consulta 3
    @GetMapping("/pedidos-cambios-rapidos")
    public List<Map<String, Object>> getPedidosConMasDe3CambiosEn10Min() {
        return sentenciasNOSQLService.getPedidosConMasDe3CambiosEn10Min();
    }

    //consulta 4
    @GetMapping("/rutas-frecuentes-ultimos-7-dias")
    public List<Map> getRutasFrecuentesUltimos7Dias() {
        return sentenciasNOSQLService.getRutasFrecuentesUltimos7Dias();
    }

    @GetMapping("/rutas-por-repartidor/{repartidorId}")
    public List<Map> getRutasPorRepartidor(@PathVariable String repartidorId) {
        return sentenciasNOSQLService.getRutasPorRepartidor(repartidorId);
    }

    // Endpoints adicionales para debug
    @GetMapping("/debug/todas-las-rutas")
    public List<Map> getAllRutasForDebug() {
        return sentenciasNOSQLService.getAllRutasForDebug();
    }


    //consulta 5
    @GetMapping("/clientes-sin-evento-compra")
    public List<String> getClienteIdsSinEventoCompra(){
        return sentenciasNOSQLService.getClienteIdsSinEventoCompra();
    }

    //consulta 6
    @GetMapping("/OpinionesAgrupadasPorHora")
    public List<Map> getOpinionesAgrupadasPorHora() {
        return sentenciasNOSQLService.getOpinionesAgrupadasPorHora();
    }
}
