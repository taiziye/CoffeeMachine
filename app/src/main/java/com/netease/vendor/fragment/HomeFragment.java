package com.netease.vendor.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.netease.vendor.R;
import com.netease.vendor.activity.CoffeeInfoActivity;
import com.netease.vendor.activity.PayCartActivity;
import com.netease.vendor.adapter.HomeGridAdapter;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.common.fragment.TFragment;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.GetCoffeeInfo;
import com.netease.vendor.service.bean.result.GeneralActionResult;
import com.netease.vendor.service.bean.result.GetCoffeeResult;
import com.netease.vendor.service.bean.result.GetDiscountResult;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.domain.CoffeeInfo;
import com.netease.vendor.service.protocol.MachineMaterialMap;
import com.netease.vendor.ui.GridViewExt;
import com.netease.vendor.ui.GridViewExt.OnItemClickListenerExt;
import com.netease.vendor.util.SharePrefConfig;
import com.netease.vendor.util.TimeUtil;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.U;
import com.netease.vendor.util.log.LogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class HomeFragment extends TFragment implements OnClickListener{

    public interface OnShowDiscountInfoListener{
        public void OnShowDiscountInfo(String discountInfo);
    }

    private OnShowDiscountInfoListener onShowDiscountInfoListener;

    public static final int UPDATE_COFFEE_THRESHOLD = 1000;
    
	private List<CoffeeInfo> mALLCoffees = new ArrayList<CoffeeInfo>();
	
	private static final int PAGESIZE = 6;
	
	private ViewFlipper mViewFlipper;

    private LinearLayout mPageIndicatorBar;
    private ImageView mPageIndicatorPrevious;
    private ImageView mPageIndicatorNext;
    private TextView mPageIndicatorTip;
    private FrameLayout mPayCartLinear;
    private ImageView mPayCart;
    private TextView mPayCartIndicator;

	private int mPageNum = 0;
	private int mPageIndex = 0;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buy_coffee, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(onShowDiscountInfoListener == null){
            onShowDiscountInfoListener = (OnShowDiscountInfoListener) activity;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findView();
        fetchAllCoffees();
    }
    
    private void findView() {
    	mViewFlipper = (ViewFlipper) getView().findViewById(R.id.myFlipper);
    	mViewFlipper.setOnClickListener(new ViewFlipperClickEvent());
    	mViewFlipper.setLongClickable(true);

        mPageIndicatorBar = (LinearLayout) getView().findViewById(R.id.page_indicator_layout);
        mPageIndicatorBar.setVisibility(View.INVISIBLE);
        mPageIndicatorPrevious = (ImageView) getView().findViewById(R.id.page_indicator_previous);
        mPageIndicatorPrevious.setOnClickListener(this);
        mPageIndicatorNext = (ImageView) getView().findViewById(R.id.page_indicator_next);
        mPageIndicatorNext.setOnClickListener(this);
        mPageIndicatorTip = (TextView) getView().findViewById(R.id.page_indicator_tip);

        mPayCartLinear = (FrameLayout) getView().findViewById(R.id.home_cart_linear);
        mPayCartLinear.setVisibility(View.INVISIBLE);
        mPayCart = (ImageView) getView().findViewById(R.id.home_cart);
        mPayCart.setOnClickListener(this);
        mPayCartIndicator = (TextView) getView().findViewById(R.id.home_cart_num_indicator);
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

    public class ViewFlipperClickEvent implements OnClickListener {

		@Override
		public void onClick(View v) {
			GridViewExt gv = (GridViewExt) mViewFlipper.getChildAt(mPageIndex);
			if(gv != null){
				gv.unCheckPressed();
			}
		}
	}
    
    private void fetchAllCoffees(){
    	List<CoffeeInfo> cacheInfos = MyApplication.Instance().getCoffeeInfos();
    	if(cacheInfos == null || cacheInfos.size() <= 0){
    		getCoffeeInfoFromServer();
    	}else if(TimeUtil.getNow_millisecond() - 
    			MyApplication.Instance().getLastCoffeeInfoUpdateTime() >= UPDATE_COFFEE_THRESHOLD){
    		getCoffeeInfoFromServer();
    	}else{
    		 mALLCoffees.clear();
			 mALLCoffees.addAll(cacheInfos);
			 
			 initFragment();
    	}
    }
    
    private void getCoffeeInfoFromServer(){
    	MyApplication.Instance().setLastCoffeeInfoUpdateTime(TimeUtil.getNow_millisecond());
    	LogUtil.vendor("cache is empty or cache info is outdated, requery");
    	GetCoffeeInfo info = new GetCoffeeInfo();
		info.setUid(U.getMyVendorNum());
		executeBackground(info.toRemote());
    }
    
    @Override
	public void onReceive(Remote remote) {
		int what = remote.getWhat();
		if (what == ITranCode.ACT_COFFEE) {
			int action = remote.getAction();
			if(action == ITranCode.ACT_COFFEE_GET_COFFEE){
				GetCoffeeResult result = GeneralActionResult.parseObject(remote.getBody());
				if(result != null && result.getResCode() == 200){
				    // get all coffees
                    List<CoffeeInfo> coffees = result.getCoffees();
                    if(coffees != null){
						 mALLCoffees.clear();
						 mALLCoffees.addAll(coffees);
                    }
                    // init fragment
                    initFragment();
                    // show discount information
                    try{
                        showDiscountInfo(result.getDiscountInfo());
                    }catch(Exception e){e.printStackTrace();}
				}else{
                    ToastUtil.showToast(getActivity(), "获取咖啡信息错误：" + result.getResCode());
				}
			}
		} 
	}

    private void showDiscountInfo(GetDiscountResult discountInfo){
        if(discountInfo == null)
            return;
        String info = null;
        if(discountInfo.getDiscount() != null && discountInfo.getReductMeet() != null){
            double showDiscount = Double.parseDouble(discountInfo.getDiscount()) * 10;
            info = String.format(Locale.getDefault(), getString(R.string.home_discount_reduction),
                    String.valueOf(showDiscount), String.valueOf(discountInfo.getReductMeet()),
                            String.valueOf(discountInfo.getReductSub()));
        }else if(discountInfo.getDiscount() != null && discountInfo.getReductMeet() == null){
            double showDiscount = Double.parseDouble(discountInfo.getDiscount()) * 10;
            info = String.format(Locale.getDefault(), getString(R.string.home_discount_only), String.valueOf(showDiscount));
        }else if(discountInfo.getDiscount() == null && discountInfo.getReductMeet() != null){
            info = String.format(Locale.getDefault(), getString(R.string.home_reduction_only),
                    String.valueOf(discountInfo.getReductMeet()), String.valueOf(discountInfo.getReductSub()));
        }

        onShowDiscountInfoListener.OnShowDiscountInfo(info);
    }

    private void checkLackMaterials(){
        double stockWater = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_WATER);
        boolean isLackWater =(stockWater - MachineMaterialMap.MATERIAL_WATER_LIMIT_VALUE) <= 0;
        double stockCup = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM);
        boolean isLackCup =(stockCup - MachineMaterialMap.MATERIAL_COFFEE_CUP_NUM_LIMIT_VALUE) <= 0;
        double stockBox1 =	SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_1);
        boolean isLackBox1 =(stockBox1 - MachineMaterialMap.MATERIAL_BOX_1_LIMIT_VALUE) <= 0;
        double stockBox2 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_2);
        boolean isLackBox2 =(stockBox2 - MachineMaterialMap.MATERIAL_BOX_2_LIMIT_VALUE) <= 0;
        double stockBox3 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_3);
        boolean isLackBox3 =(stockBox3 - MachineMaterialMap.MATERIAL_BOX_3_LIMIT_VALUE) <= 0;
        double stockBox4 = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_BOX_4);
        boolean isLackBox4 =(stockBox4 - MachineMaterialMap.MATERIAL_BOX_4_LIMIT_VALUE) <= 0;
        double stockBean = SharePrefConfig.getInstance().getDosingValue(MachineMaterialMap.MATERIAL_COFFEE_BEAN);
        boolean isLackBean =(stockBean - MachineMaterialMap.MATERIAL_COFFEE_BEAN_LIMIT_VALUE) <= 0;

        LogUtil.vendor("CHECK STOCK: [" + stockWater + "," + stockBox1 + "," + stockBox2 + "," +  stockBox3 + ","
                +  stockBox4 + "," +  stockBean + "," + stockCup + "]");
        LogUtil.vendor("CHECK STOCK: [" + isLackWater + "," + isLackBox1 + "," + isLackBox2 + "," +  isLackBox3 + ","
                +  isLackBox4 + "," +  isLackBean + "," + isLackCup + "]");

        for(CoffeeInfo info : mALLCoffees){
            info.setLackMaterials(false);
            ArrayList<CoffeeDosingInfo> dosingList = info.getDosingList();
            for(int i = 0; i < dosingList.size(); i++){
                CoffeeDosingInfo dosing = dosingList.get(i);
                int boxID = dosing.getId();
                if(isLackWater ||  isLackCup){
                    info.setLackMaterials(true);
                    break;
                }

                if(isLackBox1 && boxID == MachineMaterialMap.MATERIAL_BOX_1){
                    info.setLackMaterials(true);
                    break;
                }else if(isLackBox2 && boxID == MachineMaterialMap.MATERIAL_BOX_1){
                    info.setLackMaterials(true);
                    break;
                }else if(isLackBox3 && boxID == MachineMaterialMap.MATERIAL_BOX_2){
                    info.setLackMaterials(true);
                    break;
                }else if(isLackBox4 && boxID == MachineMaterialMap.MATERIAL_BOX_3){
                    info.setLackMaterials(true);
                    break;
                }else if(isLackBean && boxID == MachineMaterialMap.MATERIAL_BOX_4){
                    info.setLackMaterials(true);
                    break;
                }else if(isLackBean && boxID == MachineMaterialMap.MATERIAL_COFFEE_BEAN_LIMIT_VALUE){
                    info.setLackMaterials(true);
                    break;
                }
            }
        }
    }

    private void initFragment() {
        // check stock
        checkLackMaterials();

    	// initialize page indicator
    	mPageIndex = 0;

    	int coffeeCount = mALLCoffees.size();
    	mPageNum = (int) (Math.ceil(((double) coffeeCount / (double) PAGESIZE)));

        mPageIndicatorBar.setVisibility(View.VISIBLE);
        mPageIndicatorPrevious.setEnabled(false);
        if(mPageNum <= 1){
            mPageIndicatorNext.setEnabled(false);
        }else{
            mPageIndicatorNext.setEnabled(true);
        }
        mPageIndicatorTip.setText(Html.fromHtml("<font color='#b0734f'>" + (mPageIndex + 1) + "</font>" + "/" +
                "<font color='#b0734f'>" + mPageNum + "</font>"));

        mViewFlipper.removeAllViews();
		for (int i = 0; i < mPageNum; i++) {
			GridViewExt gridview = new GridViewExt(this.getActivity());
			gridview.setOnItemClickListener(mGridItemClickListener);
			mViewFlipper.addView(gridview);
		}

        // default show first page
		GridViewExt grid = (GridViewExt) mViewFlipper.getChildAt(mPageIndex);
		if(grid != null){
			grid.setColumnCount(2);
            HomeGridAdapter adapter = new HomeGridAdapter(this.getActivity(), loadPage(mPageIndex, mALLCoffees));
			grid.setAdapter(adapter);
		}
	}

    public void refreshFragment(){
        LogUtil.vendor("menu refreshing...");

        checkLackMaterials();

        for (int i = 0; i < mPageNum; i++) {
            GridViewExt grid = (GridViewExt) mViewFlipper.getChildAt(i);
            if(grid != null) {
                HomeGridAdapter adapter = new HomeGridAdapter(this.getActivity(), loadPage(i, mALLCoffees));
                grid.setColumnCount(2);
                grid.setAdapter(adapter);
            }
        }
    }

    private OnItemClickListenerExt mGridItemClickListener = new OnItemClickListenerExt(){

        @Override
        public boolean onItemClick(CoffeeInfo info, View view) {
            if(info != null && !info.isLackMaterials()){
                CoffeeInfoActivity.start(getActivity(), info);
                Log.e("tag","CoffeeInfoActivity");
                return true;
            }

            return false;
        }
    };
    
    private List<CoffeeInfo> loadPage(final int pageNum, final List<CoffeeInfo> info) {
        List<CoffeeInfo> coffees = new ArrayList<CoffeeInfo>();
    	int index = pageNum * PAGESIZE;
    	int left = info.size() - pageNum * PAGESIZE;
    	int count = left >= PAGESIZE ? PAGESIZE : left;
    	for(int i = index; i < index + count; i++){
            coffees.add(mALLCoffees.get(i));
    	}

        return coffees;
	}
    
    private void loadGrid(int pagePosition) {
        GridViewExt itemGrid = (GridViewExt) mViewFlipper.getChildAt(pagePosition);
        if(itemGrid != null){
            BaseAdapter adapter = itemGrid.getAdapter();
            if(adapter == null){
                itemGrid.setColumnCount(2);
                itemGrid.setAdapter(new HomeGridAdapter(this.getActivity(), loadPage(pagePosition, mALLCoffees)));
            }
        }
	}

    @Override
    public void onResume(){
        super.onResume();
        LogUtil.vendor("HomeFragment->onResume");
        updateCartGoods();
    }

    /**
     * 向左划动
     */
    public void loadPageByLeft() {
        if(mPageNum == 0){
            return;
        }

        if (mPageIndex < mPageNum - 1) {
            mPageIndex++;
            updatePageIndicatorStatus(mPageIndex, mPageNum);
            loadGrid(mPageIndex);
            mViewFlipper.showNext();
        }
    }

    /**
     * 向右划动
     */
    public void loadPageByRight() {
        if(mPageNum == 0){
            return;
        }

        if (mPageIndex > 0) {
            mPageIndex--;
            updatePageIndicatorStatus(mPageIndex, mPageNum);
            loadGrid(mPageIndex);
            mViewFlipper.showPrevious();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.page_indicator_previous:
                if(mPageIndex <= 0)
                    return;
                mPageIndex--;
                updatePageIndicatorStatus(mPageIndex, mPageNum);
                loadGrid(mPageIndex);
                mViewFlipper.showPrevious();
                break;
            case R.id.page_indicator_next:
                if(mPageIndex >= mPageNum - 1)
                    return;
                mPageIndex++;
                updatePageIndicatorStatus(mPageIndex, mPageNum);
                loadGrid(mPageIndex);
                mViewFlipper.showNext();
                break;
            case R.id.home_cart:
                switchToCart();
                break;
        }
    }

    private void updatePageIndicatorStatus(int pageIndex, int pageNum){
        if(pageIndex > 0 ){
            mPageIndicatorPrevious.setEnabled(true);
        }else{
            mPageIndicatorPrevious.setEnabled(false);
        }

        if(pageIndex < pageNum - 1){
            mPageIndicatorNext.setEnabled(true);
        }else{
            mPageIndicatorNext.setEnabled(false);
        }

        mPageIndicatorTip.setText(Html.fromHtml("<font color='#b0734f'>" + (mPageIndex + 1) + "</font>" + "/" + mPageNum));
    }

    private void switchToCart(){
        PayCartActivity.start(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
