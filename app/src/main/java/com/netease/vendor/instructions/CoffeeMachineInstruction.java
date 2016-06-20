package com.netease.vendor.instructions;

/**
 * 
 * @author hzyemaowei
 *
 * 咖啡机指令
 */
public class CoffeeMachineInstruction {

	public static final String START_TAG = "AA";
	public static final int ADDRESS = 16;
	public static final String END_TAG = "EE";
	
	public static final String SYNC = "AA 10 0A 01 5F 05 42 EE";
	//取版本号
	public static final String VERSION = "AA 10 C0 00 D0 EE";
	//出棒测试
	public static final String STICK_TEST = "AA 10 10 00 00 EE";
	//出棒
	public static final String STICK = "AA 10 11 00 01 EE";
	//查看搅拌电机当前状态
	public static final String STICK_STATUS = "AA 10 13 00 03 EE";
	//杯嘴转到里面
	public static final String CUP_TURN_IN = "AA 10 25 01 00 34 EE";
	//杯嘴转到外面
	public static final String CUP_TURN_OUT = "AA 10 25 01 01 35 EE";
	//杯筒转动测试
	public static final String CUP_TURN_TEST = "AA 10 22 00 32 EE";
	//杯筒转动
	public static final String CUP_TURN = "AA 10 23 00 33 EE";
	//落杯测试
	public static final String CUP_DROP_TEST = "AA 10 20 00 30 EE";
	//落杯
	public static final String CUP_DROP = "AA 10 21 01 01 31 EE";
    //锅炉排气（开）
    public static final String WATER_INJECTION_ON = "AA 10 3A 01 01 2A EE";
    //锅炉排气（关）
    public static final String WATER_INJECTION_OFF = "AA 10 3A 01 00 2B EE";
	//设置锅炉温度，回差5度
	public static final String SET_TEMP = "AA 10 30 03 69 67 01 2C EE";
	//打开水泵
	public static final String PUMP_OPEN = "AA 10 31 01 01 21 EE";
	//关闭水泵
	public static final String PUMP_CLOSE = "AA 10 31 01 00 20 EE";
	//打开磨豆电机
	public static final String BEAN_ELECTRICAL_OPEN = "AA 10 32 01 01 22 EE";
	//关闭磨豆电机
	public static final String BEAN_ELECTRICAL_CLOSE = "AA 10 32 01 00 23 EE";
	//打开冲泡电机
	public static final String BREW_ELECTRICAL_OPEN = "AA 10 33 01 01 23 EE";
	//关闭冲泡电机
	public static final String BREW_ELECTRICAL_CLOSE = "AA 10 33 01 00 22 EE";
	//打开下粉电磁阀
	public static final String POWDER_ELECTRICAL_OPEN = "AA 10 34 01 01 24 EE";
	//关闭下粉电磁阀
	public static final String POWDER_ELECTRICAL_CLOSE = "AA 10 34 01 00 25 EE";
	//出水10ml
	public static final String WATER_OUT = "AA 10 35 02 00 0A 2D EE";
	//清洗冲泡器10ml
	public static final String CLEAN_BREW = "AA 10 36 02 00 0A 2E EE";
	//打磨豆咖啡10ml
	public static final String BEAN_GRIND = "AA 10 37 02 00 0A 2F EE";
	//获取锅炉当前温度
	public static final String TEMP_GET = "AA 10 38 00 28 EE";
	//获取磨豆系统当前状态
	public static final String GRIND_STATUS_GET = "AA 10 39 00 29 EE";
	//最后一条指令执行结果
	public static final String LAST_EXE_ORDER = "AA 10 3F 00 2F EE";
	//清洗
	public static final String WASHING = "AA 10 40 02 00 32 60 EE";
	//全检
	public static final String CHECKING = "AA 10 41 00 51 EE";
}
