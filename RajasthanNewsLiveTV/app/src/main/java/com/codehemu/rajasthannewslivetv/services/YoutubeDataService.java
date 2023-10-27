package com.codehemu.rajasthannewslivetv.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class YoutubeDataService {
    Context ctx;
    public static final String TAG = "TAG";

    public YoutubeDataService(Context ctx) {
        this.ctx = ctx;
    }

    public interface OnDataResponse{
        Void onError(String error);
        void onResponse(JSONObject response);
    }

    public void getYoutubeData(String url, OnDataResponse onDataResponse){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        onDataResponse.onResponse(response);
                        Log.d(TAG,"1onResponse: " + response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onDataResponse.onError(error.getLocalizedMessage());
                Log.d(TAG, "onErrorResponses: " + error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);

    }
}
