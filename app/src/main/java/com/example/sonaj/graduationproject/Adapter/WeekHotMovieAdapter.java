package com.example.sonaj.graduationproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.sonaj.graduationproject.Activity.MovieDetailActivity;
import com.example.sonaj.graduationproject.ItemTodayRecommendMovie;
import com.example.sonaj.graduationproject.ItemWeekHotMovie;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.binding.BaseRecyclerViewAdapter;
import com.example.sonaj.graduationproject.databinding.ItemTodayRecommendMovieBinding;
import com.example.sonaj.graduationproject.databinding.ItemWeekHotMovieBinding;

import java.util.List;

public class WeekHotMovieAdapter extends RecyclerView.Adapter<WeekHotMovieAdapter.WMovieViewHolder> {

    List<ItemWeekHotMovie> weekHotMovies;
    static Context context;
    String sharedKey = "like";


    public WeekHotMovieAdapter(Context context, List<ItemWeekHotMovie> weekHotMovies) {
        this.context = context;
        this.weekHotMovies = weekHotMovies;
    }

    @NonNull
    @Override
    public WMovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemWeekHotMovieBinding wBinding = ItemWeekHotMovieBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);

        return new WMovieViewHolder(wBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final WMovieViewHolder wMovieViewHolder, int i) {
        if(weekHotMovies==null) return;
        final ItemWeekHotMovie item = weekHotMovies.get(i);
        wMovieViewHolder.bind(item);


        if(item.getisLike()){ // 좋아요가 눌러져서 true 값을 가지고 있는 작품은 눌려지게
            wMovieViewHolder.Wbinding.btnWeekHotMovieLike.setChecked(true);
        }else{ // 아니면 좋아요 해제
            wMovieViewHolder.Wbinding.btnWeekHotMovieLike.setChecked(false);
        }


        //like 버튼 클릭해서 sharedPreference 에 저장 또는 삭제
        wMovieViewHolder.Wbinding.btnWeekHotMovieLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(wMovieViewHolder.Wbinding.btnWeekHotMovieLike.isChecked()) {

                    SharedPreferences likeSP = context.getSharedPreferences(sharedKey,0);
                    SharedPreferences.Editor editor = likeSP.edit();
                    editor.putString(item.getTitle(),item.getTitle()); // key 에 title 저장
                    editor.commit();

                    item.setLike(true);
                    item.setLikeCount(item.getLikeCount()+1);
                    wMovieViewHolder.Wbinding.tvWeekHotMovieLikeCount.setText(String.valueOf(item.getLikeCount())); //view 에 likeCount 변화 +1
                }else{

                    SharedPreferences likeSP = context.getSharedPreferences(sharedKey, 0);
                    String value = likeSP.getString(item.getTitle(),"없음");
                    if(value!=null) { // 해당 값이 있는 경우
                        SharedPreferences.Editor editor = likeSP.edit();
                        editor.remove(item.getTitle()); // 삭제
                        editor.commit();

                        item.setLike(false);
                        item.setLikeCount(item.getLikeCount()-1);
                        wMovieViewHolder.Wbinding.tvWeekHotMovieLikeCount.setText(String.valueOf(item.getLikeCount()));//view 에 likeCount 변화 =1
                    }
                }
            }
        });


        // list item 클릭해서 상세 화면으로 넘어가는 부분
        wMovieViewHolder.Wbinding.ivWeekHotMovie.setOnClickListener(new View.OnClickListener() {
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
                intent.putExtra("year",item.getDate());
                intent.putExtra("like",item.getisLike());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(weekHotMovies==null) return 0;
        return weekHotMovies.size();
    }

    public void clear(){
        weekHotMovies.clear();
        notifyDataSetChanged();
    }

    public class WMovieViewHolder extends RecyclerView.ViewHolder {

        ItemWeekHotMovieBinding Wbinding;

        public WMovieViewHolder(ItemWeekHotMovieBinding binding) {
            super(binding.getRoot());
            this.Wbinding = binding;
            Wbinding = DataBindingUtil.bind(itemView);
        }
        void bind(ItemWeekHotMovie item){Wbinding.setWeekMovieItem(item);}
    }
}
