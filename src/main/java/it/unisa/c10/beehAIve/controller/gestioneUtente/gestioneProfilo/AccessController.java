package it.unisa.c10.beehAIve.controller.gestioneUtente.gestioneProfilo;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.service.gestioneUtente.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

// Gestisce Login, Logout e Registrazione di un utente

@Controller
public class AccessController {
  private ProfileService profileService;
  @Autowired
  public AccessController(ProfileService profileService) {
    this.profileService = profileService;
  }

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
                             @RequestParam String confirmPassword, Model model) {

    // TODO da pulire e commentare
    if (!regexEmail(email)) {
      model.addAttribute("error","Invalid email address.");
    } else if (!regexFirstName(firstName)) {
      model.addAttribute("error","First Name cannot contain special." +
          "symbols except for ' and -.");
    } else if (!regexLastName(lastName)) {
      model.addAttribute("error","Last Name cannot contain special." +
          "symbols except for ' and -.");
    } else if (!(companyName.isEmpty()) && (companyPiva.isEmpty())){
      model.addAttribute("If the Company name is not null, the P.IVA cannot be null");
    } else if ((companyName.isEmpty()) && !(companyPiva.isEmpty())){
      model.addAttribute("If the P.IVA is not null, the Company Name cannot be null");
    } else if (!(companyName.isEmpty()) && !regexCompanyName(companyName)) {
      model.addAttribute("error","Company Name cannot contain special" +
          "symbols except for ' and -.");
    } else if (!(companyPiva.isEmpty()) && !regexCompanyPiva(companyPiva)) {
      model.addAttribute("error","The P.IVA number must contain 9 or more" +
          "digits.");
    } else if (!regexPassword(password)) {
      model.addAttribute("error","The password must be at least 8 " +
          "characters long and contain at least one uppercase letter, one lowercase letter, one " +
          "digit, and one special character ( @.$!%*?& ).");
    } else if (!(password.equals(confirmPassword))) {
      model.addAttribute("error","Passwords don't match.");
    } else if (profileService.emailExists(email)) {
      model.addAttribute("error","Email already exists");
    } else if (!(companyPiva.isEmpty()) && (profileService.pivaExists(companyPiva))) {
      model.addAttribute("error","Piva already exists");
    } else if(companyName.isEmpty()){
      profileService.registration(email,password,firstName,lastName);
    } else {
      profileService.registration(email,password,firstName,lastName,companyName,companyPiva);
    }

    return "registration-page";
    /*if (password.equals(confirmPassword)) {
      model.addAttribute("username", email);
      return "redirect:/success";
    } else {
      model.addAttribute("error", "Invalid username or password");
      return "login";
    }*/
  }

  /*@GetMapping("login")
  public String showLoginForm(WebRequest request, Model model) {
    Beekeeper beekeeper = new Beekeeper();
    model.addAttribute("user", beekeeper);
    return "login";
  }

  // TODO finire login
  @PostMapping("login")
  public ModelAndView loginUserAccount(@ModelAttribute("user") Beekeeper beekeeper,
      HttpServletRequest request,
      Errors errors) {

    try {
      Beekeeper registered = profileService.registration(beekeeper);
    } catch (RuntimeException uaeEx) {
      // TODO gestione errore
    }
    return new ModelAndView("successRegister", "user", beekeeper);
  }*/

 }
