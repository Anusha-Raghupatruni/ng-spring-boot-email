package com.ngemail.domain;

import java.util.List;

public class Email {
    private String sender;
    private List<String> recipients;
    private List<String> ccs;
    private List<String> bccs;
    private String subject;
    private String content;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public List<String> getCcs() {
        return ccs;
    }

    public void setCcs(List<String> ccs) {
        this.ccs = ccs;
    }

    public List<String> getBccs() {
        return bccs;
    }

    public void setBccs(List<String> bccs) {
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

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("sender: " + getSender() + ", ");
        builder.append("recipients: " + (getRecipients() == null ? "null" : getRecipients().toString()) + ", ");
        builder.append("ccs: " + (getCcs() == null ? "null" : getCcs().toString()) + ", ");
        builder.append("bcc: " + (getBccs() == null ? "null" : getBccs().toString()) + ", ");
        builder.append("subject: " + getSubject() + ", ");
        builder.append("content: " + getContent() + ", ");
        return builder.toString();
    }
}
