package com.example.appweather;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CityFragment extends Fragment {
    private List<String> lstCity;
    private MaterialSearchBar searchBar;
    TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_pressure, txt_temperature, txt_description, txt_date_time, txt_wind, txt_geo_coord;
    LinearLayout weather_panel;
    ProgressBar loading;
    ImageView imageView;
    CompositeDisposable compositeDisposable;
    OnCallBackReceive onCallBackReceive;
    IOpenWeatherMap mService;
    FloatingActionButton floatingActionButton;
    WeatherResult currentWeatherResult;

    public void setOnCallBackReceiveListener(OnCallBackReceive onCallBackReceive) {
        this.onCallBackReceive = onCallBackReceive;
    }


    static CityFragment instance;

    public static CityFragment getInstance() {
        if (instance == null)
            instance = new CityFragment();
        return instance;
    }

    public CityFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    public static CityFragment newInstance(String name, int age) {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("age", age);

        CityFragment fragment = new CityFragment();
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_city, container, false);

        imageView = rootview.findViewById(R.id.img_weather);
        txt_city_name = rootview.findViewById(R.id.txt_city_name);
        txt_humidity = rootview.findViewById(R.id.txt_humidity);
        txt_sunrise = rootview.findViewById(R.id.txt_sunrise);
        txt_sunset = rootview.findViewById(R.id.txt_sunset);
        txt_pressure = rootview.findViewById(R.id.txt_pressure);
        txt_temperature = rootview.findViewById(R.id.txt_temperature);
        txt_description = rootview.findViewById(R.id.txt_description);
        txt_date_time = rootview.findViewById(R.id.txt_date_time);
        txt_wind = rootview.findViewById(R.id.txt_wind);
        txt_geo_coord = rootview.findViewById(R.id.txt_geo_coords);

        weather_panel = rootview.findViewById(R.id.weather_panel);
        loading = rootview.findViewById(R.id.loading);
        floatingActionButton = rootview.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallBackReceive.onCallBackReceive(currentWeatherResult);
            }
        });

        searchBar = rootview.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);
        new AsyncLoadCity().execute();
        SaveDataInObject();
        return rootview;
    }

    public boolean SaveDataInObject() {
        if (lstCity != null) {

        }
        return true;
    }

    private class AsyncLoadCity extends SimpleAsyncTask<List<String>> {

        @Override
        protected List<String> doInBackground() {
            lstCity = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream is = getResources().openRawResource(R.raw.city_list);
                BufferedInputStream gzipInputStream = new BufferedInputStream(is);

                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(reader);

                String readed;
                while ((readed = in.readLine()) != null)
                    builder.append(readed);
                lstCity = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>() {
                }.getType());
                Log.d("nnn", "doInBackground: " + lstCity.get(1));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return lstCity;
        }

        @Override
        protected void onSuccess(final List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<>();
                    for (String search : listCity) {
                        if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());
                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });
            searchBar.setLastSuggestions(listCity);

            loading.setVisibility(View.GONE);
        }
    }

    private void getWeatherInformation(String cityName) {
        compositeDisposable.add(mService.getWeatherByCityName(cityName,
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {

                        currentWeatherResult = weatherResult;

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
                        Log.d("xxx", "accept: " + new StringBuilder(("Sp:" + weatherResult.getWind().getSpeed() +
                                ",Deg:" + weatherResult.getWind().getDeg())).toString());
                        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure()))
                                .append(" hpa").toString());
                        txt_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity()))
                                .append(" % ").toString());
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
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }


}

interface OnCallBackReceive {
    void onCallBackReceive(WeatherResult weatherResult);
}
