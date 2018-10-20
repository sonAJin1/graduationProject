package com.example.sonaj.graduationproject.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sonaj.graduationproject.Adapter.WritePostAdapter;
import com.example.sonaj.graduationproject.CharactorMake;
import com.example.sonaj.graduationproject.R;
import com.example.sonaj.graduationproject.Util.AppSettings;
import com.example.sonaj.graduationproject.Util.Constants;
import com.example.sonaj.graduationproject.Util.Logs;
import com.example.sonaj.graduationproject.Util.MultiViewActivity;
import com.example.sonaj.graduationproject.Util.RecycleUtils;
import com.example.sonaj.graduationproject.View.ContentsView;
import com.example.sonaj.graduationproject.View.LikeView;
import com.example.sonaj.graduationproject.View.PostView;
import com.example.sonaj.graduationproject.View.SalonView;
import com.example.sonaj.graduationproject.databinding.MainBinding;
import com.example.sonaj.graduationproject.service.BTCTemplateService;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends MultiViewActivity implements PostView.RequestListener{

    // Debugging
    private static final String TAG = "RetroWatchActivity";

    MainBinding binding;
    BottomNavigationView navigation;
    OnClick onClick;

    // Context, System
    private Context mContext;
    private BTCTemplateService mService;
    private ActivityHandler mActivityHandler;

    // Refresh timer
    private Timer mRefreshTimer = null;

    // UI stuff
    private FragmentManager mFragmentManager;

    //View list
    //SalonView salonView;
    ContentsView contentsView;
    LikeView likeView;
    PostView postView;

    // View 의 위치를 나타내는 값
    final int POSITION_SALON_View = 0;
    final int POSITION_CONTENTS_VIEW = 1;
    final int POSITION_MARKET_VIEW = 2;

    //bluetooth 로 gauge 조절
    int currentWeight = 80; // 처음엔 95 이었다가 들어오는 값으로 빠졌다가 new 가 들어오면 다시 95로 회복 > 100 이 아닌 이유 >  처음에도 찰랑거리는 애니메이션 보여주기 위해서
    int drunkDegree = 0; // new 가 몇번 들어왔는지 최대 3 (취함 정도 표시)
    int newWeight;
    int oldWeight = 0;


    /**sharedPreference */
    static String sharedKey = "usrInfo";
    int usrDrink;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    onClick.showSalonView();
                    return true;
                case R.id.navigation_dashboard:
                    onClick.showContents();
                    return true;
                case R.id.navigation_notifications:
                    onClick.showMarket();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //----- System, Context
        mContext = this;	//.getApplicationContext();
        mActivityHandler = new ActivityHandler();
        AppSettings.initializeAppSettings(mContext);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the primary sections of the app.
        mFragmentManager = getSupportFragmentManager();

        init();

        // 처음 bluetooth 상태 표시
        setBluetoothStatus(0);

        //맨처음 취한 정도
        drunkDegree = 0;
        oldWeight = 0;
        currentWeight = 100;
        //showDrunk(drunkDegree);


        // Do data initialization after service started and binded
        doStartService();

        // 맨 처음 화면은 살롱 화면
        onClick.showSalonView();

        Log.e("현재 포커스", String.valueOf(getCurrentFocus()));
    }

    public void setBluetoothStatus(int status){
        switch (status){
            case 0: //초기화중
                binding.appBarContent.viewPost.icSalonView.bluetoothStatus.setImageDrawable(mContext.getDrawable(R.drawable.ic_bluetooth_invisible));
               // binding.appBarContent.viewPost.imBluetoothStatus.setBackgroundColor(mContext.getColor(R.color.bluetoothInvisible));
                break;
            case 1: // 대기중
                binding.appBarContent.viewPost.icSalonView.bluetoothStatus.setImageDrawable(mContext.getDrawable(R.drawable.ic_bluetooth_invisible));
              //  binding.appBarContent.viewPost.imBluetoothStatus.setBackgroundColor(mContext.getColor(R.color.bluetoothInvisible));
                break;
            case 2: //연결중
                binding.appBarContent.viewPost.icSalonView.bluetoothStatus.setImageDrawable(mContext.getDrawable(R.drawable.ic_bluetooth_away));
               // binding.appBarContent.viewPost.imBluetoothStatus.setBackgroundColor(mContext.getColor(R.color.bluetoothAway));
                break;
            case 3:  //연결됨
                binding.appBarContent.viewPost.icSalonView.bluetoothStatus.setImageDrawable(mContext.getDrawable(R.drawable.ic_bluetooth_online));
               // binding.appBarContent.viewPost.imBluetoothStatus.setBackgroundColor(mContext.getColor(R.color.bluetoothOnline));
                break;
            case 4: //연결오류
                binding.appBarContent.viewPost.icSalonView.bluetoothStatus.setImageDrawable(mContext.getDrawable(R.drawable.ic_bluetooth_error));
              //  binding.appBarContent.viewPost.imBluetoothStatus.setBackgroundColor(mContext.getColor(R.color.bluetoothError));
                break;
        }
    }

    /**
     * UI 화면 초기화
     */
    @Override
    public void init() {

        //사용자들이 선택해서 보여질 VIEW
        contentsView = new ContentsView(this, binding.appBarContent.viewContents);
        likeView = new LikeView(this, binding.appBarContent.viewLike);
        postView = new PostView(this,binding.appBarContent.viewPost,this);

        //salonView에서 넘어가서 게시글 보여질 때 볼 화면


        //네비게이션 바
        navigation = binding.navigation;
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //선택에 의해 보여질 화면들 관리를 위해 리스트에 추가
        initViewList();
        addView(postView);
        addView(contentsView);
        addView(likeView);

        //뷰 클릭 메소드 xml 과 연결
        onClick = new OnClick();

    }

    public class OnClick {

        // 살롱 화면 보여주기
        public void showSalonView() {
            setView(POSITION_SALON_View);
            postView.setPostView(); // 화면을 보여줄 때 서버에서 데이터 가져오기
            postView.setUsrInfo(); // 누를 때 마다 사용자 info 갱신 > 오늘 즐길 콘텐츠가 달라 질 수 있기 때문

        }

        // 콘텐츠 추천 화면 보여주기
        public void showContents() {
            if(currentViewPosition!=POSITION_CONTENTS_VIEW) {
                setView(POSITION_CONTENTS_VIEW);
                contentsView.setContentView();
            }
        }

        // 마켓 화면 보여주기
        public void showMarket(){
            if(currentViewPosition!=POSITION_MARKET_VIEW) {
                setView(POSITION_MARKET_VIEW);
                likeView.setLikeView();
            }
        }
    }



    /**
     *
     * Bluetooth setting
     *
     * */
    @Override
    public synchronized void onStart() {
        super.onStart();
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        // Stop the timer
        if(mRefreshTimer != null) {
            mRefreshTimer.cancel();
            mRefreshTimer = null;
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finalizeActivity();
    }

    @Override
    public void onLowMemory (){
        super.onLowMemory();
        // onDestroy is not always called when applications are finished by Android system.
        finalizeActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan:
                // Launch the DeviceListActivity to see devices and do scan
                doScan();
                return true;
            case R.id.action_discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();		// TODO: Disable this line to run below code
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        // This prevents reload after configuration changes
        super.onConfigurationChanged(newConfig);
    }


    /*****************************************************
     *	Private methods
     ******************************************************/

    /**
     * Service connection
     */
    private ServiceConnection mServiceConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, "Activity - Service connected");

            mService = ((BTCTemplateService.ServiceBinder) binder).getService();

            // Activity couldn't work with mService until connections are made
            // So initialize parameters and settings here. Do not initialize while running onCreate()
            initialize();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    /**
     * Start service if it's not running
     */
    private void doStartService() {
        Log.d(TAG, "# Activity - doStartService()");
        startService(new Intent(this, BTCTemplateService.class));
        bindService(new Intent(this, BTCTemplateService.class), mServiceConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * Stop the service
     */
    private void doStopService() {
        Log.d(TAG, "# Activity - doStopService()");
        mService.finalizeService();
        stopService(new Intent(this, BTCTemplateService.class));
    }

    /**
     * Initialization / Finalization
     */
    private void initialize() {
        Logs.d(TAG, "# Activity - initialize()");
        mService.setupService(mActivityHandler);

        // If BT is not on, request that it be enabled.
        // RetroWatchService.setupBT() will then be called during onActivityResult
        if(!mService.isBluetoothEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }

        // Load activity reports and display
        if(mRefreshTimer != null) {
            mRefreshTimer.cancel();
        }

        // Use below timer if you want scheduled job
        mRefreshTimer = new Timer();
        mRefreshTimer.schedule(new RefreshTimerTask(), 5*1000);
    }

    private void finalizeActivity() {
        Logs.d(TAG, "# Activity - finalizeActivity()");

        if(!AppSettings.getBgService()) {
            doStopService();
        } else {
        }
        // Clean used resources
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
    }

    /**
     * Launch the DeviceListActivity to see devices and do scan
     */
    public void doScan() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CONNECT_DEVICE);
    }

    /**
     * Ensure this device is discoverable by others
     */
    private void ensureDiscoverable() {
        if (mService.getBluetoothScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
    }


    /*****************************************************
     *	Public classes
     ******************************************************/

    /**
     * Receives result from external activity
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logs.d(TAG, "onActivityResult " + resultCode);

        switch(requestCode) {
            case Constants.REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Attempt to connect to the device
                    if(address != null && mService != null)
                        mService.connectDevice(address);
                }
                break;

            case Constants.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a BT session
                    mService.setupBT();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Logs.e(TAG, "BT is not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                }
                break;
        }	// End of switch(requestCode)
    }



    /*****************************************************
     *	Handler, Callback, Sub-classes
     ******************************************************/

    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what) {
                // Receives BT state messages from service
                // and updates BT state UI
                case Constants.MESSAGE_BT_STATE_INITIALIZED:
                    Log.e("Bluetooth", String.valueOf(getResources().getString(R.string.bt_title) + ": " +
                              getResources().getString(R.string.bt_state_init)));

                    //salon view 에 있는 bluetooth 상태 알 수 있는 이미지 변경
                    setBluetoothStatus(0);
                    break;
                case Constants.MESSAGE_BT_STATE_LISTENING:
                    Log.e("Bluetooth", String.valueOf(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_wait)));

                    //salon view 에 있는 bluetooth 상태 알 수 있는 이미지 변경
                    setBluetoothStatus(1);
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTING:
                    Log.e("Bluetooth", String.valueOf(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_connect)));

                    //salon view 에 있는 bluetooth 상태 알 수 있는 이미지 변경
                    setBluetoothStatus(2);
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTED:
                    if(mService != null) {
                        String deviceName = mService.getDeviceName();
                        if(deviceName != null) {
                            Log.e("Bluetooth", String.valueOf(getResources().getString(R.string.bt_title) + ": " +
                                    getResources().getString(R.string.bt_state_connected) + " " + deviceName));

                            //salon view 에 있는 bluetooth 상태 알 수 있는 이미지 변경
                            setBluetoothStatus(3);
                        }
                    }
                    break;
                case Constants.MESSAGE_BT_STATE_ERROR:
                    Log.e("Bluetooth", String.valueOf(R.string.bt_state_error));
                    //salon view 에 있는 bluetooth 상태 알 수 있는 이미지 변경
                    setBluetoothStatus(4);
                    break;

                // BT Command status
                case Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED:
                    Log.e("Bluetooth", String.valueOf(R.string.bt_cmd_sending_error));
                    //salon view 에 있는 bluetooth 상태 알 수 있는 이미지 변경
                    setBluetoothStatus(4);
                    break;

                ///////////////////////////////////////////////
                // When there's incoming packets on bluetooth
                // do the UI works like below
                ///////////////////////////////////////////////
                case Constants.MESSAGE_READ_CHAT_DATA:
                    if(msg.obj != null) {
                        Log.e("message", String.valueOf(msg.obj));
                        String bMsg = String.valueOf(msg.obj);
                        bMsg = bMsg.replaceAll("(\r\n|\r|\n|\n\r)", ""); // 줄바꿈 제거
                        bMsg = bMsg.replaceAll(" ",""); // 공백제거

                        if (bMsg.equals("s")) {
                            if(oldWeight==0){ // 처음 들어오는 값이라면
                                postView.startBlinkAnimation();
                                postView.changeOnView();
                            }

                        }else if(isStringDouble(bMsg)){ // 숫자로 바꿀 수 있는 값인지 확인하고
                            newWeight = Integer.parseInt(bMsg); // 지금 들어 온 값은 new
                            if(oldWeight>0){
                                if(newWeight>oldWeight+5){ // 새로운 값이 더 크다면 새잔
                                    currentWeight = 80; //new (새잔)을 받으면 게이지는 95로 돌아감
                                    Log.e("currentWeight", String.valueOf(currentWeight));
                                    if(drunkDegree<3){ // 취한 정도 3보다 작으면 더해주기 (최대가 3)
                                        drunkDegree++;
                                    }
                                    showDrunk(drunkDegree); // 취한 정도 보여주기
                                }else{
                                    if(currentWeight>0){
                                        currentWeight -= Integer.parseInt(bMsg); // 값이 들어오면 현재 값에서 그만큼 빼주기
                                        Log.e("currentWeight", String.valueOf(currentWeight));
                                    }
//                                    if(currentWeight<=0){
//                                        currentWeight = 0;
//                                        if(drunkDegree<3){ // 취한 정도 3보다 작으면 더해주기 (최대가 3)
//                                            drunkDegree++;
//                                        }
//                                        showDrunk(drunkDegree); // 취한 정도 보여주기
//                                    }
//                                    if(currentWeight<10){ //10보다 작으면 다 먹은걸로 간주
//                                        if(drunkDegree<3){ // 취한 정도 3보다 작으면 더해주기 (최대가 3)
//                                            drunkDegree++;
//                                        }
//                                        showDrunk(drunkDegree); // 취한 정도 보여주기
//                                    }

                                }
                            }
                            binding.appBarContent.viewPost.imDrinkGauge.setProgressValue(currentWeight); // 게이지에 반영
                            oldWeight = newWeight;
                        }
                    }
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }	// End of class ActivityHandler

    public boolean isStringDouble(String s){ // string 이 숫자인지 아닌지 판단할 때 사용
        try {
            Double.parseDouble(s);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    //취함 정도를 보여주는 요소들 -게이지 발그레, 옆에 쌓인 술잔
    public void showDrunk(int drunk){

        SharedPreferences usrSP = mContext.getSharedPreferences(sharedKey, 0);
        usrDrink = usrSP.getInt("usrDrink",0);

        switch (drunk){
            case 0: // 아예 안취함
                binding.appBarContent.viewPost.imDrunk01.setVisibility(View.GONE);
                binding.appBarContent.viewPost.imDrunk02.setVisibility(View.GONE);
                binding.appBarContent.viewPost.imDrunk03.setVisibility(View.GONE);
                CharactorMake.setCheekDegree(drunk,usrDrink,binding.appBarContent.viewPost.imDrunkCheekBeer);
                break;
            case 1: // 취함정도 1
                binding.appBarContent.viewPost.imDrunk01.setVisibility(View.VISIBLE);
                binding.appBarContent.viewPost.imDrunk02.setVisibility(View.GONE);
                binding.appBarContent.viewPost.imDrunk03.setVisibility(View.GONE);
                CharactorMake.setCheekDegree(drunk,usrDrink,binding.appBarContent.viewPost.imDrunkCheekBeer);
                break;
            case 2: // 취함정도 2
                binding.appBarContent.viewPost.imDrunk01.setVisibility(View.VISIBLE);
                binding.appBarContent.viewPost.imDrunk02.setVisibility(View.VISIBLE);
                binding.appBarContent.viewPost.imDrunk03.setVisibility(View.GONE);
                CharactorMake.setCheekDegree(drunk,usrDrink,binding.appBarContent.viewPost.imDrunkCheekBeer);
                break;
            case 3: // 취함정도 3
                binding.appBarContent.viewPost.imDrunk01.setVisibility(View.VISIBLE);
                binding.appBarContent.viewPost.imDrunk02.setVisibility(View.VISIBLE);
                binding.appBarContent.viewPost.imDrunk03.setVisibility(View.VISIBLE);
                CharactorMake.setCheekDegree(drunk,usrDrink,binding.appBarContent.viewPost.imDrunkCheekBeer);
                break;
        }
    }

    public int getDrunkDegree(){
        return drunkDegree;
    }

    /**
     * Auto-refresh Timer
     */
    private class RefreshTimerTask extends TimerTask {
        public RefreshTimerTask() {}

        public void run() {
            mActivityHandler.post(new Runnable() {
                public void run() {
                    // TODO:
                    mRefreshTimer = null;
                }
            });
        }
    }

}
