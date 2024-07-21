package com.bashkir777.jwtauthservice.auth.services;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.NtpTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import dev.samstevens.totp.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;

@Service
public class TwoFactorAuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(TwoFactorAuthenticationService.class);
    private final TimeProvider timeProvider = new NtpTimeProvider("pool.ntp.org");
    @Value(value = "${spring.application.name}")
    private String appName;

    public TwoFactorAuthenticationService() throws UnknownHostException {
    }

    public String generateNewSecret() {
        return new DefaultSecretGenerator().generate();
    }

    public String formatQRCodeImageURL(String secret, String username) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", appName, username, secret, appName);
    }

    public boolean isOTPValid(String secret, String code) throws UnknownHostException {
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return codeVerifier.isValidCode(secret, code);
    }

}
