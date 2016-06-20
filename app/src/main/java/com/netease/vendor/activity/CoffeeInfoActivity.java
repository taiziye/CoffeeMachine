package com.netease.vendor.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.vendor.R;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.beans.CartPayItem;
import com.netease.vendor.common.action.TActivity;
import com.netease.vendor.loader.ImageLoaderTool;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.GetDiscountInfo;
import com.netease.vendor.service.bean.result.GeneralActionResult;
import com.netease.vendor.service.bean.result.GetDiscountResult;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.domain.CoffeeInfo;
import com.netease.vendor.ui.AddSubView;
import com.netease.vendor.util.CoffeeUtil;
import com.netease.vendor.util.CountDownTimer;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;

public class CoffeeInfoActivity extends TActivity implements OnClickListener {

	public static final String COFFEE_INFO = "coffee_info";

	private TextView mHomeMacNo;
	private ImageView mPayBack;
	private FrameLayout mPayCartLinear;
	private ImageView mPayCart;
	private TextView mPayCartIndicator;
	private ImageView mCoffeeImg;
	private TextView mCoffeeName;
	private TextView mCoffeePrice;
	private TextView mCoffeeOriPrice;

	private TextView mAddSugarTip;
    private LinearLayout mAddSugarArea;
	private ImageView mAddNoSugar;
	private ImageView mAddLittleSugar;
	private ImageView mAddMiddleSugar;
	private ImageView mAddMoreSugar;

	private LinearLayout mSetNumLinear;
	private AddSubView mSerNumView;

//	private ImageView mPayLoading;
	private LinearLayout mPayArea;
	private LinearLayout mPayDetailParent;
	private TextView mPayDetail;
	private TextView mPayTotolPay;

	private ImageView mAddToCart;
	private ImageView mAnimImageView;
	private Animation mAnimation;
	private TextView mPayAlipayQrcode;
	private TextView mPayWechatQrcode;

	private ImageView mBanner;
    
    private CoffeeInfo mCoffeeInfo;

    private CountDownTimer countDownTimer;
    private static final int IDLE_TIMEOUT_VALUE = 60;

	public enum SugarLevel {
		NONE("无糖", 1), LITTLE("少糖", 2), NORMAL("普通", 3), MORE("多糖", 4);

		private String name;
		private int weight;

		private SugarLevel(String name, int weight) {
			this.name = name;
			this.weight = weight;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getWeight() {
			return weight;
		}

		public void setWeight(int weight) {
			this.weight = weight;
		}
	}

    private SugarLevel mAddSugarLevel = SugarLevel.NORMAL;
    private boolean isShowAddSugarBar = false;
    private double sugarBase = 0;

	private boolean isGoToCartPay = false;

	private String tempDiscount;
	private String tempReductMeet;
	private String tempReductSub;

    public static void start(Activity activity, CoffeeInfo info) {
        Intent intent = new Intent();
        intent.setClass(activity, CoffeeInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(COFFEE_INFO, info);
        activity.startActivity(intent);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coffee_info_layout);
		proceedExtra();
		
		initViews();
        initTimer();
		initAnimation();

		getDiscountInfo();
	}
	
	private void proceedExtra() {
		Intent intent = getIntent();
		mCoffeeInfo = (CoffeeInfo) intent.getSerializableExtra(COFFEE_INFO);
        if(mCoffeeInfo != null){
			LogUtil.vendor("[CoffeeInfoActivity] " + mCoffeeInfo.getCoffeeTitle());
            ArrayList<CoffeeDosingInfo> dosingList = mCoffeeInfo.getDosingList();
            for(int i = 0; i < dosingList.size(); i++){
				CoffeeDosingInfo info = dosingList.get(i);
                LogUtil.vendor("[CoffeeInfoActivity] " + info.toString());
				/**
				 * 判断本地机器上面糖量是否足够
				 */
                if(info.getMacConifg() == 1 && info.getValue() > 0){
                    isShowAddSugarBar = true;
					sugarBase = info.getValue();
                }
            }
        }
    }

