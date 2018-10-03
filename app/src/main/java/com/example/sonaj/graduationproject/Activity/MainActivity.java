package com.example.sonaj.graduationproject.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
       // salonView.setBluetoothStatus(0);
        setBluetoothStatus(0);

        // Do data initialization after service started and binded
        doStartService();

        // 맨 처음 화면은 살롱 화면
        onClick.showSalonView();

        Log.e("create","");
    }

    public void setBluetoothStatus(int status){
        switch (status){
            case 0: //초기화중
                binding.appBarContent.viewPost.icSalonView.bluetoothStatus.setImageDrawable(mContext.getDrawable(R.drawable.ic_bluetooth_invisible));
                break;
            case 1: // 대기중
                binding.appBarContent.viewPost.icSalonView.bluetoothStatus.setImageDrawable(mContext.getDrawable(R.drawable.ic_bluetooth_invisible));
                break;
            case 2: //연결중
                binding.appBarContent.viewPost.icSalonView.bluetoothStatus.setImageDrawable(mContext.getDrawable(R.drawable.ic_bluetooth_away));
                break;
            case 3:  //연결됨
                binding.appBarContent.viewPost.icSalonView.bluetoothStatus.setImageDrawable(mContext.getDrawable(R.drawable.ic_bluetooth_online));
                break;
            case 4: //연결오류
                binding.appBarContent.viewPost.icSalonView.bluetoothStatus.setImageDrawable(mContext.getDrawable(R.drawable.ic_bluetooth_error));
                break;
        }
    }

    /**
     * UI 화면 초기화
     */
    @Override
    public void init() {

        //사용자들이 선택해서 보여질 VIEW
       // salonView = new SalonView(this, binding.appBarContent.viewSalon);
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

    // 살롱 화면 보여주기 (salon View 에서 가져다 쓰기 위해 onClick 밖으로 꺼내서 메소드 형태로 둠)
//    public void showSalonViewDo() {
//        if(salonView.getIsCoaster()){ //코스터가 올라갔다고 인지가 된 상황이면
//            removeView(0); //salon view 없애고
//            addView(postView,0); //post view 보이게
//            salonView.getView().setVisibility(View.GONE);
//        }
//        setView(POSITION_SALON_View);
//        postView.setPostView(); // 화면을 보여줄 때 서버에서 데이터 가져오기
//        postView.setUsrInfo(); // 누를 때 마다 사용자 info 갱신 > 오늘 즐길 콘텐츠가 달라 질 수 있기 때문
//    }



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
                    //salonView.setBluetoothStatus(0);
                    setBluetoothStatus(0);
                    break;
                case Constants.MESSAGE_BT_STATE_LISTENING:
                    Log.e("Bluetooth", String.valueOf(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_wait)));

                    //salon view 에 있는 bluetooth 상태 알 수 있는 이미지 변경
                 //   salonView.setBluetoothStatus(1);
                    setBluetoothStatus(1);
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTING:
                    Log.e("Bluetooth", String.valueOf(getResources().getString(R.string.bt_title) + ": " +
                            getResources().getString(R.string.bt_state_connect)));

                    //salon view 에 있는 bluetooth 상태 알 수 있는 이미지 변경
                    //salonView.setBluetoothStatus(2);
                    setBluetoothStatus(2);
                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTED:
                    if(mService != null) {
                        String deviceName = mService.getDeviceName();
                        if(deviceName != null) {
                            Log.e("Bluetooth", String.valueOf(getResources().getString(R.string.bt_title) + ": " +
                                    getResources().getString(R.string.bt_state_connected) + " " + deviceName));

                            //salon view 에 있는 bluetooth 상태 알 수 있는 이미지 변경
                           // salonView.setBluetoothStatus(3);
                            setBluetoothStatus(3);
                        }
                    }
                    break;
                case Constants.MESSAGE_BT_STATE_ERROR:
                    Log.e("Bluetooth", String.valueOf(R.string.bt_state_error));
                    //salon view 에 있는 bluetooth 상태 알 수 있는 이미지 변경
                    //salonView.setBluetoothStatus(4);
                    setBluetoothStatus(4);
                    break;

                // BT Command status
                case Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED:
                    Log.e("Bluetooth", String.valueOf(R.string.bt_cmd_sending_error));
                    //salon view 에 있는 bluetooth 상태 알 수 있는 이미지 변경
                    //salonView.setBluetoothStatus(4);
                    setBluetoothStatus(4);
                    break;

                ///////////////////////////////////////////////
                // When there's incoming packets on bluetooth
                // do the UI works like below
                ///////////////////////////////////////////////
                case Constants.MESSAGE_READ_CHAT_DATA:
                    if(msg.obj != null) {
//					ExampleFragment frg = (ExampleFragment) mSectionsPagerAdapter.getItem(FragmentAdapter.FRAGMENT_POS_EXAMPLE);
//					frg.showMessage((String)msg.obj);
                    }
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }	// End of class ActivityHandler

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
