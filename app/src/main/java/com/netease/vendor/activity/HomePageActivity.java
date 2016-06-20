package com.netease.vendor.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.vendor.R;
import com.netease.vendor.adapter.FragmentTabAdapter;
import com.netease.vendor.common.action.TActivity;
import com.netease.vendor.fragment.ExchangeFragment;
import com.netease.vendor.fragment.FetchCoffeeFragment;
import com.netease.vendor.fragment.HomeFragment;
import com.netease.vendor.fragment.HelpFragment;
import com.netease.vendor.fragment.ToDoFragment;
import com.netease.vendor.service.Remote;
import com.netease.vendor.util.CountDownTimer;
import com.netease.vendor.util.ScreenUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;

public class HomePageActivity extends TActivity implements OnClickListener, View.OnTouchListener,
        GestureDetector.OnGestureListener, HomeFragment.OnShowDiscountInfoListener {

    public static final String REFRESH_MENU = "menu_refresh";

	private TextView mHomeMacNo;
    private TextView mHomeDiscountInfo;
    private RelativeLayout mHomeTitleBar;
	private RadioGroup mTabRg;
	
    public List<Fragment> fragments = new ArrayList<Fragment>();
    private HomeFragment homeFragment;
    private int mCurrentTabIndex = 0;

    private CountDownTimer idleDownTimer;
    private static final int IDLE_TIMEOUT_VALUE = 100;

    private static final int FLING_MIN_DISTANCE = 100;
    private static final int FLING_MIN_VELOCITY = 200;
    private GestureDetector mGestureDetector;

    public static void start(Activity activity, boolean refreshMenu) {
		Intent intent = new Intent();
		intent.setClass(activity, HomePageActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(REFRESH_MENU, refreshMenu);
		activity.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage_layout);

        initView();
		initTitleBar();
		initTabBar();
        initIdleTimer();
        initGestrue();
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
        LogUtil.vendor("HomePageActivity -> onNewIntent");

        boolean isRefreshMenu= intent.getBooleanExtra(REFRESH_MENU, false);
        if(isRefreshMenu){
            if(homeFragment != null){
                homeFragment.refreshFragment();
            }
        }

//      mTabRg.check(R.id.tab_buy_coffee);
	}

	private void initView(){
		mHomeMacNo = (TextView) findViewById(R.id.home_title_machine_num);
        mHomeDiscountInfo = (TextView) findViewById(R.id.home_title_discount_info);
        mHomeTitleBar = (RelativeLayout) findViewById(R.id.home_title_view);
	}
	
	private void initTitleBar(){
		mHomeMacNo.setText(String.format(getString(R.string.home_coffee_machine_no), U.getMyVendorName()));
	}
	
	private void initTabBar(){
        homeFragment = new HomeFragment();
		fragments.add(homeFragment);
        //fragments.add(new ToDoFragment());
        //fragments.add(new ToDoFragment());
      fragments.add(new FetchCoffeeFragment());
      fragments.add(new ExchangeFragment());
        fragments.add(new HelpFragment());

        mTabRg = (RadioGroup) findViewById(R.id.tabs_home_page);
        mTabRg.setLayoutParams(new LinearLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tab_content, mTabRg);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener(){
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                System.out.println("Extra---- " + index + " checked!!! ");
                mCurrentTabIndex = index;
            }
        });
	}

    private void initIdleTimer(){
       idleDownTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {

           @Override
           public void currentInterval(int value) {
               onCountDown(value);
           }
       });
    }

    private void onCountDown(int value){
        if(value <= 0){
            WelcomeActivity.start(this);
            this.finish();
        }
    }

    private void initGestrue(){
        mGestureDetector = new GestureDetector(this, this);
        mGestureDetector.setIsLongpressEnabled(true);
    }

    @Override
    public void onStart(){
        super.onStart();
        LogUtil.vendor("HomePageActivity->onStart");
        if(idleDownTimer != null)
            idleDownTimer.startCountDownTimer(IDLE_TIMEOUT_VALUE, 1000, 1000);
    }

    @Override
    public void  onResume(){
        super.onResume();
        LogUtil.vendor("HomePageActivity->onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        LogUtil.vendor("HomePageActivity->onPause");
    }

    @Override
    public void onStop(){
        super.onStop();
        LogUtil.vendor("HomePageActivity->onStop");
        if(idleDownTimer != null)
            idleDownTimer.cancelCountDownTimer();
    }

	@Override
	public void onClick(View v) {
		
	}
	
	@Override
	public void onReceive(Remote remote) {
		
	}

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.out.println("---onFling---- ");
        if(mCurrentTabIndex == 0 && homeFragment != null){
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                try{
                    homeFragment.loadPageByLeft();
                }catch(Exception e){
                    e.printStackTrace();
                    LogUtil.e("ERROR", "something wrong with loadPageByLeft");
                }
            } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                try{
                    homeFragment.loadPageByRight();
                }catch(Exception e){
                    e.printStackTrace();
                    LogUtil.e("ERROR", "something wrong with loadPageByRight");
                }
            }
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void OnShowDiscountInfo(String discountInfo) {
        if(!TextUtils.isEmpty(discountInfo)){
            mHomeTitleBar.setBackgroundColor(Color.parseColor("#f15353"));
            mHomeDiscountInfo.setText(discountInfo);
        }else{
            mHomeTitleBar.setBackgroundColor(Color.parseColor("#00000000"));
            mHomeDiscountInfo.setText(null);
        }
    }
}
