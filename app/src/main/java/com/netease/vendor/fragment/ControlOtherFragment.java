package com.netease.vendor.fragment;

import com.netease.vendor.R;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.common.action.TViewWatcher;
import com.netease.vendor.common.fragment.TFragment;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.MachineStatusReportInfo;
import com.netease.vendor.service.bean.action.VerifyPasswordInfo;
import com.netease.vendor.service.bean.result.VerifyPasswordResult;
import com.netease.vendor.service.domain.Ancestor;
import com.netease.vendor.service.protocol.MachineStatusCode;
import com.netease.vendor.ui.EasyAlertDialogForSure;
import com.netease.vendor.ui.ControlVeriPwdDialog;
import com.netease.vendor.ui.GenericSettingDialog;
import com.netease.vendor.util.SharePrefConfig;
import com.netease.vendor.util.TimeUtil;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;
//import com.netease.vendor.util.multicard.SysUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ControlOtherFragment extends TFragment implements OnClickListener, EasyAlertDialogForSure.OnDialogListener,
        ControlVeriPwdDialog.OnVerifyPwdDialogListener{

    public static final String WASHTIME_PREFERENCES_SET = "清洗时间";

    private Button mMachineLogout;
    private Button mMoveBackground;
    private Button mFixError;
    private Button mSetWashTime;
    private TextView mAppVersion;
    
	public ControlOtherFragment() {
		this.setFragmentId(R.id.other_fragment);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_other, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }
    
    private void initView(){
        mMachineLogout = (Button) getView().findViewById(R.id.coffee_machine_logout);
        mMachineLogout.setOnClickListener(this);

        mMoveBackground = (Button) getView().findViewById(R.id.coffee_machine_move_background);
        mMoveBackground.setOnClickListener(this);

        mFixError = (Button) getView().findViewById(R.id.coffee_machine_fix_error);
        mFixError.setOnClickListener(this);

        mSetWashTime = (Button) getView().findViewById(R.id.coffee_machine_set_wash_time);
        mSetWashTime.setOnClickListener(mSetWashTimeOnClick);

        mAppVersion = (TextView) getView().findViewById(R.id.app_version);
        //mAppVersion.setText(SysUtil.getVersion(getActivity()));
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
            case R.id.coffee_machine_logout:
                ControlVeriPwdDialog veriPwdDialog4Logout = new ControlVeriPwdDialog(getActivity(), this,
                        VerifyPasswordInfo.TYPE_LOGOUT);
                veriPwdDialog4Logout.show();
                break;
            case R.id.coffee_machine_move_background:
                ControlVeriPwdDialog veriPwdDialog4Background = new ControlVeriPwdDialog(getActivity(), this,
                        VerifyPasswordInfo.TYPE_MOVE_BACKGROUND);
                veriPwdDialog4Background.show();
                break;
            case R.id.coffee_machine_fix_error:
                String message = getString(R.string.control_fix_error_sure);
                EasyAlertDialogForSure dialog = new EasyAlertDialogForSure(getActivity(), this, false, message);
                dialog.show();
                break;
        }
	}

    private OnClickListener mSetWashTimeOnClick = new OnClickListener() {

        private Map<String, String> map;

        @Override
        public void onClick(View v) {
            map = new HashMap<String, String>();
            StringBuilder sb = new StringBuilder();
            for(String time : MyApplication.Instance().mGlobalWashTimeSet){
                sb.append(",");
                sb.append(time);
            }
            if(sb.length()>0 &&sb.charAt(0)==','){
                sb.deleteCharAt(0);
            }
            map.put(WASHTIME_PREFERENCES_SET, sb.toString());

            GenericSettingDialog dialog = new GenericSettingDialog(
                    getActivity(), map,
                    new GenericSettingDialog.OnGenericSettingDialog() {

                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public boolean onConfirm(Map<String, String> resultMap) {
                            try{
                                String tmpTimes = resultMap.get(WASHTIME_PREFERENCES_SET);
                                String[] times = tmpTimes.split(",");
                                for(String time:times){
                                    if(!time.matches("[0-9]{2}:[0-9]{2}")){
                                        throw new Exception();
                                    }
                                }
                                MyApplication.Instance().mGlobalWashTimeSet.clear();
                                for(String time:times){
                                    MyApplication.Instance().mGlobalWashTimeSet.add(time);
                                }
                                SharePrefConfig.getInstance().setWashTime(MyApplication.Instance().mGlobalWashTimeSet);

                                ToastUtil.showToast(getActivity(), MyApplication.Instance().mGlobalWashTimeSet.toString());
                                return true;
                            }catch(Exception e){
                                ToastUtil.showToast(getActivity(), "输入格式错误");
                            }
                            return false;
                        }
                    });
            dialog.show();
        }

    };

    @Override
    public void OnDialogCancel() {
        //DO NOTHING
    }

    @Override
    public void OnDialogConfirm() {
        // report server
        List<Integer> status = new ArrayList<Integer>();
        MachineStatusReportInfo info = new MachineStatusReportInfo();
        info.setUid(U.getMyVendorNum());
        info.setTimestamp(TimeUtil.getNow_millisecond());
        status.add(MachineStatusCode.SUCCESS);
        info.setStatus(status);
        execute(info.toRemote());
    }


    @Override
    public void verifyPwdCancel() {
        //DO NOTHING
    }

    @Override
    public void verifyPwdConfirm(String password, int type) {
        VerifyPasswordInfo info = new VerifyPasswordInfo();
        info.setPassword(password);
        info.setType(type);
        execute(info.toRemote());
    }

    @Override
    public void onReceive(Remote remote) {
        if (remote.getWhat() == ITranCode.ACT_USER) {
            if (remote.getAction() == ITranCode.ACT_USER_VERIFY_PWD) {
                LogUtil.vendor("onReceive -> ACT_USER_VERIFY_PWD");
                VerifyPasswordResult result = Ancestor.parseObject(remote.getBody());
                if(result != null && result.isCorrect()){
                    if(result.getType() == VerifyPasswordInfo.TYPE_LOGOUT){
                        Remote logout = new Remote();
                        logout.setAction(ITranCode.ACT_USER_LOGOUT);
                        logout.setWhat(ITranCode.ACT_USER);
                        TViewWatcher.newInstance().send(logout);//这里在Fragment里面讲数据提交到远程服务
                    }else if(result.getType() == VerifyPasswordInfo.TYPE_MOVE_BACKGROUND){
                        getActivity().moveTaskToBack(true);
                    }
                }else{
                    ToastUtil.showToast(getActivity(), "密码验证错误");
                }
            }
        }
    }

}
