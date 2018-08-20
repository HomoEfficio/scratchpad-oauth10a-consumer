package io.homo.efficio.scratchpad.oauth10a.consumer.util;

import io.homo.efficio.scratchpad.oauth10a.consumer.domain.AbstractOAuthRequestHeader;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static io.homo.efficio.scratchpad.oauth10a.consumer.util.URLUtils.getUrlDecoded;
import static io.homo.efficio.scratchpad.oauth10a.consumer.util.URLUtils.getUrlEncoded;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
@Component
public class OAuth10aSignatureSupport {

    public void fillNonce(AbstractOAuthRequestHeader header) {
        header.setOauthNonce(Base64.getEncoder()
                .encodeToString(String.valueOf(UUID.randomUUID()).getBytes()));
    }

    public void fillSignature(AbstractOAuthRequestHeader header) {
        String key = header.getKey();
        String baseString = generateBaseString(header);
        try {
            final SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), OAuth10aConstants.HMAC_SHA1_ALGORITHM_NAME);
            final Mac mac = Mac.getInstance(OAuth10aConstants.HMAC_SHA1_ALGORITHM_NAME);
            mac.init(signingKey);
            final String signature = Base64.getEncoder().encodeToString(mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8)));
            header.setOauthSignature(signature);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateBaseString(AbstractOAuthRequestHeader header) {
        String httpMethod = header.getHttpMethod();
        String baseUri = getBaseStringUri(header);
        String requestParameters = getRequestParameters(header);

        final StringBuilder sb = new StringBuilder();
        sb.append(httpMethod)
                .append('&').append(getUrlEncoded(baseUri))
                .append('&').append(getUrlEncoded(requestParameters));

        return sb.toString();
    }

    private String getBaseStringUri(AbstractOAuthRequestHeader header) {
        return header.getCredentialsUrl();
    }

    private String getRequestParameters(AbstractOAuthRequestHeader header) {
        return getNormalizedParameters(getParameterSources(header));
    }

    private Map<String, List<String>> getParameterSources(AbstractOAuthRequestHeader header) {
        final Map<String, List<String>> paramSources = new HashMap<>();

        final String queryString = header.getQueryString();
        kvToMultiValueMap(paramSources, queryString);

        final Map<String, String> requestHeaderMap = header.getRequestHeaderMap();

        for (Map.Entry<String, String> entry: requestHeaderMap.entrySet()) {
            putMultiValue(paramSources, entry.getKey(), entry.getValue());
        }

        final String requestBody = header.getRequestBody();
        kvToMultiValueMap(paramSources, requestBody);

        return paramSources;
    }

    private void kvToMultiValueMap(Map<String, List<String>> paramSources, String queryString) {
        if (queryString != null && !queryString.isEmpty()) {
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] kv = param.split("=");
                String key = getUrlDecoded(kv[0]);
                String value = kv.length == 2 ? getUrlDecoded(kv[1]) : "";
                putMultiValue(paramSources, key, value);
            }
        }
    }

    private void putMultiValue(Map<String, List<String>> map, String key, String value) {
        if (map.get(key) == null) {
            final ArrayList<String> values = new ArrayList<>();
            values.add(value);
            map.put(key, values);
        } else {
            map.get(key).add(value);
        }
    }

    private String getNormalizedParameters(Map<String, List<String>> parameterSources) {
        SortedMap<String, List<String>> normalizedParametersMap = new TreeMap<>();
        final Set<Map.Entry<String, List<String>>> entries = parameterSources.entrySet();
        for (Map.Entry<String, List<String>> entry: entries) {
            for (String value: entry.getValue()) {
                putMultiValue(normalizedParametersMap, getUrlEncoded(entry.getKey()), getUrlEncoded(value));
            }
            List<String> values = normalizedParametersMap.get(getUrlEncoded(entry.getKey()));
            Collections.sort(values);
        }

        final Set<Map.Entry<String, List<String>>> normalizedEntries = normalizedParametersMap.entrySet();
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry: normalizedEntries) {
            for (String value: entry.getValue()) {
                sb.append('&')
                        .append(entry.getKey()).append('=').append(value);
            }
        }
        return sb.toString().substring(1);
    }

}
