package com.aut.yuxiang.lbs_middleware;

import android.databinding.BaseObservable;
import android.databinding.Bindable;


/**
 * Created by yuxiang on 2/02/17.
 */

public class LocationBindingData extends BaseObservable {
    private String longitude;
    private String latitude;
    private String accuracy;

    @Bindable
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
        notifyPropertyChanged(BR.latitude);
    }

    @Bindable
    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
        notifyPropertyChanged(BR.accuracy);
    }

    @Bindable
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
        notifyPropertyChanged(BR.longitude);
    }
}
