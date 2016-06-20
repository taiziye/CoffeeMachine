package com.netease.vendor.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.vendor.R;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.common.action.TActivity;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.GetDosingListInfo;
import com.netease.vendor.service.bean.action.SyncStockInfo;
import com.netease.vendor.service.bean.result.GeneralActionResult;
import com.netease.vendor.service.bean.result.GetDosingResult;
import com.netease.vendor.service.bean.result.UpdateStockResult;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.protocol.MachineMaterialMap;
import com.netease.vendor.ui.ProgressDlgHelper;
import com.netease.vendor.util.SharePrefConfig;
import com.netease.vendor.util.TimeUtil;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.U;

import java.util.List;

public class MaterialConfigActivity extends TActivity implements OnClickListener{

	private Context mContext;

    private boolean foreground;

	private Button mSetDosingBtn;

	private EditText mSetDosingWater;
    private EditText mSetDosingCupNum;

	private EditText mSetDosingNo1;
    private EditText mSetDosingNo2;
    private EditText mSetDosingNo3;
    private EditText mSetDosingNo4;
    private EditText mSetDosingNo9;

    private String dosingWater;
    private String dosingCupNum;

    private String dosingNo1;
    private String dosingNo2;
    private String dosingNo3;
    private String dosingNo4;
    private String dosingNo9;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.material_config_layout);
		mContext = this;
		proceedExtras();
		
		initView();
	}
	
	private void proceedExtras(){
	}
	
	private void initView(){
        mSetDosingBtn = (Button) findViewById(R.id.login_init_dosing);
        mSetDosingBtn.setOnClickListener(this);

        mSetDosingWater = (EditText) findViewById(R.id.set_dosing_water);
        mSetDosingCupNum = (EditText) findViewById(R.id.set_dosing_cup);

        mSetDosingNo1 = (EditText) findViewById(R.id.set_dosing_no1);
        mSetDosingNo2 = (EditText) findViewById(R.id.set_dosing_no2);
        mSetDosingNo3 = (EditText) findViewById(R.id.set_dosing_no3);
        mSetDosingNo4 = (EditText) findViewById(R.id.set_dosing_no4);
        mSetDosingNo9 = (EditText) findViewById(R.id.set_dosing_no9);

		showKeyboard(mSetDosingNo1);
	}

	@Override
	public void onClick(View v) {		
		switch(v.getId()){
		case R.id.login_init_dosing:
            getCoffeeDosingList();
			break;
		default:
			break;
		}
	}

    private void getCoffeeDosingList(){
        ProgressDlgHelper.showProgress(this, "获取原料列表");
        GetDosingListInfo info = new GetDosingListInfo();
        info.setUid(U.getMyVendorNum());
        execute(info.toRemote());
    }

    private void doInitDosing(List<CoffeeDosingInfo> dosings){
        if(dosings == null){
            ToastUtil.showToast(this, R.string.control_doing_list_is_null);
            return;
        }

        dosingWater = mSetDosingWater.getText().toString();
        dosingCupNum = mSetDosingCupNum.getText().toString();

        dosingNo1 = mSetDosingNo1.getText().toString();
        dosingNo2 = mSetDosingNo2.getText().toString();
        dosingNo3 = mSetDosingNo3.getText().toString();
        dosingNo4 = mSetDosingNo4.getText().toString();
        dosingNo9 = mSetDosingNo9.getText().toString();

        if(TextUtils.isEmpty(dosingWater) || TextUtils.isEmpty(dosingCupNum) || TextUtils.isEmpty(dosingNo1)
                || TextUtils.isEmpty(dosingNo2) || TextUtils.isEmpty(dosingNo3) || TextUtils.isEmpty(dosingNo4)
                || TextUtils.isEmpty(dosingNo9)){
            ToastUtil.showToast(this, R.string.control_set_dosing_is_null);
            return;
        }

        JSONArray array = new JSONArray();
        for(CoffeeDosingInfo info : dosings){
            String value = "";

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

            if(!TextUtils.isEmpty(value)){
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("id", info.getId());
                jsonObj.put("value", Double.parseDouble(value));
                array.add(jsonObj);
            }
        }

        ProgressDlgHelper.showProgress(this, "更新配料列表");
        SyncStockInfo info = new SyncStockInfo();
        info.setUid(U.getMyVendorNum());
        info.setInventory(array.toString());
        execute(info.toRemote());
    }

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mSetDosingWater.getWindowToken(), 0);
	}

	private void showKeyboard(EditText editText) {
		if (!foreground) {
			return;
		}
		editText.setFocusable(true);
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void onResume() {
		super.onResume();
		foreground = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		foreground = false;
	}
	
	@Override
	public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_DOSING_LIST) {
                ProgressDlgHelper.closeProgress();
                GetDosingResult result = GeneralActionResult.parseObject(remote.getBody());
                if(result != null && !result.isAuto()){
                    if(result.getResCode() == 200){
                        doInitDosing(result.getDosings());
                    }else{
                        ToastUtil.showToast(this, "获取原料失败...");
                    }
                }
            }else if(remote.getAction() == ITranCode.ACT_COFFEE_STOCK_UPDATE){
                ProgressDlgHelper.closeProgress();
                UpdateStockResult result = GeneralActionResult.parseObject(remote.getBody());
                if(result != null && !result.isAuto()) {
                    if (result.getResCode() == 200) {
                        ToastUtil.showToast(this, "初始化成功");
                        mSetDosingBtn.setEnabled(false);
                        // update local record
                        SharePrefConfig.getInstance().setDosingValue(dosingNo1, MachineMaterialMap.MATERIAL_BOX_1);
                        SharePrefConfig.getInstance().setDosingValue(dosingNo2, MachineMaterialMap.MATERIAL_BOX_2);
                        SharePrefConfig.getInstance().setDosingValue(dosingNo3, MachineMaterialMap.MATERIAL_BOX_3);
                        SharePrefConfig.getInstance().setDosingValue(dosingNo4, MachineMaterialMap.MATERIAL_BOX_4);
                        SharePrefConfig.getInstance().setDosingValue(dosingNo9, MachineMaterialMap.MATERIAL_COFFEE_BEAN);
                        SharePrefConfig.getInstance().setDosingValue(dosingWater, MachineMaterialMap.MATERIAL_WATER);
                        SharePrefConfig.getInstance().setDosingValue(dosingCupNum, MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);

                        SharePrefConfig.getInstance().setDosingInit(true);

                        MyApplication.Instance().setLastSyncStockTime(TimeUtil.getNow_millisecond());

                        WelcomeActivity.start(this);
                        this.finish();
                    }else{
                        ToastUtil.showToast(this, "初始化失败");
                    }
                }
            }
        }
	}
}
