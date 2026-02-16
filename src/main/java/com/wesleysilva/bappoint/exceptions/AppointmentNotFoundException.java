package com.wesleysilva.bappoint.exceptions;

import java.util.UUID;

public class AppointmentNotFound extends RuntimeException {
    public AppointmentNotFound() {super("Appointment not found.");}
}
