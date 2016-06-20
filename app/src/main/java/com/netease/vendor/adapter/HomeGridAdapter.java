package com.netease.vendor.adapter;

import java.util.List;

import com.netease.vendor.R;
import com.netease.vendor.loader.ImageLoaderTool;
import com.netease.vendor.service.domain.CoffeeInfo;
import com.netease.vendor.util.ScreenUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeGridAdapter extends BaseAdapter {

	private List<CoffeeInfo> mCoffeeList;

    private Context mContext;

    public HomeGridAdapter(Context context, List<CoffeeInfo> info) {
        this.mContext = context;
        this.mCoffeeList = info;
    }

	@Override
	public int getCount() {
		return mCoffeeList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCoffeeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		viewHolder holder;
		if (convertView == null) {
			holder = new viewHolder();
			convertView = inflater.inflate(R.layout.home_coffee_grid_item, null);
			holder.mContentArea = (LinearLayout) convertView.findViewById(R.id.coffee_content_area);
			holder.mCoffeeImg = (ImageView) convertView.findViewById(R.id.coffee_info_img);			
			holder.mCoffeeName = (TextView) convertView.findViewById(R.id.coffee_info_name);
			holder.mCoffeePrice = (TextView) convertView.findViewById(R.id.coffee_info_price);
			holder.mCoffeeOriPrice = (TextView) convertView.findViewById(R.id.coffee_info_ori_price);
            holder.mCoffeeSoldOut = (RelativeLayout) convertView.findViewById(R.id.coffee_info_sold_out);
            holder.mCoffeeHot = (ImageView) convertView.findViewById(R.id.coffee_info_hot);
            holder.mCoffeeNew = (ImageView) convertView.findViewById(R.id.coffee_info_new);
			holder.mContentImgArea = (RelativeLayout) convertView.findViewById(R.id.coffee_content_img_area);

			int screenWidth = ScreenUtil.screenWidth;
			int screenHeight = ScreenUtil.screenHeight;
			int statusbarheight = ScreenUtil.statusbarheight;
			int titlebarHeight = mContext.getResources().getDimensionPixelSize(R.dimen.home_title_bar_height);
			int topbarHeight = mContext.getResources().getDimensionPixelSize(R.dimen.home_indicator_bar_height); 
			int tabbarHeight = (int) ((ScreenUtil.screenWidth / 4.0)* (135.0/270.0));
			
			int availableHeight = screenHeight - statusbarheight - titlebarHeight - topbarHeight - tabbarHeight;			
			int hopeWidth = (int) (screenWidth/2.0);
			int hopeHeight = (int) (availableHeight/3.0);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(hopeWidth, hopeHeight);
			params.leftMargin = 0;
			params.rightMargin = 0;
			params.topMargin = 0;
			holder.mContentArea.setLayoutParams(params);

			int imgWidth = (int)(hopeHeight*0.75);
			int imgHeight = (int)(hopeHeight*0.75);
			FrameLayout.LayoutParams imgParams = new FrameLayout.LayoutParams(imgWidth, imgHeight);
			imgParams.leftMargin = (hopeWidth - imgWidth) / 2;
			imgParams.rightMargin = 0;
			imgParams.topMargin = 0;
			holder.mContentImgArea.setLayoutParams(imgParams);
			holder.mCoffeeSoldOut.setLayoutParams(imgParams);

			convertView.setTag(holder);
		} else {
			holder = (viewHolder) convertView.getTag();
		}

		CoffeeInfo info = mCoffeeList.get(position);

		String imgURL = info.getImgUrl();
		ImageLoaderTool.disPlay(imgURL.trim(), holder.mCoffeeImg, R.drawable.buy_coffee_loading);
        holder.mCoffeeName.setText(info.getCoffeeTitle());

        if(info.isLackMaterials()){
            holder.mCoffeeSoldOut.setVisibility(View.VISIBLE);
            holder.mCoffeeHot.setVisibility(View.INVISIBLE);
            holder.mCoffeeNew.setVisibility(View.INVISIBLE);
        }else{
            holder.mCoffeeSoldOut.setVisibility(View.GONE);
            if(info.isHot()){
                holder.mCoffeeHot.setVisibility(View.VISIBLE);
            }else{
                holder.mCoffeeHot.setVisibility(View.GONE);
            }
            if(info.isNew()){
                holder.mCoffeeNew.setVisibility(View.VISIBLE);
            }else{
                holder.mCoffeeNew.setVisibility(View.GONE);
            }
        }

		if(info.getPrice() == info.getDiscount()){
			holder.mCoffeeOriPrice.setVisibility(View.GONE);
			holder.mCoffeePrice.setText("¥" + info.getPrice());
			holder.mCoffeePrice.setTextColor(Color.parseColor("#e1ddbe"));
		}else{
			holder.mCoffeeOriPrice.setVisibility(View.VISIBLE);
			holder.mCoffeeOriPrice.setText("¥" + info.getPrice());
			holder.mCoffeeOriPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			holder.mCoffeeOriPrice.getPaint().setAntiAlias(true);
			holder.mCoffeePrice.setText("¥" + info.getDiscount());
			holder.mCoffeePrice.setTextColor(Color.parseColor("#f15353"));
		}

        return convertView;
	}

	public class viewHolder {
		private LinearLayout mContentArea;
		private RelativeLayout mContentImgArea;
		private ImageView mCoffeeImg;
		private TextView mCoffeeName;
		private TextView mCoffeePrice;
		private TextView mCoffeeOriPrice;
        private RelativeLayout mCoffeeSoldOut;
        private ImageView mCoffeeHot;
        private ImageView mCoffeeNew;
	}

}
