package com.example.myweather;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;

import com.example.myweather.db.Settings;
import com.example.myweather.util.Utility;

/**
 * Created by hp on 2018/12/12.
 */

public class MainTitleLayout extends LinearLayout {//自定义“主活动的标题”控件
    private Toolbar toolbar;
    public MainTitleLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.master_title, this);
        toolbar = (Toolbar)findViewById(R.id.master_toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.map_location_item:
                        Uri uri = Uri.parse("geo:"+ Utility.mCity.getLat()+","+Utility.mCity.getLon());//纬度，经度
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        getContext().startActivity(intent);
                        break;
                    case R.id.settings_item:
                        SettingsActivity.actionStart(getContext(),1);
                        break;
                    default:
                }
                return false;
            }
        });
    }
}
