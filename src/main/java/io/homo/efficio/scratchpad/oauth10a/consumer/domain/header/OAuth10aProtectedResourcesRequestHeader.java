package io.homo.efficio.scratchpad.oauth10a.consumer.domain.header;

import io.homo.efficio.scratchpad.oauth10a.consumer.domain.NextAction;
import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuth10aConstants;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static io.homo.efficio.scratchpad.oauth10a.consumer.util.EncodingUtils.getPercentEncoded;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
@Data
public class OAuth10aProtectedResourcesRequestHeader extends AbstractOAuth10aRequestHeader {

    private String oauthToken;

    private String oauthTokenSecret;


    public OAuth10aProtectedResourcesRequestHeader(NextAction nextAction, String consumerKey, String consumerSecret, String oauth_token, String oauth_token_secret) {
        URI nextUri = nextAction.getUri();
        this.httpMethod = HttpMethod.POST.toString();
        this.scheme = nextUri.getScheme();
        this.serverName = nextUri.getHost();
        this.serverPort = nextUri.getPort() == -1
                ? ("http".equals(this.scheme) ? 80 : "https".equals(this.scheme) ? 443 : -1)
                : nextUri.getPort();
//      nextUri.getQuery(),  // uri.getQuery()는 decoding한 문자열을 반환하므로 사용하면 안됨
        this.queryString = nextAction.getUrl().indexOf('?') > 0
                ? nextAction.getUrl().substring(nextAction.getUrl().indexOf('?') + 1)
                : null;
        this.contentType = MediaType.APPLICATION_JSON_VALUE;
        this.requestBody = nextAction.getRequestBody();
        this.serverUrl = nextAction.getUrl();
        this.oauthConsumerKey = consumerKey;
        this.oauthConsumerSecret = consumerSecret;
        this.oauthToken = oauth_token;
        this.oauthTokenSecret = oauth_token_secret;
        this.oauthTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
    }

    @Override
    public String getRequestHeader() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OAuth ")
                .append(OAuth10aConstants.OAUTH_CONSUMER_KEY).append("=\"").append(this.oauthConsumerKey).append("\", ")
                .append(OAuth10aConstants.OAUTH_NONCE).append("=\"").append(this.oauthNonce).append("\", ")
                .append(OAuth10aConstants.OAUTH_SIGNATURE).append("=\"").append(getPercentEncoded(this.oauthSignature)).append("\", ")
                .append(OAuth10aConstants.OAUTH_SIGNATURE_METHOD).append("=\"").append(this.oauthSignatureMethod).append("\", ")
                .append(OAuth10aConstants.OAUTH_TIMESTAMP).append("=\"").append(this.oauthTimestamp).append("\", ")
                .append(OAuth10aConstants.OAUTH_TOKEN).append("=\"").append(this.oauthToken).append("\", ")
                .append(OAuth10aConstants.OAUTH_VERSION).append("=\"").append(this.oauthVersion).append("\"");
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
        headerMap.put(OAuth10aConstants.OAUTH_TOKEN, this.getOauthToken());
        headerMap.put(OAuth10aConstants.OAUTH_TIMESTAMP, this.getOauthTimestamp());
        headerMap.put(OAuth10aConstants.OAUTH_NONCE, this.getOauthNonce());
        headerMap.put(OAuth10aConstants.OAUTH_VERSION, this.getOauthVersion());

        return headerMap;
    }
}
