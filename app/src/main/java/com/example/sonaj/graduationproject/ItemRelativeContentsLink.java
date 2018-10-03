package com.example.sonaj.graduationproject;

public class ItemRelativeContentsLink {
    String title;
    String subTitle;
    String imgURL;
    String internetLink;

    public ItemRelativeContentsLink(String title, String subTitle, String imgURL, String internetLink) {
        this.title = title;
        this.subTitle = subTitle;
        this.imgURL = imgURL;
        this.internetLink = internetLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getInternetLink() {
        return internetLink;
    }

    public void setInternetLink(String internetLink) {
        this.internetLink = internetLink;
    }
}
