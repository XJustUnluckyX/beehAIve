package it.unisa.c10.beehAIve.controller.gestioneUtente.gestioneProfilo;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.service.gestioneUtente.ProfileService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Gestisce la modifica dei dati personali, la modifica della password ed eventuali altre operazioni sul Profilo
// come modificare il metodo di pagamento

@Controller
@SessionAttributes("beekeeper")
public class ProfileController {

  final private ProfileService profileService;
  final private  AccessController accessController;
  @Autowired
  public ProfileController(ProfileService profileService, AccessController accessController) {
    this.profileService = profileService;
    this.accessController = accessController;
  }

  @PostMapping("/changeInfo")
  public String changeInfo(@RequestParam String firstName, @RequestParam String lastName,
                           @RequestParam String companyName, HttpSession session, Model model) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sul formato del nome
    if (!accessController.regexFirstName(firstName)) {
      model.addAttribute("error", "First Name cannot contain special " +
          "symbols except for ' and - .");
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
    if (!accessController.regexLastName(lastName)) {
      model.addAttribute("error", "Last name cannot contain special " +
          "symbols except for ' and - .");
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
    if (!accessController.regexCompanyName(companyName)) {
      model.addAttribute("error", "Company name cannot contain special " +
          "symbols except for ' and - .");
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

    beekeeper = profileService.changeInfo(beekeeper.getEmail(), firstName, lastName, companyName);

    session.setAttribute("firstName",beekeeper.getFirstName());
    session.setAttribute("lastName",beekeeper.getLastName());
    session.setAttribute("companyName",beekeeper.getCompanyName());

    return "user-page";
  }

  @RequestMapping("/request")
  public ModelAndView getRequest(@ModelAttribute Beekeeper user, ModelAndView mv, HttpServletRequest request) {
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

    if(!profileService.userExists(beekeeper.getEmail(), oldPassword)) {
      model.addAttribute("error", "Old password is incorrect.");
      return "user-page";
    }

    if(!(newPassword.equals(confirmNewPassword))) {
      model.addAttribute("error", "The new passwords don't match.");
      return "user-page";
    }

    profileService.changePassword(beekeeper.getEmail(), newPassword);
    return "user-page";
  }
}
