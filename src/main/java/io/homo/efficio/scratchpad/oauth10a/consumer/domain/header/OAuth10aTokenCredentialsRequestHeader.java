package io.homo.efficio.scratchpad.oauth10a.consumer.domain.header;

import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuth10aConstants;
import lombok.Data;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static io.homo.efficio.scratchpad.oauth10a.consumer.util.EncodingUtils.getPercentEncoded;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
@Data
public class OAuth10aTokenCredentialsRequestHeader extends AbstractOAuth10aRequestHeader {

    private String oauthVerifier;

    private String oauthToken;

    private String oauthTokenSecret;

    public OAuth10aTokenCredentialsRequestHeader(String serverUrl,
                                                 String oauthConsumerKey,
                                                 String oauthConsumerSecret,
                                                 String oauthToken,
                                                 String oauthTokenSecret,
                                                 String oauthVerifier) {
        this.httpMethod = HttpMethod.POST.toString();
        URI uri = URI.create(serverUrl);
        this.scheme = uri.getScheme();
        this.serverName = uri.getHost();
        this.serverPort = uri.getPort() == -1
                ? ("http".equals(this.scheme) ? 80 : "https".equals(this.scheme) ? 443 : -1)
                : uri.getPort();
        this.queryString = uri.getQuery();
        this.contentType = null;  // no need for temporary credentials
        this.requestBody = null;  // no need for temporary credentials
        this.serverUrl = serverUrl;
        this.oauthConsumerKey = oauthConsumerKey;
        this.oauthConsumerSecret = oauthConsumerSecret;
        this.oauthToken = oauthToken;
        this.oauthTokenSecret = oauthTokenSecret;
        this.oauthVerifier = oauthVerifier;
        this.oauthTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
    }

    @Override
    public String getRequestHeader() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OAuth ")
                .append(OAuth10aConstants.OAUTH_CONSUMER_KEY).append("=\"").append(this.oauthConsumerKey).append("\", ")
                .append(OAuth10aConstants.OAUTH_TOKEN).append("=\"").append(this.oauthToken).append("\", ")
                .append(OAuth10aConstants.OAUTH_VERIFIER).append("=\"").append(this.oauthVerifier).append("\", ")
                .append(OAuth10aConstants.OAUTH_SIGNATURE_METHOD).append("=\"").append(this.oauthSignatureMethod).append("\", ")
                .append(OAuth10aConstants.OAUTH_TIMESTAMP).append("=\"").append(this.oauthTimestamp).append("\", ")
                .append(OAuth10aConstants.OAUTH_NONCE).append("=\"").append(this.oauthNonce).append("\", ")
                .append(OAuth10aConstants.OAUTH_VERSION).append("=\"").append(this.oauthVersion).append("\", ")
                .append(OAuth10aConstants.OAUTH_SIGNATURE).append("=\"").append(getPercentEncoded(this.oauthSignature)).append("\"");
        return sb.toString();
    }

    @Override
    public String getKey() {
        return getPercentEncoded(this.getOauthConsumerSecret()) + "&" + getPercentEncoded(this.getOauthTokenSecret());
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
