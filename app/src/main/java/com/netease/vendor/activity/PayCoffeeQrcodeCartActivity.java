package com.netease.vendor.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.vendor.R;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.barcode.QRCodeEncoder;
import com.netease.vendor.common.action.TActivity;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.StatusChangeNotify;
import com.netease.vendor.service.bean.action.CancelTradeCartInfo;
import com.netease.vendor.service.bean.action.PayQrcodeCartInfo;
import com.netease.vendor.service.bean.action.PayStatusAskCartInfo;
import com.netease.vendor.service.bean.result.GeneralActionResult;
import com.netease.vendor.service.bean.result.PayNotifyResult;
import com.netease.vendor.service.bean.result.PayQrcodeCartResult;
import com.netease.vendor.service.bean.result.PayStatusAskCartResult;
import com.netease.vendor.service.domain.Ancestor;
import com.netease.vendor.service.protocol.ResponseCode;
import com.netease.vendor.ui.ProgressDlgHelper;
import com.netease.vendor.util.AudioPlayer;
import com.netease.vendor.util.CountDownTimer;
import com.netease.vendor.util.CountDownTimer.CountDownCallback;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PayCoffeeQrcodeCartActivity extends TActivity implements OnClickListener {

	public static final String TAG = "PayCoffeeQrcodeCart";

	public static final String COFFEE_INDENTS = "coffee_indents";
	public static final String COFFEE_PAY_METHOD = "coffee_pay";
	
	private TextView mHomeMacNo;
	private ImageView mPayBack;
	private TextView mPayTimer;

	private TextView mPayMethodTip;
	private ImageView mPayOperationTip;
	private TextView mGetCoffeeProcess;
	private ImageView mPayQrcode;

    private LinearLayout mPayDetailParent;
	private TextView mPayDetail;
    
    private String mCoffeeIndents;
    private int mPayMethod;
    
    private CountDownTimer countDownTimer;

    private String mPayIndent;
	private String mPayCoffeeIndents;

    private AtomicInteger payAction = new AtomicInteger(0);
    private AtomicBoolean payStatusRetry = new AtomicBoolean(false);

    public static void start(Activity activity, String coffeeIndents, int payMethod) {
		Intent intent = new Intent();
		intent.setClass(activity, PayCoffeeQrcodeCartActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(COFFEE_INDENTS, coffeeIndents);
		intent.putExtra(COFFEE_PAY_METHOD, payMethod);
		activity.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_coffee_qrcode_layout);
		proceedExtra();
		
		initViews();
		initPay();
		
		requestPay();
	}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.vendor(TAG + "->onNewIntent");
    }
	
	private void proceedExtra(){
		Intent intent = getIntent();
        if(intent != null){
            mCoffeeIndents = intent.getStringExtra(COFFEE_INDENTS);
            mPayMethod = intent.getIntExtra(COFFEE_PAY_METHOD, 1);
        }
	}

	private void initViews(){	
		mHomeMacNo = (TextView) findViewById(R.id.home_title_machine_num);
		mPayBack = (ImageView) findViewById(R.id.pay_qrcode_back_btn);
		mPayTimer = (TextView) findViewById(R.id.pay_qrcode_timer);
		mHomeMacNo.setText(String.format(getString(R.string.home_coffee_machine_no), U.getMyVendorName()));
		mPayBack.setOnClickListener(this);
		
		mPayMethodTip = (TextView) findViewById(R.id.pay_coffee_method_tip);
		mPayOperationTip = (ImageView) findViewById(R.id.pay_coffee_operation_tip);
		mGetCoffeeProcess = (TextView) findViewById(R.id.pay_coffee_process_tip);
		mPayQrcode = (ImageView) findViewById(R.id.pay_coffee_qrcode);

		mPayDetailParent = (LinearLayout) findViewById(R.id.pay_coffee_detail_linear);
		mPayDetail = (TextView) findViewById(R.id.pay_coffee_detail);
	}

	private void initPay(){
        mGetCoffeeProcess.setText(R.string.pay_generate_qrcode);
        onChangePayMethod(mPayMethod);
        countDownTimer = new CountDownTimer(new CountDownCallback() {

            @Override
            public void currentInterval(int value) {
                onCountDown(value);
            }
        });
        countDownTimer.startCountDownTimer(90, 1000, 1000);
    }
	
	private void onChangePayMethod(int payMethod) {
		if(payMethod == PayMethod.AliQr.tag){
			mPayMethodTip.setText(R.string.pay_ali_qrcode_tips);
			mPayOperationTip.setImageResource(R.drawable.pay_ali_qrcode_instruction);
		}else if(payMethod == PayMethod.WeiXin.tag){
			mPayMethodTip.setText(R.string.pay_weixin_qrcode_tips);
			mPayOperationTip.setImageResource(R.drawable.pay_wechat_instruction);
		}else{
			LogUtil.vendor("unknown pay method");
		}
	}
	
	private void onCountDown(int value){
		mPayTimer.setText(String.format(this.getString(R.string.pay_timer_tip), value));
		if(payAction.get() == 0  && (value == 30 || value == 60 )){
            askPayStatus(mPayIndent);
		}
		
		if(value <= 0){
            cancelTrade();
			AudioPlayer.getInstance().stop();
			WelcomeActivity.start(this);
			finish();
		}
	}
	
	private void requestPay(){
		if (!isReachable()) {
			ToastUtil.showToast(PayCoffeeQrcodeCartActivity.this, R.string.network_is_not_available);
			return;
		}

		ProgressDlgHelper.showProgress(this, "正在请求二维码");

		PayQrcodeCartInfo info = new PayQrcodeCartInfo();
		info.setUid(U.getMyVendorNum());
		info.setCoffeeIndents(mCoffeeIndents);
		info.setProvider((short) mPayMethod);
		execute(info.toRemote());
	}

    private boolean isReachable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    private void askPayStatus(String coffeeIndent){
        if(!TextUtils.isEmpty(coffeeIndent)){
			PayStatusAskCartInfo info = new PayStatusAskCartInfo();
            info.setUid(U.getMyVendorNum());
            info.setPayIndent(mPayIndent);
            execute(info.toRemote());
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.pay_qrcode_back_btn:
            if(payAction.compareAndSet(0, 2)){
                if(countDownTimer != null){
                    countDownTimer.cancelCountDownTimer();
                }
				cancelTrade();
				AudioPlayer.getInstance().stop();
                this.finish();
            }
			break;
		default:
			break;
		}
	}

    public void cancelTrade(){
        if(!TextUtils.isEmpty(mPayIndent) ){
			CancelTradeCartInfo info = new CancelTradeCartInfo();
            info.setUid(U.getMyVendorNum());
            info.setPayIndent(mPayIndent);
			execute(info.toRemote());
        }
    }
	
	@Override
	public void onReceive(Remote remote) {
        // system action
        if (remote.getWhat() == ITranCode.ACT_SYS) {
            if (remote.getAction() == ITranCode.ACT_SYS_STATUS_CHANGE) {
                StatusChangeNotify notify = Ancestor.parseObject(remote.getBody());
                if(notify != null){
                    int status = notify.getStatus();
                    LogUtil.vendor(TAG + ": network status is " + status );
                    if(status == ITranCode.STATUS_LOGINED){
                        if(!TextUtils.isEmpty(mPayIndent) && payStatusRetry.compareAndSet(false, true)){
                            LogUtil.vendor(TAG + ": network is establish, so we ask pay status right now: " + mPayIndent);
                            askPayStatus(mPayIndent);
                        }
                    }
                }
            }
        }
		// coffee action
		if (remote.getWhat() == ITranCode.ACT_COFFEE) {
			if(remote.getAction() == ITranCode.ACT_COFFEE_PAY_QRCODE_CART){
				ProgressDlgHelper.closeProgress();
				PayQrcodeCartResult result = GeneralActionResult.parseObject(remote.getBody());
                if(result != null){
                    if(result.getResCode() == 200){
                        onReceivePayQRCode(result);
                    }else{
                        ToastUtil.showToast(this, parseError(result.getResCode()));
                    }
                }
			}else if(remote.getAction() == ITranCode.ACT_COFFEE_PAY_NOTIFY){
				PayNotifyResult result = GeneralActionResult.parseObject(remote.getBody());
				if(result != null){
					if(result.getResCode() == 200){
						doPaySuccessful();
					}else{
                        ToastUtil.showToast(this, parseError(result.getResCode()));
					}
				}
			}else if(remote.getAction() == ITranCode.ACT_COFFEE_ASK_CART_PAY_RESULT){
				PayStatusAskCartResult result = GeneralActionResult.parseObject(remote.getBody());
				if(result != null && result.getResCode() == 200){
                    String indent = result.getPayIndent();
                    if(!TextUtils.isEmpty(mPayIndent) && !TextUtils.isEmpty(indent)
                            && mPayIndent.equals(indent)){
                        doPaySuccessful();
                    }
				}
			}
		}
	}

	private void doPaySuccessful(){
		MyApplication.Instance().clearCartPay();
        LogUtil.vendor("doPaySuccessful() prepare");
		if(payAction.compareAndSet(0, 1)){
            LogUtil.vendor("doPaySuccessful() start");

			mPayTimer.setText("");
			mGetCoffeeProcess.setText(R.string.pay_pay_success);
			if(countDownTimer != null){
				countDownTimer.cancelCountDownTimer();
			}

			AudioPlayer.getInstance().stop();
			MakeCoffeeCartActivity.start(this, mPayCoffeeIndents, mPayIndent);
			finish();
		}
	}

	private void onReceivePayQRCode(PayQrcodeCartResult result){
		mPayIndent = result.getPayIndent();
		mPayCoffeeIndents = result.getCoffeeIndents();
		// show tips
		mGetCoffeeProcess.setText(R.string.pay_waiting_scan);
		if(mPayMethod == PayMethod.AliQr.tag){
			AudioPlayer.getInstance().play(this, R.raw.sound_pay_by_alipay);
		}else if(mPayMethod == PayMethod.WeiXin.tag){
			AudioPlayer.getInstance().play(this, R.raw.sound_pay_by_wechat);
		}
        // show qrcode
        String qrcodeURL = result.getQrCodeUrl();
        if(!TextUtils.isEmpty(qrcodeURL)){
            Bitmap qrcodeAndroid = generateQrCodeBitmap(qrcodeURL, 280);
            if(qrcodeAndroid != null){
                mPayQrcode.setImageBitmap(qrcodeAndroid);
            }
        }
        // show price
		mPayDetailParent.setVisibility(View.VISIBLE);
		double priceOri = Double.parseDouble(result.getPriceOri());
		double price = Double.parseDouble(result.getPrice());
		double favour = priceOri - price;
		String detailTip;
		if(favour > 0){
			detailTip = String.format(Locale.getDefault(), getString(R.string.pay_qrcode_detail_tip), favour, price);
		}else{
			detailTip = String.format(Locale.getDefault(), getString(R.string.pay_qrcode_detail_tip_no_favour), price);
		}
		mPayDetail.setText(detailTip);
	}

	private Bitmap generateQrCodeBitmap(String content, int size) {
		Bitmap bitmap = null;
		try {
			if(!TextUtils.isEmpty(content)){
				bitmap = QRCodeEncoder.getQrCodeBitmap(this, content, size);
			}
		} catch (Exception e) {
			LogUtil.e("PayCoffeeCart", "createQRCode error:" + e.getMessage());
		}
		return bitmap;
	}
	
	private int parseError(int resCode){
		int resId = 0;
		switch(resCode){
		case 501:
			resId = R.string.pay_error_waiting_pay;
			break;
		case 502:
			resId = R.string.pay_error_cancel_indent;
			break;
        case ResponseCode.RES_ETIMEOUT:
            resId = R.string.pay_error_timeout;
            break;
        case 403:
            resId = R.string.exchange_error_dosing_not_enough;
            break;
		default:
			resId = R.string.exchange_error_default;
			break;
		}
		
		return resId;
	}
	
	public enum PayMethod {
		AliQr(1),
		WeiXin(2),
		AliWa(3),
		;
		
		int tag;
		
		PayMethod(int tag) {
			this.tag = tag;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ProgressDlgHelper.closeProgress();
	}
}
