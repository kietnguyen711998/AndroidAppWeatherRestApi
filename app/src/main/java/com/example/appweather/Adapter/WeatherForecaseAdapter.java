package com.example.appweather.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appweather.Common.Common;
import com.example.appweather.Model.WeatherForecastResult;
import com.example.appweather.R;
import com.squareup.picasso.Picasso;

public class WeatherForecaseAdapter extends RecyclerView.Adapter<WeatherForecaseAdapter.MyViewHolder> {
    Context context;
    WeatherForecastResult weatherForecastResult;

    public WeatherForecaseAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast,parent,false);
        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/wn/")
                .append(weatherForecastResult.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.img_weather);
        holder.txt_date.setText(new StringBuilder(Common.convertUnixToDate(weatherForecastResult
                .list.get(position).dt)));
        holder.txt_description.setText(new StringBuilder(weatherForecastResult.list.get(position)
                .weather.get(0).getDescription()));
        holder.txt_temperature.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position)
                .main.getTemp())).append("°C"));

    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txt_date,txt_temperature,txt_description;
        ImageView img_weather;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_weather = itemView.findViewById(R.id.img_weather);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_temperature = itemView.findViewById(R.id.txt_temperature);
            txt_description = itemView.findViewById(R.id.txt_description);
        }
    }
}
