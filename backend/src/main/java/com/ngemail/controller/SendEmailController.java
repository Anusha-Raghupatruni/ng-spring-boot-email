package com.ngemail.controller;

import com.ngemail.configuration.AppConfigProperties;
import com.ngemail.service.EmailRESTClientResponse;
import com.ngemail.service.SendEmailServiceImpl;
import com.ngemail.domain.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/email")
public class SendEmailController {
    private static final String ENDPOINT_VERSION_PREFIX = "/v";

    private SendEmailServiceImpl sendEmailService;

    @Autowired
    public SendEmailController(SendEmailServiceImpl sendEmailService) {
        this.sendEmailService = sendEmailService;
    }

    @GetMapping(value = ENDPOINT_VERSION_PREFIX + AppConfigProperties.API.VERSION + "/status")
    public ResponseEntity<SendEmailResponse> getStatus() {
        return createResponse(HttpStatus.OK, "Working!");
    }

    @PostMapping(value = ENDPOINT_VERSION_PREFIX + AppConfigProperties.API.VERSION + "/send")
    public ResponseEntity<SendEmailResponse> send(@RequestBody @Valid SendEmailRequest request) {
        Email email = createEmail(request);
        EmailRESTClientResponse response = sendEmailService.sendEmail(email);

        if (response == null) {
            return createResponse(HttpStatus.BAD_GATEWAY, "Unable to reach mail server");
        }

        final int code = response.getCode();
        if (code >= HttpStatus.OK.value() && code < HttpStatus.MULTIPLE_CHOICES.value()) {
            String message = response.getMessage();
            if (message == null || message.isEmpty()) {
                message = "Success";
            }
            return createResponse(HttpStatus.OK, message);
        } else if (HttpStatus.BAD_REQUEST.value() == code) {
            // TODO try to get the field that is causing the error and pass in message
            return createResponse(HttpStatus.BAD_REQUEST, "Content in email is invalid");
        } else if (HttpStatus.PAYLOAD_TOO_LARGE.value() == code) {
            return createResponse(HttpStatus.PAYLOAD_TOO_LARGE, "Size of email is too large for server to handle");
        } else {
            return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Attempts were made by server but encountered errors");
        }
    }

    private Email createEmail(SendEmailRequest request) {
        final Email email = new Email();
        email.setSender(request.getSender().getEmail());
        email.setRecipients(getEmails(request.getRecipients()));
        email.setCcs(getEmails(request.getCcs()));
        email.setBccs(getEmails(request.getBccs()));
        email.setSubject(request.getSubject());
        email.setContent(request.getContent());
        return email;
    }

    private List<String> getEmails(List<SendEmailRequest.Person> people) {
        final List<String> jsonEmails = new ArrayList<>(people.size());
        for (SendEmailRequest.Person person: people) {
            jsonEmails.add(person.getEmail());
        }
        return jsonEmails;
    }

    private ResponseEntity<SendEmailResponse> createResponse(HttpStatus status, String message) {
        final SendEmailResponse response = new SendEmailResponse();

        Map<String, Object> data = new HashMap<>(1);
        data.put("message", message);
        response.setData(data);

        return new ResponseEntity<>(response, status);
    }
}
