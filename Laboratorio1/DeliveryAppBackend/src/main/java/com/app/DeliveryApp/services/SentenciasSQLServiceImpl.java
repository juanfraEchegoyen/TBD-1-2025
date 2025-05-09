package com.app.DeliveryApp.services;

import com.app.DeliveryApp.dto.*;
import com.app.DeliveryApp.repositories.SentenciasSQLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
