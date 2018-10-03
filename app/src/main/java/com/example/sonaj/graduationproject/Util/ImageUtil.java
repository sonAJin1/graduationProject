package com.example.sonaj.graduationproject.Util;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ImageUtil {

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView imageView, String url){
        if(!url.equals("")){
            Picasso.with(imageView.getContext()).load(url).transform(new RoundedCornersTransformation(12,0)).fit().centerCrop().into(imageView);
        }
    }

    @BindingAdapter({"bind:likeImageUrl"})
    public static void loadLikeImage(ImageView imageView, String url){
        if(!url.equals("")){
          Picasso.with(imageView.getContext()).load(url).fit().centerCrop().into(imageView);
        }
    }
}
