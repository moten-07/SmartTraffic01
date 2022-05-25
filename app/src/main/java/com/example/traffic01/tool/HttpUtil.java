package com.example.traffic01.tool;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 500;
    public static final String DESC = "desc";
    public static final String ASC = "asc";
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder().build();
    private static final MediaType TYPE_JSON = MediaType.parse("application/json");
    /**
     * android 本地需要改为这个
     */
    public static String BASEURL = "http://10.0.2.2:8080";
    /**
     * 第三方模拟器需要用这个（ipconfig出的地址）
     */
//	public static String BASEURL = "http://192.168.87.14:8080";

    public static Gson GSON = new Gson();

    public static void get(String url, MyCallBack callBack) {
        Request request = new Request.Builder()
                .url(BASEURL + url)
                .get()
                .build();
        CLIENT.newCall(request).enqueue(callBack);
    }

    public static void post(String url, Object object, MyCallBack callBack) {
        RequestBody body = RequestBody.create(TYPE_JSON, GSON.toJson(object));
        Request request = new Request.Builder()
                .url(BASEURL + url)
                .post(body)
                .build();
        CLIENT.newCall(request).enqueue(callBack);
    }

}
