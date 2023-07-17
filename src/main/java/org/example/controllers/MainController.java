package org.example.controllers;

import org.example.service.LinkService;
import org.example.model.Link;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String createLink(@AuthenticationPrincipal UserDetails userDetails,Link newLink){
        linkService.saveByUser(newLink,userDetails);
        return "redirect:/";
    }
    @RequestMapping(value = "/{id}/disable",method = RequestMethod.PUT)
    public String disableLink(@PathVariable String id,@AuthenticationPrincipal UserDetails userDetails){
        linkService.disableLink(id,userDetails);
        return "redirect:/";
    }
    @RequestMapping(value = "/{id}/enable",method = RequestMethod.PUT)
    public String enableLink(@PathVariable String id,@AuthenticationPrincipal UserDetails userDetails){
        linkService.enableLink(id,userDetails);
        return "redirect:/";
    }
}
