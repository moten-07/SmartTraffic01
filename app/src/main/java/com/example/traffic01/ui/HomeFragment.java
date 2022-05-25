package com.example.traffic01.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import com.example.traffic01.R;
import com.example.traffic01.databinding.FragmentHomeBinding;
import com.example.traffic01.model.intent.RoadStateModel;
import com.example.traffic01.model.intent.WeatherModel;
import com.example.traffic01.model.local.PostToSelectRoad;
import com.example.traffic01.model.local.Road;
import com.example.traffic01.tool.MyCallBack;
import com.example.traffic01.tool.HttpUtil;
import com.google.android.material.snackbar.Snackbar;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<Road> roadModels;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        getWeather();
        initRoad();
    }

    private void getWeather() {
        HttpUtil.get(
                "/transport_service/weather",
                new MyCallBack(requireActivity(), WeatherModel.class) {
                    @Override
                    public void function(Object object) {
                        WeatherModel model = (WeatherModel) object;
                        binding.humidity.setText(String.format(getResources().getString(R.string.humidity), model.data.wd));
                        binding.pm2P5.setText(String.format(getResources().getString(R.string.pm2P5), model.data.air));
                        binding.temperature.setText(String.format(getResources().getString(R.string.temperature), model.data.temperature));
                    }
                }
        );
    }

    private void initRoad() {
        Road r1 = new Road();
        Road r2 = new Road();
        Road r3 = new Road();

        roadModels = Arrays.asList(r1, r2, r3);
        List<String> roadNames = Arrays.asList("环城快速路", "学院路", "绿藤路");

        for (int i = 0; i < roadModels.size(); i++) {
            roadModels.get(i).id = i + 1;
            roadModels.get(i).name = roadNames.get(i);
            // 绑定从网络加载的状态
            bindingState(roadModels.get(i).id, roadModels.get(i));
        }
        // 文本绑定
        binding.roadCityName1.setText(r1.name);
        binding.roadCityName2.setText(r1.name);
        binding.roadXyName.setText(r2.name);
        binding.roadLtName.setText(r3.name);
        binding.imageRefresh.setOnClickListener(v -> {
            getWeather();
            Snackbar.make(binding.imageRefresh, "已刷新", Snackbar.LENGTH_SHORT).show();
        });

    }

    private void bindingColor(int index) {
        // 路况标识色绑定
        switch (index) {
            case 0:
                // 图片重绘
                VectorDrawableCompat compat = VectorDrawableCompat.create(
                        getResources(),
                        R.drawable.ic__road_ring,
                        requireActivity().getTheme()
                );
                if (compat != null) {
                    compat.setTint(getResources().getColor(
                            setRoadColor(roadModels.get(0).state),
                            requireActivity().getTheme()
                    ));
                }
                // 底图重新绑定
                binding.roadCity.setImageDrawable(compat);
                break;
            case 1:
                binding.roadXy.setBackgroundColor(getResources().getColor(
                        setRoadColor(roadModels.get(index).state),
                        requireActivity().getTheme()
                ));
                break;
            case 2:
                binding.roadLt.setBackgroundColor(getResources().getColor(
                        setRoadColor(roadModels.get(index).state),
                        requireActivity().getTheme()
                ));
                break;
            default:
                break;
        }


    }

    private int setRoadColor(int index) {
        // 颜色设定
        switch (index) {
            case 1:
                return R.color.road_unobstructed;
            case 2:
                return (R.color.road_smoother);
            case 3:
                return (R.color.road_crowded);
            case 4:
                return (R.color.road_congestion);
            case 5:
                return R.color.road_report;
            default:
                return R.color.grey;
        }
    }

    private void bindingState(int id, Road road) {
        HttpUtil.post(
                "/transport_service/road_state",
                new PostToSelectRoad(id, "user01"),
                new MyCallBack(requireActivity(), RoadStateModel.class) {
                    @Override
                    public void function(Object object) {
                        RoadStateModel data = (RoadStateModel) object;
                        if (data.code == HttpUtil.SUCCESS_CODE) {
                            road.state = data.roadState;
                            bindingColor(id - 1);
                        }
                        if (data.code == HttpUtil.FAIL_CODE) {
                            Toast.makeText(requireContext(), data.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
