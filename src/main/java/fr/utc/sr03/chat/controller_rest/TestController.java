package fr.utc.sr03.chat.controller_rest;

import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * URLs du endpoint :
 * - http://localhost:8080/api/test/users
 * - http://localhost:8080/api/test/users-with-cors
 * => Controller "API" (sans template html, retourne du JSON)
 */
@Controller()
@RequestMapping("api/open/test")
public class TestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    @ResponseBody // Pour faire sans template html - peut etre mis aussi en annotation de la classe ou utiliser l'annotation @RestController (qui fait @Controller + @ResponseBody)
    public String testUserRepo() {
        LOGGER.info("=== ALL USERS ===");
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            LOGGER.info(user.getFirstName() + " : " + user.isAdmin());
        });

        LOGGER.info("=== USERS BY NAME AND PASSWORD ===");
        User user1 = userRepository.findByMailAndPassword("jb@test.com", "jb");
        LOGGER.info("user1 = " + user1.getMail());
        User user2 = userRepository.findByMailAndPassword("pf@test.com", "beaup");
        LOGGER.info("user2 = " + user2);

        LOGGER.info("=== NOMS >= 5 caracteres ===");
        List<User> usersCustomQuery = userRepository.findByLastNameLength(5);
        usersCustomQuery.forEach(admin -> {
            LOGGER.info(admin.getLastName());
        });

        LOGGER.info("=== ADMIN ONLY ===");
        List<User> admins = userRepository.findAdminOnly();
        admins.forEach(admin -> {
            LOGGER.info(admin.getFirstName() + " : " + admin.isAdmin());
        });

        LOGGER.info("=== INSERT ===");
        User newUser = new User();
        newUser.setFirstName("Clarence");
        newUser.setLastName("DICKS");
        newUser.setMail("cd@test.com");
        newUser.setPassword("cd");
        newUser.setAdmin(true);
        LOGGER.info(newUser.getFirstName() + " " + newUser.getLastName() + " inserted");
        userRepository.save(newUser);

        LOGGER.info("=== ALL USERS (2) ===");
        List<User> usersApresInsert = userRepository.findAll();
        usersApresInsert.forEach(user -> {
            LOGGER.info(user.getFirstName() + " : " + user.isAdmin());
        });

        return "ok";
    }

    /**
     * Find all users, avec autorisation CORS pour les requetes venant de l'UI JS
     */
    @GetMapping("/users-with-cors")
    @ResponseBody
    @CrossOrigin(origins="*", allowedHeaders="*")
    public List<User> testFindAllUsersWithCors() {
        return userRepository.findAll();
    }

    /**
     * Find user by id, + erreur 404 si l'utilisateur n'existe pas
     */
    @GetMapping("/users-with-cors/{id}")
    @ResponseBody
    @CrossOrigin(origins="*", allowedHeaders="*")
    public User testUserByIdWithCors(@PathVariable long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utilisateur n'existe pas"));
    }
}
