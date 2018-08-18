package io.homo.efficio.scratchpad.oauth10a.consumer.controller;

import io.homo.efficio.scratchpad.oauth10a.consumer.domain.Mention;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author homo.efficio@gmail.com
 * Created on 2018-08-18.
 */
@Controller
@RequestMapping("/mentions")
@Slf4j
public class MentionController {

    @GetMapping
    public ModelAndView showMentionForm(ModelAndView mav) {
        mav.setViewName("mentionForm");
        return mav;
    }

    @PostMapping
    public String writeToTwitter(Mention mention) {
        log.info("### mention: " + mention.getMention());
        return "redirect:" + "https://www.daum.net";
    }
}
