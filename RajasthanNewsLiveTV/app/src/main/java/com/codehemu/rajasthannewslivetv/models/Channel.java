package com.codehemu.rajasthannewslivetv.models;

import java.io.Serializable;

public class Channel implements Serializable {
    private int id;
    private String name;
    private String description;
    private String live_url;
    private String thumbnail;
    private String facebook;
    private String youtube;
    private String website;
    private String category;
    private String liveTvLink;
    private String contact;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLive_url() {
        return live_url;
    }

    public void setLive_url(String live_url) {
        this.live_url = live_url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLiveTvLink() {
        return liveTvLink;
    }

    public void setLiveTvLink(String liveTvLink) {
        this.liveTvLink = liveTvLink;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", live_url='" + live_url + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", facebook='" + facebook + '\'' +
                ", youtube='" + youtube + '\'' +
                ", website='" + website + '\'' +
                ", category='" + category + '\'' +
                ", liveTvLink='" + liveTvLink + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}