package com.ngemail.service.gateway.mailgun;

import com.ngemail.configuration.gateway.MailGunProperties;
import com.ngemail.domain.Email;
import com.ngemail.service.EmailRESTClient;
import com.ngemail.service.EmailRESTClientResponse;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class MailGunEmailRESTClient implements EmailRESTClient {
    private MailGunProperties properties;
    private OkHttpClient client;

    @Autowired
    public MailGunEmailRESTClient(MailGunProperties mailGunProperties) {
        this.properties = mailGunProperties;

        final OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.authenticator((route, response) -> {
            final String credential = Credentials.basic("api", properties.getApiKey());
            return response.request().newBuilder().header("Authorization", credential).build();
        });

        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);

        client = builder.build();
    }

    @Override
    public EmailRESTClientResponse send(Email email) throws IOException {
        final Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url(properties.getBaseUrl() + "/messages")
                .post(map(email).build())
                .build();

        final Response response = client.newCall(request).execute();
        EmailRESTClientResponse clientResponse = new EmailRESTClientResponse();
        clientResponse.setCode(response.code());
        clientResponse.setMessage(response.body().string());
        return clientResponse;
    }

    MultipartBody.Builder map(Email email) {
        final MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("from", email.getSender())
                .addFormDataPart("to", getMultipleEmailsAsString(email.getRecipients()))
                .addFormDataPart("text", email.getContent());

        if (email.getSubject() != null) {
            bodyBuilder.addFormDataPart("subject", email.getSubject());
        }

        return bodyBuilder;
    }

    private String getMultipleEmailsAsString(List<String> emails) {
        final StringBuilder builder = new StringBuilder();
        for (int index = 0; index < emails.size(); index++) {
            if (index > 0) {
                builder.append(',');
            }

            builder.append(emails.get(index));
        }
        return builder.toString();
    }
}