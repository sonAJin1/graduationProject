package com.example.sonaj.graduationproject;

public class ItemLikeContents {


    String imgURL;
    String title;
    String subTitle;
    int date;
    String actor;
    String director;
    String contents;
    String summary;
    float naverScore;
    float imdbScore;
    int rtScore;
    int emotion;
    int type;
    int likeCount;
    String lastSelectTime;
    int todayContents;
    boolean like;

    public ItemLikeContents (String title, String subTitle, String contents, String actor, String director, String summary, String imgURL,
                                  int type, int likeCount, String lastSelectTime, int todayContents, float naverScore, float imdbScore, int rtScore,
                                  int emotion, int date){
        this.title = title;
        this.subTitle = subTitle;
        this.contents = contents;
        this.actor = actor;
        this.director = director;
        this.summary = summary;
        this.imgURL = imgURL;
        this.type = type;
        this.likeCount = likeCount;
        this.lastSelectTime = lastSelectTime;
        this.todayContents = todayContents;
        this.naverScore = naverScore;
        this.imdbScore = imdbScore;
        this.rtScore = rtScore;
        this.emotion = emotion;
        this.date = date;
    }

    public void setimgURL(String movieImgUrl) {
        this.imgURL = movieImgUrl;
    }
    public String getimgURL() {
        return imgURL;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    public void setSubtitle(String subTitle) {
        this.subTitle = subTitle;
    }
    public String getSubtitle() {
        return subTitle;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
    public String getActor() {
        return actor;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDirector() {
        return director;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getLikeCount() {
        return likeCount+1;
    }

    public void setLastSelectTime(String lastSelectTime) {
        this.lastSelectTime = lastSelectTime;
    }

    public String getLastSelectTime() {
        return lastSelectTime;
    }

    public void setTodayContents(int todayContents) {
        this.todayContents = todayContents;
    }

    public int getTodayContents() {
        return todayContents;
    }

    public void setNaverScore(float naverScore) {
        this.naverScore = naverScore;
    }

    public float getNaverScore() {
        return naverScore;
    }

    public void setimdbScore(float IMDBScore) {
        this.imdbScore = IMDBScore;
    }

    public float getimdbScore() {
        return imdbScore;
    }

    public void setrtScore(int RTTomatoScore) {
        this.rtScore = RTTomatoScore;
    }

    public String getrtScore() {
        return rtScore+"%";
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public int getEmotion() {
        return emotion;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getDate() {
        return date;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean getisLike() {
        return like;
    }
}
