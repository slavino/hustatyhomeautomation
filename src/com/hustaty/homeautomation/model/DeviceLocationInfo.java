package com.hustaty.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * User: hustasl
 * Date: 2/26/13
 * Time: 11:34 AM
 */
public class DeviceLocationInfo implements Serializable {

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lon")
    private double longitude;

    @SerializedName("accuracy")
    private double accuracy;

    @SerializedName("distance")
    private double distance;

    @SerializedName("deviceID")
    private String deviceId;

    @SerializedName("wifi")
    private String wifi;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }
}
