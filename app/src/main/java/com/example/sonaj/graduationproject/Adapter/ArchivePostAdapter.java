package com.example.sonaj.graduationproject.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sonaj.graduationproject.Activity.SelectMyPostActivity;
import com.example.sonaj.graduationproject.Activity.SelectPostActivity;
import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.ItemGetPost;
import com.example.sonaj.graduationproject.ItemJustSelected;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.databinding.ItemArchiveBinding;
import com.example.sonaj.graduationproject.databinding.ItemJustSelectedBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ArchivePostAdapter extends RecyclerView.Adapter<ArchivePostAdapter.AViewHolder> {

    List<ItemGetPost> itemPostList;
    Context context;

    /**sharedPreference */
    static String sharedKey = "usrInfo";
    String usrNickname;

    // 날짜 헤더 표시를 위해 비교하는 값
    String compareDate;

    public ArchivePostAdapter(Context context,List<ItemGetPost> itemPostList){
        this.context = context;
        this.itemPostList = itemPostList;
    }

    @NonNull
    @Override
    public AViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemArchiveBinding Abinding = ItemArchiveBinding.
                inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);

        SharedPreferences usrSP = context.getSharedPreferences(sharedKey, 0); // 지금 이용중인 사용자의 닉네임 가져옴
        usrNickname = usrSP.getString("usrNickname","사용자 닉네임");

        return new AViewHolder(Abinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AViewHolder aViewHolder, int i) {
        if(itemPostList == null) return;
        ItemGetPost item = itemPostList.get(i);
        aViewHolder.bind(item);


        if(!usrNickname.equals(item.getNickname())){ // background 에 라운딩 주기 > 다른사람이 보냈을 때
            aViewHolder.binding.rlBackground.setBackground(ContextCompat.getDrawable(context,R.drawable.post_rounding));
        }

        if(i==0){
            //맨 처음 item
            //날짜 비교
            //현재 날짜 가져오기
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String getTime = sdf.format(date);
            Log.e("CurrentTime",getTime);

            String[] postDate  = item.getUploadTime().split(" "); // post 날짜를 빈칸으로 나눈다
            compareDate = postDate[0]; // 가져온 날짜가 비교 기준이 된다.
            Log.e("postDate",postDate[0]);

            if(getTime.equals(compareDate)){ // 오늘 날짜와 비교해서 같으면
                aViewHolder.binding.tvDateTitle.setText("오늘");
            }else{
                aViewHolder.binding.tvDateTitle.setText(compareDate); // 오늘 날짜와 같지 않으면 해당 날짜를 출력한다
            }
            aViewHolder.binding.tvDateTitle.setVisibility(View.VISIBLE); // 날짜 헤더를 보여준다.


        }else{ // 2번째 item 부터

            String[] postDate  = item.getUploadTime().split(" "); // post 날짜를 빈칸으로 나눈다
            if(compareDate.equals(postDate[0])){
                //만약 전에 출력된 날짜와 같다면 헤더를 감춘다
                aViewHolder.binding.tvDateTitle.setVisibility(View.GONE);
            }else {
                //만약 같지 않다면 날짜 헤더를 출력해서 보여준다
                aViewHolder.binding.tvDateTitle.setText(postDate[0]);
                aViewHolder.binding.tvDateTitle.setVisibility(View.VISIBLE);
                // 다음에 가져오는 item 날짜와 비교하기 위해 compare 값으로 넣어준다.
                compareDate = postDate[0];
            }

        }



        // 리스트에서 보이는 글자 수 제한
        if(item.getText().length()>18){
            aViewHolder.binding.tvUsrTextMore.setVisibility(View.VISIBLE);
        }else{
            aViewHolder.binding.tvUsrTextMore.setVisibility(View.GONE);
        }

        CharactorMake.setDrinkBackgroundColor(item.getDrinkKind(),aViewHolder.binding.rlDrinkColor); //들어오는 술 종류에 따라 배경색
        CharactorMake.setEmotionFace(item.getEmotion(),aViewHolder.binding.imEmotion); // 들어오는 감정에 따라 표정





        /// ======================== 관련 내용 따로 띄우는 intent ============================
        aViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 댓글 제외하고 dialog 로 보낸 뒤 dialog 에서 댓글 관련 호출
                Intent intent = new Intent(context.getApplicationContext(), SelectPostActivity.class);
                intent.putExtra("position",i);
                intent.putExtra("group",itemPostList.get(i).getGroup());
                intent.putExtra("lvl",itemPostList.get(i).getLvl());
                intent.putExtra("order",itemPostList.get(i).getOrder());
                intent.putExtra("DrinkKind",itemPostList.get(i).getDrinkKind());
                intent.putExtra("Emotion",itemPostList.get(i).getEmotion());
                intent.putExtra("SelectContent",itemPostList.get(i).getSelectContent());
                intent.putExtra("CocktailReceived",itemPostList.get(i).getCocktailReceived());
                intent.putExtra("CheeringCock",itemPostList.get(i).getCheeringCock());
                intent.putExtra("LaughCock",itemPostList.get(i).getLaughCock());
                intent.putExtra("ComfortCock",itemPostList.get(i).getComfortCock());
                intent.putExtra("SadCock",itemPostList.get(i).getSadCock());
                intent.putExtra("AngerCock",itemPostList.get(i).getAngerCock());
                intent.putExtra("Views",itemPostList.get(i).getViews());
                intent.putExtra("Image",itemPostList.get(i).getImage());
                intent.putExtra("UploadTime",itemPostList.get(i).getUploadTime());
                intent.putExtra("text",itemPostList.get(i).getText());
                intent.putExtra("nickname",itemPostList.get(i).getNickname());
             //   context.startActivity(intent);
                Activity activity = (Activity)context;
                activity.startActivityForResult(intent,1);
            }
        });

}

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==1){
            if(resultCode ==Activity.RESULT_OK){
                int result = data.getIntExtra("result",-1);
                deleteItem(result); // 날아온 position 값 가지고 있는 item 삭제
                Toast.makeText(context,"이야기가 삭제되었습니다",Toast.LENGTH_LONG).show();
            }else if(resultCode ==Activity.RESULT_CANCELED){
                // 반환값이 없을 경우
            }
        }
    }


    public void deleteItem(int position){
        itemPostList.remove(position);
        notifyDataSetChanged();

    }



    @Override
    public int getItemCount() {
        if(itemPostList==null)return 0;
        return itemPostList.size();
    }

    public class AViewHolder extends RecyclerView.ViewHolder{

        ItemArchiveBinding binding;

        public AViewHolder(@NonNull ItemArchiveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding = DataBindingUtil.bind(itemView);
        }
        void bind(ItemGetPost item){
            binding.setPostItem(item);
        }
    }
}
