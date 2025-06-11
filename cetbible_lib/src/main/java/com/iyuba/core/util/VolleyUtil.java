package com.iyuba.core.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyUtil {


    private static com.android.volley.RequestQueue queue;


    public static RequestQueue getInstance(Context context) {

        if (queue == null) {

            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

}
