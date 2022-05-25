package com.example.traffic01.ui;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.traffic01.model.local.LightInfo;
import com.example.traffic01.model.local.PostToLight;
import com.example.traffic01.tool.MyCallBack;
import com.example.traffic01.tool.MyDatabaseHelper;
import com.example.traffic01.tool.HttpUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TrafficLightFragment extends Fragment {

	private final List<String> headers = Arrays.asList("路口", "红灯", "黄灯", "绿灯");
	private final List<LightInfo> lightList = new ArrayList<>();
	private FragmentTrafficLightBinding binding;
	private SQLiteDatabase db;

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

		MyDatabaseHelper helper = new MyDatabaseHelper(requireContext(), "Store.db", 1);
		db = helper.getWritableDatabase();

		headerRecycle();
		contentRecycle();
		aboutRadioGroup();
	}

	private void aboutRadioGroup() {
		binding.roadAsc.setOnClickListener(new RadioSelect());
		binding.roadDesc.setOnClickListener(new RadioSelect());
		binding.redAsc.setOnClickListener(new RadioSelect());
		binding.redDesc.setOnClickListener(new RadioSelect());
		binding.yellowAsc.setOnClickListener(new RadioSelect());
		binding.yellowDesc.setOnClickListener(new RadioSelect());
		binding.greenAsc.setOnClickListener(new RadioSelect());
		binding.greenDesc.setOnClickListener(new RadioSelect());
	}

	private void headerRecycle() {
		binding.rvFirst.setLayoutManager(new GridLayoutManager(requireContext(), 4));
		binding.rvFirst.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
			@NotNull
			@Override
			public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
				return new ViewHolder(LayoutInflater.from(requireContext()).inflate(R.layout.item_content, parent, false));
			}

			@Override
			public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
				TextView textView = holder.itemView.findViewById(R.id.textView2);
				textView.setText(headers.get(position));
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.rgb(98, 0, 238));
			}

			@Override
			public int getItemCount() {
				return headers.size();
			}
		});
	}

	private void contentRecycle() {
		binding.rvSecond.setLayoutManager(new LinearLayoutManager(requireContext()));
		binding.rvSecond.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
			@NotNull
			@Override
			public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
				return new ViewHolder(LayoutInflater.from(requireContext()).inflate(R.layout.item_light, parent, false));
			}

			@Override
			public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
				LightInfo info = lightList.get(position);
				TextView textView1 = holder.itemView.findViewById(R.id.light_road_id);
				textView1.setText(String.valueOf(info.id));
				TextView textView2 = holder.itemView.findViewById(R.id.light_road_red);
				textView2.setText(String.valueOf(info.red_light));
				TextView textView3 = holder.itemView.findViewById(R.id.light_road_yellow);
				textView3.setText(String.valueOf(info.yellow_light));
				TextView textView4 = holder.itemView.findViewById(R.id.light_road_green);
				textView4.setText(String.valueOf(info.green_light));
			}

			@Override
			public int getItemCount() {
				return lightList.size();
			}
		});
		getLightsInfo(1);

	}

	private void selectData(String orderBy, String sort) {
		lightList.clear();
		Cursor cursor = db.query("traffic_lights", null, null, null, null, null, String.format("%s %s", orderBy, sort));
		if (cursor.moveToFirst()) {
			do {
				lightList.add(new LightInfo(
						cursor.getInt(cursor.getColumnIndex("id")),
						cursor.getInt(cursor.getColumnIndex("redlight")),
						cursor.getInt(cursor.getColumnIndex("yellolight")),
						cursor.getInt(cursor.getColumnIndex("greenlight"))
				));
			} while (cursor.moveToNext());
		}
		Objects.requireNonNull(binding.rvSecond.getAdapter()).notifyDataSetChanged();
		cursor.close();
	}

	private void getLightsInfo(int index) {
		HttpUtil.post(
				"/transport_service/traffic_light",
				new PostToLight(index, "user1"),
				new MyCallBack(requireActivity(), TrafficLightModel.class) {
					@Override
					public void function(Object object) {
						TrafficLightModel data = (TrafficLightModel) object;
						Cursor cursor = db.rawQuery("select * from traffic_lights where id = " + index, null);
						if (cursor.moveToFirst()) {
							// 有数据就更新
							int timeR = 0;
							int timeY = 0;
							int timeG = 0;
							for (TrafficLightModel.StatusDTO dto : data.status) {
								switch (dto.status) {
									case "Green":
										timeG = dto.time;
										break;
									case "Yellow":
										timeY = dto.time;
										break;
									case "Red":
										timeR = dto.time;
										break;
									default:
										break;
								}
							}
							String sql = "update traffic_lights set " +
									"redlight = " + timeR + "," +
									"yellolight = " + timeY + "," +
									"greenlight = " + timeG + " " +
									" where id = " + index;
							db.execSQL(sql);
							cursor.close();
						} else {
							// 没数据就加载
							String sql = "insert into traffic_lights values ("
									+ index + ","
									+ data.status.get(0).time + ","
									+ data.status.get(1).time + ","
									+ data.status.get(2).time + ")";
							db.execSQL(sql);
						}
						if (index != (headers.size() - 1)) {
							// 回调，加载下一条数据
							getLightsInfo(index + 1);
						} else {
							selectData("id", HttpUtil.ASC);
						}
					}
				}
		);
	}

	private static class ViewHolder extends RecyclerView.ViewHolder {

		public ViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
		}
	}

	private class RadioSelect implements View.OnClickListener {
		@SuppressLint("NonConstantResourceId")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.road_asc:
					selectData("id", HttpUtil.ASC);
					binding.radio2.clearCheck();
					break;
				case R.id.road_desc:
					selectData("id", HttpUtil.DESC);
					binding.radio1.clearCheck();
					break;
				case R.id.red_asc:
					selectData("redlight", HttpUtil.ASC);
					binding.radio2.clearCheck();
					break;
				case R.id.red_desc:
					selectData("redlight", HttpUtil.DESC);
					binding.radio1.clearCheck();
					break;
				case R.id.yellow_asc:
					selectData("yellolight", HttpUtil.ASC);
					binding.radio2.clearCheck();
					break;
				case R.id.yellow_desc:
					selectData("yellolight", HttpUtil.DESC);
					binding.radio1.clearCheck();
					break;
				case R.id.green_asc:
					selectData("greenlight", HttpUtil.ASC);
					binding.radio2.clearCheck();
					break;
				case R.id.green_desc:
					selectData("greenlight", HttpUtil.DESC);
					binding.radio1.clearCheck();
					break;
				default:
					break;
			}
		}
	}
}
