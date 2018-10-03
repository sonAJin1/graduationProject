package com.example.sonaj.graduationproject;

public class ItemJustSelected {
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
    int likeCount;
    int type;


    public ItemJustSelected(String title, String subtitle, int date, float naverScore, float IMDBScore, int RTTomatoScore, String director, String actor, String summary, String contents, String imageUrl, boolean like, int likeCount, int type) {
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
        this.likeCount = likeCount;
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

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}