package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZonaCoberturaClienteDTO {
    private List<String> areaCoberturaWkt;      // WKT formato
    private String ubicacionClienteWkt;   // WKT formato
}