package com.hustaty.homeautomation.model;

import com.google.gson.annotations.SerializedName;

/**
 * User: hustasl
 * Date: 2/28/13
 * Time: 9:07 PM
 */
public class StoredEventResult implements IModel {

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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StoredEventResult{");
        sb.append("valueToPass='").append(valueToPass).append('\'');
        sb.append(", validFrom='").append(validFrom).append('\'');
        sb.append(", validUntil='").append(validUntil).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toHTMLFormattedString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("<b>ValueToPass:</b>").append(valueToPass);
        sb.append("<br/><b>ValidFrom:<b>").append(validFrom);
        sb.append("<br/><b>ValidUntil:</b>").append(validUntil);
        return sb.toString();
    }
}
