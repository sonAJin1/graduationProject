package com.example.sonaj.graduationproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.sonaj.graduationproject.Activity.MovieDetailActivity;
import com.example.sonaj.graduationproject.ItemWeekHotMovie;
import com.example.sonaj.graduationproject.databinding.ItemCustomContentBinding;
import com.example.sonaj.graduationproject.databinding.ItemWeekHotMovieBinding;

import java.util.List;

public class CustomContentAdapter extends RecyclerView.Adapter<CustomContentAdapter.WMovieViewHolder>{
    List<ItemWeekHotMovie> customCotent;
    static Context context;
    String sharedKey = "like";


    public CustomContentAdapter(Context context, List<ItemWeekHotMovie> customCotent) {
        this.context = context;
        this.customCotent = customCotent;
    }

    @NonNull
    @Override
    public WMovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemCustomContentBinding wBinding = ItemCustomContentBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);

        return new WMovieViewHolder(wBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WMovieViewHolder wMovieViewHolder, int i) {
        if(customCotent==null) return;
        final ItemWeekHotMovie item = customCotent.get(i);
        wMovieViewHolder.bind(item);


        if(item.getisLike()){ // 좋아요가 눌러져서 true 값을 가지고 있는 작품은 눌려지게
            wMovieViewHolder.Wbinding.btnCustomContentLike.setChecked(true);
        }else{ // 아니면 좋아요 해제
            wMovieViewHolder.Wbinding.btnCustomContentLike.setChecked(false);
        }


        //like 버튼 클릭해서 sharedPreference 에 저장 또는 삭제
        wMovieViewHolder.Wbinding.btnCustomContentLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(wMovieViewHolder.Wbinding.btnCustomContentLike.isChecked()) {

                    SharedPreferences likeSP = context.getSharedPreferences(sharedKey,0);
                    SharedPreferences.Editor editor = likeSP.edit();
                    editor.putString(item.getTitle(),item.getTitle()); // key 에 title 저장
                    editor.commit();

                    item.setLike(true);
                    item.setLikeCount(item.getLikeCount()+1);
                    wMovieViewHolder.Wbinding.tvCustomContentLikeCount.setText(String.valueOf(item.getLikeCount())); //view 에 likeCount 변화 +1
                }else{

                    SharedPreferences likeSP = context.getSharedPreferences(sharedKey, 0);
                    String value = likeSP.getString(item.getTitle(),"없음");
                    if(value!=null) { // 해당 값이 있는 경우
                        SharedPreferences.Editor editor = likeSP.edit();
                        editor.remove(item.getTitle()); // 삭제
                        editor.commit();

                        item.setLike(false);
                        item.setLikeCount(item.getLikeCount()-1);
                        wMovieViewHolder.Wbinding.tvCustomContentLikeCount.setText(String.valueOf(item.getLikeCount()));//view 에 likeCount 변화 =1
                    }
                }
            }
        });


        // list item 클릭해서 상세 화면으로 넘어가는 부분
        wMovieViewHolder.Wbinding.ivCustomContent.setOnClickListener(new View.OnClickListener() {
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
        if(customCotent==null) return 0;
        return customCotent.size();
    }

    public void clear(){
        customCotent.clear();
        notifyDataSetChanged();
    }

    public class WMovieViewHolder extends RecyclerView.ViewHolder {

        ItemCustomContentBinding Wbinding;

        public WMovieViewHolder(ItemCustomContentBinding binding) {
            super(binding.getRoot());
            this.Wbinding = binding;
            Wbinding = DataBindingUtil.bind(itemView);
        }
        void bind(ItemWeekHotMovie item){Wbinding.setCustomContentItem(item);}
    }
}
