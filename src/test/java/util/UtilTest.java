package util;

import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuth10aConstants;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
public class UtilTest {

    @Test
    public void base64encode() {
        String random = String.valueOf(UUID.randomUUID());
        String base64Encoded = Base64.getEncoder().encodeToString(random.getBytes());
        System.out.println(random);
        System.out.println(base64Encoded);
        System.out.println(base64Encoded.length());
        System.out.println("kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg".length());
    }

    @Test
    public void getHost80() {
        String serverName = "www.daum.net";
        int serverPort = 80;
        String lowercaseHost = serverName.trim().toLowerCase();
        String host = null;
        if (serverPort == 80) {
            host = lowercaseHost;
        } else {
            host = lowercaseHost + ":" + serverPort;
        }

        assertThat(host).isEqualTo("www.daum.net");
    }

    @Test
    public void getHostNon80() {
        String serverName = "www.daum.net";
        int serverPort = 8080;
        String lowercaseHost = serverName.trim().toLowerCase();
        String host = null;
        if (serverPort == 80) {
            host = lowercaseHost;
        } else {
            host = lowercaseHost + ":" + serverPort;
        }
        assertThat(host).isEqualTo("www.daum.net:8080");
    }

    @Test
    public void urlDecode() throws UnsupportedEncodingException {
        String url = "b5=%3D%253D&a3=a&c%40=&a2=r%20b";
        final String decoded = URLDecoder.decode(url, "UTF-8");

        assertThat(decoded).isEqualTo("b5==%3D&a3=a&c@=&a2=r b");
    }

    @Test
    public void baseUri() {
        String scheme = "http";
        String lowercaseServerName = "example.com";
        int serverPort = 80;
        String requestUri = "/request";

        String baseUri = serverPort == 80
                ? scheme + "://" + lowercaseServerName + requestUri
                : scheme + "://" + lowercaseServerName + ":" + serverPort + requestUri;

        assertThat(baseUri).isEqualTo("http://example.com/request");
    }

    @Test
    public void split() {
        String test = "=3";
        String[] kv = test.split(":");
        assertThat(kv.length).isEqualTo(1);
    }

    @Test
    public void linkedHashMap() {
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("a3", "2 a");
        map.put("a3", "a");
        map.put("a7", "3");

    }

    @Test
    public void decodingPlus() {
        final String decoded = getUrlDecoded("2+q");

        assertThat(decoded).isEqualTo("2 q");
    }

    @Test
    public void encodingSpace() {
        final String encoded = getUrlEncoded("2 q");

        assertThat(encoded).isEqualTo("2%20q");
    }

    @Test
    public void getParameterSources() {
        final Map<String, List<String>> paramSources = new HashMap<>();
        final String queryString = "b5=%3D%253D&a3=a&c%40=&a2=r%20b";
        kvToMultiValueMap(paramSources, queryString);

        putMultiValue(paramSources, OAuth10aConstants.OAUTH_CONSUMER_KEY, "9djdj82h48djs9d2");
        putMultiValue(paramSources, OAuth10aConstants.OAUTH_TOKEN, "kkk9d7dh3k39sjv7");
        putMultiValue(paramSources, OAuth10aConstants.OAUTH_SIGNATURE_METHOD, "HMAC-SHA1");
        putMultiValue(paramSources, OAuth10aConstants.OAUTH_TIMESTAMP, "137131201");
        putMultiValue(paramSources, OAuth10aConstants.OAUTH_NONCE, "7d8f3e4a");

        final String requestBody = "c2&a3=2+q";
        kvToMultiValueMap(paramSources, requestBody);



        SortedMap<String, List<String>> normalizedParametersMap = new TreeMap<>();
        final Set<Map.Entry<String, List<String>>> entries = paramSources.entrySet();
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
        String normalizedParameters = sb.toString().substring(1);

        String httpMethod = "POST";
        String baseUri = "http://example.com/request";
        String requestParameters = normalizedParameters;

        final StringBuilder result = new StringBuilder();
        result.append(httpMethod)
                .append('&').append(getUrlEncoded(baseUri))
                .append('&').append(getUrlEncoded(requestParameters));
//                .append('&').append(requestParameters);

        assertThat(result.toString()).isEqualTo("POST&http%3A%2F%2Fexample.com%2Frequest&a2%3Dr%2520b%26a3%3D2%2520q%26a3%3Da%26b5%3D%253D%25253D%26c%2540%3D%26c2%3D%26oauth_consumer_key%3D9djdj82h48djs9d2%26oauth_nonce%3D7d8f3e4a%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D137131201%26oauth_token%3Dkkk9d7dh3k39sjv7");
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

    private String getUrlDecoded(String encoded) {
        try {
            return URLDecoder.decode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUrlEncoded(String decoded) {
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
}
