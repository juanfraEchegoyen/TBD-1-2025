package com.app.DeliveryApp.models.sentenciasSQL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaEntregasFallidas {
    private String nombreEmpresa;
    private Integer totalEntregasFallidas;
}
