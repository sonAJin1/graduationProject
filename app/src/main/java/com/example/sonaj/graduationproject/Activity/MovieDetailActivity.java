package com.example.sonaj.graduationproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.sonaj.graduationproject.Adapter.RelativeContentsLinkAdapter;
import com.example.sonaj.graduationproject.Adapter.TodayMovieRecommendAdapter;
import com.example.sonaj.graduationproject.Adapter.WeekHotMovieAdapter;
import com.example.sonaj.graduationproject.ItemGetContentsServer;
import com.example.sonaj.graduationproject.ItemRelativeContentsLink;
import com.example.sonaj.graduationproject.ItemTodayRecommendMovie;
import com.example.sonaj.graduationproject.ItemWeekHotMovie;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.DataUtil;
import com.example.sonaj.graduationproject.View.ContentsView;
import com.example.sonaj.graduationproject.databinding.ActivityMovieDetailBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MovieDetailActivity extends AppCompatActivity {

    ActivityMovieDetailBinding binding;
    Context mContext;

    private static String IP_ADDRESS = "http://13.209.48.183/getRelativeLink.php";

    /** 서버에서 받아온 관련 콘텐츠 list*/
    List<ItemRelativeContentsLink> serverRelativeContentList;
    RelativeContentsLinkAdapter relativeContentsLinkAdapter;

    /** 버튼 onClick */
    OnClick onClick;

    /** intent로 받아 온 내용 */
    String title;
    String actor;
    String director;
    String summary;
    String contents;
    float naver;
    float IMDB;
    String RTTomato;
    String imgUrl;
    int type;
    int year;
    boolean isLike;

    /** Content line */
    int lineCnt;

    /** sharedPreference */
    // Usr 관련 데이터 저장해두는 sharedPreference
    static String sharedKey = "usrInfo";
    String typeToString=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        //인텐트로 관련 내용 받아오기
        Intent intent = getIntent();
        title  = intent.getStringExtra("title");
        actor  = intent.getStringExtra("actor");
        director  = intent.getStringExtra("director");
        summary  = intent.getStringExtra("summary");
        contents  = intent.getStringExtra("contents");
        naver = intent.getFloatExtra("naver",0);
        IMDB  = intent.getFloatExtra("IMDB",0);
        RTTomato  = intent.getStringExtra("RTTomato");
        imgUrl = intent.getStringExtra("imageUrl");
        type = intent.getIntExtra("type",0);
        year = intent.getIntExtra("year",2018);
        isLike = intent.getBooleanExtra("like",false);

        Picasso.with(this).load(imgUrl).fit().centerCrop().into(binding.imContentsDetailTitle); // imageLoad

        init();
        showContentByType(); // 콘텐츠의 종류에 따라 보여지는 view 수정
        getRelativeContentsPHP(); // 관련 콘텐츠 (링크) 받아오기
        setLikeBtnChange(); // 좋아요 버튼 누를 때 shared 에 저장
        setTextViewMaxLine(); // content 내용 길이 조절

    }

    public void showContentByType(){

        //TYPE 에 상관없이 공통으로 찍히는 것
        binding.tvContentsTitle.setText(title); // title
        binding.tvContentsDetailDirector.setText(director); // 감독
        binding.tvContentsDetailSummary.setText(summary); // 개요
        binding.tvContentsDetailActor.setText(actor); // 배우
        binding.tvContentsDetailContents.setText(contents); //내용
        binding.tvContentsDetailImdbScore.setText(String.valueOf(IMDB)); // 가운데 imdb 평점은 어떤 콘텐츠이던 값이 찍힘
        binding.tvContentsYear.setText(String.valueOf(year));
        if(isLike){
            binding.btnDetailLike.setChecked(true);
        }else{
            binding.btnDetailLike.setChecked(false);
        }

        switch (type){
            case 0 : //영화
                binding.tvContentsDetailNaverScore.setVisibility(View.VISIBLE);
                binding.tvContentsDetailNaverScoreTitle.setVisibility(View.VISIBLE);
                binding.tvContentsDetailRttomatoScore.setVisibility(View.VISIBLE);
                binding.tvContentsDetailRttomatoScoreTitle.setVisibility(View.VISIBLE);
                binding.tvContentsDetailImdbScoreTitle.setText("IMDB 평점");
                binding.tvContentsDetailNaverScore.setText(String.valueOf(naver));
                binding.tvContentsDetailRttomatoScore.setText(RTTomato);
                binding.tvContentsDetailDirectorTitle.setText("감독"); //감독
                binding.tvContentsDetailActorTitle.setText("출연"); // 배우
                binding.tvContentsDetailSummaryTitle.setText("개요"); // 개요
                binding.tvContentType.setText("영화");

                break;
            case 1 : //책
                binding.tvContentsDetailNaverScore.setVisibility(View.INVISIBLE);
                binding.tvContentsDetailNaverScoreTitle.setVisibility(View.INVISIBLE);
                binding.tvContentsDetailRttomatoScore.setVisibility(View.INVISIBLE);
                binding.tvContentsDetailRttomatoScoreTitle.setVisibility(View.INVISIBLE);
                binding.tvContentsDetailImdbScoreTitle.setText("네이버 평점");
                binding.tvContentsDetailDirectorTitle.setText("저자"); //감독
                binding.tvContentsDetailActorTitle.setText("출판사"); // 배우
                binding.tvContentsDetailSummaryTitle.setText("페이지"); // 개요
                binding.tvContentType.setText("책");

                break;
            case 2: //드라마
                int imdb = Math.round(IMDB);
                binding.tvContentsDetailNaverScore.setVisibility(View.INVISIBLE);
                binding.tvContentsDetailNaverScoreTitle.setVisibility(View.INVISIBLE);
                binding.tvContentsDetailRttomatoScore.setVisibility(View.INVISIBLE);
                binding.tvContentsDetailRttomatoScoreTitle.setVisibility(View.INVISIBLE);
                binding.tvContentsDetailImdbScoreTitle.setText("시즌");
                binding.tvContentsDetailImdbScore.setText(imdb+"개");
                binding.tvContentsDetailDirectorTitle.setText("주연"); //감독
                binding.tvContentsDetailActorTitle.setText("장르"); // 배우
                binding.tvContentsDetailSummaryTitle.setText("특징"); // 개요
                binding.tvContentType.setText("드라마");
                break;
        }

    }

    public void init(){
        serverRelativeContentList = new ArrayList<>(); // 서버에서 받아오는 내용을 담을 list

        onClick = new OnClick();
        binding.setOnClick(onClick);
    }

    private void setRecyclerView(){


        /** 오늘의 추천 콘텐츠 recyclerView */

        relativeContentsLinkAdapter = new RelativeContentsLinkAdapter(this, serverRelativeContentList);
        binding.rcRelatedContent.setAdapter(relativeContentsLinkAdapter);

        //recyclerView 스크롤 방향 설정 (가로로 스크롤 > HORIZONTAL)
        binding.rcRelatedContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // recyclerView 사이 간격 설정
        binding.rcRelatedContent.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 25; // recyclerView 사이 간격 10
            }
        });

    }




    public void setLikeBtnChange(){
        // like 버튼 누를때 sharedPreference 에 저장하고 삭제하는 부분
        binding.btnDetailLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(binding.btnDetailLike.isChecked()) {
                    SharedPreferences likeSP = mContext.getSharedPreferences("like",0);
                    SharedPreferences.Editor editor = likeSP.edit();
                    editor.putString(title,title); // key 에 title 저장
                    editor.commit();
                }else{
                    SharedPreferences likeSP = mContext.getSharedPreferences("like", 0);
                    String value = likeSP.getString(title,"없음");

                    if(value!=null) { // 해당 값이 있는 경우
                        SharedPreferences.Editor editor = likeSP.edit();
                        editor.remove(title); // 삭제
                        editor.commit();
                    }
                }
            }
        });

    }


    /**관련 콘텐츠 API에 DATA 요청*/
    public void getRelativeContentsPHP(){
        GetRelativeContentsData task = new GetRelativeContentsData();
        task.execute(IP_ADDRESS);
    }


    /**관련 콘텐츠 API에 DATA 요청*/
    private class GetRelativeContentsData extends AsyncTask<String, Void, ItemRelativeContentsLink[]> {


        @Override
        protected ItemRelativeContentsLink[] doInBackground(String... params) {
            String severURL = params[0];

            OkHttpClient client = new OkHttpClient.Builder().build();
            RequestBody formBody = new FormBody.Builder()
                    .add("contentTitle",(String) binding.tvContentsTitle.getText())
                    .build();
            Request request = new Request.Builder()
                    .url(severURL)
                    .post(formBody)
                    .build();

            try{

                Response response = client.newCall(request).execute();

                //gson을 이용하여 json을 자바 객채로 전환하기
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("result");
                //Log.e("rootObject", String.valueOf(rootObject));
                ItemRelativeContentsLink[] post = gson.fromJson(rootObject,ItemRelativeContentsLink[].class);
                return post;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(ItemRelativeContentsLink[] posts){
            super.onPostExecute(posts);

            if(posts!=null || posts.length>0){
                //받아온 데이터가 있으면 관련콘텐츠 item에 넣는다
                for(ItemRelativeContentsLink post : posts){
                    serverRelativeContentList.add(new ItemRelativeContentsLink(
                            post.getTitle(),
                            post.getSubTitle(),
                            post.getImgURL(),
                            post.getInternetLink()
                    ));
                    }
                }else{
                return;
            }
            setRecyclerView();
            }

        }

    public class OnClick{
        public void backClick(View view){
            // 화면을 꺼버린다. 해당 액티비티를 호출하는 화면이 여러군데이기 때문에
            MovieDetailActivity movieDetailActivity = MovieDetailActivity.this;
            movieDetailActivity.finish();
            overridePendingTransition(R.anim.from_right_to_left_in,R.anim.from_right_to_left_out);
        }
        public void selectContent(View view){
            // sharedPreference 에 넣을 값 만들기 type+/+title
            String usrContent = changeTypetoString(type) +"/"+title;
            // sharedPreference 에 저장
            SharedPreferences usrSP = mContext.getSharedPreferences(sharedKey,0);
            SharedPreferences.Editor editor = usrSP.edit();
            editor.putString("usrContent",usrContent); // usrNickname 저장
            editor.commit();

            // 화면을 꺼버린다. 해당 액티비티를 호출하는 화면이 여러군데이기 때문에
            MovieDetailActivity movieDetailActivity = MovieDetailActivity.this;
            movieDetailActivity.finish();
            overridePendingTransition(R.anim.from_right_to_left_in,R.anim.from_right_to_left_out);
        }
        public void showFullContents(View view){
            binding.tvContentsDetailContents.setLines(lineCnt); // 전체 다 보이게
            binding.tvMore.setVisibility(View.GONE); //더보기 안보이게
            binding.imMore.setVisibility(View.GONE); //그라데이션 안보이게
        }
    }
    public void setTextViewMaxLine(){
        final int limit = 7;

        binding.tvContentsDetailContents.post(new Runnable() {
            @Override
            public void run() {
                lineCnt = binding.tvContentsDetailContents.getLineCount(); //text view line 수 가져오기
                if(lineCnt>7){
                    binding.tvContentsDetailContents.setLines(limit); // 7줄로 제한
                    binding.tvMore.setVisibility(View.VISIBLE); // 더보기 보이게
                    binding.imMore.setVisibility(View.VISIBLE); // 그라데이션 보이게
                }else{
                    binding.tvMore.setVisibility(View.GONE); //더보기 안보이게
                    binding.imMore.setVisibility(View.GONE); //그라데이션 안보이게
                }
            }
        });
    }

    public String changeTypetoString(int type){
        switch (type){
            case 0: //영화
                typeToString = "영화";
                break;
            case 1: //책
                typeToString = "책";
                break;
            case 2: //드라마
                typeToString = "드라마";
                break;
        }
        return typeToString;
    }
}



