package com.scm.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;
import com.scm.utils.Helper;
import com.scm.utils.Message;
import com.scm.utils.MessageType;
import com.scm.utils.ScmAppConstants;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @GetMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();

        contactForm.setFavorite(true);
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    /**
     * Handles the POST request for saving a new contact.
     *
     * This method performs the following tasks:
     * 1. Validates the form data. If there are errors, logs them and sets an error
     * message in the session,
     * then returns to the add contact view.
     * 2. Retrieves the logged-in user's email from the authentication object.
     * 3. Calls the service method to process and save the contact.
     * 4. Sets a success message in the session.
     * 5. Redirects to the add contact page upon successful submission.
     *
     * @param contactForm    the form containing contact details for submission
     * @param result         the binding result for validation
     * @param authentication the authentication object containing the user's
     *                       authentication information
     * @param session        the HTTP session for storing messages
     * @return the view name to redirect to
     */
    @PostMapping("/add")
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
            Authentication authentication, HttpSession session) {

        // Validate form data
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> logger.info(error.toString()));
            session.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());
            return "user/add_contact";
        }

        // Get logged-in user's email
        String username = Helper.getEmailOfLoggedInUser(authentication);
        contactService.processAndSaveContact(contactForm, username);

        session.setAttribute("message",
                Message.builder()
                        .content("You have successfully added a new contact")
                        .type(MessageType.green)
                        .build());
        return "redirect:/user/contacts/add";
    }

    /**
     * Handles the request for viewing user contacts with pagination and sorting
     * options.
     * 
     * This method performs the following tasks:
     * 1. Retrieves the logged-in user's email from the authentication object.
     * 2. Retrieves the user details using the email.
     * 3. Loads the user's contacts with pagination and sorting based on the
     * provided parameters.
     * 4. Adds the contacts and page size to the model for the view.
     * 5. Adds a new ContactSearchForm to the model for search functionality.
     * 6. Returns the view name "user/contacts" to display the contacts.
     * 
     * @param page           the page number to retrieve
     * @param size           the number of contacts per page
     * @param sortBy         the attribute by which the contacts should be sorted
     * @param direction      the direction of sorting, either "asc" for ascending or
     *                       "desc" for descending
     * @param model          the model to which the contacts and other attributes
     *                       will be added
     * @param authentication the authentication object containing the user's
     *                       authentication information
     * @return the view name "user/contacts" for displaying the contacts
     */
    @RequestMapping
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = ScmAppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction, Model model,
            Authentication authentication) {

        // load all the user contacts
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByUserEmail(username);

        Page<Contact> pageContact = contactService.getContactByUser(user, page, size, sortBy, direction);

        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", ScmAppConstants.PAGE_SIZE);
        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";
    }

    /**
     * Handles the request for searching contacts with pagination and sorting
     * options.
     *
     * This method performs the following tasks:
     * 1. Logs the search field and keyword for debugging purposes.
     * 2. Retrieves the logged-in user's details using their email.
     * 3. Searches for contacts based on the specified field (name, email, or phone
     * number) and keyword.
     * 4. Adds the search form, contacts, and page size to the model for the view.
     * 5. Returns the view name "user/search" to display the search results.
     *
     * @param contactSearchForm the form containing search criteria
     * @param size              the number of contacts per page
     * @param page              the page number to retrieve
     * @param sortBy            the attribute by which the contacts should be sorted
     * @param direction         the direction of sorting, either "asc" for ascending
     *                          or "desc" for descending
     * @param model             the model to which the search form and contacts will
     *                          be added
     * @param authentication    the authentication object containing the user's
     *                          authentication information
     * @return the view name "user/search" for displaying the search results
     */
    @RequestMapping("/search")
    public String searchHandler(

            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "size", defaultValue = ScmAppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model,
            Authentication authentication) {

        logger.info("field {} keyword {}", contactSearchForm.getField(), contactSearchForm.getValue());
        var user = userService.getUserByUserEmail(Helper.getEmailOfLoggedInUser(authentication));

        Page<Contact> pageContact = null;
        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            pageContact = contactService.searchContactByName(contactSearchForm.getValue(), size, page, sortBy,
                    direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchContactByEmail(contactSearchForm.getValue(), size, page, sortBy,
                    direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContact = contactService.searchContactByPhoneNumber(contactSearchForm.getValue(), size, page, sortBy,
                    direction, user);
        }

        logger.info("pageContact {}", pageContact);

        model.addAttribute("contactSearchForm", contactSearchForm);
        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", ScmAppConstants.PAGE_SIZE);

        return "user/search";
    }

    /**
     * Handles the request for deleting a contact by its ID.
     * 
     * This method performs the following tasks:
     * 1. Calls the service method to delete the contact with the specified ID.
     * 2. Logs the deletion of the contact for debugging purposes.
     * 3. Sets a success message in the session indicating that the contact was
     * deleted successfully.
     * 4. Redirects the user to the contacts page.
     *
     * @param contactId the ID of the contact to be deleted
     * @param session   the current HTTP session to set messages
     * @return the view name to redirect to after deletion
     */
    @RequestMapping("/delete/{contactId}")
    public String deleteContact(
            @PathVariable("contactId") String contactId,
            HttpSession session) {
        contactService.deleteContactById(contactId);
        logger.info("contactId {} deleted", contactId);

        session.setAttribute("message",
                Message.builder()
                        .content("Contact is Deleted successfully !! ")
                        .type(MessageType.green)
                        .build());

        return "redirect:/user/contacts";
    }

    @GetMapping("/view/{contactId}")
    public String updateContactFormView(
            @PathVariable("contactId") String contactId,
            Model model) {
        ContactForm contactForm = contactService.prepareContactForm(contactId);

        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contactId);

        return "user/update_contact_view";
    }

    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable("contactId") String contactId,
            @Valid @ModelAttribute ContactForm contactForm,
            BindingResult bindingResult,
            Model model) {

        // Validate form data
        if (bindingResult.hasErrors()) {
            return "user/update_contact_view";
        }

        contactService.updateContactFromForm(contactId, contactForm);
        model.addAttribute("message", Message.builder().content("Contact Updated !!").type(MessageType.green).build());

        return "redirect:/user/contacts/view/" + contactId;
    }
}