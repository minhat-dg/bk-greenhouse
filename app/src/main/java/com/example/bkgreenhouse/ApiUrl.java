package com.example.bkgreenhouse;

public class ApiUrl {
    private String LED_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-led/data/retain";
    private String LED_POST_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-led/data";
    private String PUMP_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-pump/data/retain";
    private String PUMP_POST_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-pump/data";
    private String TEMP_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-temp/data/retain";
    private String HUMID_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-humi/data/retain";

    public String getTEMP_GET_URL() {
        return TEMP_GET_URL;
    }

    public void setTEMP_GET_URL(String TEMP_GET_URL) {
        this.TEMP_GET_URL = TEMP_GET_URL;
    }

    public String getHUMID_GET_URL() {
        return HUMID_GET_URL;
    }

    public void setHUMID_GET_URL(String HUMID_GET_URL) {
        this.HUMID_GET_URL = HUMID_GET_URL;
    }

    public String getLED_GET_URL() {
        return LED_GET_URL;
    }

    public void setLED_GET_URL(String LED_GET_URL) {
        this.LED_GET_URL = LED_GET_URL;
    }

    public String getLED_POST_URL() {
        return LED_POST_URL;
    }

    public void setLED_POST_URL(String LED_POST_URL) {
        this.LED_POST_URL = LED_POST_URL;
    }

    public String getPUMP_GET_URL() {
        return PUMP_GET_URL;
    }

    public void setPUMP_GET_URL(String PUMP_GET_URL) {
        this.PUMP_GET_URL = PUMP_GET_URL;
    }

    public String getPUMP_POST_URL() {
        return PUMP_POST_URL;
    }

    public void setPUMP_POST_URL(String PUMP_POST_URL) {
        this.PUMP_POST_URL = PUMP_POST_URL;
    }
}
