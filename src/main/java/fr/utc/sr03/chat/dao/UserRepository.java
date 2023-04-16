package fr.utc.sr03.chat.dao;

import fr.utc.sr03.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    // Requete generee automatiquement par Spring
    User findByMailAndPassword(@Param("mail") String mail, @Param("password") String password);

    // Requete creee manuellement
    @Query("SELECT u FROM User u WHERE LENGTH(u.lastName) >= :lastNameLength")
    List<User> findByLastNameLength(@Param("lastNameLength") int lastNameLength);
}
