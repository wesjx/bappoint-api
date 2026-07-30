package com.wesleysilva.bappoint.exceptions;

public class AppointmentMustBeInTheFutureException extends RuntimeException{
    public AppointmentMustBeInTheFutureException() {super("Appointment start time must be in the future");}
}