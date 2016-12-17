package com.aut.yuxiang.lbs_middleware.lbs_net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by yuxiang on 16/12/16.
 */

public class NetRequestBuilder {

    private Context context;
    private int method;
    private String url;
    private Listener listener;
    private ErrorListener errorListener;
    private Request request;

    private Class clazz;
    private Map<String, String> headers;
    private JSONObject jsonObject;
    private String requestType;
    private Gson gson;
    public NetRequestBuilder(Context context) {
        this.context = context;
    }

    public NetRequestBuilder setMethod(int method) {
        this.method = method;
        return this;
    }


    public NetRequestBuilder setUrl(String url) {
        this.url = url;
        return this;
    }


    public NetRequestBuilder setListener(Listener listener) {
        this.listener = listener;
        return this;
    }


    public NetRequestBuilder setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }

    public NetRequestBuilder setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

    public NetRequestBuilder setRequestType(String requestType) {
        this.requestType = requestType;
        return this;
    }

    public NetRequestBuilder setRequest(Request request) {
        this.request = request;
        return this;
    }

    public NetRequestBuilder setClazz(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    public NetRequestBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public NetRequestBuilder setGson(Gson gson) {
        this.gson = gson;
        return this;
    }

    public RequestSender Build() {
        return new RequestSender(context,
                method,
                url,
                listener,
                errorListener,
                requestType,
                jsonObject,
                request,
                clazz,
                headers,
                gson
        );
    }
}
