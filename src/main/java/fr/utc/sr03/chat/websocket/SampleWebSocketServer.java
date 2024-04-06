package fr.utc.sr03.chat.websocket;

import fr.utc.sr03.chat.dao.UserRepository;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Hashtable;

@Component
@ServerEndpoint(value="/samplewebsocketserver/{login}", configurator= SampleWebSocketServer.EndpointConfigurator.class)
public class SampleWebSocketServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleWebSocketServer.class);

    private static SampleWebSocketServer singleton;

    private static UserRepository userRepository;

    private final Hashtable<String, Session> sessions = new Hashtable<>();

    private SampleWebSocketServer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //+++++++++++++++++++++++++++++++++++++++++++
    // CONFIG
    // - Singleton => Permet de ne pas avoir une instance différente par client
    // - Le configurateur utilise le singleton
    //+++++++++++++++++++++++++++++++++++++++++++
    public static class EndpointConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getEndpointInstance(Class<T> endpointClass) {
            return (T) SampleWebSocketServer.getInstance();
        }
    }

    public static SampleWebSocketServer getInstance() {
        if (SampleWebSocketServer.singleton == null) {
            SampleWebSocketServer.singleton = new SampleWebSocketServer(userRepository);
        }
        return SampleWebSocketServer.singleton;
    }

    //+++++++++++++++++++++++++++++++++++++++++++
    // CONNECTION + MESSAGES
    //+++++++++++++++++++++++++++++++++++++++++++
    @OnOpen
    public void open(Session session, @PathParam("login") String login) {
        LOGGER.info("Session ouverte pour [" + login + "]");
        session.getUserProperties().put("login", login);
        sessions.put(session.getId(), session);
        sendMessage(session, "Session ouverte pour [" + login + "] cote serveur");
    }

    @OnClose
    public void close(Session session) {
        String login = (String) session.getUserProperties().get("login");
        LOGGER.info("Session fermee pour [" + login + "]");
        sessions.remove(session.getId());
        sendMessage(session, "Session fermee pour [" + login + "] cote serveur");
    }

    @OnError
    public void onError(Throwable error) {
        LOGGER.error(error.getMessage());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String login = (String) session.getUserProperties().get("login");
        LOGGER.info("Message recu de [" + login +"] : [" + message + "]");
    }

    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LOGGER.error("Erreur lors de l'envoi du message a la session [" + session.getId() + "] : " + e.getMessage());
        }
    }
}