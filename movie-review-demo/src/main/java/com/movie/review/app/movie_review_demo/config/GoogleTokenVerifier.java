package com.movie.review.app.movie_review_demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GoogleTokenVerifier {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> verifyGoogleAuthCode(String code, String redirectUri) throws Exception {
        //  Exchange authorization code for access token
        String tokenUrl = "https://oauth2.googleapis.com/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
        
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Failed to exchange authorization code for access token");
        }

        Map<String, Object> tokenResponse = objectMapper.readValue(response.getBody(), Map.class);
        System.out.println("Token Response: " + tokenResponse);
        String accessToken = (String) tokenResponse.get("access_token");

        // Use access token to get user info
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        
        HttpEntity<String> userInfoRequest = new HttpEntity<>(userInfoHeaders);
        
        ResponseEntity<String> userInfoResponse = restTemplate.exchange(
            userInfoUrl, 
            HttpMethod.GET, 
            userInfoRequest, 
            String.class
        );

        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Failed to fetch user info from Google");
        }
        System.out.println("User Info Response: " + userInfoResponse.getBody());
        return objectMapper.readValue(userInfoResponse.getBody(), Map.class);
    }
}