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

    public String generateQRCodeImageURI(String secret) {
        QrData qrData = new QrData.Builder()
                .label("Scan this code with Google Authenticator Mobile App").secret(secret)
                .issuer(appName).algorithm(HashingAlgorithm.SHA1).digits(6).period(30).build();
        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imgData = new byte[0];
        try {
            imgData = generator.generate(qrData);
        } catch (QrGenerationException e) {
            log.error("Error while generating QR Code");
        }
        return Utils.getDataUriForImage(imgData, generator.getImageMimeType());
    }

    public boolean isOTPValid(String secret, String code) throws UnknownHostException {
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return codeVerifier.isValidCode(secret, code);
    }

}
