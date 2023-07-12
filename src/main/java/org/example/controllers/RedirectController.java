package org.example.controllers;

import org.example.Service.LinkService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RedirectController {
    private final LinkService linkService;

    public RedirectController(LinkService linkService) {
        this.linkService = linkService;
    }

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
