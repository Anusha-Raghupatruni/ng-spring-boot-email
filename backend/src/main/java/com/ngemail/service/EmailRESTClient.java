package com.ngemail.service;

import com.ngemail.domain.Email;

import java.io.IOException;

public interface EmailRESTClient {
    EmailRESTClientResponse send(Email email) throws IOException;
}
