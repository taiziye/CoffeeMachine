package com.netease.vendor.activity;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.netease.vendor.R;
import com.netease.vendor.common.action.TActivity;
import com.netease.vendor.common.action.TViewWatcher;
import com.netease.vendor.helper.dialog.EasyAlertDialog;
import com.netease.vendor.net.http.MD5;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.LoginRequestInfo;
import com.netease.vendor.service.bean.result.GeneralActionResult;
import com.netease.vendor.service.bean.result.LoginResult;
import com.netease.vendor.service.protocol.ResponseCode;
import com.netease.vendor.ui.ClearableEditText;
import com.netease.vendor.ui.ProgressDlgHelper;
import com.netease.vendor.util.SharePrefConfig;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.U;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends TActivity implements OnClickListener, TextWatcher{

	private Context mContext;
	
	private ClearableEditText mLoginTxtAccount;
	private ClearableEditText mLoginTxtPassword;
	private Button mEntranceLogin;
	
	private String mUserAccount;
	private String mUserPassword;
	
	private boolean foreground;
	
	private Timer disableTimer;

    private EasyAlertDialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		mContext = this;
        proceedExtras();
		
		setupLoginPage();

        checkBeKickout();
	}
	
	private void proceedExtras(){
    }
	
	private void setupLoginPage(){
		mLoginTxtAccount = (ClearableEditText) findViewById(R.id.login_account);
		mLoginTxtAccount.addTextChangedListener(this);
		mLoginTxtPassword = (ClearableEditText) findViewById(R.id.login_password);
		
		mEntranceLogin = (Button) findViewById(R.id.entrance_login);
		mEntranceLogin.setClickable(true);
		mEntranceLogin.setOnClickListener(this);

		showKeyboard(mLoginTxtAccount);
	}

    private void checkBeKickout(){
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;

        if (bundle.getInt("status", -1) == ITranCode.STATUS_KICKOUT) {
            OnClickListener clickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.EasyDialogPositiveBtn) {
                        mDialog.dismiss();
                    }
                }
            };
            String title = getString(R.string.network_kick_title);
            String message = getString(R.string.network_need_verify_again);
            String ok = getString(R.string.network_kick_i_know);

            mDialog = EasyAlertDialog.show(this,R.layout.easyalert_dialog_cleardata_layout, title, message, ok,
                    clickListener);
        }
    }

	@Override
	public void onClick(View v) {		
		switch(v.getId()){
		case R.id.entrance_login:
			if (!isReachable()) {
				ToastUtil.showToast(LoginActivity.this, R.string.network_is_not_available);
				return;
			}
			hideKeyboard();
			doLogin();
			break;
		default:
			break;
		}
	}

	private boolean isReachable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return (info != null && info.isAvailable());
	}
	
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEntranceLogin.getWindowToken(), 0);
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
	
	private void sendToService(Remote remote) {
		TViewWatcher.newInstance().send(remote);
	}
	
	private void doLogin(){	
		mUserAccount = mLoginTxtAccount.getText().toString();
		mUserPassword = mLoginTxtPassword.getText().toString();

		if(!isValidStr(mUserAccount)){
			ToastUtil.showToast(mContext, R.string.login_account_is_null);
			showKeyboard(mLoginTxtAccount);
			return;
		}
		
		if(!isValidStr(mUserPassword)){
			ToastUtil.showToast(mContext, R.string.login_password_is_null);
			showKeyboard(mLoginTxtPassword);
			return;
		}
		
		LoginRequestInfo info = new LoginRequestInfo();
		info.setUid(mUserAccount);
		info.setPassword(MD5.md5(mUserPassword));
		sendToService(info.toRemote());

		ProgressDlgHelper.showProgress(mContext, "正在登录");
		startLoginDisableTimer();
	}
	
	private boolean isValidStr(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		
		return dest.length() != 0;
	}

	private void startLoginDisableTimer() {
		
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
		WeakReference<LoginActivity> theActivity;

		public SafeHandler(LoginActivity activity) {
			this.theActivity = new WeakReference<LoginActivity>(activity);
		}

		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case UI_MSG_WHAT_DISABLE_TIMER:
				LoginActivity activity = theActivity.get();
				if (activity != null) {
					activity.onLoginTimeout();
				}
				break;
			}
		}
	}

	private SafeHandler timerHandler = new SafeHandler(this);
	
	private void onLoginTimeout(){		
		ProgressDlgHelper.closeProgress();
		stopLoginTimer();
		ToastUtil.showToast(mContext, "登录超时");
	}
	
	private void stopLoginTimer(){
		if (disableTimer != null) {
			disableTimer.cancel();
			disableTimer = null;
		}
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
		int what = remote.getWhat();
		if (what == ITranCode.ACT_USER) {
			onReceiveUserActions(remote);
		} else if (what == ITranCode.ACT_SYS) {
		
		}
	}
	
	private void onReceiveUserActions(Remote remote) {
		int action = remote.getAction();
		if(action == ITranCode.ACT_USER_LOGIN){	
			ProgressDlgHelper.closeProgress();
			stopLoginTimer();
			
			LoginResult result = GeneralActionResult.parseObject(remote.getBody());
			if (result.getResCode() == ResponseCode.RES_SUCCESS) {
				ToastUtil.showToast(mContext, "登录成功");
				U.saveAppSet(U.KEY_USER_IS_LOGINED, "true", U.getMyVendorNum());

                boolean isInit = SharePrefConfig.getInstance().isDosingInit();
                if(isInit){
                    Intent intent = new Intent(mContext, WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    this.finish();
                }else{
                    Intent intent = new Intent(mContext, MaterialConfigActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    this.finish();
                }
			} else if(result.getResCode() == ResponseCode.RES_EUIDPASS || result.getResCode() == ResponseCode.RES_ENONEXIST){
				ToastUtil.showToast(mContext, "用户名或密码错误:" + result.getResCode());
			} else{
				ToastUtil.showToast(mContext, "登录失败：" +result.getResCode());
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() == 0) {
			return;
		}
	}
}
