package com.example.sonaj.graduationproject;

import android.databinding.BaseObservable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ItemTodayRecommendMovie extends BaseObservable{
    //private로 변경하고 xml
    //난독화 할때 변수명도 바뀌는데 난독화에서 제외하는데 파싱하는 애들은 private 로 난독화를 막아야함
    String imageUrl;
    String title;
    String subtitle;
    int date;
    String actor;
    String director;
    String contents;
    String summary;
    float naverScore;
    float IMDBScore;
    int RTTomatoScore;
    boolean like;
    int type;


    public ItemTodayRecommendMovie(String title, String subtitle, int date, float naverScore, float IMDBScore, int RTTomatoScore, String director, String actor, String summary, String contents, String imageUrl, boolean like,int type) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.subtitle = subtitle;
        this.date = date;
        this.naverScore = naverScore;
        this.IMDBScore = IMDBScore;
        this.RTTomatoScore = RTTomatoScore;
        this.actor = actor;
        this.director = director;
        this.contents = contents;
        this.summary = summary;
        this.like = like;
        this.type = type;
    }

    public  String getTitle() {
        return title;
    }

    public void setTitle(String title) {

        this.title = title;

    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getimageUrl() {
        return imageUrl;
    }

    public void setimageUrl(String imageUrl) {

        this.imageUrl = imageUrl;

    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public float getNaverScore() {
        return naverScore;
    }

    public void setNaverScore(float naverScore) {
        this.naverScore = naverScore;
    }

    public float getIMDBScore() {
        return IMDBScore;
    }

    public void setIMDBScore(float IMDBScore) {
        this.IMDBScore = IMDBScore;
    }

    public String getRTTomatoScore() {

        return RTTomatoScore+"%";
    }

    public void setRTTomatoScore(int RTTomatoScore) {
        this.RTTomatoScore = RTTomatoScore;
    }

    public String getActor() {
        return actor = actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getDirector() {
        return director = director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getSummary(){
        return summary = summary;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }

    public String getContents(){
        return contents = contents;
    }
    public void setContents(String contents){
        this.contents = contents;
    }

    public boolean getisLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}