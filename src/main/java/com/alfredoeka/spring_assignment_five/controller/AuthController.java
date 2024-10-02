package com.alfredoeka.spring_assignment_five.controller;

import com.alfredoeka.spring_assignment_five.model.oauth.LoginRequest;
import com.alfredoeka.spring_assignment_five.model.oauth.TokenResponse;
import com.alfredoeka.spring_assignment_five.repository.JpaRegisteredClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizationService authorizationService;
    private final JpaRegisteredClientRepository clientRepository;
    private final OAuth2TokenGenerator<OAuth2Token> tokenGenerator;

    public AuthController(AuthenticationManager authenticationManager,
                          OAuth2AuthorizationService authorizationService,
                          JpaRegisteredClientRepository clientRepository,
                          OAuth2TokenGenerator<OAuth2Token> tokenGenerator) {
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.clientRepository = clientRepository;
        this.tokenGenerator = tokenGenerator;
    }

    @PostMapping("/user-login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                RegisteredClient client = clientRepository.findByClientId("my-client-web");

                Set<String> scopes = Set.of("read", "write");

                OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(client)
                        .principalName(authentication.getName())
                        .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                        .attribute(Principal.class.getName(), authentication);

                OAuth2TokenContext tokenContext = DefaultOAuth2TokenContext.builder()
                        .registeredClient(client)
                        .principal(authentication)
                        .authorizationGrantType(AuthorizationGrantType.PASSWORD)
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

                TokenResponse tokenResponse = new TokenResponse();
                tokenResponse.setAccess_token(accessToken.getTokenValue());
                tokenResponse.setRefresh_token(refreshToken != null ? refreshToken.getTokenValue() : null);
                tokenResponse.setScope(String.join(" ", scopes));
                tokenResponse.setToken_type(accessToken.getTokenType().getValue());
                tokenResponse.setExpires_in(Objects.requireNonNull(accessToken.getExpiresAt()).getEpochSecond() - Instant.now().getEpochSecond());
                tokenResponse.setJti(UUID.randomUUID().toString());

                return ResponseEntity.ok(tokenResponse);
            }

            return ResponseEntity.badRequest().body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Authentication failed: " + e.getMessage());
        }
    }
}
