package com.app.DeliveryApp.dto;

import com.app.DeliveryApp.models.DetallePedido;
import java.util.List;

public class PedidoRequestDTO {
    private String estadoEntrega;
    private String prioridadPedido;
    private boolean problemaCritico;
    private String rutCliente;
    private String rutEmpresa;
    private String rutRepartidor;

    private List<DetallePedido> detalles;

    public String getEstadoEntrega() { return estadoEntrega; }
    public void setEstadoEntrega(String estadoEntrega) { this.estadoEntrega = estadoEntrega; }
    public String getPrioridadPedido() { return prioridadPedido; }
    public void setPrioridadPedido(String prioridadPedido) { this.prioridadPedido = prioridadPedido; }
    public boolean isProblemaCritico() { return problemaCritico; }
    public void setProblemaCritico(boolean problemaCritico) { this.problemaCritico = problemaCritico; }
    public String getRutCliente() { return rutCliente; }
    public void setRutCliente(String rutCliente) { this.rutCliente = rutCliente; }
    public String getRutEmpresa() { return rutEmpresa; }
    public void setRutEmpresa(String rutEmpresa) { this.rutEmpresa = rutEmpresa; }
    public String getRutRepartidor() { return rutRepartidor; }
    public void setRutRepartidor(String rutRepartidor) { this.rutRepartidor = rutRepartidor; }
    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }

    public com.app.DeliveryApp.models.Pedido toPedidoModel() {
        com.app.DeliveryApp.models.Pedido pedido = new com.app.DeliveryApp.models.Pedido();
        pedido.setEstadoEntrega(this.estadoEntrega);
        pedido.setPrioridadPedido(this.prioridadPedido);
        pedido.setProblemaCritico(this.problemaCritico);
        pedido.setRutCliente(this.rutCliente);
        pedido.setRutEmpresa(this.rutEmpresa);
        pedido.setRutRepartidor(this.rutRepartidor);
        return pedido;
    }
}