package it.unisa.c10.beehAIve.controller.gestioneUtente.gestioneProfilo;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.service.gestioneUtente.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AccessController {
  final private ProfileService profileService;
  @Autowired
  public AccessController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @GetMapping("registration")
  public String showRegistrationForm(HttpSession session) {
    if (session.getAttribute("beekeeper") == null) {
      return "registration-page";
    } else {
      return "index";
    }
  }

  @PostMapping("/registration-form")
  public String registration(@RequestParam String email, @RequestParam String firstName,
                             @RequestParam String lastName, @RequestParam String companyName,
                             @RequestParam String companyPiva, @RequestParam String password,
                             @RequestParam String confirmPassword, Model model) {
    // Controllo sul formato dell'email
    if (!regexEmail(email)) {
      model.addAttribute("error", "Invalid email address.");
      return "registration-page";
    }
    // Controllo sulla lunghezza dell'email
    if (email.length() < 5) {
      model.addAttribute("error", "Email address too short");
      return "registration-page";
    }
    if (email.length() > 50) {
      model.addAttribute("error", "Email address too long");
      return "registration-page";
    }
    // Controllo sull'eventuale esistenza della stessa email nel database
    if (profileService.emailExists(email)) {
      model.addAttribute("error", "Email already exists");
      return "registration-page";
    }

    // Controllo sul formato del nome
    if (!regexFirstName(firstName)) {
      model.addAttribute("error", "First name must start with capital " +
          "letter and cannot contain special symbols except for ' and - .");
      return "registration-page";
    }
    // Controllo sulla lunghezza del nome
    if (firstName.length() < 2) {
      model.addAttribute("error", "First name too short.");
      return "registration-page";
    } else if (firstName.length() > 15) {
      model.addAttribute("error", "First name too long.");
      return "registration-page";
    }

    // Controllo sul formato del cognome
    if (!regexLastName(lastName)) {
      model.addAttribute("error", "Last name must start with capital " +
          "letter and cannot contain special symbols except for ' and - .");
      return "registration-page";
    }
    // Controllo sulla lunghezza del cognome
    if (lastName.length() < 2) {
      model.addAttribute("error", "Last name too short.");
      return "registration-page";
    } else if (lastName.length() > 15) {
      model.addAttribute("error", "Last name too long.");
      return "registration-page";
    }

    // Controllo sul formato del nome dell'azienda
    if (!regexCompanyName(companyName)) {
      model.addAttribute("error", "Company cannot contain special symbols except for ' and - .");
      return "registration-page";
    }
    // Controllo sulla lunghezza del nome dell'azienda
    if (companyName.length() < 2) {
      model.addAttribute("error", "Company name too short.");
      return "registration-page";
    } else if (companyName.length() > 100) {
      model.addAttribute("error", "Company name too long.");
      return "registration-page";
    }

    // Controllo sul formato della PIVA
    if (!regexCompanyPiva(companyPiva)) {
      model.addAttribute("error", "PIVA must contain only digits.");
      return "registration-page";
    }
    // Controllo sulla lunghezza della PIVA
    if (companyPiva.length() < 9) {
      model.addAttribute("error", "PIVA too short.");
      return "registration-page";
    }
    if (companyPiva.length() > 20) {
      model.addAttribute("error", "PIVA too long.");
      return "registration-page";
    }
    // Controllo sull'eventuale esistenza della stessa PIVA nel database
    if (profileService.pivaExists(companyPiva)) {
      model.addAttribute("error", "PIVA already exists.");
      return "registration-page";
    }

    // Controllo sul formato della password
    if (!regexPassword(password)) {
      model.addAttribute("error", "The password must be at least 8 " +
          "characters long and contain at least one uppercase letter, one lowercase letter, one " +
          "digit, and one special character ( @.$!%*?& ).");
      return "registration-page";
    }
    // Controllo sulla lunghezza della password
    if (password.length() < 8) {
      model.addAttribute("error", "Password too short.");
      return "registration-page";
    }
    if (password.length() > 100) {
      model.addAttribute("error", "Password too long.");
      return "registration-page";
    }
    // Controllo sulla corrispondenza delle password
    if (!password.equals(confirmPassword)) {
      model.addAttribute("error", "Passwords don't match.");
      return "registration-page";
    }

    // Salvataggio del nuovo utente nel database
    profileService.registration(email, password, firstName, lastName, companyName, companyPiva);

    return "login-page";
  }

  @GetMapping("/login")
  public String showLoginForm(HttpSession session) {
    if (session.getAttribute("beekeeper") == null) {
      return "login-page";
    } else {
      return "index";
    }
  }

  @PostMapping("/login-form")
  public String login(@RequestParam String email, @RequestParam String password, Model model,
                      HttpSession session) {
    if (!(profileService.userExists(email, password))) {
      model.addAttribute("error", "Email or Password are incorrect.");
      return "login-page";
    } else {
      Optional<Beekeeper> beekeeper = profileService.findBeekeeper(email);
      if (beekeeper.isPresent()) {
        session.setAttribute("beekeeper", beekeeper.get());
      }

      // Generiamo lo user per Spring Security
      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority("USER"));
      UserDetails user = new org.springframework.security.core.userdetails.User(email, beekeeper
          .get().getPasswordhash(),authorities);

      // Generiamo la chiave di autenticazione
      Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
          user.getAuthorities());

      // Prendiamo il security context e aggiungiamo l'autenticazione
      SecurityContext sc = SecurityContextHolder.getContext();
      sc.setAuthentication(authentication);

      // Inseriamo l'autenticazione alla sessione
      session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,sc);

      return "index";
    }
  }

  private boolean regexEmail (String email) {
    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    return email.matches(emailRegex);
  }

  private boolean regexPassword (String password) {
    String passwordRegex =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@.$!%*?&])[A-Za-z\\d@.$!%*?&]{8,}$";
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
 }
