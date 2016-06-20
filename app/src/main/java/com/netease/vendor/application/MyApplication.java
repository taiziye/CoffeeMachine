package com.netease.vendor.application;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.netease.vendor.action.CoffeeAction;
import com.netease.vendor.action.LoginAction;
import com.netease.vendor.action.SysAction;
import com.netease.vendor.action.UserAction;
import com.netease.vendor.beans.CartPayItem;
import com.netease.vendor.common.action.TActionFactory;
import com.netease.vendor.common.action.TViewWatcher;
import com.netease.vendor.common.database.TDatabase;
import com.netease.vendor.domain.CoffeeIndentStatus;
import com.netease.vendor.helper.cache.BaseDataCacher;
import com.netease.vendor.helper.cache.ImageCacher;
import com.netease.vendor.helper.cache.LruCache;
import com.netease.vendor.loader.ImageLoaderConfig;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.VendorService;
import com.netease.vendor.service.bean.result.GetDiscountResult;
import com.netease.vendor.service.domain.CoffeeDosingInfo;
import com.netease.vendor.service.domain.CoffeeInfo;
import com.netease.vendor.util.CommonUtil;
import com.netease.vendor.util.ScreenUtil;
import com.netease.vendor.util.SharePrefConfig;
import com.netease.vendor.util.ToastUtil;
import com.netease.vendor.util.log.AppErrorLogHandler;
import com.netease.vendor.util.log.LogUtil;
import com.netease.vendor.util.multicard.MultiCard;

import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;

public class MyApplication extends Application{

	private static MyApplication instance;

	public static MyApplication Instance() {
		return instance;
	}

	private boolean active;

    public boolean isMachineIdle() {
        return isMachineIdle;
    }

    public void setMachineIdle(boolean isMachineIdle) {
        this.isMachineIdle = isMachineIdle;
    }

    private boolean isMachineIdle;

    public boolean isMakingCoffee() {
        return isMakingCoffee;
    }

    public void setMakingCoffee(boolean isMakingCoffee) {
        this.isMakingCoffee = isMakingCoffee;
    }

    private boolean isMakingCoffee;

	public static Set<String> mGlobalWashTimeSet = new HashSet<String>();

