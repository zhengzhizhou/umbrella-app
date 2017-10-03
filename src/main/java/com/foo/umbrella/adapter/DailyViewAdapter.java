package com.foo.umbrella.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foo.umbrella.R;
import com.foo.umbrella.data.model.ForecastCondition;

import org.threeten.bp.LocalDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/10/2.
 */
public class DailyViewAdapter extends RecyclerView.Adapter<DailyViewAdapter.ViewHolder>{
    private List<ForecastCondition> forecastConditions;
    private Context context;
    private String units;
    private SharedPreferences sharedPreferences;
    private int highest, lowest;
    public DailyViewAdapter(Context context,List<ForecastCondition> forecastConditions, int highest, int lowest){
        this.forecastConditions = forecastConditions;
        this.context = context;
        this.highest = highest;
        this.lowest = lowest;
        sharedPreferences = context.getSharedPreferences("umbrella", Context.MODE_PRIVATE);
        units = sharedPreferences.getString("units", "abc");
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView time, temp;
        ImageView desc;
        public ViewHolder(final View v){
            super(v);
            desc = (ImageView) v.findViewById(R.id.desc_weather);
            time = (TextView) v.findViewById(R.id.hourlyTime);
            temp = (TextView) v.findViewById(R.id.temp);
        }
    }

    @Override
    public DailyViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(forecastConditions.get(position).getDisplayTime().toString());
            holder.time.setText(new SimpleDateFormat("K:mm a").format(dateObj));
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        String desc = forecastConditions.get(position).getCondition();
        if(desc.equals("Clear")){
            holder.desc.setImageResource(R.drawable.weather_sunny);
        }else if(desc.equals("Cloudy")){
            holder.desc.setImageResource(R.drawable.weather_cloudy);
        }else if(desc.equals("Foggy") ){
            holder.desc.setImageResource(R.drawable.weather_fog);
        }else if(desc.equals("Hail")){
            holder.desc.setImageResource(R.drawable.weather_hail);
        }else if(desc.equals("Sleet")){
            holder.desc.setImageResource(R.drawable.weather_snowy_rainy);
        }else if(desc.equals("Snow")){
            holder.desc.setImageResource(R.drawable.weather_snowy);
        }else if(desc.equals("Partly Cloudy")){
            holder.desc.setImageResource(R.drawable.weather_partlycloudy);
        }else if(desc.equals("Thunderstorms")){
            holder.desc.setImageResource(R.drawable.weather_lightning);
        }
        if(units == "Fahrenheit"){
            holder.temp.setText(forecastConditions.get(position).getTempFahrenheit() + " F");
        }else{
            holder.temp.setText(forecastConditions.get(position).getTempCelsius() + " C");
        }

        if(position == highest){
            holder.desc.setColorFilter(Color.parseColor("#FF9800"));
            holder.time.setTextColor(Color.parseColor("#FF9800"));
            holder.temp.setTextColor(Color.parseColor("#FF9800"));
        }
        if(position == lowest){
            holder.desc.setColorFilter(Color.parseColor("#03A9F4"));
            holder.time.setTextColor(Color.parseColor("#03A9F4"));
            holder.temp.setTextColor(Color.parseColor("#03A9F4"));
        }
    }

    @Override
    public int getItemCount(){
        return forecastConditions.size();
    }

}
