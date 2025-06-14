package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.dto.*;
import com.app.DeliveryApp.models.ZonaCobertura;
import com.app.DeliveryApp.repositories.SentenciasSQLRepository;
import com.app.DeliveryApp.services.SentenciasSQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sentenciassql")
@CrossOrigin(origins="http://localhost:3000")
public class SentenciasSQLController {

    @Autowired
    private SentenciasSQLService sentenciasSQLService;
    private SentenciasSQLRepository SentenciasSQLRepository;

    @GetMapping("/clienteMayorGastos")
    public ResponseEntity<ClienteGastoDTO> getClienteConMayorGastos() {
        try {
            ClienteGastoDTO clienteGasto = sentenciasSQLService.getClienteConMayorGastos();
            return ResponseEntity.ok(clienteGasto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/productosMasVendidos")
    public ResponseEntity<List<ProductoMasVendidoDTO>> getProductosMasVendidosUltimoMes() {
        try {
            List<ProductoMasVendidoDTO> productos = sentenciasSQLService.getProductosMasVendidosUltimoMes();
            return ResponseEntity.ok(productos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/empresasEntregasFallidas")
    public ResponseEntity<List<EmpresaEntregasFallidasDTO>> getEmpresasEntregasFallidas() {
        try {
            List<EmpresaEntregasFallidasDTO> empresas = sentenciasSQLService.getEmpresasEntregasFallidas();
            return ResponseEntity.ok(empresas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/tiempoPromedioRepartidor")
    public ResponseEntity<List<RepartidorTiempoPromedioDTO>> getTiempoPromedioRepartidor() {
        try {
            List<RepartidorTiempoPromedioDTO> tiempoPromedio = sentenciasSQLService.getTiempoPromedioRepartidor();
            return ResponseEntity.ok(tiempoPromedio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/repartidoresMejorRendimiento")
    public ResponseEntity<List<RepartidorMejorRendimientoDTO>> getRepartidoresMejorRendimiento() {
        try {
            List<RepartidorMejorRendimientoDTO> repartidores = sentenciasSQLService.getRepartidoresMejorRendimiento();
            return ResponseEntity.ok(repartidores);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/metodoPagoFrecuente")
    public ResponseEntity<MetodoPagoFrecuenteDTO> getMetodoPagoFrecuente() {
        try {
            MetodoPagoFrecuenteDTO metodoPago = sentenciasSQLService.getMetodoPagoFrecuente();
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
    //Lab 2

    //Endpoint query 1
    @GetMapping("/entregasCercanas/{rutEmpresa}")
    public ResponseEntity<?> obtenerEntregasCercanas(@PathVariable String rutEmpresa) {
        try {
            List<EntregaDTO> entregas = sentenciasSQLService.obtenerEntregasCercanas(rutEmpresa);
            if (entregas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron entregas cercanas");
            }
            return ResponseEntity.ok(entregas);
        } catch (Exception e) {
            e.printStackTrace(); // Imprime detalles en la consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }


    //Endpoint query 2
    @GetMapping("/zonasCoberturaYUbicacionPorCliente/{rutCliente}")
    public ResponseEntity<List<ZonaCoberturaClienteDTO>> getZonasCoberturaYUbicacionPorCliente(@PathVariable String rutCliente) {
        try {
            List<ZonaCoberturaClienteDTO> zonas = sentenciasSQLService.getZonasCoberturaYUbicacionPorCliente(rutCliente);
            return ResponseEntity.ok(zonas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //Endpoint query 3
    @GetMapping("/distanciaRecorrida/{rutRepartidor}")
    public ResponseEntity<DistanciaDTO> calcularDistanciaRepartidor(@PathVariable String rutRepartidor) {
        try {
            DistanciaDTO distancia = sentenciasSQLService.calcularDistanciaRepartidor(rutRepartidor);
            if (distancia != null) {
                return ResponseEntity.ok(distancia);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //Endpoint query 5
    @GetMapping("/pedidosQueCruzaronZonas")
    public ResponseEntity<List<PedidoZonasDTO>> obtenerPedidosQueCruzaronZonas() {
        try {
            List<PedidoZonasDTO> pedidos = sentenciasSQLService.obtenerPedidosQueCruzaronZonas();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //Endpoint query 6
    @GetMapping("/clientesLejanos")
    public ResponseEntity<List<ClienteLejanoDTO>> getClientesLejanos() {
        try {
            List<ClienteLejanoDTO> clientes = sentenciasSQLService.getClientesAMasDe5KmDeEmpresa();
            return ResponseEntity.ok(clientes); // Devuelve 200 OK, aunque la lista esté vacía
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Special Sentence 1
    @GetMapping("/zonaPerteneceCliente/{rutCliente}")
    public ResponseEntity<String> getZonaPerteneceCliente(@PathVariable String rutCliente) {
        try {
            String zona = sentenciasSQLService.getZonaPerteneceCliente(rutCliente);
            if (zona != null) {
                return ResponseEntity.ok(zona);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró zona para el cliente");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }
}
