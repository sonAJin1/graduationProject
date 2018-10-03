package com.example.sonaj.graduationproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class CharactorMake {

//    int drink;
//    int emotion;
static Context context;

    public CharactorMake(int drink , int emotion){
//        this.drink = drink;
//        this.emotion = emotion;
    }

    public static void setDrinkBackgroundColor(int drink, RelativeLayout relativeLayout){

        relativeLayout.setBackgroundResource(R.drawable.emotion_select_background);
        GradientDrawable gd = (GradientDrawable) relativeLayout.getBackground().getCurrent();

        switch (drink){
            case 0: //맥주인 경우
                gd.setColor(Color.parseColor("#e0ad2d"));
                break;
            case 1: //소주인 경우
                gd.setColor(Color.parseColor("#3daf37"));
                break;
            case 2: //막걸리인 경우
                gd.setColor(Color.parseColor("#DCD9D6"));
                break;
            case 3: //와인인 경우
                gd.setColor(Color.parseColor("#ef2056"));
                break;
        }
    }
    public static void setDrinkBackgroundColor(int drink, RadioButton radioButton){

        radioButton.setBackgroundResource(R.drawable.emotion_select_background);
        GradientDrawable gd = (GradientDrawable) radioButton.getBackground().getCurrent();

        switch (drink){
            case 0: //맥주인 경우
                gd.setColor(Color.parseColor("#e0ad2d"));
                break;
            case 1: //소주인 경우
                gd.setColor(Color.parseColor("#3daf37"));
                break;
            case 2: //막걸리인 경우
                gd.setColor(Color.parseColor("#DCD9D6"));
                break;
            case 3: //와인인 경우
                gd.setColor(Color.parseColor("#ef2056"));
                break;
        }
    }

    public static void setDrinkGuage(int drinkKind, RelativeLayout relativeLayout){
        switch (drinkKind){
            case 0: // 무표정
                relativeLayout.setBackgroundResource(R.drawable.ic_emotion00);
                break;
            case 1: // 메롱
                relativeLayout.setBackgroundResource(R.drawable.ic_emotion01);
                break;
            case 2: // 눈감고 웃음
                relativeLayout.setBackgroundResource(R.drawable.ic_emotion02);
                break;
            case 3: // 시무룩
                relativeLayout.setBackgroundResource(R.drawable.ic_emotion03);
                break;
            case 4: // 화남
                relativeLayout.setBackgroundResource(R.drawable.ic_emotion04);
                break;
        }
    }

    public static void setEmotionFace(int emotion, ImageView imageView){
        switch (emotion){
            case 0: // 무표정
                imageView.setImageResource(R.drawable.ic_emotion00);
                break;
            case 1: // 메롱
                imageView.setImageResource(R.drawable.ic_emotion01);
                break;
            case 2: // 눈감고 웃음
                imageView.setImageResource(R.drawable.ic_emotion02);
                break;
            case 3: // 시무룩
                imageView.setImageResource(R.drawable.ic_emotion03);
                break;
            case 4: // 화남
                imageView.setImageResource(R.drawable.ic_emotion04);
                break;
        }
    }


}