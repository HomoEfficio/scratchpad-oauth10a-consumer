package io.homo.efficio.scratchpad.oauth10a.consumer.domain;

import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuth10aConstants;
import lombok.Data;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-19.
 */
@Data
public class TemporaryCredentials extends AbstractOAuth10aCredentials {

    // underscore name is needed to be bound automatically

    private String oauth_token_secret;

    private boolean oauth_callback_confirmed;

    @Override
    public AbstractOAuth10aCredentials getCredentialsFrom(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) return null;
        final TemporaryCredentials o = new TemporaryCredentials();
        String[] elements = responseBody.split("&");
        for (String element : elements) {
            String[] kv = element.split("=");
            if (OAuth10aConstants.OAUTH_TOKEN.equals(kv[0].toLowerCase()))
                o.setOauth_token(kv[1]);
            if (OAuth10aConstants.OAUTH_TOKEN_SECRET.equals(kv[0].toLowerCase()))
                o.setOauth_token_secret(kv[1]);
            if (OAuth10aConstants.OAUTH_CALLBACK_CONFIRMED.equals(kv[0].toLowerCase()))
                o.setOauth_callback_confirmed(Boolean.valueOf(kv[1]));
        }
        return o;
    }

    @Override
    public String toString() {
        return "TemporaryCredentials{" +
                "oauth_token_secret='" + oauth_token_secret + '\'' +
                ", oauth_callback_confirmed=" + oauth_callback_confirmed +
                ", oauth_token='" + oauth_token + '\'' +
                '}';
    }
}
