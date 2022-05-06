package com.example.bkgreenhouse;

import android.util.Log;

public class ApiUrl {
    private String LED_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-led/data/retain";
    private String BUZZER_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-buzzer/data/retain";
    private String PUMP1_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-pump/data/retain";
    private String PUMP2_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-pump-2/data/retain";
    private String TEMP_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-temp/data/retain";
    private String HUMID_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-humi/data/retain";
    private String MOISTURE_GET_URL = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-moisture/data/retain";
    private String CREATE_ACTION = "https://io.adafruit.com/api/v2/luongcao2202/actions";
    private String DELETE_ACTION = "https://io.adafruit.com/api/v2/luongcao2202/triggers/";
    private String TEMP_GET = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-temp/data";
    private String HUMID_GET = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-humi/data";
    private String MOISTURE_GET = "https://io.adafruit.com/api/v2/luongcao2202/feeds/bbc-moisture/data";
    private String FEED_LED = "1822260";
    private String FEED_MOISTURE = "1852970";
    private String FEED_HUMID = "1833235";
    private String FEED_BUZZER = "1859160";
    private String FEED_PUMP1 = "1833222";
    private String FEED_PUMP2 = "1852973";
    private String FEED_TEMP = "1825911";

    public String getHUMID_GET() {
        return HUMID_GET;
    }

    public String getMOISTURE_GET() {
        return MOISTURE_GET;
    }

    public String getFEED_LED() {
        return FEED_LED;
    }

    public String getFEED_MOISTURE() {
        return FEED_MOISTURE;
    }

    public String getFEED_HUMID() {
        return FEED_HUMID;
    }

    public String getFEED_BUZZER() {
        return FEED_BUZZER;
    }

    public String getFEED_PUMP1() {
        return FEED_PUMP1;
    }

    public String getFEED_PUMP2() {
        return FEED_PUMP2;
    }

    public String getFEED_TEMP() {
        return FEED_TEMP;
    }

    public String getLED_GET_URL() {
        return LED_GET_URL;
    }

    public String getCreateReactiveActionBody(String op, String feedId, String triggerValue, String actionFeedId, String actionValue){
        return "{\"trigger\":{\"trigger_type\":\"reactive\",\"feed_id\":\""
                + feedId +"\",\"operator\":\""
                + op + "\",\"to_feed_id\":\"\",\"value\":\""
                + triggerValue + "\",\"action\":\"feed\",\"notify_limit\":\"0\",\"notify_on_reset\":0,\"action_feed_id\":\""
                + actionFeedId +"\",\"action_value\":\""
                + actionValue + "\"}}";
    }

    public String getCreateScheduleActionBody(String actionFeedId, String triggerMinute, String triggerHour, String triggerEvery, String value, String timeUnit){
        String trigger = "";
        switch(timeUnit){
            case "phút":
                trigger = "*/" +triggerEvery+ " * * * *";
                break;
            case "giờ":
                if (triggerEvery.equals("1")){
                    trigger = "0" + " * * * *";
                }
                else {
                    trigger = "0 */" + triggerEvery + " * * *";
                }
                break;
            case "ngày":
                if (triggerEvery.equals("1")){
                    trigger = triggerMinute + " " + triggerHour + " *" + " * *";
                } else {
                    trigger = triggerMinute + " " + triggerHour + " 1/" + triggerEvery + " * *";
                }
                break;
            case "tuần":
                switch (triggerEvery){
                    case "Thứ hai":
                        triggerEvery = "mon";
                        break;
                    case "Thứ ba":
                        triggerEvery = "tue";
                        break;
                    case "Thứ tư":
                        triggerEvery = "wed";
                        break;
                    case "Thứ năm":
                        triggerEvery = "thu";
                        break;
                    case "Thứ sáu":
                        triggerEvery = "fri";
                        break;
                    case "Thứ bảy":
                        triggerEvery = "sat";
                        break;
                    case "Chủ nhật":
                        triggerEvery = "sun";
                        break;
                }
                trigger = triggerMinute+ " " + triggerHour + " * * " + triggerEvery;
                break;
            default:
                return "";
        }
        return "{\"trigger\":{\"trigger_type\":\"schedule\",\"value\":\""
                    + trigger + "\",\"action\":\"feed\",\"action_feed_id\":\""
                    + actionFeedId + "\",\"action_value\":\""
                    + value + "\"}}";
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

    public String getCREATE_ACTION() {
        return CREATE_ACTION;
    }

    public String getDELETE_ACTION() {
        return DELETE_ACTION;
    }

    public String getBUZZER_GET_URL() {
        return BUZZER_GET_URL;
    }

    public String getTEMP_GET() {
        return TEMP_GET;
    }
}
