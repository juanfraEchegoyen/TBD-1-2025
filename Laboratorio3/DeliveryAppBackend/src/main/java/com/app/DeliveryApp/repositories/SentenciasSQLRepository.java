package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.dto.*;


import java.util.List;

public interface SentenciasSQLRepository {
    ClienteGastoDTO getClienteConMayorGastos();
    List<ProductoMasVendidoDTO> getProductosMasVendidosUltimoMes();
    List<EmpresaEntregasFallidasDTO> getEmpresasEntregasFallidas();
    List<RepartidorTiempoPromedioDTO> getTiempoPromedioRepartidor();
    List<RepartidorMejorRendimientoDTO> getRepartidoresMejorRendimiento();
    MetodoPagoFrecuenteDTO getMetodoPagoFrecuente();
    List<RankingBonusDTO> getRankingDevolucionesOCancelaciones();
    List<ZonaCoberturaClienteDTO> getZonasCoberturaYUbicacionPorCliente(String rutCliente);
    List<ClienteLejanoDTO> getClientesAMasDe5KmDeEmpresa();
    List<EntregaDTO> obtenerEntregasCercanas(String rutEmpresa);
    DistanciaDTO calcularDistanciaRepartidor(String rutRepartidor);
    List<PedidoZonasDTO> obtenerPedidosQueCruzaronZonas();
    String getZonaPerteneceCliente(String rutCliente);
    List<EntregaLejanaDTO> obtenerEntregasMasLejanasPorEmpresa();
}
