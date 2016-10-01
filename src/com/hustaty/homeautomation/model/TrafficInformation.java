package com.hustaty.homeautomation.model;

import com.google.gson.annotations.SerializedName;

/**
 * User: hustasl
 * Date: 3/1/13
 * Time: 10:18 PM
 */
public class TrafficInformation implements IModel {

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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TrafficInformation{");
        sb.append("type='").append(type).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toHTMLFormattedString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("<b>Type:</b>").append(type);
        sb.append("<br/><b>Description:</b>").append(description);
        sb.append("<br/><b>Latitude:</b>").append(latitude);
        sb.append("<br/><b>Longitude:</b>").append(longitude);
        return sb.toString();
    }
}
