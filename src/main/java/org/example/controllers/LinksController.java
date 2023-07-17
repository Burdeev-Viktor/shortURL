package org.example.controllers;

import org.example.service.LinkService;
import org.example.model.Link;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LinksController {
    private final LinkService linkService;

    public LinksController(LinkService linkService) {
        this.linkService = linkService;
    }

    @RequestMapping(value = "/create-link", method = RequestMethod.GET)
    public String createLink(Model model) {
        model.addAttribute("link", new Link());
        return "link";
    }

    @RequestMapping(value = "/create-link", method = RequestMethod.POST)
    public String saveLink(Model model, Link link) {
        link = linkService.save(link);
        model.addAttribute("link", link);
        return "link";
    }
}
