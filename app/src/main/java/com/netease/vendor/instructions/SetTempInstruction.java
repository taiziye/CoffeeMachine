package com.netease.vendor.instructions;

import com.netease.vendor.util.HexUtil;

public class SetTempInstruction {

	private static final int ORDER = 0x30;
	private static final int DATA_LENGTH = 0x03;
	private static final int RESET = 0x01;
	
	private int temp;
	private int constantTemp;
	
	public SetTempInstruction(int temp, int constantTemp){
		this.temp = temp;
		this.constantTemp = constantTemp;
	}
	
	public String getSetTempOrder(){
		String res = "";
		res += CoffeeMachineInstruction.START_TAG + " ";
		res += HexUtil.Int2HexString(CoffeeMachineInstruction.ADDRESS) + " ";
		res += HexUtil.Int2HexString(ORDER) + " ";
		res += HexUtil.Int2HexString(DATA_LENGTH) + " ";
		
		res += HexUtil.Int2HexString(temp) + " ";
		res += HexUtil.Int2HexString(constantTemp) + " ";
		res += HexUtil.Int2HexString(RESET) + " ";
		res += HexUtil.Int2HexString(getVerify()) + " ";
		res += CoffeeMachineInstruction.END_TAG;
		return res;
	}
	
	private int getVerify(){
		return CoffeeMachineInstruction.ADDRESS ^ ORDER ^ DATA_LENGTH ^ 
				temp ^ constantTemp ^ RESET;
	}
}
