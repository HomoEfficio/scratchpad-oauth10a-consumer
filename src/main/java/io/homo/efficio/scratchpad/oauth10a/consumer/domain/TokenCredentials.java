package io.homo.efficio.scratchpad.oauth10a.consumer.domain;

import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuth10aConstants;
import lombok.Data;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-19.
 */
@Data
public class TokenCredentials extends AbstractOAuth10aCredentials {

    // underscore name is needed to be bound automatically

    private String oauth_token_secret;

    @Override
    public AbstractOAuth10aCredentials getCredentialsFrom(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) return null;
        final TokenCredentials o = new TokenCredentials();
        String[] elements = responseBody.split("&");
        for (String element : elements) {
            String[] kv = element.split("=");
            if (OAuth10aConstants.OAUTH_TOKEN.equals(kv[0].toLowerCase()))
                o.setOauth_token(kv[1]);
            if (OAuth10aConstants.OAUTH_TOKEN_SECRET.equals(kv[0].toLowerCase()))
                o.setOauth_token_secret(kv[1]);
        }
        return o;
    }

    @Override
    public String toString() {
        return "TokenCredentials{" +
                "oauth_token_secret='" + oauth_token_secret + '\'' +
                ", oauth_token='" + oauth_token + '\'' +
                '}';
    }
}
