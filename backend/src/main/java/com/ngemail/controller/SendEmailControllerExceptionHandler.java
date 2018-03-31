package com.ngemail.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice(basePackageClasses = { SendEmailController.class })
public class SendEmailControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        final List<String> errors = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add("Field " + error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : e.getBindingResult().getGlobalErrors()) {
            errors.add("Object " + error.getObjectName() + ": " + error.getDefaultMessage());
        }

        final SendEmailResponse response = new SendEmailResponse();
        Map<String, Object> data = new HashMap<>(2);
        data.put("message", "Validation failed for request arguments");
        data.put("errors", errors);
        response.setData(data);

        return handleExceptionInternal(e, response, headers, HttpStatus.BAD_REQUEST, request);
    }

    // TODO default exception handler
}
