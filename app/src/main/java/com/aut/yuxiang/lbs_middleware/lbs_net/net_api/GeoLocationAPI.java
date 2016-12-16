package com.aut.yuxiang.lbs_middleware.lbs_net.net_api;

import android.content.Context;

import com.aut.yuxiang.lbs_middleware.lbs_net.NetRequestBuilder;
import com.aut.yuxiang.lbs_middleware.lbs_net.NetRequestInterface;
import com.aut.yuxiang.lbs_middleware.lbs_net.RequestSender;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationRequestEntity;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationRequestEntity.CellTower;
import com.aut.yuxiang.lbs_middleware.lbs_net.entity.GeolocationResponseEntity;

import java.util.ArrayList;

/**
 * Created by yuxiang on 16/12/16.
 */

public class GeoLocationAPI extends NetAPI {

    public GeoLocationAPI(Context context, NetRequestInterface netRequestInterface) {
        super(context, netRequestInterface);
    }

    protected Object generateRequestEntity() {
        GeolocationRequestEntity entity = new GeolocationRequestEntity();
        entity.setHomeMobileCountryCode(310);
        entity.setHomeMobileNetworkCode(410);
        entity.setRadioType("gsm");
        entity.setCarrier("Vodafone");
        entity.setConsiderIp("true");

        CellTower cellTower = new CellTower();
        cellTower.setCellId(42);
        cellTower.setLocationAreaCode(415);
        cellTower.setMobileCountryCode(310);
        cellTower.setMobileNetworkCode(410);
        cellTower.setAge(0);
        cellTower.setSignalStrength(-60);
        cellTower.setTimingAdvance(15);

        ArrayList<CellTower> cellTowers  = new ArrayList<>();

        cellTowers.add(cellTower);
        entity.setCellTowers(cellTowers);
        return entity;
    }

    @Override
    protected NetRequestBuilder configBuilder(NetRequestBuilder netRequestBuilder) {

        netRequestBuilder.setClazz(GeolocationResponseEntity.class);
        netRequestBuilder.setUrl(GeolocationRequestEntity.GEOLOCATION_URL);
        netRequestBuilder.setMethod(RequestSender.POST);
        netRequestBuilder.setRequestType(RequestSender.GSON);
        return netRequestBuilder;
    }
}
