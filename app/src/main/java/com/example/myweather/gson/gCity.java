package com.example.myweather.gson;

/**
 * Created by hp on 2018/12/9.
 */

public class gCity {
    public int id;//城市id
    public String name;
    public String country;//国家代码（双字母）
    public Coord coord;//地理位置

    public class Coord{//地理位置
        public double lon;//经度
        public double lat;//纬度
    }
}
