package fr.utc.sr03.chat.controller_rest;

import fr.utc.sr03.chat.dao.UserRepository;
import fr.utc.sr03.chat.model.User;
import fr.utc.sr03.chat.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * URLs du endpoint :
 * - http://localhost:8080/api/secure/test/login
 * - http://localhost:8080/api/secure/test/users
 */
@RestController
@RequestMapping("api/secure/test")
@CrossOrigin(origins="*", allowedHeaders="*")
public class SecureController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecureController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> postLogin(@RequestBody User user) {
        if (user != null && user.getMail() != null && !user.getMail().isEmpty() && user.getPassword() != null && !user.getPassword().isEmpty()){
            User userFromDb = userRepository.findByMailAndPassword(user.getMail(), user.getPassword());
            if (userFromDb != null){
                return ResponseEntity.ok()
                        // Ajout de l'entete Access-Control-Expose-Headers pour autoriser le client a lire l'entete Authorization (ignore par defaut si le client utilise CORS - ex : navigateur web)
                        .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION)
                        // Ajout de l'entete Authorization avec le token JWT
                        .header(HttpHeaders.AUTHORIZATION, jwtTokenProvider.createSimpleToken(userFromDb.getMail(), (userFromDb.isAdmin() ? "ADMIN" : "USER")))
                        // Ajout de l'utilisateur dans le body
                        .body(userFromDb);
            }
            else{
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login ou mot de passe incorrect");
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/users")
    public List<User> findAllUsers() {
        // Recup de l'utilisateur connecte si besoin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            LOGGER.info("Utilisateur connecte : {}", authentication.getName());
        }
        else{
            LOGGER.info("Aucun utilisateur connecte");
        }

        return userRepository.findAll();
    }
}
