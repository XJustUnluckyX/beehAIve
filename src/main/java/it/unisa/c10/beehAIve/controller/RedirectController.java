package it.unisa.c10.beehAIve.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

  @GetMapping("")
  public String home (Model model) {
    return "index";
  }
//  @GetMapping("/login-page")
//  public String login (Model model) {
//    return "login-page";
//  }
//  @GetMapping("/registration-page")
//  public String register (Model model) {
//    return "registration-page";
//  }
  @GetMapping("/subscription-page")
  public String subscription (Model model) { return "subscription-page";}
  @GetMapping("/creation-hive")
  public String creation (Model model) { return "hive/creation-hive";}
  @GetMapping("/parameters-hive")
  public String parameter (Model model) { return "hive/parameters-hive";}
  @GetMapping("/contact-us")
  public String contact (Model model) { return "/contact-us";}
  @GetMapping("/about-us")
  public String about (Model model) { return "/about-us";}
  @GetMapping("/sensor-spec")
  public String sensor (Model model) { return "/sensor-spec";}

  @GetMapping("/error404")
  public String er404 (Model model) { return "error/404";}
  @GetMapping("/error500")
  public String er500 (Model model) { return "error/500";}
  @GetMapping("/subscription-test")
  public String subscriptionTest(Model model) {return "subscription-test";}

  @GetMapping("/user-page")
  public String user (Model model) {
    return "/user-page";
  }

  @GetMapping("/home")
  public String homePage (Model model) {
    return "/index";
  }

  @GetMapping("/calendar-planning")
  public String calendarTest (Model model) {
    return "calendar-page";
  }
}
