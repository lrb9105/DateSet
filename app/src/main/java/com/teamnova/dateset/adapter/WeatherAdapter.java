package com.teamnova.dateset.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.teamnova.dateset.R;
import com.teamnova.dateset.addedfunc.weather.WeatherActivity;
import com.teamnova.dateset.dto.WeatherDto;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyViewHolder> {
    private ArrayList<WeatherDto> weatherList;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView_time;
        ImageView imageView_weatherImg;
        TextView textView_temp;
        TextView textView_percentOfRain;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView_time = itemView.findViewById(R.id.textView_time);
            imageView_weatherImg = itemView.findViewById(R.id.imageView_weatherImg);
            textView_temp = itemView.findViewById(R.id.textView_temp);
            textView_percentOfRain = itemView.findViewById(R.id.textView_percentOfRain);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public WeatherAdapter(ArrayList<WeatherDto> weatherList, Context context) {
        this.weatherList = weatherList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WeatherAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_weather_per_time, parent, false);
        WeatherAdapter.MyViewHolder vh = new WeatherAdapter.MyViewHolder(itemView);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(WeatherAdapter.MyViewHolder holder, int position) {
        // 현재시간
        String time = WeatherActivity.getHour(weatherList.get(position).getDateTime());
        holder.textView_time.setText(time);
        String weather = weatherList.get(position).getWeatherMain();
        String weatherDescription = weatherList.get(position).getWeatherDescription();
        boolean b = time.equals("오전 07시") || time.equals("오전 08시") || time.equals("오전 09시") || time.equals("오전 10시")
                || time.equals("오전 11시") || time.equals("오후 12시") || time.equals("오후 01시") || time.equals("오후 02시")
                || time.equals("오후 03시") || time.equals("오후 04시") || time.equals("오후 05시") || time.equals("오후 06시") || time.equals("오후 07시");

        switch(weather){
            case "Thunderstorm":
                holder.imageView_weatherImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.thunderstorm));
                break;
            case "Drizzle":
            case "Rain":
                holder.imageView_weatherImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.rain));
                break;
            case "Snow":
                holder.imageView_weatherImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.snow));
                break;
            case "Mist":
            case "Smoke":
            case "Haze":
            case "Fog":
            case "Sand":
            case "Dust":
            case "Ash":
            case "Squall":
            case "Tornado":
                holder.imageView_weatherImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.mist));
                break;
            case "Clear":
                if(b){
                    holder.imageView_weatherImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.clear_sky_day));
                } else{
                    holder.imageView_weatherImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.clear_sky_night));
                }
                break;
            case "Clouds":
                if(weatherDescription.equals("few clouds")){
                    if(b){
                        holder.imageView_weatherImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.few_clouds_day));
                    } else{
                        holder.imageView_weatherImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.few_clouds_night));
                    }
                } else {
                    holder.imageView_weatherImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.broken_clouds));
                    break;
                }
                break;
        }
        holder.textView_temp.setText("" + Math.round(Double.parseDouble(weatherList.get(position).getTemp())) + "°C / "
                + Math.round(Double.parseDouble(weatherList.get(position).getFeelsLikeTemp())) + "°C");
        holder.textView_percentOfRain.setText("강수확률 " + Math.round(Double.parseDouble(weatherList.get(position).getPop()) * 100) + "%");
        Log.d("debug_datetime",""+weatherList.get(position).getDateTime());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return weatherList.size();
    }
}
