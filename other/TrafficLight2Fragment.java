package com.example.traffic01.ui.trafficlight;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.traffic01.R;
import com.example.traffic01.databinding.FragmentTrafficLightBinding;
import com.example.traffic01.model.intent.TrafficLightModel;
import com.example.traffic01.model.local.PostToLight;
import com.example.traffic01.tool.MyCallBack;
import com.example.traffic01.tool.OkhttpUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class TrafficLight2Fragment extends Fragment {

    int roadNumber = 4;
    private FragmentTrafficLightBinding binding;
    private SparseArray<List<Object>> dataList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTrafficLightBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataList = new SparseArray<>();
        for (int position = 0; position < roadNumber; position++) {
            if (position == 0) {
                // 添加标题
                dataList.put(position, Arrays.asList("路口", "红灯", "黄灯", "绿灯"));
            } else {
                getLights(position);
            }
        }
    }

    private void getLights(int position) {
        OkhttpUtil.post(
                "/transport_service/traffic_light",
                new PostToLight(position, "user1"),
                new MyCallBack(requireActivity(), TrafficLightModel.class) {
                    @Override
                    public void function(Object object) {
                        TrafficLightModel data = (TrafficLightModel) object;
                        if (data.code == OkhttpUtil.SUCCESS_CODE) {
                            String[] str = new String[4];
                            for (TrafficLightModel.StatusDTO dto : data.status) {
                                str[0] = String.valueOf(position);
                                switch (dto.status) {
                                    case "Red":
                                        str[1] = String.valueOf(dto.time);
                                        break;
                                    case "Yellow":
                                        str[2] = String.valueOf(dto.time);
                                        break;
                                    case "Green":
                                        str[3] = String.valueOf(dto.time);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            dataList.put(position, Arrays.asList(str));
                            if (position == roadNumber - 1) {
                                Log.d("dataList", dataList.toString());
                            }
                            binding.rvFirst.setLayoutManager(new LinearLayoutManager(requireContext()));
                            binding.rvFirst.setAdapter(firstAdapter());
                        }

                    }
                }
        );
    }

    private RecyclerView.Adapter firstAdapter() {
        return new RecyclerView.Adapter<ViewHolder>() {
            @NonNull
            @NotNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(requireContext()).inflate(R.layout.item_grad, parent, false));
            }

            @Override
            public int getItemCount() {
                return roadNumber;
            }

            @Override
            public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
                RecyclerView recyclerView = holder.itemView.findViewById(R.id.item_grad);

                recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
                recyclerView.setAdapter(secondAdapter(position));
            }

        };
    }

    private RecyclerView.Adapter secondAdapter(int index) {
        Log.d("TAG", "onBindViewHolder: " + dataList.get(index));
        return new RecyclerView.Adapter<ViewHolder>() {
            @NonNull
            @NotNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(requireContext()).inflate(R.layout.item_content, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
                TextView textView = holder.itemView.findViewById(R.id.textView2);
                textView.setText(dataList.get(index).get(position).toString());
                textView.setBackgroundColor(Color.rgb(98, 0, 238));
                textView.setTextColor(Color.WHITE);
            }

            @Override
            public int getItemCount() {
                return dataList.get(index).size();
            }
        };
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}