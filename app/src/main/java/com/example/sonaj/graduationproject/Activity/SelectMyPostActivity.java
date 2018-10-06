package com.example.sonaj.graduationproject.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.databinding.ActivitySelectPostBinding;

public class SelectMyPostActivity extends Activity {

    Context mContext;
   // ActivitySelectPostBinding binding;

    //intent 로 받아오는 값
    int group;
    int lvl;
    int order;
    int DrinkKind;
    int Emotion;
    String SelectContent;
    int CocktailReceived;
    int CheeringCock;
    int LaughCock;
    int ComfortCock;
    int SadCock;
    int AngerCock;
    int Views;
    int Image;
    String UploadTime;
    String text;
    String nickname;

    //
    TextView tvUsrNickname;
    TextView tvUsrContent;
    TextView tvWriteTime;
    TextView tvPostContent;
    TextView tvViews;
    TextView tvreceiveCocktail;
    TextView tvCheeringCocktailCount;
    TextView tvLaughCocktailCount;
    TextView tvComfortCocktailCount;
    TextView tvSadCocktailCount;
    TextView tvAngerCocktailCount;
    RelativeLayout rlDrinkColor;
    RelativeLayout rlDrinkBackgroundColor;
    ImageView imEmotion;
    TextView tvContentMore;
    ImageView imTrashBtn;
    ImageView x_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제
        mContext = this;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_select_post);
       // binding = DataBindingUtil.setContentView(this, R.layout.activity_select_post);


        init();


        //인텐트로 관련 내용 받아오기
        Intent intent = getIntent();
        group  = intent.getIntExtra("group",0);
        lvl  = intent.getIntExtra("lvl",0);
        order  = intent.getIntExtra("order",0);
        DrinkKind  = intent.getIntExtra("DrinkKind",0);
        Emotion  = intent.getIntExtra("Emotion",0);
        SelectContent = intent.getStringExtra("SelectContent");
        text = intent.getStringExtra("text");
        CocktailReceived  = intent.getIntExtra("CocktailReceived",0);
        CheeringCock  = intent.getIntExtra("CheeringCock",0);
        LaughCock = intent.getIntExtra("LaughCock",0);
        ComfortCock = intent.getIntExtra("ComfortCock",0);
        SadCock = intent.getIntExtra("SadCock",2018);
        AngerCock = intent.getIntExtra("AngerCock",2018);
        Views = intent.getIntExtra("Views",2018);
        Image = intent.getIntExtra("Image",2018);
        UploadTime = intent.getStringExtra("UploadTime");
        nickname = intent.getStringExtra("nickname");

        setContentText();
        showBackgroundLight(DrinkKind);
        clickTrashBtn();

    }

    public void init(){
        tvUsrNickname = (TextView)findViewById(R.id.tv_usr_nickname);
        tvUsrContent = (TextView)findViewById(R.id.tv_usr_content);
        tvWriteTime = (TextView)findViewById(R.id.tv_write_time);
        tvPostContent = (TextView)findViewById(R.id.tv_post_content);
        tvViews = (TextView)findViewById(R.id.tv_views);
        tvContentMore = (TextView)findViewById(R.id.tv_content_more);
        tvreceiveCocktail = (TextView)findViewById(R.id.receive_cocktail);
        rlDrinkColor = (RelativeLayout) findViewById(R.id.rl_drink_color);
        rlDrinkBackgroundColor = (RelativeLayout) findViewById(R.id.rl_drink_background_color);
        imEmotion = (ImageView) findViewById(R.id.im_emotion);
        tvCheeringCocktailCount = (TextView)findViewById(R.id.tv_cheering_cocktail_count);
        tvLaughCocktailCount = (TextView)findViewById(R.id.tv_laugh_cocktail_count);
        tvComfortCocktailCount = (TextView)findViewById(R.id.tv_comfort_cocktail_count);
        tvSadCocktailCount = (TextView)findViewById(R.id.tv_sad_cocktail_count);
        tvAngerCocktailCount = (TextView)findViewById(R.id.tv_anger_cocktail_count);
        imTrashBtn = (ImageView)findViewById(R.id.im_trash_btn);
        x_btn = (ImageView)findViewById(R.id.x_btn);
    }

    public void setContentText(){
        tvUsrNickname.setText(nickname);
        tvUsrContent.setText(SelectContent);
        tvWriteTime.setText(UploadTime);
        tvPostContent.setText(text);

        // int 에 String 처리가 필요한 item 들 처리
        tvCheeringCocktailCount.setText("("+CheeringCock+")");
        tvLaughCocktailCount.setText("("+LaughCock+")");
        tvComfortCocktailCount.setText("("+ComfortCock+")");
        tvSadCocktailCount.setText("("+SadCock+")");
        tvAngerCocktailCount.setText("("+AngerCock+")");

        //view
        String views = "";
        if(Views>9){
            views = Views+"회";
        }else{
            views = "0"+Views+"회";
        }
        tvViews.setText(views);

        //receive cocktail
        String receiveCocktail = "";
        if(CocktailReceived>9){
            receiveCocktail = CocktailReceived+"개";
        }else{
            receiveCocktail = "0"+CocktailReceived+"개";
        }
        tvreceiveCocktail.setText(receiveCocktail);

        CharactorMake.setDrinkBackgroundColor(DrinkKind,rlDrinkColor);
        CharactorMake.setEmotionFace(Emotion,imEmotion);

    }

    public void showBackgroundLight(int drinkKind){
        Animation showAnimation = new AlphaAnimation(0.6f,1);
        showAnimation.setDuration(500);

            switch (drinkKind){
                case 0: // 맥주인 경우
                    rlDrinkBackgroundColor.setBackgroundResource(R.drawable.bg_light_beer02);
                    break;
                case 1: // 소주인 경우
                    rlDrinkBackgroundColor.setBackgroundResource(R.drawable.bg_light_soju02);
                    break;
                case 2: // 막걸리인 경우
                    rlDrinkBackgroundColor.setBackgroundResource(R.drawable.bg_light_traditional02);
                    break;
                case 3: // 와인인 경우
                    rlDrinkBackgroundColor.setBackgroundResource(R.drawable.bg_light_wine02);
                    break;
            }
            rlDrinkBackgroundColor.startAnimation(showAnimation);

        }


    public void clickTrashBtn(){
        imTrashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        x_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
