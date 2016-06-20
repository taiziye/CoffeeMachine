package com.netease.vendor.service.action;

import android.text.TextUtils;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.service.bean.action.AddStockInfo;
import com.netease.vendor.service.bean.action.AppDownloadInfo;
import com.netease.vendor.service.bean.action.CancelTradeCartInfo;
import com.netease.vendor.service.bean.action.CancelTradeInfo;
import com.netease.vendor.service.bean.action.GetAdvPicsInfo;
import com.netease.vendor.service.bean.action.GetCoffeeInfo;
import com.netease.vendor.service.bean.action.GetDiscountInfo;
import com.netease.vendor.service.bean.action.GetDosingListInfo;
import com.netease.vendor.service.bean.action.GetMachineConfigInfo;
import com.netease.vendor.service.bean.action.MachineStatusReportInfo;
import com.netease.vendor.service.bean.action.PayQrcodeCartInfo;
import com.netease.vendor.service.bean.action.PayQrcodeInfo;
import com.netease.vendor.service.bean.action.PaySonicWaveInfo;
import com.netease.vendor.service.bean.action.PayStatusAskCartInfo;
import com.netease.vendor.service.bean.action.PayStatusAskInfo;
import com.netease.vendor.service.bean.action.RollbackCoffeeIndent;
import com.netease.vendor.service.bean.action.RollbackCoffeeIndentCart;
import com.netease.vendor.service.bean.action.SyncStockInfo;
import com.netease.vendor.service.bean.action.VerifyCoffeeInfo;
import com.netease.vendor.service.bean.result.AddStockResult;
import com.netease.vendor.service.bean.result.GetAdvPicsResult;
import com.netease.vendor.service.bean.result.GetDiscountResult;
import com.netease.vendor.service.bean.result.GetDosingResult;
import com.netease.vendor.service.bean.result.GetMachineConfigResult;
import com.netease.vendor.service.bean.result.UpdateStockResult;
import com.netease.vendor.service.core.ResendRequestTask;
import com.netease.vendor.service.domain.Ancestor;
import com.netease.vendor.service.protocol.ResponseCode;
import com.netease.vendor.service.protocol.request.coffee.AddStockRequest;
import com.netease.vendor.service.protocol.request.coffee.AppDownloadRequest;
import com.netease.vendor.service.protocol.request.coffee.CancelTradeCartRequest;
import com.netease.vendor.service.protocol.request.coffee.CancelTradeRequest;
import com.netease.vendor.service.protocol.request.coffee.GetAdvPicRequest;
import com.netease.vendor.service.protocol.request.coffee.GetCoffeeRequest;
import com.netease.vendor.service.protocol.request.coffee.GetDiscountRequest;
import com.netease.vendor.service.protocol.request.coffee.GetDosingListRequest;
import com.netease.vendor.service.protocol.request.coffee.GetMachineConfigRequest;
import com.netease.vendor.service.protocol.request.coffee.MachineStatusReportRequest;
import com.netease.vendor.service.protocol.request.coffee.PayAliSonicWaveRequest;
import com.netease.vendor.service.protocol.request.coffee.PayQrcodeCartRequest;
import com.netease.vendor.service.protocol.request.coffee.PayQrcodeCartRetryTask;
import com.netease.vendor.service.protocol.request.coffee.PayQrcodeRequest;
import com.netease.vendor.service.protocol.request.coffee.PayQrcodeRetryTask;
import com.netease.vendor.service.protocol.request.coffee.PayStatusAskCartRequest;
import com.netease.vendor.service.protocol.request.coffee.PayStatusAskRequest;
import com.netease.vendor.service.protocol.request.coffee.RollbackCartRequest;
import com.netease.vendor.service.protocol.request.coffee.RollbackRequest;
import com.netease.vendor.service.protocol.request.coffee.RollbackRetryTask;
import com.netease.vendor.service.protocol.request.coffee.UpdateStockRequest;
import com.netease.vendor.service.protocol.request.coffee.VerifyCoffeeRequest;
import com.netease.vendor.service.protocol.request.coffee.VerifyCoffeeRetryTask;
import com.netease.vendor.util.log.LogUtil;

