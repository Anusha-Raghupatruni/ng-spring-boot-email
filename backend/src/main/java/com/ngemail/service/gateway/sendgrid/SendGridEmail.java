package com.ngemail.service.gateway.sendgrid;

import java.util.List;

public class SendGridEmail {
    private List<Personalizations> personalizations;
    private Email from;
    private List<Content> content;

    public static class Email {
        String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class Personalizations {
        private String subject;
        private List<Email> to;
        private List<Email> cc;
        private List<Email> bcc;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public List<Email> getTo() {
            return to;
        }

        public void setTo(List<Email> to) {
            this.to = to;
        }

        public List<Email> getCc() {
            return cc;
        }

        public void setCc(List<Email> cc) {
            this.cc = cc;
        }

        public List<Email> getBcc() {
            return bcc;
        }

        public void setBcc(List<Email> bcc) {
            this.bcc = bcc;
        }
    }

    public static class Content {
        private String type;
        private String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public List<Personalizations> getPersonalizations() {
        return personalizations;
    }

    public void setPersonalizations(List<Personalizations> personalizations) {
        this.personalizations = personalizations;
    }

    public Email getFrom() {
        return from;
    }

    public void setFrom(Email from) {
        this.from = from;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }
}
