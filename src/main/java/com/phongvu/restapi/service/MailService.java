package com.phongvu.restapi.service;

public interface MailService {
    void sendResetPasswordEmail(String toEmail, String token);
}
