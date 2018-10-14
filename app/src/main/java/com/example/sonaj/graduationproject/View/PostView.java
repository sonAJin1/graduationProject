package com.example.sonaj.graduationproject.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sonaj.graduationproject.Activity.ArchiveActivity;

import com.example.sonaj.graduationproject.Adapter.PostAdapter;
import com.example.sonaj.graduationproject.Adapter.WritePostAdapter;
import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.BaseView;
import com.example.sonaj.graduationproject.Util.ObjectUtils;
import com.example.sonaj.graduationproject.databinding.PostViewBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostView extends BaseView implements SalonView.RequestListener{
    Context context;
    PostViewBinding binding;

    OnClick onClick;

    /** 다른 사람들이야기 받는 list 와 내 이야기를 받는 list 가 있어야함*/
    /**Adapter*/
    PostAdapter postAdapter; // 다른 사람들 이야기 list Adapter
    WritePostAdapter writePostAdapter; // 내 이야기 list Adapter

    /** adapter item list */
    List<ItemGetPost> postList;
    List<ItemGetPost> myPostList;
    List<ItemGetPost> postCommentList;
    List<ItemGetPost> myPostCommentList;


    /** 서버 통신 */
    private static String POST_IP_ADDRESS = "http://13.209.48.183/getPost.php";
    private static String MY_POST_IP_ADDRESS = "http://13.209.48.183/getMyPost.php";


    /** 서버에서 받아온 list*/
    static HashMap<String, ItemGetPost> serverContentItem = new HashMap<String, ItemGetPost>(); // 서버에서 받아오는 내용을 담을 list;

    final static int POST = 0; // 다른사람 POST
    final static int MY_POST = 1; // 내가 쓴 POST
    final static int WRITE_POST = 2; //이야기 쓰기
    static boolean isPost = true;


    /**sharedPreference */
    static String sharedKey = "usrInfo";
    String usrNickname="";
    int usrDrink;

    /** 몇번 째 이야기 인지 */
    int postCount=1; // 다른사람 이야기 count
    int myPostPosition = 0; // 내 이야기 count

    SalonView salonView;
    RequestListener requestListener;


    /**
     * 생성자에서 view 를 설정하므로 setView 메소드를 생성하지 않음.
     *
     * @param context     : View 가 그렬질 영역의 context
     * @param dataBinding : xml 의 View 들을 담고 있는 데이터 바인딩
     */
    public PostView(Context context, PostViewBinding dataBinding,RequestListener requestListener) {
        super(context, dataBinding);
        this.context = context;
        this.requestListener = requestListener;
        binding = dataBinding;
        init();
        setUsrInfo(); // 게이지, 사용자 정보 set
    }

    @Override
    public void init() {
        onClick = new OnClick();
        binding.setOnClick(onClick);

        //LIST 초기화
        postList = new ArrayList();
        myPostList = new ArrayList<>();

        //댓글 list 초가
        postCommentList = new ArrayList<>();
        myPostCommentList = new ArrayList<>();

        binding.tvCountPost.setText("오늘의 "+postCount+"번째 받은 이야기");
        salonView = new SalonView(context, binding.icSalonView,this);
        setSalonViewButton();


        binding.icSalonView.tvSalonStatus1.setText("1,266명의 사용자 3,541개의 이야기");


    }

    /** 메인 activity 에 있는 메소드 가져다 쓸 때*/
    public interface RequestListener{
        void doScan();
    }


    public void setUsrInfo(){
        SharedPreferences usrSP = context.getSharedPreferences(sharedKey, 0);
        String usrContent = usrSP.getString("usrContent","선택한 콘텐츠가 없습니다");
        usrNickname = usrSP.getString("usrNickname","사용자 닉네임");
        usrDrink = usrSP.getInt("usrDrink",0);
        int usrEmotion = usrSP.getInt("usrEmotion",0);
        binding.imDrinkGauge.setProgressValue(80); // 게이지에 반영
        CharactorMake.setGuageImage(usrDrink,usrEmotion,binding.imDrinkGaugeShape); // 게이지 모양 사용자가 선택한 주종으로 변경
        setGuageColor(usrDrink); // 게이지 색깔 설정


        binding.tvUsrNickname.setText(usrNickname);
        binding.tvUsrSelectContent.setText(usrContent);



    }

    //post 화면 보여줄 때
    public void setPostView(){
        if(postList.size()>0){
            postAdapter.clear();
        }
        getPostPHP(POST); // 서버에 다른사람 데이터 요청
        showBackgroundNeon(POST);
        showSelectContent(); // salon view 에 선택한 콘텐츠 보여주기


    }

    // 이야기쓰기 화면 보여줄 때
    public void setWritePostView(){
        showBackgroundNeon(WRITE_POST);
        if(myPostList.size()>0){
            writePostAdapter.clear();
        }
        myPostList.add(0,new ItemGetPost(0,0,0,"",0,0,"",0,0,0,
                0,0,0,0,"","","")); // position 0 자리는 메세지를 보내는 화면이 뜨기 때문에 0에 들어가서 가려지는 내용이 없게하기 위함

        getPostPHP(MY_POST); // 서버에 내가 쓴 데이터 요청
    }

    public void setSalonViewButton(){
        binding.icSalonView.bluetoothStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestListener.doScan();
            }
        });
    }

    public void showPostView(){
        binding.rlPostView.setVisibility(View.VISIBLE);
        binding.icSalonView.rlPostView.setVisibility(View.GONE);
    }



    public void showSelectContent(){
        SharedPreferences usrSP = context.getSharedPreferences(sharedKey, 0);
        String usrContent = usrSP.getString("usrContent","즐길 콘텐츠를 선택해주세요"); // 선택한 콘텐츠가 있으면 콘텐츠 출력, 없으면 선택해달라는 텍스트 출력
        binding.icSalonView.tvSelectContent.setText(usrContent);
    }

    public void setBackgroundLight(int drinkKind, ImageView imageView){
        Animation showAnimation = new AlphaAnimation(0.6f,1);
        showAnimation.setDuration(500);
        switch (drinkKind){
            case 0: // 맥주인 경우
                imageView.setBackgroundResource(R.drawable.beer_bg_);
                break;
            case 1: // 소주인 경우
                imageView.setBackgroundResource(R.drawable.soju_bg_);
                break;
            case 2: // 막걸리인 경우
                imageView.setBackgroundResource(R.drawable.traditional_bg_);
                break;
            case 3: // 와인인 경우
                imageView.setBackgroundResource(R.drawable.wine_bg_);
                break;
        }
        imageView.startAnimation(showAnimation);
    }

    public void setGuageColor(int drinkKind){
        switch (drinkKind){
            case 0: // 맥주
                binding.imDrinkGauge.setWaveColor(context.getColor(R.color.beerColor));
                break;
            case 1: // 소주
                binding.imDrinkGauge.setWaveColor(context.getColor(R.color.sojuColor));
                break;
            case 2: // 막걸리
                binding.imDrinkGauge.setWaveColor(context.getColor(R.color.transitionColor));
                break;
            case 3: // 와인
                binding.imDrinkGauge.setWaveColor(context.getColor(R.color.wineColor));
                break;

        }
    }


    public void startBlinkAnimation(){
        binding.icSalonView.imNeonOn.setAlpha(1f); //켜진 neon 보이게 변경
        binding.icSalonView.llNeonOn.setAlpha(1f); //켜진 neon 배경 보이게 변경

        Animation animation = new AlphaAnimation(0.1f,1);
        animation.setDuration(110);
        animation.setInterpolator(new AccelerateDecelerateInterpolator()); //점점 빠르게
        animation.setRepeatCount(2);
        animation.setRepeatMode(Animation.REVERSE);
        binding.icSalonView.imNeonOn.startAnimation(animation);
        binding.icSalonView.llNeonOn.startAnimation(animation);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation showAnimation = new AlphaAnimation(0.2f,1);
                showAnimation.setDuration(1000);
                binding.icSalonView.imNeonOn.startAnimation(showAnimation);
                binding.icSalonView.llNeonOn.startAnimation(showAnimation);

            }
        },400);

        salonView.isCoaster =true; // 정보 입력할 수 있는 다이얼로그를 띄울 수 있게 boolean 값 true 로 변경

    }

    public void changeOnView(){
        binding.icSalonView.imDivider.setVisibility(View.GONE); //하단의 하얀색 바 제거
        binding.icSalonView.tvNotification.setText("혼술살롱에 오신 것을 환영합니다"); //text 변경
    }


    public void setRecyclerView(int post){

        switch (post){
            case POST:
                /** 다른 사람들 이야기 recyclerView */

                postAdapter = new PostAdapter(context, postList, postCommentList);
                binding.rcPostListView.setAdapter(postAdapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(context){
                    @Override
                    public boolean canScrollVertically() { // 세로스크롤 막기
                        return false;
                    } // recyclerview 세로 스크롤 막기 (item scrollView 가 작동할 수 있게 하기 위해서)
                };
                layoutManager.setReverseLayout(false);
                layoutManager.setStackFromEnd(true);
                binding.rcPostListView.setLayoutManager(layoutManager);


                if(binding.rcPostListView.getItemDecorationCount()>0){ // 전에 설정된 간격이 있으면
                    binding.rcPostListView.removeItemDecorationAt(0); // 전에 간격 없애기
                }

           //      recyclerView 사이 간격 설정

                binding.rcPostListView.addItemDecoration(new ItemDecoration() {

                    int verOverlap = -1125, horiOverlap = 60;

                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        final int itemPosition = parent.getChildAdapterPosition(view);

                        outRect.set(0, verOverlap,0,0);


                    }
                });

                // Swipe 로 삭제하는 기능
                ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                        int swipedPosition = viewHolder.getAdapterPosition();
                        PostAdapter postAdapter = (PostAdapter)binding.rcPostListView.getAdapter();
                        postAdapter.onItemDismiss(swipedPosition);

//                        Animation showAnimation = new ScaleAnimation(0.9f,1f,0.9f,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//                        showAnimation.setDuration(200);
//                        binding.rcPostListView.startAnimation(showAnimation);

                        postCount++;
                        binding.tvCountPost.setText("오늘의 "+postCount+"번째 받은 이야기");

                        if(postAdapter.getItemCount()==0) { //이야기를 다 봤으면
                           // binding.llNeonOn.setBackgroundResource(R.drawable.background_on);
                            binding.imNeonOnLoading.setVisibility(View.VISIBLE);
                            Toast.makeText(context, "이야기를 모두 보셨습니다", Toast.LENGTH_LONG).show();
                        }

                        if(swipedPosition>0){
                            int currentDrinkKind = postAdapter.getItem(swipedPosition-1).getDrinkKind(); // 지금 이야기를 쓴 사람이 어떤 맥주를 마셨는지
                            setBackgroundLight(currentDrinkKind,binding.llNeonOn); // 주종에 따라서 배경 빛 색깔 바꾸기
                        }



                    }
                };
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(binding.rcPostListView);




                break;

            case MY_POST:

                /** 내 이야기 recyclerView */
                writePostAdapter = new WritePostAdapter(context, myPostList,myPostCommentList);
                binding.rcMyPostListView.setAdapter(writePostAdapter);

                // recyclerView 스크롤 방향설정

                binding.rcMyPostListView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));

                if(binding.rcMyPostListView.getItemDecorationCount()>0){ // 전에 설정된 간격이 있으면
                    binding.rcMyPostListView.removeItemDecorationAt(0); // 전에 간격 없애기
                }

                binding.rcMyPostListView.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.right = 5;
                        outRect.left = 5;
                    }
                }); // 간격 설정 적용

                // 스크롤 할 때 아이템 화면 중앙에 맞추기
                LinearSnapHelper snapHelper = new LinearSnapHelper() {
                    @Override
                    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                        View centerView = findSnapView(layoutManager);
                        if (centerView == null)
                            return RecyclerView.NO_POSITION;

                        int position = layoutManager.getPosition(centerView);
                        int targetPosition = -1;
                        if (layoutManager.canScrollHorizontally()) {
                            if (velocityX < 0) {
                                targetPosition = position - 1;
                            } else {
                                targetPosition = position + 1;
                            }
                        }

                        if (layoutManager.canScrollVertically()) {
                            if (velocityY < 0) {
                                targetPosition = position - 1;
                            } else {
                                targetPosition = position + 1;
                            }
                        }

                        final int firstItem = 0;
                        final int lastItem = layoutManager.getItemCount() - 1;
                        targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                        myPostPosition = targetPosition;

                        /** 타겟 position 잡아서 휴지통 띄워주기*/
                        if(targetPosition==0){
                            binding.tvCountMyPost.setText("이야기 쓰기");
                            binding.imTrashBtn.setVisibility(View.GONE);
                            showBackgroundNeon(WRITE_POST);
                            setBackgroundLight(usrDrink,binding.llNeonOnWrite); // 이야기 쓰기 배경은 사용자가 마시고 있는 주류의 빛 색깔

                        }else{
                            binding.tvCountMyPost.setText("오늘의 "+myPostPosition+"번째 내 이야기");
                            binding.imTrashBtn.setVisibility(View.VISIBLE);
                            showBackgroundNeon(MY_POST);
                            setBackgroundLight(writePostAdapter.getItem(myPostPosition).getDrinkKind(),binding.llNeonOnMyPost); // 내가 어떤 술을 마시면서 썼던 내용들인지

                            if(targetPosition==lastItem){
                            }
                        }
                        return targetPosition;
                    }

                };
                binding.rcMyPostListView.setOnFlingListener(null); // 등록되어있던 onFlagListener (snapHelper) 해제
                snapHelper.attachToRecyclerView(binding.rcMyPostListView);

                break;
        }

    }



    /** API에 DATA 요청*/
    public void getPostPHP(int post){
        GetPostData task = new GetPostData();

        switch (post){
            case POST : // 다른사람들 이야기 요청
                isPost = true;
                task.execute(POST_IP_ADDRESS);
                break;
            case MY_POST : // 내가 쓴 이야기 요청
                isPost = false;
                task.execute(MY_POST_IP_ADDRESS);
                break;

        }

    }

    private void showBackgroundNeon(int status){
        switch (status){
            case POST:
                binding.llNeonOnMyPost.setVisibility(View.GONE);
                binding.llNeonOn.setVisibility(View.VISIBLE);
                binding.llNeonOnWrite.setVisibility(View.GONE);
                break;
            case MY_POST:
                binding.llNeonOnMyPost.setVisibility(View.VISIBLE);
                binding.llNeonOn.setVisibility(View.GONE);
                binding.llNeonOnWrite.setVisibility(View.GONE);
                break;
            case WRITE_POST:
                binding.llNeonOnMyPost.setVisibility(View.GONE);
                binding.llNeonOn.setVisibility(View.GONE);
                binding.llNeonOnWrite.setVisibility(View.VISIBLE);
                break;
        }
    }


    public class OnClick {
        public void writePost(View view) {
            // post 보이는 listview gone 하고 내 이야기 쓰는 부분 listview 띄우기
            setButonvisible(0); // 상단 버튼 변경
            setWritePostView(); // 서버에서 데이터 가져오게 요청
            binding.tvCountMyPost.setVisibility(View.VISIBLE);
            binding.tvCountPost.setVisibility(View.GONE);
            setBackgroundLight(usrDrink,binding.llNeonOnWrite); // 이야기 쓰기 배경은 사용자가 마시고 있는 주류의 빛 색깔

        }
        public void showArchive(View view){
            // 받은이야기, 보낸이야기가 보이는 화면으로 변환
            Intent intent = new Intent(context, ArchiveActivity.class);
            context.startActivity(intent);
            binding.tvCountMyPost.setVisibility(View.GONE);
            binding.tvCountPost.setVisibility(View.VISIBLE);
        }
        public void closeWritePost(View view){
            setButonvisible(1); // 상단 버튼 변경
            binding.tvCountMyPost.setVisibility(View.GONE);
            binding.tvCountPost.setVisibility(View.VISIBLE);
            showBackgroundNeon(POST);
            setBackgroundLight(postAdapter.getItem(postList.size()-1).getDrinkKind(),binding.llNeonOn);

        }

        public void deleteMyPost(View view){ //post 삭제
            writePostAdapter.delete(myPostPosition); // 지금 이야기 삭제
        }

    }


    //버튼을 눌렀을 때 화면에 해당하는 view visible 설정
    public void setButonvisible(int status){
        switch (status){
            case 0: // 내 이야기 쓰기 버튼을 눌렀을 때
                // x 버튼만 남기고 안보이게
                binding.btnCloseWrite.setVisibility(View.VISIBLE);
                binding.btnArchiving.setVisibility(View.GONE);
                binding.btnWriteMyPost.setVisibility(View.GONE);
                // 내 이야기 list 에 해당하는 내용 보이게
                binding.rcMyPostListView.setVisibility(View.VISIBLE);

                // post list 에 해당 하는 내용 안보이게
                binding.rcPostListView.setVisibility(View.GONE);
                binding.tvStatus.setVisibility(View.GONE);
                break;
            case 1: // 내 이야기 쓰기 버튼 닫을 때 > post 화면으로 넘어가게
                // x 버튼 안보이게
                binding.btnCloseWrite.setVisibility(View.GONE);
                binding.btnArchiving.setVisibility(View.VISIBLE);
                binding.btnWriteMyPost.setVisibility(View.VISIBLE);
                // 내 이야기 list 에 해당하는 내용 안보이게
                binding.rcMyPostListView.setVisibility(View.GONE);
                binding.imTrashBtn.setVisibility(View.GONE);
                // post list 보이게
                binding.rcPostListView.setVisibility(View.VISIBLE);
                binding.tvStatus.setVisibility(View.VISIBLE);
        }
    }


    /*****************
     * server 요청
     * ****************/

    /** API에 DATA 가져오기 요청*/
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

        protected void onPreExecute(){
            super.onPreExecute();
            binding.llNeonOnLoading.setVisibility(View.VISIBLE);
            binding.imNeonOnLoading.setVisibility(View.VISIBLE);
            Animation animation = new AlphaAnimation(0.8f,1);
            animation.setDuration(110);
            animation.setInterpolator(new AccelerateDecelerateInterpolator()); //점점 빠르게
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            binding.imNeonOnLoading.startAnimation(animation);
            binding.llNeonOnLoading.startAnimation(animation);
        }

        @Override
        protected void onPostExecute(ItemGetPost[] posts){
            super.onPostExecute(posts);
                postCommentList.clear();
                myPostCommentList.clear();
            if(ObjectUtils.isEmpty(posts)){

            }else{

                if(posts!=null || posts.length>0){
                    binding.imNeonOnLoading.clearAnimation();
                    binding.llNeonOnLoading.clearAnimation();
                    binding.imNeonOnLoading.setVisibility(View.GONE);
                    binding.llNeonOnLoading.setVisibility(View.GONE);
                    //받아온 데이터가 있으면 일단 ItemGetContentsServer 에 넣은 후 꺼내쓴다.
                    for(ItemGetPost post : posts){
                            if(isPost){
                                // 다른사람들의 post
                                if(post.getLvl()==0) { // 댓글 말고 게시글만
                                    if(!post.getNickname().equals(usrNickname)){ // 사용자가 쓰지 않은 글들만
                                        postList.add(new ItemGetPost(
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

                                }else if(post.getLvl()>0){ // 댓글
                                    postCommentList.add(new ItemGetPost(
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
                            }else if(!isPost){
                                // 내 post
                                if(post.getLvl()==0) { // 댓글 말고 게시글만
                                    if(post.getNickname().equals(usrNickname)) {// 내가 쓴 글들만
                                        myPostList.add(new ItemGetPost(
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
                                else if(post.getLvl()>0){ // 댓글
                                    myPostCommentList.add(new ItemGetPost(
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
            if(isPost){
                setRecyclerView(POST); // 받아온 데이터를 어뎁터에 넣어주기
            }else if(!isPost){
                setRecyclerView(MY_POST); // 받아온 데이터를 어뎁터에 넣어주기
            }
            //처음 보여지는 post 사용자의 주류 종류에 따른 배경 빛색깔
            setBackgroundLight(postAdapter.getItem(postList.size()-1).getDrinkKind(),binding.llNeonOn);

        }
    }

}
