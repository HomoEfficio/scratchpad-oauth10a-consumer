package io.homo.efficio.scratchpad.oauth10a.consumer.domain;

import lombok.Data;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-19.
 */
@Data
public abstract class AbstractOAuth10aCredentials {

    // underscore name is needed to be bound automatically
    protected String oauth_token;

}
