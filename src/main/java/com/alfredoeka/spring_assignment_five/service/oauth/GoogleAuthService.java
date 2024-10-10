package com.alfredoeka.spring_assignment_five.service.oauth;

import com.alfredoeka.spring_assignment_five.entity.oauth.User;
import com.alfredoeka.spring_assignment_five.model.ApiResponse;
import com.alfredoeka.spring_assignment_five.model.SignInGoogleResponse;
import com.alfredoeka.spring_assignment_five.model.oauth.GoogleUserInfo;
import com.alfredoeka.spring_assignment_five.model.oauth.TokenResponse;
import com.alfredoeka.spring_assignment_five.repository.JpaRegisteredClientRepository;
import com.alfredoeka.spring_assignment_five.repository.oauth.ClientRepository;
import com.alfredoeka.spring_assignment_five.repository.oauth.UserRepository;
import com.alfredoeka.spring_assignment_five.service.EmailService;
import com.alfredoeka.spring_assignment_five.service.registration.UserRegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.time.Instant;
import java.util.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;


@Service
public class GoogleAuthService {

    private final JpaRegisteredClientRepository clientRepository;
    private final OAuth2TokenGenerator<OAuth2Token> tokenGenerator;

    @Autowired
    private OAuth2AuthorizationService authorizationService;

    @Autowired
    private JwtEncoder jwtEncoder;

    public GoogleAuthService(JpaRegisteredClientRepository clientRepository, OAuth2TokenGenerator<OAuth2Token> tokenGenerator) {
        this.clientRepository = clientRepository;
        this.tokenGenerator = tokenGenerator;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OAuth2AuthorizedClientService clientService;


    private static final Logger log = LoggerFactory.getLogger(GoogleAuthService.class);
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.callback-uri}")
    private String getCallbackUri;

    public String getAuthorizationUrl() {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                clientId,
                clientSecret,
                Arrays.asList("openid", "profile", "email")
        ).build();

        return flow.newAuthorizationUrl().setRedirectUri(getCallbackUri).build();
    }

    public SignInGoogleResponse<?> getUserData(String authorizationCode) throws IOException {
        try {
            HttpTransport transport = new NetHttpTransport();

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    transport,
                    GsonFactory.getDefaultInstance(),
                    clientId,
                    clientSecret,
                    Arrays.asList("openid", "profile", "email")
            ).build();

            GoogleTokenResponse tokenResponse = flow.newTokenRequest(authorizationCode)
                    .setRedirectUri(getCallbackUri)
                    .execute();

            String userId = tokenResponse.parseIdToken().getPayload().getSubject();
            Credential credential = flow.createAndStoreCredential(tokenResponse, userId);
            HttpRequestFactory requestFactory = transport.createRequestFactory(credential);

            GenericUrl url = new GenericUrl("https://www.googleapis.com/oauth2/v1/userinfo");
            HttpRequest request = requestFactory.buildGetRequest(url);
            String userIdentity = request.execute().parseAsString();

            ObjectMapper mapper = new ObjectMapper();
            GoogleUserInfo userInfo = mapper.readValue(userIdentity, GoogleUserInfo.class);

            User user = userRepository.findOneByUsername(userInfo.getEmail());
            if (user != null) {
                TokenResponse accessToken = generateOAuth2Token(user);
                return SignInGoogleResponse.builder()
                        .data(accessToken)
                        .type("login")
                        .message("Success")
                        .status(HttpStatus.OK.value())
                        .build();
            } else {
                User newUser = new User();
                newUser.setFullname(userInfo.getName());
                newUser.setUsername(userInfo.getEmail());

                // Generate OTP
                String otp = UserRegistrationService.generateOTP();
                Date otpExpiredDate = UserRegistrationService.calculateOtpExpiryDate();
                newUser.setOtp(otp);
                newUser.setOtpExpiredDate(otpExpiredDate);

                newUser.setEnabled(false);

                emailService.sendAfterRegistrationEmail(newUser, otp);

                userRepository.save(newUser);
                return SignInGoogleResponse.builder()
                        .data(newUser)
                        .type("register")
                        .status(HttpStatus.OK.value())
                        .build();
            }
        } catch (Exception e) {
            return SignInGoogleResponse.builder()
                    .message("Error occured: " + e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
        }
    }

    private TokenResponse generateOAuth2Token(User user) {
        RegisteredClient client = clientRepository.findByClientId("my-client-web");
        if (client == null) {
            throw new IllegalStateException("RegisteredClient not found");
        }

        try {
            // Assume googleLoginRequest contains the username (email) from Google Sign-In
            String username = user.getUsername();

            // Create an authentication token without password
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

            Set<String> scopes = Set.of("read", "write");

            OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(client)
                    .principalName(authentication.getName())
                    .authorizationGrantType(AuthorizationGrantType.PASSWORD) // You might want to use a different grant type for Google Sign-In
                    .attribute(Principal.class.getName(), authentication);

            OAuth2TokenContext tokenContext = DefaultOAuth2TokenContext.builder()
                    .registeredClient(client)
                    .principal(authentication)
                    .authorizationGrantType(AuthorizationGrantType.PASSWORD) // Same as above
                    .authorizedScopes(scopes)
                    .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                    .authorizationGrant(authentication)
                    .build();

            OAuth2Token generatedAccessToken = tokenGenerator.generate(tokenContext);
            if (generatedAccessToken == null) {
                throw new OAuth2AuthenticationException(OAuth2ErrorCodes.SERVER_ERROR);
            }
            OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                    generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
                    generatedAccessToken.getExpiresAt(), scopes);

            OAuth2RefreshToken refreshToken = null;
            if (client.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
                tokenContext = DefaultOAuth2TokenContext.builder()
                        .registeredClient(client)
                        .principal(authentication)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .authorizedScopes(scopes)
                        .tokenType(OAuth2TokenType.REFRESH_TOKEN)
                        .authorizationGrant(authentication)
                        .build();

                OAuth2Token generatedRefreshToken = tokenGenerator.generate(tokenContext);
                if (generatedRefreshToken != null) {
                    refreshToken = new OAuth2RefreshToken(generatedRefreshToken.getTokenValue(),
                            generatedRefreshToken.getIssuedAt(), generatedRefreshToken.getExpiresAt());
                }
            }

            authorizationBuilder
                    .token(accessToken)
                    .refreshToken(refreshToken);

            OAuth2Authorization authorization = authorizationBuilder.build();
            authorizationService.save(authorization);

            TokenResponse _tokenResponse = new TokenResponse();
            _tokenResponse.setAccess_token(accessToken.getTokenValue());
            _tokenResponse.setRefresh_token(refreshToken != null ? refreshToken.getTokenValue() : null);
            _tokenResponse.setScope(String.join(" ", scopes));
            _tokenResponse.setToken_type(accessToken.getTokenType().getValue());
            _tokenResponse.setExpires_in(Objects.requireNonNull(accessToken.getExpiresAt()).getEpochSecond() - Instant.now().getEpochSecond());
            _tokenResponse.setJti(UUID.randomUUID().toString());

            return _tokenResponse;
        } catch (Exception e) {
            throw e;
        }
    }

}
