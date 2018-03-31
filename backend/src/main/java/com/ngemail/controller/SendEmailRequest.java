package com.ngemail.controller;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

public class SendEmailRequest {
    @Valid
    private Person sender;

    @Valid
    @NotEmpty
    @Size(min = 1, max = 1000)
    private List<Person> recipients;

    @Valid
    @Size(max = 1000)
    private List<Person> ccs;

    @Valid
    @Size(max = 1000)
    private List<Person> bccs;

    // Max length as recommended in SendGrid API, which refers to:
    // https://stackoverflow.com/questions/1592291/what-is-the-email-subject-length-limit#answer-1592310
    @Size(max = 78)
    private String subject;

    @NotBlank
    private String content;

    public static class Person {
        @NotBlank
        @Email
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public Person getSender() {
        return sender;
    }

    public void setSender(Person sender) {
        this.sender = sender;
    }

    public List<Person> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Person> recipients) {
        this.recipients = recipients;
    }

    public List<Person> getCcs() {
        return ccs;
    }

    public void setCcs(List<Person> ccs) {
        this.ccs = ccs;
    }

    public List<Person> getBccs() {
        return bccs;
    }

    public void setBccs(List<Person> bccs) {
        this.bccs = bccs;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
