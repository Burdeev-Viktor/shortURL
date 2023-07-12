package org.example.controllers;

import org.example.service.LinkService;
import org.example.model.Link;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;

@Controller
public class MainController {
    private final LinkService linkService;

    public MainController(LinkService linkService) {
        this.linkService = linkService;
    }

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String signIn(@AuthenticationPrincipal UserDetails userDetails,Model model){
        List<Link> links = linkService.findLinksByUser(userDetails);
        model.addAttribute("links",links);
        model.addAttribute("newLink",new Link());
        return "main";
    }
    @RequestMapping(value = "/user/create-link",method = RequestMethod.POST)
    public String createLink(@AuthenticationPrincipal UserDetails userDetails,Model model,Link newLink){
        linkService.saveByUser(newLink,userDetails);
        return "redirect:/";
    }
}
