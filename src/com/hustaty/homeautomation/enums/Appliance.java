package com.hustaty.homeautomation.enums;

/**
 * User: hustasl
 * Date: 2/24/13
 * Time: 2:32 PM
 */
public enum Appliance {

    HOTWATER("HotWater"),
    HEATING("Heating"),
    HEATING_INTERRUPT("HeatingInterrupt");

    private String value;

    private Appliance(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
