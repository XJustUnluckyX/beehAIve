package it.unisa.c10.beehAIve.controller.gestioneUtente.gestioneProfilo;

import it.unisa.c10.beehAIve.service.gestioneUtente.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Gestisce la modifica dei dati personali, la modifica della password ed eventuali altre operazioni sul Profilo
// come modificare il metodo di pagamento

@Controller
public class ProfileController {

  private ProfileService profileService;
  @Autowired
  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @PostMapping("/changeInfo")
  public String changeInfo(@RequestParam String firstName, @RequestParam String lastName,
                           HttpSession session) {
    Object emailObject = session.getAttribute("email");
    String email = emailObject.toString();
    profileService.changeInfo(email, firstName, lastName);
    return "user-page";
  }

  @PostMapping("/changePassword")
  public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
                               @RequestParam String confirmNewPassword, Model model,
                               HttpSession session){
    Object emailObject = session.getAttribute("email");
    String email = emailObject.toString();

    if(!profileService.userExists(email,oldPassword)) {
      model.addAttribute("error", "old password is incorrect");
      return "user-page";
    }

    if(!(newPassword.equals(confirmNewPassword))) {
      model.addAttribute("error", "the new passwords don't match");
      return "user-page";
    }

    profileService.changePassword(email, newPassword);
    return "user-page";
  }
}
