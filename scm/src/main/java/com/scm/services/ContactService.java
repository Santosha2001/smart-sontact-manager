package com.scm.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;

public interface ContactService {

    void processAndSaveContact(ContactForm contactForm, String username);

    Contact saveContact(Contact contact);

    Contact updateContact(Contact contact);

    List<Contact> getAllContacts();

    Contact getContactById(String id);

    void deleteContactById(String id);

    Page<Contact> searchContactByName(String nameKeyword, int size, int page, String sortBy, String order, User user);

    Page<Contact> searchContactByEmail(String emailKeyword, int size, int page, String sortBy, String order, User user);

    Page<Contact> searchContactByPhoneNumber(String phoneNumberKeyword, int size, int page, String sortBy, String order,
            User user);

    List<Contact> getContactsByUserId(String userId);

    Page<Contact> getContactByUser(User user, int page, int size, String sortField, String sortDirection);

    ContactForm prepareContactForm(String contactId);

    void updateContactFromForm(String contactId, ContactForm contactForm);
}
