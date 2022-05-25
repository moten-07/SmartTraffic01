package com.example.traffic01.ui;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.traffic01.R;
import com.example.traffic01.databinding.FragmentEtcBinding;
import com.example.traffic01.model.intent.BaseModel;
import com.example.traffic01.model.local.Recharge;
import com.example.traffic01.model.local.RechargeRecord;
import com.example.traffic01.tool.MyCallBack;
import com.example.traffic01.tool.MyDatabaseHelper;
import com.example.traffic01.tool.HttpUtil;
import com.google.android.material.snackbar.Snackbar;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

public class EtcFragment extends Fragment {

    private FragmentEtcBinding binding;
    /**
     * 全局变量
     * 充值对象、充值数目、当前小车标识
     * 数据库、 充值记录列表、余额列表（暂未进行数据库改造）
     * 小车列表
     */
    private String cardId;
    private Integer money;
    private Integer index = 0;
    private SQLiteDatabase db;
    private List<String> dialogShowList;
    private List<Integer> balance;
    private List<String> carList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEtcBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        balance = new ArrayList<>();
        // 显示余额
        carList = Arrays.asList("1号", "2号", "3号");
        aboutDatabase();
        // 从数据库加载数据
        selectData();

        // 充值金额列表
        final List<Integer> moneyList = Arrays.asList(100, 200, 300);

        ArrayAdapter<String> numberAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, carList);
        ArrayAdapter<Integer> moneyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, moneyList);
        numberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moneyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 下拉列表点击事件，下同
        OnItemSelectedListener listener1 = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cardId = carList.get(position);
                index = position;
                // 余额同步更新
                binding.balance.setText(String.format("%s元", balance.get(index)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        OnItemSelectedListener listener2 = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                money = moneyList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        // 绑定下拉框1号
        binding.spinnerCarNumber.setAdapter(numberAdapter);
        binding.spinnerCarNumber.setOnItemSelectedListener(listener1);

        // 绑定下拉框2号
        binding.spinnerAddMoney.setAdapter(moneyAdapter);
        binding.spinnerAddMoney.setOnItemSelectedListener(listener2);


        // 充值按钮
        binding.buttonRecharge.setOnClickListener(v -> HttpUtil.post(
                "/transport_service/car_recharge",
                new Recharge(cardId, money, "user01"),
                new MyCallBack(requireActivity(), BaseModel.class) {
                    @Override
                    public void function(Object object) {
                        BaseModel data = (BaseModel) object;
                        if (data.code == HttpUtil.SUCCESS_CODE) {
                            // 余额修改
                            balance.set(index, balance.get(index) + money);
                            binding.balance.setText(String.format("%s元", balance.get(index)));

                            // 时间设置
                            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
                            Date date = new Date();
                            RechargeRecord rechargeRecord = new RechargeRecord(format.format(date), cardId, money);
                            // 数据写入
                            db.execSQL("insert into recharge(date,name,money) values(\"" + rechargeRecord.getDate() + "\",\"" + rechargeRecord.getCarId() + "\"," + rechargeRecord.getMoney() + ")");
                            db.execSQL("update car_balance set balance = balance + " + rechargeRecord.getMoney() + " where carname = \"" + rechargeRecord.getCarId() + "\"");
                            String string = String.format("%s%s小车充值%s元", format.format(date), cardId, money);
                            binding.topShow.setText(string);
                        }
                        Snackbar.make(v, data.msg, Snackbar.LENGTH_SHORT).show();
                    }
                }));

        // 更多 按钮
        binding.showMore.setOnClickListener(v -> showDialog());
        // 查询 按钮
        binding.buttonSelect.setOnClickListener(v -> {
            Snackbar.make(v, "查询成功", Snackbar.LENGTH_SHORT).show();
            // 没必要，出题人是**吗
            selectData();
        });
    }

    private void aboutDatabase() {
        MyDatabaseHelper helper = new
                MyDatabaseHelper(requireContext(), "Store.db", 1);
        db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from car_balance", null);
        if (cursor.moveToFirst()) {
            Log.d("TAG", "aboutDatabase: 有数据");
            do {
                // 查询余额列表
                Integer money = cursor.getInt(cursor.getColumnIndex("balance"));
                balance.add(money);
            } while (cursor.moveToNext());
            binding.balance.setText(String.format("%s元", balance.get(index)));
            cursor.close();
        } else {
            Log.d("TAG", "aboutDatabase: 冇数据");
            for (String car : carList) {
                db.execSQL("insert into car_balance(carname,balance) values (\"" + car + "\",1000)");
            }
            // 添加数据后回调
            aboutDatabase();
        }
    }

    private void showDialog() {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.list_content, null, false);
        AlertDialog dialog = new AlertDialog
                .Builder(requireContext())
                .setView(view)
                .create();
        // 数据列表
        aboutRecycleView(view);

        view.findViewById(R.id.button_close).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void selectData() {
        dialogShowList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from recharge", null);
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Integer money = cursor.getInt(cursor.getColumnIndex("money"));
                dialogShowList.add(String.format("%s-%s-%s", date, name, money));
                binding.topShow.setText(String.format("%s%s小车充值%s元", date, name, money));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void aboutRecycleView(View view) {
        // 从数据库加载数据
        selectData();
        RecyclerView recyclerView = view.findViewById(R.id.list_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(new RecyclerView.Adapter<ItemViewHolder>() {
            @NotNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
                return new ItemViewHolder(LayoutInflater.from(requireContext()).inflate(R.layout.item_content, parent, false));
            }

            @Override
            public void onBindViewHolder(@NotNull ItemViewHolder holder, int position) {
                TextView textView = holder.root.findViewById(R.id.textView2);
                textView.setText(dialogShowList.get(position));
            }

            @Override
            public int getItemCount() {
                return dialogShowList.size();
            }
        });
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        View root;

        public ItemViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            root = itemView;
        }
    }
}
