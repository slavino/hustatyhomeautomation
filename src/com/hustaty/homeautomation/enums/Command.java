package com.hustaty.homeautomation.enums;

/**
 * User: hustasl
 * Date: 2/24/13
 * Time: 2:42 PM
 */
public enum Command {

    ON("ON"),
    OFF("OFF");

    private String value;

    private Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
