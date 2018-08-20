package io.homo.efficio.scratchpad.oauth10a.consumer.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.http.HttpMethod;

import java.net.URI;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-20.
 */
@Data
public class NextAction {

    @NonNull
    private HttpMethod httpMethod;

    @NonNull
    private String url;

    @NonNull
    private URI uri;

    private String requestBody;

    public NextAction() {
    }

    public NextAction(HttpMethod httpMethod, String url, String requestBody) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.uri = URI.create(url);
        this.requestBody = requestBody;
    }

}
