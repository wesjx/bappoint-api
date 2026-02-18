package com.wesleysilva.bappoint.exceptions;

public class OffDayNotFoundException extends RuntimeException{
    public OffDayNotFoundException() {
        super("OffDay not found.");
    }
}
