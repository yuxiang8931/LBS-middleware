package com.aut.yuxiang.lbs_middleware.lbs_net.entity;

import java.util.ArrayList;

/**
 * Created by yuxiang on 16/12/16.
 */

public class GeolocationRequestEntity {
    public static final String API_KEY = "AIzaSyBpRDMmaOkiZoQduKGGCyhthwlofCDiFLg";
    public static final String GEOLOCATION_URL = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + API_KEY;
    private int homeMobileCountryCode;
    private int homeMobileNetworkCode;
    private String radioType;
    private String carrier;
    private String considerIp;
    private ArrayList<CellTower> cellTowers;

    public GeolocationRequestEntity() {
    }

    public static class CellTower {
        private int cellId;
        private int locationAreaCode;
        private int mobileCountryCode;
        private int mobileNetworkCode;
        private int age;
        private int signalStrength;
        private int timingAdvance;

        public CellTower() {
        }

        public void setCellId(int cellId) {
            this.cellId = cellId;
        }

        public void setLocationAreaCode(int locationAreaCode) {
            this.locationAreaCode = locationAreaCode;
        }

        public void setMobileCountryCode(int mobileCountryCode) {
            this.mobileCountryCode = mobileCountryCode;
        }

        public void setMobileNetworkCode(int mobileNetworkCode) {
            this.mobileNetworkCode = mobileNetworkCode;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void setSignalStrength(int signalStrength) {
            this.signalStrength = signalStrength;
        }

        public void setTimingAdvance(int timingAdvance) {
            this.timingAdvance = timingAdvance;
        }
    }

    public void setHomeMobileCountryCode(int homeMobileCountryCode) {
        this.homeMobileCountryCode = homeMobileCountryCode;
    }

    public void setHomeMobileNetworkCode(int homeMobileNetworkCode) {
        this.homeMobileNetworkCode = homeMobileNetworkCode;
    }

    public void setRadioType(String radioType) {
        this.radioType = radioType;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void setConsiderIp(String considerIp) {
        this.considerIp = considerIp;
    }

    public void setCellTowers(ArrayList<CellTower> cellTowers) {
        this.cellTowers = cellTowers;
    }
}
