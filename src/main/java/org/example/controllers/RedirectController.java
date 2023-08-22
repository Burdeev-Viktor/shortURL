package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.service.LinkService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
public class RedirectController {
    private final LinkService linkService;

    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    @RequestMapping(value = "r/{id}",method = RequestMethod.GET)
    public String redirect(@PathVariable String id){
        StopWatch watch = new StopWatch();
        watch.start();
        String link = linkService.findFastLink(id);
        watch.stop();
        System.out.println("Total execution time: "
                + watch.getTotalTimeMillis());
        return "redirect:" + link;
    }
}
