package com.example.myweather;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;

import com.example.myweather.db.WeatherDetail;
import com.example.myweather.util.Utility;

public class NotifyService extends Service {
    public NotifyService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //创建一个定时任务，定时启动NotifyService
        NotificationManager manager = (NotificationManager)getSystemService
                (NOTIFICATION_SERVICE);
        manager.cancelAll();//取消所有通知
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int updateTime = 10*1000;//10秒更新一次test
        long triggerAtTime = SystemClock.elapsedRealtime() + updateTime;
        Intent intentNotification = new Intent(this,NotifyService.class);
        PendingIntent pi = PendingIntent.getService(this,0,intentNotification,0);
        alarmManager.cancel(pi);//取消旧的定时器

        if (!Utility.mSettings.isNotify()){//当设置为不启用通知时
            stopSelf();//停止服务
        }else {//当设置为“启用”时
            WeatherDetail today = Utility.weatherDetailList.get(0);
            String forecastInfo = "Forecast:"+today.getMain()+" "
                    +"High:"+today.getMaxTemp()+"° "+today.getMinTemp()+"° ";
            Intent mainIntent = new Intent(this,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,mainIntent,0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("WeatherForecast")
                    .setContentText(forecastInfo)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.d01)
                    .setLargeIcon(BitmapFactory.decodeResource(
                            getResources(),R.mipmap.notify_icon))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            manager.notify(1,notification);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);//设定新的定时器
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
