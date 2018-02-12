package com.tkachov.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileConfig {

    private Integer deviceId;
    private String startPeriod;
    private String endPeriod;
    private String chargeValue;


    public ProfileConfig(String startPeriod, String endPeriod, String chargeValue) {
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.chargeValue = chargeValue;
        deviceId = (int) System.nanoTime();
    }
}
