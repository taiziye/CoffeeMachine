package com.netease.vendor.service.bean.result;

import java.util.ArrayList;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.bean.BeanAncestor;
import com.netease.vendor.service.domain.CoffeeDosingInfo;

public class VerifyCoffeeResult extends BeanAncestor {

	private int resCode;
	
	private int coffeeId; 
	
	private ArrayList<CoffeeDosingInfo> dosingList = new ArrayList<CoffeeDosingInfo>();
	
	private String coffeeIndent;
	
	private long timestamp;

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_VERIFY_COFFEE;
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

	public int getCoffeeId() {
		return coffeeId;
	}

	public void setCoffeeId(int coffeeId) {
		this.coffeeId = coffeeId;
	}

	public String getCoffeeIndent() {
		return coffeeIndent;
	}

	public void setCoffeeIndent(String coffeeIndent) {
		this.coffeeIndent = coffeeIndent;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public ArrayList<CoffeeDosingInfo> getDosingList() {
		return dosingList;
	}

	public void addDosingInfo(CoffeeDosingInfo info){
		if(dosingList == null){
			dosingList = new ArrayList<CoffeeDosingInfo>();
		}
		
		dosingList.add(info);
	}
}
