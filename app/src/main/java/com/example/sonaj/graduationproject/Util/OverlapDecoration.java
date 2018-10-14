package com.example.sonaj.graduationproject.Util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.sonaj.graduationproject.R;

public class OverlapDecoration extends RecyclerView.ItemDecoration {
    private int mItemOffset;

    public OverlapDecoration(int itemOffset) {
        mItemOffset = itemOffset;
    }

    public OverlapDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
    }

}
