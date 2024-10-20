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
        return "index";
    }

    @RequestMapping("/about")
    public String aboutPage(Model model) {
        return "about";
    }

    @RequestMapping("/services")
    public String servicesPage() {
        return "services";
    }

    @GetMapping("/contact")
    public String contact() {
        return new String("contact");
    }

    @GetMapping("/login")
    public String login() {
        return new String("login");
    }

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
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult,
            HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            // Retain the existing user data and return to the register page
            model.addAttribute("userForm", userForm);
            return "register";
        }

        User user = userService.convertUserFormToUser(userForm);
        User savedUser = userService.saveUser(user);

        if (savedUser == null) {
            // Set an error message in the session if the email already exists
            Message message = Message.builder().content("Email already in use").type(MessageType.red).build();
            session.setAttribute("message", message);
            // Retain the existing user data
            model.addAttribute("userForm", userForm);
            return "register";
        }

        Message message = Message.builder().content("Registration Successful").type(MessageType.green).build();
        session.setAttribute("message", message);
        return "redirect:/register";
    }

}
