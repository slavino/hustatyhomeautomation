package com.hustaty.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ArduinoThermoServerStatus implements Serializable {

//	EXAMPLE : {"LastCommunicationFromArduino":"2013-02-17 20:02:02",
//	"thermostat1":"0","thermostat2":"1","hotWaterSwitch":"0","hotWaterSupply":"0",
//	"heatingState":"1","remainingTimeForLastServerCommand":"0","freeMemory":"3066",
//	"uptime":"6172510","reset":"","t28F82D850400001F":"-0.88","t28B79F8504000082":"20.06",
//	"t28205B850400008B":"23.19","t280F5B8504000019":"20.62"}
	
	@SerializedName("LastCommunicationFromArduino")
	private String lastCommunicationFromArduino;
	
	@SerializedName("thermostat1")
	private String thermostat1;

	@SerializedName("thermostat2")
	private String thermostat2;
	
	@SerializedName("hotWaterSwitch")
	private String hotWaterSwitch;
	
	@SerializedName("hotWaterSupply")
	private String hotWaterSupply;
	
	@SerializedName("heatingState")
	private String heatingState;
	
	@SerializedName("remainingTimeForLastServerCommand")
	private String remainingTimeForLastServerCommand;
	
	@SerializedName("freeMemory")
	private String freeMemory;
	
	@SerializedName("uptime")
	private String uptime;

	@SerializedName("uptime2")
	private String uptime2;

	@SerializedName("reset")
	private String reset;
	
	@SerializedName("t28F82D850400001F")
	private String t28F82D850400001F;
	
	@SerializedName("t28B79F8504000082")
	private String t28B79F8504000082;
	
	@SerializedName("t28205B850400008B")
	private String t28205B850400008B;
	
	@SerializedName("t280F5B8504000019")
	private String t280F5B8504000019;
	
	public String getLastCommunicationFromArduino() {
		return lastCommunicationFromArduino;
	}

	public void setLastCommunicationFromArduino(String lastCommunicationFromArduino) {
		this.lastCommunicationFromArduino = lastCommunicationFromArduino;
	}

	public String getThermostat1() {
		return thermostat1;
	}

	public void setThermostat1(String thermostat1) {
		this.thermostat1 = thermostat1;
	}

	public String getThermostat2() {
		return thermostat2;
	}

	public void setThermostat2(String thermostat2) {
		this.thermostat2 = thermostat2;
	}

	public String getHotWaterSwitch() {
		return hotWaterSwitch;
	}

	public void setHotWaterSwitch(String hotWaterSwitch) {
		this.hotWaterSwitch = hotWaterSwitch;
	}

	public String getHotWaterSupply() {
		return hotWaterSupply;
	}

	public void setHotWaterSupply(String hotWaterSupply) {
		this.hotWaterSupply = hotWaterSupply;
	}

	public String getHeatingState() {
		return heatingState;
	}

	public void setHeatingState(String heatingState) {
		this.heatingState = heatingState;
	}

	public String getRemainingTimeForLastServerCommand() {
		return remainingTimeForLastServerCommand;
	}

	public void setRemainingTimeForLastServerCommand(
			String remainingTimeForLastServerCommand) {
		this.remainingTimeForLastServerCommand = remainingTimeForLastServerCommand;
	}

	public String getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(String freeMemory) {
		this.freeMemory = freeMemory;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public String getUptime2() {
		return uptime2;
	}

	public void setUptime2(String uptime2) {
		this.uptime2 = uptime2;
	}

	public String getReset() {
		return reset;
	}

	public void setReset(String reset) {
		this.reset = reset;
	}

	public String getT28F82D850400001F() {
		return t28F82D850400001F;
	}

	public void setT28F82D850400001F(String t28f82d850400001f) {
		t28F82D850400001F = t28f82d850400001f;
	}

	public String getT28B79F8504000082() {
		return t28B79F8504000082;
	}

	public void setT28B79F8504000082(String t28b79f8504000082) {
		t28B79F8504000082 = t28b79f8504000082;
	}

	public String getT28205B850400008B() {
		return t28205B850400008B;
	}

	public void setT28205B850400008B(String t28205b850400008b) {
		t28205B850400008B = t28205b850400008b;
	}

	public String getT280F5B8504000019() {
		return t280F5B8504000019;
	}

	public void setT280F5B8504000019(String t280f5b8504000019) {
		t280F5B8504000019 = t280f5b8504000019;
	}
	
}
