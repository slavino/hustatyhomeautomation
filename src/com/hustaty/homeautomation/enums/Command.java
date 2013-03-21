package com.hustaty.homeautomation.enums;

/**
 * User: hustasl
 * Date: 2/24/13
 * Time: 2:42 PM
 */
public enum Command {

    HOTWATER_ON("hotWaterON"),
    HOTWATER_OFF("hotWaterOFF"),
    HEATING_ON("heatingON"),
    HEATING_OFF("heatingOFF"),
    HEATINGINTERRUPT_ON("heatingInterruptON"),
    HEATINGINTERRUPT_OFF("heatingInterruptOFF"),
    HEATINGPUMP_ON("heatingPumpON"),
    HEATINGPUMP_OFF("heatingPumpOFF");

    private String value;

    private Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
