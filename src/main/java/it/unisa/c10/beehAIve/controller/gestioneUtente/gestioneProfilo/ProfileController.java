package it.unisa.c10.beehAIve.controller.gestioneUtente.gestioneProfilo;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.service.gestioneUtente.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
  final private ProfileService profileService;

  @Autowired
  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @PostMapping("/changeInfo")
  public String changeInfo(@RequestParam String firstName, @RequestParam String lastName,
                           @RequestParam String companyName, HttpSession session, RedirectAttributes redirectAttributes) {

    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Controllo sul formato del nome
    if (!regexFirstName(firstName)) {
      redirectAttributes.addFlashAttribute("error", "First name must start with capital " +
          "letter and cannot contain special symbols except for ' and - .");
      return "redirect:/user";
    }
    // Controllo sulla lunghezza del nome
    if (firstName.length() < 2) {
      redirectAttributes.addFlashAttribute("error", "First name too short.");
      return "redirect:/user";
    } else if (firstName.length() > 15) {
      redirectAttributes.addFlashAttribute("error", "First name too long.");
      return "redirect:/user";
    }

    // Controllo sul formato del cognome
    if (!regexLastName(lastName)) {
      redirectAttributes.addFlashAttribute("error", "Last Name must start with capital " +
          "letter and cannot contain special symbols except for ' and - .");
      return "redirect:/user";
    }
    // Controllo sulla lunghezza del cognome
    if (lastName.length() < 2) {
      redirectAttributes.addFlashAttribute("error", "Last name too short.");
      return "redirect:/user";
    } else if (lastName.length() > 15) {
      redirectAttributes.addFlashAttribute("error", "Last name too long.");
      return "redirect:/user";
    }

    // Controllo sul formato del nome dell'azienda
    if (!regexCompanyName(companyName)) {
      redirectAttributes.addFlashAttribute("error", "Company Name must start with capital " +
          "letter and cannot contain special symbols except for ' and - .");
      return "redirect:/user";
    }
    // Controllo sulla lunghezza del nome dell'azienda
    if (companyName.length() < 2) {
      redirectAttributes.addFlashAttribute("error", "Company name too short.");
      return "redirect:/user";
    } else if (companyName.length() > 100) {
      redirectAttributes.addFlashAttribute("error", "Company name too long.");
      return "redirect:/user";
    }

    beekeeper.setFirstName(firstName);
    beekeeper.setLastName(lastName);
    beekeeper.setCompanyName(companyName);

    profileService.changeInfo(beekeeper.getEmail(), firstName, lastName, companyName);

    session.setAttribute("beekeeper", beekeeper);

    return "redirect:/user";
  }

  @PostMapping("/changePassword")
  public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
                               @RequestParam String confirmNewPassword, Model model,
                               HttpSession session, RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    String beekeeperEmail = beekeeper.getEmail();

    // Verifica sull'identità dell'apicoltore
    if(!profileService.userExists(beekeeperEmail, oldPassword)) {
      redirectAttributes.addFlashAttribute("error", "Old password is incorrect.");
      return "redirect:/user";
    }

    // Controllo sul formato della password
    if (!regexPassword(newPassword)) {
      redirectAttributes.addFlashAttribute("error", "The password must be at least 8 " +
        "characters long and contain at least one uppercase letter, one lowercase letter, one " +
        "digit, and one special character ( @.$!%*?& ).");
      return "redirect:/user";
    }
    // Controllo sulla lunghezza massima della password
    if (newPassword.length() > 100) {
      redirectAttributes.addFlashAttribute("error", "Password too long.");
      return "redirect:/user";
    }

    // Controllo sulla corrispondenza tra le due password
    if(!(newPassword.equals(confirmNewPassword))) {
      redirectAttributes.addFlashAttribute("error", "Passwords don't match.");
      return "redirect:/user";
    }

    // Cambiamento della password nel database
    profileService.changePassword(beekeeperEmail, newPassword);

    return "redirect:/user";
  }

  @GetMapping("/user")
  public String user (Model model) {
    return "user";
  }

  private boolean regexPassword (String password) {
    String passwordRegex =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@.$!%*?&_-])[A-Za-z\\d@.$!%*?&_-]+$";
    return password.matches(passwordRegex);
  }

  private boolean regexFirstName (String firstName) {
    String firstNameRegex = "^[A-Z][a-z'-]*(?: [A-Z][a-z'-]+)*$";
    return firstName.matches(firstNameRegex);
  }

  private boolean regexLastName (String lastName) {
    String lastNameRegex = "^[A-Z][a-z'-]*(?: [A-Z][a-z'-]+)*$";
    return lastName.matches(lastNameRegex);
  }

  private boolean regexCompanyName (String companyName) {
    String companyRegex = "^[a-zA-Z0-9\\s.'\",&()-]+$";
    return companyName.matches(companyRegex);
  }

}