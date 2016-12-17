package com.aut.yuxiang.lbs_middleware.lbs_net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by yuxiang on 16/12/16.
 */

public class RequestSender {
    private Context context;
    private String requestType;
    private int method;
    private String url;
    private Listener listener;
    private ErrorListener errorListener;
    private JSONObject jsonObject;
    private Request request;
    private Class clazz;
    private Map<String, String> headers;
    private Gson gson;
    public static final int GET = Method.GET;
    public static final int POST = Method.POST;
    public static final String JSON = "json";
    public static final String STRING = "string";
    public static final String GSON = "gson";

    public RequestSender(Context context,
                         int method,
                         String url,
                         Listener listener,
                         ErrorListener errorListener,
                         String requestType,
                         JSONObject jsonObject,
                         Request request,
                         Class clazz,
                         Map<String, String> headers,
                         Gson gson) {
        this.context = context;
        this.url = url;
        this.listener = listener;
        this.method = method;
        this.errorListener = errorListener;
        this.requestType = requestType;
        this.jsonObject = jsonObject;
        this.request = request;
        this.clazz = clazz;
        this.headers = headers;
        this.gson = gson;
    }

    public void send() {
        Request request = null;
        switch (requestType) {
            case JSON:
                request = new StringRequest(method, url, listener, errorListener);
                break;
            case STRING:
                request = new JsonObjectRequest(method, url, jsonObject, listener, errorListener);
                break;
            case GSON:
                request = new GSONRequest(method, url, jsonObject, gson, clazz, headers, listener, errorListener);
                break;
            default:
                request = this.request;
                break;
        }
        NetUtil.getInstance(context).addToRequestQueue(request);
    }


}
