package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.sentenciasSQL.*;

import java.util.List;

public interface SentenciasSQLService {
    ClienteGasto getClienteConMayorGastos();
    List<ProductoMasVendido> getProductosMasVendidosUltimoMes();
    List<EmpresaEntregasFallidas> getEmpresasEntregasFallidas();
    List<RepartidorTiempoPromedio> getTiempoPromedioRepartidor();
    List<RepartidorMejorRendimiento> getRepartidoresMejorRendimiento();
    String getMetodoPagoFrecuente();

}
