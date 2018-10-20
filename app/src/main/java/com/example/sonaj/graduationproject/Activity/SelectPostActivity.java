package com.example.sonaj.graduationproject.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonaj.graduationproject.Adapter.CommentAdapter;
import com.example.sonaj.graduationproject.Adapter.PostAdapter;
import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.ObjectUtils;
import com.example.sonaj.graduationproject.databinding.ActivitySelectPost2Binding;
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

public class SelectPostActivity extends Activity {
    Context mContext;
    //ActivitySelectPost2Binding binding;

    /** activity 배경 투명도를 조절하는 것이 AppCompatActivity 에서 databinding 하는 방법으로는 불가하여 activity 로 변경하고
     * databinding 방법이 아니라 예전 findById 으로 사용*/

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
    int position;


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
    Button ibCommentSend;
    RadioGroup llCocktailImageGroup;
    LinearLayout llCocktailSendGroup;
    RecyclerView commentRecyclerview;
    LinearLayout llComment;
    EditText etComment;
    ImageButton btnComment;

    // 댓글 관련 리스트, 어댑터
    CommentAdapter commentAdapter;
    TreeMap<Integer,ItemGetPost> commentList;

    final static String IP_ADDRESS =  "http://13.209.48.183/getComment.php";
    final static String SEND_MESSAGE_ADDRESS = "http://13.209.48.183/setComment.php";

    String usrContent;
    String usrNickname;
    int usrDrink;
    int usrEmotion;
    String comment;

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

        init();

        //인텐트로 관련 내용 받아오기
        Intent intent = getIntent();
        position = intent.getIntExtra("position",-1);
        group = intent.getIntExtra("group", 0);
        lvl = intent.getIntExtra("lvl", 0);
        order = intent.getIntExtra("order", 0);
        DrinkKind = intent.getIntExtra("DrinkKind", 0);
        Emotion = intent.getIntExtra("Emotion", 0);
        SelectContent = intent.getStringExtra("SelectContent");
        text = intent.getStringExtra("text");
        CocktailReceived = intent.getIntExtra("CocktailReceived", 0);
        CheeringCock = intent.getIntExtra("CheeringCock", 0);
        LaughCock = intent.getIntExtra("LaughCock", 0);
        ComfortCock = intent.getIntExtra("ComfortCock", 0);
        SadCock = intent.getIntExtra("SadCock", 2018);
        AngerCock = intent.getIntExtra("AngerCock", 2018);
        Views = intent.getIntExtra("Views", 2018);
        Image = intent.getIntExtra("Image", 2018);
        UploadTime = intent.getStringExtra("UploadTime");
        nickname = intent.getStringExtra("nickname");

        setContentText(); // intent 로 보낸 내용 ui 에 적용
        setTextViewMaxLine(); // 게시글 더보기 기능 설정
        showBackgroundLight(DrinkKind); // 배경 빛 색상 설정
        clickTrashBtn(); // 쓰레기통 적용

