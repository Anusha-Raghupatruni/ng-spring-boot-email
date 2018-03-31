package com.ngemail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngemail.configuration.AppConfigProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SendEmailController.class)
public class SendEmailControllerTest {
    private static final String SEND_EMAIL_URL = "/email/v" + AppConfigProperties.API.VERSION + "/send";
    private static final String REQ_ARGUMENTS_FAILED_MESSAGE = "Validation failed for request arguments";
    private static final String RES_FIELD_ERROR_PREFIX = "Field ";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private SendEmailController sendEmailController;

    @Test
    public void sendOK() throws Exception {
        final String testMessage = "Success!";
        final ResponseEntity<SendEmailResponse> response = new ResponseEntity<>(
                createTestEmailResponse(testMessage), HttpStatus.OK);

        given(sendEmailController.send(any(SendEmailRequest.class))).willReturn(response);

        final SendEmailRequest testSendEmailRequest = createTestSendEmailRequest("abc@test.com");

        mvc.perform(post(SEND_EMAIL_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSendEmailRequest)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data.message", is(testMessage)));
    }

    @Test
    public void sendInvalidSenderEmail() throws Exception {
        final ResponseEntity<SendEmailResponse> response = new ResponseEntity<>(
                createTestEmailResponse("Shouldn't work"), HttpStatus.OK);

        given(sendEmailController.send(any(SendEmailRequest.class))).willReturn(response);

        final SendEmailRequest testSendEmailRequest = createTestSendEmailRequest("abctest.com");

        mvc.perform(post(SEND_EMAIL_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSendEmailRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message", is(REQ_ARGUMENTS_FAILED_MESSAGE)))
                .andExpect(jsonPath("$.data.errors[0]", startsWith(RES_FIELD_ERROR_PREFIX + "sender.email:")));
    }

    @Test
    public void sendNoSender() throws Exception {
        final ResponseEntity<SendEmailResponse> response = new ResponseEntity<>(
                createTestEmailResponse("Shouldn't work"), HttpStatus.OK);

        given(sendEmailController.send(any(SendEmailRequest.class))).willReturn(response);

        final SendEmailRequest testSendEmailRequest = createTestSendEmailRequest(null);

        mvc.perform(post(SEND_EMAIL_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSendEmailRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message", is(REQ_ARGUMENTS_FAILED_MESSAGE)))
                .andExpect(jsonPath("$.data.errors[0]", startsWith(RES_FIELD_ERROR_PREFIX + "sender.email:")));
    }

    @Test
    public void sendInvalidRecipient() throws Exception {
        final ResponseEntity<SendEmailResponse> response = new ResponseEntity<>(
                createTestEmailResponse("Shouldn't work"), HttpStatus.OK);

        given(sendEmailController.send(any(SendEmailRequest.class))).willReturn(response);

        final SendEmailRequest testSendEmailRequest = createTestSendEmailRequest("abc@test.com");
        testSendEmailRequest.getRecipients().get(0).setEmail("deftest.com");

        mvc.perform(post(SEND_EMAIL_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSendEmailRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message", is(REQ_ARGUMENTS_FAILED_MESSAGE)))
                .andExpect(jsonPath("$.data.errors[0]", startsWith(RES_FIELD_ERROR_PREFIX + "recipients[0].email:")));
    }

    @Test
    public void sendNoRecipient() throws Exception {
        final ResponseEntity<SendEmailResponse> response = new ResponseEntity<>(
                createTestEmailResponse("Shouldn't work"), HttpStatus.OK);

        given(sendEmailController.send(any(SendEmailRequest.class))).willReturn(response);

        final SendEmailRequest testSendEmailRequest = createTestSendEmailRequest("abc@test.com");
        testSendEmailRequest.setRecipients(null);

        mvc.perform(post(SEND_EMAIL_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSendEmailRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message", is(REQ_ARGUMENTS_FAILED_MESSAGE)))
                .andExpect(jsonPath("$.data.errors[0]", startsWith(RES_FIELD_ERROR_PREFIX + "recipients:")));
    }

    @Test
    public void sendNullContent() throws Exception {
        final ResponseEntity<SendEmailResponse> response = new ResponseEntity<>(
                createTestEmailResponse("Shouldn't work"), HttpStatus.OK);

        given(sendEmailController.send(any(SendEmailRequest.class))).willReturn(response);

        final SendEmailRequest testSendEmailRequest = createTestSendEmailRequest("abc@test.com");
        testSendEmailRequest.setContent(null);

        mvc.perform(post(SEND_EMAIL_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSendEmailRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message", is(REQ_ARGUMENTS_FAILED_MESSAGE)))
                .andExpect(jsonPath("$.data.errors[0]", startsWith(RES_FIELD_ERROR_PREFIX + "content:")));
}

    @Test
    public void sendBlankContent() throws Exception {
        final ResponseEntity<SendEmailResponse> response = new ResponseEntity<>(
                createTestEmailResponse("Shouldn't work"), HttpStatus.OK);

        given(sendEmailController.send(any(SendEmailRequest.class))).willReturn(response);

        final SendEmailRequest testSendEmailRequest = createTestSendEmailRequest("abc@test.com");
        testSendEmailRequest.setContent(" ");

        mvc.perform(post(SEND_EMAIL_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSendEmailRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message", is(REQ_ARGUMENTS_FAILED_MESSAGE)))
                .andExpect(jsonPath("$.data.errors[0]", startsWith(RES_FIELD_ERROR_PREFIX + "content:")));
    }

    private SendEmailResponse createTestEmailResponse(String message) {
        final SendEmailResponse sendEmailResponse = new SendEmailResponse();
        final Map<String, Object> data = new HashMap<>(1);

        data.put("message", message);
        sendEmailResponse.setData(data);

        return sendEmailResponse;
    }

    private SendEmailRequest createTestSendEmailRequest(String senderEmail) {
        final SendEmailRequest sendEmailRequest = new SendEmailRequest();
        final SendEmailRequest.Person sender = new SendEmailRequest.Person();
        sender.setEmail(senderEmail);
        sendEmailRequest.setSender(sender);

        final List<SendEmailRequest.Person> recipients = new ArrayList<>(1);
        final SendEmailRequest.Person recipient = new SendEmailRequest.Person();
        recipient.setEmail("def@test.com");
        recipients.add(recipient);
        sendEmailRequest.setRecipients(recipients);

        sendEmailRequest.setContent("Blah blah blah!");

        return sendEmailRequest;
    }
}
