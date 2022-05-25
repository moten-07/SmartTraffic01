package com.example.traffic01.model.intent;

import java.util.List;

public class TrafficLightModel {

    public int code;
    public String msg;
    public List<StatusDTO> status;

    public static class StatusDTO {
        public int time;
        public String status;

        @Override
        public String toString() {
            return "StatusDTO{" +
                    "time=" + time +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
}
