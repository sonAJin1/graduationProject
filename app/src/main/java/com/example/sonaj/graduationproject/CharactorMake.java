package com.example.sonaj.graduationproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    public static void setPostTitleImage(int drinkKind, int emotion, ImageView imageView){
        switch (drinkKind){
            case 0: // 맥주
                if(emotion==0){imageView.setImageResource(R.drawable.ic_beer_00_post_title);}
                if(emotion==1){imageView.setImageResource(R.drawable.ic_beer01_post_title);}
                if(emotion==2){imageView.setImageResource(R.drawable.ic_beer02_post_title);}
                if(emotion==3){imageView.setImageResource(R.drawable.ic_beer03_post_title);}
                if(emotion==4){imageView.setImageResource(R.drawable.ic_beer04_post_title);}
                break;
            case 1: // 소주
                if(emotion==0){imageView.setImageResource(R.drawable.ic_soju00_post_title);}
                if(emotion==1){imageView.setImageResource(R.drawable.ic_soju01_post_title);}
                if(emotion==2){imageView.setImageResource(R.drawable.ic_soju02_post_title);}
                if(emotion==3){imageView.setImageResource(R.drawable.ic_soju03_post_title);}
                if(emotion==4){imageView.setImageResource(R.drawable.ic_soju04_post_title);}
                break;
            case 2: // 막걸리
                if(emotion==0){imageView.setImageResource(R.drawable.ic_traditional00_post_title);}
                if(emotion==1){imageView.setImageResource(R.drawable.ic_traditional01_post_title);}
                if(emotion==2){imageView.setImageResource(R.drawable.ic_traditional02_post_title);}
                if(emotion==3){imageView.setImageResource(R.drawable.ic_traditional03_post_title);}
                if(emotion==4){imageView.setImageResource(R.drawable.ic_traditional04_post_title);}
                break;
            case 3: // 와인
                if(emotion==0){imageView.setImageResource(R.drawable.ic_wine00_post_title);}
                if(emotion==1){imageView.setImageResource(R.drawable.ic_wine01_post_title);}
                if(emotion==2){imageView.setImageResource(R.drawable.ic_wine02_post_title);}
                if(emotion==3){imageView.setImageResource(R.drawable.ic_wine03_post_title);}
                if(emotion==4){imageView.setImageResource(R.drawable.ic_wine04_post_title);}
                break;
        }
    }

    public static void setGuageImage(int drinkKind, int emotion, ImageView imageView){
        switch (drinkKind){
            case 0: // 맥주
                if(emotion==0){imageView.setImageResource(R.drawable.beer_gauge_00);}
                if(emotion==1){imageView.setImageResource(R.drawable.beer_gauge_01);}
                if(emotion==2){imageView.setImageResource(R.drawable.beer_gauge_02);}
                if(emotion==3){imageView.setImageResource(R.drawable.beer_gauge_03);}
                if(emotion==4){imageView.setImageResource(R.drawable.beer_gauge_04);}
                break;
            case 1: // 소주
                if(emotion==0){imageView.setImageResource(R.drawable.soju_gauge_00);}
                if(emotion==1){imageView.setImageResource(R.drawable.soju_gauge_01);}
                if(emotion==2){imageView.setImageResource(R.drawable.soju_gauge_02);}
                if(emotion==3){imageView.setImageResource(R.drawable.soju_gauge_03);}
                if(emotion==4){imageView.setImageResource(R.drawable.soju_gauge_04);}
                break;
            case 2: // 막걸리
                if(emotion==0){imageView.setImageResource(R.drawable.traditional_gauge_00);}
                if(emotion==1){imageView.setImageResource(R.drawable.traditional_gauge_01);}
                if(emotion==2){imageView.setImageResource(R.drawable.traditional_gauge_02);}
                if(emotion==3){imageView.setImageResource(R.drawable.traditional_gauge_03);}
                if(emotion==4){imageView.setImageResource(R.drawable.traditional_gauge_04);}
                break;
            case 3: // 와인
                if(emotion==0){imageView.setImageResource(R.drawable.wine_gauge_00);}
                if(emotion==1){imageView.setImageResource(R.drawable.wine_gauge_01);}
                if(emotion==2){imageView.setImageResource(R.drawable.wine_gauge_02);}
                if(emotion==3){imageView.setImageResource(R.drawable.wine_gauge_03);}
                if(emotion==4){imageView.setImageResource(R.drawable.wine_gauge_04);}
                break;
        }
    }

    public static void setEmptyDrinkShape(int drinkKind, ImageView imDrunk01, ImageView imDrunk02, ImageView imDrunk03){
        switch (drinkKind){
            case 0: // 맥주일 때
                imDrunk01.setImageResource(R.drawable.ic_beer_completion);
                imDrunk02.setImageResource(R.drawable.ic_beer_completion);
                imDrunk03.setImageResource(R.drawable.ic_beer_completion);
                break;
            case 1: // 소주일 때
                imDrunk01.setImageResource(R.drawable.ic_soju_completion);
                imDrunk02.setImageResource(R.drawable.ic_soju_completion);
                imDrunk03.setImageResource(R.drawable.ic_soju_completion);
                break;
            case 2: // 막걸리일 때
                imDrunk01.setImageResource(R.drawable.ic_traditional_completion_0);
                imDrunk02.setImageResource(R.drawable.ic_traditional_completion_0);
                imDrunk03.setImageResource(R.drawable.ic_traditional_completion_0);
                break;
            case 3: // 와인일 때
                imDrunk01.setImageResource(R.drawable.ic_wine_completion);
                imDrunk02.setImageResource(R.drawable.ic_wine_completion);
                imDrunk03.setImageResource(R.drawable.ic_wine_completion);
                break;
        }
    }

    public static void setCheekMargin(int drinkKind, ImageView imDrinkCheek){

        /**맥주인 경우 marginBottom 18dp
         * 소주, 막걸리인 경우 marginBottom 5dp*/

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)imDrinkCheek.getLayoutParams();
        layoutParams.bottomMargin = 0;
        imDrinkCheek.setLayoutParams(layoutParams);

        switch (drinkKind){
            case 0: // 맥주
                layoutParams.bottomMargin = 55;
                break;
            case 1: // 소주
                layoutParams.bottomMargin = 12;
                break;
            case 2: // 막걸리
                layoutParams.bottomMargin = 12;
                break;
            case 3: // 와인
                layoutParams.bottomMargin = 100;
                break;
        }
        imDrinkCheek.setLayoutParams(layoutParams);
    }

    public static void setCheekMarginPost(int drinkKind, ImageView imDrinkCheek){

        /**맥주인 경우 marginBottom 18dp
         * 소주, 막걸리인 경우 marginBottom 5dp*/

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)imDrinkCheek.getLayoutParams();
        layoutParams.bottomMargin = 0;
        imDrinkCheek.setLayoutParams(layoutParams);

        switch (drinkKind){
            case 0: // 맥주
                layoutParams.bottomMargin = 110;
                break;
            case 1: // 소주
                layoutParams.bottomMargin = 35;
                break;
            case 2: // 막걸리
                layoutParams.bottomMargin = 35;
                break;
            case 3: // 와인
                layoutParams.bottomMargin = 190;
                break;
        }
        imDrinkCheek.setLayoutParams(layoutParams);
    }

    public static void setCheekDegree(int drinkDegree, int drinkKind, ImageView imDrinkCheek){
        if(drinkKind==3){ // 와인인 경우
            switch (drinkDegree){
                case 0: // 맥주
                    imDrinkCheek.setVisibility(View.GONE);
                    break;
                case 1: // 소주
                    imDrinkCheek.setVisibility(View.VISIBLE);
                    imDrinkCheek.setBackgroundResource(R.drawable.wine_drunken01);
                    break;
                case 2: // 막걸리
                    imDrinkCheek.setVisibility(View.VISIBLE);
                    imDrinkCheek.setBackgroundResource(R.drawable.wine_drunken02);
                    break;
                case 3: // 와인
                    imDrinkCheek.setVisibility(View.VISIBLE);
                    imDrinkCheek.setBackgroundResource(R.drawable.wine_drunken03);
                    break;
            }
        }else{ // 다른 주류인 경우
            switch (drinkDegree){
                case 0: // 맥주
                    imDrinkCheek.setVisibility(View.GONE);
                    break;
                case 1: // 소주
                    imDrinkCheek.setVisibility(View.VISIBLE);
                    imDrinkCheek.setBackgroundResource(R.drawable.traditional_drunken01);
                    break;
                case 2: // 막걸리
                    imDrinkCheek.setVisibility(View.VISIBLE);
                    imDrinkCheek.setBackgroundResource(R.drawable.traditional_drunken02);
                    break;
                case 3: // 와인
                    imDrinkCheek.setVisibility(View.VISIBLE);
                    imDrinkCheek.setBackgroundResource(R.drawable.traditional_drunken03);
                    break;
            }
        }


    }
}
