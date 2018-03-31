package com.ngemail.service.gateway.sendgrid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngemail.domain.Email;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SendGridEmailBodyTest {
    private SendGridEmailRESTClient sendGridEmailRESTClient;

    @Before
    public void setUp() {
        sendGridEmailRESTClient = new SendGridEmailRESTClient(null);
    }

    @Test
    public void testSingleRecipientEmail() throws JsonProcessingException {
        final Email testEmail = new Email();
        testEmail.setSender("abc@test.com");
        List<String> toEmails = new ArrayList<>(1);
        toEmails.add("def@test.com");
        testEmail.setRecipients(toEmails);

        final String jsonString = sendGridEmailRESTClient.getEmailAsJSONString(testEmail);
        assertEquals("JSON string does not match",
                "{\"personalizations\":[{" + "\"subject\":null,"
                        + "\"to\":[{\"email\":\"def@test.com\"}],"
                        + "\"cc\":null,"
                        + "\"bcc\":null}],"
                        + "\"from\":{\"email\":\"abc@test.com\"},"
                        + "\"content\":[{\"type\":\"text/plain\",\"value\":null}]}",
                jsonString);
    }

    @Test
    public void testMultipleRecipientEmail() throws JsonProcessingException {
        final Email testEmail = new Email();
        testEmail.setSender("abc@test.com");
        final List<String> toEmails = new ArrayList<>(1);
        toEmails.add("def@test.com");
        toEmails.add("ghi@test.com");
        testEmail.setRecipients(toEmails);

        final String jsonString = sendGridEmailRESTClient.getEmailAsJSONString(testEmail);
        assertEquals("JSON string does not match",
                "{\"personalizations\":[{\"subject\":null,"
                        + "\"to\":[{\"email\":\"def@test.com\"},{\"email\":\"ghi@test.com\"}],"
                        + "\"cc\":null,"
                        + "\"bcc\":null}],"
                        + "\"from\":{\"email\":\"abc@test.com\"},"
                        + "\"content\":[{\"type\":\"text/plain\",\"value\":null}]}",
                jsonString);
    }
}