    //接收系统时间改变的广播，执行定时清洗功能
    private final BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String time = sdf.format(now);
                if(mGlobalWashTimeSet.contains(time)){
					LogUtil.e("TEST", "start to execute washing task");
					new WashingTask().execute();
                }
            }
        }
    };

	private DataCacheHandler dataHandler;

	// indent status cache
	private LruCache<String, CoffeeIndentStatus> indentTimeoutRequest = new LruCache<String, CoffeeIndentStatus>(500);
	public CoffeeIndentStatus getIndentStatus(String coffeeIndent) {
		if (coffeeIndent == null) {
			return null;
		}

		CoffeeIndentStatus indentStatus = indentTimeoutRequest
				.get(coffeeIndent);
		return indentStatus;
	}
	public void addIndentStatus(CoffeeIndentStatus status) {
		indentTimeoutRequest.put(status.getCoffeeIndent(), status);
	}
	public void removeIndentStatus(String coffeeIndent) {
		indentTimeoutRequest.remove(coffeeIndent);
	}

	// cart pay items
	private ArrayList<CartPayItem> cartPayItems = new ArrayList<CartPayItem>();
	public  ArrayList<CartPayItem> getCartPayItems() {
		return cartPayItems;
	}
	public void addCoffeeToCartPay(CartPayItem item){
		if(cartPayItems == null){
			cartPayItems = new ArrayList<CartPayItem>();
		}

		boolean isNew = true;
		for(CartPayItem payItem: cartPayItems){
			if(item.getCoffeeInfo().getCoffeeId() == payItem.getCoffeeInfo().getCoffeeId()
					&& item.getSugarLevel() == payItem.getSugarLevel()){
				payItem.setBuyNum(payItem.getBuyNum() + item.getBuyNum());
				isNew = false;
				break;
			}
		}

		if(isNew) {
			cartPayItems.add(item);
		}
	}
	public void removeCartPay(CartPayItem item){
		for(int i = 0; i < cartPayItems.size(); i++){
			CartPayItem cpi = cartPayItems.get(i);
			if(cpi.getCoffeeInfo().getCoffeeId() == item.getCoffeeInfo().getCoffeeId()
					&& cpi.getSugarLevel() == item.getSugarLevel()){
				cartPayItems.remove(i);
				break;
			}
		}
	}
	public void clearCartPay(){
		if(cartPayItems != null){
			cartPayItems.clear();
		}
	}
	public int getCartNums(){
		int num = 0;
		if(cartPayItems == null)
			return num;
		for(CartPayItem item : cartPayItems){
			num += item.getBuyNum();
		}

		return num;
	}

	// 咖啡信息相关
	private long lastCoffeeInfoUpdateTime = -1;

	public long getLastCoffeeInfoUpdateTime() {
		return lastCoffeeInfoUpdateTime;
	}

	public void setLastCoffeeInfoUpdateTime(long time) {
		lastCoffeeInfoUpdateTime = time;
	}

    // 同步库存信息
    private long lastSyncStockTime = -1;
    public long getLastSyncStockTime() {
        return lastSyncStockTime;
    }
    public void setLastSyncStockTime(long lastSyncStockTime) {
        this.lastSyncStockTime = lastSyncStockTime;
    }

	// 咖啡配方信息
	private List<CoffeeInfo> coffeeInfos;
	public List<CoffeeInfo> getCoffeeInfos() {
		return coffeeInfos;
	}
	public ArrayList<CoffeeDosingInfo> getDosingListInfoByCoffeeID(int coffeeID){
		ArrayList<CoffeeDosingInfo> baseDosingList = new ArrayList<CoffeeDosingInfo>();
		List<CoffeeInfo> coffeeInfos = getCoffeeInfos();
		if(coffeeInfos == null){
			ToastUtil.showToast(this, "无法获取咖啡配方");
			LogUtil.e("vendor", "can't get the coffeeInfos in cache");
			return null;
		}

		for(CoffeeInfo info : coffeeInfos){
			if(info.getCoffeeId() == coffeeID){
				ArrayList<CoffeeDosingInfo> list = info.getDosingList();
				baseDosingList.addAll(list);
				break;
			}
		}

		return baseDosingList;
	}
	public String getCoffeeNameByCoffeeID(int coffeeID){
		String coffeeName = "";
		List<CoffeeInfo> coffeeList = getCoffeeInfos();
		if(coffeeList != null){
			for(CoffeeInfo coffee: coffeeList){
				if(coffee.getCoffeeId() == coffeeID){
					coffeeName = coffee.getCoffeeTitle();
					break;
				}
			}
		}
		return coffeeName;
	}

	// discount information
	private GetDiscountResult discountInfo;
	public void setDiscountInfo(GetDiscountResult discountInfo){
		this.discountInfo = discountInfo;
	}
	public GetDiscountResult getDiscountInfo(){
		return discountInfo;
	}

	protected boolean isCoreService = false;

	public void onCreate() {
		super.onCreate();
		instance = this;

		// 系统启动则启动
		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ApplicationInfo applicationInfo = instance.getApplicationInfo();
		List<RunningAppProcessInfo> runningAppProcessInfos = mActivityManager
				.getRunningAppProcesses();
		for (int i = 0; i < runningAppProcessInfos.size(); i++) {
			RunningAppProcessInfo runningAppProcessInfo = runningAppProcessInfos.get(i);
			if (android.os.Process.myPid() == runningAppProcessInfo.pid) {
				if (runningAppProcessInfo.processName
						.equals(applicationInfo.processName)) {
					LogUtil.systemOut("我是主进程啊");
					isCoreService = false;
				} else if (runningAppProcessInfo.processName
						.equals(applicationInfo.processName + ":core")) {
					LogUtil.systemOut("我是服务进程啊");
					isCoreService = true;
				}
				break;
			}
		}

		// 如果在这里启动，则代码首次安装启动，因为服务正常情况是在开机被激活的哦
		if (!VendorService.active) { // 如果后台服务没有启动则在这里启动
			LogUtil.systemOut("Create Vendor Service");
			Intent service = new Intent(this, VendorService.class);
			service.putExtra(VendorService.EXTRA_FROM, VendorService.FROM_PACKAGE);
			this.startService(service);
		}

		if (!isCoreService) {
			// 注册客户端action
			TActionFactory factory = TActionFactory.newInstance();
			factory.registerAction(new UserAction());
			factory.registerAction(new CoffeeAction());
			factory.registerAction(new LoginAction());
			factory.registerAction(new SysAction());

			// 初始化多媒体卡信息
			MultiCard.init();

			// 初始化屏幕信息
			ScreenUtil.GetInfo(MyApplication.this);

			// 初始化缓存
			initDataCache();

            // 初始化广播监听
            registerBroadcast();
		} else {
			TDatabase.getInstance().openDatabase(); // 数据库就放在服务层了喽
		}

		// 错误日志
		AppErrorLogHandler.getInstance(this);
		// COMMON
		CommonUtil.init();
		// 图片加载器
		ImageLoaderConfig.checkImageLoaderConfig(this);
        //加载自动清洗时间
        loadWashTime();
    }

    private void loadWashTime(){
        Set<String> timeSet = SharePrefConfig.getInstance().getWashTime();
		if(timeSet != null){
			mGlobalWashTimeSet.addAll(timeSet);
		}else{
			String[] DEFAULT_WASH_TIMEPOINT = {"13:00","20:00"};
			mGlobalWashTimeSet.addAll(Arrays.asList(DEFAULT_WASH_TIMEPOINT));
		}
    }

    private void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, filter);
    }

	public void onTerminate() {
		if (!isCoreService) {
			TViewWatcher.newInstance().unBindView(dataHandler);
			LogUtil.d("MyApplication", "application terminate");
		} else {
			TDatabase.getInstance().closeDatabase();// 当然关闭也在服务层
		}
	}

	@Override
	public void onLowMemory() {
		LogUtil.vendor("onLowMemory");
		super.onLowMemory();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private void initDataCache() {
		BaseDataCacher.instance();
		ImageCacher.newInstance();

		coffeeInfos = new ArrayList<CoffeeInfo>();
		dataHandler = new DataCacheHandler();
		TViewWatcher.newInstance().bindView(dataHandler);
	}

    private class WashingTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params){
            while(!isMachineIdle()){
                try{
                    TimeUnit.SECONDS.sleep(1);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }

            Remote remote = new Remote();
            remote.setWhat(ITranCode.ACT_COFFEE_SERIAL_PORT);
            remote.setAction(ITranCode.ACT_COFFEE_SERIAL_PORT_WASHING);
            TViewWatcher.newInstance().notifyAll(remote);
            return true;
        }
    }
}