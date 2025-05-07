package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.sentenciasSQL.*;

import java.util.List;

public interface SentenciasSQLRepository {
    ClienteGasto getClienteConMayorGastos();
    List<ProductoMasVendido> getProductosMasVendidosUltimoMes();
    List<EmpresaEntregasFallidas> getEmpresasEntregasFallidas();
    List<RepartidorTiempoPromedio> getTiempoPromedioRepartidor();
    List<RepartidorMejorRendimiento> getRepartidoresMejorRendimiento();
    MetodoPagoFrecuente getMetodoPagoFrecuente();
}
