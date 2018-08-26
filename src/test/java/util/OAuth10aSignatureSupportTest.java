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
                "YourAppConsumerKey",
                "YourAppConsumerSecret",
                "YourAppCallbackURL"
        );
        header.setOauthNonce("NDg0ZDNjOTktYTJlMC00YmI5LThhMDktZDBkZGQ0MDA0ZTIw");
        header.setOauthTimestamp("1535288634");


        // when
        oa10aSigSupport.fillSignature(header);


        // then
        assertThat(header.getOauthSignature()).isEqualTo("DNpRbry9XwYfEf+KXz4tV5Ufbpk=");
    }

    @Test
    public void accessToken__sigTest() {
        // given
        OAuth10aTokenCredentialsRequestHeader header = new OAuth10aTokenCredentialsRequestHeader(
                "https://api.twitter.com/oauth/access_token",
                "YourAppConsumerKey",
                "YourAppConsumerSecret",
                "YourRequestToken",
                "YourRequestTokenSecret",
                "YourOAuthVerifier"
        );
        header.setOauthNonce("ZmRmNDQ5Y2YtN2IwNC00YzFkLTgxODItN2YwZmEzYjRhZTJj");
        header.setOauthTimestamp("1535289096");


        // when
        oa10aSigSupport.fillSignature(header);


        // then
        assertThat(header.getOauthSignature()).isEqualTo("64lbyOhFJRcmudwWSwrmL1cQhEQ=");
    }

    @Test
    public void protectedResources__sigTest() {
        // given
        NextAction nextAction = new NextAction(
                HttpMethod.POST,
                "https://api.twitter.com/1.1/statuses/update.json?status=OAuth10a%20Test",
                null
        );

        OAuth10aProtectedResourcesRequestHeader header =
                new OAuth10aProtectedResourcesRequestHeader(
                        nextAction,
                        "YourAppConsumerKey",
                        "YourAppConsumerSecret",
                        "YourAccessToken",
                        "YourAccessTokenSecret"
                );
        header.setOauthTimestamp("1535272771");
        header.setOauthNonce("Y2I0Yjk4ZDItZjg2OS00Y2VjLThkMjgtY2RmMWY0YzZiOTlj");


        // when
        oa10aSigSupport.fillSignature(header);


        // then
        assertThat(header.getOauthSignature()).isEqualTo("de5B57uqqbMG/Z/6vm5i5kJaxxA=");
    }
}
