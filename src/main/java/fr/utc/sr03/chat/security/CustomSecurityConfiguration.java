package fr.utc.sr03.chat.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class CustomSecurityConfiguration {
	private final JwtTokenFilter jwtTokenFilter;

	public CustomSecurityConfiguration(JwtTokenFilter jwtTokenFilter) {
		this.jwtTokenFilter = jwtTokenFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// Configuration des autorisations par requetes
				.authorizeHttpRequests((authz) -> authz
						// Autorisation des ressources "statiques"
						.requestMatchers("/css/**").permitAll()
						// Autorisation des Controllers "Web" : la securite est geree manuellement dans les controllers
						.requestMatchers("/login/**", "/admin/**", "/web/test").permitAll()
						// Autorisation du endpoint "test" pour la demo
						.requestMatchers("/api/open/test/**").permitAll()
						// Autorisation du endpoint REST "login" ... faut bien pouvoir se logger
						.requestMatchers("/api/secure/test/login/**").permitAll()
						// Autorisation des endpoints Websocket : la securite est geree manuellement dans le serveur websocket
						.requestMatchers("/samplewebsocketserver/**").permitAll()
						// Toutes les autres requetes necessitent une authentification
						.anyRequest().authenticated()
				)
				// Desactivation Spring CSRF protection pour autoriser les requetes POST
				.csrf(AbstractHttpConfigurer::disable)
				// Application du filtre JWT
				.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}