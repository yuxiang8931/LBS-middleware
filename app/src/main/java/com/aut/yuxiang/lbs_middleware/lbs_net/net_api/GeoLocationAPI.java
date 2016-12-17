package com.aut.yuxiang.lbs_middleware.lbs_net.net_api;

import android.content.Context;

import com.aut.yuxiang.lbs_middleware.lbs_net.NetRequestBuilder;
import com.aut.yuxiang.lbs_middleware.lbs_net.NetRequestInterface;
import com.aut.yuxiang.lbs_middleware.lbs_net.RequestSender;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationRequestEntity;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationResponseEntity;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationResponseEntity.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by yuxiang on 16/12/16.
 */

public class GeoLocationAPI extends NetAPI {

    public GeoLocationAPI(Context context, NetRequestInterface netRequestInterface) {
        super(context, netRequestInterface);
    }

    protected Object generateRequestEntity(Object entity) {
//        GeolocationRequestEntity entity = (GeolocationRequestEntity)originalEntity;
//        entity.setHomeMobileCountryCode(310);
//        entity.setHomeMobileNetworkCode(410);
//        entity.setRadioType("gsm");
//        entity.setCarrier("Vodafone");
//        entity.setConsiderIp("true");
//
//        CellTower cellTower = new CellTower();
//        cellTower.setCellId(42);
//        cellTower.setLocationAreaCode(415);
//        cellTower.setMobileCountryCode(310);
//        cellTower.setMobileNetworkCode(410);
//        cellTower.setAge(0);
//        cellTower.setSignalStrength(-60);
//        cellTower.setTimingAdvance(15);
//
//        ArrayList<CellTower> cellTowers  = new ArrayList<>();
//
//        cellTowers.add(cellTower);
//        entity.setCellTowers(cellTowers);
        return entity;
    }

    @Override
    protected NetRequestBuilder configBuilder(NetRequestBuilder netRequestBuilder) {

        netRequestBuilder.setClazz(GeolocationResponseEntity.class);
        netRequestBuilder.setUrl(GeolocationRequestEntity.GEOLOCATION_URL);
        netRequestBuilder.setMethod(RequestSender.POST);
        netRequestBuilder.setRequestType(RequestSender.GSON);
        netRequestBuilder.setGson(getGson());
        return netRequestBuilder;
    }


    private Gson getGson()
    {
        return new GsonBuilder().registerTypeAdapter(Location.class, new LocationDeserializer()).create();
    }

    private class LocationDeserializer implements JsonDeserializer<GeolocationResponseEntity.Location>
    {

        @Override
        public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonElement location = json.getAsJsonObject();
            GeolocationResponseEntity.Location loc = new Gson().fromJson(location, GeolocationResponseEntity.Location.class);
            return loc;

        }
    }
}
