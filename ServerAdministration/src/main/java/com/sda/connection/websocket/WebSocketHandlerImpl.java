package com.sda.connection.websocket;

import com.sda.connection.constant.ConstantPool;
import com.sda.connection.service.WebSocketTransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Component
public class WebSocketHandlerImpl implements WebSocketHandler {

    @Autowired
    private WebSocketTransportService webSocketTransportService;

    private Logger logger = LoggerFactory.getLogger(WebSocketHandlerImpl.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        logger.info ("User {} connecting to SSH server.", webSocketSession.getAttributes().get(ConstantPool.USER_ID));
        webSocketTransportService.initConnection(webSocketSession);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        if (webSocketMessage instanceof TextMessage) {
            String message = webSocketMessage.getPayload().toString();
            if(message.contains("masterPassword"))
                webSocketTransportService.recvHandle(((TextMessage)webSocketMessage).getPayload(), webSocketSession);
            else {
                logger.info("User {} issued command {}", webSocketSession.getAttributes().get(ConstantPool.USER_ID), webSocketMessage.getPayload());
                webSocketTransportService.recvHandle(((TextMessage) webSocketMessage).getPayload(), webSocketSession);
            }
        } else {
            System.out.println("Unexpected WebSocket message type: " + webSocketMessage);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        logger.error("Connection could not be made. ");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.info("User {} disconnected", webSocketSession.getAttributes().get(ConstantPool.USER_ID));
        webSocketTransportService.close(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
