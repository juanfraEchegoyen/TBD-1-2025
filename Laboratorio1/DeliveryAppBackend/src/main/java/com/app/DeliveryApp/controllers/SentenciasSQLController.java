package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.dto.RankingBonusDTO;
import com.app.DeliveryApp.models.sentenciasSQL.*;
import com.app.DeliveryApp.services.SentenciasSQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sentenciassql")
@CrossOrigin(origins="http://localhost:3000")
public class SentenciasSQLController {

    @Autowired
    private SentenciasSQLService sentenciasSQLService;

    @GetMapping("/clienteMayorGastos")
    public ResponseEntity<ClienteGasto> getClienteConMayorGastos() {
        try {
            ClienteGasto clienteGasto = sentenciasSQLService.getClienteConMayorGastos();
            return ResponseEntity.ok(clienteGasto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/productosMasVendidos")
    public ResponseEntity<List<ProductoMasVendido>> getProductosMasVendidosUltimoMes() {
        try {
            List<ProductoMasVendido> productos = sentenciasSQLService.getProductosMasVendidosUltimoMes();
            return ResponseEntity.ok(productos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/empresasEntregasFallidas")
    public ResponseEntity<List<EmpresaEntregasFallidas>> getEmpresasEntregasFallidas() {
        try {
            List<EmpresaEntregasFallidas> empresas = sentenciasSQLService.getEmpresasEntregasFallidas();
            return ResponseEntity.ok(empresas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/tiempoPromedioRepartidor")
    public ResponseEntity<List<RepartidorTiempoPromedio>> getTiempoPromedioRepartidor() {
        try {
            List<RepartidorTiempoPromedio> tiempoPromedio = sentenciasSQLService.getTiempoPromedioRepartidor();
            return ResponseEntity.ok(tiempoPromedio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/repartidoresMejorRendimiento")
    public ResponseEntity<List<RepartidorMejorRendimiento>> getRepartidoresMejorRendimiento() {
        try {
            List<RepartidorMejorRendimiento> repartidores = sentenciasSQLService.getRepartidoresMejorRendimiento();
            return ResponseEntity.ok(repartidores);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/metodoPagoFrecuente")
    public ResponseEntity<MetodoPagoFrecuente> getMetodoPagoFrecuente() {
        try {
            MetodoPagoFrecuente metodoPago = sentenciasSQLService.getMetodoPagoFrecuente();
            return ResponseEntity.ok(metodoPago);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/rankingDevolucionesOCancelaciones")
    public ResponseEntity<List<RankingBonusDTO>> getRankingDevolucionesOCancelaciones() {
        try {
            List<RankingBonusDTO> ranking = sentenciasSQLService.getRankingDevolucionesOCancelaciones();
            return ResponseEntity.ok(ranking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
