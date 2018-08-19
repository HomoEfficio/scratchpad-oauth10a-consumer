package io.homo.efficio.scratchpad.oauth10a.consumer.util;

import io.homo.efficio.scratchpad.oauth10a.consumer.domain.TemporaryCredentials;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.TemporaryCredentialsRequestHeader;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
public class OAuthUtils {

    public static String generateNonce() {
        return Base64.getEncoder()
                .encodeToString(String.valueOf(UUID.randomUUID()).getBytes());
    }

    public static String generateSignature(TemporaryCredentialsRequestHeader tcHeader, String consumerSecret, String tokenSecret) {
        String key = getUrlEncoded(consumerSecret) + "&" + getUrlEncoded(tokenSecret);
        String baseString = generateBaseString(tcHeader);
        try {
            final SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF-8"), OAuthConstants.HMAC_SHA1_ALGORITHM_NAME);
            final Mac mac = Mac.getInstance(OAuthConstants.HMAC_SHA1_ALGORITHM_NAME);
            mac.init(signingKey);
            return Base64.getEncoder().encodeToString(mac.doFinal(baseString.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateBaseString(TemporaryCredentialsRequestHeader tcHeader) {
        String httpMethod = tcHeader.getHttpMethod();
        String baseUri = getBaseStringUri(tcHeader);
        String requestParameters = getRequestParameters(tcHeader);

        final StringBuilder sb = new StringBuilder();
        sb.append(httpMethod)
                .append('&').append(getUrlEncoded(baseUri))
                .append('&').append(getUrlEncoded(requestParameters));

        return sb.toString();
    }

    private static String getBaseStringUri(TemporaryCredentialsRequestHeader tcHeader) {
        return tcHeader.getTemporaryCredentialsUrl();
    }

    private static String getRequestParameters(TemporaryCredentialsRequestHeader tcHeader) {
        return getNormalizedParameters(getParameterSources(tcHeader));
    }

    private static Map<String, List<String>> getParameterSources(TemporaryCredentialsRequestHeader tcHeader) {
        final Map<String, List<String>> paramSources = new HashMap<>();

        final String queryString = tcHeader.getQueryString();
        kvToMultiValueMap(paramSources, queryString);

        putMultiValue(paramSources, OAuthConstants.OAUTH_CONSUMER_KEY, tcHeader.getOauthConsumerKey());
        putMultiValue(paramSources, OAuthConstants.OAUTH_SIGNATURE_METHOD, tcHeader.getOauthSignatureMethod());
        putMultiValue(paramSources, OAuthConstants.OAUTH_TIMESTAMP, tcHeader.getOauthTimestamp());
        putMultiValue(paramSources, OAuthConstants.OAUTH_NONCE, tcHeader.getOauthNonce());
        putMultiValue(paramSources, OAuthConstants.OAUTH_CALLBACK, tcHeader.getOauthCallbackUrl());
        putMultiValue(paramSources, OAuthConstants.OAUTH_VERSION, tcHeader.getOauthVersion());

        final String requestBody = tcHeader.getRequestBody();
        kvToMultiValueMap(paramSources, requestBody);

        return paramSources;
    }

    private static void kvToMultiValueMap(Map<String, List<String>> paramSources, String queryString) {
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

    private static void putMultiValue(Map<String, List<String>> map, String key, String value) {
        if (map.get(key) == null) {
            final ArrayList<String> values = new ArrayList<>();
            values.add(value);
            map.put(key, values);
        } else {
            map.get(key).add(value);
        }
    }

    private static String getNormalizedParameters(Map<String, List<String>> parameterSources) {
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

    public static String getUrlDecoded(String encoded) {
        try {
            return URLDecoder.decode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUrlEncoded(String decoded) {
        try {
            return URLEncoder.encode(decoded, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("%21", "!")
                    .replaceAll("%27", "'")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static TemporaryCredentials getTemporaryCredentialsFrom(String i) {
        if (i == null || i.isEmpty()) return null;
        final TemporaryCredentials o = new TemporaryCredentials();
        String[] elements = i.split("&");
        for (String element : elements) {
            String[] kv = element.split("=");
            if (OAuthConstants.OAUTH_TOKEN.equals(kv[0].toLowerCase()))
                o.setOauthToken(kv[1]);
            if (OAuthConstants.OAUTH_TOKEN_SECRET.equals(kv[0].toLowerCase()))
                o.setOauthTokenSecret(kv[1]);
            if (OAuthConstants.OAUTH_CALLBACK_CONFIRMED.equals(kv[0].toLowerCase()))
                o.setOauthCallbackConfirmed(Boolean.valueOf(kv[1]));
        }
        return o;
    }
}
