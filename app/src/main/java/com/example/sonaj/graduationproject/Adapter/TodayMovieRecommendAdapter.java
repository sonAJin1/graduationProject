package com.example.sonaj.graduationproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableArrayMap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.example.sonaj.graduationproject.Activity.MovieDetailActivity;
import com.example.sonaj.graduationproject.binding.BaseRecyclerViewAdapter;
import com.example.sonaj.graduationproject.ItemTodayRecommendMovie;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.databinding.ItemTodayRecommendMovieBinding;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class TodayMovieRecommendAdapter extends RecyclerView.Adapter<TodayMovieRecommendAdapter.TMovieViewHolder> {

    List<ItemTodayRecommendMovie> todayRecommendMovies;
    static Context context;
    String sharedKey = "like";

    public TodayMovieRecommendAdapter(Context context, List<ItemTodayRecommendMovie> todayRecommendMovies){
        this.context = context;
        this.todayRecommendMovies = todayRecommendMovies;
    }

    @NonNull
    @Override
    public TMovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemTodayRecommendMovieBinding Tbinding = ItemTodayRecommendMovieBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);


        return new TMovieViewHolder(Tbinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final TMovieViewHolder tMovieViewHolder, final int i) {
        if(todayRecommendMovies == null) return;
        final ItemTodayRecommendMovie item = todayRecommendMovies.get(i);
        tMovieViewHolder.bind(item);

        switch (item.getType()){
            case 0: //영화
                tMovieViewHolder.Tbinding.tvNaverScore.setVisibility(View.VISIBLE);
                tMovieViewHolder.Tbinding.tvNaverScoreTitle.setVisibility(View.VISIBLE);
                tMovieViewHolder.Tbinding.tvImbdScore.setVisibility(View.VISIBLE);
                tMovieViewHolder.Tbinding.tvImbdScoreTitle.setVisibility(View.VISIBLE);
                tMovieViewHolder.Tbinding.tvRttomatoScore.setVisibility(View.VISIBLE);
                tMovieViewHolder.Tbinding.tvRttomatoScoreTitle.setVisibility(View.VISIBLE);
                break;
            case 1: //책
                tMovieViewHolder.Tbinding.tvNaverScore.setVisibility(View.GONE);
                tMovieViewHolder.Tbinding.tvNaverScoreTitle.setVisibility(View.GONE);
                tMovieViewHolder.Tbinding.tvImbdScore.setVisibility(View.VISIBLE);
                tMovieViewHolder.Tbinding.tvImbdScoreTitle.setVisibility(View.VISIBLE);
                tMovieViewHolder.Tbinding.tvRttomatoScore.setVisibility(View.GONE);
                tMovieViewHolder.Tbinding.tvRttomatoScoreTitle.setVisibility(View.GONE);
                break;
            case 2: //드라마
                tMovieViewHolder.Tbinding.tvNaverScore.setVisibility(View.GONE);
                tMovieViewHolder.Tbinding.tvNaverScoreTitle.setVisibility(View.GONE);
                tMovieViewHolder.Tbinding.tvImbdScore.setVisibility(View.VISIBLE);
                tMovieViewHolder.Tbinding.tvImbdScoreTitle.setVisibility(View.VISIBLE);
                tMovieViewHolder.Tbinding.tvRttomatoScore.setVisibility(View.GONE);
                tMovieViewHolder.Tbinding.tvRttomatoScoreTitle.setVisibility(View.GONE);
                break;
        }
        if(item.getisLike()){ // 좋아요가 눌러져서 true 값을 가지고 있는 작품은 눌려지게
         //   tMovieViewHolder.Tbinding.btnTodayLike.setChecked(true);
        }else{ // 아니면 좋아요 해제
         //   tMovieViewHolder.Tbinding.btnTodayLike.setChecked(false);
        }


        // list item 클릭해서 상세 화면으로 넘어가는 부분
        tMovieViewHolder.Tbinding.ivTodayMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), MovieDetailActivity.class);
                intent.putExtra("title",item.getTitle());
                intent.putExtra("actor",item.getActor());
                intent.putExtra("director",item.getDirector());
                intent.putExtra("summary",item.getSummary());
                intent.putExtra("contents",item.getContents());
                intent.putExtra("naver",item.getNaverScore());
                intent.putExtra("IMDB",item.getIMDBScore());
                intent.putExtra("RTTomato",item.getRTTomatoScore());
                intent.putExtra("imageUrl",item.getimageUrl());
                intent.putExtra("isLike",item.getisLike());
                intent.putExtra("type",item.getType());
                context.startActivity(intent);
            }
        });


        // like 버튼 누를때 sharedPreference 에 저장하고 삭제하는 부분
//        tMovieViewHolder.Tbinding.btnTodayLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
////                if(tMovieViewHolder.Tbinding.btnTodayLike.isChecked()) {
////                    SharedPreferences likeSP = context.getSharedPreferences(sharedKey,0);
////                    SharedPreferences.Editor editor = likeSP.edit();
////                    editor.putString(item.getTitle(),item.getTitle()); // key 에 title 저장
////                    editor.commit();
////                    item.setLike(true);
////                }else{
////                    SharedPreferences likeSP = context.getSharedPreferences(sharedKey, 0);
////                    String value = likeSP.getString(item.getTitle(),"없음");
////
////                    if(value!=null) { // 해당 값이 있는 경우
////                        SharedPreferences.Editor editor = likeSP.edit();
////                        editor.remove(item.getTitle()); // 삭제
////                        editor.commit();
////                        item.setLike(false);
////                    }
////                }
//            }
//        });



    }

    @Override
    public int getItemCount() {
        if(todayRecommendMovies==null) return 0;
        return todayRecommendMovies.size();
    }

    public void clear(){
        todayRecommendMovies.clear();
        notifyDataSetChanged();
    }

    public void add(ItemTodayRecommendMovie itemTodayRecommendMovie){
        todayRecommendMovies.add(itemTodayRecommendMovie);
        notifyDataSetChanged();
    }

    public class TMovieViewHolder extends RecyclerView.ViewHolder {

        ItemTodayRecommendMovieBinding Tbinding;

        public TMovieViewHolder(ItemTodayRecommendMovieBinding binding) {
            super(binding.getRoot());
            this.Tbinding = binding;
            Tbinding = DataBindingUtil.bind(itemView);
        }
        void bind(ItemTodayRecommendMovie item){Tbinding.setTodayMovieItem(item);}
    }
}