package com.hustaty.homeautomation.model;

import com.google.gson.annotations.SerializedName;

/**
 * User: hustasl
 * Date: 3/1/13
 * Time: 10:18 PM
 */
public class TrafficInformation {

    @SerializedName("type")
    private String type;

    @SerializedName("description")
    private String description;

    @SerializedName("lat")
    private Double latitude;

    @SerializedName("lon")
    private Double longitude;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
