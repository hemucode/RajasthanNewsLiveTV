package com.codehemu.rajasthannewslivetv;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codehemu.rajasthannewslivetv.adopters.ChannelAdopters;
import com.codehemu.rajasthannewslivetv.models.Channel;
import com.codehemu.rajasthannewslivetv.models.Common;
import com.codehemu.rajasthannewslivetv.services.ChannelDataService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListingActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    RecyclerView newsChannelList;
    ChannelAdopters newsChannelAdopters,newsChannelAdopters1;
    List<Channel> newsChannels,newsChannels1;
    ChannelDataService service;
    ProgressBar progressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout linearLayout;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        service = new ChannelDataService(this);

        mSwipeRefreshLayout = findViewById(R.id.swipe);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String activity = extras.getString("activity");

            if (activity.equals("containing")){
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
                getSupportActionBar().setTitle(R.string.containing_information);
                getListActivity("no","BengaliJson",getString(R.string.Bengali_news_json),"details",1);
                getListActivity1("HindiJson",getString(R.string.Hindi_news_json),"details",1);
            }
            if (activity.equals("bengaliNews")) {
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        getListActivity("yes","BengaliJson",getString(R.string.Bengali_news_json),"medium",2);

                    }
                });
                getSupportActionBar().setTitle(R.string.bengali_channel);
                getListActivity("no","BengaliJson",getString(R.string.Bengali_news_json),"medium",2);

            }
            if (activity.equals("hindiNews")) {
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        getListActivity("yes","HindiJson",getString(R.string.Hindi_news_json),"medium",2);

                    }
                });
                getSupportActionBar().setTitle(R.string.hindi_news_channel);
                getListActivity("no","HindiJson",getString(R.string.Hindi_news_json),"medium",2);

            }
            if (activity.equals("EnglishNews")) {
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        getListActivity("yes","EnglishJson",getString(R.string.English_news_json),"medium",2);

                    }
                });
                getSupportActionBar().setTitle(R.string.english_news_channel);
                getListActivity("no","EnglishJson",getString(R.string.English_news_json),"medium",2);

            }
            if (activity.equals("bengaliPaper")) {
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        getListActivity("no","PaperJson",getString(R.string.Bengali_paper_json),"medium",2);

                    }
                });
                getSupportActionBar().setTitle(R.string.e_paper);
                getListActivity("no","PaperJson",getString(R.string.Bengali_paper_json),"medium",2);

            }
        }
    }


    public void getListActivity(String refresh,String storeName,String url,String item, int spanCount) {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        newsChannelList = findViewById(R.id.recyclerView);
        newsChannels = new ArrayList<>();
        newsChannelList.setLayoutManager(new GridLayoutManager(this, spanCount, LinearLayoutManager.VERTICAL,false));
        newsChannelAdopters = new ChannelAdopters(this, newsChannels, item){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                progressBar.setVisibility(View.INVISIBLE);
            }
        };
        newsChannelList.setAdapter(newsChannelAdopters);

        SharedPreferences getShared = getSharedPreferences(storeName, MODE_PRIVATE);
        String JsonValue = getShared.getString("str","noValue");

        if (refresh.equals("yes")){
            service.getChannelData( url, new ChannelDataService.OnDataResponse() {
                @Override
                public Void onError(String error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    return null;
                }
                @Override
                public void onResponse(JSONArray response) {
                   // Log.d(TAG, "onErrorResponse: " + response.toString());
                    SharedPreferences sharedPreferences = ListingActivity.this.getSharedPreferences(storeName,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("str",response.toString());
                    editor.apply();
                }
            });
        }


        if (JsonValue.equals("noValue")) {
            service.getChannelData( url, new ChannelDataService.OnDataResponse() {
                @Override
                public Void onError(String error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    return null;
                }

                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject channelData = response.getJSONObject(i);
                            Channel c = new Channel();
                            c.setId(channelData.getInt("id"));
                            c.setName(channelData.getString("name"));
                            c.setDescription(channelData.getString("description"));
                            c.setLive_url(channelData.getString("live_url"));
                            c.setThumbnail(channelData.getString("thumbnail"));
                            c.setFacebook(channelData.getString("facebook"));
                            c.setYoutube(channelData.getString("youtube"));
                            c.setWebsite(channelData.getString("website"));
                            c.setCategory(channelData.getString("category"));
                            c.setLiveTvLink(channelData.getString("liveTvLink"));
                            c.setContact(channelData.getString("contact"));
                            newsChannels.add(c);
                            newsChannelAdopters.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        }else {
            try {
                JSONArray jsonArray = new JSONArray(JsonValue);
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject channelData = jsonArray.getJSONObject(i);
                        Channel c = new Channel();
                        c.setId(channelData.getInt("id"));
                        c.setName(channelData.getString("name"));
                        c.setDescription(channelData.getString("description"));
                        c.setLive_url(channelData.getString("live_url"));
                        c.setThumbnail(channelData.getString("thumbnail"));
                        c.setFacebook(channelData.getString("facebook"));
                        c.setYoutube(channelData.getString("youtube"));
                        c.setWebsite(channelData.getString("website"));
                        c.setCategory(channelData.getString("category"));
                        c.setLiveTvLink(channelData.getString("liveTvLink"));
                        c.setContact(channelData.getString("contact"));
                        newsChannels.add(c);
                        newsChannelAdopters.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    public void getListActivity1(String storeName,String url,String item, int spanCount) {
        newsChannelList = findViewById(R.id.recyclerView);
        newsChannels1 = new ArrayList<>();
        newsChannelList.setLayoutManager(new GridLayoutManager(this, spanCount, LinearLayoutManager.VERTICAL,false));
        newsChannelAdopters1 = new ChannelAdopters(this, newsChannels, item){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                progressBar.setVisibility(View.INVISIBLE);
            }
        };
        newsChannelList.setAdapter(newsChannelAdopters1);

        SharedPreferences getShared = getSharedPreferences(storeName, MODE_PRIVATE);
        String JsonValue = getShared.getString("str","noValue");

        if (JsonValue.equals("noValue")) {
            service.getChannelData( url, new ChannelDataService.OnDataResponse() {
                @Override
                public Void onError(String error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                    return null;
                }

                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject channelData = response.getJSONObject(i);
                            Channel c = new Channel();
                            c.setId(channelData.getInt("id"));
                            c.setName(channelData.getString("name"));
                            c.setDescription(channelData.getString("description"));
                            c.setLive_url(channelData.getString("live_url"));
                            c.setThumbnail(channelData.getString("thumbnail"));
                            c.setFacebook(channelData.getString("facebook"));
                            c.setYoutube(channelData.getString("youtube"));
                            c.setWebsite(channelData.getString("website"));
                            c.setCategory(channelData.getString("category"));
                            c.setLiveTvLink(channelData.getString("liveTvLink"));
                            c.setContact(channelData.getString("contact"));
                            newsChannels.add(c);
                            newsChannelAdopters1.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        }else {
            try {
                JSONArray jsonArray = new JSONArray(JsonValue);
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject channelData = jsonArray.getJSONObject(i);
                        Channel c = new Channel();
                        c.setId(channelData.getInt("id"));
                        c.setName(channelData.getString("name"));
                        c.setDescription(channelData.getString("description"));
                        c.setLive_url(channelData.getString("live_url"));
                        c.setThumbnail(channelData.getString("thumbnail"));
                        c.setFacebook(channelData.getString("facebook"));
                        c.setYoutube(channelData.getString("youtube"));
                        c.setWebsite(channelData.getString("website"));
                        c.setCategory(channelData.getString("category"));
                        c.setLiveTvLink(channelData.getString("liveTvLink"));
                        c.setContact(channelData.getString("contact"));
                        newsChannels.add(c);
                        newsChannelAdopters.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    public class NetworkChangeListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Common.isConnectToInternet(context)) {
                final Dialog dialog = new Dialog(context); // Context, this, etc.
                dialog.setContentView(R.layout.activity_network);
                linearLayout = dialog.findViewById(R.id.dismiss);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        }
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}