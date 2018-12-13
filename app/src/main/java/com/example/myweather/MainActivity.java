package com.example.myweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myweather.db.City;
import com.example.myweather.db.Settings;
import com.example.myweather.db.WeatherDetail;
import com.example.myweather.gson.CityWeather;
import com.example.myweather.util.ActivityCollector;
import com.example.myweather.util.BaseActivity;
import com.example.myweather.util.HttpUtil;
import com.example.myweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();//将系统自带的标题栏隐藏起来
        }
    }

    @Override
    protected void onDestroy() {
        DataSupport.deleteAll(City.class);
        Utility.mCity.clearSavedState();//把对象的存储状态清理掉!!!!
        Utility.mCity.save();
        DataSupport.deleteAll(Settings.class);
        Utility.mSettings.clearSavedState();//把对象的存储状态清理掉!!!!
        Utility.mSettings.save();
        DataSupport.deleteAll(WeatherDetail.class);
        if (Utility.weatherDetailList.size() > 0){
            Log.d("onDestroy","存入天气信息啦");//test
            for(WeatherDetail detail : Utility.weatherDetailList)
                detail.clearSavedState();//把对象的存储状态清理掉!!!!
            DataSupport.saveAll(Utility.weatherDetailList);
        }
        super.onDestroy();
        ActivityCollector.finishAll();
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

}
