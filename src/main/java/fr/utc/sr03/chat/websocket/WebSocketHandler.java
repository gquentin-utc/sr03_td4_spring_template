package fr.utc.sr03.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);
    
    private final String wsServerName;
    private final List<WebSocketSession> sessions;
    private final List<MessageSocket> messageSocketsHistory;

    public WebSocketHandler(String wsServerName) {
        this.wsServerName = wsServerName;
        this.sessions = new ArrayList<>();
        this.messageSocketsHistory = new ArrayList<>();
    }

    /**
     * Connexion établie
     * @param session
     * @throws IOException
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        LOGGER.info(session.getId());

        // Ajout de la nouvelle session a la liste
        sessions.add(session);

        // Historique des messages
        for(MessageSocket messageSocket : messageSocketsHistory){
            session.sendMessage(new TextMessage(messageSocket.getUser()+ " : " + messageSocket.getMessage()));
        }

        LOGGER.info("Connexion etablie sur " + this.wsServerName);
    }

    /**
     * Connexion fermee
     * @param session
     * @param status
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Suppression de la session a la liste
        sessions.remove(session);

        LOGGER.info("Deconnexion de " + this.wsServerName);
    }

    /**
     * Reception d'un message
     * @param session
     * @param message
     * @throws IOException
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String receivedMessage = (String) message.getPayload();
        MessageSocket messageSocket = mapper.readValue(receivedMessage, MessageSocket.class);

        // Pour stocker le message dans l'historique
        messageSocketsHistory.add(messageSocket);

        // Envoi du message à tous les connectes
        this.broadcast(messageSocket.getUser()+ " : " + messageSocket.getMessage());

    }

    public void broadcast(String message) throws IOException {
        // Envoi du message a toutes les sessions
        // A modifier pour envoyer le message a toutes les sessions d'un seul chat
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(message));
        }
    }
}
