package com.app.DeliveryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteLejanoDTO {
    private String rutCliente;
    private String nombreCliente;
    private String ubicacionClienteWkt;
}