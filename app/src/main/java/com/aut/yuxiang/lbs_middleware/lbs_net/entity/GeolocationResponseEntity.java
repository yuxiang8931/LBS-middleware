package com.aut.yuxiang.lbs_middleware.lbs_net.entity;

/**
 * Created by yuxiang on 16/12/16.
 */

public class GeolocationResponseEntity {
    private String accuracy;
    private Location location;

    public GeolocationResponseEntity() {
    }

    static class Location {
        private String lat;
        private String lng;

        public Location() {
        }
    }
}