	private void initViews(){	
		mHomeMacNo = (TextView) findViewById(R.id.home_title_machine_num);
		mPayBack = (ImageView) findViewById(R.id.coffee_info_back);
		mHomeMacNo.setText(String.format(getString(R.string.home_coffee_machine_no), U.getMyVendorName()));
		mPayBack.setOnClickListener(this);
		mPayCartLinear = (FrameLayout) findViewById(R.id.coffee_info_cart_linear);
		mPayCartLinear.setVisibility(View.INVISIBLE);
		mPayCart = (ImageView) findViewById(R.id.coffee_info_cart);
		mPayCart.setOnClickListener(this);
		mPayCartIndicator = (TextView) findViewById(R.id.coffee_info_cart_indicator);

		mCoffeeImg = (ImageView) findViewById(R.id.coffee_info_img);
		mCoffeeName = (TextView) findViewById(R.id.coffee_info_name);
		mCoffeePrice = (TextView) findViewById(R.id.coffee_info_price);
		mCoffeeOriPrice = (TextView) findViewById(R.id.coffee_info_ori_price);
		String imgURL = mCoffeeInfo.getImgUrl() == null ? "" : mCoffeeInfo.getImgUrl();
		ImageLoaderTool.disPlay(imgURL.trim(), mCoffeeImg, R.drawable.buy_coffee_loading);
		mCoffeeName.setText(mCoffeeInfo.getCoffeeTitle());

		if(mCoffeeInfo.getPrice() == mCoffeeInfo.getDiscount()){
			mCoffeeOriPrice.setVisibility(View.GONE);
			mCoffeePrice.setText("¥" + mCoffeeInfo.getPrice());
			mCoffeePrice.setTextColor(Color.parseColor("#e1ddbe"));
		}else{
			mCoffeeOriPrice.setVisibility(View.VISIBLE);
			mCoffeeOriPrice.setText("¥" + mCoffeeInfo.getPrice());
			mCoffeeOriPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			mCoffeeOriPrice.getPaint().setAntiAlias(true);
			mCoffeePrice.setText("¥" + mCoffeeInfo.getDiscount());
			mCoffeePrice.setTextColor(Color.parseColor("#f15353"));
		}

		mAddSugarTip = (TextView) findViewById(R.id.coffee_info_add_sugar_tip);
        mAddSugarArea = (LinearLayout) findViewById(R.id.add_sugar_area);
		LogUtil.vendor("IsShowAddSugarBar = " + isShowAddSugarBar);
        if(isShowAddSugarBar){
            mAddSugarArea.setVisibility(View.VISIBLE);
			mAddSugarTip.setVisibility(View.VISIBLE);
        }else {
            mAddSugarArea.setVisibility(View.INVISIBLE);
			mAddSugarTip.setVisibility(View.INVISIBLE);
        }
		mAddNoSugar =(ImageView) findViewById(R.id.add_sugar_no);
		mAddNoSugar.setOnClickListener(this);
		mAddLittleSugar =(ImageView) findViewById(R.id.add_sugar_little);
		mAddLittleSugar.setOnClickListener(this);
		mAddMiddleSugar =(ImageView) findViewById(R.id.add_sugar_middle);
		mAddMiddleSugar.setOnClickListener(this);
		mAddMoreSugar = (ImageView) findViewById(R.id.add_sugar_more);
		mAddMoreSugar.setOnClickListener(this);

		mSetNumLinear = (LinearLayout) findViewById(R.id.set_buy_num);
		mSerNumView = new AddSubView(this, 1);
		mSerNumView.setOnNumChangeListener(new AddSubView.OnNumChangeListener(){

			@Override
			public void onNumChange(View view, int num) {
			}

			@Override
			public void onNumChangeAdd(View view, int num) {
				updatePrice();
			}

			@Override
			public void onNumChangeSub(View view, int num) {
				updatePrice();
			}
		});
		mSetNumLinear.addView(mSerNumView);

//		mPayLoading = (ImageView) findViewById(R.id.coffee_info_pay_loading);
//		AnimationDrawable animationDrawable = (AnimationDrawable) mPayLoading.getBackground();
//		animationDrawable.start();
		mPayArea = (LinearLayout) findViewById(R.id.coffee_info_pay_area);
		mPayDetailParent = (LinearLayout) findViewById(R.id.coffee_info_pay_detail_parent);
		mPayDetail = (TextView) findViewById(R.id.coffee_info_pay_detail);
		mPayTotolPay = (TextView) findViewById(R.id.coffee_info_total_pay);

		mAddToCart = (ImageView) findViewById(R.id.coffee_info_add_to_cart);
		mAddToCart.setOnClickListener(this);
		mPayAlipayQrcode = (TextView) findViewById(R.id.coffee_info_pay_alipay);
		mPayAlipayQrcode.setOnClickListener(this);
		mPayWechatQrcode = (TextView) findViewById(R.id.coffee_info_pay_wechat);
		mPayWechatQrcode.setOnClickListener(this);

		updateCartGoods();
		
		mBanner = (ImageView) findViewById(R.id.coffee_info_banner);
//		int bannerWidth = ScreenUtil.screenWidth;
//		int bannerHeight = (int) (bannerWidth * (468.0 / 1080.0));
//		mBanner.setLayoutParams(new LinearLayout.LayoutParams(bannerWidth,
//				bannerHeight));

		if(mCoffeeInfo != null && (getString(R.string.EspressoCoffee).equals(mCoffeeInfo.getCoffeeTitle())
				|| getString(R.string.CafeAmericano).equals(mCoffeeInfo.getCoffeeTitle()))){
			onChangeSugarLevel(SugarLevel.NONE);
		}else{
			onChangeSugarLevel(SugarLevel.NORMAL);
		}
	}

