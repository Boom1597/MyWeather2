package com.example.myweather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myweather.db.WeatherDetail;

/**
 * Created by hp on 2018/12/10.
 */

public class DetailFragment extends Fragment {
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.detail_frag, container, false);
        return view;
    }

    //待检查
    public void refresh(WeatherDetail detail){//（用于双页模式）更新（显示）细节视图的信息
        View visibilityLayout = view.findViewById(R.id.visibility_layout);
        visibilityLayout.setVisibility(View.VISIBLE);
        TextView date1View = (TextView)view.findViewById(R.id.date1_view);
        TextView date2View = (TextView)view.findViewById(R.id.date2_view);
        TextView minTempView = (TextView)view.findViewById(R.id.minTemp_view);
        TextView maxTempView = (TextView)view.findViewById(R.id.maxTemp_view);
        ImageView weatherIconView = (ImageView)view.findViewById(R.id.weather_icon_view);
        TextView weatherTextView = (TextView)view.findViewById(R.id.weather_text_view);
        TextView humidityView = (TextView)view.findViewById(R.id.humidity_view);
        TextView pressureView = (TextView)view.findViewById(R.id.pressure_view);
        TextView windView = (TextView)view.findViewById(R.id.wind_view);

        date1View.setText(detail.getDate1());
        date2View.setText(detail.getDate2());
        minTempView.setText(Integer.toString(detail.getMinTemp())+"°");
        maxTempView.setText(Integer.toString(detail.getMaxTemp())+"°");
        weatherIconView.setImageBitmap(detail.getIcon());
        weatherTextView.setText(detail.getMain());
        humidityView.setText("Humidity："+detail.getHumidity()+"%");
        pressureView.setText("Pressure："+detail.getPressure()+"hPa");
        windView.setText("Wind："+detail.getSpeed()+"km/h "+ detail.getDeg());
    }
}
