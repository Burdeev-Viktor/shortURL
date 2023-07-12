package org.example.controllers;

import org.example.Service.UserService;
import org.example.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/sign-in",method = RequestMethod.GET)
    public String signIn(Model model){
        model.addAttribute("user",new User());
        return "sign-in";
    }

    @RequestMapping(value = "/sign-up",method = RequestMethod.GET)
    public String signUp(Model model){
        System.out.println("dasdas");
        model.addAttribute("newUser",new User());
        return "sign-up";
    }
    @PostMapping("/sign-up")
    public String registration(User user){
        if(userService.userIsExistsByLoginAndPassword(user)){
            return "sign-up";
        }
        user.setName(user.getLogin());
        userService.save(user);
        return "redirect:/";
    }

}
