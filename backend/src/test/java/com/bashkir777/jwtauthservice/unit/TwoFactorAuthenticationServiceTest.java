package com.bashkir777.jwtauthservice.unit;

import com.bashkir777.jwtauthservice.auth.services.TwoFactorAuthenticationService;
import static org.assertj.core.api.Assertions.*;

import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.time.TimeProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for TwoFactorAuthenticationService")
public class TwoFactorAuthenticationServiceTest {
    private final long TIME = System.currentTimeMillis();
    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private TwoFactorAuthenticationService tfaAuthService;

    @BeforeEach
    public void setUpMocks(){
        doReturn(TIME).when(timeProvider).getTime();
    }

    @Test
    @DisplayName("TwoFactorAuthenticationService successfully generates secret key and correctly verifies OTP")
    public void twoFactorAuthenticationService_successfullyGeneratesSecretKeyAndCorrectlyVerifiesOTP() throws CodeGenerationException {
        String secret = tfaAuthService.generateNewSecret();
        String otpForSecret = tfaAuthService.generateCurrentOtpForSecretKey(secret);
        assertThat(tfaAuthService.isOTPValid(secret, otpForSecret)).isEqualTo(true);
        verify(timeProvider, times(2)).getTime();
        verifyNoMoreInteractions(timeProvider);
    }
}
