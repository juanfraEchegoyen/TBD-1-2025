package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.models.views.ResumenPedidosCliente;
import com.app.DeliveryApp.services.ResumenPedidosClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vistas")
@CrossOrigin(origins="http://localhost:3000")
public class ResumenPedidosClienteController {
    @Autowired
    private ResumenPedidosClienteService service;

    @GetMapping("/resumen-clientes")
    public List<ResumenPedidosCliente> getResumenClientes() {
        return service.getResumen();
    }
}