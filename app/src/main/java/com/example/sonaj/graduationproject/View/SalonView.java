package com.example.sonaj.graduationproject.View;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.example.sonaj.graduationproject.Activity.MainActivity;
import com.example.sonaj.graduationproject.Activity.MovieDetailActivity;
import com.example.sonaj.graduationproject.EnterPostInformationDialog;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.BaseView;
import com.example.sonaj.graduationproject.databinding.SalonViewBinding;

public class SalonView extends BaseView {

    Context context;
    SalonViewBinding salonBinding;
    OnClick onClick;
    RequestListener requestListener;

    // 코스터에 잔이 올라와서 다른 뷰로 넘어갈 수 있는 상태인지
    boolean isCoaster;
    //코스터가 올려진 뒤 다른 화면으로 넘어갔는지 여부 > 이게 넘어간 뒤에야 홈으로 돌아왔을 때 salon view 가 아니라 main 이 보일 수 있게 할 수 있음
    boolean isShowPost;

    //Dialog
    EnterPostInformationDialog enterPostInformationDialog ;


    /**sharedPreference */
    static String sharedKey = "usrInfo";


    /**
     * 생성자에서 view 를 설정하므로 setView 메소드를 생성하지 않음.
     *
     * @param context     : View 가 그렬질 영역의 context
     * @param dataBinding : xml 의 View 들을 담고 있는 데이터 바인딩
     */
    public SalonView(Context context, SalonViewBinding dataBinding,RequestListener requestListener) {
        super(context, dataBinding);
        this.context = context;
        this.salonBinding = dataBinding;
        this.requestListener = requestListener;
//        onClick = new OnClick();
       // salonBinding.setOnClick(onClick);
        init();
    }

    public SalonView(Context context, SalonViewBinding dataBinding) {
        super(context, dataBinding);
        this.context = context;
        this.salonBinding = dataBinding;
//        this.requestListener = requestListener;
  //      onClick = new OnClick();
  //      salonBinding.setOnClick(onClick);
        init();
    }


    @Override
    public void init() {
        salonBinding.imNeonOn.setAlpha(0f); //처음에는 neon on 투명(안보이게)
        salonBinding.llNeonOn.setAlpha(0f); //처음에는 neon on background 투명

        isCoaster = false; // 처음에는 다른 화면으로 넘어갈 수 없는 상태

        enterPostInformationDialog = new EnterPostInformationDialog(context); // 정보 입력하는 dialog
        enterPostInformationDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        enterPostInformationDialog.getWindow().setDimAmount(0.7f);
        DialogDismissListener(); // dialog 가 닫혔을 때의 이벤트

        //showSelectContent(); //선택한 콘텐츠가 있으면 변경
    }



    public class OnClick{
//        public void animClick(View view){
//            if(isCoaster == true){
//                // 코스터가 올라와서 애니메이션까지 다 보고 클릭하면 선택한 콘텐츠가 있는지 확인
////                SharedPreferences likeSP = context.getSharedPreferences(sharedKey, 0);
////                String value = likeSP.getString("usrContent","없음");
////                if(value.equals("없음")){ // 없으면 다른 화면으로 안넘겨줌
////                    Toast.makeText(context,"콘텐츠를 선택해 주세요",Toast.LENGTH_LONG).show();
////                }else{ // 콘텐츠가 있는 경우 내 정보를 입력할 수 있는 창으로 넘김
////
////                }
//
//                enterPostInformationDialog.show();
//
//            }else{
////                startBlinkAnimation();
////                changeOnView();
//                /**TODO 코스터가 연결되었으면 OnClick 에서 다른화면으로 넘어갈 수 있는 상태를 결정하는게 아니라
//                 * TODO 블루투스가 인지 되었는지 확인 후에 IScoaster 를 true 로 바꿔야한다 */
//              //  isCoaster = true; //이제 다른 화면으로 넘어갈 수 있는 상태 > 코스터 올라왔음
//            }
//        }
//        public void scanBluetooth(View view){
//          //  requestListener.doScan();
//        }


    }

    public void DialogDismissListener(){
        enterPostInformationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                // 다이얼로그가 닫히면 postView 로 넘어간다
//                requestListener.showSalonViewDo();
                requestListener.showPostView();
                requestListener.setUsrInfo();
            }
        });
    }


    /**Salon view 에서 postView 에 있는 메소드를 호출해야 할 경우*/
    public interface RequestListener{
//        void doScan();
//        void showSalonViewDo();

        void showPostView();
        void setUsrInfo();
    }

}
