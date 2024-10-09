package com.scm.services.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.exceptions.ResourceNotFoundException;
import com.scm.forms.ContactForm;
import com.scm.repositories.ContactRepository;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ContactRepository contactRepository;

    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    /**
     * Saves a new contact to the repository.
     * 
     * @param contact The contact object to be saved.
     * @return The saved contact object with the generated ID.
     */
    @Override
    public Contact saveContact(Contact contact) {
        String contactId = UUID.randomUUID().toString();
        contact.setId(contactId);
        return contactRepository.save(contact);
    }

    /**
     * Processes the contact form and saves the contact.
     * 
     * This method performs the following tasks:
     * 1. Retrieves the user associated with the given email.
     * 2. Converts the form data to a Contact object.
     * 3. Processes and uploads the contact image, if provided.
     * 4. Saves the contact using the saveContact method.
     *
     * @param contactForm the form containing contact details for submission
     * @param username    the email of the logged-in user
     */
    @Override
    public void processAndSaveContact(ContactForm contactForm, String username) {
        // Get user by email
        User user = userService.getUserByUserEmail(username);

        // Convert form data to Contact object
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setFavorite(contactForm.isFavorite());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setUser(user);
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());

        // Process contact image
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(filename);
        }

        saveContact(contact);
    }

    /**
     * Updates an existing contact with new information.
     * 
     * This method retrieves an existing contact from the repository using its ID.
     * If the contact is found, it updates its fields with the new information
     * provided
     * in the contact parameter and saves the updated contact back to the
     * repository.
     * If the contact is not found, it throws a ResourceNotFoundException.
     *
     * @param contact the Contact object containing updated information
     * @return the updated Contact object saved in the repository
     * @throws ResourceNotFoundException if the contact with the specified ID is not
     *                                   found
     */
    @Override
    public Contact updateContact(Contact contact) {

        var contactOld = contactRepository.findById(contact.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        contactOld.setName(contact.getName());
        contactOld.setEmail(contact.getEmail());
        contactOld.setPhoneNumber(contact.getPhoneNumber());
        contactOld.setAddress(contact.getAddress());
        contactOld.setDescription(contact.getDescription());
        contactOld.setPicture(contact.getPicture());
        contactOld.setFavorite(contact.isFavorite());
        contactOld.setWebsiteLink(contact.getWebsiteLink());
        contactOld.setLinkedInLink(contact.getLinkedInLink());
        contactOld.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());

        return contactRepository.save(contactOld);
    }

    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    /**
     * Retrieves a contact by its ID.
     * 
     * This method searches the contact repository for a contact with the specified
     * ID.
     * If a contact is found, it is returned; otherwise, a ResourceNotFoundException
     * is thrown.
     *
     * @param id the ID of the contact to be retrieved
     * @return the Contact object with the specified ID
     * @throws ResourceNotFoundException if no contact is found with the given ID
     */
    @Override
    public Contact getContactById(String id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
    }

    /**
     * Deletes a contact by its ID.
     * 
     * This method retrieves a contact from the repository using its ID.
     * If the contact is found, it is deleted from the repository.
     * If the contact is not found, a ResourceNotFoundException is thrown.
     *
     * @param id the ID of the contact to be deleted
     * @throws ResourceNotFoundException if no contact is found with the given ID
     */
    @Override
    public void deleteContactById(String id) {
        var contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
        contactRepository.delete(contact);

    }

    @Override
    public List<Contact> getContactsByUserId(String userId) {
        return contactRepository.findByUserId(userId);

    }

    /**
     * Retrieves a paginated list of contacts for a specific user.
     * 
     * This method creates a pageable request based on the provided page number,
     * size,
     * sort by attribute, and sorting direction. It then uses this pageable request
     * to
     * find and return a page of contacts associated with the given user.
     *
     * @param user      the user whose contacts are to be retrieved
     * @param page      the page number to retrieve
     * @param size      the number of contacts per page
     * @param sortBy    the attribute by which the contacts should be sorted
     * @param direction the direction of sorting, either "asc" for ascending or
     *                  "desc" for descending
     * @return a Page of Contact objects for the specified user
     */
    @Override
    public Page<Contact> getContactByUser(User user, int page, int size, String sortBy, String direction) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);

        return contactRepository.findByUser(user, pageable);
    }

    /**
     * Searches for contacts by name keyword, paginated and sorted as specified.
     * 
     * This method creates a pageable request based on the provided page number,
     * size,
     * sort by attribute, and sorting order. It then uses this pageable request to
     * find and return a page of contacts associated with the given user that
     * contain
     * the specified name keyword.
     *
     * @param nameKeyword the keyword to search for in contact names
     * @param size        the number of contacts per page
     * @param page        the page number to retrieve
     * @param sortBy      the attribute by which the contacts should be sorted
     * @param order       the sorting order, either "asc" for ascending or "desc"
     *                    for descending
     * @param user        the user whose contacts are to be searched
     * @return a Page of Contact objects that match the search criteria
     */
    @Override
    public Page<Contact> searchContactByName(String nameKeyword, int size, int page, String sortBy, String order,
            User user) {

        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);

        return contactRepository.findByUserAndNameContaining(user, nameKeyword, pageable);
    }

    /**
     * Searches for contacts by email keyword, paginated and sorted as specified.
     * 
     * This method creates a pageable request based on the provided page number,
     * size,
     * sort by attribute, and sorting order. It then uses this pageable request to
     * find and return a page of contacts associated with the given user that
     * contain
     * the specified email keyword.
     *
     * @param emailKeyword the keyword to search for in contact emails
     * @param size         the number of contacts per page
     * @param page         the page number to retrieve
     * @param sortBy       the attribute by which the contacts should be sorted
     * @param order        the sorting order, either "asc" for ascending or "desc"
     *                     for descending
     * @param user         the user whose contacts are to be searched
     * @return a Page of Contact objects that match the search criteria
     */
    @Override
    public Page<Contact> searchContactByEmail(String emailKeyword, int size, int page, String sortBy, String order,
            User user) {
        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);

        return contactRepository.findByUserAndEmailContaining(user, emailKeyword, pageable);
    }

    /**
     * Searches for contacts by phone number keyword, paginated and sorted as
     * specified.
     * 
     * This method creates a pageable request based on the provided page number,
     * size,
     * sort by attribute, and sorting order. It then uses this pageable request to
     * find and return a page of contacts associated with the given user that
     * contain
     * the specified phone number keyword.
     *
     * @param phoneNumberKeyword the keyword to search for in contact phone numbers
     * @param size               the number of contacts per page
     * @param page               the page number to retrieve
     * @param sortBy             the attribute by which the contacts should be
     *                           sorted
     * @param order              the sorting order, either "asc" for ascending or
     *                           "desc" for descending
     * @param user               the user whose contacts are to be searched
     * @return a Page of Contact objects that match the search criteria
     */
    @Override
    public Page<Contact> searchContactByPhoneNumber(String phoneNumberKeyword, int size, int page, String sortBy,
            String order, User user) {

        Sort sort = order.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);

        return contactRepository.findByUserAndPhoneNumberContaining(user, phoneNumberKeyword, pageable);
    }

    /**
     * Prepares a ContactForm object for the specified contact ID.
     * 
     * This method retrieves the contact associated with the given ID,
     * converts its details into a ContactForm object, and returns the ContactForm.
     *
     * @param contactId the ID of the contact to be converted into a ContactForm
     * @return a ContactForm object containing the contact's details
     */
    @Override
    public ContactForm prepareContactForm(String contactId) {
        var contact = getContactById(contactId);

        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setPicture(contact.getPicture());

        return contactForm;
    }

    /**
     * Updates an existing contact with information from a ContactForm.
     * 
     * This method performs the following tasks:
     * 1. Retrieves the existing contact by its ID.
     * 2. Updates the contact's fields with the information from the ContactForm.
     * 3. Processes and uploads a new contact image if provided.
     * 4. Saves the updated contact back to the repository.
     *
     * @param contactId   the ID of the contact to be updated
     * @param contactForm the form containing updated contact details
     */
    @Override
    public void updateContactFromForm(String contactId, ContactForm contactForm) {
        // Get existing contact
        var contact = getContactById(contactId);
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());

        // Process image
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            logger.info("file is not empty");
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            contact.setCloudinaryImagePublicId(fileName);
            contact.setPicture(imageUrl);
        } else {
            logger.info("file is empty");
        }

        updateContact(contact);
    }

}
