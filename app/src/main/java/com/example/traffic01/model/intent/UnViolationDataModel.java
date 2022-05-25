package com.example.traffic01.model.intent;

import java.util.List;

public class UnViolationDataModel {
    @Override
    public String toString() {
        return "UnViolationDataModel{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }

    public int code;
    public List<UnViolationDataModel.DataDTO> data;
    public String msg;

    public static class DataDTO {
        public String carnumber;
        public String datetime;
        public String paddr;
        public String pcode;
    }
}
