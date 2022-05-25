package com.example.traffic01.model.local;

/**
 * 充值记录
 */
public class RechargeRecord {
    private String date;
    private String carId;
    private Integer money;

    public RechargeRecord(String date, String carId, Integer money) {
        this.date = date;
        this.carId = carId;
        this.money = money;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }
}
