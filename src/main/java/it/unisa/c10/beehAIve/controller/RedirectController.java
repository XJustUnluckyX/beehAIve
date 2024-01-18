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
  /*
  @GetMapping("/login-page")
  public String login (Model model) {
    return "login-page";
  }
  */
  /*
  @GetMapping("/registration-page")
  public String register (Model model) {
    return "registration-page";
  }
  */
  @GetMapping("/subscription-page")
  public String subscription (Model model) { return "subscription-page";}
  @GetMapping("/creation-hive")
  public String creation (Model model) { return "hive/creation-hive";}
  @GetMapping("/state-hive")
  public String state (Model model) { return "hive/state-hive";}
  @GetMapping("/parameters-hive")
  public String parameter (Model model) { return "hive/parameters-hive";}
  @GetMapping("/operations-hive")
  public String operation (Model model) { return "hive/operations-hive";}
  @GetMapping("/contact-us")
  public String contact (Model model) { return "/contact-us";}
  @GetMapping("/about-us")
  public String about (Model model) { return "/about-us";}
  @GetMapping("/sensor-spec")
  public String sensor (Model model) { return "/sensor-spec";}
  @GetMapping("/user-page")
  public String user (Model model) { return "/user-page";}
}
