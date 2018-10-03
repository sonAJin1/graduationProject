package com.example.sonaj.graduationproject.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.ItemRelativeContentsLink;
import com.example.sonaj.graduationproject.databinding.ItemArchiveBinding;
import com.example.sonaj.graduationproject.databinding.ItemPostCommentBinding;
import com.example.sonaj.graduationproject.databinding.ItemRelativeContentsLinkBinding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.RViewHolder>{

    TreeMap<Integer,ItemGetPost> itemPostList;
    static Context context;

    public CommentAdapter(Context context, TreeMap<Integer, ItemGetPost> itemPostList){
        this.context = context;
        this.itemPostList = itemPostList;
    }

    @NonNull
    @Override
    public RViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemPostCommentBinding Abinding = ItemPostCommentBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);
        return new CommentAdapter.RViewHolder(Abinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RViewHolder rViewHolder, int i) {
        if(itemPostList == null) return;
        ItemGetPost item = itemPostList.get(i);
        rViewHolder.bind(item);
        final ItemPostCommentBinding binding = rViewHolder.binding;


        CharactorMake.setDrinkBackgroundColor(item.getDrinkKind(), binding.rlDrinkColor);
        CharactorMake.setEmotionFace(item.getEmotion(), binding.imEmotion);
        CharactorMake.setDrinkBackgroundColor(item.getDrinkKind(), binding.rlDrinkColorSub);
        CharactorMake.setEmotionFace(item.getEmotion(), binding.imEmotionSub);

        int commentPosition = item.getLvl();

        if(commentPosition==1){
            binding.rlMainComment.setVisibility(View.VISIBLE);
            binding.rlSubComment.setVisibility(View.GONE);
        }else if(commentPosition>1){
            binding.rlMainComment.setVisibility(View.GONE);
            binding.rlSubComment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if(itemPostList==null)return 0;
        return itemPostList.size();
    }

    public class RViewHolder extends RecyclerView.ViewHolder {

        ItemPostCommentBinding binding;

        public RViewHolder(ItemPostCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding = DataBindingUtil.bind(itemView);
        }

        void bind(ItemGetPost item) {
              binding.setPostItem(item);

        }
    }
}