    private void initTimer(){
        countDownTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {

            @Override
            public void currentInterval(int value) {
                onCountDown(value);
            }
        });
    }

    private void onCountDown(int value){
        if(value <= 0){
            this.finish();
        }
    }

	private void initAnimation(){
		mAnimImageView = (ImageView) findViewById(R.id.cart_anim_icon);
		mAnimation = AnimationUtils.loadAnimation(this, R.anim.cart_anim);
		mAnimation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				updateCartGoods();

				mAnimImageView.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void updateCartGoods(){
		int num = MyApplication.Instance().getCartNums();
		if(num > 0){
			mPayCartLinear.setVisibility(View.VISIBLE);
			mPayCartIndicator.setText(String.valueOf(num));
		}else{
			mPayCartLinear.setVisibility(View.INVISIBLE);
		}
	}
	
	private void onChangeSugarLevel(SugarLevel level){
		mAddSugarLevel = level;
		int imageRes[] = {R.drawable.add_no_sugar, R.drawable.add_little_sugar, 
				R.drawable.add_middle_sugar, R.drawable.add_more_sugar};  
		if(level == SugarLevel.NONE){
			mAddNoSugar.setImageResource(imageRes[0] + 1);
			mAddLittleSugar.setImageResource(imageRes[1]);
			mAddMiddleSugar.setImageResource(imageRes[2]);
			mAddMoreSugar.setImageResource(imageRes[3]);
		}else if(level == SugarLevel.LITTLE){
			mAddNoSugar.setImageResource(imageRes[0]);
			mAddLittleSugar.setImageResource(imageRes[1] + 1);
			mAddMiddleSugar.setImageResource(imageRes[2]);
			mAddMoreSugar.setImageResource(imageRes[3]);
		}else if(level == SugarLevel.NORMAL){
			mAddNoSugar.setImageResource(imageRes[0]);
			mAddLittleSugar.setImageResource(imageRes[1]);
			mAddMiddleSugar.setImageResource(imageRes[2] + 1);
			mAddMoreSugar.setImageResource(imageRes[3]);
		}else if(level == SugarLevel.MORE){
			mAddNoSugar.setImageResource(imageRes[0]);
			mAddLittleSugar.setImageResource(imageRes[1]);
			mAddMiddleSugar.setImageResource(imageRes[2]);
			mAddMoreSugar.setImageResource(imageRes[3] + 1);
		}
	}

	private void getDiscountInfo(){
		/**
		 * 打折信息属于咖啡机的全局信息，应当存储在本地当中
		 */
		GetDiscountResult discountInfo = MyApplication.Instance().getDiscountInfo();
		if(discountInfo != null) {
			tempDiscount =  discountInfo.getDiscount();
			tempReductMeet = discountInfo.getReductMeet();
			tempReductSub = discountInfo.getReductSub();
			LogUtil.e("DEBUG", "" + tempDiscount + ", " + tempReductMeet + "-" + tempReductSub);
		}

		updatePrice();

//		GetDiscountInfo info = new GetDiscountInfo();
//		info.setUid(U.getMyVendorNum());
//		execute(info.toRemote());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.coffee_info_back:
			this.finish();
			break;
		case R.id.add_sugar_no:
			onChangeSugarLevel(SugarLevel.NONE);
			break;
		case R.id.add_sugar_little:
			onChangeSugarLevel(SugarLevel.LITTLE);
			break;
		case R.id.add_sugar_middle:
			onChangeSugarLevel(SugarLevel.NORMAL);
			break;
		case R.id.add_sugar_more:
			onChangeSugarLevel(SugarLevel.MORE);
			break;
		case R.id.coffee_info_cart:
			switchToCart();
			break;
		case R.id.coffee_info_add_to_cart:
			int num = mSerNumView.getNum();
			if(!CoffeeUtil.isExcceedCartLimit(num)){
				CartPayItem item = new CartPayItem();
				item.setCoffeeInfo(mCoffeeInfo);
				item.setBuyNum(num);
				item.setSugarLevel(mAddSugarLevel.getWeight());
				MyApplication.Instance().addCoffeeToCartPay(item);

				mAnimImageView.setVisibility(View.VISIBLE);
				mAnimImageView.startAnimation(mAnimation);
			}else{
				ToastUtil.showToast(this, R.string.cart_exceeds_max_num);
			}
			break;
		case R.id.coffee_info_pay_alipay:
			onCoffeePay(PayCoffeeQrcodeCartActivity.PayMethod.AliQr.tag);
			break;
		case R.id.coffee_info_pay_wechat:
			onCoffeePay(PayCoffeeQrcodeCartActivity.PayMethod.WeiXin.tag);
			break;
		default:
			break;
		}
	}

