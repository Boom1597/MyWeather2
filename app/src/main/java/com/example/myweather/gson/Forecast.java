package com.example.myweather.gson;

import java.util.List;

/**
 * Created by hp on 2018/12/9.
 */

public class Forecast {
    public String dt_txt;//预测数据计算时间，UTC
    public Main main;//天气的主要信息
    public List<Weather> weather;//天气(没办法，返回的居然是个只有一个元素的数组，神经啊)
    public Wind wind;//风

    public class Main{
        public double temp;//温度。单位默认值：开尔文，公制：摄氏度，英制：华氏度。
        public double pressure;//海平面上的大气压力默认为hPa
        public double humidity;//湿度，％
    }
    public class Weather{
        public int id;//天气id
        public String main;//天气文字描述
        public String icon;//天气图标id
    }
    public class Wind{
        public double speed;//风速。单位默认值：米/秒，公制：米/秒，英制：英里/小时。
        public double deg;// 风向，度（气象）
    }
}
