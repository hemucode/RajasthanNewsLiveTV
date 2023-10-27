package com.codehemu.rajasthannewslivetv;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codehemu.rajasthannewslivetv.adopters.ChannelAdopters;
import com.codehemu.rajasthannewslivetv.models.Channel;
import com.codehemu.rajasthannewslivetv.models.Common;
import com.codehemu.rajasthannewslivetv.models.InAppUpdate;
import com.codehemu.rajasthannewslivetv.services.ChannelDataService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    AdView adView, adView1,adView2;
    public static final String TAG = "TAG";

    LinearLayout linearLayout;
    RecyclerView newsChannelList,newsChannelList2,newsChannelList3;
    ChannelAdopters newsChannelAdopters,newsChannelAdopters2,newsChannelAdopters3;
    List<Channel> newsChannels,newsChannels2,newsChannels3;
    ChannelDataService service;
    SwipeRefreshLayout mSwipeRefreshLayout;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    CardView cardView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    String appsName, packageName;
    ReviewManager manager;
    ReviewInfo reviewInfo;
    TextView more_bengali,more_hindi,email_click;
    Button ePaper,englishNews,topNews,RateBtn,aboutBtn,shareBtn;
    private InAppUpdate inAppUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        service = new ChannelDataService(this);

        inAppUpdate = new InAppUpdate(MainActivity.this);
        inAppUpdate.checkForAppUpdate();

        this.appsName = getApplication().getString(R.string.app_name);
        this.packageName = getApplication().getPackageName();
        cardView = findViewById(R.id.InternetAlert);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        getListActivity1("no",getString(R.string.Bengali_banner_json));
        getListActivity2("no",getString(R.string.Bengali_news_json));
        getListActivity3("no",getString(R.string.Hindi_news_json));

        adsDisplay();
        RefreshLayout();
        RequestReviewInfo();
        moreButton();

        new webScript().execute();


    }


    private void moreButton() {
        more_bengali = findViewById(R.id.more_bengali);
        more_hindi = findViewById( R.id.moreHindi);


        more_bengali.setOnClickListener(v -> openListingActivity("bengaliNews"));
        more_hindi.setOnClickListener(v -> openListingActivity("hindiNews"));

        ePaper = findViewById(R.id.ePaper);

        ePaper.setOnClickListener(v -> openListingActivity("bengaliPaper"));
        englishNews = findViewById(R.id.englishBtn);

        englishNews.setOnClickListener(v -> openListingActivity("EnglishNews"));
        topNews = findViewById(R.id.topNews);
        topNews.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ShortActivity.class)));

        RateBtn = findViewById(R.id.rateBtn);
        RateBtn.setOnClickListener(v -> LinkRateUs());
        aboutBtn = findViewById(R.id.aboutAppBtn);
        aboutBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AboutActivity.class)));
        shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(v -> LinkShareApp());
    }

    private void openListingActivity(String activity) {
        startActivity(new Intent(MainActivity.this, ListingActivity.class).
                putExtra("activity",activity));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.rateHeart) {
            RateMe();
        }
        if (item.getItemId() == R.id.shorts) {
            startActivity(new Intent(MainActivity.this, ShortActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void RateMe(){
        if (reviewInfo != null){
            Task<Void> flow = manager.launchReviewFlow(this,reviewInfo);

            flow.addOnCompleteListener(task -> {
            });
        }

    }

    private void RequestReviewInfo(){
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                reviewInfo = task.getResult();
            }else {
                Toast.makeText(this, "Not Review", Toast.LENGTH_SHORT).show();
            }

        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        if (item.getItemId() == R.id.contain) {
            openListingActivity("containing");
        }

        if (item.getItemId() == R.id.policy) {
            startActivity(new Intent(MainActivity.this, WebActivity.class).
                    putExtra("title","Privacy Policy")
                    .putExtra("url",getString(R.string.policy_url)));
        }
        if (item.getItemId() == R.id.disclaimer) {
            final Dialog dialog = new Dialog(MainActivity.this); // Context, this, etc.
            dialog.setContentView(R.layout.activity_disclaimer);
            linearLayout = dialog.findViewById(R.id.dismiss);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            email_click = dialog.findViewById(R.id.email_click);

            email_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String emailID = getString(R.string.my_email);
                    String AppNAME = getString(R.string.app_name);
                    Uri data = Uri.parse("mailto:"
                            + emailID
                            + "?subject=" +AppNAME+ " Feedback" + "&body=" + "");
                    intent.setData(data);
                    startActivity(intent);
                }
            });

            dialog.show();
        }


        if (item.getItemId() == R.id.share) {
            LinkShareApp();
        }

        if (item.getItemId() == R.id.more) {
            openLink("https://play.google.com/store/apps/dev?id=7464231534566513633");
        }

        if (item.getItemId() == R.id.rate) {
            LinkRateUs();
        }
        if (item.getItemId() == R.id.about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        }
        if (item.getItemId() == R.id.website) {
            openLink("https://www.codehemu.com/");
        }
        if (item.getItemId() == R.id.fb) {
            openLink("https://www.facebook.com/codehemu/");
        }
        if (item.getItemId() == R.id.yt) {
            openLink("https://www.youtube.com/c/HemantaGayen");
        }
        return false;
    }

    private void LinkShareApp() {
        Intent share = new Intent("android.intent.action.SEND");
        share.setType("text/plain");
        share.putExtra("android.intent.extra.SUBJECT", MainActivity.this.appsName);
        String APP_Download_URL = "https://play.google.com/store/apps/details?id=" + MainActivity.this.packageName;
        share.putExtra("android.intent.extra.TEXT", MainActivity.this.appsName + getString(R.string.download_it) + APP_Download_URL);
        MainActivity.this.startActivity(Intent.createChooser(share, getString(R.string.share_it)));
    }

    private void LinkRateUs() {
        try {
            Intent intent2 = new Intent("android.intent.action.VIEW");
            intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + MainActivity.this.packageName));
            MainActivity.this.startActivity(intent2);
        }
        catch (Exception e){
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("market://details?id=" + MainActivity.this.packageName));
            MainActivity.this.startActivity(intent);
        }
    }

    public void openLink(String url) {
        Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(linkOpen);
    }

    private void RefreshLayout() {
        mSwipeRefreshLayout = findViewById(R.id.refresh_app);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            adsDisplay();
            getListActivity1("yes",getString(R.string.Bengali_banner_json));
            getListActivity2("yes",getString(R.string.Bengali_news_json));
            getListActivity3("yes",getString(R.string.Hindi_news_json));
        });
    }

    public class NetworkChangeListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Common.isConnectToInternet(context)) {
                cardView.setVisibility(View.VISIBLE);
            } else {
                cardView.setVisibility(View.INVISIBLE);
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


    public void getListActivity1(String refresh,String url) {
        newsChannelList = findViewById(R.id.SliderList_1);
        newsChannelList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        newsChannels = new ArrayList<>();
        newsChannelAdopters = new ChannelAdopters(this, newsChannels, "big"){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
            }

        };
        newsChannelList.setAdapter(newsChannelAdopters);


        SharedPreferences getShared = getSharedPreferences("BigBengaliJson", MODE_PRIVATE);
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
                    Log.d(TAG, "onErrorResponse: " + response.toString());
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("BigBengaliJson",Context.MODE_PRIVATE);
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
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("BigBengaliJson",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("str",response.toString());
                    editor.apply();
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

    public void getListActivity2(String refresh,String url) {
        newsChannelList2 = findViewById(R.id.SliderList_2);
        newsChannelList2.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        newsChannels2 = new ArrayList<>();
        newsChannelAdopters2 = new ChannelAdopters(this, newsChannels2, "small"){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
            }
        };
        newsChannelList2.setAdapter(newsChannelAdopters2);

        SharedPreferences getShared = getSharedPreferences("BengaliJson", MODE_PRIVATE);
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
                //    Log.d(TAG, "onErrorResponse: " + response.toString());
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("BengaliJson",Context.MODE_PRIVATE);
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
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("BengaliJson",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("str",response.toString());
                    editor.apply();
                    for (int i = 0; i < 8; i++) {
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
                            newsChannels2.add(c);
                            newsChannelAdopters2.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        }else {
            try {
                JSONArray jsonArray = new JSONArray(JsonValue);
                for (int i = 0; i < 8; i++) {
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
                        newsChannels2.add(c);
                        newsChannelAdopters2.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void getListActivity3(String refresh,String url) {
        newsChannelList3 = findViewById(R.id.SliderList_3);
        newsChannelList3.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        newsChannels3 = new ArrayList<>();
        newsChannelAdopters3 = new ChannelAdopters(this, newsChannels3, "small"){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
            }
        };
        newsChannelList3.setAdapter(newsChannelAdopters3);

        SharedPreferences getShared = getSharedPreferences("HindiJson", MODE_PRIVATE);
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
                //    Log.d(TAG, "onErrorResponse: " + response.toString());
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("HindiJson",Context.MODE_PRIVATE);
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
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("HindiJson",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("str",response.toString());
                    editor.apply();
                    for (int i = 0; i < 8; i++) {
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
                            newsChannels3.add(c);
                            newsChannelAdopters3.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        }else {
            try {
                JSONArray jsonArray = new JSONArray(JsonValue);
                for (int i = 0; i < 8; i++) {
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
                        newsChannels3.add(c);
                        newsChannelAdopters3.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    public void adsDisplay(){
        MobileAds.initialize(this, initializationStatus -> {
        });

        adView = findViewById(R.id.adView);
        adView1 = findViewById(R.id.adView1);
        adView2 = findViewById(R.id.adView2);


        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView1.loadAd(adRequest);
        adView2.loadAd(adRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inAppUpdate.onActivityResult(requestCode, resultCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        inAppUpdate.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inAppUpdate.onDestroy();
    }

    private class webScript extends AsyncTask<Void , Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            SharedPreferences getShared = getSharedPreferences("shorts", MODE_PRIVATE);

            String JsonValue = getShared.getString("row","noValue");

            String JsonValueEdit = getShared.getString("edit","noValue");

            if (!JsonValue.equals("noValue") && !JsonValueEdit.equals("noValue") ){
                try {
                    JSONArray jsonArray = new JSONArray(JsonValue);
                    JSONArray jsonArrayEdit = new JSONArray(JsonValueEdit);

                    JSONObject row = jsonArray.getJSONObject(0);
                    JSONObject edit = jsonArrayEdit.getJSONObject(0);

                    if (!row.getString("title").equals(edit.getString("title"))||
                            edit.getString("desc").equals("")){
                        JSONArray arr = new JSONArray();
                        HashMap<String, JSONObject> map = new HashMap<>();
                        Log.d(TAG, "1onErrorResponse: " + "desc");
                        Document document;
                        String desc;
                        for (int i = 0; i < 20; i++){
                            JSONObject channelData = jsonArray.getJSONObject(i);
                            document = Jsoup.connect(channelData.getString("link")).get();
                            Elements rightSec = document.select(".khbr_rght_sec").select("p");
                            Elements container = document.select(".container").select("p");
                            Elements slider_con = document.select(".slider_con").select("p");
                            Elements all_p = document.select("p");
                            if (rightSec.first()!= null){
                                if (rightSec.text().length() > 400){
                                    desc = rightSec.text().substring(0,400);
                                }else {
                                    desc = rightSec.text();
                                }
                            }else if (container.first()!=null){
                                if (container.text().length() > 400){
                                    desc = container.text().substring(0,400);
                                }else {
                                    desc = container.text();
                                }
                            }else if (slider_con.first()!=null){
                                if (slider_con.text().length() > 401){
                                    desc = slider_con.text().substring(0,400);
                                }else {
                                    desc = slider_con.text();
                                }
                            }else if (all_p.first()!=null){
                                if (all_p.text().length() > 400){
                                    desc = all_p.text().substring(0,400);
                                }else {
                                    desc = all_p.text();
                                }
                            }else {
                                desc = getString(R.string.sorry_desc);
                            }
                            JSONObject json = new JSONObject();

                            json.put("id",i);
                            json.put("title",channelData.getString("title"));
                            json.put("desc",desc);
                            json.put("link",channelData.getString("link"));
                            json.put("thumbnail",channelData.getString("thumbnail"));
                            map.put("json" + i, json);
                            arr.put(map.get("json" + i));
                            Log.d(TAG, "1onErrorResponse: " + desc);
                        }
                        SharedPreferences sharedPreferences = getSharedPreferences("shorts", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("edit",arr.toString());
                        editor.apply();
                    }

                } catch (JSONException e) {
                    //throw new RuntimeException(e);
                } catch (IOException e) {
                    // throw new RuntimeException(e);
                }
            }

            if (!JsonValue.equals("noValue") && JsonValueEdit.equals("noValue")){
                try {
                    JSONArray arr = new JSONArray();
                    HashMap<String, JSONObject> map = new HashMap<>();

                    JSONArray jsonArray = new JSONArray(JsonValue);
                    Document document;
                    String desc;
                    for (int i = 0; i < 20; i++){
                        JSONObject channelData = jsonArray.getJSONObject(i);
                        document = Jsoup.connect(channelData.getString("link")).get();
                        Elements rightSec = document.select(".khbr_rght_sec").select("p");
                        Elements container = document.select(".container").select("p");
                        Elements slider_con = document.select(".slider_con").select("p");
                        Elements all_p = document.select("p");
                        if (rightSec.first()!= null){
                            if (rightSec.text().length() > 400){
                                desc = rightSec.text().substring(0,400);
                            }else {
                                desc = rightSec.text();
                            }
                        }else if (container.first()!=null){
                            if (container.text().length() > 400){
                                desc = container.text().substring(0,400);
                            }else {
                                desc = container.text();
                            }
                        }else if (slider_con.first()!=null){
                            if (slider_con.text().length() > 401){
                                desc = slider_con.text().substring(0,400);
                            }else {
                                desc = slider_con.text();
                            }
                        }else if (all_p.first()!=null){
                            if (all_p.text().length() > 400){
                                desc = all_p.text().substring(0,400);
                            }else {
                                desc = all_p.text();
                            }
                        }else {
                            desc = getString(R.string.sorry_desc);
                        }


                        JSONObject json = new JSONObject();

                        json.put("id",i);
                        json.put("title",channelData.getString("title"));
                        json.put("desc",desc);
                        json.put("link",channelData.getString("link"));
                        json.put("thumbnail",channelData.getString("thumbnail"));
                        map.put("json" + i, json);
                        arr.put(map.get("json" + i));
                        Log.d(TAG, "1onErrorResponse: " + desc);
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("shorts", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("edit",arr.toString());
                    Log.d(TAG, "1onsResponse: " + arr);
                    editor.apply();
                } catch (JSONException | IOException e) {
                    Log.d(TAG, "1onErrorResponse: " + e);
                }

            }

            return null;
        }


    }


}