# Moteur de template

## THYMELEAF
Dans le pom.xml :
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```
Les fichiers html doivent etre dans `src/main/resources/templates/`

## JSP (pour info)
Dans le pom.xml :
```
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
</dependency>
```
Dans application.properties :
```
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```
Les fichiers jsp doivent etre dans `src/main/webapp/WEB-INF/jsp/`

# Validation
Dans le pom.xml
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```
Dans la classe java de l'entite JPA
```
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
```
```
@Column(name = "firstname")
@Size(min = 2)
@NotEmpty(message = "firstname obligatoire")
private String firstName;
```
Dans le controller : Ajouter l'annotation @Valid et un parametre de type BindingResult a la fonction qui repond au POST
```
@PostMapping
public String postLogin(@ModelAttribute @Valid User user, BindingResult result, Model model) {
    ...
}
```

# Requetes presonnalisees
## Requetes simples
Dans l'interface qui etend JpaRepository, ajouter la fonction, précédée de l'anotation @Query dans laquelle est definie la requete  
_=> voir l'interface `fr.utc.sr03.chat.dao.UserRepository`_

## Requetes complexes
Creer une interface java qui liste les fonctions personnalisees  
_=> voir l'interface `fr.utc.sr03.chat.dao.UserRepositoryCustom`_  

Implementer les fonctions de l'interface  
_=> voir la classe `fr.utc.sr03.chat.dao.UserRepositoryImpl`_  

Dans l'interface initiale qui etend JpaRepository (fr.utc.sr03.chat.dao.UserRepository), etendre egalement l'interface personnalisee  
_=> voir l'interface `fr.utc.sr03.chat.dao.UserRepository`_  

# CORS
Ajouter l'annotation @CrossOrigin a la classe controller ou a la methode getmapping/postmapping
```
@CrossOrigin(origins="*", allowedHeaders="*")
```

# Websocket
Dans le pom.xml :
```
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-websocket</artifactId>
</dependency>
```
Creer une classe java de configuration  
_=> voir la classe fr.utc.sr03.chat.websocket.WebSocketConfig_

Creer une classe de java qui contient le serveur Websocket  
_=> voir la classe fr.utc.sr03.chat.websocket.SampleWebSocketServer_

Client HTML / JS :  
_=> voir le fichier src/main/resources/test_websocket/sample_client.html_

# JWT
## Configuration
Dans le pom.xml :
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

Dans application.properties :
```
api.security.token.signatureSecretKey=LeelooDallasMultipass
api.security.token.validityInMilliseconds=3600000
```

Creer une classe java de fonctions utilitaires pour la gestion du token  
_=> voir la classe fr.utc.sr03.chat.security.JwtTokenProvider_

Creer une classe java de qui sera utilisee par spring security pour filtrer les requetes entrantes  
_=> voir la classe fr.utc.sr03.chat.security.JwtTokenFilter_

Creer une classe java de configuration  
_=> voir la classe fr.utc.sr03.chat.security.CustomSecurityConfiguration_

## Utilisation

### 1 : Authentification
Ici, cette partie est faite dans le Controller  

- Verifier l'identite de l'utilisateur
- Generer un token
- Retourner le token au client  

_=> voir la classe fr.utc.sr03.chat.controller_rest.SecureController_

### 2 : Gestion du token cote client
Cote client, il faut stocker le token (dans le local storage par exemple)  
A chaque appel au webservice, le token devra etre envoye dans le header "Authorization" de la requete

### 3 : Verification du token cote serveur
La verification du token est faite a chaque requete par Spring 
telle que definie dans la classe de configuration (CustomSecurityConfiguration)
Le filtre permet de convertir le token en objet Authentication comprehensible par Spring

## Securisation Websocket
La connexion websocket ne permet pas de passer des headers lors des appels.  
Une solution peut etre de passer le token dans l'url de connexion websocket,
puis d'autoriser la connexion si le token est valide et selon les droits de l'utilisateur.