        // 댓글 서버로 요청 보내기
        commentRequest task = new commentRequest();
        task.execute(IP_ADDRESS);

    }

    public void init() {
        tvUsrNickname = (TextView) findViewById(R.id.tv_usr_nickname);
        tvUsrContent = (TextView) findViewById(R.id.tv_usr_content);
        tvWriteTime = (TextView) findViewById(R.id.tv_write_time);
        tvPostContent = (TextView) findViewById(R.id.tv_post_content);
        tvViews = (TextView) findViewById(R.id.tv_views);
        tvContentMore = (TextView) findViewById(R.id.tv_content_more);
        tvreceiveCocktail = (TextView) findViewById(R.id.receive_cocktail);
        rlDrinkColor = (RelativeLayout) findViewById(R.id.rl_drink_color);
        rlDrinkBackgroundColor = (RelativeLayout) findViewById(R.id.rl_drink_background_color);
        imEmotion = (ImageView) findViewById(R.id.im_emotion);
        imTrashBtn = (ImageView) findViewById(R.id.im_trash_btn);
        x_btn = (ImageView) findViewById(R.id.x_btn);
        ibCocktailSend = (Button) findViewById(R.id.ib_cocktail_send);
        ibCommentSend = (Button) findViewById(R.id.ib_comment_send);
        llCocktailImageGroup = (RadioGroup) findViewById(R.id.ll_cocktail_image_group);
        llCocktailSendGroup = (LinearLayout) findViewById(R.id.ll_cocktail_send_group);
        commentRecyclerview = (RecyclerView) findViewById(R.id.rc_like_contents);
        llComment = (LinearLayout)findViewById(R.id.ll_comment);
        etComment = (EditText)findViewById(R.id.et_comment);
        btnComment = (ImageButton)findViewById(R.id.im_send_commend);

        commentList = new TreeMap<>(); // 댓글 리스트 초기화
    }

    public void setContentText() {
        tvUsrNickname.setText(nickname);
        tvUsrContent.setText(SelectContent);
        tvWriteTime.setText("20분 전");
        tvPostContent.setText(text);


        //view
        String views = "";
        if (Views > 9) {
            views = Views + "회";
        } else {
            views = "0" + Views + "회";
        }
        tvViews.setText(views);

        //receive cocktail
        String receiveCocktail = "";
        if (CocktailReceived > 9) {
            receiveCocktail = CocktailReceived + "개";
        } else {
            receiveCocktail = "0" + CocktailReceived + "개";
        }
        tvreceiveCocktail.setText(receiveCocktail);

        CharactorMake.setDrinkBackgroundColor(DrinkKind, rlDrinkColor);
        CharactorMake.setEmotionFace(Emotion, imEmotion);


    }

    public void setTextViewMaxLine() {
        final int limit = 4;

        tvPostContent.post(new Runnable() {
            @Override
            public void run() {
                int lineCnt = tvPostContent.getLineCount(); //text view line 수 가져오기
                if (lineCnt > limit) {
                    tvPostContent.setLines(limit); // 4줄로 제한
                    tvContentMore.setVisibility(View.VISIBLE); // 더보기 보이게
                } else {
                    tvContentMore.setVisibility(View.GONE); //더보기 안보이게

                }
            }
        });
    }

    public void showBackgroundLight(int drinkKind) {
        Animation showAnimation = new AlphaAnimation(0.6f, 1);
        showAnimation.setDuration(500);

        switch (drinkKind) {
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

    public void clickTrashBtn() {
        //지우기 버튼
        imTrashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",position);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        // x 버튼
        x_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        //칵테일 주기
        ibCocktailSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llCocktailSendGroup.setVisibility(View.VISIBLE);
            }
        });

        ibCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llComment.setVisibility(View.VISIBLE);
                //키보드 보이게 하는 부분
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment = String.valueOf(etComment.getText());
                SharedPreferences usrSP = mContext.getSharedPreferences("usrInfo", 0);
                usrContent = usrSP.getString("usrContent","선택한 콘텐츠가 없습니다");
                usrNickname = usrSP.getString("usrNickname","사용자 닉네임");
                usrDrink = usrSP.getInt("usrDrink",0);
                usrEmotion = usrSP.getInt("usrEmotion",0);

                // 댓글 서버로 보내기
                commentSendRequest task = new commentSendRequest();
                task.execute(SEND_MESSAGE_ADDRESS);
            }
        });


        llCocktailImageGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int sendCocktailPosition = radioGroup.indexOfChild(radioGroup.findViewById(i));
                llCocktailSendGroup.setVisibility(View.GONE);
                if (sendCocktailPosition != -1) {
                    //textColor 변경
                    ibCocktailSend.setTextColor(mContext.getColor(R.color.sendBtnStatusColor));
                    // left icon color 변경
                    Drawable[] drawables = ibCocktailSend.getCompoundDrawables();
                    Drawable wrapDrawable = DrawableCompat.wrap(drawables[0]);
                    DrawableCompat.setTint(wrapDrawable, mContext.getColor(R.color.sendBtnStatusColor));
                } else if (sendCocktailPosition == -1) {
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

    private void sendNewComment(TreeMap<Integer,ItemGetPost> commentList){
        List<ItemGetPost> sortCommentList = new ArrayList<>();

        Log.e("commentList size", String.valueOf(commentList.size()));
        Iterator<Integer> integerIteratorKey = commentList.keySet().iterator(); //키값 오름차순 정렬
        while(integerIteratorKey.hasNext()){
            int key = integerIteratorKey.next();
            sortCommentList.add(commentList.get(key)); //key 값으로 정렬된 순서대로 value 값 넣어서 arraylist로 만든다
        }
        commentAdapter.clean();
        commentAdapter.add(sortCommentList);
        Toast.makeText(mContext,"댓글이 등록되었습니다",Toast.LENGTH_LONG).show();
        etComment.setText(""); //올리면 초기화
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

    /** 댓글 요청 */
    /**
     * API에 DATA 요청
     */
    private class commentSendRequest extends AsyncTask<String, Void, ItemGetPost[]> {


        @Override
        protected ItemGetPost[] doInBackground(String... params) {
            String severURL = params[0];

            OkHttpClient client = new OkHttpClient.Builder().build();
            /**post로 게시물 내용 보내기*/
            RequestBody formBody = new FormBody.Builder()
                    .add("group", String.valueOf(group))
                    .add("lvl","1")
                    //  .add("postOrder","0")
                    .add("usrNickname",usrNickname) // 얘네는 sharedPreference 에서 가져와서 보여주기
                    .add("drinkKind", String.valueOf(usrDrink))
                    .add("drunkDegree", String.valueOf(0))
                    .add("emotion", String.valueOf(usrEmotion))
                    .add("selectContent",usrContent)
                    .add("text",comment)
//                    .add("uploadTime","") > uploadTime 은 서버에 현재시간으로 넣어줄 것
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
                    sendNewComment(commentList);
                }

            }

        }

    }
}
