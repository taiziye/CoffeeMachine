package com.netease.vendor.fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.zxing.WriterException;
import com.netease.vendor.R;
import com.netease.vendor.activity.MakeCoffeeActivity;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.barcode.QRCodeEncoder;
import com.netease.vendor.common.dbhelper.CoffeeIndentDbHelper;
import com.netease.vendor.common.fragment.TFragment;
import com.netease.vendor.domain.CoffeeIndentStatus;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.VerifyCoffeeInfo;
import com.netease.vendor.service.bean.result.GeneralActionResult;
import com.netease.vendor.service.bean.result.RollbackResult;
import com.netease.vendor.service.bean.result.VerifyCoffeeResult;
import com.netease.vendor.service.bean.result.VerifyQrcodeResult;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.domain.CoffeeIndent;
import com.netease.vendor.service.domain.CoffeeInfo;
import com.netease.vendor.ui.DigitsEditText;
import com.netease.vendor.ui.ProgressDlgHelper;
import com.netease.vendor.util.CommonUtil;
import com.netease.vendor.util.NetworkUtil;
import com.netease.vendor.util.TimeUtil;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class FetchCoffeeFragment extends TFragment implements OnClickListener{
    
	private ImageView mBackBtn;
    private DigitsEditText mEditText;
    private ImageView mQrcode;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fetch_coffee, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }
    
    private void initView(){
    	mBackBtn = (ImageView) getView().findViewById(R.id.buy_coffee_back_btn);
    	mBackBtn.setOnClickListener(this);
    	mQrcode = (ImageView) getView().findViewById(R.id.fetch_coffee_qrcode);
    	
    	initEditText();
    	initDigitsPad();
    	initQrcode();
    }
    
    private void initEditText(){
    	mEditText = (DigitsEditText) getView().findViewById(R.id.fetch_coffee_input_edit);
    }
    
    private void initQrcode(){
    	Bitmap qrcode = generateQrCodeBitmap();
    	if(qrcode != null){
    		mQrcode.setImageBitmap(qrcode);
    	}
    }
    
    private Bitmap generateQrCodeBitmap() {
		Bitmap bitmap = null;
		String vendorNum = U.getMyVendorNum();
		if (!TextUtils.isEmpty(vendorNum)) {
			try {
				bitmap = QRCodeEncoder.getQrCodeBitmap(getActivity(), vendorNum, 200);
			} catch (WriterException e) {
				LogUtil.e("Fetch Coffee", "createQRCode error:" + e.getMessage());
			}
		}
		return bitmap;
	}
    
    private void initDigitsPad(){
        getView().findViewById(R.id.keyboard_number1).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number2).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number3).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_back).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number4).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number5).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number6).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_clear).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number7).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number8).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number9).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_star).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_number0).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_pound).setOnClickListener(this);
        getView().findViewById(R.id.keyboard_sure).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.keyboard_number1:
                 keyPressed(KeyEvent.KEYCODE_1);
                 break;
            case R.id.keyboard_number2:
                 keyPressed(KeyEvent.KEYCODE_2);
                 break;
            case R.id.keyboard_number3:
                 keyPressed(KeyEvent.KEYCODE_3);
                 break;
            case R.id.keyboard_number4:
                 keyPressed(KeyEvent.KEYCODE_4);
                 break;
            case R.id.keyboard_number5:
                 keyPressed(KeyEvent.KEYCODE_5);
                 break;
            case R.id.keyboard_number6:
                 keyPressed(KeyEvent.KEYCODE_6);
                 break;
            case R.id.keyboard_number7:
                 keyPressed(KeyEvent.KEYCODE_7);
                 break;
            case R.id.keyboard_number8:
                 keyPressed(KeyEvent.KEYCODE_8);
                 break;
            case R.id.keyboard_number9:
                 keyPressed(KeyEvent.KEYCODE_9);
                 break;
            case R.id.keyboard_number0:
                 keyPressed(KeyEvent.KEYCODE_0);
                 break;
            case R.id.keyboard_back:
                 onDelete();
                 break;
            case R.id.keyboard_clear:
            	 onDeleteAll();
                 break;
            case R.id.keyboard_star:
            	 keyPressed(KeyEvent.KEYCODE_STAR);
            	 break;
            case R.id.keyboard_pound:
            	 keyPressed(KeyEvent.KEYCODE_POUND);
                 break;
            case R.id.keyboard_sure:
            	 doVerifyCoffee();
                 break;
            case R.id.buy_coffee_back_btn:
    			 this.getActivity().finish();
    			 break;
            default:
                 break;
        }
    }
    
    private void keyPressed(int keyCode) {
         KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
         mEditText.onKeyDown(keyCode, event);
    }
    
    private void onDeleteAll(){
        final Editable digits = mEditText.getText();
        if(digits != null)
            digits.clear();
    }
    
    private void onDelete(){
        keyPressed(KeyEvent.KEYCODE_DEL);
    }
    
    private void doVerifyCoffee(){
    	// check the network
    	if(!NetworkUtil.isNetAvailable(getActivity())){
            ToastUtil.showToast(getActivity(), R.string.network_failed_unavailable);
    		return;
    	}
    	// check the fetch code effectiveness
    	String coffeeIndent = mEditText.getText().toString();
    	if(TextUtils.isEmpty(coffeeIndent)){
    		ToastUtil.showToast(getActivity(), getString(R.string.verify_code_is_null));
    		return;
    	}
        // check if already make coffee
    	boolean isNeedVerify = true;
    	CoffeeIndent dbIndent = CoffeeIndentDbHelper.getCoffeeIndent(coffeeIndent);
    	if(dbIndent != null){
    		int status = dbIndent.getStatus();
    		switch(status){
    		case CoffeeIndent.STATUS_COFFEE_DONE:
                ToastUtil.showToast(getActivity(), R.string.exchange_error_have_fetch);
    			isNeedVerify = false;
    			break;
    		default:
    			break;
    		}
    	}
    	// request verify
    	if(isNeedVerify){
    		// check the old status
    		CoffeeIndentStatus oldStatus = MyApplication.Instance().getIndentStatus(coffeeIndent);
    		if(oldStatus != null && oldStatus.getStatus() == CoffeeIndentStatus.INDENT_STATUS_REQUESTING){
                ToastUtil.showToast(getActivity(), R.string.fetch_is_requesting);
    			return;
    		}
    		
    		// set the status is requesting
    		long timestamp = TimeUtil.getNow_millisecond();
    		if(oldStatus != null){
    			oldStatus.setStatus(CoffeeIndentStatus.INDENT_STATUS_REQUESTING);
    		}else{
    			CoffeeIndentStatus newStatus = new CoffeeIndentStatus();
    			newStatus.setCoffeeIndent(coffeeIndent);
    			newStatus.setStatus(CoffeeIndentStatus.INDENT_STATUS_REQUESTING);
    			newStatus.setTimestamp(timestamp);
    			MyApplication.Instance().addIndentStatus(newStatus);
    		}

    		// send the request to server
    		ProgressDlgHelper.showProgress(getActivity(), "正在验证");
    		VerifyCoffeeInfo info = new VerifyCoffeeInfo();
        	info.setUid(U.getMyVendorNum());
        	info.setCoffeeIndent(coffeeIndent);
        	info.setTimestamp(timestamp);
        	info.setRetry(false);
        	execute(info.toRemote());
    	}
    }

	@Override
	public void onReceive(Remote remote) {
		if (remote.getWhat() == ITranCode.ACT_COFFEE) {
			if (remote.getAction() == ITranCode.ACT_COFFEE_VERIFY_COFFEE) { // 取货码取货
				ProgressDlgHelper.closeProgress();
				VerifyCoffeeResult result = GeneralActionResult.parseObject(remote.getBody());
				if(result != null && result.getResCode() == 200){
					String coffeeIndent = result.getCoffeeIndent();
                    int coffeeId = result.getCoffeeId();
                    ArrayList<CoffeeDosingInfo> dosingList = result.getDosingList();
                    if(dosingList != null){
                        // get the base dosing list
                        ArrayList<CoffeeDosingInfo> baseDosingList = null;
                        List<CoffeeInfo> coffeeInfos = MyApplication.Instance().getCoffeeInfos();
                        if(coffeeInfos != null){
                            for(CoffeeInfo info : coffeeInfos){
                                if(info.getCoffeeId() == coffeeId){
                                    baseDosingList = info.getDosingList();
                                    break;
                                }
                            }
                        }

                        if(baseDosingList != null){
                            // update the value
                            for(int i = 0; i < dosingList.size(); i++){
                                CoffeeDosingInfo inc = dosingList.get(i);
                                for(int j = 0; j < baseDosingList.size(); j++){
                                    CoffeeDosingInfo base = baseDosingList.get(j);
                                    int baseID = base.getId();
                                    String baseName = base.getName();
                                    int baseOrder = base.getOrder();
                                    int baseEjection = base.getEjection();
                                    int baseStirvol = base.getStirvol();
                                    int baseSirtime = base.getStirtime();
                                    if(baseID == inc.getId()){
                                        inc.setName(baseName);
                                        inc.setOrder(baseOrder);
                                        inc.setEjection(baseEjection);
                                        inc.setStirvol(baseStirvol);
                                        inc.setStirtime(baseSirtime);
                                        break;
                                    }
                                }
                            }
                            // switch to make coffee
                            MakeCoffeeActivity.start(getActivity(), coffeeIndent, coffeeId, dosingList, true);
                        }else{
                            LogUtil.e("FetchCoffeeByCode", "ERROR: baseDosingList is null.");
                        }
                    }else{
                        LogUtil.e("FetchCoffeeByCode", "ERROR: dosingList is null.");
                    }
				}else{
                    ToastUtil.showToast(getActivity(), parseError(result.getResCode()));
				}	
			}
			else if(remote.getAction() == ITranCode.ACT_COFFEE_VERIFY_QRCODE){ // 二维码取货
				VerifyQrcodeResult result = GeneralActionResult.parseObject(remote.getBody());
				if(result != null && result.getResCode() == 200){
                    String coffeeIndent = result.getCoffeeIndent();
                    int coffeeId = result.getCoffeeId();
                    ArrayList<CoffeeDosingInfo> dosingList = result.getDosingList();
                    if(dosingList != null){
                        // get the base dosing list
                        ArrayList<CoffeeDosingInfo> baseDosingList = null;
                        List<CoffeeInfo> coffeeInfos = MyApplication.Instance().getCoffeeInfos();
                        if(coffeeInfos != null){
                            for(CoffeeInfo info : coffeeInfos){
                                if(info.getCoffeeId() == coffeeId){
                                    baseDosingList = info.getDosingList();
                                    break;
                                }
                            }
                        }

                        if(baseDosingList != null){
                            // update the value
                            for(int i = 0; i < dosingList.size(); i++){
                                CoffeeDosingInfo inc = dosingList.get(i);
                                for(int j = 0; j < baseDosingList.size(); j++){
                                    CoffeeDosingInfo base = baseDosingList.get(j);
                                    int baseID = base.getId();
                                    String baseName = base.getName();
                                    int baseOrder = base.getOrder();
                                    int baseEjection = base.getEjection();
                                    int baseStirvol = base.getStirvol();
                                    int baseSirtime = base.getStirtime();
                                    if(baseID == inc.getId()){
                                        inc.setName(baseName);
                                        inc.setOrder(baseOrder);
                                        inc.setEjection(baseEjection);
                                        inc.setStirvol(baseStirvol);
                                        inc.setStirtime(baseSirtime);
                                        break;
                                    }
                                }
                            }
                            // switch to make coffee
                            MakeCoffeeActivity.start(getActivity(), coffeeIndent, coffeeId, dosingList,true);
                        }else{
                            LogUtil.e("vendor", "ACT_COFFEE_VERIFY_QRCODE ERROR: baseDosingList is null.");
                        }
                    }else{
                        LogUtil.e("vendor", "ACT_COFFEE_VERIFY_QRCODE ERROR: dosingList is null.");
                    }
                }else{
                    ToastUtil.showToast(getActivity(), parseError(result.getResCode()));
                }
			}
			else if(remote.getAction() == ITranCode.ACT_COFFEE_INDENT_ROLLBACK){
				RollbackResult result = GeneralActionResult.parseObject(remote.getBody());
				if(result != null){
					String coffeeIndent = result.getCoffeeIndent();
					boolean isRetry = result.isRetry();
					if(!isRetry){
						// add to cache
						long timestamp = TimeUtil.getNow_millisecond();
						CoffeeIndentStatus newStatus = new CoffeeIndentStatus();
		    			newStatus.setCoffeeIndent(coffeeIndent);
		    			newStatus.setStatus(CoffeeIndentStatus.INDENT_STATUS_REQUESTING);
		    			newStatus.setTimestamp(timestamp);
		    			MyApplication.Instance().addIndentStatus(newStatus);
		    			// send the request to server
		    			ProgressDlgHelper.showProgress(getActivity(), "重新验证");
		        		VerifyCoffeeInfo info = new VerifyCoffeeInfo();
		            	info.setUid(U.getMyVendorNum());
		            	info.setCoffeeIndent(coffeeIndent);
		            	info.setTimestamp(timestamp);
		            	info.setRetry(true);
		            	execute(info.toRemote());
					}
				}
			}
			else if(remote.getAction() == ITranCode.ACT_COFFEE_INDENT_TIME_OUT){
				ProgressDlgHelper.closeProgress();

			}
		}
	}  
	
	private int parseError(int resCode){
		int resId = 0;
		switch(resCode){
		case 301:
			resId = R.string.exchange_error_no_coffeeindent;
			break;
		case 302:
			resId = R.string.exchange_error_have_fetch;
			break;
		case 303:
			resId = R.string.exchange_error_system;
			break;
		case 306:
			resId = R.string.exchange_error_no_pay;
			break;
		case 307:
			resId = R.string.exchange_error_drawback;
			break;
		case 308:
			resId = R.string.exchange_error_have_fetch_by_other_mac;
			break;
		default:
			resId = R.string.exchange_error_default;
			break;
		}
		
		return resId;
	}	
}
