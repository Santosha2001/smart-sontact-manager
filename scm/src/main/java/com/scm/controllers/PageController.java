package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.services.UserService;
import com.scm.utils.Message;
import com.scm.utils.MessageType;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(Model model) {
        System.out.println("Home page handler");
        return "index";
    }

    @RequestMapping("/about")
    public String aboutPage(Model model) {
        System.out.println("About page loading");
        return "about";
    }

    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("services page loading");
        return "services";
    }

    // contact page
    @GetMapping("/contact")
    public String contact() {
        return new String("contact");
    }

    // this is showing login page
    @GetMapping("/login")
    public String login() {
        return new String("login");
    }

    // registration page
    @GetMapping("/register")
    public String register(Model model) {

        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        return "register";
    }

    /**
     * This method register's a new user:
     * 1. Validates the user form data.
     * 2. Converts the form data to a User object using the userService.
     * 3. Saves the User object, which includes generating a user ID,
     * encoding the password, setting default roles, and saving the user to the
     * database.
     * 4. Sets a success message in the session.
     * 5. Redirects the user to the registration page upon successful registration.
     *
     * @param userForm       the form containing user details for registration
     * @param rBindingResult the binding result for validation
     * @param session        the HTTP session for storing messages
     * @return the view name to redirect to
     */
    @PostMapping("/do-register")
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult,
            HttpSession session) {

        if (rBindingResult.hasErrors()) {
            return "register";
        }
        User user = userService.convertUserFormToUser(userForm);
        userService.saveUser(user);

        Message message = Message.builder().content("Registration Successful").type(MessageType.green).build();
        session.setAttribute("message", message);

        return "redirect:/register";
    }
}
