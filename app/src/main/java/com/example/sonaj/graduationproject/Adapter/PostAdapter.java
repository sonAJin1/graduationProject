package com.example.sonaj.graduationproject.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.EnterPostInformationDialog;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.ItemJustSelected;
import com.example.sonaj.graduationproject.ItemLikeContents;
import com.example.sonaj.graduationproject.ItemWeekHotMovie;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.Constants;
import com.example.sonaj.graduationproject.Util.ObjectUtils;
import com.example.sonaj.graduationproject.Util.ShadowUtils;
import com.example.sonaj.graduationproject.databinding.ItemJustSelectedBinding;
import com.example.sonaj.graduationproject.databinding.ItemLikeContentBinding;
import com.example.sonaj.graduationproject.databinding.ItemMyPostBinding;
import com.example.sonaj.graduationproject.databinding.ItemPostBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
    String usrContent;
    String usrNickname;
    int usrDrink;
    int usrEmotion;

    ItemPostBinding binding;



    /** 서버 통신 */
    private static String IP_ADDRESS = "http://13.209.48.183/setComment.php";

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

//        viewGroup.setScaleX(0.9f);
//        viewGroup.setScaleY(0.9f);

        return new PostAdapter.PViewHolder(Binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PViewHolder pViewHolder, int i) {
        if (PostList == null) return;

        final ItemGetPost item = PostList.get(i);
        pViewHolder.bind(item);
        binding = pViewHolder.binding;

       // TransitionManager.

//        if(i==getItemCount()){
//            pViewHolder.itemView.setScaleX(1f);
//            pViewHolder.itemView.setScaleY(1f);
//        }
        showCommentList(item); // 댓글


        writeMessage(binding);
        sendMessage(binding,item);

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

    }

    private void writeMessage(ItemPostBinding binding){

         // 댓글 달기
         binding.ibCommentSend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                binding.llComment.setVisibility(View.VISIBLE);
                 //키보드 보이게 하는 부분
                 InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                 imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
             }
         });

         // 칵테일 보내기
         binding.ibCocktailSendM.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 binding.llCocktailSendGroup.setVisibility(View.VISIBLE);
             }
         });

        // 칵테일 보내기 선택
        binding.llCocktailImageGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                sendCocktailPosition = radioGroup.indexOfChild(radioGroup.findViewById(i));
                binding.llCocktailSendGroup.setVisibility(View.GONE);
                if(sendCocktailPosition!=-1){
                    //textColor 변경
                    binding.ibCocktailSendM.setTextColor(context.getColor(R.color.sendBtnStatusColor));
                    // left icon color 변경
                    Drawable[] drawables = binding.ibCocktailSendM.getCompoundDrawables();
                    Drawable wrapDrawable = DrawableCompat.wrap(drawables[0]);
                    DrawableCompat.setTint(wrapDrawable, context.getColor(R.color.sendBtnStatusColor));
                }else if(sendCocktailPosition==-1){
                    //textColor 변경
                    binding.ibCocktailSendM.setTextColor(context.getColor(R.color.black));
                    // left icon color 변경
                    Drawable[] drawables = binding.ibCocktailSendM.getCompoundDrawables();
                    Drawable wrapDrawable = DrawableCompat.wrap(drawables[0]);
                    DrawableCompat.setTint(wrapDrawable, context.getColor(R.color.black));
                }
            }
        });
    }

    private void sendNewComment(ItemPostBinding binding,TreeMap<Integer,ItemGetPost> commentList){
        List<ItemGetPost> sortCommentList = new ArrayList<>();

        Iterator<Integer> integerIteratorKey = commentList.keySet().iterator(); //키값 오름차순 정렬
        while(integerIteratorKey.hasNext()){
            int key = integerIteratorKey.next();
            sortCommentList.add(commentList.get(key)); //key 값으로 정렬된 순서대로 value 값 넣어서 arraylist로 만든다
        }

        CommentAdapter commentA = (CommentAdapter) binding.rvComment.getAdapter();
        Log.e("commentA.getItemCount_b", String.valueOf(commentA.getItemCount()));
//        commentA.clean();
        binding.rvComment.invalidate();
        commentA.add(sortCommentList);
        Log.e("commentA.getItemCount()", String.valueOf(commentA.getItemCount()));

        Toast.makeText(context,"댓글이 등록되었습니다",Toast.LENGTH_LONG).show();


        synchronized (this){
            notifyAll();
        }


        binding.etComment.setText(""); //올리면 초기화

    }

    private void sendMessage(ItemPostBinding binding,ItemGetPost item){

         // 댓글 서버로 보내는 버튼
         binding.imSendCommend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 comment = String.valueOf(binding.etComment.getText());
                 commentGroup = item.getGroup();
                 Log.e("group", String.valueOf(commentGroup));

                 SharedPreferences usrSP = context.getSharedPreferences("usrInfo", 0);
                 usrContent = usrSP.getString("usrContent","선택한 콘텐츠가 없습니다");
                 usrNickname = usrSP.getString("usrNickname","사용자 닉네임");
                 usrDrink = usrSP.getInt("usrDrink",0);
                 usrEmotion = usrSP.getInt("usrEmotion",0);

                 // 댓글 서버로 보내기
                 writePost task = new writePost();
                 task.execute(IP_ADDRESS);

                 new android.os.Handler().postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         sendNewComment(binding,commentList); // adapter 에 들어온 댓글 적용
                     }
                 },200);


             }
         });
    }


    private void showCommentList(ItemGetPost item){
        commentList = new TreeMap<>();
        List<ItemGetPost> sortCommentList = new ArrayList<>();

        if(allCommentList.size()>0){
            for(int i = 0; i<allCommentList.size(); i++){
                if(item.getGroup()==allCommentList.get(i).getGroup()){ //post group 값과 comment group 값이 같으면
                    commentList.put(allCommentList.get(i).getOrder(),allCommentList.get(i));
                }
            }
            Iterator<Integer> integerIteratorKey = commentList.keySet().iterator(); //키값 오름차순 정렬
            while(integerIteratorKey.hasNext()){
                int key = integerIteratorKey.next();
                sortCommentList.add(commentList.get(key)); //key 값으로 정렬된 순서대로 value 값 넣어서 arraylist로 만든다
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
    private class writePost extends AsyncTask<String, Void, ItemGetPost[]> {


        @Override
        protected ItemGetPost[] doInBackground(String... params) {
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

                //gson을 이용하여 json을 자바 객채로 전환하기
                Gson gson = new GsonBuilder().setLenient().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("result");
                Log.e("rootObject", String.valueOf(rootObject));
                ItemGetPost[] post = gson.fromJson(rootObject,ItemGetPost[].class);
                return post;


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(ItemGetPost[] posts){
            super.onPostExecute(posts);
            if(ObjectUtils.isEmpty(posts)){
                Toast.makeText(context,"댓글 등록에 실패했습니다",Toast.LENGTH_LONG).show();
            }else {

                if (posts != null || posts.length > 0) {
                    commentList.clear(); // 댓글창 clear

                    for (ItemGetPost post : posts) {
                        // 댓글을 등록하면 해당 게시물의 댓글을 모두 가져온다
                        //lvl 이 0보다 큰것만 (댓글만) list 에 등록
                        if(post.getLvl()>0){

                            commentList.put(post.getOrder(),
                                    new ItemGetPost(
                                    post.getGroup(),
                                    post.getLvl(),
                                    post.getOrder(),
                                    post.getNickname(),
                                    post.getDrinkKind(),
                                    post.getEmotion(),
                                    post.getSelectContent(),
                                    post.getCocktailReceived(),
                                    post.getCheeringCock(),
                                    post.getLaughCock(),
                                    post.getComfortCock(),
                                    post.getSadCock(),
                                    post.getAngerCock(),
                                    post.getViews(),
                                    post.getText(),
                                    post.getImage(),
                                    post.getUploadTime()
                            ));
                        }
                    }

                }

            }

        }
    }

}


