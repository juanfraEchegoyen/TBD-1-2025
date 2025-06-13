package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZonaCoberturaClienteDTO {
    private String areaCoberturaWkt;      // WKT formato
    private String ubicacionClienteWkt;   // WKT formato
}