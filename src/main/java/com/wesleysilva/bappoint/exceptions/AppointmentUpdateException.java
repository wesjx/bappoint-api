package com.wesleysilva.bappoint.exceptions;

public class AppointmentUpdateException extends RuntimeException {
    public AppointmentUpdateException() {super("Failed to update appointment.");}
}
