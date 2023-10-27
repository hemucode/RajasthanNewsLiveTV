package com.codehemu.rajasthannewslivetv.services;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class ChannelDataService {
    Context ctx;
    public static final String TAG = "TAG";

    public ChannelDataService(Context ctx) {
        this.ctx = ctx;
    }

    public interface OnDataResponse{
        Void onError(String error);
        void onResponse(JSONArray response);
    }

    public void getChannelData(String url, OnDataResponse onDataResponse){
        RequestQueue queue = Volley.newRequestQueue(ctx);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                onDataResponse.onResponse(response);
                //Log.d(TAG,"1onResponse: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onDataResponse.onError(error.getLocalizedMessage());
                Log.d(TAG, "onErrorResponses: " + error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);

    }

}