package com.example.sonaj.graduationproject.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sonaj.graduationproject.Adapter.CommentAdapter;
import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.ObjectUtils;
import com.example.sonaj.graduationproject.databinding.ActivitySelectPostBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SelectMyPostActivity extends Activity {

    Context mContext;
   // ActivitySelectPostBinding binding;

    //intent 로 받아오는 값
    int position;
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
    TextView tvComment;
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
    RecyclerView commentRecyclerview;

    // 댓글 관련 리스트, 어댑터
    CommentAdapter commentAdapter;
    TreeMap<Integer,ItemGetPost> commentList;

    final static String IP_ADDRESS =  "http://13.209.48.183/getComment.php";


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


        showBackgroundLight(DrinkKind);
        clickTrashBtn();

        // 댓글 서버로 요청 보내기
        commentRequest task = new commentRequest();
        task.execute(IP_ADDRESS);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentText(); // intent 로 보낸 내용 ui 에 적용
            }
        },200);

    }

    public void init(){
        tvUsrNickname = (TextView)findViewById(R.id.tv_usr_nickname);
        tvUsrContent = (TextView)findViewById(R.id.tv_usr_content);
        tvWriteTime = (TextView)findViewById(R.id.tv_write_time);
        tvPostContent = (TextView)findViewById(R.id.tv_post_content);
        tvViews = (TextView)findViewById(R.id.tv_views);
        tvComment = (TextView)findViewById(R.id.tv_comment);
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
        commentRecyclerview = (RecyclerView) findViewById(R.id.rc_like_contents);
        commentList = new TreeMap<>();
    }

    public void setContentText(){
        tvUsrNickname.setText(nickname);
        tvUsrContent.setText(SelectContent);
        tvWriteTime.setText("1분 전");
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

        // comment
        String commentCountShow = "";
        int commentCount =0;
        if(commentAdapter.getItemCount()>0){
            commentCount = commentAdapter.getItemCount();
        }

        if(commentCount>9) {
            commentCountShow = commentCount + "개";
        }else{
            commentCountShow = "0" + commentCount + "개";
        }
        tvComment.setText(commentCountShow);

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
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",position);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        x_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
    }

    private void showCommentList(){

        List<ItemGetPost> sortCommentList = new ArrayList<>();

        if(commentList.size()>0){
            Iterator<Integer> integerIteratorKey = commentList.keySet().iterator(); //키값 오름차순 정렬
            while(integerIteratorKey.hasNext()){
                int key = integerIteratorKey.next();
                sortCommentList.add(commentList.get(key)); //key 값으로 정렬된 순서대로 value 값 넣어서 arraylist로 만든다
            }
            commentAdapter = new CommentAdapter(mContext, sortCommentList,group,order);
            commentRecyclerview.setAdapter(commentAdapter);
            commentRecyclerview.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        }

    }

    /** 댓글 요청 */
    /**
     * API에 DATA 요청
     */
    private class commentRequest extends AsyncTask<String, Void, ItemGetPost[]> {


        @Override
        protected ItemGetPost[] doInBackground(String... params) {
            String severURL = params[0];

            OkHttpClient client = new OkHttpClient.Builder().build();
            RequestBody formBody = new FormBody.Builder()
                    .add("group", String.valueOf(group))
                    .build();

            Request request = new Request.Builder()
                    .url(severURL)
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                //gson을 이용하여 json을 자바 객채로 전환하기
                Gson gson = new GsonBuilder().setLenient().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("result");
                Log.e("rootObject", String.valueOf(rootObject));
                ItemGetPost[] post = gson.fromJson(rootObject, ItemGetPost[].class);
                return post;


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ItemGetPost[] posts) {
            super.onPostExecute(posts);
            if (ObjectUtils.isEmpty(posts)) {
            } else {

                if (posts != null || posts.length > 0) {
                    //받아온 데이터가 있으면 일단 ItemGetContentsServer 에 넣은 후 꺼내쓴다.
                    for (ItemGetPost post : posts) {

                        if(post.getLvl()>0){ // 댓글만
                            commentList.put(post.getOrder(),
                                    new ItemGetPost(
                                            post.getGroup(),
                                            post.getLvl(),
                                            post.getOrder(),
                                            post.getNickname(),
                                            post.getDrinkKind(),
                                            post.getEmotion(),
                                            post.getSelectContent(),
                                            post.getCocktailReceived(),
                                            post.getCheeringCock(),
                                            post.getLaughCock(),
                                            post.getComfortCock(),
                                            post.getSadCock(),
                                            post.getAngerCock(),
                                            post.getViews(),
                                            post.getText(),
                                            post.getImage(),
                                            post.getUploadTime(),
                                            post.getDrunkDegree()
                                    ));
                        }

                    }
                    showCommentList();
                }

            }

        }

    }
}
