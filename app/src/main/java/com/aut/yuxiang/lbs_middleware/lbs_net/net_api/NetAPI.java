package com.aut.yuxiang.lbs_middleware.lbs_net.net_api;

import android.content.Context;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.aut.yuxiang.lbs_middleware.lbs_net.NetRequestBuilder;
import com.aut.yuxiang.lbs_middleware.lbs_net.NetRequestInterface;
import com.aut.yuxiang.lbs_middleware.lbs_net.RequestSender;
import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuxiang on 16/12/16.
 */

public abstract class NetAPI {
    private static final String TAG = "NetAPI";
    private Context context;
    private NetRequestInterface netRequestInterface;

    public NetAPI(Context context, NetRequestInterface netRequestInterface) {
        this.context = context;
        this.netRequestInterface = netRequestInterface;
    }

    protected abstract Object generateRequestEntity(Object entity);

    protected abstract NetRequestBuilder configBuilder(NetRequestBuilder netRequestBuilder);

    public void sendAPI(Object entity) {
        NetRequestBuilder builder = new NetRequestBuilder(context);
        generateRequestEntity(entity);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
        String json = gson.toJson(entity);
        try {
            JSONObject jsonObject = new JSONObject(json);
            builder.setJsonObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setListener(new Listener() {
            @Override
            public void onResponse(Object response) {
                LogHelper.showLog(TAG, "Response:  "+response.toString());
                netRequestInterface.onResponse(response);
            }
        });

        builder.setErrorListener(new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.showLog(TAG, "Error Response:  " + new String(error.networkResponse.data));
                netRequestInterface.onErrorResponse(error);
            }
        });
        configBuilder(builder);
        RequestSender requestSender = builder.Build();
        requestSender.send();
    }

}
