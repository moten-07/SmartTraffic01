package com.example.traffic01.model.intent;

import com.google.gson.annotations.SerializedName;

public class WeatherModel {

    public String msg;
    public int code;
    public DataDTO data;

    public static class DataDTO {
        public int temperature;
        @SerializedName("humidity")
        public int wd;
        @SerializedName("pm2.5")
        public int air;

        @Override
        public String toString() {
            return "DataDTO{" +
                    "temperature=" + temperature +
                    ", wd=" + wd +
                    ", air=" + air +
                    '}';
        }
    }
}
