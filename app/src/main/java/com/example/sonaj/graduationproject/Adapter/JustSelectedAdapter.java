package com.example.sonaj.graduationproject.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
