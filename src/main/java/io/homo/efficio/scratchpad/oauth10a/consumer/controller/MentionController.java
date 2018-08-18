package io.homo.efficio.scratchpad.oauth10a.consumer.controller;

import io.homo.efficio.scratchpad.oauth10a.consumer.domain.Mention;
import io.homo.efficio.scratchpad.oauth10a.consumer.domain.TemporaryCredentialsRequestHeader;
import io.homo.efficio.scratchpad.oauth10a.consumer.util.OAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class MentionController {

    @Value("${oauth10a.consumer.key}")
    private String consumerKey;

    @Value("${oauth10a.consumer.secret}")
    private String consumerSecret;

    @GetMapping
    public ModelAndView showMentionForm(ModelAndView mav) {
        mav.setViewName("mentionForm");
        return mav;
    }

    @PostMapping
    @ResponseBody
    public String writeToTwitter(HttpServletRequest request, Mention mention) {
        final TemporaryCredentialsRequestHeader tcHeader
                = new TemporaryCredentialsRequestHeader(request, this.consumerKey);
        String signature = OAuthUtils.generateSignature(tcHeader, this.consumerSecret, "");
        log.info("### mention: " + mention.getMention());
        log.info("### signature: " + signature);
        return "redirect:" + "https://www.daum.net";
    }
}
