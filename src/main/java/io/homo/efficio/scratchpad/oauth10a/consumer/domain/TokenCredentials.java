package io.homo.efficio.scratchpad.oauth10a.consumer.domain;

import lombok.Data;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-19.
 */
@Data
public class TokenCredentials {

    private String oauthToken;

    private String oauthTokenSecret;
}
