package com.example.sonaj.graduationproject;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.sonaj.graduationproject.databinding.DialogEnterPostInformationBinding;

import carbon.widget.EditText;

public class EnterPostInformationDialog extends Dialog{
    Context mContext;
    DialogEnterPostInformationBinding binding;
    OnClick onClick;

    //radioButton
    boolean drinkButtonClicked = false;
    int emotionPosition;
    int drinkPosition;

    // Usr 관련 데이터 저장해두는 sharedPreference
    static String sharedKey = "usrInfo";

    public EnterPostInformationDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding = DialogEnterPostInformationBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        onClick = new OnClick();
        binding.setOnClick(onClick);


        setTextViewListenter(); // editText 글자 수 변하는거 감지
        drinkChangeListener();
        setEmotion(); // radio button java 에서 src 로 표정 입히기
        setEmotionChange();

    }

    public void setEmotionChange(){

        binding.emotionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(drinkButtonClicked){

                  emotionPosition = binding.emotionGroup.indexOfChild(findViewById(binding.emotionGroup.getCheckedRadioButtonId())); // 선택된 emotion 의 position 값

                    switch (emotionPosition){
                        case 0:
                            CharactorMake.setDrinkBackgroundColor(binding.drinkBtnGroup.indexOfChild(findViewById(binding.drinkBtnGroup.getCheckedRadioButtonId())),binding.emotion00);
                            binding.emotion01.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion02.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion03.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion04.setBackgroundResource(R.drawable.emotion_un_select_background);
                            break;
                        case 1:
                            CharactorMake.setDrinkBackgroundColor(binding.drinkBtnGroup.indexOfChild(findViewById(binding.drinkBtnGroup.getCheckedRadioButtonId())),binding.emotion01);
                            binding.emotion00.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion02.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion03.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion04.setBackgroundResource(R.drawable.emotion_un_select_background);
                            break;
                        case 2:
                            CharactorMake.setDrinkBackgroundColor(binding.drinkBtnGroup.indexOfChild(findViewById(binding.drinkBtnGroup.getCheckedRadioButtonId())),binding.emotion02);
                            binding.emotion00.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion01.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion03.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion04.setBackgroundResource(R.drawable.emotion_un_select_background);
                            break;
                        case 3:
                            CharactorMake.setDrinkBackgroundColor(binding.drinkBtnGroup.indexOfChild(findViewById(binding.drinkBtnGroup.getCheckedRadioButtonId())),binding.emotion03);
                            binding.emotion00.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion01.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion02.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion04.setBackgroundResource(R.drawable.emotion_un_select_background);
                            break;
                        case 4:
                            CharactorMake.setDrinkBackgroundColor(binding.drinkBtnGroup.indexOfChild(findViewById(binding.drinkBtnGroup.getCheckedRadioButtonId())),binding.emotion04);
                            binding.emotion00.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion01.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion02.setBackgroundResource(R.drawable.emotion_un_select_background);
                            binding.emotion03.setBackgroundResource(R.drawable.emotion_un_select_background);
                            break;
                    }

                }
            }
        });

    }

    public void setEmotion(){
       // binding.emotion00\

    }

    public void drinkChangeListener(){
        binding.drinkBtnGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                drinkButtonClicked = true;
                drinkPosition = binding.drinkBtnGroup.indexOfChild(findViewById(binding.drinkBtnGroup.getCheckedRadioButtonId()));
                emotionPosition = binding.emotionGroup.indexOfChild(findViewById(binding.emotionGroup.getCheckedRadioButtonId())); // 선택된 emotion 의 position 값
                switch (emotionPosition){
                    case 0:
                        CharactorMake.setDrinkBackgroundColor(drinkPosition,binding.emotion00);
                        break;
                    case 1:
                        CharactorMake.setDrinkBackgroundColor(drinkPosition,binding.emotion01);
                        break;
                    case 2:
                        CharactorMake.setDrinkBackgroundColor(drinkPosition,binding.emotion02);
                        break;
                    case 3:
                        CharactorMake.setDrinkBackgroundColor(drinkPosition,binding.emotion03);
                        break;
                    case 4:
                        CharactorMake.setDrinkBackgroundColor(drinkPosition,binding.emotion04);
                        break;

                }

            }
        });
    }

    // editText 글자 수 변하는거 감지
    public void setTextViewListenter(){
        binding.etUsrNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.btnOk.setVisibility(View.GONE);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length()>0){ // 글자가 입력되는 순간 완료 버튼이 나온다
                    binding.btnOk.setVisibility(View.VISIBLE);
                }else{
                    binding.btnOk.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    public class OnClick{
        public void clickOkBtn(View view){
            String usrNickname = String.valueOf(binding.etUsrNickname.getText()); // edit text 에 쓴 유저의 닉네임 가져오기

            int drinkItem = binding.drinkBtnGroup.indexOfChild(findViewById(binding.drinkBtnGroup.getCheckedRadioButtonId())); // 선택된 drink 의 position 값
            int emotionItem = binding.emotionGroup.indexOfChild(findViewById(binding.emotionGroup.getCheckedRadioButtonId())); // 선택된 drink 의 position 값

            if(drinkItem==-1 || emotionItem==-1){ // 주류랑 감정이 선택되지 않은 경우 넘어가지 않는다.
                Toast.makeText(mContext,"선택되지 않은 목록이 있습니다",Toast.LENGTH_LONG).show();
            }else{
                // sharedPreference 에 저장
                SharedPreferences usrSP = mContext.getSharedPreferences(sharedKey,0);
                SharedPreferences.Editor editor = usrSP.edit();
                editor.putString("usrNickname",usrNickname); // usrNickname 저장
                editor.putInt("usrDrink",drinkItem); // usrDrink 저장
                editor.putInt("usrEmotion",emotionItem); // usrEmotion 저장
                editor.commit();

                //키보드 내리는 부분
                InputMethodManager immhide = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


                // dialog 닫기
                dismiss();
            }
        }

    }
}
