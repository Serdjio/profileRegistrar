package com.tkachov;

import com.tkachov.model.ProfileConfig;
import com.tkachov.schedule.ScheduleService;
import com.tkachov.server.ProfileRegistrarCreator;
import com.tkachov.server.ProfileRegistratorImpl;
import com.tkachov.websocket.client.ProfileWebSocketClient;
import com.tkachov.websocket.exception.RegisterProfileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class RegisterProfileClient {
    private static final Logger log = LoggerFactory.getLogger(RegisterProfileClient.class);

    public static void main(String[] args) {

        ProfileWebSocketClient socket = configureWebSocketClient();
        ProfileRegistratorImpl profileRegistrator = ProfileRegistrarCreator.createOrGetRegistrator();
        ScheduleService scheduleService = new ScheduleService(Executors.newScheduledThreadPool(4), profileRegistrator);

        createProfiles().forEach(profileRegistrator::registerProfile);
        scheduleService.startScheduledProfilesExecutions(socket);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> log.info("Scheduler is stopped")));
    }

    private static ProfileWebSocketClient configureWebSocketClient() {
        String dest = "ws://localhost:8080/charge-cables";
        ProfileWebSocketClient socket = new ProfileWebSocketClient();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(socket, new URI(dest));
        } catch (DeploymentException | IOException | URISyntaxException e) {
            log.error("Unable to start socket ", e.getCause());
            throw new RegisterProfileException("Unable to start socket ", e);
        }
        return socket;
    }

    private static List<ProfileConfig> createProfiles() {
        ProfileConfig profileConfig1 = new ProfileConfig(afterSeconds(10), afterSeconds(15), "15 Amper");
        ProfileConfig profileConfig2 = new ProfileConfig(afterSeconds(21), null, "MAX");
        return Arrays.asList(profileConfig1, profileConfig2);
    }

    private static String afterSeconds(int sec) {
        return LocalDateTime.now().plusSeconds(sec).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
