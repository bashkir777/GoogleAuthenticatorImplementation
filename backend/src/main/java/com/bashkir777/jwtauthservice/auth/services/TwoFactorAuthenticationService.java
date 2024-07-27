package com.bashkir777.jwtauthservice.auth.services;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorAuthenticationService {
    private final TimeProvider timeProvider;
    private final CodeGenerator codeGenerator;
    @Value(value = "${spring.application.name}")
    private String appName;

    @Autowired
    public TwoFactorAuthenticationService(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
        this.codeGenerator = new DefaultCodeGenerator();
    }

    public String generateNewSecret() {
        return new DefaultSecretGenerator().generate();
    }

    public String formatQRCodeImageURL(String secret, String username) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", appName, username, secret, appName);
    }

    public boolean isOTPValid(String secret, String code) {
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return codeVerifier.isValidCode(secret, code);
    }

    public String generateCurrentOtpForSecretKey(String secretKey) throws CodeGenerationException {
        return codeGenerator.generate(secretKey, timeProvider.getTime() / 30);
    }

}
