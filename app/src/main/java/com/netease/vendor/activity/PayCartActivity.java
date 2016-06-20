package com.netease.vendor.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.vendor.R;
import com.netease.vendor.adapter.PayCartViewHolder;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.beans.CartPayItem;
import com.netease.vendor.common.action.TActivity;
import com.netease.vendor.common.adapter.TAdapter;
import com.netease.vendor.common.adapter.TListItem;
import com.netease.vendor.common.adapter.TViewHolder;
import com.netease.vendor.common.component.TListView;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.result.GetDiscountResult;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.domain.CoffeeInfo;
import com.netease.vendor.util.CountDownTimer;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PayCartActivity extends TActivity implements OnClickListener, TViewHolder.ViewHolderEventListener {

	private Context mContext;

	private TextView mHomeMacNo;
	private ImageView mBackBtn;
	private ImageView mBackHome;
	private TextView mPayCartTitle;

	private TextView mPayAliQrcode;
	private TextView mPayAliWave;
	private TextView mPayWeixin;

	private TListView mListView;
	private BaseAdapter mAdapter;
	private List<CartPayItem> mCartPayItems;

	private TextView mTotalNum;
	private TextView mTotalPrice;
	private TextView mFavourDiscount;
	private TextView mFavourMeetsub;
	private TextView mActualPay;

	private CountDownTimer mCountDownTimer;

	private String tempDiscount;
	private String tempReductMeet;
	private String tempReductSub;

    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, PayCartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_cart_layout);
		mContext = this;
		proceedExtra();

		initViews();
		initTimer();
		setupCartList();
	}

	private void proceedExtra() {
		Intent intent = getIntent();
		if(intent != null){
		}
	}

	private void initViews(){
		mHomeMacNo = (TextView) findViewById(R.id.home_title_machine_num);
		mHomeMacNo.setText(String.format(getString(R.string.home_coffee_machine_no), U.getMyVendorName()));
		mBackBtn = (ImageView) findViewById(R.id.pay_cart_back);
		mBackBtn.setOnClickListener(this);
		mBackHome = (ImageView) findViewById(R.id.pay_cart_back_home);
		mBackHome.setOnClickListener(this);
		mPayCartTitle = (TextView) findViewById(R.id.pay_cart_title);
		mPayCartTitle.setText(R.string.pay_cart_title_cart);

		mTotalNum = (TextView) findViewById(R.id.pay_cart_total_num);
		mTotalPrice = (TextView) findViewById(R.id.pay_cart_total_price);
		mFavourDiscount = (TextView) findViewById(R.id.pay_cart_favour_discount);
		mFavourMeetsub = (TextView) findViewById(R.id.pay_cart_favour_meetsub);
		mActualPay = (TextView) findViewById(R.id.pay_cart_actual_pay);

		mPayAliQrcode = (TextView) findViewById(R.id.pay_cart_alipay_qrcode);
		mPayAliQrcode.setOnClickListener(this);
		mPayWeixin = (TextView) findViewById(R.id.pay_cart_wechat_qrcode);
		mPayWeixin.setOnClickListener(this);
	}

	private void setupCartList(){
		//update items from global cache
		mCartPayItems = MyApplication.Instance().getCartPayItems();

		mListView = (TListView) findViewById(R.id.pay_cart_list);
		Map<Integer, Class> viewHolders = new HashMap<Integer, Class>();
		viewHolders.put(0, PayCartViewHolder.class);
		mAdapter = new TAdapter(this, this, viewHolders, mCartPayItems);
		mListView.setAdapter(mAdapter);

		getDiscountInfo();
		updateTotalPrice();
	}

	private void getDiscountInfo(){
		GetDiscountResult discountInfo = MyApplication.Instance().getDiscountInfo();
		if(discountInfo != null) {
			tempDiscount =  discountInfo.getDiscount();
			tempReductMeet = discountInfo.getReductMeet();
			tempReductSub = discountInfo.getReductSub();
		}
	}

	private void updateTotalPrice(){
		try{
			double total = 0;
			int totalNum = 0;
			for(CartPayItem item : mCartPayItems){
				total += (item.getCoffeeInfo().getDiscount() * item.getBuyNum());
				totalNum += item.getBuyNum();
			}
			BigDecimal totalPrice = new BigDecimal(Double.toString(total));
			// 是否折上折
			BigDecimal favourDiscout = new BigDecimal("0");
			if(tempDiscount != null && new BigDecimal(tempDiscount).compareTo(new BigDecimal("0")) == 1 ){
				favourDiscout = (new BigDecimal("1").subtract(new BigDecimal(tempDiscount))).multiply(totalPrice);
			}
			// 是否有满减
			BigDecimal favourMeetsub = new BigDecimal("0");
			if(tempReductMeet != null && new BigDecimal(tempReductMeet).compareTo(new BigDecimal("0")) == 1){
				if(totalPrice.subtract(favourDiscout).subtract(new BigDecimal(tempReductMeet)).compareTo(new BigDecimal("0")) != -1){
					favourMeetsub = new BigDecimal(tempReductSub);
				}
			}
			BigDecimal actualPay = totalPrice.subtract(favourMeetsub).subtract(favourDiscout);

			//UI
			mTotalNum.setText(String.format(Locale.getDefault(), getString(R.string.cart_total_num), totalNum));
			mTotalPrice.setText(String.format(Locale.getDefault(), getString(R.string.cart_total_price), totalPrice.doubleValue()));
			if(favourDiscout.compareTo(new BigDecimal("0")) == 1){
				mFavourDiscount.setVisibility(View.VISIBLE);
				mFavourDiscount.setText(String.format(Locale.getDefault(), getString(R.string.cart_favour_discount),
						favourDiscout.setScale(2, BigDecimal.ROUND_DOWN).doubleValue()));
			}else{
				mFavourDiscount.setVisibility(View.GONE);
			}
			if(favourMeetsub.compareTo(new BigDecimal("0")) == 1){
				mFavourMeetsub.setVisibility(View.VISIBLE);
				mFavourMeetsub.setText(String.format(Locale.getDefault(), getString(R.string.cart_favour_meetsub),
						favourMeetsub.doubleValue()));
			}else{
				mFavourMeetsub.setVisibility(View.GONE);
			}
			mActualPay.setText(String.format(Locale.getDefault(), getString(R.string.cart_actual_pay),
					actualPay.setScale(2, BigDecimal.ROUND_UP).doubleValue()));
		}catch(Exception e){
			e.printStackTrace();
			LogUtil.e("PAY CART", "UPDATE PRICE ERROR!");
		}
	}

	private void initTimer(){
		mCountDownTimer = new CountDownTimer(new CountDownTimer.CountDownCallback() {

			@Override
			public void currentInterval(int value) {
				onCountDown(value);
			}
		});
	}

	private void onCountDown(int value){
		mPayCartTitle.setText(String.format(this.getString(R.string.pay_timer_tip), value));
		if(value <= 0){
			WelcomeActivity.start(this);
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
			case R.id.pay_cart_back_home:
				switchToHome();
				break;
			case R.id.pay_cart_back:
				finish();
				break;
			case R.id.pay_cart_alipay_qrcode:
				onCoffeePay(PayCoffeeQrcodeCartActivity.PayMethod.AliQr.tag);
				break;
//			case R.id.pay_alipay_wave:
//				cancelCountDownTimer();
//				onCoffeePay(PayCoffeeQrcodeActivity.PayMethod.AliWa.tag);
//				break;
			case R.id.pay_cart_wechat_qrcode:
				onCoffeePay(PayCoffeeQrcodeCartActivity.PayMethod.WeiXin.tag);
				break;
			default:
				break;
		}
	}

	private void switchToHome(){
//		MyApplication.Instance().clearCartPay();
		HomePageActivity.start(this, false);
	}

	private void onCoffeePay(int payMethod){
		if(mCartPayItems == null || mCartPayItems.size() <= 0){
			ToastUtil.showToast(mContext, R.string.pay_cart_no_drinks_to_pay);
			return;
		}

		if(payMethod == PayCoffeeQrcodeCartActivity.PayMethod.AliWa.tag) {

		}else{
			String coffeeIndents = getCoffeeIndents();
			LogUtil.e("TEST", "coffeeIndents = " + coffeeIndents);
			PayCoffeeQrcodeCartActivity.start(this, coffeeIndents, payMethod);
		}
	}

	private String getCoffeeIndents(){
		JSONArray coffeeIndents = new JSONArray();
		for(int i = 0; i < mCartPayItems.size(); i++){
			CartPayItem item = mCartPayItems.get(i);
			int num = item.getBuyNum();
			while(num-- > 0){
				CoffeeInfo coffeeInfo = item.getCoffeeInfo();
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
							int sugarLevel = item.getSugarLevel();
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

		}

		return coffeeIndents.toString();
	}
	
	@Override
	public void onReceive(Remote remote) {
		if(remote.getWhat() == ITranCode.ACT_COFFEE){

		}
	}

	@Override
	public boolean onViewHolderLongClick(View view, TListItem item) {
		return false;
	}

	@Override
	public boolean onViewHolderClick(View view, TListItem item) {
		if(item instanceof CartPayItem){
			CartPayItem cartPayItem = (CartPayItem) item;
			MyApplication.Instance().removeCartPay(cartPayItem);
			for(int i = 0; i < mCartPayItems.size(); i++){
				CartPayItem cpi = mCartPayItems.get(i);
				if(cpi.getCoffeeInfo().getCoffeeId() == cartPayItem.getCoffeeInfo().getCoffeeId()
						&& cpi.getSugarLevel() == cartPayItem.getSugarLevel()){
					mCartPayItems.remove(i);
					break;
				}
			}
			mAdapter.notifyDataSetChanged();

			updateTotalPrice();

			return true;
		}

		return false;
	}

	@Override
	public void onCartNumChangeNotify() {
		updateTotalPrice();
	}

	@Override
	public void onStart(){
		super.onStart();
		if(mCountDownTimer != null)
			mCountDownTimer.startCountDownTimer(60, 1000, 1000);
	}

	@Override
	public void onStop(){
		super.onStop();
		if(mCountDownTimer != null)
			mCountDownTimer.cancelCountDownTimer();
	}
}
