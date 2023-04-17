package fr.utc.sr03.chat.controller;

import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * URL du endpoint : http://localhost:8080/test
 */
@Controller
public class TestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

	@Autowired
    private UserRepository userRepository;

    @GetMapping("/test")
    @ResponseBody // Pour faire sans template html
    public String test() {
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
        newUser.setFirstName("Sophie");
        newUser.setLastName("REDONCULE");
        newUser.setMail("sr@test.com");
        newUser.setPassword("sr");
        newUser.setAdmin(true);
        LOGGER.info(newUser.getFirstName() + " " + newUser.getLastName() + " inserted");

        LOGGER.info("=== ALL USERS (2) ===");
        userRepository.save(newUser);
        List<User> usersApresInsert = userRepository.findAll();
        usersApresInsert.forEach(user -> {
            LOGGER.info(user.getFirstName() + " : " + user.isAdmin());
        });

        return "ok";
    }
}