	private void onCoffeePay(int payMethod){
		if(payMethod == PayCoffeeQrcodeCartActivity.PayMethod.AliWa.tag) {

		}else{
			String coffeeIndents = getCoffeeIndents();
			LogUtil.e("COFFEE_INFO", "coffeeIndents = " + coffeeIndents);
			PayCoffeeQrcodeCartActivity.start(this, coffeeIndents, payMethod);
		}
	}

	private String getCoffeeIndents(){
		JSONArray coffeeIndents = new JSONArray();

		int num = mSerNumView.getNum();
		while(num-- > 0){
			CoffeeInfo coffeeInfo = mCoffeeInfo;
			if(coffeeInfo != null){
				int goodSid = coffeeInfo.getCoffeeId();

				JSONArray dosings = new JSONArray();
				ArrayList<CoffeeDosingInfo> dosingList = coffeeInfo.getDosingList();

				for(int j = 0; j < dosingList.size(); j++){
					CoffeeDosingInfo dosing = dosingList.get(j);
					if(dosing.getMacConifg() == 1){
						JSONObject jsonObj = new JSONObject();
						jsonObj.put("dosingID", dosing.getId());

						double sugarWeight = 0;
						int sugarLevel = mAddSugarLevel.getWeight();
						if(sugarLevel == CoffeeInfoActivity.SugarLevel.NONE.getWeight()){
							sugarWeight = 0;
						}else if(sugarLevel == CoffeeInfoActivity.SugarLevel.LITTLE.getWeight()){
							sugarWeight = dosing.getValue() * 0.5;
						}else if(sugarLevel == CoffeeInfoActivity.SugarLevel.NORMAL.getWeight()){
							sugarWeight = dosing.getValue();
						}else if(sugarLevel == CoffeeInfoActivity.SugarLevel.MORE.getWeight()){
							sugarWeight = dosing.getValue() * 1.5;
						}

						jsonObj.put("value", sugarWeight);
						dosings.add(jsonObj);
						break;
					}
				}

				JSONObject indent = new JSONObject();
				indent.put("goodsid", goodSid);
				indent.put("dosing", dosings.toString());

				coffeeIndents.add(indent);
			}
		}

		return coffeeIndents.toString();
	}

	private void switchToCart(){
		isGoToCartPay = true;
		PayCartActivity.start(this);
	}

    @Override
    public void onStart(){
        super.onStart();
        LogUtil.e("vendor", "CoffeeInfoPage->onStart");
        if(countDownTimer != null)
            countDownTimer.startCountDownTimer(IDLE_TIMEOUT_VALUE, 1000, 1000);
    }

	@Override
	public void onResume(){
		super.onResume();
		if(isGoToCartPay){
			isGoToCartPay = false;
			updateCartGoods();
		}
	}

