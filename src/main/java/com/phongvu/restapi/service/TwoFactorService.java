package com.phongvu.restapi.service;

public interface TwoFactorService {
    String generateNewSecret();
    String generateQrCodeImageUri(String secret, String username);
    boolean isOtpValid(String secret, String code);
}
