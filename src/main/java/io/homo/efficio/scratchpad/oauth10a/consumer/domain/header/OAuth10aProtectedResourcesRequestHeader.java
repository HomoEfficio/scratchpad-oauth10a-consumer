package io.homo.efficio.scratchpad.oauth10a.consumer.domain.header;

import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuth10aConstants;
import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static io.homo.efficio.scratchpad.oauth10a.consumer.util.URLUtils.getUrlEncoded;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
@Data
public class OAuth10aProtectedResourcesRequestHeader extends AbstractOAuth10aRequestHeader {

    private String oauthToken;

    private String oauthTokenSecret;

//    public OAuth10aProtectedResourcesRequestHeader(HttpServletRequest request,
    public OAuth10aProtectedResourcesRequestHeader(String scheme,
                                                   String serverName,
                                                   int serverPort,
                                                   String queryString,
                                                   String contentType,
                                                   String requestBody,
                                                   String serverUrl,
                                                   String oauthConsumerKey,
                                                   String oauthConsumerSecret,
                                                   String oauthToken,
                                                   String oauthTokenSecret) {
        this.httpMethod = HttpMethod.POST.toString();
        this.scheme = scheme;
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.queryString = queryString;
        this.contentType = contentType;
        this.requestBody = requestBody;
        this.serverUrl = serverUrl;
        this.oauthConsumerKey = oauthConsumerKey;
        this.oauthConsumerSecret = oauthConsumerSecret;
        this.oauthToken = oauthToken;
        this.oauthTokenSecret = oauthTokenSecret;
        this.oauthTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
    }

    @Override
    public String getRequestHeader() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OAuth ")
                .append(OAuth10aConstants.OAUTH_CONSUMER_KEY).append("=\"").append(this.oauthConsumerKey).append("\", ")
                .append(OAuth10aConstants.OAUTH_NONCE).append("=\"").append(this.oauthNonce).append("\", ")
                .append(OAuth10aConstants.OAUTH_SIGNATURE).append("=\"").append(getUrlEncoded(this.oauthSignature)).append("\", ")
                .append(OAuth10aConstants.OAUTH_SIGNATURE_METHOD).append("=\"").append(this.oauthSignatureMethod).append("\", ")
                .append(OAuth10aConstants.OAUTH_TIMESTAMP).append("=\"").append(this.oauthTimestamp).append("\", ")
                .append(OAuth10aConstants.OAUTH_TOKEN).append("=\"").append(this.oauthToken).append("\", ")
                .append(OAuth10aConstants.OAUTH_VERSION).append("=\"").append(this.oauthVersion).append("\"");
        return sb.toString();
    }

    @Override
    public String getKey() {
        return getUrlEncoded(this.getOauthConsumerSecret()) + "&" + getUrlEncoded(this.getOauthTokenSecret());
    }

    @Override
    public Map<String, String> getRequestHeaderMap() {
        final HashMap<String, String> headerMap = new HashMap<>();

        headerMap.put(OAuth10aConstants.OAUTH_CONSUMER_KEY, this.getOauthConsumerKey());
        headerMap.put(OAuth10aConstants.OAUTH_SIGNATURE_METHOD, this.getOauthSignatureMethod());
        headerMap.put(OAuth10aConstants.OAUTH_TIMESTAMP, this.getOauthTimestamp());
        headerMap.put(OAuth10aConstants.OAUTH_NONCE, this.getOauthNonce());
        headerMap.put(OAuth10aConstants.OAUTH_VERSION, this.getOauthVersion());

        return headerMap;
    }
}
