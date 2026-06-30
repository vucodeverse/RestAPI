package com.phongvu.restapi.service.impl;

import com.phongvu.restapi.service.TwoFactorService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorServiceImpl implements TwoFactorService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    @Override
    public String generateNewSecret() {
        return gAuth.createCredentials().getKey();
    }

    @Override
    public String generateQrCodeImageUri(String secret, String username) {
        GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder(secret).build();
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("RestAPI", username, key);
    }

    @Override
    public boolean isOtpValid(String secret, String code) {
        try {
            return gAuth.authorize(secret, Integer.parseInt(code));
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
