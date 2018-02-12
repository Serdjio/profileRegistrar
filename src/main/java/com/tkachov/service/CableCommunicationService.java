package com.tkachov.service;

import com.tkachov.model.DeviceCommandDefinition;
import com.tkachov.websocket.client.ProfileWebSocketClient;
import lombok.AllArgsConstructor;

import java.util.Queue;

@AllArgsConstructor
public class CableCommunicationService implements Runnable {

    private final ProfileWebSocketClient webSocketClient;
    private Queue<DeviceCommandDefinition> deviceCommandDefinitions;

    @Override
    public void run() {
        for (DeviceCommandDefinition commandDefinition : deviceCommandDefinitions) {
            webSocketClient.sendMessage(commandDefinition.toJson());
        }
    }
}
