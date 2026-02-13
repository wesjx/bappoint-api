package com.wesleysilva.bappoint.exceptions;

public class CompanyDeleteException extends RuntimeException{
    public CompanyDeleteException() {super("Failed to delete company.");}
}
