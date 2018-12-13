package com.example.myweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by hp on 2018/12/8.
 */

public class HttpUtil {//http工具类
    /**
     * 向服务器发送http请求
     * @param address——请求地址
     * @param callback——注册的回调器，来处理服务器相应
     */
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
