package org.example.controllers;

import org.example.Const;
import org.example.Service.LinkSQLService;
import org.example.model.Link;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Random;

@Controller
public class Links {
    private final LinkSQLService linkSQLService;

    public Links(LinkSQLService linkSQLService) {
        this.linkSQLService = linkSQLService;
    }


    @RequestMapping(value = "/create-link",method = RequestMethod.GET)
    public String createLink(Model model){
    model.addAttribute("link",new Link());
    return "link";
    }
    @RequestMapping(value = "/create-link",method = RequestMethod.POST)
    public String saveLink(Model model,Link link){
    link = linkSQLService.create(link);
    System.out.println(link.getOrigin());
        model.addAttribute("link",link);
        return "link";
    }
}
