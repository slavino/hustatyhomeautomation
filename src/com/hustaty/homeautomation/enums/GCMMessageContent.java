package com.hustaty.homeautomation.enums;

/**
 * Created by user on 21-Jul-14.
 */
public enum GCMMessageContent {

    IP_ADDRESS("IP"),
    MESSAGE("message"),
    PUBLIC_SSL_PORT("publicSSLport");

    private String key;

    private GCMMessageContent(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
