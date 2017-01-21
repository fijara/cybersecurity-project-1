package sec.project.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {
    
    public static final long MAX_PARTICIPANTS = 1000;

    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/";
    }
    
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm(Model model, @RequestParam String returnToUrl) {
        model.addAttribute("id", "");
        model.addAttribute("returnToUrl", returnToUrl);
        model.addAttribute("form_action", "Register");
        //
        return "registration";
    }
    
    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public String updateForm(Model model, @RequestParam String id) {
        Signup signup = signupRepository.findOne(Long.valueOf(id));
        if (signup != null) {
            model.addAttribute("id", id);
            model.addAttribute("name", signup.getName());
            model.addAttribute("address", signup.getAddress());
            model.addAttribute("email", signup.getEmail());
        }
        //
        model.addAttribute("returnToUrl", "/");
        model.addAttribute("form_action", "Update");
        //
        return "registration";
    }
    
    @RequestMapping(value = "/registration/{id}", method = RequestMethod.GET)
    public String registration(Model model, @PathVariable String id) {
        Signup signup = signupRepository.findOne(Long.valueOf(id));
        if (signup != null) {
            model.addAttribute("name", signup.getName());
            model.addAttribute("address", signup.getAddress());
            model.addAttribute("email", signup.getEmail());
        }
        return "registration";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model, 
                            @RequestParam String id,
                            @RequestParam String name, 
                            @RequestParam String address, 
                            @RequestParam String email, 
                            @RequestParam String termsandcond,
                            @RequestParam String returnToUrl) throws UnsupportedEncodingException {
        if (!"accepted".equals(termsandcond)) {
            //
            model.addAttribute("id", id);
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("address", address);
            model.addAttribute("returnToUrl", returnToUrl);
            model.addAttribute("error", "Fill in all fields and accept T&C");
            model.addAttribute("form_action", "Register");
            return "registration";
        } else if (signupRepository.count() >= MAX_PARTICIPANTS) {
            //
            model.addAttribute("error_title", "Registration");
            model.addAttribute("error_heading", "Sorry, event already fully booked");
            model.addAttribute("error_description", "The event is already fully booked. You may ask for available seat 24h before the event.");
            model.addAttribute("error_continue", "/");
            return "error";
        } else {
            //
            Signup registration;
            if (id == null || id.isEmpty()) {
                //
                registration = signupRepository.save(new Signup(name, address, email));
            } else {
                //
                registration = signupRepository.findOne(Long.valueOf(id));
                registration.setAddress(address);
                registration.setEmail(email);
                registration.setName(name);
                registration = signupRepository.save(registration);
            }
            //
            model.addAttribute("id", registration.getId());
            model.addAttribute("name", registration.getName());
            model.addAttribute("address", registration.getAddress());
            model.addAttribute("email", registration.getEmail());
            model.addAttribute("returnToUrl", returnToUrl);
            return "done";
        }
    }
    
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public String check(    Model model) {
        return "check";
    }
    
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public String check( Model model,
                         @RequestParam String email) {
        List<Signup> registrations = signupRepository.findByInjectableQuery(email);
        if (registrations != null && !registrations.isEmpty()) {
            //
            model.addAttribute("registrations", registrations);
            return "registrations";
        }
        return "redirect:/check";
    }
    
    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    public String cancel(Model model, @RequestParam String id) {
        Signup signup = signupRepository.findOne(Long.valueOf(id));
        if (signup != null) {
            //
            model.addAttribute("name", signup.getName());
            model.addAttribute("address", signup.getAddress());
            model.addAttribute("email", signup.getEmail());
            model.addAttribute("id", signup.getId());
        } else {
            //
            model.addAttribute("id", Long.valueOf(id));
        }
        //
        return "cancel";
    }
    
    @RequestMapping(value = "/canceled", method = RequestMethod.GET)
    public String canceled(Model model, @RequestParam String id, @RequestParam String confirm) {
        //
        if (Boolean.valueOf(confirm)) {
            //
            Long pk = Long.valueOf(id);
            if (signupRepository.exists(pk)) {
                //
                signupRepository.delete(pk);
            }
            //
            return "canceled";
        } else {
            //
            return "redirect:/cancel?id=" + id;
        }
    }
}
