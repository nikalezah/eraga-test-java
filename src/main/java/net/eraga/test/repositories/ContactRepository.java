package net.eraga.test.repositories;

import net.eraga.test.models.Contact;
import net.eraga.test.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    @Modifying
    @Query("delete from Contact c where c.person = :person")
    void deleteByPerson(Person person);
}
