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
@SessionAttributes("beekeeper")
public class AccessController {
  final private ProfileService profileService;
  @Autowired
  public AccessController(ProfileService profileService) {
    this.profileService = profileService;
  }


  //Pagina di Registrazione


  @GetMapping("registration")
  public String showRegistrationForm(HttpSession session) {
    if (session.getAttribute("beekeeper") == null) {
      return "registration-page";
    } else {
      return "index";
    }
  }

  protected boolean regexEmail (String email) {
    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    return email.matches(emailRegex);
  }

  protected boolean regexPassword (String password) {
    String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@.$!%*?&])[A-Za-z\\d@.$!%*?&]{8,}$";
    return password.matches(passwordRegex);
  }

  protected boolean regexFirstName (String firstName) {
    String firstNameRegex = "^[A-Z][a-z'-]+(?: [A-Z][a-z'-]+)*$";
    return firstName.matches(firstNameRegex);
  }

  protected boolean regexLastName (String lastName) {
    String lastNameRegex = "^[A-Z][a-z'-]+(?: [A-Z][a-z'-]+)*$";
    return lastName.matches(lastNameRegex);
  }

  protected boolean regexCompanyName (String companyName) {
    String companyRegex = "^[a-zA-Z0-9\\s.'\",&()-]+$";
    return companyName.matches(companyRegex);
  }

  protected boolean regexCompanyPiva (String companyPiva) {
    String companyPivaRegex = "^[\\d-]{9,}$";
    return companyPiva.matches(companyPivaRegex);
  }

  @PostMapping("/registration-form")
  public String registration(@RequestParam String email, @RequestParam String firstName,
                             @RequestParam String lastName, @RequestParam String companyName,
                             @RequestParam String companyPiva, @RequestParam String password,
                             @RequestParam String confirmPassword, Model model) {

    // Controlli sul formato dei parametri

    // Controllo sul formato dell'email
    if (!regexEmail(email)) {
      model.addAttribute("error", "Invalid email address.");
      return "registration-page";
    }

    // Controllo sulla lunghezza della mail
    if (email.length() > 50) {
      model.addAttribute("error", "Email address too long");
      return "registration-page";
    }

    // Controllo sul formato del nome
    if (!regexFirstName(firstName)) {
      model.addAttribute("error", "First Name cannot contain special " +
          "symbols except for ' and - .");
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
      model.addAttribute("error", "Last name cannot contain special " +
          "symbols except for ' and - .");
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

    // Controllo sul formato del nome della compagnia
    if (!regexCompanyName(companyName)) {
      model.addAttribute("error", "Company name cannot contain special " +
          "symbols except for ' and - .");
      return "registration-page";
    }

    // Controllo sulla lunghezza del nome della compagnia
    if (companyName.length() < 2) {
      model.addAttribute("error", "Company name too short.");
      return "registration-page";
    } else if (companyName.length() > 100) {
      model.addAttribute("error", "Company name too long.");
      return "registration-page";
    }

    // Controllo sul formato della P.IVA
    if (!regexCompanyPiva(companyPiva)) {
      model.addAttribute("error", "The PIVA number must contain 9 or more " +
          "digits.");
      return "registration-page";
    }

    // Controllo sulla lunghezza della P.IVA
    if (companyPiva.length() > 20) {
      model.addAttribute("error", "PIVA too long");
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
      model.addAttribute("error", "PIVA already exists.");
      return "registration-page";
    }

    // Se nessun controllo ha generato errore salva il nuovo utente
    profileService.registration(email, password, firstName, lastName, companyName, companyPiva);

    return "login-page";
  }


  // Pagina di login


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
    System.out.println("Entered Controller");
    if (!(profileService.userExists(email, password))) {
      model.addAttribute("error", "Email or password are incorrect.");
      return "login-page";
    } else {
      Optional<Beekeeper> beekeeper = profileService.findBeekeeper(email);
      if (beekeeper.isPresent()) {
        session.setAttribute("beekeeper",beekeeper.get());
        session.setAttribute("paymentDue",beekeeper.get().getPaymentDue());
        session.setAttribute("email", beekeeper.get().getEmail());
        session.setAttribute("companyName", beekeeper.get().getCompanyName());
        session.setAttribute("firstName", beekeeper.get().getFirstName());
        session.setAttribute("lastName", beekeeper.get().getLastName());
        session.setAttribute("companyPiva",beekeeper.get().getCompanyPiva());
        session.setAttribute("subscriptionExpirationDate",beekeeper.get().getSubscrExpirationDate().toString());
      }

      // Generiamo lo user per Spring Security
      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority("USER"));
      UserDetails user = new org.springframework.security.core.userdetails.User(email,beekeeper.get().getPasswordhash(),authorities);

      // Generiamo la chiave di autenticazione
      Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

      // Prendiamo il security context e aggiungiamo l'autenticazione
      SecurityContext sc = SecurityContextHolder.getContext();
      sc.setAuthentication(authentication);

      // Inseriamo l'autenticazione alla sessione
      session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,sc);

      return "index";
    }
  }
 }
