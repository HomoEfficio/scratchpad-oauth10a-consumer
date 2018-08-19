package io.homo.efficio.scratchpad.oauth10a.consumer.service;

import io.homo.efficio.scratchpad.oauth10a.consumer.domain.AbstractOAuth10aCredentials;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.AbstractOAuthRequestHeader;
import io.homo.efficio.scratchpad.oauth10a.consumer.exception.OAuth10aException;
import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuth10aSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;

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

    @NonNull
    private OAuth10aSupport oAuth10aSupport;

    public <T extends AbstractOAuth10aCredentials> ResponseEntity<T> getCredentials(AbstractOAuthRequestHeader oAuthRequestHeader, Class<T> clazz) {
        this.oAuth10aSupport.fillNonce(oAuthRequestHeader);
        this.oAuth10aSupport.fillSignature(oAuthRequestHeader);
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", oAuthRequestHeader.getRequestHeader());
        log.info("### {} request header: {}", clazz.getSimpleName(), oAuthRequestHeader.getRequestHeader());
        final HttpEntity<String> reqEntity = new HttpEntity<>(httpHeaders);

        final ResponseEntity<String> response = this.restTemplate.exchange(
                oAuthRequestHeader.getCredentialsUrl(),
                HttpMethod.POST,
                reqEntity,
                String.class
        );

        if (response.getStatusCode().equals(HttpStatus.OK)) {

            AbstractOAuth10aCredentials oAuthCredentials = null;
            try {
                oAuthCredentials = clazz.getConstructor().newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            final T credentials =
                    (T) this.oAuth10aSupport.getCredentialsFrom(oAuthCredentials, response.getBody());
            return new ResponseEntity<>(credentials, HttpStatus.OK);
        } else {
            throw new OAuth10aException("Response from Server: " + response.getStatusCode() + " " + response.getBody());
        }
    }
}
