package com.netease.vendor.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.vendor.R;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.beans.CartPayIndent;
import com.netease.vendor.common.action.TActivity;
import com.netease.vendor.common.dbhelper.CoffeeIndentDbHelper;
import com.netease.vendor.instructions.CoffeeMachineInstruction;
import com.netease.vendor.instructions.CoffeeMachineResultProcess;
import com.netease.vendor.instructions.MixedDrinksInstruction;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.MachineStatusReportInfo;
import com.netease.vendor.service.bean.action.RollbackCoffeeIndentCart;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.domain.CoffeeIndent;
import com.netease.vendor.service.protocol.MachineMaterialMap;
import com.netease.vendor.service.protocol.MachineStatusCode;
import com.netease.vendor.ui.EasyAlertDialogForSure;
import com.netease.vendor.util.AudioPlayer;
import com.netease.vendor.util.CountDownTimer;
import com.netease.vendor.util.CountDownTimer.CountDownCallback;
import com.netease.vendor.util.ScreenUtil;
import com.netease.vendor.util.SerialPortDataWritter;
import com.netease.vendor.util.SharePrefConfig;
import com.netease.vendor.util.TimeUtil;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class MakeCoffeeCartActivity extends TActivity implements OnClickListener {

	public static final String TAG = "[MakeCoffeeCart]";

	public enum CoffeeMachineStatus{
		READY, PROCESSING
	}

	public static final String PAY_INDENT = "pay_indent";
	public static final String COFFEE_INDENTS = "coffee_indents";

	private Context mContext;

	private ImageView mMakeCoffeeCancel;
	private ImageView mMakeCoffeeProgress;
	private ImageView mMakeCoffeeSuccess;
	private ImageView mMakeCoffeeFailed;
	private TextView mMakeCoffeeProgressTip;
	private ImageView mMakeCoffeeRetry;

	private static final int QUIT_WAIT_TIME = 2;
	private CountDownTimer mQuitTimer;
	private Timer mQueryTimer;
	private int mQueryCount = 0;
	private boolean isQueryPickCupMode = false;
	private Timer mQueryPickTimer;
	private int mQueryPickCount = 0;

	// 串口相关
	private CoffeeMachineStatus mMachineStatus;
	// 咖啡制作列表
	private List<CartPayIndent> mCoffeeIndentsList = new LinkedList<CartPayIndent>();
	private ArrayList<CoffeeDosingInfo> currentDosingList;
	private int mCurrentIndentIndex = 0;
	private String mPayIndent;

    private boolean isRefreshMenu = false;

	public static void start(Activity activity, String coffeeIndents, String payIndent) {
		Intent intent = new Intent();
		intent.setClass(activity, MakeCoffeeCartActivity.class);
		intent.putExtra(COFFEE_INDENTS, coffeeIndents);
		intent.putExtra(PAY_INDENT, payIndent);
		activity.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.make_coffee_cart_layout);
		mContext = this;
        MyApplication.Instance().setMakingCoffee(true);

		proceedExtra();
		initView();
		initTimer();
		if(checkHaveIndents()){
			makeCoffee();
		}
	}

	private void proceedExtra() {
		Intent intent = getIntent();
		if (intent != null) {
			mPayIndent = intent.getStringExtra(PAY_INDENT);
			String coffeeIndents = intent.getStringExtra(COFFEE_INDENTS);
			LogUtil.e("DEBUG", "coffeeIndents = " + coffeeIndents);
			if(!TextUtils.isEmpty(coffeeIndents)) {
				JSONArray array = JSON.parseArray(coffeeIndents);
				if (array != null && array.size() > 0) {
					int size = array.size();
					for (int i = 0; i < size; ++i) {
						JSONObject jsonObject = array.getJSONObject(i);
						CartPayIndent cpi = new CartPayIndent();
						cpi.fromJSONObject(jsonObject);
						mCoffeeIndentsList.add(cpi);
					}
				}
			}else{
				//TODO
			}
		}
	}

	private void initView() {
		mMakeCoffeeCancel = (ImageView) findViewById(R.id.make_coffee_cancel);
		mMakeCoffeeCancel.setOnClickListener(this);
		mMakeCoffeeProgress = (ImageView) findViewById(R.id.make_coffee_animation);
		AnimationDrawable animationDrawable = (AnimationDrawable) mMakeCoffeeProgress.getBackground();
		animationDrawable.start();
		mMakeCoffeeFailed = (ImageView) findViewById(R.id.make_coffee_failed);
		mMakeCoffeeSuccess = (ImageView) findViewById(R.id.make_coffee_success);
		mMakeCoffeeRetry = (ImageView) findViewById(R.id.make_coffee_retry);
		mMakeCoffeeRetry.setOnClickListener(this);
		mMakeCoffeeProgressTip = (TextView) findViewById(R.id.make_coffee_progress_tip);
		mMakeCoffeeProgressTip.setText(R.string.make_coffee_prepare);
	}

	private void initTimer(){
		mQuitTimer = new CountDownTimer(new CountDownCallback() {

			@Override
			public void currentInterval(int value) {
				onCountDown(value);
			}
		});
	}

	private boolean checkHaveIndents(){
		if(mCoffeeIndentsList.size() <= 0){
			ToastUtil.showToast(this, "订单列表为空");
			mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_FAIL);
			mQuitTimer.startCountDownTimer(QUIT_WAIT_TIME, 1000, 1000);

			return false;
		}

		return true;
	}

	private void onCountDown(final int value){
		// back to home
		if(value <= 0){
			HomePageActivity.start(this, isRefreshMenu);
		}
	}

	private void makeCoffee() {
		if(mCurrentIndentIndex >= mCoffeeIndentsList.size()){
			return;
		}

		CartPayIndent currentIndent = mCoffeeIndentsList.get(mCurrentIndentIndex);
		int coffeeID = currentIndent.getCoffeeID();
		ArrayList<CoffeeDosingInfo> dosingList = MyApplication.Instance().getDosingListInfoByCoffeeID(coffeeID);
		ArrayList<CoffeeDosingInfo> incDosingList = currentIndent.getDosings();
		for(int i = 0; i < incDosingList.size(); i++){
			CoffeeDosingInfo inc = incDosingList.get(i);
			for(int j = 0; j < dosingList.size(); j++){
				CoffeeDosingInfo base = dosingList.get(j);
				if(base != null && inc != null && inc.getId() == base.getId()){
					base.setValue(inc.getValue());
					break;
				}
			}
		}

		// 咖啡具体配置参数
		if (dosingList.size() < 5) {
			for(int k = 0; k < dosingList.size(); k++){
				if(dosingList.get(k).getValue() == 0 && dosingList.get(k).getWater() == 0){
					dosingList.get(k).setOrder(100);
					dosingList.get(k).setId(0);
				}
			}
			for (int l = dosingList.size(); l < 5; l++) {
				CoffeeDosingInfo info = new CoffeeDosingInfo();
				info.setId(0);
				info.setOrder(100);
				info.setValue(0);
				dosingList.add(info);
			}
		}
		Collections.sort(dosingList);
		currentDosingList = dosingList;

		// 根据配置参数，生成打咖啡的指令
		MixedDrinksInstruction md = new MixedDrinksInstruction(
				currentDosingList.get(0).getId(),
				currentDosingList.get(1).getId(),
				currentDosingList.get(2).getId(),
				currentDosingList.get(3).getId(),
				currentDosingList.get(4).getId(),
				MachineMaterialMap.transferToMachine(currentDosingList.get(0).getId(), currentDosingList.get(0).getValue()),
				MachineMaterialMap.transferToMachine(currentDosingList.get(1).getId(), currentDosingList.get(1).getValue()),
				MachineMaterialMap.transferToMachine(currentDosingList.get(2).getId(), currentDosingList.get(2).getValue()),
				MachineMaterialMap.transferToMachine(currentDosingList.get(3).getId(), currentDosingList.get(3).getValue()),
				MachineMaterialMap.transferToMachine(currentDosingList.get(4).getId(), currentDosingList.get(4).getValue()),
				currentDosingList.get(0).getWater(),
				currentDosingList.get(1).getWater(),
				currentDosingList.get(2).getWater(),
				currentDosingList.get(3).getWater(),
				currentDosingList.get(4).getWater(),
				currentDosingList.get(0).getStirtime(),
				currentDosingList.get(1).getStirtime(),
				currentDosingList.get(2).getStirtime(),
				currentDosingList.get(3).getStirtime(),
				currentDosingList.get(4).getStirtime(),
				currentDosingList.get(0).getStirvol(),
				currentDosingList.get(1).getStirvol(),
				currentDosingList.get(2).getStirvol(),
				currentDosingList.get(3).getStirvol(),
				currentDosingList.get(4).getStirvol(),
				currentDosingList.get(0).getEjection(),
				currentDosingList.get(1).getEjection(),
				currentDosingList.get(2).getEjection(),
				currentDosingList.get(3).getEjection(),
				currentDosingList.get(4).getEjection());
		String srcStr = md.getMixedDrinksOrder();
		SerialPortDataWritter.writeData(srcStr);
		mMachineStatus = CoffeeMachineStatus.READY;
	}


	@Override
	public void onReceive(Remote remote) {
		if(remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT
				&& remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE){
			final String res = remote.getBody();
			LogUtil.vendor(TAG + " make coffee result:" + res);
			switch(mMachineStatus){
				case READY:
					if(res.length() == 14){
						int result = CoffeeMachineResultProcess.processBeginMixedCoffeeResult(res);
						LogUtil.e("READY", "result is " + result);
						if(result == MachineStatusCode.SUCCESS){
							enableCancelIndents(false);
							// 退出检测取杯模式
							quitPickUpModeIfNeeded();
							// 机器成功开始打咖啡
							mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_START);
							mMachineStatus = CoffeeMachineStatus.PROCESSING;
							// 更新到本地数据库中
							CartPayIndent currentIndent = mCoffeeIndentsList.get(mCurrentIndentIndex);
							updateIndentStatusDB(FLAG_INIT, currentIndent.getIndentID(), currentIndent.getCoffeeID());
							// 开始每隔3秒发送指令，查看咖啡机状态
							checkMakeCoffeeResult();
						}else if(result == MachineStatusCode.ALREADY_HAVE_CUP){
							enableCancelIndents(true);
							if(!isQueryPickCupMode){
								mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_RETRY);
								LogUtil.vendor(TAG + " please pick up cup and continue");
							}
						}else{
							quitPickUpModeIfNeeded();
							setFailed(result);
						}
					}
					break;
				case PROCESSING:
					if(res.length() == 16){
						int result = CoffeeMachineResultProcess.processMixedCoffeeResult(res.substring(0, 16));
						if(result == MachineStatusCode.SUCCESS){
							// 机器成功完成打咖啡操作
							mMachineStatus = CoffeeMachineStatus.READY;
							// 更新到本地数据库中
							CartPayIndent currentIndent = mCoffeeIndentsList.get(mCurrentIndentIndex);
							updateIndentStatusDB(FLAG_SUCCESS, currentIndent.getIndentID(), currentIndent.getCoffeeID());
							// 更新库存
							updateDosingStock(currentDosingList);
							// 清除Timer
							cancelQueryTimer();

							if(mCurrentIndentIndex >= mCoffeeIndentsList.size() - 1){
								enableCancelIndents(false);
								mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_SUCCESS_ALL);
								mQuitTimer.startCountDownTimer(QUIT_WAIT_TIME, 1000, 1000);
							}else{
								enableCancelIndents(true);
								Message message = new Message();
								message.what = MSG_UI_MAKE_COFFEE_SUCCESS;
								message.obj = MyApplication.Instance().getCoffeeNameByCoffeeID(currentIndent.getCoffeeID());
								mUIHandler.sendMessage(message);

								mCurrentIndentIndex++;
								checkPickCupStatus();
							}
						}else if(result==MachineStatusCode.MACHINE_BUSY
								||result==MachineStatusCode.FOLL_CUP_SYSTEM_BUSY
								||result==MachineStatusCode.MOVE_MOUTH_MOTOR_BUSY
								||result==MachineStatusCode.SPOON_MOTOR_BUSY
								||result==MachineStatusCode.TASK_BUSY
								||result==MachineStatusCode.MACHINE_BUSY) {
							//咖啡机未完成，忽略本指令
							LogUtil.vendor("machine is busy!");
						}else if(result != MachineStatusCode.UNRELEVANT_RESULT){
							//任务失败
							setFailed(result);
							cancelQueryTimer();
						}
					}
					break;
				default:
					//错误状态
					break;
			}
		}
	}

	private void checkMakeCoffeeResult(){
		mQueryTimer = new Timer();
		mQueryTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (mQueryCount >= 30) {
					// 查询超过30次，还没有成功打出咖啡
					setFailed(MachineStatusCode.TIME_OUT);
					cancelQueryTimer();
					return;
				}
				mQueryCount++;

				SerialPortDataWritter.writeData(CoffeeMachineInstruction.LAST_EXE_ORDER);
			}
		}, 2000, 3000);
	}

	private void checkPickCupStatus(){
		isQueryPickCupMode = true;
		mQueryPickTimer = new Timer();
		mQueryPickTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (mQueryPickCount >= 10) {
					cancelPickUpTimer();
					mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_RETRY);
					LogUtil.vendor("[CheckPickCup]" + " please pick up cup and continue");
					return;
				}
				mQueryPickCount++;

				makeCoffee();
			}
		}, 5000, 5000);
	}

	private void quitPickUpModeIfNeeded(){
		if(isQueryPickCupMode){
			cancelPickUpTimer();
		}
	}

	/****************************************** UI ***********************************************************/
	private static final int MSG_UI_MAKE_COFFEE_START = 0;
	private static final int MSG_UI_MAKE_COFFEE_FAIL = 1;
	private static final int MSG_UI_MAKE_COFFEE_RETRY = 2;
	private static final int MSG_UI_MAKE_COFFEE_SUCCESS = 3;
	private static final int MSG_UI_MAKE_COFFEE_SUCCESS_ALL = 4;
	private static final int MSG_UI_MAKE_COFFEE_CANCEL = 5;

	static class SafeHandler extends Handler {
		WeakReference<MakeCoffeeCartActivity> theActivity;

		public SafeHandler(MakeCoffeeCartActivity activity) {
			this.theActivity = new WeakReference<MakeCoffeeCartActivity>(activity);
		}

		@Override
		public void handleMessage(Message message) {
			MakeCoffeeCartActivity activity = theActivity.get();
			if (activity == null) return;

			switch (message.what) {
				case MSG_UI_MAKE_COFFEE_START:
					activity.onMakeCoffeeStart();
					break;
				case MSG_UI_MAKE_COFFEE_FAIL:
					activity.onMakeCoffeeFail();
					break;
				case MSG_UI_MAKE_COFFEE_RETRY:
					activity.onMakeCoffeeRetry();
					break;
				case MSG_UI_MAKE_COFFEE_SUCCESS:
					String coffeeName = String.valueOf(message.obj);
					activity.onMakeCoffeeSuccess(coffeeName);
					break;
				case MSG_UI_MAKE_COFFEE_SUCCESS_ALL:
					activity.onMakeCoffeeSuccessAll();
					break;
				case MSG_UI_MAKE_COFFEE_CANCEL:
					activity.onMakeCoffeeCancel();
					break;
			}
		}
	}

	private SafeHandler mUIHandler = new SafeHandler(this);

	/**
	 * start to make coffee
	 */
	private void onMakeCoffeeStart(){
		// ui
		mMakeCoffeeProgress.setVisibility(View.VISIBLE);
		mMakeCoffeeSuccess.setVisibility(View.INVISIBLE);
		mMakeCoffeeFailed.setVisibility(View.INVISIBLE);
		mMakeCoffeeRetry.setVisibility(View.INVISIBLE);
		if(mCoffeeIndentsList != null){
			CartPayIndent currentIndent = mCoffeeIndentsList.get(mCurrentIndentIndex);
			String coffeeName = MyApplication.Instance().getCoffeeNameByCoffeeID(currentIndent.getCoffeeID());
			int remainCoffeeNum = mCoffeeIndentsList.size() - 1 - mCurrentIndentIndex;
			String message;
			if(remainCoffeeNum <= 0){
				message = String.format(getString(R.string.make_coffee_status), coffeeName);
			}else{
				message = String.format(getString(R.string.make_coffee_cart_status),
						coffeeName, remainCoffeeNum);
			}
			mMakeCoffeeProgressTip.setText(message);
		}
		// sound tip
		AudioPlayer.getInstance().play(this, R.raw.sound_coffee_proceeding);
	}

	/**
	 * continue to make coffee
	 */
	private void onMakeCoffeeSuccess(String coffeeName){
		// ui
		mMakeCoffeeProgress.setVisibility(View.INVISIBLE);
		mMakeCoffeeSuccess.setVisibility(View.VISIBLE);
		mMakeCoffeeProgressTip.setText(String.format(getString(R.string.make_coffee_successful_and_continue), coffeeName));
		mMakeCoffeeFailed.setVisibility(View.INVISIBLE);
		mMakeCoffeeRetry.setVisibility(View.INVISIBLE);
		// sound tip
		AudioPlayer.getInstance().play(this, R.raw.sound_coffee_make_continue);
	}

	/**
	 * finish all coffee
	 */
	private void onMakeCoffeeSuccessAll(){
		// ui
		mMakeCoffeeRetry.setVisibility(View.INVISIBLE);
		mMakeCoffeeProgressTip.setText(R.string.make_coffee_successful_all);
		// sound tip
		AudioPlayer.getInstance().play(this, R.raw.sound_coffee_finish_all);
	}

	/**
	 * fail to make coffee, show tip
	 */
	private void onMakeCoffeeFail(){
		// ui
		mMakeCoffeeProgress.setVisibility(View.INVISIBLE);
		mMakeCoffeeSuccess.setVisibility(View.INVISIBLE);
		mMakeCoffeeProgressTip.setText(getString(R.string.make_coffee_failed));
		mMakeCoffeeFailed.setVisibility(View.VISIBLE);
		mMakeCoffeeRetry.setVisibility(View.INVISIBLE);
		// sound tip
		AudioPlayer.getInstance().play(this, R.raw.sound_coffee_make_fail);
	}

	/**
	 * fail to make coffee, because of have cup
	 */
	private void onMakeCoffeeRetry(){
		// ui
		mMakeCoffeeProgress.setVisibility(View.INVISIBLE);
		mMakeCoffeeSuccess.setVisibility(View.INVISIBLE);
		mMakeCoffeeProgressTip.setText(MakeCoffeeCartActivity.this.getString(R.string.make_coffee_retry));
		mMakeCoffeeFailed.setVisibility(View.VISIBLE);
		mMakeCoffeeRetry.setVisibility(View.VISIBLE);
		// sound tip
		AudioPlayer.getInstance().play(this, R.raw.sound_coffee_make_retry);
	}

	/**
	 * cancel make coffee
	 */
	private void onMakeCoffeeCancel(){
		// ui
		mMakeCoffeeRetry.setVisibility(View.INVISIBLE);
		mMakeCoffeeProgressTip.setText(R.string.make_coffee_user_cancel);
		// sound tip
		AudioPlayer.getInstance().play(this, R.raw.sound_coffee_make_cancel);
	}
	/****************************************** UI ***********************************************************/

	private static final int FLAG_INIT = 1; //init
	private static final int FLAG_ERROR = 2; //error
	private static final int FLAG_SUCCESS = 3; //success
	private void updateIndentStatusDB(int flag, String indentID, int coffeeID){
		if(flag == FLAG_INIT){ //init
			CoffeeIndent ci = new CoffeeIndent();
			ci.setCoffeeindent(indentID);
			ci.setCoffeeid(coffeeID);
			ci.setDosing("");
			ci.setStatus(CoffeeIndent.STATUS_COFFEE_START);
			CoffeeIndentDbHelper.insertCoffeeIndent(ci);
		}else if(flag == FLAG_ERROR){ //Error
			CoffeeIndent oldCi = CoffeeIndentDbHelper.getCoffeeIndent(indentID);
			if(oldCi != null){
				oldCi.setStatus(CoffeeIndent.STATUS_COFFEE_ERROR);
				CoffeeIndentDbHelper.updateCoffeeIndentStatus(oldCi);
				LogUtil.vendor("update coffee indent status to error in local DB:"+ indentID);
			}else{
				CoffeeIndent newCi = new CoffeeIndent();
				CartPayIndent currentIndent = mCoffeeIndentsList.get(mCurrentIndentIndex);
				newCi.setCoffeeindent(currentIndent.getIndentID());
				newCi.setCoffeeid(currentIndent.getCoffeeID());
				newCi.setDosing("");
				newCi.setStatus(CoffeeIndent.STATUS_COFFEE_ERROR);
				CoffeeIndentDbHelper.insertCoffeeIndent(newCi);
				LogUtil.vendor("insert error status of coffee indent into local DB:"+ indentID);
			}
		}else if (flag == FLAG_SUCCESS){ //Success
			CoffeeIndent ci = CoffeeIndentDbHelper.getCoffeeIndent(indentID);
			if(ci != null){
				ci.setStatus(CoffeeIndent.STATUS_COFFEE_DONE);
				CoffeeIndentDbHelper.updateCoffeeIndentStatus(ci);
				LogUtil.vendor("make coffee successfully! CoffeeIndent:" + indentID);
			}
		}
	}

	private void setFailed(int errorCode){
		// update indent status in DB
		CartPayIndent currentIndent = mCoffeeIndentsList.get(mCurrentIndentIndex);
		String indentID = currentIndent.getIndentID();
		int coffeeID = currentIndent.getCoffeeID();
		LogUtil.e(TAG, "make coffee unsuccessully, code:" + errorCode + ", indent:" + indentID);
		updateIndentStatusDB(FLAG_ERROR, indentID, coffeeID);
		// rollback order
		rollBackOrder(errorCode);
		// report server
		List<Integer> status = new ArrayList<Integer>();
		MachineStatusReportInfo info = new MachineStatusReportInfo();
		info.setUid(U.getMyVendorNum());
		info.setTimestamp(TimeUtil.getNow_millisecond());
		status.add(errorCode);
		info.setStatus(status);
		execute(info.toRemote());

		mMachineStatus = CoffeeMachineStatus.READY;
		// quit time
		mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_FAIL);
		mQuitTimer.startCountDownTimer(QUIT_WAIT_TIME, 1000, 1000);
	}

	private void updateDosingStock(ArrayList<CoffeeDosingInfo> dosingList){
		Map<Integer, Double> dosingMap = new HashMap<Integer, Double>();
		for (CoffeeDosingInfo info : dosingList) {
			dosingMap.put(info.getId(), info.getValue());
		}

		double stockBox1 =	SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
		double stockBox2 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
		double stockBox3 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
		double stockBox4 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
		double stockBean = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
		double stockWater = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
		double stockCupNum = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
		LogUtil.e("DEBUG", stockWater + "-" + stockBox1 + "-" + stockBox2 + "-" + stockBox3 + "-" + stockBox4
				+ "-" + stockBean + "-" + stockCupNum);

		int resumeWater = 0;
		Iterator<CoffeeDosingInfo> it = dosingList.iterator();
		while(it.hasNext()){
			CoffeeDosingInfo info = it.next();
			int water = info.getWater();
			resumeWater += water;
		}
		double resumeBox1 = dosingMap.get(MachineMaterialMap.MATERIAL_BOX_1) == null ? 0
				: dosingMap.get(MachineMaterialMap.MATERIAL_BOX_1);
		double resumeBox2 = dosingMap.get(MachineMaterialMap.MATERIAL_BOX_2) == null ? 0
				: dosingMap.get(MachineMaterialMap.MATERIAL_BOX_2);
		double resumeBox3 = dosingMap.get(MachineMaterialMap.MATERIAL_BOX_3) == null ? 0
				: dosingMap.get(MachineMaterialMap.MATERIAL_BOX_3);
		double resumeBox4 = dosingMap.get(MachineMaterialMap.MATERIAL_BOX_4) == null ? 0
				: dosingMap.get(MachineMaterialMap.MATERIAL_BOX_4);
		double resumeBean = dosingMap.get(MachineMaterialMap.MATERIAL_COFFEE_BEAN) == null ? 0
				: dosingMap.get(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
		double resumeCupNum = 1;
		LogUtil.e("DEBUG", resumeWater + "-" + resumeBox1 + "-" + resumeBox2 + "-" + resumeBox3 + "-" + resumeBox4
				+ "-" + resumeBean + "-" + resumeCupNum);

		double leftWater = stockWater - resumeWater;
		BigDecimal leftWaterBD = new BigDecimal(leftWater);
		leftWater = leftWaterBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftWater < 0){ leftWater = 0; }
		double leftBox1 = stockBox1 - resumeBox1;
		BigDecimal leftBox1BD = new BigDecimal(leftBox1);
		leftBox1 = leftBox1BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftBox1 < 0){leftBox1 = 0;}
		double leftBox2 = stockBox2 - resumeBox2;
		BigDecimal leftBox2BD = new BigDecimal(leftBox2);
		leftBox2 = leftBox2BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftBox2 < 0){leftBox2 = 0;}
		double leftBox3 = stockBox3 - resumeBox3;
		BigDecimal leftBox3BD = new BigDecimal(leftBox3);
		leftBox3 = leftBox3BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftBox3 < 0){leftBox3 = 0;}
		double leftBox4 = stockBox4 - resumeBox4;
		BigDecimal leftBox4BD = new BigDecimal(leftBox4);
		leftBox4 = leftBox4BD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftBox4 < 0){leftBox4 = 0;}
		double leftBean = stockBean - resumeBean;
		BigDecimal leftBeanBD = new BigDecimal(leftBean);
		leftBean = leftBeanBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if(leftBean < 0){leftBean = 0;}
		double leftCupNum = stockCupNum - resumeCupNum;

		// update local stock
		SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftWater),
				MachineMaterialMap.MATERIAL_WATER);
		SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox1),
				MachineMaterialMap.MATERIAL_BOX_1);
		SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox2),
				MachineMaterialMap.MATERIAL_BOX_2);
		SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox3),
				MachineMaterialMap.MATERIAL_BOX_3);
		SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBox4),
				MachineMaterialMap.MATERIAL_BOX_4);
		SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftBean),
				MachineMaterialMap.MATERIAL_COFFEE_BEAN);
		SharePrefConfig.getInstance().setDosingValue(String.valueOf(leftCupNum),
				MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);

		// check stock, if reach alarm value, report server
        /*
		if(leftWater <= MachineMaterialMap.MATERIAL_WATER_ALARM_VALUE
                || leftBox1 <= MachineMaterialMap.MATERIAL_BOX_1_ALARM_VALUE
                || leftBox2 <= MachineMaterialMap.MATERIAL_BOX_2_ALARM_VALUE
                || leftBox3 <= MachineMaterialMap.MATERIAL_BOX_3_ALARM_VALUE
                || leftBox4 <= MachineMaterialMap.MATERIAL_BOX_4_ALARM_VALUE
                || leftBean <= MachineMaterialMap.MATERIAL_COFFEE_BEAN_ALARM_VALUE
                || leftCupNum <= MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM_ALARM_VALUE){
            // report server
            List<Integer> status = new ArrayList<Integer>();
            MachineStatusReportInfo info = new MachineStatusReportInfo();
            info.setUid(U.getMyVendorNum());
            info.setTimestamp(TimeUtil.getNow_millisecond());
            status.add(MachineStatusCode.MATERIAL_DEFICIENCY);
            info.setStatus(status);
            execute(info.toRemote());
        }*/

		// check stock, if reach limit value, refresh menu
		if(leftWater <= MachineMaterialMap.MATERIAL_WATER_LIMIT_VALUE
				|| leftBox1 <= MachineMaterialMap.MATERIAL_BOX_1_LIMIT_VALUE
				|| leftBox2 <= MachineMaterialMap.MATERIAL_BOX_2_LIMIT_VALUE
				|| leftBox3 <= MachineMaterialMap.MATERIAL_BOX_3_LIMIT_VALUE
				|| leftBox4 <= MachineMaterialMap.MATERIAL_BOX_4_LIMIT_VALUE
				|| leftBean <= MachineMaterialMap.MATERIAL_COFFEE_BEAN_LIMIT_VALUE
				|| leftCupNum <= MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM_LIMIT_VALUE){
			isRefreshMenu = true;
		}
	}

	private void rollBackOrder(int errorCode){
		RollbackCoffeeIndentCart info = new RollbackCoffeeIndentCart();
		info.setPayIndent(mPayIndent);
		JSONArray array = new JSONArray();
		for(int i = mCurrentIndentIndex; i < mCoffeeIndentsList.size(); i++){
			CartPayIndent indent = mCoffeeIndentsList.get(i);
			array.add(indent.getIndentID());
		}
		info.setCoffeeIndents(array.toString());
		info.setReason(String.valueOf(errorCode));
		execute(info.toRemote());
	}

	private void enableCancelIndents(boolean flag){
		if(flag){
			mMakeCoffeeCancel.setVisibility(View.VISIBLE);
		}else{
			mMakeCoffeeCancel.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.make_coffee_retry:
			mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_START);
			makeCoffee();
			break;
		case R.id.make_coffee_cancel:
			String message = getString(R.string.make_coffee_cancel);
			EasyAlertDialogForSure dialog = new EasyAlertDialogForSure(this, new EasyAlertDialogForSure.OnDialogListener(){

				@Override
				public void OnDialogCancel() {
					// DO NOTHING
				}

				@Override
				public void OnDialogConfirm() {
					quitPickUpModeIfNeeded();
					rollBackOrder(MachineStatusCode.USER_CANCELED);

					mUIHandler.sendEmptyMessage(MSG_UI_MAKE_COFFEE_CANCEL);
					mQuitTimer.startCountDownTimer(QUIT_WAIT_TIME, 1000, 1000);
				}
			}, true, message);
			dialog.show();

			break;
		}
	}

	@Override
	protected void onStop() {
		releaseImageViews();
		super.onStop();
	}

	private void releaseImageViews() {
		releaseImageView(mMakeCoffeeProgress);
	}

	private void releaseImageView(ImageView imageView) {
		Drawable d = imageView.getDrawable();
		if (d != null)
			d.setCallback(null);
		imageView.setImageDrawable(null);
		imageView.setBackgroundDrawable(null);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		cancelQuitTimer();
		cancelQueryTimer();
        MyApplication.Instance().setMakingCoffee(false);
		AudioPlayer.getInstance().stop();
	}
	
	private void cancelQueryTimer(){
		if(mQueryTimer != null){
			mQueryTimer.cancel();
			mQueryTimer = null;
			mQueryCount = 0;
		}
	}

	private void cancelPickUpTimer(){
		if(mQueryPickTimer != null){
			mQueryPickTimer.cancel();
			mQueryPickTimer = null;
			mQueryPickCount = 0;
			isQueryPickCupMode = false;
		}
	}

    public void cancelQuitTimer() {
        if (mQuitTimer != null) {
			mQuitTimer.cancelCountDownTimer();
        }
    }
}
