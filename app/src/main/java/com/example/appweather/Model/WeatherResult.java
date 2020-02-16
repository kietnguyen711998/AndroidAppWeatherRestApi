package com.example.appweather.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class WeatherResult implements Parcelable {
    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private Wind wind;
    private Clouds clouds;
    private int dt;
    private Sys sys;
    private int id;
    private String name;
    private int cod;

    public WeatherResult() {
    }

    protected WeatherResult(Parcel in) {
        coord = in.readParcelable(Coord.class.getClassLoader());
        base = in.readString();
        dt = in.readInt();
        id = in.readInt();
        name = in.readString();
        cod = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(coord, flags);
        dest.writeString(base);
        dest.writeInt(dt);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(cod);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeatherResult> CREATOR = new Creator<WeatherResult>() {
        @Override
        public WeatherResult createFromParcel(Parcel in) {
            return new WeatherResult(in);
        }

        @Override
        public WeatherResult[] newArray(int size) {
            return new WeatherResult[size];
        }
    };

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Main getMain() {
        return main;
    }


    public void setMain(Main main) {
        this.main = main;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }
}
