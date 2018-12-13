package com.example.myweather.db;

import android.graphics.Bitmap;

import com.example.myweather.gson.CityWeather;
import com.example.myweather.gson.Forecast;
import com.example.myweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hp on 2018/12/10.
 */

public class WeatherDetail extends DataSupport {//处理后的天气信息

    private String date1;//星期 eg：Tomorrow
    private String date2;//日期 eg：May 18
    private int maxTemp;//最高气温
    private int minTemp;//最低温度
    private String iconTxt;//天气图标
    private Bitmap icon;//天气图标
    private String main;//天气文字描述
    private int humidity;//湿度
    private int pressure;//气压
    private int speed;//风速
    private String deg;//风向

    public WeatherDetail(){}

    public WeatherDetail(CityWeather cityWeather,String nowDate2,int index){
        Forecast fc = cityWeather.list.get(index);//index为nowDate2的在cityWeather.list的第一条位置
        iconTxt = fc.weather.get(0).icon;
        icon = null;
        main = fc.weather.get(0).main;
        humidity = (int)fc.main.humidity;
        pressure = (int)fc.main.pressure;
        speed = (int)(fc.wind.speed*1000);
        deg = getDegOfString(fc.wind.deg);//获取风向的英文
        //遍历得出最高气温和最低气温
        double maxt = 0,mint = 99999;
        for(Forecast forecast : cityWeather.list){
            String[] date = forecast.dt_txt.split(" ");
            if(date[0].equals(nowDate2)){
                if(maxt < forecast.main.temp)
                    maxt = forecast.main.temp;
                if(mint > forecast.main.temp)
                    mint = forecast.main.temp;
            }else {
                continue;
            }
        }
        String tempUnits = Utility.mSettings.getTempUnits();
        if (tempUnits.equals("Kelvin")) {//开尔文开氏度(摄氏度+273.16)
            maxTemp = (int)maxt;
            minTemp = (int)mint;
        }else if (tempUnits.equals("Fahrenheit")){//华氏度
            maxTemp = (int)((maxt-273.16)*9/5+32);
            minTemp = (int)((mint-273.16)*9/5+32);
        }else {//默认摄氏度Celsius
            maxTemp = (int)(maxt-273.16);
            minTemp = (int)(mint-273.16);
        }
        date1 = getDate1ofDate(fc.dt_txt);
        date2 = getDate2ofDate(fc.dt_txt);
    }

    public void getTempAfterUnits(String unitsBef, String unitsAft){
        if (unitsBef.equals(unitsAft))
            return;
        else if (unitsBef.equals("Celsius") && unitsAft.equals("Fahrenheit")){
            maxTemp = maxTemp*9/5+32;
            minTemp = minTemp*9/5+32;
        }else if (unitsBef.equals("Fahrenheit") && unitsAft.equals("Celsius")){
            maxTemp = (maxTemp-32)/9*5;
            minTemp = (maxTemp-32)/9*5;
        }else if (unitsBef.equals("Celsius") && unitsAft.equals("Kelvin")){
            maxTemp = (int)(maxTemp+273.16);
            minTemp = (int)(minTemp+273.16);
        }else if (unitsBef.equals("Fahrenheit") && unitsAft.equals("Kelvin")){
            maxTemp = (int)((maxTemp-32)/9*5+273.16);
            minTemp = (int)((minTemp-32)/9*5+273.16);
        }else if (unitsBef.equals("Kelvin") && unitsAft.equals("Celsius")) {
            maxTemp = (int) (maxTemp - 273.16);
            minTemp = (int) (minTemp - 273.16);
        }else if (unitsBef.equals("Kelvin") && unitsAft.equals("Fahrenheit")) {
            maxTemp = (int)((maxTemp-273.16)*9/5+32);
            minTemp = (int)((minTemp-273.16)*9/5+32);
        }
    }

    public String getDate1ofDate(String dateTime){//处理dt_txt得星期
        String[] temp = dateTime.split(" ");
        dateTime = temp[0];
        Calendar cal = Calendar.getInstance();
        if (dateTime.equals("")) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date;
            try {
                date = sdf.parse(dateTime);
            } catch (ParseException e) {
                date = null;
                e.printStackTrace();
            }
            if (date != null) {
                cal.setTime(new Date(date.getTime()));
            }
        }
        switch (cal.get(Calendar.DAY_OF_WEEK)){
            case 1: return "星期日";
            case 2: return "星期一";
            case 3: return "星期二";
            case 4: return "星期三";
            case 5: return "星期四";
            case 6: return "星期五";
            case 7: return "星期六";
            default: return "";
        }
    }

    public String getDate2ofDate(String date){
        String d2 = null;
        String[] dateArrary = date.split(" ");
        String[] d2Arrary = dateArrary[0].split("-");
        if(d2Arrary[1].equals("01")) d2 = "Jan. "+d2Arrary[2];
        else if(d2Arrary[1].equals("02")) d2 = "Feb. "+d2Arrary[2];
        else if(d2Arrary[1].equals("03")) d2 = "Mar. "+d2Arrary[2];
        else if(d2Arrary[1].equals("04")) d2 = "Apr. "+d2Arrary[2];
        else if(d2Arrary[1].equals("05")) d2 = "May. "+d2Arrary[2];
        else if(d2Arrary[1].equals("06")) d2 = "Jun. "+d2Arrary[2];
        else if(d2Arrary[1].equals("07")) d2 = "Jul. "+d2Arrary[2];
        else if(d2Arrary[1].equals("08")) d2 = "Aug. "+d2Arrary[2];
        else if(d2Arrary[1].equals("09")) d2 = "Sep. "+d2Arrary[2];
        else if(d2Arrary[1].equals("10")) d2 = "Oct. "+d2Arrary[2];
        else if(d2Arrary[1].equals("11")) d2 = "Nov. "+d2Arrary[2];
        else if(d2Arrary[1].equals("12")) d2 = "Dec. "+d2Arrary[2];
        return d2;
    }

    public String getDegOfString(double deg){//根据风向值获取风向英文
        if(deg < 22.5) return "NNE";
        else if (deg < 45) return "NE";
        else if(deg < 67.5) return "ENE";
        else if(deg < 90) return "E";
        else if(deg < 112.5) return "ESE";
        else if(deg < 135) return "SE";
        else if(deg < 157.5) return "SSE";
        else if(deg < 180) return "S";
        else if(deg < 202.5) return "SSW";
        else if(deg < 225) return "SW";
        else if(deg < 247.5) return "WSW";
        else if(deg < 270) return "W";
        else if(deg < 292.5) return "WNW";
        else if(deg < 315) return "NW";
        else if(deg < 337.5) return "NNW";
        else if(deg <= 360) return "N";
        return null;
    }

    public String getIconTxt() {
        return iconTxt;
    }

    public void setIconTxt(String iconTxt) {
        this.iconTxt = iconTxt;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getDeg() {
        return deg;
    }

    public void setDeg(String deg) {
        this.deg = deg;
    }

}
