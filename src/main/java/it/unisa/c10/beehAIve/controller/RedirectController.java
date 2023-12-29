package it.unisa.c10.beehAIve.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

  @GetMapping("")
  public String home (Model model) {
    return "index";
  }
  @GetMapping("/login")
  public String login (Model model) {
    return "login";
  }

}
