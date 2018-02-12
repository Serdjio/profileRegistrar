package com.tkachov.server;

import com.tkachov.model.DeviceCommandDefinition;
import com.tkachov.model.ProfileConfig;
import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

@Getter
public class ProfileRegistratorImpl implements ProfileRegistrator {

    public static final String H_M_S_PATTERN = "HH:mm:ss";
    public static final String SET_M_CURRENT = "set m current=";
    private final NavigableMap<Integer, ConcurrentLinkedQueue<DeviceCommandDefinition>> devicesMap = new ConcurrentSkipListMap<>();
    private final Queue<Integer> retryDeviceIds = new ConcurrentLinkedQueue<>();

    @Override
    public void registerProfile(ProfileConfig profileConfig) {
        if (profileConfig.getStartPeriod() != null) {
            int startSecondOfDay = LocalTime.parse(profileConfig.getStartPeriod(), DateTimeFormatter.ofPattern(H_M_S_PATTERN)).toSecondOfDay();
            DeviceCommandDefinition startChargeCommand = new DeviceCommandDefinition(profileConfig.getDeviceId(), "start charge", SET_M_CURRENT + profileConfig.getChargeValue());
            addToDeviceMap(startSecondOfDay, startChargeCommand);
        }
        if (profileConfig.getEndPeriod() != null) {
            int finishSecondOfDay = LocalTime.parse(profileConfig.getEndPeriod(), DateTimeFormatter.ofPattern(H_M_S_PATTERN)).toSecondOfDay();
            DeviceCommandDefinition finishChargeCommand = new DeviceCommandDefinition(profileConfig.getDeviceId(), "stop charge", SET_M_CURRENT + profileConfig.getChargeValue());
            addToDeviceMap(finishSecondOfDay, finishChargeCommand);
        }
    }

    private void addToDeviceMap(int secondOfDay, DeviceCommandDefinition commandDefinition) {
        if(devicesMap.containsKey(secondOfDay)){
            devicesMap.get(secondOfDay).add(commandDefinition);
        } else {
            ConcurrentLinkedQueue<DeviceCommandDefinition> listOfCommandDefinitions = new ConcurrentLinkedQueue<>();
            listOfCommandDefinitions.add(commandDefinition);
            devicesMap.put(secondOfDay, listOfCommandDefinitions);
        }
    }
}
