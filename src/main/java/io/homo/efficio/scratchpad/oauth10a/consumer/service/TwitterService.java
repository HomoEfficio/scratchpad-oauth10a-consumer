package io.homo.efficio.scratchpad.oauth10a.consumer.service;

import io.homo.efficio.scratchpad.oauth10a.consumer.domain.TemporaryCredentials;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.OAuthRequestHeader;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.TokenCredentials;
import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuth10aException;
import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuthUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TwitterService {

    @Value("${oauth10a.provider.temporary.credentials.url}")
    private String temporaryCredentialsUrl;

    @Value("${oauth10a.provider.token.credentials.url}")
    private String tokenCredentialsUrl;

    @NonNull
    private RestTemplate restTemplate;

    public ResponseEntity<TemporaryCredentials> getTemporaryCredentials(OAuthRequestHeader rtHeader) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", rtHeader.getAuthorizationHeader());
        log.info("### authorization header: {}", rtHeader.getAuthorizationHeader());
        final HttpEntity<String> reqEntity = new HttpEntity<>(httpHeaders);

        final ResponseEntity<String> response = this.restTemplate.exchange(
                this.temporaryCredentialsUrl,
                HttpMethod.POST,
                reqEntity,
                String.class
        );

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            final TemporaryCredentials temporaryCredentials =
                    OAuthUtils.getTemporaryCredentialsFrom(response.getBody());
            return new ResponseEntity<>(temporaryCredentials, HttpStatus.OK);
        } else {
            throw new OAuth10aException("Response from Server: " + response.getStatusCode() + " " + response.getBody());
        }
    }

    public ResponseEntity<TokenCredentials> getTokenCredentials(OAuthRequestHeader atHeader) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", atHeader.getTokenCredentialsHeader());
        log.info("### authorization header: {}", atHeader.getTokenCredentialsHeader());
        final HttpEntity<String> reqEntity = new HttpEntity<>(httpHeaders);

        final ResponseEntity<String> response = this.restTemplate.exchange(
                this.tokenCredentialsUrl,
                HttpMethod.POST,
                reqEntity,
                String.class
        );

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            final TokenCredentials tokenCredentials =
                    OAuthUtils.getTokenCredentialsFrom(response.getBody());
            return new ResponseEntity<>(tokenCredentials, HttpStatus.OK);
        } else {
            throw new OAuth10aException("Response from Server: " + response.getStatusCode() + " " + response.getBody());
        }
    }
}