public class CoffeeAction extends TAction {

	@Override
	public void execute(Remote remote) {
		switch (remote.getAction()) {
		case ITranCode.ACT_COFFEE_GET_COFFEE:
			getCoffeeInfos(remote);
			break;
		case ITranCode.ACT_COFFEE_VERIFY_COFFEE:
			verifyCoffee(remote);
			break;
		case ITranCode.ACT_COFFEE_ROLL_BACK:
			rollbackCoffeeIndent(remote);
			break;
		case ITranCode.ACT_COFFEE_PAY_QRCODE:
			payCoffeeQrcode(remote);
			break;
		case ITranCode.ACT_COFFEE_ASK_PAY_RESULT:
			payStatusAsk(remote);
			break;
		case ITranCode.ACT_COFFEE_ASK_CART_PAY_RESULT:
			payStatusAskCart(remote);
			break;
		case ITranCode.ACT_COFFEE_APP_DOWNLOAD:
			getAppDownloadURL(remote);
			break;
		case ITranCode.ACT_COFFEE_PAY_SONICWAVE:
			paySonicWave(remote);
			break;
		case ITranCode.ACT_COFFEE_REPORT_STATUS:
			reportStatus(remote);
			break;
        case ITranCode.ACT_COFFEE_CANCEL_TRADE:
            cancelTrade(remote);
            break;
		case ITranCode.ACT_COFFEE_CANCEL_TRADE_CART:
			cancelTradeCart(remote);
			break;
        case ITranCode.ACT_COFFEE_STOCK_UPDATE:
            updateStock(remote);
            break;
        case ITranCode.ACT_COFFEE_DOSING_LIST:
            getCoffeeDosingList(remote);
            break;
		case ITranCode.ACT_COFFEE_STOCK_ADD:
			addStock(remote);
			break;
		case ITranCode.ACT_COFFEE_PAY_QRCODE_CART:
			PayCoffeeQrcodeCart(remote);
			break;
		case ITranCode.ACT_COFFEE_ROLL_BACK_CART:
			rollbackCartIndents(remote);
			break;
		case ITranCode.ACT_COFFEE_GET_MACHINE_CONIFG:
			getMachineConfig(remote);
			break;
		case ITranCode.ACT_COFFEE_GET_ADV_PICS:
			getAdvPics(remote);
			break;
		case ITranCode.ACT_COFFEE_GET_DISCOUNT:
			getDiscountInfo(remote);
			break;
		default:
			LogUtil.vendor("don't recognized coffee action: "
					+ remote.getAction());
			break;
		}
	}

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}
	
	private void getCoffeeInfos(Remote remote){
		GetCoffeeInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null)
			return;
		GetCoffeeRequest request = new GetCoffeeRequest(info.getUid());
		core.sendRequestToServer(request);	
	}
	
	private void verifyCoffee(Remote remote){
		VerifyCoffeeInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		String coffeeIndent = info.getCoffeeIndent();
		long timestamp = info.getTimestamp();
		boolean isRetry = info.isRetry();
		LogUtil.vendor("verify coffee code request: " + coffeeIndent + ", " + timestamp + ", " + isRetry);
		VerifyCoffeeRequest request = new VerifyCoffeeRequest(info.getUid(), coffeeIndent, timestamp, isRetry);
		core.sendRequestToServer(request);
		VerifyCoffeeRetryTask task = new VerifyCoffeeRetryTask(request);
		core.addRequestRetryTimer(task, 0, 30);
	}
	
	private void rollbackCoffeeIndent(Remote remote) {
		RollbackCoffeeIndent info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		String coffeeIndent = info.getCoffeeIndent();
		long timestamp = info.getTimestamp();
		boolean isRetry = info.isRetry();
		LogUtil.vendor("roolback coffee code request for indent " + coffeeIndent + " at " + timestamp);
		RollbackRequest request = new RollbackRequest(info.getUid(), 
				coffeeIndent, timestamp, isRetry);
		core.sendRequestToServer(request);	
		RollbackRetryTask task = new RollbackRetryTask(request);
		core.addRequestRetryTimer(task, 0, 30);
	}
	
	private void payCoffeeQrcode(Remote remote){
		PayQrcodeInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		int coffeeId = info.getCoffeeId();
		String dosing = info.getDosing();
		short provider = info.getProvider();
		LogUtil.vendor("pay coffee by qrcode: " + "[" + coffeeId + ", " + dosing + ", " + provider + "]");
		PayQrcodeRequest request = new PayQrcodeRequest(info.getUid(), coffeeId, dosing, provider);
		core.sendRequestToServer(request);	
		PayQrcodeRetryTask task = new PayQrcodeRetryTask(request);
		core.addRequestRetryTimer(task, 0, 30);
	}

	private void PayCoffeeQrcodeCart(Remote remote){
		PayQrcodeCartInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) return;
		String coffeeIndents = info.getCoffeeIndents();
		short provider = info.getProvider();
		PayQrcodeCartRequest request = new PayQrcodeCartRequest(info.getUid(), coffeeIndents, provider);
		core.sendRequestToServer(request);
		PayQrcodeCartRetryTask task = new PayQrcodeCartRetryTask(request);
		core.addRequestRetryTimer(task, 0, 30);
	}
	
	private void paySonicWave(Remote remote){
		PaySonicWaveInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null)
			return;
		int coffeeId = info.getCoffeeId();
		String dosing = info.getDosing();
		short provider = info.getProvider();
		String dynamicID = info.getDynamicID();
		LogUtil.vendor("pay ali sonicwave: " + "[" + coffeeId + ", " + dosing + ", " + provider + ", " + dynamicID + "]");
		PayAliSonicWaveRequest request = new PayAliSonicWaveRequest(info.getUid(), coffeeId, dosing, provider, dynamicID);
		core.sendRequestToServer(request);	
	}
	
	private void payStatusAsk(Remote remote){
		PayStatusAskInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		String coffeeIndent = info.getCoffeeIndent();
		LogUtil.vendor("ask pay status for indent: " + coffeeIndent);
		PayStatusAskRequest request = new PayStatusAskRequest(info.getUid(), coffeeIndent);
		core.sendRequestToServer(request);
        core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] payStatusAsk->timeout");
			}
		}, 0, 30);
	}

	private void payStatusAskCart(Remote remote){
		PayStatusAskCartInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null)
			return;
		String payIndent = info.getPayIndent();
		LogUtil.vendor("ask pay status for indent: " + payIndent);
		PayStatusAskCartRequest request = new PayStatusAskCartRequest(info.getUid(), payIndent);
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] payStatusAskCart->timeout");
			}
		}, 0, 30);
	}
	
	private void getAppDownloadURL(Remote remote){
		AppDownloadInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		AppDownloadRequest request = new AppDownloadRequest(info.getUid());
		core.sendRequestToServer(request);	
	}
	
	private void reportStatus(Remote remote){
		MachineStatusReportInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) 
			return;
		long timestamp = info.getTimestamp();
		String machineStatusJson = info.toJsonArray();
		LogUtil.vendor("report machine status to server: " + timestamp + " -> " + machineStatusJson);
		if(!TextUtils.isEmpty(machineStatusJson)){
			MachineStatusReportRequest request = new MachineStatusReportRequest(info.getUid(), timestamp, machineStatusJson);
			core.sendRequestToServer(request);	
		}
	}

    private void cancelTrade(Remote remote){
        CancelTradeInfo info = Ancestor.parseObject(remote.getBody());
        if(info == null)
            return;
        String coffeeIndent = info.getCoffeeIndent();
        if(!TextUtils.isEmpty(coffeeIndent)){
            CancelTradeRequest request = new CancelTradeRequest(info.getUid(), coffeeIndent);
            core.sendRequestToServer(request);
        }
    }

	private void cancelTradeCart(Remote remote){
		CancelTradeCartInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) return;
		String payIndent = info.getPayIndent();
		if(!TextUtils.isEmpty(payIndent)){
			CancelTradeCartRequest request = new CancelTradeCartRequest(info.getUid(), payIndent);
			core.sendRequestToServer(request);
		}
	}

    private void updateStock(Remote remote){
        final SyncStockInfo info = Ancestor.parseObject(remote.getBody());
        if(info == null)
            return;
        LogUtil.vendor("update stock to server:  " + info.getInventory());
        UpdateStockRequest request = new UpdateStockRequest(info.getUid(), info.getInventory(), info.isAuto());
        core.sendRequestToServer(request);
        core.addRequestRetryTimer(new ResendRequestTask(request) {
            @Override
            public void onTimeout() {
                LogUtil.vendor("[CoffeeAction] updateStock->timeout");

                UpdateStockResult result = new UpdateStockResult();
                result.setAuto(info.isAuto());
                result.setResCode(ResponseCode.RES_ETIMEOUT);
                core.notifyListener(result.toRemote());
            }
        }, 0, 30);
    }

	private void addStock(Remote remote){
		final AddStockInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null)
			return;
		LogUtil.vendor("add stock to server:  " + info.getInventory());
		AddStockRequest request = new AddStockRequest(info.getUid(), info.getUserID(), info.getInventory());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] addStock->timeout");

				AddStockResult result = new AddStockResult();
				result.setResCode(ResponseCode.RES_ETIMEOUT);
				core.notifyListener(result.toRemote());
			}
		}, 0, 30);
	}

    private void getCoffeeDosingList(Remote remote){
        final GetDosingListInfo info = Ancestor.parseObject(remote.getBody());
        if(info == null)
            return;
        GetDosingListRequest request = new GetDosingListRequest(info.getUid(), info.isAuto());
        core.sendRequestToServer(request);
        core.addRequestRetryTimer(new ResendRequestTask(request) {
            @Override
            public void onTimeout() {
                LogUtil.vendor("[CoffeeAction] getCoffeeDosingList->timeout");

                GetDosingResult result = new GetDosingResult();
                result.setResCode(ResponseCode.RES_ETIMEOUT);
                result.setAuto(info.isAuto());
                core.notifyListener(result.toRemote());
            }
        }, 0, 30);
    }

	private void rollbackCartIndents(Remote remote){
		RollbackCoffeeIndentCart info = Ancestor.parseObject(remote.getBody());
		if(info == null) return;
		LogUtil.e("TEST", info.getCoffeeIndents());
		RollbackCartRequest request = new RollbackCartRequest(info.getUid(), info.getPayIndent(), info.getCoffeeIndents()
			, info.getReason());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] rollbackCartIndents->timeout");
				// TODO make it more intelligent
			}
		}, 0, 30);
	}

	private void getMachineConfig(Remote remote){
		GetMachineConfigInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) return;
		GetMachineConfigRequest request = new GetMachineConfigRequest(info.getUid());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] getMachineConfig->timeout");
				GetMachineConfigResult result = new GetMachineConfigResult();
				result.setResCode(ResponseCode.RES_ETIMEOUT);
				core.notifyListener(result.toRemote());
			}
		}, 2, 10);
	}

	private void getAdvPics(Remote remote){
		LogUtil.e("[DEBUG]",  "getAdvPics from remote");
		GetAdvPicsInfo info =  Ancestor.parseObject(remote.getBody());
		if(info == null) return;
		GetAdvPicRequest request = new GetAdvPicRequest(info.getUid());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] getAdvPics->timeout");
				GetAdvPicsResult result = new GetAdvPicsResult();
				result.setResCode(ResponseCode.RES_ETIMEOUT);
				core.notifyListener(result.toRemote());
			}
		}, 3, 10);
	}

	private void getDiscountInfo(Remote remote){
		GetDiscountInfo info = Ancestor.parseObject(remote.getBody());
		if(info == null) return;
		GetDiscountRequest request = new GetDiscountRequest(info.getUid());
		core.sendRequestToServer(request);
		core.addRequestRetryTimer(new ResendRequestTask(request) {
			@Override
			public void onTimeout() {
				LogUtil.vendor("[CoffeeAction] getDiscountInfo->timeout");
				GetDiscountResult result = new GetDiscountResult();
				result.setResCode(ResponseCode.RES_ETIMEOUT);
				core.notifyListener(result.toRemote());
			}
		}, 0, 10);
	}
}
