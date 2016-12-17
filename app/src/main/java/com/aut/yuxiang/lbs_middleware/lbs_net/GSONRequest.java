package com.aut.yuxiang.lbs_middleware.lbs_net;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by yuxiang on 16/12/16.
 */

public class GSONRequest<T> extends Request<T> {
    private static final String TAG = "GSONRequest";
    private final Gson gson;
    private final Class clazz;
    private final Map<String, String> headers;
    private final Listener<T> listener;
    private JSONObject jsonObject;
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GSONRequest(int method, String url, JSONObject jsonObject, Gson gson, Class clazz, Map<String, String> headers,
                       Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.gson = gson;
        this.jsonObject = jsonObject;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
//        return new JsonObjectRequest(getMethod(), null, null, null, null).getHeaders();
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
//        super.deliverResponse(response);
        listener.onResponse(response);
    }

    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return new String(jsonObject.toString()).getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        LogHelper.showLog(TAG, new String(response.data));
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return (Response<T>) Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            LogHelper.showLog(TAG, e.getMessage());
            return Response.error(new ParseError(e));
        }
    }
}
