package io.homo.efficio.scratchpad.oauth10a.consumer.util;

import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-19.
 */
public class EncodingUtils {

    public static String getPercentDecoded(String value) {
        return UriUtils.decode(value, StandardCharsets.UTF_8);
    }

    public static String getPercentEncoded(String value) {
        return UriUtils.encode(value, StandardCharsets.UTF_8);
    }
}
