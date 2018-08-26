package util;

import io.homo.efficio.scratchpad.oauth10a.consumer.domain.NextAction;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.header.OAuth10aProtectedResourcesRequestHeader;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.header.OAuth10aTemporaryCredentialRequestHeader;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.header.OAuth10aTokenCredentialsRequestHeader;
import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuth10aSignatureSupport;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author homo.efficio@gmail.com
 * created on 2018-08-26
 */
public class OAuth10aSignatureSupportTest {

    private OAuth10aSignatureSupport oa10aSigSupport = new OAuth10aSignatureSupport();

    @Test
    public void requestToken__sigTest() {
        // given
        OAuth10aTemporaryCredentialRequestHeader header = new OAuth10aTemporaryCredentialRequestHeader(
                "https://api.twitter.com/oauth/request_token",
                "8Y3anpLDzrFEY8a3bHL7NNugL",
                "HoGAE6K7QuV9dJ6WhWhSSziUnXv12GyF78uZEr0g4YKt1WEIV7",
                "http://twitter-app.homoefficio.io:8080/oauth/callback"
        );
        header.setOauthNonce("NDg0ZDNjOTktYTJlMC00YmI5LThhMDktZDBkZGQ0MDA0ZTIw");
        header.setOauthTimestamp("1535288634");


        // when
        oa10aSigSupport.fillSignature(header);


        // then
        assertThat(header.getOauthSignature()).isEqualTo("Gm70xu93zofoeWKoZwDqBxEFNEg=");
    }

    @Test
    public void accessToken__sigTest() {
        // given
        OAuth10aTokenCredentialsRequestHeader header = new OAuth10aTokenCredentialsRequestHeader(
                "https://api.twitter.com/oauth/access_token",
                "8Y3anpLDzrFEY8a3bHL7NNugL",
                "HoGAE6K7QuV9dJ6WhWhSSziUnXv12GyF78uZEr0g4YKt1WEIV7",
                "BxAB4QAAAAAA8Gc3AAABZXZb5kE",
                "YHitp4Iglj1vzxjfqFei5ynn0kWNdurX",
                "Ea7inhOCV5aMm83fwEvepbT6QfuFwDZ9"
        );
        header.setOauthNonce("ZmRmNDQ5Y2YtN2IwNC00YzFkLTgxODItN2YwZmEzYjRhZTJj");
        header.setOauthTimestamp("1535289096");


        // when
        oa10aSigSupport.fillSignature(header);


        // then
        assertThat(header.getOauthSignature()).isEqualTo("aawDX/Lzz5TwLwOHHZy5Q2w2nIw=");
    }

    @Test
    public void protectedResources__sigTest() {
        // given
        NextAction nextAction = new NextAction(
                HttpMethod.POST,
                "https://api.twitter.com/1.1/statuses/update.json?status=Java%20URIEncoder%20sucks",
                null
        );

        OAuth10aProtectedResourcesRequestHeader header =
                new OAuth10aProtectedResourcesRequestHeader(
                        nextAction,
                        "8Y3anpLDzrFEY8a3bHL7NNugL",
                        "HoGAE6K7QuV9dJ6WhWhSSziUnXv12GyF78uZEr0g4YKt1WEIV7",
                        "935307669082009600-W3Hyf5xFYiyGF9P9zmaDGUlFatglZPX",
                        "2XBO7aGEHF5ZtruyNa6yRgA0nk42HNmL94ZHhfXgkcooR"
                );
        header.setOauthTimestamp("1535272771");
        header.setOauthNonce("Y2I0Yjk4ZDItZjg2OS00Y2VjLThkMjgtY2RmMWY0YzZiOTlj");


        // when
        oa10aSigSupport.fillSignature(header);


        // then
        assertThat(header.getOauthSignature()).isEqualTo("BsaZS0ryUutG9rQ6ULHOOs1pI/0=");
    }
}
