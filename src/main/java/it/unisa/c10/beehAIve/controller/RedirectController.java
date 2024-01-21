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
  @GetMapping("/subscription_plans")
  public String subscription (Model model) { return "subscription_plans";}
  @GetMapping("/contact_us")
  public String contact (Model model) { return "/contact_us";}
  @GetMapping("/about_us")
  public String about (Model model) { return "/about_us";}
  @GetMapping("/sensors")
  public String sensor (Model model) { return "/sensors";}
}
