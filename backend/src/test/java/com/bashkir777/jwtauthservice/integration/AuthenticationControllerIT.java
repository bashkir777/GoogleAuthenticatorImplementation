package com.bashkir777.jwtauthservice.integration;

import com.bashkir777.jwtauthservice.app.data.entities.RefreshToken;
import com.bashkir777.jwtauthservice.app.data.entities.User;
import com.bashkir777.jwtauthservice.app.data.enums.TokenType;
import com.bashkir777.jwtauthservice.app.data.repositories.TokenRepository;
import com.bashkir777.jwtauthservice.app.data.repositories.UserRepository;
import com.bashkir777.jwtauthservice.auth.dto.*;
import com.bashkir777.jwtauthservice.auth.services.JwtService;
import com.bashkir777.jwtauthservice.auth.services.TwoFactorAuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.regex.Pattern;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerIT {
    @Value("${spring.application.name}")
    private String appName;
    private UserDetails userDetails;
    private MockMvc mockMvc;
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String SECRET = "5BJK32X4XVJFYDQHP2B3SPFY6PXYMZXN";
    private TwoFactorAuthenticationService tfaService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private JwtService jwtService;

    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }
    @Autowired
    public void setTfaService(TwoFactorAuthenticationService tfaService){
        this.tfaService = tfaService;
    }

    @Autowired
    public void serPasswordEncoder(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Autowired
    public void setTokenRepository(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Test
    @DisplayName("Logout correctly removes refresh token from whitelist")
    @SqlGroup(
            {
                    @Sql(scripts = "/sql/createUserTFAEnabled.sql"
                            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                    @Sql(scripts = "/sql/truncateUsers.sql"
                            ,executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            }
    )
    public void logout_CorrectlyRemovesRefreshTokenFromWhitelist() throws Exception{
        User user = userRepository.getUserByUsername(USERNAME);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(jwtService.generateToken(user, TokenType.REFRESH, null))
                .build();
        tokenRepository.save(refreshToken);
        tokenRepository.flush();
        var requestBody = RefreshTokenDTO.builder()
                .refreshToken(refreshToken.getToken()).build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Success[] success = new Success[1];
        assertThatCode(()->success[0] = objectMapper.readValue(response, Success.class))
                .doesNotThrowAnyException();
        assertThat(success[0].getSuccess()).isTrue();
        assertThat(tokenRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Reset password correctly resets password")
    @SqlGroup(
            {
                    @Sql(scripts = "/sql/createUserTFAEnabled.sql"
                            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                    @Sql(scripts = "/sql/truncateUsers.sql"
                            ,executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            }
    )
    public void resetPassword_CorrectlyResetsPassword() throws Exception{
        final String NEW_PASSWORD = "newPassword";
        var requestBody = ResetPassword.builder().newPassword(NEW_PASSWORD)
                .otp(tfaService.generateCurrentOtpForSecretKey(SECRET))
                        .username(USERNAME).build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk()).andReturn();

        String answerBody = mvcResult.getResponse().getContentAsString();
        AuthenticationResponse[] authResponse = new AuthenticationResponse[1];
        assertThatCode(
                () -> authResponse[0]
                        = objectMapper.readValue(answerBody, AuthenticationResponse.class))
                .doesNotThrowAnyException();
        assertThat(authResponse[0].getAccessToken()).isNotEmpty().isNotNull();
        assertThat(authResponse[0].getRefreshToken()).isNotEmpty().isNotNull();
        String passwordInDB = userRepository.getUserByUsername(USERNAME).getPassword();
        assertThat(passwordEncoder.matches(NEW_PASSWORD, passwordInDB)).isTrue();
    }


    @Test
    @DisplayName("Get username is free return true if username is free")
    public void getUsernameIsFree_UsernameIsFree_ReturnsTrue() throws Exception{
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/auth/is-username-free/" + USERNAME))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        IsFree[] isFree = new IsFree[1];
        assertThatCode(
                () -> isFree[0] = objectMapper.readValue(responseBody, IsFree.class)
        ).doesNotThrowAnyException();
        assertThat(isFree[0].getIsFree()).isTrue();
    }

    @Test
    @DisplayName("Get TFA is enable returns true if user has enabled TFA")
    @SqlGroup(
            {
                    @Sql(scripts = "/sql/createUserTFAEnabled.sql"
                            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                    @Sql(scripts = "/sql/truncateUsers.sql"
                            ,executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            }
    )
    public void getTFAIsEnable_UserHasTFAEnabled_ReturnsTrue() throws Exception{
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/auth/tfa-enabled/" + USERNAME))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        TFAEnabled[] tfaEnabled = new TFAEnabled[1];
        assertThatCode(
                () -> tfaEnabled[0] = objectMapper.readValue(responseBody, TFAEnabled.class)
        ).doesNotThrowAnyException();
        assertThat(tfaEnabled[0].isTfaEnabled()).isTrue();
    }


    @Test
    @DisplayName("Login valid info and TFA enabled returns tokens")
    @SqlGroup(
            {
                    @Sql(scripts = "/sql/createUserTFAEnabled.sql"
                            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                    @Sql(scripts = "/sql/truncateUsers.sql"
                     ,executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
            }
    )
    public void login_validInfoTFAEnabled_returnsTokens() throws Exception{
        var requestBody = AuthenticationRequest.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .otp(tfaService.generateCurrentOtpForSecretKey(SECRET))
                        .build();
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyJson)).andExpect(status().isOk()).andReturn();
        String answerBody = mvcResult.getResponse().getContentAsString();
        AuthenticationResponse[] authResponse = new AuthenticationResponse[1];
        assertThatCode(
                () -> authResponse[0]
                        = objectMapper.readValue(answerBody, AuthenticationResponse.class))
                .doesNotThrowAnyException();
        assertThat(authResponse[0].getAccessToken()).isNotEmpty().isNotNull();
        assertThat(authResponse[0].getRefreshToken()).isNotEmpty().isNotNull();
    }


    @Test
    @DisplayName("Register new user with valid info and TFA enabled returns tokens")
    public void registerNewUser_validInfoTFAEnabled_returnsTokens() throws Exception{
        var requestBody = RegisterRequest.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .firstname(USERNAME)
                .lastname(USERNAME)
                .tfaEnabled(true)
                .secret(SECRET)
                .otp(tfaService.generateCurrentOtpForSecretKey(SECRET))
                .build();

        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isCreated()).andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();

        AuthenticationResponse[] authResponse = new AuthenticationResponse[1];
        assertThatCode(
                () -> authResponse[0] = objectMapper.readValue(responseJson, AuthenticationResponse.class)
        ).doesNotThrowAnyException();
        assertThat(authResponse[0].getAccessToken()).isNotEmpty().isNotNull();
        assertThat(authResponse[0].getRefreshToken()).isNotEmpty().isNotNull();
    }


    @Test
    @DisplayName("Get /generate-secret-qr-url/{username} returns formated " +
            "for Google Authenticator and wrapped In JSON String")
    public void getSecretQrURL_ReturnsStringFormatedForGoogleAuthenticatorWrappedInJSONString() throws Exception {
        Pattern pattern = Pattern.compile(
                        String.format("otpauth://totp/%s:%s\\?secret=.*&issuer=%s"
                                , appName
                                , USERNAME
                                , appName
                        )
        );

        var requestBuilder =
                MockMvcRequestBuilders.get("/api/v1/auth/generate-secret-qr-url/username");
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk()).andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();

        final QRCode[] qrCode = new QRCode[1];
        assertThatCode(() -> qrCode[0] = objectMapper.readValue(responseJson, QRCode.class)
        ).doesNotThrowAnyException();

        assertThat(pattern.matcher(qrCode[0].getSecretQrUrl()).matches()).isTrue();
    }

}