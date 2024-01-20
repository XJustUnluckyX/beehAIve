package it.unisa.c10.beehAIve.controller.gestioneUtente.gestioneProfilo;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.service.gestioneUtente.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

@Controller
public class ProfileController {
  final private ProfileService profileService;

  @Autowired
  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @PostMapping("/changeInfo")
  public String changeInfo(@RequestParam String firstName, @RequestParam String lastName,
                           @RequestParam String companyName, HttpSession session, Model model) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sul formato del nome
    if (!profileService.regexFirstName(firstName)) {
      model.addAttribute("error", "First Name must start with capital " +
          "letter and cannot contain special symbols except for ' and - .");
      return "user-page";
    }

    // Controllo sulla lunghezza del nome
    if (firstName.length() < 2) {
      model.addAttribute("error", "First name too short.");
      return "user-page";
    } else if (firstName.length() > 15) {
      model.addAttribute("error", "First name too long.");
      return "user-page";
    }

    // Controllo sul formato del cognome
    if (!profileService.regexLastName(lastName)) {
      model.addAttribute("error", "Last Name must start with capital " +
          "letter and cannot contain special symbols except for ' and - .");
      return "user-page";
    }

    // Controllo sulla lunghezza del cognome
    if (lastName.length() < 2) {
      model.addAttribute("error", "Last name too short.");
      return "user-page";
    } else if (lastName.length() > 15) {
      model.addAttribute("error", "Last name too long.");
      return "user-page";
    }

    // Controllo sul formato del nome della compagnia
    if (!profileService.regexCompanyName(companyName)) {
      model.addAttribute("error", "Company Name must start with capital " +
          "letter and cannot contain special symbols except for ' and - .");
      return "user-page";
    }

    // Controllo sulla lunghezza del nome della compagnia
    if (companyName.length() < 2) {
      model.addAttribute("error", "Company name too short.");
      return "user-page";
    } else if (companyName.length() > 100) {
      model.addAttribute("error", "Company name too long.");
      return "user-page";
    }

    beekeeper.setFirstName(firstName);
    beekeeper.setLastName(lastName);
    beekeeper.setCompanyName(companyName);

    profileService.changeInfo(beekeeper.getEmail(), firstName, lastName, companyName);

    session.setAttribute("beekeeper", beekeeper);

    return "user-page";
  }

  @RequestMapping("/request")
  public ModelAndView getRequest(@ModelAttribute Beekeeper user, ModelAndView mv,
                                 HttpServletRequest request) {
    mv.addObject("reset_user_email", user.getEmail());

    WebUtils.setSessionAttribute(request, "reset_user_email", user.getEmail());
    String resetUserEmail = (String) WebUtils.getSessionAttribute(request, "reset_user_email");
    return mv;
  }

  @PostMapping("/changePassword")
  public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
                               @RequestParam String confirmNewPassword, Model model,
                               HttpSession session) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    String beekeeperEmail = beekeeper.getEmail();

    if(!profileService.userExists(beekeeperEmail, oldPassword)) {
      model.addAttribute("error", "Old password is incorrect.");
      return "user-page";
    }

    if(!(newPassword.equals(confirmNewPassword))) {
      model.addAttribute("error", "The new passwords don't match.");
      return "user-page";
    }

    profileService.changePassword(beekeeperEmail, newPassword);
    return "user-page";
  }
}