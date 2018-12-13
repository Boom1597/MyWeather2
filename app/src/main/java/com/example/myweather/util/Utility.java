package com.example.myweather.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.example.myweather.db.LocalIcon;
import com.example.myweather.db.Settings;
import com.example.myweather.db.WeatherDetail;
import com.example.myweather.db.City;
import com.example.myweather.gson.CityWeather;
import com.example.myweather.gson.Forecast;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2018/12/8.
 */

public class Utility {//处理服务器的City数据 的工具类

    public static List<WeatherDetail> weatherDetailList;//整理后的天气信息列表
    public static City mCity;
    public static Settings mSettings;
    public static List<LocalIcon> mLocalIconList;

    //解析和处理服务器返回的City数据
    public static boolean handleCityResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities = new JSONArray(response);
                int citySum = allCities.length();
                for(int i = 0; i < citySum; i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setId(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    JSONObject coordObject = cityObject.getJSONObject("Coord");
                    city.setLat(coordObject.getDouble("lat"));
                    city.setLon(coordObject.getDouble("lon"));
                    city.save();
                }
                return true;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    //用于读取assets文件夹中的城市Id数据，然后建造城市名和城市id的数据库
    public static String readFile(String fileName, Context context){
        StringBuilder data = new StringBuilder();
        try{
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(
                    new InputStreamReader(assetManager.open(fileName)));
            String line;
            while((line = bf.readLine()) != null)
                data.append(line);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return data.toString();
    }

     //将返回的json数据解析成CurrentWeather实体类
    public static CityWeather handleCurrentWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            String weatherContent = jsonObject.toString();
            return new Gson().fromJson(weatherContent,CityWeather.class);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    //处理从服务器下载的天气信息到weatherDetailList
    public static void setWeatherDetailList(CityWeather cityWeather){
        weatherDetailList = new ArrayList<>();
        String[] tmp = cityWeather.list.get(0).dt_txt.split(" ");
        String nowDate2 = tmp[0];
        weatherDetailList.add(new WeatherDetail(cityWeather, nowDate2 ,0));
        for(int i = 0; i < cityWeather.list.size(); i++){
            Forecast forecast = cityWeather.list.get(i);
            String[] date = forecast.dt_txt.split(" ");
            if(date[0].equals(nowDate2)){//同一天
                continue;
            }else {//下一天
                nowDate2 = date[0];
                weatherDetailList.add(new WeatherDetail(cityWeather, nowDate2 ,i));
            }
        }
    }

    //处理从服务器下载的天气信息到mCity
    public static void setMyCity(CityWeather mWeather){
        mCity.setId(mWeather.city.id);
        mCity.setCityName(mWeather.city.name);
        mCity.setLat(mWeather.city.coord.lat);
        mCity.setLon(mWeather.city.coord.lon);
    }

    //从本地数据库中查找图片
    public static Bitmap findFromLocalIconList(List<LocalIcon> iconList, String iconTxt){
        for (LocalIcon icon : iconList){
            if(iconTxt.equals(icon.getIconTxt())){
                return BitmapFactory.decodeByteArray(icon.getIconByte(), 0, icon.getIconByte().length);
            }
        }
        return null;
    }

}
