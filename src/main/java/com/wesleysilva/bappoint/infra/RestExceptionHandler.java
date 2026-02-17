package com.wesleysilva.bappoint.infra;

import com.wesleysilva.bappoint.exceptions.*;
import com.wesleysilva.bappoint.exceptions.AppointmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    private ResponseEntity<RestErrorMessage> emailAlreadyExistsResponse(EmailAlreadyExistsException exception) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.CONFLICT, "Email already exists.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(threatResponse);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    private ResponseEntity<RestErrorMessage> companyNotFoundResponse(CompanyNotFoundException exception) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, "Company not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    @ExceptionHandler(CompanyDeleteException.class)
    private ResponseEntity<RestErrorMessage> companyDeleteResponse(CompanyDeleteException exception) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.CONFLICT, "Failed to delete company.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(threatResponse);
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    private ResponseEntity<RestErrorMessage> appointmentNotFound(AppointmentNotFoundException exception) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, "Appointment not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    @ExceptionHandler(AppointmentDeleteException.class)
    private ResponseEntity<RestErrorMessage> appointmentDeleteException(AppointmentDeleteException exception) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.CONFLICT, "Error deleting appointment.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(threatResponse);
    }

    @ExceptionHandler(AppointmentQueryException.class)
    private ResponseEntity<RestErrorMessage> appointmentQueryException(AppointmentQueryException exception) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.CONFLICT, "Failed to list appointments.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(threatResponse);
    }

    @ExceptionHandler(ServiceNotFoundException.class)
    private ResponseEntity<RestErrorMessage> serviceNotFoundException(ServiceNotFoundException exception) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.NOT_FOUND, "Services not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatResponse);
    }

    @ExceptionHandler(AppointmentUpdateException.class)
    private ResponseEntity<RestErrorMessage> appointmentUpdateException(AppointmentUpdateException exception) {
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.CONFLICT, "Failed to update appointment.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(threatResponse);
    }
}
