package com.hustaty.homeautomation.model;

import com.google.gson.annotations.SerializedName;

/**
 * User: hustasl
 * Date: 2/26/13
 * Time: 11:34 AM
 */
public class DeviceLocationInfo implements IModel {

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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DeviceLocationInfo{");
        sb.append("latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", accuracy=").append(accuracy);
        sb.append(", distance=").append(distance);
        sb.append(", deviceId='").append(deviceId).append('\'');
        sb.append(", wifi='").append(wifi).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toHTMLFormattedString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("<b>Latitude:</b>").append(latitude);
        sb.append("<br/><b>Longitude:</b>").append(longitude);
        sb.append("<br/><b>Accuracy:</b>").append(accuracy);
        sb.append("<br/><b>Distance:</b>").append(distance);
        sb.append("<br/><b>DeviceId:</b>").append(deviceId);
        sb.append("<br/><b>WiFi:</b>").append(wifi);
        return sb.toString();
    }
}
