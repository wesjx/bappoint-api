package com.wesleysilva.bappoint.appointments.mensaging;

import com.wesleysilva.bappoint.appointments.AppointmentModel;
import com.wesleysilva.bappoint.appointments.dto.AppointmentConfirmedEventDto;
import com.wesleysilva.bappoint.appointments.dto.EmailDTO;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class AppointmentProducer {

    private final String routingKey = "emailQueue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public AppointmentProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Bean
    public Queue appointmentQueue() {
        return QueueBuilder
                .durable(routingKey)
                .build();
    }

    public void publishEvent(AppointmentModel appointment){
        LocalDate date = appointment.getAppointmentDate();
        LocalDateTime time = appointment.getStartTime();

        String companyName = appointment.getCompany().getName();

        String dateFormatted = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String timeFormatted = time.format(DateTimeFormatter.ofPattern("HH:mm"));

        var emailDto = new EmailDTO();
        emailDto.setUserId(appointment.getId());
        emailDto.setEmailFrom("wesjrpiri@gmail.com");
        emailDto.setEmailTo(appointment.getCostumerEmail());
        emailDto.setEmailSubject("Confirmation Appointment");
        emailDto.setEmailBody("Appointment confirmed with: " + companyName + "at: " + timeFormatted + " " + dateFormatted);

        rabbitTemplate.convertAndSend("", routingKey, emailDto);
    }


}
