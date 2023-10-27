package com.codehemu.rajasthannewslivetv.adopters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codehemu.rajasthannewslivetv.R;
import com.codehemu.rajasthannewslivetv.StreamActivity;
import com.codehemu.rajasthannewslivetv.WebActivity;
import com.codehemu.rajasthannewslivetv.models.Channel;
import com.codehemu.rajasthannewslivetv.services.CacheImageManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelAdopters extends RecyclerView.Adapter<ChannelAdopters.ViewHolder> {
    List<Channel> channels;
    private InterstitialAd mInterstitialAd;
    String type;
    private Context mContext;
    private Map<String, Bitmap> mBitmaps = new HashMap<>();

    public ChannelAdopters(Context mContext, List<Channel> channels, String type) {
        this.channels = channels;
        this.type = type;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ChannelAdopters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (type.equals("small")) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_small, parent, false);
        }else if (type.equals("medium")){
            v = LayoutInflater.from(mContext).inflate(R.layout.item_medium, parent, false);
        }else if (type.equals("big")){
            v = LayoutInflater.from(mContext).inflate(R.layout.item_big, parent, false);
        }else {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_details, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelAdopters.ViewHolder holder, int position) {
        int positions = position;
        String Category = channels.get(position).getCategory();
        Channel ChannelDataItem = channels.get(position);
        holder.textView.setText(channels.get(position).getName());



        if (holder.channelDes!= null) {
            holder.channelDes.setText(ChannelDataItem.getDescription());
        }

        if (holder.website!= null){
            holder.website.setText(ChannelDataItem.getWebsite());
            holder.website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(ChannelDataItem.getWebsite()));
                    v.getContext().startActivity(linkOpen);
                }
            });
        }
        if (holder.liveUrl != null){
            holder.liveUrl.setText(ChannelDataItem.getLiveTvLink());
            holder.liveUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(ChannelDataItem.getLiveTvLink()));
                    v.getContext().startActivity(linkOpen);
                }
            });
        }

        if (holder.yt!= null){
            String ytLink =  "https://www.youtube.com/channel/"+ ChannelDataItem.getYoutube();
            holder.yt.setText(ytLink);
            holder.yt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(ytLink));
                    v.getContext().startActivity(linkOpen);
                }
            });
        }

        if (holder.fb!= null){
            String fbLink  ="https://www.facebook.com/"+ChannelDataItem.getFacebook();
            holder.fb.setText(fbLink);
            holder.fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse(fbLink));
                    v.getContext().startActivity(linkOpen);
                }
            });
        }

        if (holder.email!= null){
            holder.email.setText(ChannelDataItem.getContact());
            holder.email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent linkOpen = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"+ChannelDataItem.getContact()));
                    v.getContext().startActivity(linkOpen);
                }
            });
        }

        if (holder.button!= null){
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show((Activity) v.getContext());
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();

                                v.getContext().startActivity(new Intent(v.getContext(), StreamActivity.class).putExtra("channel", channels.get(positions)));

                                mInterstitialAd = null;
                                setAds();
                            }
                        });

                    } else {
                        v.getContext().startActivity(new Intent(v.getContext(), StreamActivity.class).putExtra("channel", channels.get(positions)));

                    }
                }
            });
        }
        if (holder.cardView!= null){
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Category.equals("ePaper") || Category.equals("web")){
                        Intent t = new Intent(v.getContext(), WebActivity.class);
                        t.putExtra("title",channels.get(positions).getName());
                        t.putExtra("url",channels.get(positions).getLive_url());
                        v.getContext().startActivity(t);
                    }else {
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show((Activity) v.getContext());
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();

                                    v.getContext().startActivity(new Intent(v.getContext(), StreamActivity.class).putExtra("channel", channels.get(positions)));

                                    mInterstitialAd = null;
                                    setAds();
                                }
                            });

                        } else {
                            v.getContext().startActivity(new Intent(v.getContext(), StreamActivity.class).putExtra("channel", channels.get(positions)));
                        }
                    }
                }
            });
        }

        setAds();

        try {
            Bitmap bitmap = CacheImageManager.getImage(mContext, ChannelDataItem);
            if (bitmap == null) {
                MyImageTask task = new MyImageTask();
                task.setViewHolder(holder);
                task.execute(ChannelDataItem);
            } else {
                holder.imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView,channelDes,website,liveUrl,yt,fb,email;
        Button button;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.channelThumbnail);
            textView = itemView.findViewById(R.id.channelName);
            cardView = itemView.findViewById(R.id.CardView_item);
            channelDes = itemView.findViewById(R.id.item_channelDes);
            website = itemView.findViewById(R.id.item_website);
            liveUrl = itemView.findViewById(R.id.item_liveUrl);
            yt = itemView.findViewById(R.id.item_yt);
            fb = itemView.findViewById(R.id.item_fb);
            email = itemView.findViewById(R.id.item_email);
            button = itemView.findViewById(R.id.button2);
        }
    }

    class MyImageTask extends AsyncTask<Channel, Void, Bitmap> {

        private Channel mChannel;
        private ViewHolder mViewHolder;
        private static final String PHOTO_IMAGE_URL = "https://hemucode.github.io/LiveTV/thumbnail/";

        public void setViewHolder(ViewHolder myViewHolder) {
            this.mViewHolder = myViewHolder;
        }

        @Override
        protected Bitmap doInBackground(Channel... channels) {
            Bitmap bitmap = null;
            mChannel = channels[0];

            String url = PHOTO_IMAGE_URL + mChannel.getThumbnail();

            InputStream inputStream = null;

            try {
                URL imageUrl = new URL(url);
                inputStream = (InputStream) imageUrl.getContent();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mViewHolder.imageView.setImageBitmap(bitmap);
            CacheImageManager.putImage(mContext, mChannel, bitmap);
        }
    }

    public void setAds(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(mContext,mContext.getString(R.string.InterstitialAd), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });
    }
}