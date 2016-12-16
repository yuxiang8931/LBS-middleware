package com.aut.yuxiang.lbs_middleware.lbs_net;

import com.android.volley.VolleyError;

/**
 * Created by yuxiang on 16/12/16.
 */

public interface NetRequestInterface {

    public void onResponse(Object response);

    public void onErrorResponse(VolleyError error);
}
