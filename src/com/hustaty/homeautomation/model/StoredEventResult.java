package com.hustaty.homeautomation.model;

import com.google.gson.annotations.SerializedName;

/**
 * User: hustasl
 * Date: 2/28/13
 * Time: 9:07 PM
 */
public class StoredEventResult {

    @SerializedName("valueToPass")
    private String valueToPass;

    @SerializedName("validFrom")
    private String validFrom;

    @SerializedName("validUntil")
    private String validUntil;

    public String getValueToPass() {
        return valueToPass;
    }

    public void setValueToPass(String valueToPass) {
        this.valueToPass = valueToPass;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }
}
