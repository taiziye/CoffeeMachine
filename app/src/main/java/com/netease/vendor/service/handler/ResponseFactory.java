package com.netease.vendor.service.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.netease.vendor.service.handler.coffee.AddStockHandler;
import com.netease.vendor.service.handler.coffee.AppDownloadResponseHandler;
import com.netease.vendor.service.handler.coffee.CancelTradeCartHandler;
import com.netease.vendor.service.handler.coffee.CancelTradeHandler;
import com.netease.vendor.service.handler.coffee.GetAdvPicsResponseHandler;
import com.netease.vendor.service.handler.coffee.GetCoffeeResponseHandler;
import com.netease.vendor.service.handler.coffee.GetDicountResponseHandler;
import com.netease.vendor.service.handler.coffee.GetDosingListResponseHandler;
import com.netease.vendor.service.handler.coffee.GetMachineConfigResponseHandler;
import com.netease.vendor.service.handler.coffee.MachineStatusReportResponseHandler;
import com.netease.vendor.service.handler.coffee.MachineStatusServerResponseHandler;
import com.netease.vendor.service.handler.coffee.PayQrcodeCartResponseHandler;
import com.netease.vendor.service.handler.coffee.PayQrcodeResponseHandler;
import com.netease.vendor.service.handler.coffee.PayNotifyResponseHandler;
import com.netease.vendor.service.handler.coffee.PaySonicWaveResponseHandler;
import com.netease.vendor.service.handler.coffee.PayStatusCartResponseHandler;
import com.netease.vendor.service.handler.coffee.PayStatusResponseHandler;
import com.netease.vendor.service.handler.coffee.RollbackCartIndentResponseHandler;
import com.netease.vendor.service.handler.coffee.RollbackIndentResponseHandler;
import com.netease.vendor.service.handler.coffee.UpdateStockHandler;
import com.netease.vendor.service.handler.coffee.VerifyCoffeeResponseHandler;
import com.netease.vendor.service.handler.coffee.VerifyQrcodeResponseHandler;
import com.netease.vendor.service.protocol.LinkFrame;
import com.netease.vendor.service.protocol.response.KeepAliveResponse;
import com.netease.vendor.service.protocol.response.LoginResponse;
import com.netease.vendor.service.protocol.response.Response;
import com.netease.vendor.service.protocol.response.ResponseID;
import com.netease.vendor.service.protocol.response.UserKickedResponse;
import com.netease.vendor.service.protocol.response.coffee.AddStockResponse;
import com.netease.vendor.service.protocol.response.coffee.AppDownloadResponse;
import com.netease.vendor.service.protocol.response.coffee.CancelTradeCartResponse;
import com.netease.vendor.service.protocol.response.coffee.CancelTradeResponse;
import com.netease.vendor.service.protocol.response.coffee.GetAdvPicResponse;
import com.netease.vendor.service.protocol.response.coffee.GetCoffeeResponse;
import com.netease.vendor.service.protocol.response.coffee.GetDiscountResponse;
import com.netease.vendor.service.protocol.response.coffee.GetDosingListResponse;
import com.netease.vendor.service.protocol.response.coffee.GetMachineConfigResponse;
import com.netease.vendor.service.protocol.response.coffee.MachineStatusReportResponse;
import com.netease.vendor.service.protocol.response.coffee.MachineStatusServerResponse;
import com.netease.vendor.service.protocol.response.coffee.PayQrcodeCartResponse;
import com.netease.vendor.service.protocol.response.coffee.PayQrcodeResponse;
import com.netease.vendor.service.protocol.response.coffee.PayNotifyResponse;
import com.netease.vendor.service.protocol.response.coffee.PaySonicWaveResponse;
import com.netease.vendor.service.protocol.response.coffee.PayStatusAskCartResponse;
import com.netease.vendor.service.protocol.response.coffee.PayStatusAskResponse;
import com.netease.vendor.service.protocol.response.coffee.RollbackCartResponse;
import com.netease.vendor.service.protocol.response.coffee.RollbackResponse;
import com.netease.vendor.service.protocol.response.coffee.UpdateStockResponse;
import com.netease.vendor.service.protocol.response.coffee.VerifyCoffeeResponse;
import com.netease.vendor.service.protocol.response.coffee.VerifyQrcodeResponse;

import android.util.Pair;

public class ResponseFactory {
	private static ResponseFactory sInstance = new ResponseFactory();
	
	public static ResponseFactory getInstance() {
		return sInstance;
	}
	
	private ResponseFactory() {
		registerResponses();
	}
	
