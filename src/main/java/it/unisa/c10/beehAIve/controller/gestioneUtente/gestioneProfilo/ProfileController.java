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

  /**
   * Gestisce le richieste GET per reindirizzare l'apicoltore al proprio profilo.
   * @return Una stringa rappresentante l'URL di reindirizzamento al profilo dell'apicoltore.
   */
  @GetMapping("/user")
  public String user () {
    return "user";
  }

  /**
   * Gestisce le richieste POST per permettere all'apicoltore di modificare le proprie informazioni.
   * @param firstName Il nuovo nome dell'apicoltore.
   * @param lastName Il nuovo cognome dell'apicoltore.
   * @param companyName Il nuovo nome dell'azienda dell'apicoltore.
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper} dalla
   *                sessione
   * @param redirectAttributes Un oggetto {@code RedirectAttributes} per trasferire messaggi di
   *                           risposta.
   * @return Una stringa rappresentante l'URL di reindirizzamento alla modifica delle informazioni
   *         dell'apicoltore.
   * @see ProfileService#changeInfo(String, String, String, String)
   */
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

    redirectAttributes.addFlashAttribute("success", "Operation completed successfully");

    return "redirect:/user";
  }

  /**
   * Gestisce le richieste POST per permettere all'apicoltore di modificare la propria password.
   * @param oldPassword La vecchia password dell'utente, per verificare la sua identità.
   * @param newPassword La nuova password dell'utente.
   * @param confirmNewPassword Conferma della nuova password.
   * @param model Un oggetto {@code Model} per aggiungere attributi alla risposta.
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper} dalla
   *                sessione
   * @param redirectAttributes Un oggetto {@code RedirectAttributes} per trasferire messaggi di
   *                           risposta
   * @return Una stringa rappresentante l'URL di reindirizzamento alla modifica della password
   *         dell'apicoltore.
   * @see ProfileService#changePassword(String, String) 
   */
  @PostMapping("/changePassword")
  public String changePassword(
      @RequestParam String oldPassword,
      @RequestParam String newPassword,
      @RequestParam String confirmNewPassword,
      Model model,
      HttpSession session,
      RedirectAttributes redirectAttributes) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");
    String beekeeperEmail = beekeeper.getEmail();

    // Verifica sull'identità dell'apicoltore
    if(!profileService.userExists(beekeeperEmail, oldPassword)) {
      redirectAttributes.addFlashAttribute("error", "Old password is incorrect.");
      return "redirect:/user";
    }

    // Controllo sulla lunghezza della password
    if (newPassword.length() < 8) {
      redirectAttributes.addFlashAttribute("error", "Password too short.");
      return "redirect:/user";
    }
    if (newPassword.length() > 100) {
      redirectAttributes.addFlashAttribute("error", "Password too long.");
      return "redirect:/user";
    }

    // Controllo sul formato della password
    if (!regexPassword(newPassword)) {
      redirectAttributes.addFlashAttribute("error", "The password must be at least 8 " +
        "characters long and contain at least one uppercase letter, one lowercase letter, one " +
        "digit, and one special character ( @.$!%*?&_- ).");
      return "redirect:/user";
    }

    // Controllo sulla corrispondenza tra le due password
    if(!(newPassword.equals(confirmNewPassword))) {
      redirectAttributes.addFlashAttribute("error", "Passwords don't match.");
      return "redirect:/user";
    }

    // Cambiamento della password nel database
    profileService.changePassword(beekeeperEmail, newPassword);

    redirectAttributes.addFlashAttribute("success", "Operation completed successfully");

    return "redirect:/user";
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