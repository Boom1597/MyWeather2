package com.example.myweather;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.myweather.db.Settings;
import com.example.myweather.db.WeatherDetail;
import com.example.myweather.util.BaseActivity;
import com.example.myweather.util.Utility;

public class SettingsActivity extends BaseActivity
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener{
    private Toolbar toolbar;
    private LinearLayout locationLayout;
    private TextView locationText;
    private RadioGroup tempUnitsGroup;
    private RadioButton cButton;
    private RadioButton fButton;
    private RadioButton kButton;
    private CheckBox isNotifyBox;
    private TextView isNotifyText;
    private int fromActivity;//启动者的编号
    private String cityName;//临时城市名
    private Settings settingsTemp = new Settings();//临时设置信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
        initData();
        fromActivity = getIntent().getIntExtra("fromActivity",1);
        cityName = Utility.mCity.getCityName();
        settingsTemp.setTempUnits(Utility.mSettings.getTempUnits());
        settingsTemp.setNotify(Utility.mSettings.isNotify());

    }

    private void initView(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();//将系统自带的标题栏隐藏起来
        }
        toolbar = (Toolbar)findViewById(R.id.settings_toolbar);
        locationLayout = (LinearLayout)findViewById(R.id.location_layout);
        locationText = (TextView)findViewById(R.id.location_text);

        tempUnitsGroup = (RadioGroup)findViewById(R.id.tempUnits_group);
        cButton = (RadioButton)findViewById(R.id.c_bt);
        fButton = (RadioButton)findViewById(R.id.f_bt);
        kButton = (RadioButton)findViewById(R.id.k_bt);

        isNotifyBox = (CheckBox)findViewById(R.id.isNotify_box);
        isNotifyText = (TextView)findViewById(R.id.isNotify_text);

        locationLayout.setOnClickListener(this);
        tempUnitsGroup.setOnCheckedChangeListener(this);
        isNotifyBox.setOnCheckedChangeListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {//返回按钮监听
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData(){
        locationText.setText(Utility.mCity.getCityName());

        if (Utility.mSettings.getTempUnits().equals("Celsius")){
            cButton.setChecked(true);
            fButton.setChecked(false);
            kButton.setChecked(false);
        }else if (Utility.mSettings.getTempUnits().equals("Fahrenheit")){
            cButton.setChecked(false);
            fButton.setChecked(true);
            kButton.setChecked(false);
        }else {
            cButton.setChecked(false);
            fButton.setChecked(false);
            kButton.setChecked(true);
        }

        if (Utility.mSettings.isNotify()){
            isNotifyText.setText("Enable");
            isNotifyBox.setChecked(true);
        }
        else {
            isNotifyText.setText("Disable");
            isNotifyBox.setChecked(false);
        }

    }

    @Override//location栏布局点击事件
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.location_layout:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("请输入城市名");
                dialog.setIcon(R.drawable.d01);
                final EditText editText = new EditText(this);
                dialog.setView(editText);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cityName = editText.getText().toString();
                        locationText.setText(cityName);
                    }
                });
                dialog.setNegativeButton("Cancel", null);
                dialog.show();
                break;
            default:
        }
    }

    @Override//复选框的单击事件
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            //当选中要通知
            settingsTemp.setNotify(true);
            isNotifyText.setText("Enable");
        }else {
            //当没选中
            settingsTemp.setNotify(false);
            isNotifyText.setText("Disable");
        }
    }

    @Override//单选框的点击事件
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.c_bt:
                settingsTemp.setTempUnits("Celsius");
                break;
            case R.id.f_bt:
                settingsTemp.setTempUnits("Fahrenheit");
                break;
            case R.id.k_bt:
                settingsTemp.setTempUnits("Kelvin");
                break;
            default:
        }
    }

    //参数：fromActivity——启动者的编号
    public static void actionStart(Context context,int fromActivity){
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("fromActivity",fromActivity);
        context.startActivity(intent);
    }

    //处理设置信息，并作出反应,返回是否改变
    public boolean settingsOrCityChange(){
        if(Utility.mSettings.isNotify()!= settingsTemp.isNotify()){//当“是否要通知”发生改变：
            if(settingsTemp.isNotify()){
                Intent startIntent = new Intent(this,NotifyService.class);
                startService(startIntent);
            }else {
                Intent stoppIntent = new Intent(this,NotifyService.class);
                stopService(stoppIntent);//暂停服务
                NotificationManager manager = (NotificationManager)
                        getSystemService(NOTIFICATION_SERVICE);
                manager.cancelAll();//取消所有通知
            }
            Utility.mSettings.setNotify(settingsTemp.isNotify());
        }
        Utility.mSettings.setUpdateDate(settingsTemp.getUpdateDate());

        if (Utility.mCity.getCityName().equals(cityName) &&
                Utility.mSettings.getTempUnits().equals(settingsTemp.getTempUnits())){//当位置和单位设置没变
            Log.d("Settings","false啦，奇怪了");
            return false;
        }
        if (!Utility.mCity.getCityName().equals(cityName)){//当城市发生改变
            Utility.mCity.setCityName(cityName);
            //启动主活动，让主碎片重新联网更新天气信息，然后重新处理，重新加载
        }
        if (!Utility.mSettings.getTempUnits().equals(settingsTemp.getTempUnits())){//当气温单位发生改变：
            String unitsBef = Utility.mSettings.getTempUnits();
            String unitsAft = settingsTemp.getTempUnits();
            for (WeatherDetail detail : Utility.weatherDetailList){
                detail.getTempAfterUnits(unitsBef,unitsAft);
            }
            Utility.mSettings.setTempUnits(settingsTemp.getTempUnits());
            //启动主活动，重新加载主碎片
        }
        Log.d("Settings","咋是true了，我擦");
        return true;
    }

    @Override
    protected void onStop() {
        if (settingsOrCityChange())
            MainActivity.actionStart(this);
        finish();
        super.onStop();
    }


}