	private Map<Pair<Short, Short>, Class<? extends Response>> mResponses = new ConcurrentHashMap<Pair<Short, Short>, Class<? extends Response>>();
	private Map<Pair<Short, Short>, Integer> mPriorities = new ConcurrentHashMap<Pair<Short, Short>, Integer>();
	private Map<Class<? extends Response>, ResponseHandler> mHandlers = new ConcurrentHashMap<Class<? extends Response>, ResponseHandler>();

	private void registerResponses() {
		
		/**心跳管理**/
		registerResponse(KeepAliveResponse.class, null);

		/**登录部分**/
		registerResponse(LoginResponse.class, new LoginResponseHandler());
        registerResponse(UserKickedResponse.class, new UserKickedResponseHandler());
		
		/**咖啡部分**/
		registerResponse(GetCoffeeResponse.class, new GetCoffeeResponseHandler());
		registerResponse(VerifyCoffeeResponse.class, new VerifyCoffeeResponseHandler());
		registerResponse(PayNotifyResponse.class, new PayNotifyResponseHandler());
		registerResponse(RollbackResponse.class, new RollbackIndentResponseHandler());
		registerResponse(VerifyQrcodeResponse.class, new VerifyQrcodeResponseHandler());
        registerResponse(GetDosingListResponse.class, new GetDosingListResponseHandler());
        registerResponse(UpdateStockResponse.class, new UpdateStockHandler());
		registerResponse(AddStockResponse.class, new AddStockHandler());

		/**运维管理**/
		registerResponse(MachineStatusServerResponse.class, new MachineStatusServerResponseHandler());
		registerResponse(MachineStatusReportResponse.class, new MachineStatusReportResponseHandler());
		registerResponse(AppDownloadResponse.class, new AppDownloadResponseHandler());
		registerResponse(GetMachineConfigResponse.class, new GetMachineConfigResponseHandler());
		
		/**支付部分**/
		registerResponse(PayQrcodeResponse.class, new PayQrcodeResponseHandler());
		registerResponse(PayNotifyResponse.class, new PayNotifyResponseHandler());
		registerResponse(PayStatusAskResponse.class, new PayStatusResponseHandler());
		registerResponse(PaySonicWaveResponse.class, new PaySonicWaveResponseHandler());
        registerResponse(CancelTradeResponse.class, new CancelTradeHandler());
		registerResponse(PayQrcodeCartResponse.class, new PayQrcodeCartResponseHandler());
		registerResponse(PayStatusAskCartResponse.class, new PayStatusCartResponseHandler());
		registerResponse(CancelTradeCartResponse.class, new CancelTradeCartHandler());
		registerResponse(RollbackCartResponse.class, new RollbackCartIndentResponseHandler());
		registerResponse(GetDiscountResponse.class, new GetDicountResponseHandler());

		/**广告**/
		registerResponse(GetAdvPicResponse.class, new GetAdvPicsResponseHandler());
	}
	
	private void registerResponse(Class<? extends Response> clazz, ResponseHandler handler) {
		ResponseID annotation = (ResponseID) clazz.getAnnotation(ResponseID.class);
		if (annotation == null) {
			return;
		}
		
		short sid = annotation.service();
		String[] commands = annotation.command();
		if (commands != null && commands.length != 0) {
			for (String command : commands) {
				String[] parts = command.split("#");
				
				if (parts != null && parts.length != 0) {
					short cid = Short.parseShort(parts[0]);

					if (parts.length >= 2) {
						int priority = Integer.parseInt(parts[1]);
						mPriorities.put(new Pair<Short, Short>(sid, cid), priority);
					}
					
					mResponses.put(new Pair<Short, Short>(sid, cid), clazz);
				}				
			}
		}

		if (handler != null) {
			mHandlers.put(clazz, handler);
		}
	}
	
	public boolean existsResponse(LinkFrame header) {		
		return header == null || mResponses == null ? false : mResponses.containsKey(new Pair<Short, Short>(header.serviceId, header.commandId));
	}
	
	public Class<? extends Response> queryResponseClass(LinkFrame header) {		
		return header == null || mResponses == null ? null : mResponses.get(new Pair<Short, Short>(header.serviceId, header.commandId));
	}
	
	public Integer queryResponsePriority(LinkFrame header) {		
		return header == null || mPriorities == null ? null : mPriorities.get(new Pair<Short, Short>(header.serviceId, header.commandId));
	}
	
	public ResponseHandler queryResponseHandler(Response response) {		
		return response == null || mHandlers == null ? null : mHandlers.get(response.getClass());
	}
	
	public Response newResponse(LinkFrame header) {	
		// query class
		Class<? extends Response> clazz = queryResponseClass(header);

		if (clazz == null) {
			return null;
		}
		
		try {
			// new
			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}
}
