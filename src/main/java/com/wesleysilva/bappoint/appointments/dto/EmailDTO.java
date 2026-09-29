package com.wesleysilva.bappoint.appointments.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailDTO {
    private UUID userId;
    private String emailFrom;
    private String emailTo;
    private String emailSubject;
    private String emailBody;
}
