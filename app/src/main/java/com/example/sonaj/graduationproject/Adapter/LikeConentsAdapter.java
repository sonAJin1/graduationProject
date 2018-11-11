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
import com.example.sonaj.graduationproject.ItemLikeContents;
import com.example.sonaj.graduationproject.ItemWeekHotMovie;
import com.example.sonaj.graduationproject.databinding.ItemLikeContentBinding;
import com.example.sonaj.graduationproject.databinding.ItemWeekHotMovieBinding;

import java.util.List;

public class LikeConentsAdapter extends RecyclerView.Adapter<LikeConentsAdapter.likeViewHolder> {

    List<ItemLikeContents> likeContents;
    static Context context;

    static String sharedKey = "like";


    public LikeConentsAdapter(Context context, List<ItemLikeContents> likeContents) {
        this.context = context;
        this.likeContents = likeContents;
    }

    @NonNull
    @Override
    public likeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemLikeContentBinding LBinding = ItemLikeContentBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);

        return new likeViewHolder(LBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final likeViewHolder likeViewHolder, final int i) {
        if (likeContents == null) return;
        final ItemLikeContents item = likeContents.get(i);
        likeViewHolder.bind(item);

        likeViewHolder.Lbinding.btnLike.setChecked(true); //like list에 있는 애들은 like 버튼이 클릭되어있는 상태여야함

        // list item 클릭해서 상세 화면으로 넘어가는 부분
        likeViewHolder.Lbinding.ivLikeContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), MovieDetailActivity.class);
                intent.putExtra("title",item.getTitle());
                intent.putExtra("actor",item.getActor());
                intent.putExtra("director",item.getDirector());
                intent.putExtra("summary",item.getSummary());
                intent.putExtra("contents",item.getContents());
                intent.putExtra("naver",item.getNaverScore());
                intent.putExtra("IMDB",item.getimdbScore());
                intent.putExtra("RTTomato",item.getrtScore());
                intent.putExtra("imageUrl",item.getimgURL());
                intent.putExtra("type",item.getType());

                context.startActivity(intent);
            }
        });

        // like 버튼 누를때 sharedPreference 에 저장하고 삭제하는 부분
        likeViewHolder.Lbinding.btnLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(!likeViewHolder.Lbinding.btnLike.isChecked()) {
                    SharedPreferences likeSP = context.getSharedPreferences(sharedKey, 0);
                    String value = likeSP.getString(item.getTitle(),"없음");

                    if(value!=null) { // 해당 값이 있는 경우
                        SharedPreferences.Editor editor = likeSP.edit();
                        editor.remove(item.getTitle()); // 삭제
                        editor.commit();
                        likeViewHolder.Lbinding.btnLike.setChecked(false);
                        item.setLike(false);

                        // list item 에서도 삭제
                        likeContents.remove(i);
                        notifyDataSetChanged();
                    }
                }else{

                }
            }
        });

        //title 에 type 추가
        String type="";
        switch (item.getType()){
            case 0: //영화
                type = "영화/";
                break;
            case 1: //책
                type = "책/";
                break;
            case 2: //드라마
                type = "드라마/";
                break;
        }
        likeViewHolder.Lbinding.tvLikeTitle.setText(type+item.getTitle());

    }

    @Override
    public int getItemCount() {
        if (likeContents == null) return 0;
        return likeContents.size();
    }

    public void clear() {
        likeContents.clear();
        notifyDataSetChanged();
    }

    public class likeViewHolder extends RecyclerView.ViewHolder {

        ItemLikeContentBinding Lbinding;

        public likeViewHolder(ItemLikeContentBinding binding) {
            super(binding.getRoot());
            this.Lbinding = binding;
            Lbinding = DataBindingUtil.bind(itemView);
        }

        void bind(ItemLikeContents item) {
            Lbinding.setLikeContentItem(item);
        }
    }
}