package com.tkachov.websocket.client;

import com.tkachov.websocket.exception.RegisterProfileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;

@ClientEndpoint
public class ProfileWebSocketClient {
    private static final Logger log = LoggerFactory.getLogger(ProfileWebSocketClient.class);
    private final String uri = "ws://localhost:8080/charge-cables";
    private Session session;

    @OnOpen
    public void onOpen(Session session){
        this.session=session;
    }

    @OnMessage
    public void onMessage(String message, Session session){
        log.info("Client message received "+ message);
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        log.error("Closing a WebSocket due to " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        log.error("Error client appeared", thr.getCause());

    }

    public void sendMessage(String message){
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            log.error("Unable to send message from client", ex.getCause());
            throw new RegisterProfileException(ex);
        }
    }
}
