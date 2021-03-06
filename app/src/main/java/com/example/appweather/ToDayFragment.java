package com.example.appweather;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appweather.Common.Common;
import com.example.appweather.Model.WeatherResult;
import com.example.appweather.Retrofit.IOpenWeatherMap;
import com.example.appweather.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ToDayFragment extends Fragment {
    private ImageView imageView;
    private TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_pressure, txt_temperature, txt_description, txt_date_time, txt_wind, txt_geo_coord;
    private LinearLayout weather_panel;
    private ProgressBar loading;

    private CompositeDisposable compositeDisposable;
    private IOpenWeatherMap mService;
    private static String WEATHER_RESULT_KEY = "weather_result_key";


    static ToDayFragment getInstance(WeatherResult weatherResult) {
        ToDayFragment toDayFragment = new ToDayFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(WEATHER_RESULT_KEY, weatherResult);
        toDayFragment.setArguments(bundle);
        return toDayFragment;
    }

    private ToDayFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_to_day, container, false);
        imageView = itemView.findViewById(R.id.img_weather);
        txt_city_name = itemView.findViewById(R.id.txt_city_name);
        txt_humidity = itemView.findViewById(R.id.txt_humidity);
        txt_sunrise = itemView.findViewById(R.id.txt_sunrise);
        txt_sunset = itemView.findViewById(R.id.txt_sunset);
        txt_pressure = itemView.findViewById(R.id.txt_pressure);
        txt_temperature = itemView.findViewById(R.id.txt_temperature);
        txt_description = itemView.findViewById(R.id.txt_description);
        txt_date_time = itemView.findViewById(R.id.txt_date_time);
        txt_wind = itemView.findViewById(R.id.txt_wind);
        txt_geo_coord = itemView.findViewById(R.id.txt_geo_coords);
        weather_panel = itemView.findViewById(R.id.weather_panel);
        loading = itemView.findViewById(R.id.loading);
        getWeatherInformationByLocation();
        Log.i("WeatherLog", "Call getWeatherInformationByLocation()");
        return itemView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    private void getWeatherInformationByLocation() {
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/wn/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(imageView);

                        txt_city_name.setText(weatherResult.getName());
                        txt_description.setText(new StringBuilder("Weather in ")
                                .append(weatherResult.getName()).toString());
                        txt_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp()))
                                .append("°C").toString());
                        txt_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        txt_wind.setText(new StringBuilder(("Sp:" + weatherResult.getWind().getSpeed() +
                                " ,Deg:" + weatherResult.getWind().getDeg())).toString());
                        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure()))
                                .append(" hpa").toString());
                        txt_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        txt_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                        txt_geo_coord.setText(new StringBuilder(weatherResult.getCoord().toString()));

                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    public void updateWeather(WeatherResult weatherResult) {
        Toast.makeText(this.getContext(), "Call update weather in today weather", Toast.LENGTH_SHORT).show();
        //TODO cho ni ta update chi ten city ma thoi, len mang search cach chuyen variable object by parcelable object
        //TODO m lam object weather result phuc tap qua nen t chi chuyen variable primary type thoi, con neu muon chuyen object variable thi moi object do phai implement Parcelable nua, nhieu qua nen t khong lam so m kho hieu
//        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/wn/")
//                .append(weatherResult.getWeather().get(0).getIcon())
//                .append(".png").toString()).into(imageView);

        txt_city_name.setText(weatherResult.getName());
//        txt_description.setText(new StringBuilder("Weather in ")
//                .append(weatherResult.getName()).toString());
//        txt_temperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp()))
//                .append("°C").toString());
//        txt_date_time.setText(Common.convertUnixToDate(weatherResult.getDt()));
//        txt_wind.setText(new StringBuilder(("Sp:" + weatherResult.getWind().getSpeed() +
//                " ,Deg:" + weatherResult.getWind().getDeg())).toString());
//        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure()))
//                .append(" hpa").toString());
//        txt_sunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
//        txt_sunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
//        txt_geo_coord.setText(new StringBuilder(weatherResult.getCoord().toString()));
//        weather_panel.setVisibility(View.VISIBLE);
    }
}

