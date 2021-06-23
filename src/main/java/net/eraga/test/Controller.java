package net.eraga.test;

import net.eraga.test.models.Contact;
import net.eraga.test.models.File;
import net.eraga.test.models.Person;
import net.eraga.test.models.User;
import net.eraga.test.repositories.ContactRepository;
import net.eraga.test.repositories.FileRepository;
import net.eraga.test.repositories.PersonRepository;
import net.eraga.test.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired private UserRepository userRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private FileRepository fileRepository;
    @Autowired private ContactRepository contactRepository;

    @PostMapping("register")
    public void registerUser(@Valid @RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    @PostMapping("person")
    public Person createPerson(@Valid @RequestBody Person person) {
        person.getContacts().forEach(contact -> contact.setPerson(person));
        return personRepository.save(person);
    }

    @PostMapping("persons")
    public void createPersons(@Valid @RequestBody List<Person> persons) {
        persons.forEach(this::createPerson);
    }

    @PostMapping("person/{id}/photo")
    public Person uploadFile(@PathVariable int id,
                             @RequestParam("photo") MultipartFile mFile) throws IOException {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        File file = new File(mFile.getOriginalFilename(), mFile.getContentType(), mFile.getBytes());
        file.setPerson(person);
        fileRepository.save(file);
        return person;
    }

    @PostMapping("person/{id}/contact")
    public Person addContact(@PathVariable int id,
                             @RequestBody Contact contact) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        contact.setPerson(person);
        contactRepository.save(contact);
        return person;
    }


    @GetMapping("persons")
    public Page<Person> getPersons(@RequestParam int page) {
        return personRepository.findAll(PageRequest.of(page, 10, Sort.unsorted()));
    }

    @GetMapping("person/{id}")
    public Person getPerson(@PathVariable int id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) throws FileNotFoundException {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok().
                header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + file.getName() + "\"").
                body(new ByteArrayResource(file.getData()));
    }

    @GetMapping("report")
    public List<Report.Region> getReport() {
        return new Report(personRepository).getRegions();
    }


    @Transactional
    @PutMapping("person/{id}")
    public Person updatePerson(@PathVariable int id,
                               @RequestBody Person newPerson) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        person.setName(newPerson.getName());
        person.setRegion(newPerson.getRegion());
        person.setCity(newPerson.getCity());
        person.setComment(newPerson.getComment());

        contactRepository.deleteByPerson(person);
        person.setContacts(newPerson.getContacts());
        person.getContacts().forEach(contact -> {
            contact.setPerson(person);
            contactRepository.save(contact);
        });
        return personRepository.save(person);
    }


    @DeleteMapping("person/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable int id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        personRepository.delete(person);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
