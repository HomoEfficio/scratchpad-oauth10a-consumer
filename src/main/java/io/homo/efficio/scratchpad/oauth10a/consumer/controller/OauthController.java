package io.homo.efficio.scratchpad.oauth10a.consumer.controller;

import io.homo.efficio.scratchpad.oauth10a.consumer.domain.Mention;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.NextAction;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.VerifierResponse;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.credentials.TemporaryCredentials;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.credentials.TokenCredentials;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.header.AbstractOAuth10aRequestHeader;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.header.OAuth10aProtectedResourcesRequestHeader;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.header.OAuth10aTemporaryCredentialRequestHeader;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.header.OAuth10aTokenCredentialsRequestHeader;
import io.homo.efficio.scratchpad.oauth10a.consumer.service.TwitterService;
import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuth10aConstants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.Objects;

import static io.homo.efficio.scratchpad.oauth10a.consumer.util.EncodingUtils.getPercentEncoded;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class OauthController {

    @Value("${oauth10a.provider.temporary.credentials.url}")
    private String temporaryCredentialsUrl;

    @Value("${oauth10a.provider.authorize.url}")
    private String authorizeUrl;

    @Value("${oauth10a.provider.token.credentials.url}")
    private String tokenCredentialsUrl;

    @Value("${oauth10a.provider.post.url}")
    private String postUrl;

    @Value("${oauth10a.consumer.key}")
    private String consumerKey;

    @Value("${oauth10a.consumer.secret}")
    private String consumerSecret;

    @Value("${oauth10a.consumer.callback.url}")
    private String callbackUrl;

    @NonNull
    private TwitterService twitterService;

    @GetMapping("/mentions")
    public ModelAndView showMentionForm(ModelAndView mav) {
        mav.setViewName("mentionForm");
        return mav;
    }

    @PostMapping("/mentions")
    public String requestTemporaryCredentials(HttpServletRequest request, Mention mention) {
        final AbstractOAuth10aRequestHeader tcHeader =
                new OAuth10aTemporaryCredentialRequestHeader(this.temporaryCredentialsUrl, this.consumerKey, this.consumerSecret, this.callbackUrl);

        final ResponseEntity<TemporaryCredentials> response =
                this.twitterService.getCredentials(tcHeader, TemporaryCredentials.class);
        final TemporaryCredentials temporaryCredentials = response.getBody();

        HttpSession session = request.getSession();
        session.setAttribute(OAuth10aConstants.NEXT_ACTION,
                // https://developer.twitter.com/en/docs/tweets/post-and-engage/api-reference/post-statuses-update
                // 에 따라 POST로 보내더라도 mention 내용을 queryString으로 붙여보내야만 정상 동작함
                new NextAction(HttpMethod.POST, postUrl + "?status=" + getPercentEncoded(mention.getMention()), null));
//                new NextAction(HttpMethod.POST, postUrl, "status=" + getPercentEncoded(mention.getMention())));
        // RequestTokenSecret is better be stored in cache like Redis
        // If it is to be stored in the session, it needs to be encrypted
        session.setAttribute("RTS", temporaryCredentials.getOauth_token_secret());

        return "redirect:" + this.authorizeUrl + "?" +
                OAuth10aConstants.OAUTH_TOKEN + "=" + temporaryCredentials.getOauth_token();
    }

    @GetMapping("/callback")
    @ResponseBody
    public String requestTokenCredentials(HttpServletRequest request, VerifierResponse verifierResponse) {
        HttpSession session = request.getSession();
        // RequestTokenSecret is better be fetched from cache like Redis
        // If it is to be read from the session, it needs to be decrypted
        final String requestTokenSecret = (String) session.getAttribute("RTS");
        final AbstractOAuth10aRequestHeader tcHeader =
                new OAuth10aTokenCredentialsRequestHeader(
                        this.tokenCredentialsUrl,
                        this.consumerKey,
                        this.consumerSecret,
                        verifierResponse.getOauth_token(),
                        requestTokenSecret,
                        verifierResponse.getOauth_verifier());
        final ResponseEntity<TokenCredentials> responseEntity =
                this.twitterService.getCredentials(tcHeader, TokenCredentials.class);

        log.info("access_token: {}", Objects.requireNonNull(responseEntity.getBody()).toString());

        final NextAction nextAction = (NextAction) session.getAttribute(OAuth10aConstants.NEXT_ACTION);
        log.info("nextAction: {}", nextAction);
        final URI nextUri = nextAction.getUri();
        final OAuth10aProtectedResourcesRequestHeader resourcesRequestHeader =
                new OAuth10aProtectedResourcesRequestHeader(
                        nextAction,
                        this.consumerKey,
                        this.consumerSecret,
                        responseEntity.getBody().getOauth_token(),
                        responseEntity.getBody().getOauth_token_secret());
        log.info("OAuth10aProtectedResourcesRequestHeader: {}", resourcesRequestHeader);
        final ResponseEntity<Object> actionResponseEntity =
                this.twitterService.doNextAction(resourcesRequestHeader, nextAction);

        if (actionResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return "Mention is written!!!";
        } else {
            return actionResponseEntity.toString();
        }
    }
}
