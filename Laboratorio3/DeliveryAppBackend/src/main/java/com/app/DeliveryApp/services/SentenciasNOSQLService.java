package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.mongo.OpinionCliente;
import com.app.DeliveryApp.repositories.mongo.OpinionClienteRepo;
import com.app.DeliveryApp.repositories.mongo.LogPedidoRepo;
import com.app.DeliveryApp.repositories.mongo.HistorialRepartidorRepo;
import com.app.DeliveryApp.repositories.mongo.NavegacionUsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SentenciasNOSQLService {

    @Autowired
    private OpinionClienteRepo opinionClienteRepo;

    @Autowired
    private LogPedidoRepo logPedidoRepo;

    @Autowired
    private HistorialRepartidorRepo historialRepartidorRepo;

    @Autowired
    private NavegacionUsuarioRepo navegacionUsuarioRepo;

    /**
     * Consulta 1: Obtiene el promedio de puntuación por empresa
     */
    public List<Map> getPromedioPuntuacionPorEmpresa() {
        return opinionClienteRepo.getPromedioPuntuacionPorEmpresa();
    }

    /**
     * Consulta 2: Lista opiniones que contienen palabras clave como 'demora' o 'error'
     */
    public List<OpinionCliente> getOpinionesConDemoraOError() {
        return opinionClienteRepo.getOpinionesConDemoraOError();
    }

    /**
     * Consulta 3: Cuenta pedidos con más de 3 cambios de estado en menos de 10 minutos
     */
    public List<Map<String, Object>> getPedidosConMasDe3CambiosEn10Min() {
        return logPedidoRepo.findPedidosConMasDe3CambiosEn10Min();
    }

    /**
     * Consulta 4: Analiza las rutas más frecuentes de repartidores en los últimos 7 días
     * Retorna zonas ordenadas por cantidad de repartidores únicos que las visitan
     */
    public List<Map> getRutasFrecuentesUltimos7Dias() {
        return historialRepartidorRepo.getRutasFrecuentesUltimos7Dias();
    }

    /**
     * Consulta 4 alternativa: Rutas específicas por repartidor
     */
    public List<Map> getRutasPorRepartidor(String repartidorId) {
        return historialRepartidorRepo.getRutasPorRepartidor(repartidorId);
    }

    /**
     * Método de debug para verificar datos
     */
    public List<Map> getAllRutasForDebug() {
        return historialRepartidorRepo.getAllRutasForDebug();
    }



    /**
     * Consulta 5: Detecta clientes que realizaron búsquedas sin concretar pedidos
     */
    public List<String> getClienteIdsSinEventoCompra() {
        return navegacionUsuarioRepo.getClienteIdsSinEventoCompra();
    }

    /**
     * Consulta 6: Agrupa opiniones por hora del día para analizar patrones de satisfacción
     */
    public List<Map> getOpinionesAgrupadasPorHora() {
        return opinionClienteRepo.getOpinionesAgrupadasPorHora();
    }
}