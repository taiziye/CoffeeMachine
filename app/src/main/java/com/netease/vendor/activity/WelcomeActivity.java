package com.netease.vendor.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.vendor.R;
import com.netease.vendor.application.AppConfig;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.common.action.TActivity;
import com.netease.vendor.instructions.CoffeeMachineInstruction;
import com.netease.vendor.instructions.CoffeeMachineResultProcess;
import com.netease.vendor.instructions.SetTempInstruction;
import com.netease.vendor.loader.ImageLoaderTool;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.StatusChangeNotify;
import com.netease.vendor.service.bean.action.GetAdvPicsInfo;
import com.netease.vendor.service.bean.action.GetDosingListInfo;
import com.netease.vendor.service.bean.action.GetMachineConfigInfo;
import com.netease.vendor.service.bean.action.MachineStatusReportInfo;
import com.netease.vendor.service.bean.action.SyncStockInfo;
import com.netease.vendor.service.bean.result.GeneralActionResult;
import com.netease.vendor.service.bean.result.GetAdvPicsResult;
import com.netease.vendor.service.bean.result.GetDosingResult;
import com.netease.vendor.service.bean.result.GetMachineConfigResult;
import com.netease.vendor.service.bean.result.UpdateStockResult;
import com.netease.vendor.service.domain.Ancestor;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.protocol.MachineMaterialMap;
import com.netease.vendor.service.protocol.MachineStatusCode;
import com.netease.vendor.ui.ProgressDlgHelper;
import com.netease.vendor.util.ScreenUtil;
import com.netease.vendor.util.SerialPortDataWritter;
import com.netease.vendor.util.SharePrefConfig;
import com.netease.vendor.util.TimeUtil;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class WelcomeActivity extends TActivity implements OnClickListener {

    private static final String TAG = "WelcomePage";

    private Context mContext;

    private static final int THRESHOLD_SYSC_STOCK = 60*60*1000;    //100 mins
    private static final int THRESHOLD_ADV_UPDATE = 30*60*1000;    //30 mins

    private AtomicBoolean mWashingStatus = new AtomicBoolean(false);

    private ViewFlipper mFlipper;
    private LinearLayout mNotifyBar;
	private TextView mDescriLabel;
	private RelativeLayout mContentArea;
	private ImageView mBuyCoffee;
    private ImageView mMusicController;

    private Timer mWashTimer;
    private int mWashQueryCount = 0;
    private Timer mSetTempTimer;
    private int mSetTempQueryCount = 0;
    private Timer disableTimer;

    // background music
    private MediaPlayer bmMediaPlayer;

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_layout);
        mContext = this;

		initViews();
        initFlipper();
		initStatus();
        getMachineConfig();
        getCoffeeAdvs();

        startPlayBgMusic();
	}

    private void initViews(){
        // network status bar
        mNotifyBar = (LinearLayout) findViewById(R.id.status_notify_bar);
        mDescriLabel = (TextView) findViewById(R.id.status_desc_label);

        mContentArea = (RelativeLayout) findViewById(R.id.welcome_page_parent);
        mContentArea.setOnClickListener(this);
        mBuyCoffee = (ImageView) findViewById(R.id.welcome_buy_coffee_btn);
        mBuyCoffee.setOnClickListener(this);

        mMusicController = (ImageView) findViewById(R.id.welcome_music_image);
        //mMusicController.setBackgroundResource(R.drawable.welcome_music_controller_selector);
        mMusicController.setOnClickListener(this);
    }

    private void initFlipper(){
        mFlipper = (ViewFlipper) findViewById(R.id.welcome_flipper);
        mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
    }

    private void initStatus(){
        int status = U.getUserStatus(this);
        updateStatus(status);
    }

    private void updateStatus(int status) {
        String descTips = "";
        if (status == ITranCode.STATUS_NO_NETWORK) {
            mNotifyBar.setVisibility(View.VISIBLE);
            descTips = getString(R.string.network_is_not_available);
        } else if (status == ITranCode.STATUS_CONNECT_FAILED) {
            mNotifyBar.setVisibility(View.VISIBLE);
            descTips = getString(R.string.network_server_is_not_available);
        } else if (status == ITranCode.STATUS_FORBIDDEN) {
            mNotifyBar.setVisibility(View.VISIBLE);
            descTips = getString(R.string.network_forbidden_login);
        } else {
            mNotifyBar.setVisibility(View.GONE);
            if (status == ITranCode.STATUS_LOGGING) {
                descTips = getString(R.string.network_connecting);
            }
        }

        mDescriLabel.setText(descTips);
    }

    private void getMachineConfig(){
        if(!AppConfig.isSerialportSysnc())
            return;
        ProgressDlgHelper.showProgress(this, getString(R.string.welcome_get_machine_config));
        GetMachineConfigInfo info = new GetMachineConfigInfo();
        info.setUid(U.getMyVendorNum());
        execute(info.toRemote());

        startGetConfigDisableTimer();
    }

    private void startGetConfigDisableTimer() {

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UI_MSG_WHAT_DISABLE_TIMER;
                timerHandler.sendMessage(message);
            }
        };

        disableTimer = new Timer();
        disableTimer.schedule(timerTask, 30000);
    }

    public final static int UI_MSG_WHAT_DISABLE_TIMER = 1;

    static class SafeHandler extends Handler {
        WeakReference<WelcomeActivity> theActivity;

        public SafeHandler(WelcomeActivity activity) {
            this.theActivity = new WeakReference<WelcomeActivity>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case UI_MSG_WHAT_DISABLE_TIMER:
                    WelcomeActivity activity = theActivity.get();
                    if (activity != null) {
                        activity.onGetConfigTimeout();
                    }
                    break;
            }
        }
    }

    private SafeHandler timerHandler = new SafeHandler(this);

    private void onGetConfigTimeout(){
        ProgressDlgHelper.closeProgress();
        stopGetConfigTimer();

        sendSetCustomTempInstruction("105", "103");
    }

    private void stopGetConfigTimer(){
        if (disableTimer != null) {
            disableTimer.cancel();
            disableTimer = null;
        }
    }

    private void sendSetCustomTempInstruction(final String workTemp, final String keepTemp){
        ProgressDlgHelper.showProgress(this, getString(R.string.welcome_set_temperature));
        //开始每隔2秒发送指令，设置咖啡机温度
        mSetTempTimer = new Timer();
        mSetTempTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mSetTempQueryCount >= 30) {
                    //查询超过30次，还没有成功设置温度
                    WelcomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressDlgHelper.closeProgress();
                            ToastUtil.showToast(WelcomeActivity.this, getString(R.string.welcome_set_temperature_timeout));
                        }
                    });

                    if (mSetTempTimer != null) {
                        mSetTempTimer.cancel();
                    }
                    return;
                }

                double temp = Double.parseDouble(workTemp);
                double constantTemp = Double.parseDouble(keepTemp);
                SetTempInstruction instruction = new SetTempInstruction((int) temp, (int) constantTemp);
                SerialPortDataWritter.writeData(instruction.getSetTempOrder());
                mSetTempQueryCount++;
            }
        }, 0, 2000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("DEBUG", "WelcomeActivity->onNewIntent");
        syncStockFromServer();
        syncAdvFromServer();
        // clear cart cache
        MyApplication.Instance().clearCartPay();
    }

    private void syncStockFromServer(){
        if(TimeUtil.getNow_millisecond() -
                MyApplication.Instance().getLastSyncStockTime() >= THRESHOLD_SYSC_STOCK){
            getCoffeeDosingList();
        }
    }

    private void getCoffeeAdvs(){
        LogUtil.e("DEBUG", "WelcomeActivity->getCoffeeAdvs");
        // get adv local first
        List<String> advPicUrlsList = SharePrefConfig.getInstance().getAdvImgs();
        if(advPicUrlsList != null && advPicUrlsList.size() > 0){
            for(int i = 0; i < advPicUrlsList.size(); i++){
                String advPicUrl = advPicUrlsList.get(i);
                mFlipper.addView(addImageByURL(advPicUrl));
            }

            mFlipper.setAutoStart(true);
            mFlipper.setFlipInterval(10 * 1000);
            mFlipper.startFlipping();
        }
        // check update adv from server
        syncAdvFromServer();
    }

    private void syncAdvFromServer(){
        long time = SharePrefConfig.getInstance().getAdvUpdateTime();
        if(TimeUtil.getNow_millisecond() - time >= THRESHOLD_ADV_UPDATE){
            GetAdvPicsInfo info = new GetAdvPicsInfo();
            info.setUid(U.getMyVendorNum());
            execute(info.toRemote());
        }
    }

    private void getCoffeeDosingList(){
        GetDosingListInfo info = new GetDosingListInfo();
        info.setUid(U.getMyVendorNum());
        info.setAuto(true);
        execute(info.toRemote());
    }

    private void showDefaultAdvPics(){
        int childCount = mFlipper.getChildCount();
        LogUtil.e("DEBUG", "childCount = " + childCount);
        if(childCount <= 0){
            mFlipper.addView(addImageById(R.drawable.welcome_1));
            mFlipper.addView(addImageById(R.drawable.welcome_2));
            mFlipper.addView(addImageById(R.drawable.welcome_3));
            mFlipper.setAutoStart(true);
            mFlipper.setFlipInterval(10 * 1000);
            mFlipper.startFlipping();
        }
    }

    @Override
    public void onReceive(Remote remote) {
        // system action
        if (remote.getWhat() == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                updateStatus(notify.getStatus());
            }
        }
        // coffee action
        if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_DOSING_LIST) {
                GetDosingResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.isAuto()) {
                    if (result.getResCode() == 200) {
                        syncStock(result.getDosings());
                    } else {
                        ToastUtil.showToast(this, R.string.welcome_get_dosing_list_fail);
                    }
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_STOCK_UPDATE) {
                UpdateStockResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result != null && result.isAuto()) {
                    if (result.getResCode() == 200) {
                        ToastUtil.showToast(this, R.string.welcome_sync_stock_success);
                        MyApplication.Instance().setLastSyncStockTime(TimeUtil.getNow_millisecond());
                    } else {
                        ToastUtil.showToast(this, R.string.welcome_sync_stock_fail);
                    }
                }
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_GET_MACHINE_CONIFG) {
                ProgressDlgHelper.closeProgress();
                stopGetConfigTimer();

                GetMachineConfigResult result = GeneralActionResult.parseObject(remote.getBody());
                if (result.getResCode() == 200) {
                    String workTemp = result.getWorkTemp();
                    String keepTemp = result.getKeepTemp();
                    String washTime = result.getWashTime();
                    sendSetCustomTempInstruction(workTemp, keepTemp);
                    saveWashTime(washTime);
                } else {
                    sendSetCustomTempInstruction("105", "103");
                }
            } else if(remote.getAction() == ITranCode.ACT_COFFEE_GET_ADV_PICS) {
                GetAdvPicsResult result = GeneralActionResult.parseObject(remote.getBody());
                if(result.getResCode() == 200) {
                    mFlipper.removeAllViews();
                    List<String> advPicUrls = result.getAdvImgList();
                    if(advPicUrls == null || advPicUrls.size() <= 0){
                        showDefaultAdvPics();
                        return;
                    }

                    for(int i = 0; i < advPicUrls.size(); i++){
                        String advPicUrl = advPicUrls.get(i);
                        mFlipper.addView(addImageByURL(advPicUrl));
                    }

                    mFlipper.setAutoStart(true);
                    mFlipper.setFlipInterval(10 * 1000);
                    mFlipper.startFlipping();
                } else {
                    showDefaultAdvPics();
                }
            }
        }
        // machine action
        if (remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_WASHING) {
                beginWashing();
            } else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE) {
                if(!mWashingStatus.get()){
                    return;
                }
                final String res = remote.getBody();
                if (res.length() == 16) {
                    int result = CoffeeMachineResultProcess.processQueryWashingResult(res.substring(0, 16));
                    if (result == MachineStatusCode.SUCCESS) {
                        LogUtil.vendor("washing machine successfully!");
//                      ToastUtil.showToast(this, R.string.welcome_wash_machine_success);
                        finishWashing();
                    }else if(result == MachineStatusCode.ERROR){
                        LogUtil.vendor("washing machine failed!");
//                      ToastUtil.showToast(this, R.string.welcome_wash_machine_fail);
                        finishWashing();

                        // report server
                        List<Integer> status = new ArrayList<Integer>();
                        MachineStatusReportInfo info = new MachineStatusReportInfo();
                        info.setUid(U.getMyVendorNum());
                        info.setTimestamp(TimeUtil.getNow_millisecond());
                        status.add(MachineStatusCode.WASHING_MACHINE_FAILED);
                        info.setStatus(status);
                        execute(info.toRemote());
                    }
                }
            }
        }
        if(remote.getWhat()==ITranCode.ACT_COFFEE_SERIAL_PORT_INIT){
            if(remote.getAction()==ITranCode.ACT_COFFEE_SERIAL_PORT_INIT_RESULT){
                ProgressDlgHelper.closeProgress();
                String res = remote.getBody();
                String result = CoffeeMachineResultProcess.processSetTempResult(res);
                if (result.equals("success")) {
                    if(mSetTempTimer != null){
                        mSetTempTimer.cancel();
                    }
                    LogUtil.vendor("set temperature successfully!");
                    ToastUtil.showToast(this, R.string.welcome_set_temperature_success);
                } else {
                    LogUtil.vendor("set temperature failed!");
                    ToastUtil.showToast(this, R.string.welcome_set_temperature_fail);
                }
            }
        }
    }

    private void saveWashTime(String washTime){
        try{
            LogUtil.vendor("wash time is " + washTime);
            MyApplication.Instance().mGlobalWashTimeSet.clear();
            if(washTime != null && !TextUtils.isEmpty(washTime)){
                JSONArray array = JSON.parseArray(washTime);
                int size = array.size();
                for(int i = 0; i < size; i++){
                    String time = array.get(i).toString();
                    MyApplication.Instance().mGlobalWashTimeSet.add(time);
                }
            }
            SharePrefConfig.getInstance().setWashTime(MyApplication.Instance().mGlobalWashTimeSet);
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.e("Welcome", "set wash time error");
        }
    }

    private void syncStock(List< CoffeeDosingInfo > dosings){
        if(dosings == null){
            ToastUtil.showToast(this, R.string.control_doing_list_is_null);
            return;
        }

        double dosingWater = getDosingValue(MachineMaterialMap.MATERIAL_WATER);
        double dosingCupNum = getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
        double dosingNo1 = getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
        double dosingNo2 = getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
        double dosingNo3 = getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
        double dosingNo4 = getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
        double dosingNo9 = getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);

        JSONArray array = new JSONArray();
        for(CoffeeDosingInfo info : dosings){
            double value = 0;
            if(info.getId() == 1){
                value = dosingWater;
            }else if(info.getId() == 2){
                value = dosingCupNum;
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_1){
                value = dosingNo1;
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_2){
                value = dosingNo2;
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_3){
                value = dosingNo3;
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_4){
                value = dosingNo4;
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_COFFEE_BEAN){
                value = dosingNo9;
            }

            if(value > 0){
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("id", info.getId());
                jsonObj.put("value", value);
                array.add(jsonObj);
            }
        }

        SyncStockInfo info = new SyncStockInfo();
        info.setUid(U.getMyVendorNum());
        info.setInventory(array.toString());
        execute(info.toRemote());
    }

    private double getDosingValue(int dosingID){
        double value = SharePrefConfig.getInstance().getDosingValue(dosingID);
        return value;
    }

    public View addImageById(int id){
        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageResource(id);

        return iv;
    }

    public View addImageByURL(String url){
        ImageView iv = new ImageView(this);
        iv.setLayoutParams(new ViewFlipper.LayoutParams(ScreenUtil.screenWidth, ScreenUtil.screenHeight));
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        String imgURL = url;
        if(imgURL == null){
            imgURL = "";
        }
        ImageLoaderTool.disPlay(imgURL.trim(), iv, 0);

        return iv;
    }

    private void beginWashing(){
        if(mWashingStatus.compareAndSet(false, true)){
            ProgressDlgHelper.showProgress(this, getString(R.string.welcome_start_wash_machine));
            // send order
            SerialPortDataWritter.writeData(CoffeeMachineInstruction.WASHING);
            // start query timer
            mWashTimer = new Timer();
            mWashTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mWashQueryCount >= 30) {
                        //查询超过30次，还没有成功清洗
                        finishWashing();

                        // report server
                        List<Integer> status = new ArrayList<Integer>();
                        MachineStatusReportInfo info = new MachineStatusReportInfo();
                        info.setUid(U.getMyVendorNum());
                        info.setTimestamp(TimeUtil.getNow_millisecond());
                        status.add(MachineStatusCode.WASHING_MACHINE_FAILED);
                        info.setStatus(status);
                        execute(info.toRemote());

                        LogUtil.e("WelcomePage", "washing machine failed!");
                        return;
                    }

                    SerialPortDataWritter.writeData(CoffeeMachineInstruction.LAST_EXE_ORDER);
                    mWashQueryCount++;
                }
            }, 2000, 3000);
        }
    }

    private void finishWashing(){
        if(mWashTimer != null){
            mWashTimer.cancel();
            mWashTimer = null;
            mWashQueryCount = 0;
        }

        mWashingStatus.compareAndSet(true, false);

        WelcomeActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                ProgressDlgHelper.closeProgress();
            }
        });
    }

    /**
     * background music
     */
    private void startPlayBgMusic(){
        try {
            AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.welcome_bg_music);
            if (afd == null) return ;
            bmMediaPlayer = new MediaPlayer();
            bmMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            bmMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            bmMediaPlayer.prepare();
            bmMediaPlayer.setVolume(1.0f,1.0f);
            bmMediaPlayer.setLooping(true);
            bmMediaPlayer.start();
        } catch (IOException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (SecurityException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        }
    }

    private void stopPlayBgMusic(){
        if(bmMediaPlayer != null){
            bmMediaPlayer.stop();
            bmMediaPlayer.release();
            bmMediaPlayer = null;
        }
    }

    private void pauseBackgroundMusic() {
        if(bmMediaPlayer != null)
            bmMediaPlayer.pause();
    }

    private void resumeBackgroundMusic() {
        if(bmMediaPlayer != null)
            bmMediaPlayer.start();
    }

	@Override
     public void onClick(View v) {
        switch(v.getId()){
            case R.id.welcome_buy_coffee_btn:
                HomePageActivity.start(this, false);
                break;
            case R.id.welcome_music_image:
                if(!mMusicController.isSelected()) {
                    mMusicController.setSelected(true);
                    stopPlayBgMusic();
                } else {
                    mMusicController.setSelected(false);
                    startPlayBgMusic();
                }
                break;
        }
    }
	
	private long mQuitTimeStamp;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {		
		if (keyCode == KeyEvent.KEYCODE_BACK) {	
			if ((System.currentTimeMillis() - mQuitTimeStamp) > 2000) {
				ToastUtil.showToast(this, R.string.welcome_enter_control_panel_tip);
				mQuitTimeStamp = System.currentTimeMillis();
			} else {
				MachineControlActivity.start(this);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

    @Override
    public void onResume(){
        super.onResume();
        MyApplication.Instance().setMachineIdle(true);
        resumeBackgroundMusic();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyApplication.Instance().setMachineIdle(false);
        pauseBackgroundMusic();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ProgressDlgHelper.closeProgress();
        stopPlayBgMusic();
    }
}
