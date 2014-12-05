package com.cac.camp.camp;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by nikolaiollegaard on 05/12/14.
 */
public abstract class APIConnector {
    protected String baseURL;

    protected RequestQueue queue;

    public  APIConnector(Context c) {

        queue = Volley.newRequestQueue(c);
    }
}
