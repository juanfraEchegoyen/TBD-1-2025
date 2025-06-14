package com.app.DeliveryApp.services;

import com.app.DeliveryApp.dto.*;
import com.app.DeliveryApp.repositories.SentenciasSQLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.DeliveryApp.models.ZonaCobertura;

import java.util.List;

@Service
public class SentenciasSQLServiceImpl implements SentenciasSQLService {

    @Autowired
    private final SentenciasSQLRepository sentenciasSQLRepository;

    public SentenciasSQLServiceImpl(SentenciasSQLRepository sentenciasSQLRepository) {
        this.sentenciasSQLRepository = sentenciasSQLRepository;
    }

    @Override
    public ClienteGastoDTO getClienteConMayorGastos() {
        if (sentenciasSQLRepository.getClienteConMayorGastos().getNombreCliente().isEmpty()){
            throw new IllegalArgumentException("No hay clientes registrados");
        }
        return sentenciasSQLRepository.getClienteConMayorGastos();
    }

    @Override
    public List<ProductoMasVendidoDTO> getProductosMasVendidosUltimoMes() {
        if (sentenciasSQLRepository.getProductosMasVendidosUltimoMes().isEmpty()){
            throw new IllegalArgumentException("No hay productos vendidos en el último mes");
        }
        return sentenciasSQLRepository.getProductosMasVendidosUltimoMes();
    }

    @Override
    public List<EmpresaEntregasFallidasDTO> getEmpresasEntregasFallidas() {
        if (sentenciasSQLRepository.getEmpresasEntregasFallidas().isEmpty()){
            throw new IllegalArgumentException("No hay entregas fallidas registradas");
        }
        return sentenciasSQLRepository.getEmpresasEntregasFallidas();
    }

    @Override
    public List<RepartidorTiempoPromedioDTO> getTiempoPromedioRepartidor() {
        if (sentenciasSQLRepository.getTiempoPromedioRepartidor() == null){
            throw new IllegalArgumentException("No hay repartidores registrados");
        }
        return sentenciasSQLRepository.getTiempoPromedioRepartidor();
    }

    @Override
    public List<RepartidorMejorRendimientoDTO> getRepartidoresMejorRendimiento() {
        if (sentenciasSQLRepository.getRepartidoresMejorRendimiento().isEmpty()){
            throw new IllegalArgumentException("No hay repartidores registrados");
        }
        return sentenciasSQLRepository.getRepartidoresMejorRendimiento();
    }

    @Override
    public MetodoPagoFrecuenteDTO getMetodoPagoFrecuente() {
        if (sentenciasSQLRepository.getMetodoPagoFrecuente() == null){
            throw new IllegalArgumentException("No hay métodos de pago registrados");
        }
        return sentenciasSQLRepository.getMetodoPagoFrecuente();
    }

    @Override
    public List<RankingBonusDTO> getRankingDevolucionesOCancelaciones() {
        if (sentenciasSQLRepository.getRankingDevolucionesOCancelaciones().isEmpty()){
            throw new IllegalArgumentException("No hay devoluciones o cancelaciones registradas");
        }
        return sentenciasSQLRepository.getRankingDevolucionesOCancelaciones();
    }
    // ------------------------Lab 2 ------------------------//
    //1
    @Override
    public List<EntregaDTO> obtenerEntregasCercanas(String rutEmpresa) {
        List<EntregaDTO> pedidos = sentenciasSQLRepository.obtenerEntregasCercanas(rutEmpresa);
        if (pedidos.isEmpty()) {
            throw new IllegalArgumentException("No hay enntregas cercanas.");
        }
        return pedidos;
    }


    // Este metodo obtiene las zonas de cobertura por cliente
    @Override
    public List<ZonaCoberturaClienteDTO> getZonasCoberturaYUbicacionPorCliente(String rutCliente) {
        List<ZonaCoberturaClienteDTO> zonas = sentenciasSQLRepository.getZonasCoberturaYUbicacionPorCliente(rutCliente);
        if (zonas.isEmpty()) {
            throw new IllegalArgumentException("El cliente no está ubicado en ninguna zona de cobertura registrada");
        }
        return zonas;
    }


    // 3
    @Override
    public DistanciaDTO calcularDistanciaRepartidor(String rutRepartidor){
        DistanciaDTO distancia = sentenciasSQLRepository.calcularDistanciaRepartidor(rutRepartidor);
        if (distancia == null) {
            throw new IllegalArgumentException("NO hay distancia registrada");
        }
        return distancia;
    }

    // 5
    @Override
    public List<PedidoZonasDTO> obtenerPedidosQueCruzaronZonas(){
        List<PedidoZonasDTO> pedidoZonasDTOS = sentenciasSQLRepository.obtenerPedidosQueCruzaronZonas();
        if (pedidoZonasDTOS.isEmpty()) {
            throw new IllegalArgumentException("No hay pedidos que hayan cruzado más de 2 zonas de reparto.");
        }
        return pedidoZonasDTOS;
    }

    // Este metodo obtiene los clientes que se encuentran a más de 5km de cualquier empresa
    @Override
    public List<ClienteLejanoDTO> getClientesAMasDe5KmDeEmpresa() {
        return sentenciasSQLRepository.getClientesAMasDe5KmDeEmpresa();
    }
}
