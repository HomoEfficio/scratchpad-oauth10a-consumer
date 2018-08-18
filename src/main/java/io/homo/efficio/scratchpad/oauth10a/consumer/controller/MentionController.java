package io.homo.efficio.scratchpad.oauth10a.consumer.controller;

import io.homo.efficio.scratchpad.oauth10a.consumer.domain.Mention;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.TemporaryCredentialsRequestHeader;
import io.homo.efficio.scratchpad.oauth10a.consumer.service.TwitterService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
@Controller
@RequestMapping("/mentions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MentionController {

    @Value("${oauth10a.provider.temporary.credentials.url}")
    private String temporaryCredentialsUrl;

    @Value("${oauth10a.consumer.key}")
    private String consumerKey;

    @Value("${oauth10a.consumer.secret}")
    private String consumerSecret;

    @Value("${oauth10a.consumer.callback.url}")
    private String callbackUrl;

    @NonNull
    private TwitterService twitterService;

    @GetMapping
    public ModelAndView showMentionForm(ModelAndView mav) {
        mav.setViewName("mentionForm");
        return mav;
    }

    @PostMapping
    @ResponseBody
    public String writeToTwitter(HttpServletRequest request, Mention mention) {
        final TemporaryCredentialsRequestHeader tcHeader =
                new TemporaryCredentialsRequestHeader(request, this.temporaryCredentialsUrl, this.consumerKey, this.consumerSecret, this.callbackUrl);
        final ResponseEntity<String> temporaryCredentials =
                this.twitterService.getTemporaryCredentials(tcHeader);
        log.info("###");
        log.info(temporaryCredentials.toString());
        log.info("###");
        return temporaryCredentials.getBody();
//        return "redirect:" + "https://www.daum.net";
    }
}
