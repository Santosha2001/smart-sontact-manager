package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scm.entities.Contact;
import com.scm.services.ContactService;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ContactService contactService;

    /**
     * Handles GET requests to retrieve a contact by its ID.
     * 
     * @param contactId the ID of the contact to be retrieved
     * @return the contact with the specified ID
     */
    @GetMapping("/contacts/{contactId}")
    public Contact getContact(@PathVariable String contactId) {
        return contactService.getContactById(contactId);
    }
}
