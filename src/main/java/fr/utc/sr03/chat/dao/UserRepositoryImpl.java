package fr.utc.sr03.chat.dao;

import fr.utc.sr03.chat.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<User> findAdminOnly() {
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.admin = :admin", User.class).setParameter("admin", true);

        // Execution de la requete
        List<User> users = query.getResultList();

        // Traitement eventuel pour transformer le resultat de la requete
        //...

        return users;
    }
}