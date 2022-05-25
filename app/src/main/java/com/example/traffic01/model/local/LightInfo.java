package com.example.traffic01.model.local;

public class LightInfo {
    public int id;
    public int red_light;
    public int yellow_light;
    public int green_light;

    public LightInfo(int id, int red_light, int yellow_light, int green_light) {
        this.id = id;
        this.red_light = red_light;
        this.yellow_light = yellow_light;
        this.green_light = green_light;
    }

    @Override
    public String toString() {
        return "LightInfo{" +
                "id=" + id +
                ", red_light=" + red_light +
                ", yellow_light=" + yellow_light +
                ", green_light=" + green_light +
                '}';
    }
}
