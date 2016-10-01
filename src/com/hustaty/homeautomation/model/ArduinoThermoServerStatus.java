package com.hustaty.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ArduinoThermoServerStatus implements IModel {

    private static final String UNKNOWN_TEMPERATURE = "--.--";
    private static final String UNKNOWN_HUMIDITY = "--.--";
    private static final String UNKNOWN_STATE = "-1";
    private static final String UNKNOWN_VALUE = "--";
    private static final String DEFAULT_VALUE = "0";

	@SerializedName("LastCommunicationFromArduino")
	private String lastCommunicationFromArduino = new Date().toString();
	
	@SerializedName("thermostat1")
	private String thermostat1 = UNKNOWN_STATE;

	@SerializedName("thermostat2")
	private String thermostat2 = UNKNOWN_STATE;
	
	@SerializedName("hotWaterSwitch")
	private String hotWaterSwitch = UNKNOWN_STATE;
	
	@SerializedName("hotWaterSupply")
	private String hotWaterSupply = UNKNOWN_STATE;
	
	@SerializedName("heatingState")
	private String heatingState = UNKNOWN_STATE;
	
	@SerializedName("remainingTimeForLastServerCommand")
	private String remainingTimeForLastServerCommand = UNKNOWN_STATE;
	
	@SerializedName("freeMemory")
	private String freeMemory = UNKNOWN_VALUE;
	
	@SerializedName("uptime")
	private String uptime = UNKNOWN_VALUE;

	@SerializedName("uptime2")
	private String uptime2 = UNKNOWN_VALUE;

	@SerializedName("reset")
	private String reset = UNKNOWN_VALUE;

    // outside
	@SerializedName("t28F82D850400001F")
	private String t28F82D850400001F = UNKNOWN_TEMPERATURE;

    // bedroom
	@SerializedName("t28B79F8504000082")
	private String t28B79F8504000082 = UNKNOWN_TEMPERATURE;

    // upper lobby
	@SerializedName("t28205B850400008B")
	private String t28205B850400008B = UNKNOWN_TEMPERATURE;

    // workroom
	@SerializedName("t280F5B8504000019")
	private String t280F5B8504000019 = UNKNOWN_TEMPERATURE;

    // entrance lobby
    @SerializedName("t28F1E685040000DB")
    private String t28F1E685040000DB = UNKNOWN_TEMPERATURE;

    // kitchen
    @SerializedName("t28C9C9AA040000EA")
    private String t28C9C9AA040000EA = UNKNOWN_TEMPERATURE;

    //southern childroom
    @SerializedName("t288b4c5605000020")
    private String t288b4c5605000020 = UNKNOWN_TEMPERATURE;

    //nothern childroom
    @SerializedName("t28e6c455050000d4")
    private String t28e6c455050000d4 = UNKNOWN_TEMPERATURE;

    //bedroom 2
    @SerializedName("t282a54ab0400004e")
    private String t282a54ab0400004e = UNKNOWN_TEMPERATURE;

    @SerializedName("ARM")
    private String securityArmed = DEFAULT_VALUE;

    @SerializedName("ALM")
    private String securityAlarm = DEFAULT_VALUE;

    @SerializedName("FIR")
    private String securityFire = DEFAULT_VALUE;

    @SerializedName("TMP")
    private String securityTamper = DEFAULT_VALUE;

    @SerializedName("PNC")
    private String securityPanic = DEFAULT_VALUE;

    @SerializedName("FLT")
    private String securityFault = DEFAULT_VALUE;

    @SerializedName("AC")
    private String securityPowerSupply = DEFAULT_VALUE;

    @SerializedName("LB")
    private String securityLowBattery = DEFAULT_VALUE;

    @SerializedName("PGY")
    private String securityPgY = DEFAULT_VALUE;

    @SerializedName("nightHour")
    private Boolean nightHour;

    @SerializedName("kidzHum")
    private String kidzHum = UNKNOWN_HUMIDITY;

    @SerializedName("kidzTemp")
    private String kidzTemp = UNKNOWN_TEMPERATURE;

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

    // outside
    public String getT28F82D850400001F() {
		return t28F82D850400001F;
	}

	public void setT28F82D850400001F(String t28f82d850400001f) {
		t28F82D850400001F = t28f82d850400001f;
	}

    // bedroom
	public String getT28B79F8504000082() {
		return t28B79F8504000082;
	}

	public void setT28B79F8504000082(String t28b79f8504000082) {
		t28B79F8504000082 = t28b79f8504000082;
	}

    // upper lobby
	public String getT28205B850400008B() {
		return t28205B850400008B;
	}

	public void setT28205B850400008B(String t28205b850400008b) {
		t28205B850400008B = t28205b850400008b;
	}

    // workroom
	public String getT280F5B8504000019() {
		return t280F5B8504000019;
	}

	public void setT280F5B8504000019(String t280f5b8504000019) {
		t280F5B8504000019 = t280f5b8504000019;
	}

    // entrance lobby
    public String getT28F1E685040000DB() {
        return t28F1E685040000DB;
    }

    public void setT28F1E685040000DB(String t28F1E685040000DB) {
        this.t28F1E685040000DB = t28F1E685040000DB;
    }

    public String getT28C9C9AA040000EA() {
        return t28C9C9AA040000EA;
    }

    public void setT28C9C9AA040000EA(String t28C9C9AA040000EA) {
        this.t28C9C9AA040000EA = t28C9C9AA040000EA;
    }

    //bedroom 2
    public String getT282a54ab0400004e() {
        return t282a54ab0400004e;
    }

    public void setT282a54ab0400004e(String t282a54ab0400004e) {
        this.t282a54ab0400004e = t282a54ab0400004e;
    }

    public String getSecurityArmed() {
        return securityArmed;
    }

    public void setSecurityArmed(String securityArmed) {
        this.securityArmed = securityArmed;
    }

    public String getSecurityAlarm() {
        return securityAlarm;
    }

    public void setSecurityAlarm(String securityAlarm) {
        this.securityAlarm = securityAlarm;
    }

    public String getSecurityFire() {
        return securityFire;
    }

    public void setSecurityFire(String securityFire) {
        this.securityFire = securityFire;
    }

    public String getSecurityTamper() {
        return securityTamper;
    }

    public void setSecurityTamper(String securityTamper) {
        this.securityTamper = securityTamper;
    }

    public String getSecurityPanic() {
        return securityPanic;
    }

    public void setSecurityPanic(String securityPanic) {
        this.securityPanic = securityPanic;
    }

    public String getSecurityFault() {
        return securityFault;
    }

    public void setSecurityFault(String securityFault) {
        this.securityFault = securityFault;
    }

    public String getSecurityPowerSupply() {
        return securityPowerSupply;
    }

    public void setSecurityPowerSupply(String securityPowerSupply) {
        this.securityPowerSupply = securityPowerSupply;
    }

    public String getSecurityLowBattery() {
        return securityLowBattery;
    }

    public void setSecurityLowBattery(String securityLowBattery) {
        this.securityLowBattery = securityLowBattery;
    }

    public String getSecurityPgY() {
        return securityPgY;
    }

    public void setSecurityPgY(String securityPgY) {
        this.securityPgY = securityPgY;
    }

    public Boolean getNightHour() {
        return nightHour;
    }

    public void setNightHour(Boolean nightHour) {
        this.nightHour = nightHour;
    }

    public String getT288b4c5605000020() {
        return t288b4c5605000020.toString();
    }

    public void setT288b4c5605000020(String t288b4c5605000020) {
        this.t288b4c5605000020 = t288b4c5605000020;
    }

    public String getT28e6c455050000d4() {
        return t28e6c455050000d4.toString();
    }

    public void setT28e6c455050000d4(String t28e6c455050000d4) {
        this.t28e6c455050000d4 = t28e6c455050000d4;
    }

    public String getKidzHum() {
        return kidzHum;
    }

    public void setKidzHum(String kidzHum) {
        this.kidzHum = kidzHum;
    }

    public String getKidzTemp() {
        return kidzTemp;
    }

    public void setKidzTemp(String kidzTemp) {
        this.kidzTemp = kidzTemp;
    }

    @Override
    public String toHTMLFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>HotWater:</b> " + hotWaterSupply + "<br/>")
                .append("<b>Heating:</b> " + heatingState + "<br/>")
                .append("<b>Armed:</b> " + securityArmed + "<br/>")
                .append("<b>Alarm:</b> " + securityAlarm + "<br/>")
                .append("<b>Power supply loss:</b> " + securityPowerSupply + "<br/>")
                .append("<b>Low Battery:</b> " + securityLowBattery + "<br/>")
                .append("<b>Night Arm:</b> " + securityPgY + "<br/>")
                .append("<b>Outside:</b> " + t28F82D850400001F + "&deg;C<br/>")
                .append("<b>Bedroom:</b> " + t28B79F8504000082 + "&deg;C<br/>")
                .append("<b>Bedroom WiFi:</b> " + t282a54ab0400004e + "&deg;C<br/>")
                .append("<b>Upper Lobby:</b> " + t28205B850400008B + "&deg;C<br/>")
                .append("<b>Work Room:</b> " + t280F5B8504000019 + "&deg;C<br/>")
                .append("<b>Entrance Lobby:</b> " + t28F1E685040000DB + "&deg;C<br/>")
                .append("<b>North childroom:</b> " + t28e6c455050000d4 + "&deg;C<br/>")
                .append("<b>South childroom:</b> " + t288b4c5605000020 + "&deg;C<br/>");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ArduinoThermoServerStatus{" +
                "lastCommunicationFromArduino='" + lastCommunicationFromArduino + '\'' +
                ", thermostat1='" + thermostat1 + '\'' +
                ", thermostat2='" + thermostat2 + '\'' +
                ", hotWaterSwitch='" + hotWaterSwitch + '\'' +
                ", hotWaterSupply='" + hotWaterSupply + '\'' +
                ", heatingState='" + heatingState + '\'' +
                ", remainingTimeForLastServerCommand='" + remainingTimeForLastServerCommand + '\'' +
                ", freeMemory='" + freeMemory + '\'' +
                ", uptime='" + uptime + '\'' +
                ", uptime2='" + uptime2 + '\'' +
                ", reset='" + reset + '\'' +
                ", t28F82D850400001F='" + t28F82D850400001F + '\'' +
                ", t28B79F8504000082='" + t28B79F8504000082 + '\'' +
                ", t28205B850400008B='" + t28205B850400008B + '\'' +
                ", t280F5B8504000019='" + t280F5B8504000019 + '\'' +
                ", t28F1E685040000DB='" + t28F1E685040000DB + '\'' +
                ", t28C9C9AA040000EA='" + t28C9C9AA040000EA + '\'' +
                ", t288b4c5605000020='" + t288b4c5605000020 + '\'' +
                ", t28e6c455050000d4='" + t28e6c455050000d4 + '\'' +
                ", t282a54ab0400004e='" + t282a54ab0400004e + '\'' +
                ", kidzTemp='" + kidzTemp + "'" +
                ", kidzHum='" + kidzHum + "'" +
                ", securityArmed='" + securityArmed + '\'' +
                ", securityAlarm='" + securityAlarm + '\'' +
                ", securityFire='" + securityFire + '\'' +
                ", securityTamper='" + securityTamper + '\'' +
                ", securityPanic='" + securityPanic + '\'' +
                ", securityFault='" + securityFault + '\'' +
                ", securityPowerSupply='" + securityPowerSupply + '\'' +
                ", securityLowBattery='" + securityLowBattery + '\'' +
                ", securityPgY='" + securityPgY + '\'' +
                ", nightHour=" + nightHour +
                '}';
    }
}
