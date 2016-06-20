package com.netease.vendor.instructions;

import com.netease.vendor.util.HexUtil;

public class WaterOutInstruction {

	private static final int ORDER = 0x35;
	private static final int DATA_LENGTH = 0x02;
	
	private int water;
	
	public WaterOutInstruction(int water){
		this.water = water;
	}
	
	public String getWaterOutOrder(){
		String res = "";
		res += CoffeeMachineInstruction.START_TAG + " ";
		res += HexUtil.Int2HexString(CoffeeMachineInstruction.ADDRESS) + " ";
		res += HexUtil.Int2HexString(ORDER) + " ";
		res += HexUtil.Int2HexString(DATA_LENGTH) + " ";
		
		if (water <= 0xFF) {
			res += "00 ";
		}
		res += HexUtil.Int2HexString(water) + " ";
		res += HexUtil.Int2HexString(getVerify()) + " ";
		res += CoffeeMachineInstruction.END_TAG;
		return res;
	}
	
	private int getVerify(){
		return CoffeeMachineInstruction.ADDRESS ^ ORDER ^ DATA_LENGTH ^ 
				(water >> 8) ^ (water & 0xff);
	}
}
