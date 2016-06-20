package com.netease.vendor.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.vendor.R;
import com.netease.vendor.application.AppConfig;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.common.action.TActivity;
import com.netease.vendor.common.dbhelper.CoffeeIndentDbHelper;
import com.netease.vendor.instructions.CoffeeMachineInstruction;
import com.netease.vendor.instructions.CoffeeMachineResultProcess;
import com.netease.vendor.instructions.MixedDrinksInstruction;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.CancelTradeInfo;
import com.netease.vendor.service.bean.action.MachineStatusReportInfo;
import com.netease.vendor.service.bean.action.RollbackCoffeeIndent;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.domain.CoffeeIndent;
import com.netease.vendor.service.protocol.MachineMaterialMap;
import com.netease.vendor.service.protocol.MachineStatusCode;
import com.netease.vendor.util.CountDownTimer;
import com.netease.vendor.util.SerialPortDataWritter;
import com.netease.vendor.util.ScreenUtil;
import com.netease.vendor.util.SharePrefConfig;
import com.netease.vendor.util.CountDownTimer.CountDownCallback;
import com.netease.vendor.util.TimeUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;

public class MakeCoffeeActivity extends TActivity implements
		OnClickListener {

	
	public enum CoffeeMachineStatus{
		READY, PROCESSING;
	}

	public static final String COFFEE_INDENT = "coffee_indent";
	public static final String COFFEE_ID = "coffee_id";
	public static final String COFFEE_DOSING = "coffee_dosing";
    public static final String COFFEE_INNER_PAY = "inner_pay";

	private ImageView mBackBtn;
	private ImageView mMakeCoffeeProgress;
	private TextView mMakeCoffeeProgressTip;
	private ImageView mMakeCoffeeFailed;
	private ImageButton mMakeCoffeeRetry;

	private String mCoffeeIndent;
	private int mCoffeeID;
	private ArrayList<CoffeeDosingInfo> dosingList;
    private boolean mInnerPay;
	
	private CountDownTimer quitTimer;
	private Timer queryTimer;
	
	private int queryCount = 0;
    //咖啡是否制作成功，仅影响退出该activity时显示的文字
    private boolean makeCoffeeSuccess = false;

	// 串口相关
	private CoffeeMachineStatus machineStatus;

	private boolean isCoffeeType;
    private boolean isRefreshMenu = false;

	public static void start(Activity activity, String coffeeIndent,
			int coffeeId, ArrayList<CoffeeDosingInfo> dosingList, boolean innerPay) {
		Intent intent = new Intent();
		intent.setClass(activity, MakeCoffeeActivity.class);
		intent.putExtra(COFFEE_INDENT, coffeeIndent);
		intent.putExtra(COFFEE_ID, coffeeId);
        intent.putExtra(COFFEE_INNER_PAY, innerPay);
		intent.putParcelableArrayListExtra(COFFEE_DOSING, dosingList);
		activity.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.make_coffee_layout);

        MyApplication.Instance().setMakingCoffee(true);

		proceedExtra();
		initView();
		initTimer();

		makeCoffee();
	}

	private void initTimer(){
		quitTimer = new CountDownTimer(new CountDownCallback() {

			@Override
			public void currentInterval(int value) {
				onCountDown(value);
			}
		});
	}
	
	private void onCountDown(final int value){
		// update ui
		MakeCoffeeActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				if(makeCoffeeSuccess){
					mMakeCoffeeProgressTip.setText(String.format(MakeCoffeeActivity.this.getString(
							R.string.make_coffee_successful_all), value));
				}else{
					mMakeCoffeeProgressTip.setText(MakeCoffeeActivity.this.getString(
							R.string.make_coffee_failed));
				}
			}
		});

		// back to home
		if(value <= 0){
            HomePageActivity.start(this, isRefreshMenu);
		}
	}

	private void initView() {
		mBackBtn = (ImageView) findViewById(R.id.make_coffee_back_btn);
		mBackBtn.setOnClickListener(this);

		mMakeCoffeeProgress = (ImageView) findViewById(R.id.make_coffee_animation);
		AnimationDrawable animationDrawable = (AnimationDrawable) mMakeCoffeeProgress
				.getBackground();
		animationDrawable.start();
		mMakeCoffeeProgressTip = (TextView) findViewById(R.id.make_coffee_progress_tip);
		mMakeCoffeeProgressTip.setText(R.string.make_coffee_prepare);
		
		mMakeCoffeeFailed = (ImageView) findViewById(R.id.make_coffee_failed);
		mMakeCoffeeRetry = (ImageButton) findViewById(R.id.make_coffee_retry);
		
		mMakeCoffeeRetry.setOnClickListener(this);

		int bannerWidth = ScreenUtil.screenWidth;
		int bannerHeight = (int) (bannerWidth * (585.0 / 1080.0));
		ImageView banner = (ImageView) findViewById(R.id.make_coffee_banner);
		banner.setLayoutParams(new LinearLayout.LayoutParams(bannerWidth,
				bannerHeight));
	}

	private void proceedExtra() {
		Intent intent = getIntent();
		if (intent != null) {
			mCoffeeIndent = intent.getStringExtra(COFFEE_INDENT);
			mCoffeeID = intent.getIntExtra(COFFEE_ID, 0);
			dosingList = intent.getParcelableArrayListExtra(COFFEE_DOSING);
			if(dosingList == null){
				dosingList = new ArrayList<CoffeeDosingInfo>();
			}
            mInnerPay = intent.getBooleanExtra(COFFEE_INNER_PAY, false);

			for (CoffeeDosingInfo info : dosingList) {
				LogUtil.vendor("[MakeCoffeeActivity]" + info.toString());
				if(info.getName().equals("咖啡豆")){
					isCoffeeType = true;
					LogUtil.vendor("this is a coffee type");
				}
			}
		}
	}

	private void makeCoffee() {
		// 咖啡具体配置参数
		if (dosingList.size() < 5) {
            for(int i = 0; i < dosingList.size(); i++){
                if(dosingList.get(i).getValue() == 0 && dosingList.get(i).getWater() == 0){
                    dosingList.get(i).setOrder(100);
                    dosingList.get(i).setId(0);
                }
            }
			for (int i = dosingList.size(); i < 5; i++) {
				CoffeeDosingInfo info = new CoffeeDosingInfo();
				info.setId(0);
				info.setOrder(100);
				info.setValue(0);
				dosingList.add(info);
			}
		}
		Collections.sort(dosingList);

		if(!AppConfig.isSerialportSysnc()){
			// JUST TEST
            //updateDosingStock(dosingList);
			//makeCoffeeSuccess = true;
			//quitTimer.startCountDownTimer(5, 1000, 1000);

			List<Integer> status = new ArrayList<Integer>();
			MachineStatusReportInfo info = new MachineStatusReportInfo();
			info.setUid(U.getMyVendorNum());
			info.setTimestamp(TimeUtil.getNow_millisecond());
			status.add(MachineStatusCode.HEAT_TEMPERATURE_NOT_CHANGED);
			info.setStatus(status);
			execute(info.toRemote());

			rollBackOrder();

            makeCoffeeSuccess = false;
			quitTimer.startCountDownTimer(5, 1000, 1000);
		}else{
			// 根据配置参数，生成打咖啡的指令
			MixedDrinksInstruction md = new MixedDrinksInstruction(
					dosingList.get(0).getId(),
                    dosingList.get(1).getId(),
					dosingList.get(2).getId(),
                    dosingList.get(3).getId(),
					dosingList.get(4).getId(),
                    MachineMaterialMap.transferToMachine(dosingList.get(0).getId(), dosingList.get(0).getValue()),
                    MachineMaterialMap.transferToMachine(dosingList.get(1).getId(), dosingList.get(1).getValue()),
                    MachineMaterialMap.transferToMachine(dosingList.get(2).getId(), dosingList.get(2).getValue()),
                    MachineMaterialMap.transferToMachine(dosingList.get(3).getId(), dosingList.get(3).getValue()),
                    MachineMaterialMap.transferToMachine(dosingList.get(4).getId(), dosingList.get(4).getValue()),
                    dosingList.get(0).getWater(),
                    dosingList.get(1).getWater(),
                    dosingList.get(2).getWater(),
                    dosingList.get(3).getWater(),
                    dosingList.get(4).getWater(),
                    dosingList.get(0).getStirtime(),
                    dosingList.get(1).getStirtime(),
                    dosingList.get(2).getStirtime(),
                    dosingList.get(3).getStirtime(),
                    dosingList.get(4).getStirtime(),
                    dosingList.get(0).getStirvol(),
                    dosingList.get(1).getStirvol(),
                    dosingList.get(2).getStirvol(),
                    dosingList.get(3).getStirvol(),
                    dosingList.get(4).getStirvol(),
                    dosingList.get(0).getEjection(),
                    dosingList.get(1).getEjection(),
                    dosingList.get(2).getEjection(),
                    dosingList.get(3).getEjection(),
                    dosingList.get(4).getEjection());
			String srcStr = md.getMixedDrinksOrder();
			SerialPortDataWritter.writeData(srcStr);
			machineStatus = CoffeeMachineStatus.READY;
		}
	}
	
	private void updateDosingStock(ArrayList<CoffeeDosingInfo> dosingList){
		Map<Integer, Double> dosingMap = new HashMap<Integer, Double>();
		for (CoffeeDosingInfo info : dosingList) {
			dosingMap.put(info.getId(), info.getValue());
		}

		// caculate materical stock
		double stockWater = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
		double stockBox1 =	SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
		double stockBox2 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
		double stockBox3 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
		double stockBox4 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
		double stockBean = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
		double stockCupNum = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
		LogUtil.e("checkDosingStock", stockWater + "-" + stockBox1 + "-" + stockBox2 + "-" + stockBox3 + "-" + stockBox4
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

		double resumeBean = isCoffeeType ?  7.5 : 0;
        double resumeCupNum = 1;
		LogUtil.e("checkDosingStock", resumeWater + "-" + resumeBox1 + "-" + resumeBox2 + "-" + resumeBox3 + "-" + resumeBox4
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.make_coffee_back_btn:
			this.finish();
			break;
		case R.id.make_coffee_retry:
			setMakeCoffee();
			makeCoffee();
			break;
		}
	}

	@Override
	public void onReceive(Remote remote) {
		if(remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT_INIT){
			if(remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_INIT_RESULT){
				String res = remote.getBody();
				LogUtil.vendor("[SerialPortInit] make coffee result:" + res);
				String result = CoffeeMachineResultProcess.processSetTempResult(res);
				if(result.equals("success")){
					cancelQueryTimer();
                    makeCoffee();
				}
			}
		}else if(remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT
                && remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE){
			final String res = remote.getBody();
            LogUtil.vendor("[SerialPortMakeCoffee] make coffee result:" + res);
			switch(machineStatus){
			case READY:
				if(res.length() == 14){
					int result = CoffeeMachineResultProcess.processBeginMixedCoffeeResult(res);
					if(result == MachineStatusCode.SUCCESS){
						setMakeCoffee();
						//机器成功开始打咖啡
						machineStatus = CoffeeMachineStatus.PROCESSING;
						
						// update local DB
                        try{
                            CoffeeIndent ci = new CoffeeIndent();
                            ci.setCoffeeindent(mCoffeeIndent);
                            ci.setCoffeeid(mCoffeeID);
                            ci.setDosing("");
                            ci.setStatus(CoffeeIndent.STATUS_COFFEE_START);
                            CoffeeIndentDbHelper.insertCoffeeIndent(ci);
                        }catch(Exception e){
                            e.printStackTrace();
                            LogUtil.e("MakeCoffee", "fail to update indent status in local DB when start make coffee");
                        }

						
						//开始每隔2秒发送指令，查看咖啡机状态
						TimerTask timerTask = new TimerTask() {
							@Override
							public void run() {
								if(queryCount >= 30){
									//查询超过30次，还没有成功打出咖啡
									setFailed(MachineStatusCode.TIME_OUT);
									rollBackOrder();
									cancelQueryTimer();
									return;
								}
								SerialPortDataWritter.writeData(CoffeeMachineInstruction.LAST_EXE_ORDER);
								queryCount++;
							}
						};
						
						queryTimer = new Timer();
						queryTimer.schedule(timerTask, 2000, 3000);
					}else if(result == MachineStatusCode.ALREADY_HAVE_CUP){
						setRetry();
						LogUtil.e("MakeCoffee", "please pick up cup and continue");
					}else if(result == MachineStatusCode.MACHINE_WARM_UP){
						//开始每隔2秒发送指令，设置咖啡机温度
						TimerTask timerTask = new TimerTask() {
							@Override
							public void run() {
								if(queryCount >= 10){
									//查询超过10次，还没有成功设置温度
                                    setFailed(MachineStatusCode.SET_TEMP_FAILED);
                                    rollBackOrder();
									cancelQueryTimer();
									return;
								}
								SerialPortDataWritter.writeData(CoffeeMachineInstruction.SET_TEMP);
								queryCount++;
							}
						};
						
						queryTimer = new Timer();
						queryTimer.schedule(timerTask, 0, 1000);
					}else{
						setFailed(result);
                        rollBackOrder();
					}
				}
				break;
			case PROCESSING:
				if(res.length() == 16){
					int result = CoffeeMachineResultProcess.processMixedCoffeeResult(res.substring(0, 16));
					if(result == MachineStatusCode.SUCCESS){
						//机器成功完成打咖啡操作
						machineStatus = CoffeeMachineStatus.READY;
						
						//咖啡成功打出
                        try{
                            CoffeeIndent ci = CoffeeIndentDbHelper.getCoffeeIndent(mCoffeeIndent);
                            if(ci != null){
                                ci.setStatus(CoffeeIndent.STATUS_COFFEE_DONE);
                                CoffeeIndentDbHelper.updateCoffeeIndentStatus(ci);
                                LogUtil.vendor("make coffee successfully! CoffeeIndent:"+ mCoffeeIndent);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            LogUtil.e("MakeCoffee", "fail to update indent status in local DB after make coffee successfully");
                        }

                        // 检查库存
                        updateDosingStock(dosingList);
						
						cancelQueryTimer();
						cancelCountDownTimer();

						// quit timer
                        makeCoffeeSuccess = true;
						quitTimer.startCountDownTimer(5, 1000, 1000);
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
                        rollBackOrder();
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
	
	@Override
	public void onDestroy() {
		cancelCountDownTimer();
        MyApplication.Instance().setMakingCoffee(false);
		super.onDestroy();
	}
	
	private void cancelQueryTimer(){
		if(queryTimer!=null){
			queryTimer.cancel();
			queryTimer = null;
			queryCount = 0;
		}
	}
	
	private void setRetry(){
        MakeCoffeeActivity.this.runOnUiThread(new Runnable(){
            public void run()
            {
                mMakeCoffeeProgress.setVisibility(View.INVISIBLE);
                mMakeCoffeeProgressTip.setText(MakeCoffeeActivity.this.getString(R.string.make_coffee_retry));
                mMakeCoffeeFailed.setVisibility(View.VISIBLE);
                mMakeCoffeeRetry.setVisibility(View.VISIBLE);
            }
        });
	}

	private void setFailed(int machineStatusCode){
        LogUtil.e("MakeCoffee", "make coffee unsuccessully, MachineStatusCode:" + machineStatusCode+" , CoffeeIndent:" + mCoffeeIndent);

        // update ui
        MakeCoffeeActivity.this.runOnUiThread(new Runnable(){
            public void run() {
                mMakeCoffeeProgress.setVisibility(View.INVISIBLE);
                mMakeCoffeeProgressTip.setText(MakeCoffeeActivity.this.getString(R.string.make_coffee_failed));
                mMakeCoffeeFailed.setVisibility(View.VISIBLE);
                mMakeCoffeeRetry.setVisibility(View.INVISIBLE);
            }
        });

        // update indent status in local DB
        try{
            CoffeeIndent oldCi = CoffeeIndentDbHelper.getCoffeeIndent(mCoffeeIndent);
            if(oldCi != null){
                oldCi.setStatus(CoffeeIndent.STATUS_COFFEE_ERROR);
                CoffeeIndentDbHelper.updateCoffeeIndentStatus(oldCi);
                LogUtil.vendor("update coffee indent status to error in local DB:"+ mCoffeeIndent);
            }else{
                CoffeeIndent newCi = new CoffeeIndent();
                newCi.setCoffeeindent(mCoffeeIndent);
                newCi.setCoffeeid(mCoffeeID);
                newCi.setDosing("");
                newCi.setStatus(CoffeeIndent.STATUS_COFFEE_ERROR);
                CoffeeIndentDbHelper.insertCoffeeIndent(newCi);
                LogUtil.vendor("insert error status of coffee indent into local DB:"+ mCoffeeIndent);
            }
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.e("MakeCoffee", "fail to update indent status in local DB after make coffee unsuccessfully");
        }


        // report server
        List<Integer> status = new ArrayList<Integer>();
        MachineStatusReportInfo info = new MachineStatusReportInfo();
        info.setUid(U.getMyVendorNum());
        info.setTimestamp(TimeUtil.getNow_millisecond());
        status.add(machineStatusCode);
        info.setStatus(status);
        execute(info.toRemote());

        // cancel timer
		cancelCountDownTimer();
		machineStatus = CoffeeMachineStatus.READY;

        // quit timer
        makeCoffeeSuccess = false;
        quitTimer.startCountDownTimer(5, 1000, 1000);
	}
	
	private void rollBackOrder(){
        if(!TextUtils.isEmpty(mCoffeeIndent)){
            if(mInnerPay){
                RollbackCoffeeIndent info = new RollbackCoffeeIndent();
                info.setUid(U.getMyVendorNum());
                info.setCoffeeIndent(mCoffeeIndent);
                info.setTimestamp(TimeUtil.getNow_millisecond());
                info.setRetry(true);
                execute(info.toRemote());
            }else{
                CancelTradeInfo info = new CancelTradeInfo();
                info.setUid(U.getMyVendorNum());
                info.setCoffeeIndent(mCoffeeIndent);
                execute(info.toRemote());
            }
        }
	}
	
	private void setMakeCoffee(){
        MakeCoffeeActivity.this.runOnUiThread(new Runnable(){
            public void run() {
                mMakeCoffeeProgress.setVisibility(View.VISIBLE);
                mMakeCoffeeProgressTip.setText(MakeCoffeeActivity.this.getString(R.string.make_coffee_prepare));
                mMakeCoffeeFailed.setVisibility(View.INVISIBLE);
                mMakeCoffeeRetry.setVisibility(View.INVISIBLE);
            }
        });
	}
	
	/**
     * 取消计时任务
     */
    public void cancelCountDownTimer() {
        if (queryTimer != null) {
            queryTimer.cancel();
            queryTimer = null;
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
}
