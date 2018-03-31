package com.ngemail.service;

import com.ngemail.domain.Email;

import java.io.IOException;

public interface SendEmailService {
    EmailRESTClientResponse sendEmail(Email email) throws IOException;
}
