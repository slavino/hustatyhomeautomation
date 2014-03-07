package com.hustaty.homeautomation.enums;

/**
 * Created by user on 3/3/14.
 */
public enum SharedPreferencesKeys {

    APPLICATIONPREFERENCES_WIFI_SSID("wifiSSID"),
    APPLICATIONPREFERENCES_GLOBALSERVERIPADDRESS("globalServerIP"),
    APPLICATIONPREFERENCES_LOCALSERVERIPADDRESS("localNetworkServerIP"),
    APPLICATIONPREFERENCES_USERNAME("username"),
    APPLICATIONPREFERENCES_PASSWORD("password"),
    APPLICATIONPREFERENCES_PROTECTEDBYPIN("protectedByPIN"),
    APPLICATIONPREFERENCES_APPLICATIONPIN("applicationPIN"),
    APPLICATIONPREFERENCES_HOMEGPSLAT("homeLatitude"),
    APPLICATIONPREFERENCES_HOMEGPSLON("homeLongitude"),
    APPLICATIONPREFERENCES_GCMSENDERID("gcm_sender_id"),
    APPLICATIONPREFERENCES_SILENTTRAFFICNOTIFICATIONS("silentTrafficNotifications"),
    APPLICATIONPREFERENCES_DEVICEID("deviceID"),

    SECURITYSYSTEM_UPDATETIME("SECUPDATETIME"),
    SECURITYSYSTEM_ARMED("ARM"),
    SECURITYSYSTEM_ALARM("ALM"),
    SECURITYSYSTEM_FAULT("FLT"),
    SECURITYSYSTEM_FIRE("FIR"),
    SECURITYSYSTEM_TAMPERED("TMP"),
    SECURITYSYSTEM_ACPOWERFAILURE("AC"),
    SECURITYSYSTEM_LOWBATTERY("LB"),
    SECURITYSYSTEM_PGY("PGY"),

    HEATINGSYSTEM_HOTWATERSUPPLY("hotWaterSupply");

    private String key;

    private SharedPreferencesKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}