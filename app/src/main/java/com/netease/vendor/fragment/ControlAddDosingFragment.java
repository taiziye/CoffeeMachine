package com.netease.vendor.fragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.vendor.R;
import com.netease.vendor.common.fragment.TFragment;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.AddStockInfo;
import com.netease.vendor.service.bean.action.GetDosingListInfo;
import com.netease.vendor.service.bean.result.AddStockResult;
import com.netease.vendor.service.bean.result.GeneralActionResult;
import com.netease.vendor.service.bean.result.GetDosingResult;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.protocol.MachineMaterialMap;
import com.netease.vendor.ui.ControlAddStockDialog;
import com.netease.vendor.ui.ControlAddStockDialog.OnAddStockDialogListener;
import com.netease.vendor.ui.ProgressDlgHelper;
import com.netease.vendor.util.SharePrefConfig;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.U;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class ControlAddDosingFragment extends TFragment implements OnClickListener{

    private String dosingWater;
    private String dosingCupNum;
    private String dosingNo1;
    private String dosingNo2;
    private String dosingNo3;
    private String dosingNo4;
    private String dosingNo9;

    private EditText mSetDosingWater;
    private EditText mSetDosingCupNum;
    private EditText mSetDosingNo1;
    private EditText mSetDosingNo2;
    private EditText mSetDosingNo3;
    private EditText mSetDosingNo4;
    private EditText mSetDosingNo9;

    private Button mUpdateDosingBtn;
	
	public ControlAddDosingFragment() {
		this.setFragmentId(R.id.add_dosing_fragment);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_add_dosing, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initOrUpdateStock();
    }
    
    private void initView(){
        mSetDosingWater = (EditText) getView().findViewById(R.id.set_dosing_water);
        mSetDosingWater.setEnabled(false);
        mSetDosingCupNum = (EditText) getView().findViewById(R.id.set_dosing_cup);
        mSetDosingCupNum.setEnabled(false);
        mSetDosingNo1 = (EditText) getView().findViewById(R.id.set_dosing_no1);
        mSetDosingNo1.setEnabled(false);
        mSetDosingNo2 = (EditText) getView().findViewById(R.id.set_dosing_no2);
        mSetDosingNo2.setEnabled(false);
        mSetDosingNo3 = (EditText) getView().findViewById(R.id.set_dosing_no3);
        mSetDosingNo3.setEnabled(false);
        mSetDosingNo4 = (EditText) getView().findViewById(R.id.set_dosing_no4);
        mSetDosingNo4.setEnabled(false);
        mSetDosingNo9 = (EditText) getView().findViewById(R.id.set_dosing_no9);
        mSetDosingNo9.setEnabled(false);

        mUpdateDosingBtn = (Button) getView().findViewById(R.id.control_update_dosing);
        mUpdateDosingBtn.setOnClickListener(this);
    }

    private void initOrUpdateStock(){
        mSetDosingWater.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER));
        mSetDosingCupNum.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM));
        mSetDosingNo1.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1));
        mSetDosingNo2.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2));
        mSetDosingNo3.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3));
        mSetDosingNo4.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4));
        mSetDosingNo9.setText("" + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.control_update_dosing:
                updateStock();
                break;
        }
    }

    private void updateStock(){
        ControlAddStockDialog dialog = new ControlAddStockDialog(getActivity(), addStockDialogListener);
        dialog.show();
    }

    private OnAddStockDialogListener addStockDialogListener = new OnAddStockDialogListener(){

        @Override
        public void addStockCancel() {
            //DO NOTHING
        }

        @Override
        public void addStockConfirm(String water, String cupNum, String no1, String no2, String no3,
                                    String no4, String no9) {
            dosingWater = water;
            dosingCupNum = cupNum;
            dosingNo1 = no1;
            dosingNo2 = no2;
            dosingNo3 = no3;
            dosingNo4 = no4;
            dosingNo9 = no9;

            getCoffeeDosingList();
        }
    };

    private void getCoffeeDosingList(){
        ProgressDlgHelper.showProgress(getActivity(), "获取配料列表");
        GetDosingListInfo info = new GetDosingListInfo();
        info.setUid(U.getMyVendorNum());
        execute(info.toRemote());
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_COFFEE) {
            if (remote.getAction() == ITranCode.ACT_COFFEE_DOSING_LIST) {
                ProgressDlgHelper.closeProgress();
                GetDosingResult result = GeneralActionResult.parseObject(remote.getBody());
                if(result != null && !result.isAuto()){
                    if(result.getResCode() == 200){
                        doUpdateDosing(result.getDosings());
                    }else{
                        ToastUtil.showToast(getActivity(), "获取配料失败");
                    }
                }
            }else if(remote.getAction() == ITranCode.ACT_COFFEE_STOCK_ADD){
                ProgressDlgHelper.closeProgress();
                AddStockResult result = GeneralActionResult.parseObject(remote.getBody());
                if(result != null ) {
                    if (result.getResCode() == 200) {
                        ToastUtil.showToast(getActivity(), "添加物料成功");

                        // update local record
                        double water = Double.parseDouble(dosingWater) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(water),  MachineMaterialMap.MATERIAL_WATER);
                        double cupNum = Double.parseDouble(dosingCupNum) +  SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(cupNum), MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
                        double dosing1 = Double.parseDouble(dosingNo1) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing1), MachineMaterialMap.MATERIAL_BOX_1);
                        double dosing2 = Double.parseDouble(dosingNo2) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing2), MachineMaterialMap.MATERIAL_BOX_2);
                        double dosing3 = Double.parseDouble(dosingNo3) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing3), MachineMaterialMap.MATERIAL_BOX_3);
                        double dosing4 = Double.parseDouble(dosingNo4) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing4), MachineMaterialMap.MATERIAL_BOX_4);
                        double dosing9 = Double.parseDouble(dosingNo9) + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
                        SharePrefConfig.getInstance().setDosingValue(String.valueOf(dosing9), MachineMaterialMap.MATERIAL_COFFEE_BEAN);

                        //update ui
                        initOrUpdateStock();
                    }else{
                        ToastUtil.showToast(getActivity(), "添加物料失败");
                    }
                }
            }
        }
    }

    private void doUpdateDosing(List<CoffeeDosingInfo> dosings){
        JSONArray array = new JSONArray();

        for(CoffeeDosingInfo info : dosings){
            double totalValue = 0;
            double addValue = 0;

            if(info.getId() == 1){
                addValue = TextUtils.isEmpty(dosingWater) ? 0 : Double.parseDouble(dosingWater);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
            }else if(info.getId() == 2){
                addValue = TextUtils.isEmpty(dosingCupNum) ? 0 : Double.parseDouble(dosingCupNum);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_1){
                addValue = TextUtils.isEmpty(dosingNo1) ? 0 : Double.parseDouble(dosingNo1);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_2){
                addValue = TextUtils.isEmpty(dosingNo2) ? 0 : Double.parseDouble(dosingNo2);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_3){
                addValue = TextUtils.isEmpty(dosingNo3) ? 0 : Double.parseDouble(dosingNo3);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_BOX_4){
                addValue = TextUtils.isEmpty(dosingNo4) ? 0 : Double.parseDouble(dosingNo4);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
            }else if(info.getBoxID() == MachineMaterialMap.MATERIAL_COFFEE_BEAN){
                addValue = TextUtils.isEmpty(dosingNo9) ? 0 : Double.parseDouble(dosingNo9);
                totalValue = addValue + SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
            }

            if(addValue <= 0)
                continue;

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", info.getId());
            jsonObj.put("add_value", addValue);
            jsonObj.put("total_value", totalValue);
            array.add(jsonObj);
        }

        ProgressDlgHelper.showProgress(getActivity(), "更新配料库存");
        AddStockInfo info = new AddStockInfo();
        info.setUid(U.getMyVendorNum());
        info.setUserID(1);
        info.setInventory(array.toString());
        execute(info.toRemote());
    }
}
