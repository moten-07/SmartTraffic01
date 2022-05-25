package com.example.traffic01.tool;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public abstract class MyCallBack implements Callback {
    private Activity activity;
    private Class clz;

    public MyCallBack(Activity activity, Class clz) {
        this.activity = activity;
        this.clz = clz;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        // 失败回调
        activity.runOnUiThread(() -> {
            Log.e(activity.getLocalClassName(), "onFailure: "+e.getLocalizedMessage(),e);
            Toast.makeText(activity.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        // 成功回调
        String str =response.body().string();
        activity.runOnUiThread(()->{
            try {
                function(HttpUtil.GSON.fromJson(str,clz));
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     * 数据类型
     * @param object
     */
    public abstract void function(Object object);
}
