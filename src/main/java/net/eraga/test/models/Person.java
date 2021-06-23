package net.eraga.test.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private int id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Region is required")
    private int region;

    private String city;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<File> photos;

    private String comment;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    @NotEmpty(message = "Contact is required")
    private List<Contact> contacts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<File> getPhotos() {
        return photos;
    }

    public void setPhotos(List<File> photos) {
        this.photos = photos;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
