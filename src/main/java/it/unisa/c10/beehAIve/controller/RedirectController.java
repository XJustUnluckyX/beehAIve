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
  @GetMapping("/subscription-page")
  public String subscription (Model model) { return "subscription-page";}
  @GetMapping("/contact-us")
  public String contact (Model model) { return "/contact-us";}
  @GetMapping("/about-us")
  public String about (Model model) { return "/about-us";}
  @GetMapping("/sensor-spec")
  public String sensor (Model model) { return "/sensor-spec";}
}
