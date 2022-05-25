package com.example.traffic01.model.local;

public class Recharge {

    public String carId;
    public int money;
    public String userId;

    public Recharge(String carId, int money, String userId) {
        this.carId = carId;
        this.money = money;
        this.userId = userId;
    }
}
