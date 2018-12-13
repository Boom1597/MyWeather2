package com.example.myweather.db;

import com.example.myweather.gson.CityWeather;

import org.litepal.crud.DataSupport;


/**
 * Created by hp on 2018/12/8.
 */

public class City extends DataSupport {
    private int id;//city在服务器上的id值
    private String cityName;//城市名
    private double lon;//城市经纬度
    private double lat;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

}
