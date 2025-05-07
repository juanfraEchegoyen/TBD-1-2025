package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.views.ResumenPedidosCliente;
import com.app.DeliveryApp.models.views.ResumenDesempenoRepartidor;
import com.app.DeliveryApp.models.views.ResumenEmpresasPedidos;
import com.app.DeliveryApp.services.ResumenPedidosClienteService;
import com.app.DeliveryApp.services.ResumenDesempenoRepartidorService;
import com.app.DeliveryApp.services.ResumenEmpresasPedidosService;
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
    private EmpresasPedidosService empresasPedidosService;

    @GetMapping("/resumen-clientes")
    public List<ResumenPedidosCliente> getResumenClientes() {
        return pedidosClienteService.getResumen();
    }

    @GetMapping("/desempeno-repartidores")
    public List<DesempenoRepartidor> getResumenRepartidores() {
        return desempenoRepartidorService.getResumen();
    }

    @GetMapping("/empresas-mas-pedidos")
    public List<EmpresasPedidos> getResumenEmpresas() {
        return empresasPedidosService.getResumen();
    }
}
