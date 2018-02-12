package com.tkachov.websocket.server;

import com.tkachov.websocket.exception.CantConnectToChargeCableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/charge-cables")
public class WebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    private Session session;
    private AtomicInteger counter = new AtomicInteger();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException, CantConnectToChargeCableException {
        if (counter.incrementAndGet()%3 == 0) {
            throw new CantConnectToChargeCableException("Unable to connect to device");
        }
        log.info("Message received: " + message);
        session.getBasicRemote().sendText(message);
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        log.info("Closing a WebSocket due to " + reason.getReasonPhrase());

    }

    @OnError
    public void onError(Session session, Throwable exception) {
        log.error("Error appeared");
        if (exception instanceof CantConnectToChargeCableException) {
            //ProfileRegistratorImpl profileRegistrator = ProfileRegistrarCreator.createOrGetRegistrator();
        }

    }
}
