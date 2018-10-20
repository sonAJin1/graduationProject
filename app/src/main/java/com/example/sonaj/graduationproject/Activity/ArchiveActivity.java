package com.example.sonaj.graduationproject.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.sonaj.graduationproject.Adapter.ArchiveMyPostAdapter;
import com.example.sonaj.graduationproject.Adapter.ArchivePostAdapter;
import com.example.sonaj.graduationproject.Adapter.CustomContentAdapter;
import com.example.sonaj.graduationproject.Adapter.LikeConentsAdapter;
import com.example.sonaj.graduationproject.Adapter.PostAdapter;
import com.example.sonaj.graduationproject.ItemGetContentsServer;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.ItemJustSelected;
import com.example.sonaj.graduationproject.ItemLikeContents;
import com.example.sonaj.graduationproject.ItemTodayRecommendMovie;
import com.example.sonaj.graduationproject.ItemWeekHotMovie;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.ObjectUtils;
import com.example.sonaj.graduationproject.View.PostView;
import com.example.sonaj.graduationproject.databinding.ActivityArchiveBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ArchiveActivity extends AppCompatActivity {

    Context mContext;
    ActivityArchiveBinding binding;
    OnClick onClick;

    // 받은 메세지, 보낸 메세지 list item
    List<ItemGetPost> receivePostList;
    List<ItemGetPost> sendPostList;

    // 맞춤 콘텐츠
    List<ItemWeekHotMovie> customContent;

    /** 서버 통신 */
    private static String POST_IP_ADDRESS = "http://13.209.48.183/getPost.php";
    private static String MY_POST_IP_ADDRESS = "http://13.209.48.183/getMyPost.php";
    private static String CUTSOM_CONTENT = "http://13.209.48.183/getCustomContent.php";

    /**sharedPreference */
    static String sharedKey = "usrInfo";
    String usrNickname="";

    final static int POST = 0; // 다른사람 POST
    final static int MY_POST = 1; // 내가 쓴 POST

    boolean isReceivePost = true; //true 면 다른사람 post 가져옴

    ArchivePostAdapter archivePostAdapter;
    ArchiveMyPostAdapter archiveMyPostAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_archive);
        init();

        // 처음은 받은 이야기를 보여준다.
        onClick.getPostClick(binding.rcArchiveList);

    }

    public void init(){
        onClick = new OnClick();
        binding.setOnClick(onClick);

        receivePostList = new ArrayList<>();
        sendPostList = new ArrayList<>();
        customContent = new ArrayList<>();
    }

    // 다른사람 이야기 화면 보여줄 때
    public void setPostView(){
        if(receivePostList.size()>0){
            receivePostList.clear();
        }
        getPostPHP(POST); // 서버에 다른사람 데이터 요청
    }

    // 내이야기 화면 보여줄 때
    public void setWritePostView(){
        if(sendPostList.size()>0){
            sendPostList.clear();
        }
        getPostPHP(MY_POST); // 서버에 내가 쓴 데이터 요청
    }

    public void setReceiveCocktailVieW(){
        showReceiveCocktailCount(); // 받은 칵테일 갯수
        getCustomContentPHP(); // 맞춤 콘텐츠 받아오기

    }


    /** API에 DATA 요청*/
    public void getPostPHP(int post) {

        //서버에 post로 보낼 사용자의 nickname
        SharedPreferences usrSP = mContext.getSharedPreferences(sharedKey, 0);
        usrNickname = usrSP.getString("usrNickname","사용자 닉네임");

        GetPostData task = new GetPostData();
        switch (post) {
            case POST: // 다른사람들 이야기 요청
                isReceivePost = true;
                task.execute(POST_IP_ADDRESS);
                break;
            case MY_POST: // 내가 쓴 이야기 요청
                isReceivePost = false;
                task.execute(MY_POST_IP_ADDRESS);
                break;
        }
    }

    public void getCustomContentPHP(){
        getCustomContent task = new getCustomContent();
        task.execute(CUTSOM_CONTENT);
    }

        /** 받은 이야기, 보낸 이야기에 따라서 recyclerView item을 바꿔 껴준다
         *  받은 이야기는 postView 에서 받아서 넘기는 순간 저장될것, 보낸이야기는 local 에 저장할 것인지 server에서 가져올것인지? > server에서 가져오기 (해당되는 댓글도 가져와야하기때문)
         * */
    public void setRecyclerView(int postType, boolean isReceivePost){

        if(isReceivePost){ // 이야기 관련 된 경우
          //  RecyclerView.Adapter adapter = null;

            switch (postType){
                case POST:
                    archivePostAdapter = new ArchivePostAdapter(mContext, receivePostList);
                    binding.rcArchiveList.setAdapter(archivePostAdapter);
                    break;
                case MY_POST :
                    archiveMyPostAdapter = new ArchiveMyPostAdapter(mContext,sendPostList);
                    binding.rcArchiveList.setAdapter(archiveMyPostAdapter);
                    break;
            }

            //recyclerView 스크롤 방향 설정
            binding.rcArchiveList.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));

            if(binding.rcArchiveList.getItemDecorationCount()>0){ // 전에 설정된 간격이 있으면
                binding.rcArchiveList.removeItemDecorationAt(0); // 전에 간격 없애기
            }

            // recyclerView 사이 간격 설정
            binding.rcArchiveList.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.bottom = 20; // recyclerView 사이 간격 10
                }
            });
        }

        else{ // 맞춤 콘텐츠
            CustomContentAdapter customContentAdapter = new CustomContentAdapter(mContext,customContent);
            binding.rcCustomContentList.setAdapter(customContentAdapter);

            //recyclerView 스크롤 방향 설정
            binding.rcCustomContentList.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));

            if(binding.rcCustomContentList.getItemDecorationCount()>0){ // 전에 설정된 간격이 있으면
                binding.rcCustomContentList.removeItemDecorationAt(0); // 전에 간격 없애기
            }

            // recyclerView 사이 간격 설정
            binding.rcCustomContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.right = 20; // recyclerView 사이 간격 10
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        archivePostAdapter.onActivityResult(requestCode, resultCode, data);
    }


    public void setTextSelect(int position){
        switch (position){
            case 0: // 받은 메세지
                // 상단 텍스트 버튼 활성화
                binding.tvReceivePostTitle.setAlpha(1f);
                binding.tvSendPostTitle.setAlpha(0.5f);
                binding.tvCocktailStatusTitle.setAlpha(0.5f);
                // 안쪽 내용
                binding.rcArchiveList.setVisibility(View.VISIBLE);
                binding.llCustomContent.setVisibility(View.GONE);
                binding.llReceiveCocktail.setVisibility(View.GONE);
                break;
            case 1: // 보낸 메세지
                // 상단 텍스트 버튼 활성화
                binding.tvReceivePostTitle.setAlpha(0.5f);
                binding.tvSendPostTitle.setAlpha(1f);
                binding.tvCocktailStatusTitle.setAlpha(0.5f);
                // 안쪽 내용
                binding.rcArchiveList.setVisibility(View.VISIBLE);
                binding.llCustomContent.setVisibility(View.GONE);
                binding.llReceiveCocktail.setVisibility(View.GONE);
                break;
            case 2: // 받은 칵테일
                // 상단 텍스트 버튼 활성화
                binding.tvReceivePostTitle.setAlpha(0.5f);
                binding.tvSendPostTitle.setAlpha(0.5f);
                binding.tvCocktailStatusTitle.setAlpha(1f);
                // 안쪽 내용
                binding.rcArchiveList.setVisibility(View.GONE);
                binding.llCustomContent.setVisibility(View.VISIBLE);
                binding.llReceiveCocktail.setVisibility(View.VISIBLE);
                break;
        }
    }

    public class OnClick{
        public void backClick(View view){
            // 화면을 꺼버린다. 해당 액티비티를 호출하는 화면이 여러군데이기 때문에
            ArchiveActivity archiveActivity = ArchiveActivity.this;
            archiveActivity.finish();
            overridePendingTransition(R.anim.from_left_to_right_in,R.anim.from_left_to_right_out);
        }
        public void getPostClick(View view){ // 받은 메세지 아카이빙
            setTextSelect(0);
            setPostView();

        }
        public void sendPostClick(View view){ // 보낸 메세지 아카이빙
            setTextSelect(1);
            setWritePostView();
        }
        public void cocktailStatusClick(View view){ // 받은 칵테일 아카이빙
            setTextSelect(2);
            binding.rcArchiveList.setVisibility(View.GONE);
            binding.llCustomContent.setVisibility(View.VISIBLE);
            binding.llReceiveCocktail.setVisibility(View.VISIBLE);
            setReceiveCocktailVieW();
        }
    }

    public void showReceiveCocktailCount(){
        binding.tvCheeringCocktailCount.setText("(37)");
        binding.tvLaughCocktailCount.setText("(10)");
        binding.tvComfortCocktailCount.setText("(53)");
        binding.tvSadCocktailCount.setText("(21)");
        binding.tvAngerCocktailCount.setText("(12)");

    }


    /** API에 DATA 요청*/
    private class GetPostData extends AsyncTask<String, Void, ItemGetPost[]> {

        @Override
        protected ItemGetPost[] doInBackground(String... params) {
            String severURL = params[0];

            OkHttpClient client = new OkHttpClient.Builder().build();
            /**post로 usr id 보내기*/
            RequestBody formBody = new FormBody.Builder()
                    .add("usrNickname",usrNickname)
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
            }else{
                if(posts!=null || posts.length>0){
                    //받아온 데이터가 있으면 일단 ItemGetContentsServer 에 넣은 후 꺼내쓴다.
                    for(ItemGetPost post : posts){

                        if(post.getLvl()==0) { // 댓글 말고 게시글만
                            if (isReceivePost) {
                                // 다른사람들의 post
                                if(!post.getNickname().equals(usrNickname)){
                                    receivePostList.add(new ItemGetPost(
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
                                            post.getUploadTime(),
                                            post.getDrunkDegree()
                                    ));
                                }

                            } else if (!isReceivePost) {
                                // 내 post
                                if(post.getNickname().equals(usrNickname)){
                                    sendPostList.add(new ItemGetPost(
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
                                            post.getUploadTime(),
                                            post.getDrunkDegree()
                                    ));
                                }

                            }
                        }

                    }

                }

            }
            if(isReceivePost){
                setRecyclerView(POST,true); // 받아온 데이터를 어뎁터에 넣어주기
            }else{
                setRecyclerView(MY_POST,true); // 받아온 데이터를 어뎁터에 넣어주기
            }

        }
    }


    /** API에 DATA 요청*/
    private class getCustomContent extends AsyncTask<String, Void, ItemGetContentsServer[]> {

        @Override
        protected ItemGetContentsServer[] doInBackground(String... params) {
            String severURL = params[0];

            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url(severURL).build();

            try{
                Response response = client.newCall(request).execute();

                //gson을 이용하여 json을 자바 객채로 전환하기
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("result");
                Log.e("rootObject", String.valueOf(rootObject));
                ItemGetContentsServer[] post = gson.fromJson(rootObject,ItemGetContentsServer[].class);

                //sharedPreference
                SharedPreferences serverContent = mContext.getSharedPreferences("serverContentKey",0);
                SharedPreferences.Editor editor = serverContent.edit();
                editor.putString("likeContent", String.valueOf(rootObject)); // key 에 title 저장, value 에 serverContentItem gson 으로 변형한 값
                editor.commit();

                return post;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(ItemGetContentsServer[] posts){
            super.onPostExecute(posts);
            customContent.clear();
            if(posts!=null || posts.length>0){

                for(ItemGetContentsServer post : posts){

                    // localdb 가져와서 postTitle에 일치하는게 있는지 확인 후 boolean value 하나 만들어서 true false 값 담아 Item 추가하는 부분에 넣어주기
                    SharedPreferences likeSP = mContext.getSharedPreferences(sharedKey, 0);
                    String value = likeSP.getString(post.getTitle(),"없음");
                    boolean like;
                    if(value.equals(post.getTitle())){ // like list 에 있는 작품이면
                        like = true;

                        /** TODO 만약 like list에는 있는데 서버에서 받아왔을 때 없는 작품이면 삭제*/
                    }else{
                        like = false;
                    }

                    // CustomContent
                        customContent.add(new ItemWeekHotMovie(
                                post.getTitle(),
                                post.getSubtitle(),
                                post.getDate(),
                                post.getNaverScore(),
                                post.getimdbScore(),
                                post.getrtScore(),
                                post.getDirector(),
                                post.getActor(),
                                post.getSummary(),
                                post.getContents(),
                                post.getimgURL(),
                                like,
                                post.getLikeCount(),
                                post.getType()
                        ));


                }

            }
            setRecyclerView(3,false); // 받아온 데이터를 어뎁터에 넣어주기
        }
    }

}
