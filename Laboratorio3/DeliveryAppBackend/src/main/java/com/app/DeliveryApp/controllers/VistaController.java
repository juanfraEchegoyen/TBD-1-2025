package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.views.ResumenPedidosCliente;
import com.app.DeliveryApp.models.views.DesempenoRepartidor;
import com.app.DeliveryApp.models.views.EmpresaPedidos;
import com.app.DeliveryApp.services.ResumenPedidosClienteService;
import com.app.DeliveryApp.services.DesempenoRepartidorService;
import com.app.DeliveryApp.services.EmpresaPedidosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vistas")
@CrossOrigin(origins = "http://localhost:3000")
public class VistaController {

    @Autowired
    private ResumenPedidosClienteService pedidosClienteService;

    @Autowired
    private DesempenoRepartidorService desempenoRepartidorService;

    @Autowired
    private EmpresaPedidosService empresasPedidosService;

    @GetMapping("/resumen-clientes")
    public List<ResumenPedidosCliente> getResumenClientes() {
        return pedidosClienteService.getResumen();
    }

    @GetMapping("/desempeno-repartidores")
    public List<DesempenoRepartidor> getResumenRepartidores() {
        return desempenoRepartidorService.getResumen();
    }

    @GetMapping("/empresas-mas-pedidos")
    public List<EmpresaPedidos> getResumenEmpresas() {
        return empresasPedidosService.getResumen();
    }
}
