package com.example.myweather.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2018/12/13.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();
    //用于从List中添加活动
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    //用于从List中移除活动
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    //用于将List中存储的活动全部销毁
    public static void finishAll(){
        for (Activity activity : activities){
            if (!activity.isFinishing())
                activity.finish();
        }
    }
}
