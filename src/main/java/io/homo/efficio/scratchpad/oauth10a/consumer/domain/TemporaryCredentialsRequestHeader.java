package io.homo.efficio.scratchpad.oauth10a.consumer.domain;

import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuthConstants;
import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuthUtils;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
@Data
public class TemporaryCredentialsRequestHeader {

    private String httpMethod;

    private String scheme;

    private String serverName;

    private int serverPort;

    private String requestUri;

    private String queryString;

    private String contentType;

    private String requestBody;

    private String oauthConsumerKey;

    //   A nonce is a random string, uniquely generated by the client to allow
    //   the server to verify that a request has never been made before and
    //   helps prevent replay attacks when requests are made over a non-secure
    //   channel.  The nonce value MUST be unique across all requests with the
    //   same timestamp, client credentials, and token combinations.
    //   The parameter MAY be omitted when using the "PLAINTEXT" signature method.
    private String oauthNonce;

    private String oauthSignature;

    private String oauthSignatureMethod = OAuthConstants.OAUTH_SIGNATURE_METHOD_HMAC_SHA1;

    //   The timestamp value MUST be a positive integer.  Unless otherwise
    //   specified by the server's documentation, the timestamp is expressed
    //   in the number of seconds since January 1, 1970 00:00:00 GMT.
    //   The parameter MAY be omitted when using the "PLAINTEXT" signature method.
    private String oauthTimestamp;

    private String oauthToken;

    private String oauthVersion = OAuthConstants.VERSION_1_0;

    public TemporaryCredentialsRequestHeader(HttpServletRequest request, String oauthConsumerKey) {
        this.httpMethod = request.getMethod();
        this.scheme = request.getScheme();
        this.serverName = request.getServerName();
        this.serverPort = request.getServerPort();
        this.requestUri = request.getRequestURI();
        this.queryString = request.getQueryString();
        this.contentType = request.getContentType();
        this.requestBody = getRequestBody(request);
        this.oauthConsumerKey = oauthConsumerKey;
        this.oauthTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
        this.oauthNonce = OAuthUtils.generateNonce();
    }

    private String getRequestBody(HttpServletRequest request) {
        try {
            final BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}