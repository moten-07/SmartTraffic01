package com.example.traffic01.model.intent;

public class RoadStateModel {
    public String msg;
    public int code;
    public int roadState;

    @Override
    public String toString() {
        return "RoadStateModel{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", roadState=" + roadState +
                '}';
    }
}
