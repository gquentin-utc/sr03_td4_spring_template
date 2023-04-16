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
