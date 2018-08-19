package io.homo.efficio.scratchpad.oauth10a.consumer.domain;

import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuth10aConstants;
import lombok.Data;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static io.homo.efficio.scratchpad.oauth10a.consumer.util.URLUtils.getUrlEncoded;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
@Data
public class OAuth10aTemporaryCredentialsHeader extends AbstractOAuthRequestHeader {

    private String credentialsUrl;

    private String oauthCallbackUrl;

    private String oauthVersion = OAuth10aConstants.VERSION_1_0;

    public OAuth10aTemporaryCredentialsHeader(HttpServletRequest request,
                                              String credentialsUrl,
                                              String oauthConsumerKey,
                                              String oauthConsumerSecret,
                                              String oauthCallbackUrl) {
        this.httpMethod = HttpMethod.POST.toString();
        this.scheme = request.getScheme();
        this.serverName = request.getServerName();
        this.serverPort = request.getServerPort();
        this.queryString = request.getQueryString();
        this.contentType = request.getContentType();
        this.requestBody = getRequestBody(request);
        this.credentialsUrl = credentialsUrl;
        this.oauthConsumerKey = oauthConsumerKey;
        this.oauthConsumerSecret = oauthConsumerSecret;
        this.oauthCallbackUrl = oauthCallbackUrl;
        this.oauthTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
    }

    public String getRequestHeader() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OAuth ")
                .append(OAuth10aConstants.OAUTH_CONSUMER_KEY).append("=\"").append(this.oauthConsumerKey).append("\", ")
                .append(OAuth10aConstants.OAUTH_SIGNATURE_METHOD).append("=\"").append(this.oauthSignatureMethod).append("\", ")
                .append(OAuth10aConstants.OAUTH_TIMESTAMP).append("=\"").append(this.oauthTimestamp).append("\", ")
                .append(OAuth10aConstants.OAUTH_NONCE).append("=\"").append(this.oauthNonce).append("\", ")
                .append(OAuth10aConstants.OAUTH_VERSION).append("=\"").append(this.oauthVersion).append("\", ")
                .append(OAuth10aConstants.OAUTH_CALLBACK).append("=\"").append(getUrlEncoded(this.oauthCallbackUrl)).append("\", ")
                .append(OAuth10aConstants.OAUTH_SIGNATURE).append("=\"").append(getUrlEncoded(this.oauthSignature)).append("\"");
        return sb.toString();
    }

    @Override
    public String getKey() {
        return getUrlEncoded(this.getOauthConsumerSecret()) + "&";
    }

    @Override
    public Map<String, String> getRequestHeaderMap() {
        final HashMap<String, String> headerMap = new HashMap<>();

        headerMap.put(OAuth10aConstants.OAUTH_CONSUMER_KEY, this.getOauthConsumerKey());
        headerMap.put(OAuth10aConstants.OAUTH_SIGNATURE_METHOD, this.getOauthSignatureMethod());
        headerMap.put(OAuth10aConstants.OAUTH_TIMESTAMP, this.getOauthTimestamp());
        headerMap.put(OAuth10aConstants.OAUTH_NONCE, this.getOauthNonce());
        headerMap.put(OAuth10aConstants.OAUTH_VERSION, this.getOauthVersion());
        headerMap.put(OAuth10aConstants.OAUTH_CALLBACK, this.getOauthCallbackUrl());

        return headerMap;
    }
}
