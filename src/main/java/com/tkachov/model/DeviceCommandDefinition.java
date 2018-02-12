package com.tkachov.model;

import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeviceCommandDefinition {
    private Integer deviceId;
    private String command;
    private String chargeValue;

    public String toJson() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this, DeviceCommandDefinition.class);
    }
}
