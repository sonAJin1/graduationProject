package com.example.sonaj.graduationproject.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.RadioGroup;

import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.ItemJustSelected;
import com.example.sonaj.graduationproject.ItemLikeContents;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.ShadowUtils;
import com.example.sonaj.graduationproject.databinding.ItemJustSelectedBinding;
import com.example.sonaj.graduationproject.databinding.ItemLikeContentBinding;
import com.example.sonaj.graduationproject.databinding.ItemMyPostBinding;
import com.example.sonaj.graduationproject.databinding.ItemPostBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PViewHolder>{

    List<ItemGetPost> PostList;
    static Context context;

    // 댓글 관련 리스트, 어댑터
    List<ItemGetPost> allCommentList;
    CommentAdapter commentAdapter;
    TreeMap<Integer,ItemGetPost> commentList;



    int sendCocktailPosition = -1; //칵테일 보내기로 선택한 칵테일 position 값, 없으면 -1


     public PostAdapter(Context context, List<ItemGetPost> PostList, List<ItemGetPost> allCommentList) {
        this.context = context;
        this.PostList = PostList;
        //댓글 list
         this.allCommentList = allCommentList;

    }

    @NonNull
    @Override
    public PostAdapter.PViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemPostBinding Binding = ItemPostBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new PostAdapter.PViewHolder(Binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PViewHolder pViewHolder, int i) {
        if (PostList == null) return;
        final ItemGetPost item = PostList.get(i);
        pViewHolder.bind(item);
        final ItemPostBinding binding = pViewHolder.binding;

        showCommentList(item,binding); // 댓글

            //댓글
            int commentCount = 0;
            commentCount = commentAdapter.getItemCount();
            String commentCountShow = "";
            if(commentCount>9){
                commentCountShow = commentCount+"개";
            }else{
                commentCountShow = "0"+commentCount+"개";
            }
            binding.tvComment.setText(commentCountShow);

            //view
            String views = "";
            if (item.getViews() > 9) {
                views = item.getViews() + "회";
            } else {
                views = "0" + item.getViews() + "회";
            }
            binding.tvViews.setText(views);

            //receive cocktail
            String receiveCocktail = "";
            if (item.getCocktailReceived() > 9) {
                receiveCocktail = item.getCocktailReceived() + "개";
            } else {
                receiveCocktail = "0" + item.getCocktailReceived() + "개";
            }
            binding.receiveCocktail.setText(receiveCocktail);

            CharactorMake.setDrinkBackgroundColor(item.getDrinkKind(), binding.rlDrinkColor);
            CharactorMake.setEmotionFace(item.getEmotion(), binding.imEmotion);
            CharactorMake.setPostTitleImage(item.getDrinkKind(),item.getEmotion(),binding.drinkGauge);

            // text 부분 limint (더보기 출력)
            final int limit = 4;
            binding.tvPostContent.post(new Runnable() {
                @Override
                public void run() {
                    int lineCnt = binding.tvPostContent.getLineCount(); //text view line 수 가져오기
                    if (lineCnt > limit) {
                        binding.tvPostContent.setLines(limit); // 4줄로 제한
                        binding.tvContentMore.setVisibility(View.VISIBLE); // 더보기 보이게
                    } else {
                        binding.tvContentMore.setVisibility(View.GONE); //더보기 안보이게
                    }
                }
            });


            showCocktailSend(binding); // 칵테일 보내기 버튼 누르면 칵테일 선택 창 뜸
            selectCocktailSend(binding);
            setScrollViewEffect(binding); //scroll관련 효과
        setScrollAnimation(binding);

    }


    private void showCocktailSend(ItemPostBinding binding){
        binding.ibCocktailSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llCocktailSendGroup.setVisibility(View.VISIBLE);
            }
        });

    }

    private void selectCocktailSend(ItemPostBinding binding){
        binding.llCocktailImageGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                sendCocktailPosition = radioGroup.indexOfChild(radioGroup.findViewById(i));
                binding.llCocktailSendGroup.setVisibility(View.GONE);
                if(sendCocktailPosition!=-1){
                    //textColor 변경
                    binding.ibCocktailSend.setTextColor(context.getColor(R.color.sendBtnStatusColor));
                    // left icon color 변경
                    Drawable[] drawables = binding.ibCocktailSend.getCompoundDrawables();
                    Drawable wrapDrawable = DrawableCompat.wrap(drawables[0]);
                    DrawableCompat.setTint(wrapDrawable, context.getColor(R.color.sendBtnStatusColor));
                }else if(sendCocktailPosition==-1){
                    //textColor 변경
                    binding.ibCocktailSend.setTextColor(context.getColor(R.color.black));
                    // left icon color 변경
                    Drawable[] drawables = binding.ibCocktailSend.getCompoundDrawables();
                    Drawable wrapDrawable = DrawableCompat.wrap(drawables[0]);
                    DrawableCompat.setTint(wrapDrawable, context.getColor(R.color.black));
                }
            }
        });
    }

    private void showCommentList(ItemGetPost item,ItemPostBinding binding){
        commentList = new TreeMap<>();
        TreeMap<Integer,ItemGetPost> sortCommentList = new TreeMap<>();

        if(allCommentList.size()>0){

            for(int i =0; i<allCommentList.size(); i++){
                if(item.getGroup()==allCommentList.get(i).getGroup()){ //post group 값과 comment group 값이 같으면
                    commentList.put(allCommentList.get(i).getOrder(),allCommentList.get(i));
                }
            }
            Iterator<Integer> integerIteratorKey = commentList.keySet().iterator(); //키값 오름차순 정렬
            while(integerIteratorKey.hasNext()){
                int key = integerIteratorKey.next();
                sortCommentList.put(key,commentList.get(key)); // 오름차순으로 정렬한 것 정렬 리스트에 담기
                Log.e("key,value","key:"+key);
            }
            commentAdapter = new CommentAdapter(context, sortCommentList);
            binding.rvComment.setAdapter(commentAdapter);
            binding.rvComment.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        }

    }



    @Override
    public int getItemCount() {
        if (PostList == null) return 0;
        return PostList.size();
    }

    public void clear(){
        PostList.clear();
        notifyDataSetChanged();
    }
    public void deleteItem(int position){
         PostList.remove(position);
         notifyDataSetChanged();
    }

    public ItemGetPost getItem(int position){
         return PostList.get(position);
    }


    private boolean onItemMove(int fromPosition, int toPosition){
        if(fromPosition < toPosition){
            for(int i = fromPosition; i < toPosition; i++){
                Collections.swap(PostList,i,i+1);
            }
        }else{
            for(int i = fromPosition; i > toPosition; i--){
                Collections.swap(PostList,i,i-1);
            }
        }
        notifyItemMoved(fromPosition,toPosition);
        return true;
    }
    public void onItemDismiss(int position){
        PostList.remove(position);
        notifyItemRemoved(position);

        //여기서 칵테일 주기 'sendCocktailPosition' 값이 -1 이 아니라면 서버에 db 추가 요청
        //server 에 보내고 -1 로 초기화
        sendCocktailPosition = -1;
    }

    private void setScrollViewEffect(ItemPostBinding binding){
        binding.scrollViewPostItem.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = binding.scrollViewPostItem.getScrollY();
             ///   Log.e("scrollY", String.valueOf(scrollY));

                //170이상으로 넘어가면
                if(scrollY>170){
//                    binding.scrollViewPostItem.smoothScrollTo(0,0);

                }
            }
        });
    }

    public void setScrollAnimation(ItemPostBinding binding){
        int[] location = new int[2];
        binding.rlDrinkColor.getLocationOnScreen(location);
        int ivX = location[0];
        int ivY = location[1];

        int[] img_coordinates = new int[2];
        binding.rlDrinkColor.getLocationOnScreen(img_coordinates);
        float x = img_coordinates[0];
        float y = img_coordinates[1];

//        ViewTreeObserver vto = binding.getRoot().getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int[] location = new int[2];
//                binding.rlDrinkColor.getLocationInWindow(location);
//                 Log.e("location[0]", String.valueOf(location[0]));//x postion
//                 Log.e("location[1]", String.valueOf(location[1]));//x postion
//
//            }
//        }); // 값은 나오는데 값이 다 달라서(같은위치에 있는 item) 테스트가 필요하다


        // 이값이 맞는것 같다!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! > 이 값 먼저 테스트 후 위에 주석처리한 부분 확인하기
        binding.rlDrinkColor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.rlDrinkColor.getViewTreeObserver().removeOnPreDrawListener(this);
                //여기서 뷰의 크기를 가져온다.
                Log.e("getX()", String.valueOf(binding.rlDrinkColor.getX()));
                Log.e("getY()", String.valueOf(binding.rlDrinkColor.getY()));
                return true;
            }
        });
    }


    public class PViewHolder extends RecyclerView.ViewHolder {

        ItemPostBinding binding;

        public PViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding = DataBindingUtil.bind(itemView);
        }

        void bind(ItemGetPost item) {
            binding.setPostItem(item);

        }

    }

}


