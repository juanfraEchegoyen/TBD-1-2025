package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.services.SentenciasNOSQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sentenciasnosql")
@CrossOrigin(origins="http://localhost:3000")
public class SentenciasNOSQLController {

    @Autowired
    private SentenciasNOSQLService sentenciasNOSQLService;

    @GetMapping("/promedio-por-empresa")
    public List<Map> getPromedioPuntuacionPorEmpresa() {
        return sentenciasNOSQLService.getPromedioPuntuacionPorEmpresa();
    }
}
