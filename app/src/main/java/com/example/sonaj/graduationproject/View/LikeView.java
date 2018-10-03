package com.example.sonaj.graduationproject.View;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.ViewDataBinding;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.sonaj.graduationproject.Activity.MovieDetailActivity;
import com.example.sonaj.graduationproject.Adapter.LikeConentsAdapter;
import com.example.sonaj.graduationproject.Adapter.TodayMovieRecommendAdapter;
import com.example.sonaj.graduationproject.ItemGetContentsServer;
import com.example.sonaj.graduationproject.ItemLikeContents;
import com.example.sonaj.graduationproject.ItemTodayRecommendMovie;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.BaseView;
import com.example.sonaj.graduationproject.Util.DataUtil;
import com.example.sonaj.graduationproject.databinding.LikeViewBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LikeView extends BaseView {

    Context context;
    LikeViewBinding likeBinding;
    LikeConentsAdapter likeConentsAdapter;

    List<ItemLikeContents> likeMovieList;
    List<ItemLikeContents> likeBookList;
    List<ItemLikeContents> likeDramaList;


    /** 버튼 onClick */
    OnClickLike onClick;

    /**
     * 생성자에서 view 를 설정하므로 setView 메소드를 생성하지 않음.
     *
     * @param context     : View 가 그렬질 영역의 context
     * @param dataBinding : xml 의 View 들을 담고 있는 데이터 바인딩
     */
    public LikeView(Context context, LikeViewBinding dataBinding) {
        super(context, dataBinding);
        this.context = context;
        this.likeBinding = dataBinding;

        init();
    }



    @Override
    public void init() {
        onClick = new OnClickLike();
        likeBinding.setOnClick(onClick);
        likeMovieList = new ArrayList<>();
        likeBookList = new ArrayList<>();
        likeDramaList = new ArrayList<>();

    }
    public void setLikeView(){
        getLikeData();
        onClick.movieClick(likeBinding.rcLikeContents);
       // setRecyclerView(likeMovieList); // 맨 첫화면은 movie
        setContentCount(); // 상단에 콘텐츠 몇개인지 표시

    }

    public void setRecyclerView(List<ItemLikeContents> likeList){
        likeConentsAdapter = new LikeConentsAdapter(context, likeList);
        likeBinding.rcLikeContents.setAdapter(likeConentsAdapter);

        //recyclerView 스크롤 방향 설정 (가로로 스크롤 > HORIZONTAL)
        likeBinding.rcLikeContents.setLayoutManager(new GridLayoutManager(context, 2));
        // recyclerView 사이 간격 설정
        likeBinding.rcLikeContents.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 30; // recyclerView 사이 간격 10
            }
        });
    }

    public void getLikeData(){
       //sharedPreference 에서 key 값을 받아 온 후 server에서 받아온 데이터에서 해당 데이터만 뽑아낸다.
       // 가져온 데이터를 type 별로 list에 넣어둔다.
       // 버튼을 클릭하면 adapter 에 아이템 내용만 갈아끼워지는 식으로
        likeMovieList.clear();
        likeBookList.clear();
        likeDramaList.clear();
        // 좋아요 list 가져오기
        SharedPreferences likeSP = context.getSharedPreferences(ContentsView.sharedKey, 0);
        Map<String,?> likeHashmap = likeSP.getAll();
        for(Map.Entry<String,?> entry : likeHashmap.entrySet()){
            Log.e("entry", entry.getKey());
            String key = entry.getKey(); // 좋아요 key를 가져옴
            // server contents 에서 좋아요 key 와 같은걸 꺼내서 담으면 됨

           ItemGetContentsServer posts = ContentsView.serverContentItem.get(key); // key값에 해당하는 내용을 가져온다

            ItemLikeContents likeContents = new ItemLikeContents(
                            posts.getTitle(),
                            posts.getSubtitle(),
                            posts.getContents(),
                            posts.getActor(),
                            posts.getDirector(),
                            posts.getSummary(),
                            posts.getimgURL(),
                            posts.getType(),
                            posts.getLikeCount(),
                            posts.getLastSelectTime(),
                            posts.getTodayContents(),
                            posts.getNaverScore(),
                            posts.getimdbScore(),
                            posts.getrtScore(),
                            posts.getEmotion(),
                            posts.getDate()
                    );
            int likeType = posts.getType();
                    switch (likeType){
                case 0: // 영화

                    likeMovieList.add(likeContents);
                    break;
                case 1: // 책

                    likeBookList.add(likeContents);
                    break;
                case 2: // 드라마

                    likeDramaList.add(likeContents);
                    break;
            }

            }

    }

    public void setContentCount(){
        likeBinding.tvLikeMovieCount.setText("("+likeMovieList.size()+")");
        likeBinding.tvLikeBookCount.setText("("+likeBookList.size()+")");
        likeBinding.tvLikeDramaCount.setText("("+likeDramaList.size()+")");
    }

    public void changeSelectTypeTextColor(int type){
        switch (type){
            case 0: //영화
                // 영화 text 흰색
                likeBinding.tvLikeMovieCount.setTextColor(context.getColor(R.color.white));
                likeBinding.tvLikeMovie.setTextColor(context.getColor(R.color.white));
                // 책 text 회색
                likeBinding.tvLikeBookCount.setTextColor(context.getColor(R.color.unSelectedColor));
                likeBinding.tvLikeBook.setTextColor(context.getColor(R.color.unSelectedColor));
                // 드라마 회색
                likeBinding.tvLikeDramaCount.setTextColor(context.getColor(R.color.unSelectedColor));
                likeBinding.tvLikeDrama.setTextColor(context.getColor(R.color.unSelectedColor));
                break;
            case 1: //책
                // 영화 text 회색
                likeBinding.tvLikeMovieCount.setTextColor(context.getColor(R.color.unSelectedColor));
                likeBinding.tvLikeMovie.setTextColor(context.getColor(R.color.unSelectedColor));
                // 책 text 흰색
                likeBinding.tvLikeBookCount.setTextColor(context.getColor(R.color.white));
                likeBinding.tvLikeBook.setTextColor(context.getColor(R.color.white));
                // 드라마 회색
                likeBinding.tvLikeDramaCount.setTextColor(context.getColor(R.color.unSelectedColor));
                likeBinding.tvLikeDrama.setTextColor(context.getColor(R.color.unSelectedColor));
                break;
            case 2: //드라마
                // 영화 text 회색
                likeBinding.tvLikeMovieCount.setTextColor(context.getColor(R.color.unSelectedColor));
                likeBinding.tvLikeMovie.setTextColor(context.getColor(R.color.unSelectedColor));
                // 책 text 회색
                likeBinding.tvLikeBookCount.setTextColor(context.getColor(R.color.unSelectedColor));
                likeBinding.tvLikeBook.setTextColor(context.getColor(R.color.unSelectedColor));
                // 드라마 흰색
                likeBinding.tvLikeDramaCount.setTextColor(context.getColor(R.color.white));
                likeBinding.tvLikeDrama.setTextColor(context.getColor(R.color.white));
                break;
        }
    }


    public class OnClickLike{
        public void movieClick(View view){
            setRecyclerView(likeMovieList);
            changeSelectTypeTextColor(0);
        }
        public void bookClick(View view){
            setRecyclerView(likeBookList);
            changeSelectTypeTextColor(1);
        }
        public void dramaClick(View view){
            setRecyclerView(likeDramaList);
            changeSelectTypeTextColor(2);
        }
    }

}
