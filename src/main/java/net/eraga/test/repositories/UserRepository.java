package net.eraga.test.repositories;

import net.eraga.test.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);
    boolean existsByEmail(String email);
}
