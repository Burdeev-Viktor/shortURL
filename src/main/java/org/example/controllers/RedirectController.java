package org.example.controllers;

import org.example.Service.LinkSQLService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RedirectController {
    private final LinkSQLService linkSQLService;

    public RedirectController(LinkSQLService linkSQLService) {
        this.linkSQLService = linkSQLService;
    }

    @RequestMapping(value = "r/{id}",method = RequestMethod.GET)
    public String redirect(@PathVariable String id){
        StopWatch watch = new StopWatch();
        watch.start();
        String link = linkSQLService.findFastLink(id);
        watch.stop();
        System.out.println("Total execution time: "
                + watch.getTotalTimeMillis());
        return "redirect:" + link;
    }
}
