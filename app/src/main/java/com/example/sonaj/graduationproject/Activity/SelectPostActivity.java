package com.example.sonaj.graduationproject.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.databinding.ActivitySelectPost2Binding;
import com.example.sonaj.graduationproject.databinding.ActivitySelectPostBinding;

public class SelectPostActivity extends Activity {
    Context mContext;
    //ActivitySelectPost2Binding binding;

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
    RelativeLayout rlDrinkColor;
    RelativeLayout rlDrinkBackgroundColor;
    ImageView imEmotion;
    TextView tvContentMore;
    ImageView imTrashBtn;
    ImageView x_btn;
    Button ibCocktailSend;
    RadioGroup llCocktailImageGroup;
    LinearLayout llCocktailSendGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제
        mContext = this;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_select_post2);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_post2);

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

        setContentText(); // intent 로 보낸 내용 ui 에 적용
        setTextViewMaxLine(); // 게시글 더보기 기능 설정
        showBackgroundLight(DrinkKind); // 배경 빛 색상 설정
        clickTrashBtn(); // 쓰레기통 적용
        selectCocktailSend(); // 칵테일 보내기 버튼

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
        imTrashBtn = (ImageView)findViewById(R.id.im_trash_btn);
        x_btn = (ImageView)findViewById(R.id.x_btn);
        ibCocktailSend = (Button)findViewById(R.id.ib_cocktail_send);
        llCocktailImageGroup = (RadioGroup) findViewById(R.id.ll_cocktail_image_group);
        llCocktailSendGroup = (LinearLayout)findViewById(R.id.ll_cocktail_send_group);
    }

    public void setContentText(){
        tvUsrNickname.setText(nickname);
        tvUsrContent.setText(SelectContent);
        tvWriteTime.setText(UploadTime);
        tvPostContent.setText(text);


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

    public void setTextViewMaxLine(){
        final int limit = 4;

        tvPostContent.post(new Runnable() {
            @Override
            public void run() {
                int lineCnt = tvPostContent.getLineCount(); //text view line 수 가져오기
                if(lineCnt>limit){
                    tvPostContent.setLines(limit); // 4줄로 제한
                    tvContentMore.setVisibility(View.VISIBLE); // 더보기 보이게
                }else{
                    tvContentMore.setVisibility(View.GONE); //더보기 안보이게

                }
            }
        });
    }

    public void showBackgroundLight(int drinkKind){
        Animation showAnimation = new AlphaAnimation(0.6f,1);
        showAnimation.setDuration(500);

        switch (drinkKind){
            case 0: // 맥주인 경우
                rlDrinkBackgroundColor.setBackgroundResource(R.drawable.beer_bg_);
                break;
            case 1: // 소주인 경우
                rlDrinkBackgroundColor.setBackgroundResource(R.drawable.soju_bg_);
                break;
            case 2: // 막걸리인 경우
                rlDrinkBackgroundColor.setBackgroundResource(R.drawable.traditional_bg_);
                break;
            case 3: // 와인인 경우
                rlDrinkBackgroundColor.setBackgroundResource(R.drawable.wine_bg_);
                break;
        }
        rlDrinkBackgroundColor.startAnimation(showAnimation);

    }
//
    private void selectCocktailSend(){



    }
//

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
        ibCocktailSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                binding.ibCocktailSend.setFocusableInTouchMode(true);
//                binding.ibCocktailSend.requestFocus();
                llCocktailSendGroup.setVisibility(View.GONE);
                Log.e("dididi","");
            }
        });

        llCocktailImageGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int sendCocktailPosition = radioGroup.indexOfChild(radioGroup.findViewById(i));
                llCocktailSendGroup.setVisibility(View.GONE);
                if(sendCocktailPosition!=-1){
                    //textColor 변경
                    ibCocktailSend.setTextColor(mContext.getColor(R.color.sendBtnStatusColor));
                    // left icon color 변경
                    Drawable[] drawables = ibCocktailSend.getCompoundDrawables();
                    Drawable wrapDrawable = DrawableCompat.wrap(drawables[0]);
                    DrawableCompat.setTint(wrapDrawable, mContext.getColor(R.color.sendBtnStatusColor));
                }else if(sendCocktailPosition==-1){
                    //textColor 변경
                    ibCocktailSend.setTextColor(mContext.getColor(R.color.black));
                    // left icon color 변경
                    Drawable[] drawables = ibCocktailSend.getCompoundDrawables();
                    Drawable wrapDrawable = DrawableCompat.wrap(drawables[0]);
                    DrawableCompat.setTint(wrapDrawable, mContext.getColor(R.color.black));
                }
            }
        });
    }

}
