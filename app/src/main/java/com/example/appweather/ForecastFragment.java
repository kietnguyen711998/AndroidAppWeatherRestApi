package com.example.appweather;


import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.RangeColumn;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.example.appweather.Adapter.WeatherForecaseAdapter;
import com.example.appweather.Common.Common;
import com.example.appweather.Model.WeatherForecastResult;
import com.example.appweather.Retrofit.IOpenWeatherMap;
import com.example.appweather.Retrofit.RetrofitClient;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ForecastFragment extends Fragment {
    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;
    TextView txt_city_name, txt_geo_coord;
    RecyclerView recycler_view;
    Button btn_open;
//    private Toolbar toolbars;
    LinearLayout main_chart;

    //anychart
    private AnyChartView rangeChart;

    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    static ForecastFragment instance;

    public static ForecastFragment getInstance() {
        if (instance == null)
            instance = new ForecastFragment();
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_forecast, container, false);
        txt_city_name = rootview.findViewById(R.id.txt_city_name);
        txt_geo_coord = rootview.findViewById(R.id.txt_geo_coord);
        main_chart = rootview.findViewById(R.id.main_chart);

//        // toolbar
//        toolbars = rootview.findViewById(R.id.toolbar);
//
//        //set toolbar appearance
////        toolbar.setBackground(rootview.R.color.material_blue_grey_800);
////        toolbars.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
//
//
//        toolbars.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                return false;
//            }
//        });

//        //for crate home button
//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        activity.setSupportActionBar(toolbars);
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btn_open = rootview.findViewById(R.id.btn_open);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_chart.setVisibility(View.VISIBLE);
                btn_open.setVisibility(View.VISIBLE);
                btn_open.setText("HIDE");

            }
        });

        recycler_view = rootview.findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        getForecastWeatherInformation();

        //anychart
        rangeChart = rootview.findViewById(R.id.rangeChart);
        Cartesian cartesian = AnyChart.cartesian();
        cartesian.title("7 Days Temperature \\nin Turan°C");
        Set set = Set.instantiate();
        set.data(getData());
        Mapping turanData = set.mapAs("{ x: 'x', high: 'turanHigh', low: 'turanLow' }");
        RangeColumn columnTuran = cartesian.rangeColumn(turanData);
        columnTuran.name("Turan");
        cartesian.xAxis(true);
        cartesian.yAxis(true);
        cartesian.yScale()
                .minimum(15d)
                .maximum(35d);
        cartesian.legend(true);
        cartesian.yGrid(true)
                .yMinorGrid(true);
        cartesian.tooltip().titleFormat("{%SeriesName} ({%x})");
        rangeChart.setChart(cartesian);

        return rootview;


    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()){
//            case android.R.id.home:
//                getActivity().onBackPressed();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    //anychart
    private ArrayList getData() {
        ArrayList<DataEntry> entries = new ArrayList<>();
        entries.add(new CustomDataEntry("12/2", 29, 23));
        entries.add(new CustomDataEntry("13/2", 30, 22));
        entries.add(new CustomDataEntry("14/2", 31, 22));
        entries.add(new CustomDataEntry("15/2", 31, 22));
        entries.add(new CustomDataEntry("16/2", 27, 19));
        entries.add(new CustomDataEntry("17/2", 24, 20));
        entries.add(new CustomDataEntry("18/2", 25, 18));
        return entries;
    }

    private class CustomDataEntry extends DataEntry {
        public CustomDataEntry(String x, Number turanHigh, Number turanLow) {
            setValue("x", x);
            setValue("turanHigh", turanHigh);
            setValue("turanLow", turanLow);
        }
    }


    private void getForecastWeatherInformation() {
        compositeDisposable.add(mService.getForecastWeatherByLatLng(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        displayForecastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ERROR", "" + throwable.getMessage());
                    }
                })
        );
    }

    private void displayForecastWeather(WeatherForecastResult weatherForecastResult) {
        txt_city_name.setText(new StringBuilder(weatherForecastResult.city.name));
        txt_geo_coord.setText(new StringBuilder(weatherForecastResult.city.coord.toString()));
        WeatherForecaseAdapter weatherForecaseAdapter = new WeatherForecaseAdapter(getContext(), weatherForecastResult);
        recycler_view.setAdapter(weatherForecaseAdapter);
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
