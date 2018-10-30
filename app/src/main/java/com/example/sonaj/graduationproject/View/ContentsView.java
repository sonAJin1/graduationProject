package com.example.sonaj.graduationproject.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.sonaj.graduationproject.Adapter.JustSelectedAdapter;
import com.example.sonaj.graduationproject.Adapter.TodayMovieRecommendAdapter;
import com.example.sonaj.graduationproject.Adapter.WeekHotMovieAdapter;
import com.example.sonaj.graduationproject.ItemGetContentsServer;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.ItemJustSelected;
import com.example.sonaj.graduationproject.ItemTodayRecommendMovie;
import com.example.sonaj.graduationproject.ItemWeekHotMovie;
import com.example.sonaj.graduationproject.Util.BaseView;

import com.example.sonaj.graduationproject.Util.ObjectUtils;
import com.example.sonaj.graduationproject.databinding.ContentsViewBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContentsView extends BaseView {

    Context context;
    ContentsViewBinding contentBinding;

    /**Adapter*/
    TodayMovieRecommendAdapter TodayMovieAdapter;
    WeekHotMovieAdapter weekHotMovieAdapter;
    JustSelectedAdapter justSelectedAdapter;

    /** adapter item */
    List<ItemTodayRecommendMovie> TodayRecommendmovieList;
    List<ItemJustSelected> justSelectedsList;
    TreeMap<Integer,ItemWeekHotMovie> weekHotMovieList;
    List<ItemWeekHotMovie> sortLikeList;



    /** 서버 통신 */
    private static String IP_ADDRESS = "http://13.209.48.183/getContents.php";

    /** 서버에서 받아온 list*/
   static HashMap<String,ItemGetContentsServer> serverContentItem = new HashMap<>(); // 서버에서 받아오는 내용을 담을 list;

    /** like sharedPreference */
    static String sharedKey = "like";
    static String serverContentKey = "serverContent";

    /**
     * 생성자에서 view 를 설정하므로 setView 메소드를 생성하지 않음.
     *
     * @param context     : View 가 그렬질 영역의 context
     * @param dataBinding : xml 의 View 들을 담고 있는 데이터 바인딩
     */
    public ContentsView(Context context, ContentsViewBinding dataBinding) {
        super(context, dataBinding);
       this.context = context;
       this.contentBinding  = dataBinding;
       TodayRecommendmovieList = new ArrayList();
       weekHotMovieList = new TreeMap<>();
       justSelectedsList = new ArrayList<>();
        sortLikeList = new ArrayList<>();
       init();
       setContentView();
        getScrollViewTouchEvent(); // 자식 스크롤뷰를 스크롤 할 때 부모의 스크롤 이벤트를 무시
    }

    public void setContentView(){
        if(TodayRecommendmovieList.size()>0){
            clearRecyclerViewItem();
        }
        getContentsPHP(); // 서버에 데이터 요청
    }

    @Override
    public void init() {
    }

    private void setRecyclerView(){
                /** 오늘의 추천 콘텐츠 recyclerView */

                TodayMovieAdapter = new TodayMovieRecommendAdapter(context, TodayRecommendmovieList);
                contentBinding.rcTodayRecommendMovie.setAdapter(TodayMovieAdapter);

                //recyclerView 스크롤 방향 설정 (가로로 스크롤 > HORIZONTAL)
                contentBinding.rcTodayRecommendMovie.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));


                if(contentBinding.rcTodayRecommendMovie.getItemDecorationCount()>0){ // 전에 설정된 간격이 있으면
                    contentBinding.rcTodayRecommendMovie.removeItemDecorationAt(0); // 전에 간격 없애기
                }

                // recyclerView 사이 간격 설정
                contentBinding.rcTodayRecommendMovie.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.right = 25; // recyclerView 사이 간격 10
                    }
                });

                /** 주간 인기 콘텐츠 recyclerView */

                // 좋아요 수가 많은 것 부터 앞쪽에 배치
                if(weekHotMovieList.size()>0) {
                    Iterator<Integer> integerIteratorKey = weekHotMovieList.descendingKeySet().iterator(); //키값 오름차순 정렬
                    while (integerIteratorKey.hasNext()) {
                        int key = integerIteratorKey.next();
                        sortLikeList.add(weekHotMovieList.get(key)); //key 값으로 정렬된 순서대로 value 값 넣어서 arraylist로 만든다
                    }
                }
                weekHotMovieAdapter = new WeekHotMovieAdapter(context,sortLikeList);
                contentBinding.rcWeekHotMovie.setAdapter(weekHotMovieAdapter);

                // recyclerView 스크롤 방향설정
                contentBinding.rcWeekHotMovie.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));


                if(contentBinding.rcWeekHotMovie.getItemDecorationCount()>0){ // 전에 설정된 간격이 있으면
                    contentBinding.rcWeekHotMovie.removeItemDecorationAt(0); // 전에 간격 없애기
                }

                contentBinding.rcWeekHotMovie.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.right = 25;
                    }
                }); // 간격 설정 적용


                /** 방금 선택된 콘텐츠 recyclerView*/
                justSelectedAdapter = new JustSelectedAdapter(context,justSelectedsList);
                contentBinding.rcJustSelectedMovie.setAdapter(justSelectedAdapter);

                // recyclerView 스크롤 방향설정
                contentBinding.rcJustSelectedMovie.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));

                if(contentBinding.rcJustSelectedMovie.getItemDecorationCount()>0){ // 전에 설정된 간격이 있으면
                    contentBinding.rcJustSelectedMovie.removeItemDecorationAt(0); // 전에 간격 없애기
                }

                // recyclerView 사이 간격 설정
                contentBinding.rcJustSelectedMovie.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        outRect.right = 25;
                    }
                });

                Log.e("justSelect size", String.valueOf(justSelectedAdapter.getItemCount()));

    }

    public void getScrollViewTouchEvent(){
        contentBinding.totalScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                contentBinding.rcTodayRecommendMovie.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        contentBinding.rcTodayRecommendMovie.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        contentBinding.rcWeekHotMovie.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        contentBinding.rcJustSelectedMovie.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    public void clearRecyclerViewItem(){
        TodayMovieAdapter.clear();
        weekHotMovieAdapter.clear();
        justSelectedAdapter.clear();
        sortLikeList.clear();
    }

    /** API에 DATA 요청*/
    public void getContentsPHP(){
        GetContentsData task = new GetContentsData();
        task.execute(IP_ADDRESS);
    }


    /** API에 DATA 요청*/
    private class GetContentsData extends AsyncTask<String, Void, ItemGetContentsServer[]> {

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
                SharedPreferences serverContent = context.getSharedPreferences("serverContentKey",0);
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
            serverContentItem.clear();
            if (ObjectUtils.isEmpty(posts)) {
            } else {
                if (posts != null || posts.length > 0) {
                    //받아온 데이터가 있으면 일단 ItemGetContentsServer 에 넣은 후 꺼내쓴다.
                    for (ItemGetContentsServer post : posts) {
                        serverContentItem.put(post.getTitle(), post);

                        // localdb 가져와서 postTitle에 일치하는게 있는지 확인 후 boolean value 하나 만들어서 true false 값 담아 Item 추가하는 부분에 넣어주기
                        SharedPreferences likeSP = context.getSharedPreferences(sharedKey, 0);
                        String value = likeSP.getString(post.getTitle(), "없음");
                        boolean like;
                        if (value.equals(post.getTitle())) { // like list 에 있는 작품이면
                            like = true;

                            /** TODO 만약 like list에는 있는데 서버에서 받아왔을 때 없는 작품이면 삭제*/
                        } else {
                            like = false;
                        }
                        // 오늘의 추천 영화
                        if (post.getTodayContents() == 1) {
                            TodayRecommendmovieList.add(new ItemTodayRecommendMovie(
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
                                    post.getType()

                            ));

                        }
                        // 이번주의 핫 콘텐츠
                        if (post.getLikeCount() > 100) {
                            weekHotMovieList.put(post.getLikeCount(),
                                    new ItemWeekHotMovie(
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
                            justSelectedsList.add(new ItemJustSelected(
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
                setRecyclerView(); // 받아온 데이터를 어뎁터에 넣어주기
            }
            }
        }

        /** 가장 최근에 선택된 콘텐츠 table을 따로 구성하여 데이터 요청을 2번
         * 혹은 date 형식이 아니라 순위로 구분해서 가져오기
         * <php>
         * 내가 선택한 콘텐츠의 lastDate 가져와서 그 이상의 값들을 가지고 있는 콘텐츠 -1씩
         * 내가 선택한 콘텐츠는 데이터 size 값으로 갱신 (맨마지막에 배치)
         * </php>
         * */


    }



