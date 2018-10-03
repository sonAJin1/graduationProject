package com.example.sonaj.graduationproject.Adapter;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.ShadowUtils;
import com.example.sonaj.graduationproject.View.ContentsView;
import com.example.sonaj.graduationproject.databinding.ItemMyPostBinding;
import com.example.sonaj.graduationproject.databinding.ItemPostBinding;
import com.example.sonaj.graduationproject.databinding.ItemWritePostBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

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

    public WritePostAdapter(Context context, List<ItemGetPost> MyPostList) {
        this.context = context;
        this.MyPostList = MyPostList;
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
                        writePostBinding.btnUnSendMyPost.setVisibility(View.GONE); // 버튼 비활성화 된거 사라짐
                        writePostBinding.btnSendMyPost.setVisibility(View.VISIBLE); // 활성화 된 버튼 살아남
                        ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); //키보드 올라오게
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

                            //서버에 보내기
                            writePost task = new writePost();
                            task.execute(WRITE_POST_IP_ADDRESS);


                        }else{
                            Toast.makeText(context,"내용이 너무 짧습니다 (5글자 이상)",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;

            case CELL_TYPE_MY_POST_ITEM:
                ItemMyPostBinding myPostBinding = wViewHolder.MyPostbinding;
                myPostBinding.setItemMyPost(item);


                //shadow
                ShadowUtils.generateBackgroundWithShadow(myPostBinding.rlMyPost, R.color.white,R.dimen.recycle_main_item_padding,R.color.deerColor,R.dimen.contents_title_interval, Gravity.BOTTOM);

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
    private class writePost extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String severURL = params[0];

            OkHttpClient client = new OkHttpClient.Builder().build();
            /**post로 게시물 내용 보내기*/
            RequestBody formBody = new FormBody.Builder()
                   // .add("group",) > group은 서버에서 group 중 가장 큰 값을 가져오면 +1 해서 넣어줄 것
                    .add("lvl","0")
                    .add("postOrder","0")
                    .add("usrNickname",usrNickname)
                    .add("drinkKind", String.valueOf(usrDrink))
                    .add("emotion", String.valueOf(usrEmotion))
                    .add("selectContent",usrContent)
                    .add("text",myPost)
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
                writePostBinding.tvWriteMyPost.setText("");

                //자체 list에 add
//                MyPostList.add();
//                notifyDataSetChanged();
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
