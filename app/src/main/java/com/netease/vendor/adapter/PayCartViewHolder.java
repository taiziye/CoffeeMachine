package com.netease.vendor.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.vendor.R;
import com.netease.vendor.beans.CartPayItem;
import com.netease.vendor.common.adapter.TListItem;
import com.netease.vendor.common.adapter.TViewHolder;
import com.netease.vendor.loader.ImageLoaderTool;
import com.netease.vendor.service.domain.CoffeeInfo;
import com.netease.vendor.ui.AddSubView;
import com.netease.vendor.util.CoffeeUtil;
import com.netease.vendor.util.ToastUtil;

public class PayCartViewHolder extends TViewHolder implements AddSubView.OnNumChangeListener {

	private ImageView mCoffeeImg;
	private TextView mCoffeeName;
	private TextView mCoffeeOriPrice;
	private TextView mCoffeePrice;
	private TextView mCoffeeSugar;
	private LinearLayout mAddSubViewLinear;
	private AddSubView mSerNumView;
	private ImageView mCoffeeDel;

	private CartPayItem mCartPayItem;

	@Override
	protected int getResId() {
		return R.layout.pay_cart_item;
	}

	@Override
	public void inflate() {
		mCoffeeImg = (ImageView) view.findViewById(R.id.pay_cart_list_item_coffee);
		mCoffeeName = (TextView) view.findViewById(R.id.pay_cart_list_item_coffee_name);
		mCoffeeOriPrice = (TextView) view.findViewById(R.id.pay_cart_list_item_coffee_original_price);
		mCoffeePrice = (TextView) view.findViewById(R.id.pay_cart_list_item_coffee_price);
		mCoffeeSugar = (TextView) view.findViewById(R.id.pay_cart_list_item_sugar);
		mAddSubViewLinear = (LinearLayout) view.findViewById(R.id.pay_cart_list_item_num);
		mSerNumView = new AddSubView(context);
		mSerNumView.setOnNumChangeListener(this);
		mAddSubViewLinear.addView(mSerNumView);
		mCoffeeDel = (ImageView) view.findViewById(R.id.pay_cart_list_item_del);
	}

	@Override
	public void refresh(TListItem item) {
		mCartPayItem = (CartPayItem)item;
		CoffeeInfo info = mCartPayItem.getCoffeeInfo();
		if (info != null) {
			String imgURL = info.getImgUrl() == null ? "" : info.getImgUrl();
			ImageLoaderTool.disPlay(imgURL.trim(), mCoffeeImg, R.drawable.buy_coffee_loading);
			String coffeeName = info.getCoffeeTitle().trim();
			mCoffeeName.setText(coffeeName);

			if(info.getPrice() != info.getDiscount()){
				mCoffeePrice.setVisibility(View.VISIBLE);
				mCoffeePrice.setText(String.format(context.getString(R.string.pay_coffee_price_format),
						String.valueOf(info.getDiscount())));
				mCoffeePrice.setTextColor(Color.parseColor("#FF3838"));
				mCoffeeOriPrice.setVisibility(View.VISIBLE);
				mCoffeeOriPrice.setText(String.format(context.getString(R.string.pay_coffee_price_format),
						String.valueOf(info.getPrice())));
				mCoffeeOriPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				mCoffeeOriPrice.getPaint().setAntiAlias(true);
				mCoffeeOriPrice.setTextColor(Color.parseColor("#000000"));
			}else{
				mCoffeePrice.setText(String.format(context.getString(R.string.pay_coffee_price_format),
						String.valueOf(info.getDiscount())));
				mCoffeePrice.setTextColor(Color.parseColor("#000000"));
				mCoffeeOriPrice.setVisibility(View.GONE);
			}
		}

		mCoffeeSugar.setText(mCartPayItem.getSugarLevelDescri(context));

		if(mSerNumView != null){
			mSerNumView.setNum(mCartPayItem.getBuyNum());
		}

		setClickListener(mCartPayItem);
	}

	private void setClickListener(final CartPayItem item) {
		mCoffeeDel.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View view) {
				if (eventListener != null) {
					eventListener.onViewHolderClick(mCoffeeDel, item);
				}
			}
		});
	}

	@Override
	public void onNumChange(View view, int num) {
	}

	@Override
	public void onNumChangeAdd(View view, int num) {
		if(!CoffeeUtil.isExcceedCartLimit(1)){
			if(mCartPayItem != null){
				mCartPayItem.setBuyNum(num);
			}

			if (eventListener != null) {
				eventListener.onCartNumChangeNotify();
			}
		}else{
			AddSubView asv = (AddSubView) view;
			asv.setNum(asv.getNum() - 1);
			ToastUtil.showToast(context, R.string.cart_exceeds_max_num);
		}
	}

	@Override
	public void onNumChangeSub(View view, int num) {
		if(mCartPayItem != null){
			mCartPayItem.setBuyNum(num);
		}

		if (eventListener != null) {
			eventListener.onCartNumChangeNotify();
		}
	}
}
