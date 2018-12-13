package com.example.myweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import com.example.myweather.util.BaseActivity;
import com.example.myweather.util.Utility;

public class DetailActivity extends BaseActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private ImageView shareView;
    private ImageView mailView;
    @Override//待检查
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();//将系统自带的标题栏隐藏起来
        }
        toolbar = (Toolbar)findViewById(R.id.detail_toolbar);
        shareView = (ImageView)findViewById(R.id.share_view);
        shareView.setOnClickListener(this);
        mailView = (ImageView)findViewById(R.id.mail_view);
        mailView.setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {//返回按钮监听
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.map_location_item:
                        Uri uri = Uri.parse("geo:"+ Utility.mCity.getLat()+","+Utility.mCity.getLon());//纬度，经度
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        startActivity(intent);
                        break;
                    case R.id.settings_item:
                        SettingsActivity.actionStart(DetailActivity.this,2);
                        break;
                    default:
                }
                return false;
            }
        });

        DetailFragment detailFragment = (DetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
        int position = getIntent().getIntExtra("position",0);//第二个参数为缺省值
        detailFragment.refresh(Utility.weatherDetailList.get(position));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_view:
                //分享功能
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, R.drawable.title_icon);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BaiWeather");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "this is BaiWeather");
                startActivity(shareIntent);
                break;
            case R.id.mail_view:
                //邮件功能
                Intent mailIntent=new Intent(Intent.ACTION_SENDTO);
                mailIntent.setData(Uri.parse("mailto:123456@163.com"));
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, "BaiWeather");
                mailIntent.putExtra(Intent.EXTRA_TEXT, "this is BaiWeather");
                startActivity(mailIntent);
                break;
            default:
        }
    }

    //待检查
    public static void actionStart(Context context, int position){//启动本活动的方法
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("position",position);//weatherDetailList中的位置
        context.startActivity(intent);
    }

}
