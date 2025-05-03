package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.sentenciasSQL.*;
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
    public ClienteGasto getClienteConMayorGastos() {
        if (sentenciasSQLRepository.getClienteConMayorGastos().getNombre_cliente().isEmpty()){
            throw new IllegalArgumentException("No hay clientes registrados");
        }
        return sentenciasSQLRepository.getClienteConMayorGastos();
    }

    @Override
    public List<ProductoMasVendido> getProductosMasVendidosUltimoMes() {
        if (sentenciasSQLRepository.getProductosMasVendidosUltimoMes().isEmpty()){
            throw new IllegalArgumentException("No hay productos vendidos en el último mes");
        }
        return sentenciasSQLRepository.getProductosMasVendidosUltimoMes();
    }

    @Override
    public List<EmpresaEntregasFallidas> getEmpresasEntregasFallidas() {
        if (sentenciasSQLRepository.getEmpresasEntregasFallidas().isEmpty()){
            throw new IllegalArgumentException("No hay entregas fallidas registradas");
        }
        return sentenciasSQLRepository.getEmpresasEntregasFallidas();
    }
}
