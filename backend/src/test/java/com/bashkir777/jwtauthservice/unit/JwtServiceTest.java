package com.bashkir777.jwtauthservice.unit;

import com.bashkir777.jwtauthservice.app.data.enums.TokenType;
import com.bashkir777.jwtauthservice.auth.services.JwtService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;


@DisplayName("Module tests for class JwtService")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = JwtService.class)
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "JWTAuthService.secret-key=7dec524d50d2b1ec883f422e015659b1e57e42305e84c3cabb6d70911de2bc24")
class JwtServiceTest {
    @Autowired
    private JwtService jwtService;
    private final String MOCK_USERNAME = "username";

    private UserDetails userDetails;

    @BeforeEach
    void setUpMock() {
        userDetails = mock(UserDetails.class);
        doReturn(MOCK_USERNAME).when(userDetails).getUsername();
    }

    @Test
    @DisplayName("JwtService correctly encodes and decodes username in JWT")
    public void JwtService_CorrectlyEncodesAndDecodesUsernameInJWT() {
        String token = jwtService.generateToken(userDetails, TokenType.ACCESS, new HashMap<>());
        String extractedUsername = jwtService.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(MOCK_USERNAME);
        verify(userDetails).getUsername();
        verifyNoMoreInteractions(userDetails);
    }

    @Test
    @DisplayName("JwtService correctly encodes and decodes JWT Types")
    public void JwtService_CorrectlyEncodesAndDecodesTokenTypeInJWT() {
        String accessToken = jwtService.generateToken(userDetails, TokenType.ACCESS, new HashMap<>());
        String refreshToken = jwtService.generateToken(userDetails, TokenType.REFRESH, new HashMap<>());
        assertThat(jwtService.parseTokenType(accessToken)).isEqualTo(TokenType.ACCESS);
        assertThat(jwtService.parseTokenType(refreshToken)).isEqualTo(TokenType.REFRESH);
        verify(userDetails, times(2)).getUsername();
        verifyNoMoreInteractions(userDetails);
    }

    @Test
    @DisplayName("JwtService throws error if JWT is forged")
    public void JwtService_ThrowsErrorIfJWTForges() {
        final String forgedToken = jwtService
                .generateToken(userDetails, TokenType.ACCESS, new HashMap<>()).substring(1);

        assertThatThrownBy(
                () -> jwtService.extractAllClaimsAndValidateToken(forgedToken)
        ).isInstanceOf(RuntimeException.class);

        verify(userDetails).getUsername();
        verifyNoMoreInteractions(userDetails);
    }

}
