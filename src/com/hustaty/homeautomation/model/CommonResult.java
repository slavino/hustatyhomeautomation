package com.hustaty.homeautomation.model;

import com.google.gson.annotations.SerializedName;

/**
 * User: hustasl
 * Date: 2/24/13
 * Time: 3:51 PM
 */
public class CommonResult implements IModel {

    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CommonResult{");
        sb.append("result='").append(result).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toHTMLFormattedString() {
        return "<B>"+result+"</B>";
    }
}
