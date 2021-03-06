package com.example.sonaj.graduationproject.Adapter;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.ObjectUtils;
import com.example.sonaj.graduationproject.Util.ShadowUtils;
import com.example.sonaj.graduationproject.View.ContentsView;
import com.example.sonaj.graduationproject.View.PostView;
import com.example.sonaj.graduationproject.databinding.ItemMyPostBinding;
import com.example.sonaj.graduationproject.databinding.ItemPostBinding;
import com.example.sonaj.graduationproject.databinding.ItemWritePostBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WritePostAdapter extends RecyclerView.Adapter<WritePostAdapter.WViewHolder> {

    List<ItemGetPost> MyPostList;
    static Context context;

    private static final int CELL_TYPE_WRITE = 0;
    private static final int CELL_TYPE_MY_POST_ITEM = 1;

    int deleteGroup;
    int deletePosition;
    String uploadTime;

    int drunkDegree; // 이야기 쓸 때 서버로 보낼 내 취한 정도
    RequestListener requestListener; // mainActivity 에서 필요한 메소드를 가져다 쓸 때

    /** 서버 통신 */
    private static String WRITE_POST_IP_ADDRESS = "http://13.209.48.183/addPost.php";
    private static String DELETE_POST_IP_ADDRESS = "http://13.209.48.183/deletePost.php";

    ItemWritePostBinding writePostBinding;

    // Usr 관련 데이터 저장해두는 sharedPreference
    static String sharedKey = "usrInfo";
    String myPost;
    String usrNickname;
    int usrDrink;
    int usrEmotion;
    String usrContent;

    // 댓글 관련 리스트, 어댑터
    List<ItemGetPost> allCommentList;
    CommentAdapter commentAdapter;
    TreeMap<Integer,ItemGetPost> commentList;

    public WritePostAdapter(Context context,RequestListener requestListener, List<ItemGetPost> MyPostList, List<ItemGetPost> allCommentList) {
        this.context = context;
        this.MyPostList = MyPostList;
        this.requestListener = requestListener;
        //댓글 list
        this.allCommentList = allCommentList;
    }

    @Override
    public int getItemViewType(int position){
        if(position==0){
            return CELL_TYPE_WRITE;
        }else{
            return CELL_TYPE_MY_POST_ITEM;
        }
    }

    @NonNull
    @Override
    public WViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ViewDataBinding binding;
        context = viewGroup.getContext();
        switch (viewType){
            case CELL_TYPE_WRITE:
                ItemWritePostBinding writePostBinding = ItemWritePostBinding.
                        inflate(inflater, viewGroup, false);
                return new WritePostAdapter.WViewHolder(writePostBinding);
            case CELL_TYPE_MY_POST_ITEM:
                ItemMyPostBinding myPostBinding = ItemMyPostBinding.
                        inflate(inflater, viewGroup, false);
                return new WritePostAdapter.WViewHolder(myPostBinding);
        }
        return null;
    }



    @Override
    public void onBindViewHolder(@NonNull WViewHolder wViewHolder, int position) {

        final ItemGetPost item = MyPostList.get(position);
        switch (wViewHolder.getItemViewType()){
            case CELL_TYPE_WRITE:

                writePostBinding = wViewHolder.writePostBinding;
                writePostBinding.setWritePostItem(item);

                writePostBinding.imWrite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        writePostBinding.imWrite.setVisibility(View.GONE); // 이미지 클릭하면 이미지는 사라진다.
                        writePostBinding.tvWriteMyPost.setVisibility(View.VISIBLE); //edit text 는 나타남
                        writePostBinding.tvWriteMyPost.setFocusable(true);
                        writePostBinding.divider.setVisibility(View.VISIBLE);
                        writePostBinding.btnSendMyPost.setVisibility(View.VISIBLE); // 활성화 된 버튼 살아남
                        writePostBinding.btnUnSendMyPost.setVisibility(View.VISIBLE); // 비활성화 된거 나타남

                        //키보드 보이게 하는 부분
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);



                    }
                });

                writePostBinding.btnSendMyPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myPost = String.valueOf(writePostBinding.tvWriteMyPost.getText());
                        if(myPost.length()>4){ // 5글자 이상이면
                            SharedPreferences usrSP = context.getSharedPreferences(sharedKey, 0);
                            usrNickname = usrSP.getString("usrNickname","사용자 닉네임"); // 서버로 보낼 사용자 닉네임 가져오기
                            usrContent = usrSP.getString("usrContent","선택한 콘텐츠가 없습니다");
                            usrDrink = usrSP.getInt("usrDrink",0);
                            usrEmotion = usrSP.getInt("usrEmotion",0);
                            long now = System.currentTimeMillis();
                            Date date = new Date(now);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            uploadTime = sdf.format(date);

                            drunkDegree = requestListener.getDrunkDegree(); //postView 에서 취한 정도 받아오기

                            //서버에 보내기
                            writePost task = new writePost();
                            task.execute(WRITE_POST_IP_ADDRESS);

                            //키보드 내리는 부분
                            InputMethodManager immhide = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                        }else{
                            Toast.makeText(context,"내용이 너무 짧습니다 (5글자 이상)",Toast.LENGTH_LONG).show();
                        }
                    }
                });

                writePostBinding.tvWriteMyPost.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        writePostBinding.btnUnSendMyPost.setVisibility(View.GONE); // 버튼 비활성화 된거 사라짐
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                break;

            case CELL_TYPE_MY_POST_ITEM:
                ItemMyPostBinding myPostBinding = wViewHolder.MyPostbinding;
                myPostBinding.setItemMyPost(item);

                showCommentList(item, myPostBinding); // 댓글 보여주기

                //shadow
                ShadowUtils.generateBackgroundWithShadow(myPostBinding.rlMyPost, R.color.white,R.dimen.recycle_main_item_padding,R.color.beerColor,R.dimen.contents_title_interval, Gravity.BOTTOM);

                // int 에 String 처리가 필요한 item 들 처리
                myPostBinding.tvCheeringCocktailCount.setText("("+item.getCheeringCock()+")");
                myPostBinding.tvLaughCocktailCount.setText("("+item.getLaughCock()+")");
                myPostBinding.tvComfortCocktailCount.setText("("+item.getComfortCock()+")");
                myPostBinding.tvSadCocktailCount.setText("("+item.getSadCock()+")");
                myPostBinding.tvAngerCocktailCount.setText("("+item.getAngerCock()+")");

                //view
                String views = "";
                if(item.getViews()>9){
                    views = item.getViews()+"회";
                }else{
                    views = "0"+item.getViews()+"회";
                }
                myPostBinding.tvViews.setText(views);

                //receive cocktail
                String receiveCocktail = "";
                if(item.getCocktailReceived()>9){
                    receiveCocktail = item.getCocktailReceived()+"개";
                }else{
                    receiveCocktail = "0"+item.getCocktailReceived()+"개";
                }
                myPostBinding.receiveCocktail.setText(receiveCocktail);

                CharactorMake.setDrinkBackgroundColor(item.getDrinkKind(),myPostBinding.rlDrinkColor);
                CharactorMake.setEmotionFace(item.getEmotion(),myPostBinding.imEmotion);

                break;
        }

    }

    private void showCommentList(ItemGetPost item, ItemMyPostBinding myPostBinding){
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
            Log.e("sortCommentList", String.valueOf(sortCommentList.size()));
            commentAdapter = new CommentAdapter(context, sortCommentList);
            myPostBinding.rcLikeContents.setAdapter(commentAdapter);
            myPostBinding.rcLikeContents.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        }

    }

    /** post View 에 있는 메소드 가져다 쓸 때*/
    public interface RequestListener{
        int getDrunkDegree();
    }

    @Override
    public int getItemCount() {
        if (MyPostList == null) return 1;
        return MyPostList.size();
    }

    public void clear(){
        MyPostList.clear();
        notifyDataSetChanged();
    }

    public void delete(int position){
        deleteGroup = MyPostList.get(position).getGroup();
        deletePosition = position;

        //서버에 보내기
        deletePost deletePost = new deletePost();
        deletePost.execute(DELETE_POST_IP_ADDRESS);

    }

    public ItemGetPost getItem(int position){
        return MyPostList.get(position);
    }

    public void addItem(ItemGetPost item){
        MyPostList.add(item);
        notifyDataSetChanged();
    }

    public class WViewHolder extends RecyclerView.ViewHolder {

        private ItemWritePostBinding writePostBinding;
        private ItemMyPostBinding MyPostbinding;

        public WViewHolder(ItemWritePostBinding binding) {
            super(binding.getRoot());
            this.writePostBinding = binding;
            binding = DataBindingUtil.bind(itemView);
        }
        public WViewHolder(ItemMyPostBinding binding){
            super(binding.getRoot());
            this.MyPostbinding = binding;
            binding = DataBindingUtil.bind(itemView);
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
                   // .add("group",) > group은 서버에서 group 중 가장 큰 값을 가져오면 +1 해서 넣어줄 것
                    .add("lvl","0")
                    .add("postOrder","0")
                    .add("usrNickname",usrNickname)
                    .add("drinkKind", String.valueOf(usrDrink))
                    .add("drunkDegree", String.valueOf(drunkDegree))
                    .add("emotion", String.valueOf(usrEmotion))
                    .add("selectContent",usrContent)
                    .add("text",myPost)
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
                Toast.makeText(context,"이야기 등록에 실패했습니다",Toast.LENGTH_LONG).show();
            }else {

                if (posts != null || posts.length > 0) {
                    //받아온 데이터가 있으면 일단 ItemGetContentsServer 에 넣은 후 꺼내쓴다.
                    for (ItemGetPost post : posts) {
                        // 등록된 댓글
                        if(post.getNickname()==usrNickname){
                            MyPostList.add(new ItemGetPost(
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
                       notifyDataSetChanged();
                    }
                }
                Toast.makeText(context,"이야기가 등록되었습니다",Toast.LENGTH_LONG).show();
                writePostBinding.tvWriteMyPost.setText(""); //올리면 초기화
            }

        }
    }



    /** API에 DATA 삭제 요청*/
    private class deletePost extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String severURL = params[0];

            OkHttpClient client = new OkHttpClient.Builder().build();
            /**post로 게시물 내용 보내기*/
            RequestBody formBody = new FormBody.Builder()
                    .add("unicGroup", String.valueOf(deleteGroup)) //> group 에 해당하는 게시물 삭제
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
                Toast.makeText(context,"이야기가 삭제되었습니다",Toast.LENGTH_LONG).show();
                //서버에서 삭제되면 list에서 삭제
                MyPostList.remove(deletePosition);
                notifyDataSetChanged();
            }else{
                Toast.makeText(context,"이야기 삭제에 실패했습니다",Toast.LENGTH_LONG).show();

            }
        }
    }

}
