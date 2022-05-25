package com.example.traffic01.tool;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * 自定义数据格式转换，根据源码修改
 */
public class MyFormatter implements com.github.mikephil.charting.formatter.IValueFormatter {
    protected DecimalFormat mFormat;

    public MyFormatter() {
        this.mFormat = new DecimalFormat("###,###,##0.00");
    }

    @Override
    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
        return this.mFormat.format((double) v) + " %";
    }
}
