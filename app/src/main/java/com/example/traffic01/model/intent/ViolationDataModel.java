package com.example.traffic01.model.intent;

import java.util.List;

public class ViolationDataModel {
    public int code;
    public List<DataDTO> data;
    public String msg;

    public static class DataDTO {
        public String carnumber;
        public String datetime;
        public String paddr;
        public String pcode;
    }
}
