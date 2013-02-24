package com.hustaty.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * User: hustasl
 * Date: 2/24/13
 * Time: 3:51 PM
 */
public class CommonResult implements Serializable {

    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
