package com.example.traffic01.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.traffic01.R;
import com.example.traffic01.model.intent.UnViolationDataModel;
import com.example.traffic01.model.intent.ViolationDataModel;
import com.example.traffic01.model.local.PostToViolation;
import com.example.traffic01.tool.MyCallBack;
import com.example.traffic01.tool.HttpUtil;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViolationFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.violation_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HttpUtil.post(
                "/transport_service/traffic_unViolation",
                new PostToViolation(null, "user1"),
                new MyCallBack(requireActivity(), UnViolationDataModel.class) {
                    @Override
                    public void function(Object object) {
                        UnViolationDataModel model = (UnViolationDataModel) object;
                        Log.d("TAG", "function: " + model.data.size());
                        List<UnViolationDataModel.DataDTO> dtos = model.data;
                        getViolationSize(view, dtos.size());
                    }
                }
        );
//        getViolationSize(view, 24);


    }

    private void getViolationSize(View view, int unViolationSize) {
        HttpUtil.post(
                "/transport_service/traffic_violation",
                new PostToViolation("*", "user1"),
                new MyCallBack(requireActivity(), ViolationDataModel.class) {
                    @Override
                    public void function(Object object) {
                        ViolationDataModel model = (ViolationDataModel) object;
                        List<ViolationDataModel.DataDTO> dtos = model.data;
                        aboutChart(view, dtos.size(), unViolationSize);
                    }
                }
        );
    }

    private void aboutChart(View view, int violationSize, int unViolationSize) {
        Log.d("TAG", violationSize + "--" + unViolationSize);
        PieChart chart = view.findViewById(R.id.chart);
        List<PieEntry> entries = new ArrayList<>(Arrays.asList(
                new PieEntry((float) violationSize, "有违章"),
                new PieEntry((float) unViolationSize, "无违章")
        ));
        PieDataSet dataSet = new PieDataSet(entries, "违章数据分析");
        dataSet.setColors(
                Color.rgb(144, 1, 228),
                Color.rgb(255, 0, 0)
        );
        // 设置文本大小
        dataSet.setValueTextSize(22f);
        // 数据绑定到data对象
        PieData data = new PieData(dataSet);

        // 设置数据格式，百分比是PercentFormatter(),但只显示小数点后一位，做了些许修改
        data.setValueFormatter(new PercentFormatter());
        // 设置数据颜色
        data.setValueTextColor(Color.WHITE);
        // 显示为百分比
        chart.setUsePercentValues(true);
        // 不显示文字，
        chart.setDrawEntryLabels(false);
        // 设置中心圆的半径，0为饼图
        chart.setHoleRadius(50);
        // 去掉中心圆
        chart.setDrawHoleEnabled(false);
        // 禁止旋转
        chart.setRotationEnabled(false);
        // 不绘制图例（绘制半天不符合要求,直接手绘一个）
        chart.getLegend().setEnabled(false);
        // 禁用描述()右下英文
        chart.getDescription().setEnabled(false);

        // 数据绑定
        chart.setData(data);
        // 数据重新绘制（必须），因为数据在子线程中，回调嵌套了，会显示No chart data available
        chart.requestLayout();

//        // 图例
//        Legend legend = chart.getLegend();
//        // 图例类型，线
//        legend.setForm(Legend.LegendForm.LINE);
//        // 图例尺寸
//        legend.setFormSize(16f);
//        // 图例宽度
//        legend.setFormLineWidth(8f);
//        // 图例水平位置
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        // 文本大小
//        legend.setTextSize(18f);
    }
}
