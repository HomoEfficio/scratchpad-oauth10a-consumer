package io.homo.efficio.scratchpad.oauth10a.consumer.controller;

import io.homo.efficio.scratchpad.oauth10a.consumer.domain.*;
import io.homo.efficio.scratchpad.oauth10a.consumer.service.TwitterService;
import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuthConstants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    public String writeToTwitter(HttpServletRequest request, Mention mention) {
        final OAuthRequestHeader tcHeader =
                new OAuthRequestHeader(request, this.temporaryCredentialsUrl, this.consumerKey, this.consumerSecret, this.callbackUrl);
        final ResponseEntity<TemporaryCredentials> response =
                this.twitterService.getTemporaryCredentials(tcHeader);
        final TemporaryCredentials temporaryCredentials = response.getBody();

        HttpSession session = request.getSession();
        session.setAttribute("RTS", temporaryCredentials.getOauthTokenSecret());

        return "redirect:" + this.authorizeUrl + "?" +
                OAuthConstants.OAUTH_TOKEN + "=" + temporaryCredentials.getOauthToken();
    }

    @GetMapping("/callback")
    @ResponseBody
    public String requestAccessToken(HttpServletRequest request, VerifierResponse verifierResponse) {
        HttpSession session = request.getSession();
        final String rts = (String) session.getAttribute("RTS");
        final OAuthRequestHeader tcHeader =
                new OAuthRequestHeader(request, this.tokenCredentialsUrl, this.consumerKey, this.consumerSecret, this.callbackUrl,
                        verifierResponse.getOauth_token(), rts, verifierResponse.getOauth_verifier());
        final ResponseEntity<TokenCredentials> tokenCredentials =
                this.twitterService.getTokenCredentials(tcHeader);

        return tokenCredentials.getBody().toString();
    }
}
