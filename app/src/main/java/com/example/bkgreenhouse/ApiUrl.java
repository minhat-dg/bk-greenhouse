package com.example.bkgreenhouse;

public class ApiUrl {
    private String LED_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-led/data/retain";
    private String PUMP1_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-pump/data/retain";
    private String PUMP2_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-pump-2/data/retain";
    private String TEMP_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-temp/data/retain";
    private String HUMID_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-humi/data/retain";
    private String MOISTURE_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-moisture/data/retain";

    public String getLED_GET_URL() {
        return LED_GET_URL;
    }

    public String getPUMP1_GET_URL() {
        return PUMP1_GET_URL;
    }

    public String getPUMP2_GET_URL() {
        return PUMP2_GET_URL;
    }

    public String getTEMP_GET_URL() {
        return TEMP_GET_URL;
    }

    public String getHUMID_GET_URL() {
        return HUMID_GET_URL;
    }

    public String getMOISTURE_GET_URL() {
        return MOISTURE_GET_URL;
    }
}
