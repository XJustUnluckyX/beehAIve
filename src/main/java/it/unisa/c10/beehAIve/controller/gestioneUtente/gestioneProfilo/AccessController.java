package it.unisa.c10.beehAIve.controller.gestioneUtente.gestioneProfilo;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.service.gestioneUtente.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@SessionAttributes("beekeeper")
public class AccessController {
  private ProfileService profileService;
  @Autowired
  public AccessController(ProfileService profileService) {
    this.profileService = profileService;
  }


  //Pagina di Registrazione


  @GetMapping("registration-page")
  public String showRegistrationForm(Model model) {
    return "registration-page";
  }

  private boolean regexEmail (String email) {
    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    return email.matches(emailRegex);
  }

  private boolean regexPassword (String password) {
    String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@.$!%*?&])[A-Za-z\\d@.$!%*?&]{8,}$";
    return password.matches(passwordRegex);
  }

  private boolean regexFirstName (String firstName) {
    String firstNameRegex = "^[A-Z][a-z'-]+(?: [A-Z][a-z'-]+)*$";
    return firstName.matches(firstNameRegex);
  }

  private boolean regexLastName (String lastName) {
    String lastNameRegex = "^[A-Z][a-z'-]+(?: [A-Z][a-z'-]+)*$";
    return lastName.matches(lastNameRegex);
  }

  private boolean regexCompanyName (String companyName) {
    String companyRegex = "^[a-zA-Z0-9\\s.'\",&()-]+$";
    return companyName.matches(companyRegex);
  }

  private boolean regexCompanyPiva (String companyPiva) {
    String companyPivaRegex = "^[\\d-]{9,}$";
    return companyPiva.matches(companyPivaRegex);
  }

  @PostMapping("/registration-page")
  public String registration(@RequestParam String email, @RequestParam String firstName,
                             @RequestParam String lastName, @RequestParam String companyName,
                             @RequestParam String companyPiva, @RequestParam String password,
                             @RequestParam String confirmPassword, Model model, HttpSession session) {

    // Controlli sul formato dei parametri

    // Controllo sul formato dell'email
    if (!regexEmail(email)) {
      model.addAttribute("error", "Invalid email address.");
      return "registration-page";
    }

    // Controllo sul formato del nome
    if (!regexFirstName(firstName)) {
      model.addAttribute("error", "First Name cannot contain special." +
          "symbols except for ' and -.");
      return "registration-page";
    }

    // Controllo sul formato del cognome
    if (!regexLastName(lastName)) {
      model.addAttribute("error", "Last Name cannot contain special " +
          "symbols except for ' and -.");
      return "registration-page";
    }

    // Controllo sul formato del nome della compagnia
    if (!regexCompanyName(companyName)) {
      model.addAttribute("error", "Company Name cannot contain special " +
          "symbols except for ' and -.");
      return "registration-page";
    }

    // Controllo sul formato della P.IVA
    if (!regexCompanyPiva(companyPiva)) {
      model.addAttribute("error", "The P.IVA number must contain 9 or more " +
          "digits.");
      return "registration-page";
    }

    // Controllo sul formato della password
    if (!regexPassword(password)) {
      model.addAttribute("error", "The password must be at least 8 " +
          "characters long and contain at least one uppercase letter, one lowercase letter, one " +
          "digit, and one special character ( @.$!%*?& ).");
      return "registration-page";
    }

    // Controllo che la password sia uguale alla conferma
    if (!(password.equals(confirmPassword))) {
      model.addAttribute("error", "Passwords don't match.");
      return "registration-page";
    }

    // Controlli nel database tramite il Service

    // Controllo che la mail non sia già registrata
    if (profileService.emailExists(email)) {
      model.addAttribute("error", "Email already exists");
      return "registration-page";
    }

    // Controllo che la P.IVA non sia già registrata
    if (profileService.pivaExists(companyPiva)) {
      model.addAttribute("error", "Piva already exists");
      return "registration-page";
    }

    // Se nessun controllo ha generato errore salva il nuovo utente
    profileService.registration(email, password, firstName, lastName, companyName, companyPiva);

    return "login-page";
  }


  // Pagina di login


  @GetMapping("/login-page")
  public String showLoginForm(Model model) {
    return "login-page";
  }

  @PostMapping("/login-form")
  public String login(@RequestParam String email, @RequestParam String password, Model model,
                      HttpSession session) {
    if (!(profileService.userExists(email, password))){
      System.out.println("SEX");
      model.addAttribute("error", "Email or Password are incorrect");
      return "login-page";
    } else {
      Optional<Beekeeper> beekeeper = profileService.findBeekeeper(email);
      if (beekeeper.isPresent()) {
        session.setAttribute("beekeeper", beekeeper.get());
      }
      return "index";
    }
  }


  // Pulsante di logout


  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.removeAttribute("email");
    return "index";
  }

 }
