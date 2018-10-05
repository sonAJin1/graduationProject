package com.example.sonaj.graduationproject.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Handler;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PViewHolder>{

    List<ItemGetPost> PostList;
    static Context context;

    // 댓글 관련 리스트, 어댑터
    List<ItemGetPost> allCommentList;
    CommentAdapter commentAdapter;
    TreeMap<Integer,ItemGetPost> commentList;

    // 댓글쓰기 위한 변수
    String comment;
    int commentGroup;

    final float[] targetX = new float[1];
    final float[] targetY = new float[1];

    ItemPostBinding binding;



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
        binding = pViewHolder.binding;

        showCommentList(item); // 댓글

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

        showCocktailSend(); // 칵테일 보내기 버튼 누르면 칵테일 선택 창 뜸
        selectCocktailSend();

        writeMessage(binding);
        sendMessage(binding,item);

    }

    private void writeMessage(ItemPostBinding binding){
         binding.ibCommentSend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                binding.llComment.setVisibility(View.VISIBLE);
                 //키보드 보이게 하는 부분
                 InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                 imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
             }
         });
    }

    private void sendMessage(ItemPostBinding binding,ItemGetPost item){
         binding.imSendCommend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 comment = binding.etComment.getText();
                 commentGroup = item.getGroup();

             }
         });
    }


    private void showCocktailSend(){
        binding.ibCocktailSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llCocktailSendGroup.setVisibility(View.VISIBLE);
            }
        });

    }

    private void selectCocktailSend(){
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

    private void showCommentList(ItemGetPost item){
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

    private void setScrollViewEffect(){
        binding.scrollViewPostItem.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = binding.scrollViewPostItem.getScrollY();
             ///   Log.e("scrollY", String.valueOf(scrollY));

                //170이상으로 넘어가면
                if(scrollY>170){
                //    binding.scrollViewPostItem.smoothScrollTo(0,0);

                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation moveAnimation = new TranslateAnimation(0,targetX[0],0,targetY[0]);
                            moveAnimation.setDuration(500);
                            binding.drinkGauge.startAnimation(moveAnimation);
                        }
                    },100);

                }
            }
        });
    }

    public void setScrollAnimation(ItemPostBinding binding){
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
                targetX[0] = binding.rlDrinkColor.getX()/2 -binding.rlDrinkColor.getWidth()/4;
                targetY[0] = binding.rlDrinkColor.getY()/2;
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

    /** 내 이야기 쓰기 요청 */
    /** API에 DATA 요청*/
    private class writePost extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String severURL = params[0];

            OkHttpClient client = new OkHttpClient.Builder().build();
            /**post로 게시물 내용 보내기*/
            RequestBody formBody = new FormBody.Builder()
                     .add("group", String.valueOf(commentGroup))
                    .add("lvl","1")
                  //  .add("postOrder","0")
                    .add("usrNickname",usrNickname) // 얘네는 sharedPreference 에서 가져와서 보여주기
                    .add("drinkKind", String.valueOf(usrDrink))
                    .add("emotion", String.valueOf(usrEmotion))
                    .add("selectContent",usrContent)
                    .add("text",comment)
//                    .add("uploadTime","") > uploadTime 은 서버에 현재시간으로 넣어줄 것
                    .build();

            Request request = new Request.Builder()
                    .url(severURL)
                    .post(formBody)
                    .build();

            try{
                Response response = client.newCall(request).execute();
                String phpResponse = response.body().string();
                Log.e("response", phpResponse);

                return phpResponse;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String posts){
            super.onPostExecute(posts);
            // 성공했다는 메세지 받고 local list 에 추가
            if(posts.equals("insert ok")){
                Toast.makeText(context,"이야기가 등록되었습니다",Toast.LENGTH_LONG).show();
                //보내고 초기화
               binding.etComment.setText("");
                //자체 list에 add
//                MyPostList.add();
//                notifyDataSetChanged();
            }
        }
    }

}


