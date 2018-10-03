package com.example.sonaj.graduationproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sonaj.graduationproject.ItemRelativeContentsLink;
import com.example.sonaj.graduationproject.ItemTodayRecommendMovie;
import com.example.sonaj.graduationproject.ItemWeekHotMovie;
import com.example.sonaj.graduationproject.databinding.ItemRelativeContentsLinkBinding;
import com.example.sonaj.graduationproject.databinding.ItemTodayRecommendMovieBinding;
import com.example.sonaj.graduationproject.databinding.ItemWeekHotMovieBinding;

import java.util.List;

public class RelativeContentsLinkAdapter extends RecyclerView.Adapter<RelativeContentsLinkAdapter.RViewHolder>{
    List<ItemRelativeContentsLink> RelativeContent;
    static Context context;


    public RelativeContentsLinkAdapter(Context context, List<ItemRelativeContentsLink> RelativeContent) {
        this.context = context;
        this.RelativeContent = RelativeContent;
    }

    @NonNull
    @Override
    public RViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemRelativeContentsLinkBinding wBinding = ItemRelativeContentsLinkBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);

        return new RViewHolder(wBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RViewHolder RViewHolder, final int i) {
        if(RelativeContent==null) return;
        final ItemRelativeContentsLink item = RelativeContent.get(i);
        RViewHolder.bind(item);

        RViewHolder.Wbinding.imRelativeContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // item 을 클릭하면 웹 브라우저로 이동
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getInternetLink()));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(RelativeContent==null) return 0;
        return RelativeContent.size();
    }

    public void clear(){
        RelativeContent.clear();
        notifyDataSetChanged();
    }

    public class RViewHolder extends RecyclerView.ViewHolder {

        ItemRelativeContentsLinkBinding Wbinding;

        public RViewHolder(ItemRelativeContentsLinkBinding binding) {
            super(binding.getRoot());
            this.Wbinding = binding;
            Wbinding = DataBindingUtil.bind(itemView);
        }
        void bind(ItemRelativeContentsLink item){
            Wbinding.setRelativeContentsItem(item);


        }
    }
}
