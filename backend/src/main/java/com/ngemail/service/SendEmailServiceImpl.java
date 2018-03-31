package com.ngemail.service;

import com.ngemail.domain.Email;
import com.ngemail.service.gateway.mailgun.MailGunEmailRESTClient;
import com.ngemail.service.gateway.sendgrid.SendGridEmailRESTClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SendEmailServiceImpl implements SendEmailService {
    private List<EmailRESTClient> emailClients;

    @Autowired
    public SendEmailServiceImpl(
            MailGunEmailRESTClient mailGunEmailClient,
            SendGridEmailRESTClient sendGridEmailClient) {
        emailClients = new ArrayList<>(2);
        emailClients.add(sendGridEmailClient);
//        emailClients.add(mailGunEmailClient);
    }

    public EmailRESTClientResponse sendEmail(Email email) {
        EmailRESTClientResponse response = null;

        for (EmailRESTClient emailClient : emailClients) {
            response = send(emailClient, email);

            // Something is not right. Should log error message and log the erroneous client
            if (response == null) {
                // TODO logging
                continue;
            }

            // If it is between Status 200 and 300 then no need to retry
            final int code = response.getCode();
            if (code >= HttpStatus.OK.value() && code < HttpStatus.MULTIPLE_CHOICES.value()) {
                break;

            // If status is 400, no need to retry as it is client request params errors
            } else if (code == HttpStatus.BAD_REQUEST.value() || code == HttpStatus.PAYLOAD_TOO_LARGE.value()) {
                break;
            } else if (code == HttpStatus.UNAUTHORIZED.value()
                    || code == HttpStatus.FORBIDDEN.value()
                    || code == HttpStatus.NOT_FOUND.value()) {
                // TODO logging (to record the REST client that is getting the issue) and
                // possibly stop using the REST client temporarily, due to the nature of the error
                break;
            }
        }

        // Exhausted clients, should log the failed email before returning
        if (response == null) {
            // TODO logging
            return null;
        }

        return response;
    }

    private EmailRESTClientResponse send(EmailRESTClient emailClient, Email email) {
        try {
            return emailClient.send(email);
        } catch (IOException e) {
            // TODO logging
            e.printStackTrace();
        }

        return null;
    }
}
