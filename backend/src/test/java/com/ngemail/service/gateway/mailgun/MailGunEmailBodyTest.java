package com.ngemail.service.gateway.mailgun;

import com.ngemail.domain.Email;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okio.Buffer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MailGunEmailBodyTest {
    private MailGunEmailRESTClient mailGunEmailRESTClient;

    @Before
    public void setUp() {
        mailGunEmailRESTClient = new MailGunEmailRESTClient(null);
    }

    @Test
    public void testMapping() throws IOException {
        final Email testEmail = setupTestData();

        final MultipartBody.Builder builder = mailGunEmailRESTClient.map(testEmail);
        assertNotEquals("MultipartBody builder should not be null", builder);

        MultipartBody actualBody = builder.build();
        List<MultipartBody.Part> parts = actualBody.parts();
        MediaType mediaType = actualBody.contentType();
        assertNotNull("Content Type should not be null", mediaType);
        assertTrue("Content Type is different", mediaType.toString().contains(MultipartBody.FORM.toString()));
        assertEquals("Unexpected number of MultiBody parts", 4, parts.size());

        for (int index = 0; index < parts.size(); index++) {
            MultipartBody.Part part = parts.get(index);
            String namePart = part.headers().value(0);

            if (namePart.contains("name=\"from\"")) {
                Buffer buffer = new Buffer();
                part.body().writeTo(buffer);
                assertEquals("from field is not equal", testEmail.getSender(), buffer.readUtf8());
            }

            if (namePart.contains("name=\"to\"")) {
                Buffer buffer = new Buffer();
                part.body().writeTo(buffer);
                assertEquals("to field is not equal", getTestListString(testEmail.getRecipients()), buffer.readUtf8());
            }

            if (namePart.contains("name=\"cc\"")) {
                Buffer buffer = new Buffer();
                part.body().writeTo(buffer);
                assertEquals("cc field is not equal", getTestListString(testEmail.getCcs()), buffer.readUtf8());
            }

            if (namePart.contains("name=\"bcc\"")) {
                Buffer buffer = new Buffer();
                part.body().writeTo(buffer);
                assertEquals("bcc field is not equal", getTestListString(testEmail.getBccs()), buffer.readUtf8());
            }

            if (namePart.contains("name=\"subject\"")) {
                Buffer buffer = new Buffer();
                part.body().writeTo(buffer);
                assertEquals("subject field is not equal", testEmail.getSubject(), buffer.readUtf8());
            }
        }
    }

    private Email setupTestData() {
        final Email testEmail = new Email();
        testEmail.setSender("abc@test.com");
        testEmail.setRecipients(createEmailGroup("rec1@test.com", "rec2@test.com"));
        testEmail.setCcs(createEmailGroup("cc1@test.com"));
        testEmail.setBccs(createEmailGroup(null));
        testEmail.setSubject("Test Subject");
        testEmail.setContent("Hello World");

        return testEmail;
    }

    private List<String> createEmailGroup(String... testEmails) {
        if (testEmails == null) {
            return null;
        }

        final List<String> testEmailGroup = new ArrayList<>(testEmails.length);
        for (String testEmail : testEmails) {
            testEmailGroup.add(testEmail);
        }
        return testEmailGroup;
    }

    private String getTestListString(List<String> emailList) {
        final String emails = emailList.toString();
        return emails.substring(1, emails.length() - 1).replaceAll(" ", "");
    }
}
