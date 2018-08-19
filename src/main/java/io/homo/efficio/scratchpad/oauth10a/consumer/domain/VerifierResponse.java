package io.homo.efficio.scratchpad.oauth10a.consumer.domain;

import lombok.Data;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-19.
 */
@Data
public class VerifierResponse {

    // underscore name is needed to be bound automatically

    private String oauth_token;

    private String oauth_verifier;
}
