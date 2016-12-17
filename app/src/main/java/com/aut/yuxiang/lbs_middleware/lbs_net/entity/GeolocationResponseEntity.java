package com.aut.yuxiang.lbs_middleware.lbs_net.entity;

/**
 * Created by yuxiang on 16/12/16.
 */

public class GeolocationResponseEntity {


    private float accuracy;
    private Location location;

    public GeolocationResponseEntity() {
    }

    public static class Location {
        public Location() {
        }

        float lat;
        float lng;

        public float getLat() {
            return lat;
        }

        public float getLng() {
            return lng;
        }
    }

    public float getAccuracy() {
        return accuracy;
    }

    public Location getLocation() {
        return location;
    }
}
