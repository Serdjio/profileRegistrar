package com.tkachov.schedule;

import com.tkachov.model.DeviceCommandDefinition;
import com.tkachov.server.ProfileRegistratorImpl;
import com.tkachov.service.CableCommunicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tkachov.websocket.client.ProfileWebSocketClient;

import java.util.Calendar;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final ScheduledExecutorService scheduledPool;
    private final ProfileRegistratorImpl profileRegistrator;
    private static final int SECONDS_IN_A_DAY = 86400;

    public ScheduleService(ScheduledExecutorService scheduledPool, ProfileRegistratorImpl profileRegistrator) {
        this.scheduledPool = scheduledPool;
        this.profileRegistrator = profileRegistrator;
    }

    public void startScheduledProfilesExecutions(ProfileWebSocketClient socket) {
        ScheduledFuture<?> scheduledFuture = null;
        NavigableMap<Integer, ConcurrentLinkedQueue<DeviceCommandDefinition>> devicesMap = profileRegistrator.getDevicesMap();
        Queue<Integer> retryDeviceIds = profileRegistrator.getRetryDeviceIds();
        while (true) {
            if(scheduledFuture == null || scheduledFuture.isDone()) {
                int secondsFromBeginningOfDay = getSecondsFromBeginningOfDay();

                Map.Entry<Integer, ConcurrentLinkedQueue<DeviceCommandDefinition>> higherEntry = devicesMap.higherEntry(secondsFromBeginningOfDay);
                if (higherEntry != null) {
                    long initialDelay = higherEntry.getKey() - secondsFromBeginningOfDay;
                    scheduledFuture = scheduleWithDefinedDelay(socket, higherEntry.getValue(), initialDelay);
                } else {
                    long initialDelay = SECONDS_IN_A_DAY - secondsFromBeginningOfDay + devicesMap.firstEntry().getKey();
                    scheduledFuture = scheduleWithDefinedDelay(socket, devicesMap.firstEntry().getValue(), initialDelay);
                }
            }
            if (!retryDeviceIds.isEmpty()) {
                log.info("Retrying sending profile");
                retryDeviceIds.stream().forEach(id ->  scheduleWithDefinedDelay(socket, devicesMap.get(id), 0));
            }
        }
    }

    private ScheduledFuture<?> scheduleWithDefinedDelay(ProfileWebSocketClient socket, ConcurrentLinkedQueue<DeviceCommandDefinition> deviceCommandDefinitions, long initialDelay) {
        log.info("Scheduled run in " + initialDelay + " seconds for profile " + deviceCommandDefinitions.stream().map(deviceCommandDefinition -> deviceCommandDefinition.getDeviceId()).collect(Collectors.toList()));
        return scheduledPool.schedule(
                new CableCommunicationService(socket, deviceCommandDefinitions),
                initialDelay,
                TimeUnit.SECONDS);
    }

    private int getSecondsFromBeginningOfDay() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60) +
                (calendar.get(Calendar.MINUTE) * 60) +
                (calendar.get(Calendar.SECOND));
    }
}
