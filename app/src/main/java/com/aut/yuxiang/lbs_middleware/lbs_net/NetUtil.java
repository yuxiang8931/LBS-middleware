package com.aut.yuxiang.lbs_middleware.lbs_net;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Created by yuxiang on 16/12/16.
 */

public class NetUtil {
    private static final String TAG = "NetUtil";
    private static NetUtil mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context context;

    private NetUtil(Context context) {
        this.context = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized NetUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetUtil(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        try {
            Iterator<Entry<String, String>> iterator = req.getHeaders().entrySet().iterator();
            LogHelper.showLog(TAG, ">>>>>>>>>>>>>>>>>>Headers Info below>>>>>>>>>>>>>>>>>>>>>");
            while(iterator.hasNext())
            {
                Entry<String, String> next =  iterator.next();
                LogHelper.showLog(TAG, "KEY: "+next.getKey()+"  VALUE:"+next.getValue());
            }
            LogHelper.showLog(TAG, "<<<<<<<<<<<<<<<<<<Headers Info above<<<<<<<<<<<<<<<<<<<<<");
            LogHelper.showLog(TAG, "URL:  "+req.getUrl());
            LogHelper.showLog(TAG, "Method:  "+req.getMethod());
            LogHelper.showLog(TAG, "BodyContentType:  "+req.getBodyContentType());
            LogHelper.showLog(TAG, "Body:  "+ new String(req.getBody()));

        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
