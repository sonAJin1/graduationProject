package com.example.sonaj.graduationproject.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.ItemRelativeContentsLink;
import com.example.sonaj.graduationproject.Util.ObjectUtils;
import com.example.sonaj.graduationproject.View.ContentsView;
import com.example.sonaj.graduationproject.databinding.ItemArchiveBinding;
import com.example.sonaj.graduationproject.databinding.ItemPostCommentBinding;
import com.example.sonaj.graduationproject.databinding.ItemRelativeContentsLinkBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.RViewHolder>{

    List<ItemGetPost> commentList;
    static Context context;

    int commentGroup;
    int commentOrder;
    String text;

    /**sharedPreference */
    static String sharedKey = "usrInfo";
    int usrDrink;
    String usrContent;
    String usrNickname;
    int usrEmotion;

    /** server 에 대댓글 등록 요청 */
    private static String IP_ADDRESS = "http://13.209.48.183/setSubComment.php";


    public CommentAdapter(Context context, List<ItemGetPost> commentList){
        this.context = context;
        this.commentList = commentList;
    }

    public CommentAdapter(Context context, List<ItemGetPost> commentList, int commentGroup, int commentOrder){
        this.context = context;
        this.commentList = commentList;
        this.commentGroup = commentGroup;
        this.commentOrder = commentOrder;
    }

    @NonNull
    @Override
    public RViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemPostCommentBinding Abinding = ItemPostCommentBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);
        return new CommentAdapter.RViewHolder(Abinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RViewHolder rViewHolder, int i) {
        if(commentList == null) return;
        ItemGetPost item = commentList.get(i);
        rViewHolder.bind(item);
        final ItemPostCommentBinding binding = rViewHolder.binding;

        int commentPosition = item.getLvl();

        if(commentPosition==1){
            binding.rlMainComment.setVisibility(View.VISIBLE);
            binding.rlSubComment.setVisibility(View.GONE);
        }else if(commentPosition>1){
            binding.rlMainComment.setVisibility(View.GONE);
            binding.rlSubComment.setVisibility(View.VISIBLE);
        }

        CharactorMake.setDrinkBackgroundColor(item.getDrinkKind(), binding.rlDrinkColor);
        CharactorMake.setEmotionFace(item.getEmotion(), binding.imEmotion);
        CharactorMake.setDrinkBackgroundColor(item.getDrinkKind(), binding.rlDrinkColorSub);
        CharactorMake.setEmotionFace(item.getEmotion(), binding.imEmotionSub);

        sendSubContent(binding,i);
    }

    public void sendSubContent(ItemPostCommentBinding binding, int position){
        binding.tvCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llComment.setVisibility(View.VISIBLE);
                binding.tvCommentSend.setVisibility(View.GONE);

                binding.etComment.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.etComment.setFocusable(true);
                        binding.etComment.requestFocus();
                        //키보드 보이게 하는 부분
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                });
                binding.imSendCommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context,"click",Toast.LENGTH_LONG).show();
                        // order 계산
                        /** 경우의 수 : 댓글이 2개 이상 달려있을 경우 > for 문을 돌려서 그 다음 댓글을 찾아 다음 댓글의 order 을 새로 다는 대댓글의 order 로 설정
                         *             댓글이 1개 달려있을 경우 > for 문으로 돌려도 다음 댓글을 찾을 수 없음 > 대댓글의 order는 0일 수 없다 0인 경우는 다음 댓글을 찾지 못했다는 뜻이므로 list size 를 order 로 가져간다*/
                        commentOrder = 0; // 다음 댓글을 찾기 전에 commentOrder 초기화
                        for(int i = position+1; i<commentList.size(); i++){
                            if(commentList.get(i).getLvl()==1){ // 다음 댓글의 order을 구하기 위해 for 문을 돌린다
                                commentOrder = commentList.get(i).getOrder(); // 다음 댓글의 order 가 새로 등록될 대댓글의 order
                            }
                        }
                        if(commentOrder == 0){ // 다음 댓글을 찾지 못한경우
                            commentOrder = getItemCount();
                        }
                        // text 가져오기
                        text = String.valueOf(binding.etComment.getText());
                        // sharedPreference 에서 사용자 정보 가져오기
                        SharedPreferences usrSP = context.getSharedPreferences(sharedKey, 0);
                        usrContent = usrSP.getString("usrContent","선택한 콘텐츠가 없습니다");
                        usrNickname = usrSP.getString("usrNickname","사용자 닉네임");
                        usrDrink = usrSP.getInt("usrDrink",0);
                        usrEmotion = usrSP.getInt("usrEmotion",0);

                        writeComment task = new writeComment();
                        task.execute(IP_ADDRESS);
                    }
                });

            }
        });
    }

    public void add(List<ItemGetPost> item){
        commentList.addAll(item);
        notifyDataSetChanged();
    }

    public void clean(){
        commentList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(commentList==null)return 0;
        return commentList.size();
    }

    public class RViewHolder extends RecyclerView.ViewHolder {

        ItemPostCommentBinding binding;

        public RViewHolder(ItemPostCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding = DataBindingUtil.bind(itemView);
        }

        void bind(ItemGetPost item) {
              binding.setPostItem(item);
        }
    }


    /** 대댓글 등록 */
    private class writeComment extends AsyncTask<String, Void, ItemGetPost[]> {
        @Override
        protected ItemGetPost[] doInBackground(String... params) {
            String severURL = params[0];

            OkHttpClient client = new OkHttpClient.Builder().build();
            /**post로 게시물 내용 보내기*/
            RequestBody formBody = new FormBody.Builder()
                    .add("group", String.valueOf(commentGroup))
                    .add("lvl", String.valueOf(2)) //대댓글이기 때문에 lvl은 2
                    .add("postOrder",String.valueOf(commentOrder)) // 계산해서 넣기
                    .add("usrNickname", usrNickname) // 얘네는 sharedPreference 에서 가져와서 보여주기
                    .add("drinkKind", String.valueOf(usrDrink)) // 얘네는 sharedPreference 에서 가져와서 보여주기
                    .add("emotion", String.valueOf(usrEmotion)) // 얘네는 sharedPreference 에서 가져와서 보여주기
                    .add("selectContent", usrContent) // 얘네는 sharedPreference 에서 가져와서 보여주기
                    .add("text", text)
//                    .add("uploadTime","") > uploadTime 은 서버에 현재시간으로 넣어줄 것
                    .build();

            Request request = new Request.Builder()
                    .url(severURL)
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                //gson을 이용하여 json을 자바 객채로 전환하기
                Gson gson = new GsonBuilder().setLenient().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("result");
                Log.e("rootObject", String.valueOf(rootObject));
                ItemGetPost[] post = gson.fromJson(rootObject, ItemGetPost[].class);
                return post;


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ItemGetPost[] posts) {
            super.onPostExecute(posts);
            if (ObjectUtils.isEmpty(posts)) {
                Toast.makeText(context, "댓글 등록에 실패했습니다", Toast.LENGTH_LONG).show();
            } else {

                if (posts != null || posts.length > 0) {
                    commentList.clear(); // 댓글창 clear

                    for (ItemGetPost post : posts) {
                        // 댓글을 등록하면 해당 게시물의 댓글을 모두 가져온다
                        //lvl 이 0보다 큰것만 (댓글만) list 에 등록
                        if (post.getLvl() > 0) {
                            commentList.add(
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
                                            post.getUploadTime(),
                                            post.getDrunkDegree()
                                    ));
                        }
                    }

                }

            }

        }
    }
}
