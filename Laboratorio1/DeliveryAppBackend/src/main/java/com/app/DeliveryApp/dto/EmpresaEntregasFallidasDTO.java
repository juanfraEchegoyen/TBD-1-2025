package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaEntregasFallidasDTO {
    private String nombreEmpresa;
    private Integer totalEntregasFallidas;
}