    @Override
    public void onStop(){
        super.onStop();
        LogUtil.e("vendor", "CoffeeInfoPage->onStop");
        if(countDownTimer != null)
            countDownTimer.cancelCountDownTimer();
//		releaseImageView(mPayLoading);
    }

	private void releaseImageView(ImageView imageView) {
		Drawable d = imageView.getDrawable();
		if (d != null)
			d.setCallback(null);
		imageView.setImageDrawable(null);
		imageView.setBackgroundDrawable(null);
	}
	
	@Override
	public void onReceive(Remote remote) {
		if(remote.getWhat() == ITranCode.ACT_COFFEE){
			/*
			if(remote.getAction() == ITranCode.ACT_COFFEE_GET_DISCOUNT){
				mPayLoading.setVisibility(View.GONE);
				mPayArea.setVisibility(View.VISIBLE);
				GetDiscountResult result = GeneralActionResult.parseObject(remote.getBody());
				if(result != null && result.getResCode() == 200){
					tempDiscount =  result.getDiscount();
					tempReductMeet = result.getReductMeet();
					tempReductSub = result.getReductSub();
					LogUtil.e("DEBUG", "" + tempDiscount + ", " + tempReductMeet + "-" + tempReductSub);
				}

				updatePrice();
			}*/
		}
	}

	private void  updatePrice(){
		try{
			double original = mCoffeeInfo.getDiscount();
			int num = mSerNumView.getNum();
			BigDecimal totalPrice = new BigDecimal(Double.toString(original)).multiply(new BigDecimal(Double.toString(num)));
			// 是否折上折
			BigDecimal favourDiscout = new BigDecimal("0");
			if(tempDiscount != null && new BigDecimal(tempDiscount).compareTo(new BigDecimal("0")) == 1 ){
				favourDiscout = (new BigDecimal("1").subtract(new BigDecimal(tempDiscount))).multiply(totalPrice);
			}
			// 是否有满减
			BigDecimal favourMeetsub = new BigDecimal("0");
			if(tempReductMeet != null && new BigDecimal(tempReductMeet).compareTo(new BigDecimal("0")) == 1){
				if(totalPrice.subtract(favourDiscout).subtract(new BigDecimal(tempReductMeet)).compareTo(new BigDecimal("0")) != -1){
					/**
					 * 符合满减的条件之后再减去
					 */
					favourMeetsub = new BigDecimal(tempReductSub);
				}
			}
			BigDecimal actualPay = totalPrice.subtract(favourMeetsub).subtract(favourDiscout);

			//show price
			if(favourMeetsub.compareTo(new BigDecimal("0")) == 1  && favourDiscout.compareTo(new BigDecimal("0")) == 1 ){
				mPayDetailParent.setVisibility(View.VISIBLE);
				String payDetail = String.format(Locale.getDefault(), getString(R.string.pay_favour_tip),
						totalPrice.doubleValue(),
						favourDiscout.setScale(2, BigDecimal.ROUND_DOWN).doubleValue(),
						favourMeetsub.doubleValue());
				mPayDetail.setText(payDetail);
			}else if(favourMeetsub.compareTo(new BigDecimal("0")) == 1 && favourDiscout.compareTo(new BigDecimal("0")) != 1){
				mPayDetailParent.setVisibility(View.VISIBLE);
				String payDetail = String.format(Locale.getDefault(), getString(R.string.pay_favour_tip_meetsub),
						totalPrice.doubleValue(),
						favourMeetsub.doubleValue());
				mPayDetail.setText(payDetail);
			}else if(favourMeetsub.compareTo(new BigDecimal("0")) != 1 && favourDiscout.compareTo(new BigDecimal("0")) == 1){
				mPayDetailParent.setVisibility(View.VISIBLE);
				String payDetail = String.format(Locale.getDefault(), getString(R.string.pay_favour_tip_discount),
						totalPrice.doubleValue(),
						favourDiscout.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
				mPayDetail.setText(payDetail);
			}else{
				mPayDetailParent.setVisibility(View.GONE);
			}

			mPayTotolPay.setText(String.format(Locale.getDefault(), getString(R.string.pay_actual_pay),
					actualPay.setScale(2, BigDecimal.ROUND_UP).doubleValue()));
		}catch(Exception e){
			e.printStackTrace();
			LogUtil.e("COFFEE INFO","UPDATE PRICE ERROR!");
		}
	}
}
