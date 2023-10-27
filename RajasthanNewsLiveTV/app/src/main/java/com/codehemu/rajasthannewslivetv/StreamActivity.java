package com.codehemu.rajasthannewslivetv;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.codehemu.rajasthannewslivetv.models.Channel;
import com.codehemu.rajasthannewslivetv.models.Common;
import com.codehemu.rajasthannewslivetv.services.YoutubeDataService;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class StreamActivity extends AppCompatActivity {
    PlayerView playerView;
    public static final String TAG = "TAG";
    ImageView fbLink, youtubeLink, webLink, fullScreen;
    TextView Description;
    boolean isFullScreen = false;
    ExoPlayer player;
    ProgressBar progressBar,progressBar2;
    private AdView adView1,adView2;
    LinearLayout linearLayout;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    WebView web;
    YoutubeDataService service;

    String title,youtubeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        Channel channel = (Channel) getIntent().getSerializableExtra("channel");
        getSupportActionBar().setTitle(channel.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = channel.getName();
        youtubeID = channel.getYoutube();

        loadAds();

        playerView = findViewById(R.id.playerView);
        fullScreen = playerView.findViewById(R.id.exo_fullscreen_icon);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        web =  findViewById(R.id.webView);

        String category = channel.getCategory();
        if (category.equals("m3u8")) {
            web.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            playChannel(channel.getLive_url());
            fullScreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFullScreen("exo");
                }
            });
        }else {

            playerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            playChannel("");

            if(category.equals("api")){
                service = new YoutubeDataService(this);


                String apiUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId="+channel.getYoutube()+"&eventType=live&maxResults=1&order=date&type=video&key="+channel.getLive_url();

                service.getYoutubeData(apiUrl, new YoutubeDataService.OnDataResponse() {
                    @Override
                    public Void onError(String error) {
                        startActivity(new Intent(StreamActivity.this, WebActivity.class).putExtra("title",channel.getName()).putExtra("url","https://www.youtube.com/channel/"+channel.getYoutube()+"/live"));
                        finish();
                        return null;
                    }
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("items");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (!jsonObject.getString("id").isEmpty()){
                                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("id"));
                                web.loadUrl("https://www.youtube.com/embed/"+jsonObject1.getString("videoId"));
                            }else {
                                startActivity(new Intent(StreamActivity.this, WebActivity.class).putExtra("title",channel.getName()).putExtra("url","https://www.youtube.com/channel/"+channel.getYoutube()+"/live"));
                                finish();
                            }

                        } catch (JSONException e) {
                            startActivity(new Intent(StreamActivity.this, WebActivity.class).putExtra("title",channel.getName()).putExtra("url","https://www.youtube.com/channel/"+channel.getYoutube()+"/live"));
                            finish();
                        }

                    }
                });
            }else {
                web.loadUrl(channel.getLive_url()+channel.getYoutube());
            }


            web.getSettings().setJavaScriptEnabled(true);
            web.getSettings().setLoadWithOverviewMode(true);
            web.getSettings().setUseWideViewPort(true);

            web.setWebViewClient(new WebViewClient(){
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    String Script = "var css = document.createElement('style');" +
                            "var head = document.head;" +
                            "css.innerText = `" +
                            ".ytp-show-cards-title," +
                            ".ytp-pause-overlay," +
                            ".branding-img," +
                            ".ytp-large-play-button," +
                            ".ytp-youtube-button," +
                            ".ytp-menuitem:nth-child(1)," +
                            ".ytp-small-redirect," +
                            ".ytp-menuitem:nth-child(4)" +
                            "{display:none !important;}`;" +
                            "head.appendChild(css);" +
                            "document.querySelector('.ytp-play-button').click();" +
                            "css.type = 'text/css';"+
                            "if(document.querySelector('.ytp-error-content-wrap-reason')){Android.showToast(`error`);}else{Android.showToast(`noError`);}"+
                            "let ytpFullscreenButton = document.querySelector('.ytp-fullscreen-button');" +
                            "ytpFullscreenButton.addEventListener('click', function() { Android.showToast(`toast`); });";

                    web.evaluateJavascript(Script,null);
                    try{
                        Thread.sleep(1000);
                        progressBar2.setVisibility(View.GONE);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            });

            web.addJavascriptInterface(new WebAppInterface(this), "Android");
        }


        fbLink = findViewById(R.id.fbLink);

        fbLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.facebook.com/"+channel.getFacebook());
            }
        });

        youtubeLink = findViewById(R.id.youtubeLink);
        youtubeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.youtube.com/channel/"+ channel.getYoutube());
            }
        });

        webLink = findViewById(R.id.webLink);
        webLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(channel.getWebsite());
            }
        });

        Description = findViewById(R.id.channelDes);
        Description.setText(channel.getDescription());
        Description.setSelected(true);

    }

    public void setFullScreen(String button){
        if (isFullScreen){
            if(adView1!=null){
                adView1.setVisibility(View.VISIBLE);
            }
            if(adView2!=null){
                adView2.setVisibility(View.VISIBLE);
            }
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            if (getSupportActionBar() != null){
                getSupportActionBar().show();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
               if(button.equals("yt")){
                   ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) web.getLayoutParams();
                   params.width = params.MATCH_PARENT;
                   params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                   web.setLayoutParams(params);
            }else {
                   ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                   params.width = params.MATCH_PARENT;
                   params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);

                   playerView.setLayoutParams(params);
            }
            isFullScreen = false;
        }else {
            if(adView1!=null){
                adView1.setVisibility(View.GONE);
            }
            if(adView2!=null){
                adView2.setVisibility(View.GONE);
            }
            if(button.equals("yt")){
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) web.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                web.setLayoutParams(params);
            }else {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                playerView.setLayoutParams(params);
            }

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            if (getSupportActionBar() != null){
                getSupportActionBar().hide();
            }
            isFullScreen = true;

        }
    }

    public class WebAppInterface {
        Context mContext;
        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {

            if (toast.equals("toast")){
                StreamActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setFullScreen("yt");
                    }
                });
            }

            if (toast.equals("noError")){
                StreamActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        web.setVisibility(View.VISIBLE);
                    }
                });
            }


            if (toast.equals("error")){
                StreamActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(StreamActivity.this, WebActivity.class).putExtra("title", title).putExtra("url","https://www.youtube.com/channel/"+youtubeID+"/live"));
                    }
                });
            }

        }
    }

    public void openLink(String url){
        Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(linkOpen);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public void playChannel(String LiveUrl){
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();

        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(LiveUrl));

        player.setMediaSource(mediaSource);

        player.prepare();
        player.setPlayWhenReady(true);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == player.STATE_READY){
                    progressBar.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);
                }else if (playbackState == player.STATE_BUFFERING){
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    progressBar.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.seekToDefaultPosition();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        player.setPlayWhenReady(false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }

    public void loadAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView1 = findViewById(R.id.adView1);
        adView2 = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();

        adView1.loadAd(adRequest);
        adView2.loadAd(adRequest);

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