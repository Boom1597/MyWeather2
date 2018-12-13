package com.example.myweather;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myweather.db.City;
import com.example.myweather.db.LocalIcon;
import com.example.myweather.db.Settings;
import com.example.myweather.db.WeatherDetail;
import com.example.myweather.gson.CityWeather;
import com.example.myweather.util.HttpUtil;
import com.example.myweather.util.Utility;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hp on 2018/12/10.
 */

public class MasterFragment extends Fragment {

    private boolean isTwoPane;//是否为双页模式
    private View view;
    private TextView dateView;
    private TextView maxTempView;
    private TextView minTempView;
    private ImageView weatherIconView;
    private TextView weatherView;
    private RecyclerView masterRecyclerView;
    public SwipeRefreshLayout swipeRefresh;
    public final String KEY = "069fb6ecb359f71ad5a40fecec55ef20";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.master_frag, container, false);
        initView();
        return view;
    }

    @Override//碎片生存周期方法：当关联活动创建时
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity().findViewById(R.id.detail_layout) != null){
            isTwoPane = true;//可以找到detail_fragment布局时，为双页模式
        }else {
            isTwoPane = false;//找不到时，为单页模式
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    //初始化各控件
    private void initView(){
        dateView = (TextView)view.findViewById(R.id.date_view);
        maxTempView = (TextView)view.findViewById(R.id.maxTemp_view);
        minTempView = (TextView)view.findViewById(R.id.minTemp_view);
        weatherIconView = (ImageView)view.findViewById(R.id.weather_icon_view);
        weatherView = (TextView)view.findViewById(R.id.weather_text_view);
        masterRecyclerView = (RecyclerView)view.findViewById(R.id.master_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//new布局管理器
        masterRecyclerView.setLayoutManager(layoutManager);//为列表设置布局管理器
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));//设置下拉刷新进度条的颜色
    }

    //初始化Utility的静态数据
    private void initData(){
        Connector.getDatabase();//获取数据库
        if (Utility.mCity == null){//加载城市信息,优先级：本地—>数据库—>自定义
            List<City> cities = DataSupport.findAll(City.class);
            if (cities.size() <= 0){//当数据库中没有有所选城市的信息,自定义(本来应该是使用百度定位的)
                City city = new City();
                city.setCityName("Changsha");
                city.setLon(113.00000);
                city.setLat(28.21667);
                Utility.mCity = city;
            }
            else {//当数据库中有所选城市的信息
                Utility.mCity = cities.get(0);
            }
        }

        if (Utility.mSettings == null){//加载设置信息,优先级：本地—>数据库—>自定义
            List<Settings> settingsList = DataSupport.findAll(Settings.class);
            if (settingsList.size() <= 0){//当数据库中没有设置信息
                Settings set = new Settings();
                set.setNotify(true);
                set.setTempUnits("Celsius");
                Date curDate =  new Date(System.currentTimeMillis());//获取系统时间
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                set.setUpdateDate(formatter.format(curDate));
                Utility.mSettings = set;
            }else {//当数据库中有设置信息
                Utility.mSettings = settingsList.get(0);
            }
        }

        if (Utility.mLocalIconList == null || Utility.mLocalIconList.isEmpty()){//加载已下载天气图标,优先级：本地—>数据库
            List<LocalIcon> iconList = DataSupport.findAll(LocalIcon.class);
            Utility.mLocalIconList = new ArrayList<>();
            Utility.mLocalIconList = iconList;
        }

        //加载处理过的天气信息,优先级：联网下载—>本地—>数据库
        requestWeather(Utility.mCity.getCityName());
        if (Utility.weatherDetailList == null || Utility.weatherDetailList.isEmpty()){
            List<WeatherDetail> detailList = DataSupport.findAll(WeatherDetail.class);
            if (detailList.size() <= 0){//当数据库中没有天气信息
                Utility.weatherDetailList = new ArrayList<>();
                //requestWeather(Utility.mCity.getCityName());//test
            }else {//当数据库中有天气信息
                Utility.weatherDetailList = new ArrayList<>();
                Utility.weatherDetailList = detailList;
                for (WeatherDetail mWeather : Utility.weatherDetailList){//将本地天气图标存入静态数据Utility.weatherDetailList
                    mWeather.setIcon(Utility.findFromLocalIconList(Utility.mLocalIconList,mWeather.getIconTxt()));
                    Log.d("weatherDetailList",mWeather.getDate2());
                }
                showWeather();
            }
        }else {//当本地有天气信息
            showWeather();
        }

        if (Utility.mSettings.isNotify() &&
                Utility.weatherDetailList.size() > 0){//启用通知服务
            Intent startIntent = new Intent(getActivity(),NotifyService.class);
            getActivity().startService(startIntent);
        }else {//禁用通知服务
            Intent stoppIntent = new Intent(getActivity(),NotifyService.class);
            getActivity().stopService(stoppIntent);
        }
        //设置下拉刷新的监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(Utility.mCity.getCityName());
            }
        });
    }

    //从服务器中获取天气信息—>由此获取天气图标requestIcon—>显示出来
    public void requestWeather(final String cityName){
        final String weatherUrl = "http://api.openweathermap.org/data/2.5/forecast?q="
                +cityName+ ",cn&mode=json&APPID="+KEY;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final CityWeather mWeather = Utility.handleCurrentWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mWeather != null){
                            Utility.setWeatherDetailList(mWeather);//处理天气信息得到Utility.weatherDetailList
                            Utility.setMyCity(mWeather);//处理天气信息得到Utility.mCity
                            WeatherDetail weatherDetail = Utility.weatherDetailList.get(0);
                            requestIcon(weatherDetail.getIconTxt(),0);//递归请求图片
                        }else {
                            Toast.makeText(getActivity().getApplicationContext(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //从服务器中获取天气图标（byte[]）并转换成Bitmap，存储在 Utility.weatherDetailList
    public void requestIcon(final String iconTxt, final int index){
        Bitmap localBitmap = Utility.findFromLocalIconList(Utility.mLocalIconList,iconTxt);//从数据库中查找对应的天气图标
        if(localBitmap != null){//当数据库中有相应的图标
            Utility.weatherDetailList.get(index).setIcon(localBitmap);
            if(index+1 == Utility.weatherDetailList.size()){//当加载了最后一张图标
                showWeather();//展示天气信息
            }
            else {//加载下一张天气图标
                WeatherDetail weatherDetail = Utility.weatherDetailList.get(index + 1);
                requestIcon(weatherDetail.getIconTxt(), index + 1);
            }
            return;
        }
        //当数据库没有相应的图标，则从服务器中下载
        String iconUrl = "http://openweathermap.org/img/w/" + iconTxt+".png";
        HttpUtil.sendOkHttpRequest(iconUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),"获取天气图标失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final byte[] icon_bt = response.body().bytes();
                getActivity().runOnUiThread(new Runnable() {//在子线程中更新UI
                    @Override
                    public void run() {
                        if(icon_bt.length > 0){
                            Bitmap iconTemp = BitmapFactory.decodeByteArray(icon_bt, 0, icon_bt.length);
                            LocalIcon localIconTemp = new LocalIcon(iconTxt,icon_bt);
                            localIconTemp.clearSavedState();//把对象的存储状态清理掉!!!!
                            localIconTemp.save();//将下载的图片保存进数据库
                            Utility.mLocalIconList.add(localIconTemp);//将下载的图片链入静态数据Utility.mLocalIconList
                            Utility.weatherDetailList.get(index).setIcon(iconTemp);//将下载的图片存入静态数据Utility.weatherDetailList
                            if(index+1 == Utility.weatherDetailList.size()){//当加载了最后一张图标
                                showWeather();//展示天气信息
                            }
                            else {//加载下一张天气图标
                                WeatherDetail weatherDetail = Utility.weatherDetailList.get(index + 1);
                                requestIcon(weatherDetail.getIconTxt(), index + 1);
                            }
                        }else {
                            Toast.makeText(getActivity().getApplicationContext(),"获取天气图标失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //显示天气信息
    public void showWeather(){
        View todayView = view.findViewById(R.id.today_layout);
        if (isTwoPane){
            todayView.setVisibility(View.GONE);
        }
        else {
            todayView.setVisibility(View.VISIBLE);
            WeatherDetail todayWeahter = Utility.weatherDetailList.get(0);
            dateView.setText("Today"+","+todayWeahter.getDate2());
            maxTempView.setText(Integer.toString(todayWeahter.getMaxTemp())+"°");
            minTempView.setText(Integer.toString(todayWeahter.getMinTemp())+"°");
            weatherIconView.setImageBitmap(todayWeahter.getIcon());
            weatherView.setText(todayWeahter.getMain());
        }
        MasterAdapter masterAdapter = new MasterAdapter(Utility.weatherDetailList);
        masterRecyclerView.setAdapter(masterAdapter);
        swipeRefresh.setRefreshing(false);//表示刷新事件借宿，并隐藏刷新进度条
    }

    //新建一个内部类 来作RecyclerView的适配器
    class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.ViewHolder>{
        private List<WeatherDetail> mWeatherDetailList;

        class ViewHolder extends RecyclerView.ViewHolder{//视图缓存器
            ImageView iconView;
            TextView date1View;
            TextView mainView;
            TextView maxTempView;
            TextView minTempView;
            public ViewHolder(View view){
                super(view);
                iconView = (ImageView)view.findViewById(R.id.icon_item_view);
                date1View = (TextView)view.findViewById(R.id.date1_item_view);
                mainView = (TextView)view.findViewById(R.id.main_item_view);
                maxTempView = (TextView)view.findViewById(R.id.maxTemp_item_view);
                minTempView = (TextView)view.findViewById(R.id.minTemp_item_view);
            }
        }

        public MasterAdapter(List<WeatherDetail> weatherDetailList){
            mWeatherDetailList = weatherDetailList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.weather_item, parent ,false);
            final ViewHolder holder = new ViewHolder(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeatherDetail weatherDetail = mWeatherDetailList.get(holder.getAdapterPosition());
                    if(isTwoPane){//如果是双页模式，则刷新detail碎片中的内容
                        DetailFragment detailFragment = (DetailFragment)
                                getFragmentManager().findFragmentById(R.id.detail_fragment);
                        detailFragment.refresh(weatherDetail);
                    }else {//如果是单页模式,则直接启动detail活动
                        DetailActivity.actionStart(getActivity(),holder.getAdapterPosition());
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            WeatherDetail weatherDetail = mWeatherDetailList.get(position);
            holder.iconView.setImageBitmap(weatherDetail.getIcon());
            holder.date1View.setText(weatherDetail.getDate1());
            holder.mainView.setText(weatherDetail.getMain());
            holder.maxTempView.setText(Integer.toString(weatherDetail.getMaxTemp())+"°");
            holder.minTempView.setText(Integer.toString(weatherDetail.getMinTemp())+"°");
        }

        @Override
        public int getItemCount() {//返回列表子项数目
            return mWeatherDetailList.size();//因为列表不包括今天嘛
        }
    }

}
