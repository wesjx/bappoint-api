package com.wesleysilva.bappoint.Services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDTO {
    private UUID id;
    private String name;
    private Integer price;
    private Integer duration_minutes;
    private Boolean is_active;
}
