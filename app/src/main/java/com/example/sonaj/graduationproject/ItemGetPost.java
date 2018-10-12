package com.example.sonaj.graduationproject;

public class ItemGetPost {
    int unicGroup;
    int lvl;
    int postOrder;
    String nickname;
    int drinkKind;
    int emotion;
    String selectContent;
    int cocktailReceived;
    int cheeringCock;
    int laughCock;
    int comfortCock;
    int sadCock;
    int angerCock;
    int views;
    String text;
    String image;
    String uploadTime;

    public ItemGetPost(int unicGroup, int lvl, int postOrder, String nickname,int drinkKind,int emotion,String selectContent,int cocktailReceived,int cheeringCock,int laughCock,
                       int comfortCock,int sadCock,int angerCock,int views,String text,String image,String uploadTime) {
        this.unicGroup = unicGroup;
        this.lvl = lvl;
        this.postOrder = postOrder;
        this.nickname = nickname;
        this.drinkKind = drinkKind;
        this.emotion = emotion;
        this.selectContent = selectContent;
        this.cocktailReceived = cocktailReceived;
        this.cheeringCock = cheeringCock;
        this.laughCock = laughCock;
        this.comfortCock = comfortCock;
        this.sadCock = sadCock;
        this.angerCock = angerCock;
        this.views = views;
        this.text = text;
        this.image = image;
        this.uploadTime = uploadTime;
    }

    public void setGroup(int unicGroup) {
        this.unicGroup = unicGroup;
    }

    public int getGroup() {
        return unicGroup;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getLvl() {
        return lvl;
    }

    public void setOrder(int order) {
        this.postOrder = order;
    }

    public int getOrder() {
        return postOrder;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public int getDrinkKind() {
        return drinkKind;
    }

    public void setDrinkKind(int drinkKind) {
        this.drinkKind = drinkKind;
    }

    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public String getSelectContent() {
        return selectContent;
    }

    public void setSelectContent(String selectContent) {
        this.selectContent = selectContent;
    }

    public int getCocktailReceived() {
        return cocktailReceived;
    }

    public void setCocktailReceived(int cocktailReceived) {
        this.cocktailReceived = cocktailReceived;
    }

    public int getCheeringCock() {
        return cheeringCock;
    }

    public void setCheeringCock(int cheeringCock) {
        this.cheeringCock = cheeringCock;
    }

    public int getLaughCock() {
        return laughCock;
    }

    public void setLaughCock(int laughCock) {
        this.laughCock = laughCock;
    }

    public int getComfortCock() {
        return comfortCock;
    }

    public void setComfortCock(int comfortCock) {
        this.comfortCock = comfortCock;
    }

    public int getSadCock() {
        return sadCock;
    }

    public void setSadCock(int sadCock) {
        this.sadCock = sadCock;
    }

    public int getAngerCock() {
        return angerCock;
    }

    public void setAngerCock(int angerCock) {
        this.angerCock = angerCock;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUploadTime() {

        return uploadTime;
    }

    public String getTestTime(){
        return "20분 전";
    }


    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
}
