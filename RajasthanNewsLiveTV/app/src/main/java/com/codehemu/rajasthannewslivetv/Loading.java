package com.codehemu.rajasthannewslivetv;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codehemu.rajasthannewslivetv.models.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;


public class Loading extends AppCompatActivity {
    Handler handler;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String versionName = BuildConfig.VERSION_NAME;
        textView = findViewById(R.id.textView);
        textView.setText("Version "+versionName);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Loading.this.startActivity(new Intent(Loading.this, MainActivity.class));
                finish();
            }
        },1500);
        if (Common.isConnectToInternet(Loading.this)) {
            new webScript().execute();
        }
    }
    private class webScript extends AsyncTask<Void , Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Document document  = null;
            Element titleE,linksE,thumbnailE;
            String title,links,thumbnail;

            try {
                document = Jsoup.connect("https://hindi.news18.com/commonfeeds/v1/hin/rss/rajasthan/rajasthan.xml").get();

                JSONArray arr = new JSONArray();
                HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();

                for (int i =0; i< 20; i++){
                    titleE = document.select("item").select("title").get(i);
                    linksE = document.select("item").select("link").get(i);
                    thumbnailE = document.select("item").select("media|content").get(i);

                    if (titleE!=null && linksE!=null && thumbnailE!=null){
                        title = titleE.text();
                        links = linksE.text();
                        thumbnail = thumbnailE.attr("url");

                        JSONObject json = new JSONObject();
                        json.put("id",i);
                        json.put("title",title);
                        json.put("link",links);
                        json.put("thumbnail",thumbnail);
                        map.put("json" + i, json);
                        arr.put(map.get("json" + i));
                    }else {
                        Log.d(TAG, "1onError: RSS Feed Element not Found..");
                    }

                }

                SharedPreferences sharedPreferences = getSharedPreferences("shorts", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("row",arr.toString());
                Log.d(TAG, "1onResponse: " + arr.toString());
                editor.apply();

            } catch (IOException e) {
                Log.d(TAG, "1onError: RSS Feed Url Connect Error =" + e);
                Loading.this.startActivity(new Intent(Loading.this, MainActivity.class));
            } catch (JSONException e) {
                Log.d(TAG, "1onError: RSS Feed Json Load Error =" + e);
                Loading.this.startActivity(new Intent(Loading.this, MainActivity.class));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }

    }


}