package com.ngemail.service.gateway.sendgrid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngemail.configuration.gateway.SendGridProperties;
import com.ngemail.service.EmailRESTClient;
import com.ngemail.service.EmailRESTClientResponse;
import com.ngemail.domain.Email;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class SendGridEmailRESTClient implements EmailRESTClient {
    private SendGridProperties properties;
    private OkHttpClient client;

    @Autowired
    public SendGridEmailRESTClient(SendGridProperties sendGridProperties) {
        this.properties = sendGridProperties;

        final OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);

        client = builder.build();
    }

    @Override
    public EmailRESTClientResponse send(Email email) throws IOException {
        final String jsonEmail = getEmailAsJSONString(email);

        final Request request = new Request.Builder()
                .addHeader("authorization", "Bearer " + properties.getApiKey())
                .addHeader("content-type", "application/json")
                .url(properties.getBaseUrl() + "/send")
                .post(RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        jsonEmail))
                .build();

        final Response response = client.newCall(request).execute();
        final EmailRESTClientResponse clientResponse = new EmailRESTClientResponse();

        // TODO should log the response body, as original message should give hints on the cause of failure
        ResponseBody responseBody = response.body();
        System.out.println(responseBody.toString());

        clientResponse.setCode(response.code());
        return clientResponse;
    }

    String getEmailAsJSONString(Email email) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final SendGridEmail sendGridEmail = new SendGridEmail();

        final List<SendGridEmail.Personalizations> personalizations = new ArrayList<>(1);
        sendGridEmail.setPersonalizations(personalizations);
        SendGridEmail.Personalizations personalization = new SendGridEmail.Personalizations();
        personalizations.add(personalization);

        // Special case for Subject, in Send Grid API it says the field is optional but still have
        // minimum length of 1!
        personalization.setSubject(email.getSubject() == null || email.getSubject().isEmpty() ?
                " " : email.getSubject());

        personalization.setTo(getPersonalizationEmails(email.getRecipients()));
        personalization.setCc(getPersonalizationEmails(email.getCcs()));
        personalization.setBcc(getPersonalizationEmails(email.getBccs()));

        final SendGridEmail.Email fromEmail = new SendGridEmail.Email();
        fromEmail.setEmail(email.getSender());
        sendGridEmail.setFrom(fromEmail);

        final SendGridEmail.Content content = new SendGridEmail.Content();
        content.setType("text/plain");
        content.setValue(email.getContent());
        List<SendGridEmail.Content> contents = new ArrayList<>(1);
        contents.add(content);
        sendGridEmail.setContent(contents);

        return mapper.writeValueAsString(sendGridEmail);
    }

    private List<SendGridEmail.Email> getPersonalizationEmails(List<String> emails) {
        if (emails == null || emails.isEmpty()) {
            return null;
        }

        final List<SendGridEmail.Email> sendGridEmails = new ArrayList<>(emails.size());

        for (String email: emails) {
            final SendGridEmail.Email sendGridEmail = new SendGridEmail.Email();
            sendGridEmail.setEmail(email);
            sendGridEmails.add(sendGridEmail);
        }

        return sendGridEmails;
    }
}
