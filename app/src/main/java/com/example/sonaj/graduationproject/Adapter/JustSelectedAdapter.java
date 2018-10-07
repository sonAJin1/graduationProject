package com.example.sonaj.graduationproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sonaj.graduationproject.Activity.MovieDetailActivity;
import com.example.sonaj.graduationproject.ItemJustSelected;
import com.example.sonaj.graduationproject.databinding.ItemJustSelectedBinding;
import com.example.sonaj.graduationproject.databinding.ItemTodayRecommendMovieBinding;

import java.util.List;

public class JustSelectedAdapter extends RecyclerView.Adapter<JustSelectedAdapter.JViewHolder> {

    List<ItemJustSelected> justSelectedsItem;
    static Context context;

    public JustSelectedAdapter(Context context,List<ItemJustSelected> justSelectedsItem){
        this.context = context;
        this.justSelectedsItem = justSelectedsItem;
    }

    @NonNull
    @Override
    public JViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        ItemJustSelectedBinding Jbinding = ItemJustSelectedBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);
        return new JViewHolder(Jbinding);
    }

    @Override
    public void onBindViewHolder(@NonNull JViewHolder jViewHolder, int i) {
        if(justSelectedsItem == null) return;
        ItemJustSelected item = justSelectedsItem.get(i);
        jViewHolder.bind(item);

        // list item 클릭해서 상세 화면으로 넘어가는 부분
        jViewHolder.Jbindig.ivJustMovie.setOnClickListener(new View.OnClickListener() {
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
                intent.putExtra("type",item.getType());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(justSelectedsItem==null)return 0;
        return justSelectedsItem.size();
    }

    public void clear(){
        justSelectedsItem.clear();
        notifyDataSetChanged();
    }


    public class JViewHolder extends RecyclerView.ViewHolder{

        ItemJustSelectedBinding Jbindig;

        public JViewHolder(ItemJustSelectedBinding binding) {
            super(binding.getRoot());
            this.Jbindig = binding;
            Jbindig = DataBindingUtil.bind(itemView);
        }
        void bind(ItemJustSelected item){Jbindig.setJustSelectedItem(item);}
    }
}
