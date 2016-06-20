package com.netease.vendor.service;

/**
 * 公共交易码协议
 *  ACT_XXX what定义码
 *  ACT_XXX_xxx action动作定义码
 */
public interface ITranCode {
    
	/** 系统类交易*/
	public final int ACT_SYS 				= 1;
	/** 系统服务开始*/
	public final int ACT_SYS_FIRE 			= 1;
	/** 请求被受理*/
	public final int ACT_REQUEST_ACCEPT		= 2;
	/** 请求开始被执行*/
	public final int ACT_REQUEST_START		= 3;
	/** 请求执行完成*/
	public final int ACT_REQUEST_COMPLETE	= 4;
	/** 用户登录状态变化通知 */
	public final int ACT_SYS_STATUS_CHANGE	= 5;
	/** 从新加载数据 */
	public final int ACT_SYS_RELOAD 	    = 6;
	public final int ACT_SYS_UPDATE_FROM_SERVER   = 7;
	/** 清除用户数据 */
	public final int ACT_SYS_CLEAR_DATA = 8;
	/** 退出程序, 可选是否清空数据 */
	public final int ACT_SYS_QUIT = 9;
	public final int ACT_REFRUSH = 10;
	/** 修改用户设置 */
	public final int ACT_SYS_SAVEAPPSET = 11;
	/***导入本地通讯录**/
	public final int ACT_SYS_LOCAL_CONTACT = 12;
    /** 重启系统 **/
    public final int ACT_SYS_REBOOT = 20;

	/**用户类交易 */
	public final int ACT_USER = 100;
	/**注册 */
	public final int ACT_USER_REG = 100;
	/** 验证验证码 */
	public final int ACT_USER_VERIFY_CODE = 101;
	/**登陆 */
	public final int ACT_USER_LOGIN = 102;
	/**注销*/
	public final int ACT_USER_LOGOUT = 103;
	/**验证码截取到*/
	public final int ACT_USER_RECV_VERIFYCODE = 104;
	/** 请求心跳包*/
	public final int ACT_USER_KEEPALIVE = 105;
	/** 请求已经保存的用户名 */
	public final int ACT_USER_GET_MY_PHONE = 106;
	/** 密信用户更新密码 */
	public final int ACT_USER_UPDATE_PWD = 107; 
	/** 请求登录 **/
	public final int ACT_USER_LOGIN_REQUEST = 108;
    /** 密码验证 **/
    public final int ACT_USER_VERIFY_PWD = 109;
	
	/**咖啡类交易 */
	public final int ACT_COFFEE = 200;
	/**获取咖啡信息**/
	public final int ACT_COFFEE_GET_COFFEE = 201;
	/**验证咖啡取货码**/
	public final int ACT_COFFEE_VERIFY_COFFEE = 202;
	/**咖啡请求回滚**/
	public final int ACT_COFFEE_ROLL_BACK = 203;
	/**咖啡订单请求超时**/
	public final int ACT_COFFEE_INDENT_TIME_OUT = 204;
	/**咖啡订单回滚通知**/
	public final int ACT_COFFEE_INDENT_ROLLBACK = 205;
	/**二维码取货**/
	public final int ACT_COFFEE_VERIFY_QRCODE = 206;
	/**欢迎页面APP下载地址**/
	public final int ACT_COFFEE_APP_DOWNLOAD = 207;
    /**获取咖啡机配料列表**/
    public final int ACT_COFFEE_DOSING_LIST = 208;
	/**二维码支付请求**/
	public final int ACT_COFFEE_PAY_QRCODE = 210;
	/**二维码支付结果**/
	public final int ACT_COFFEE_PAY_NOTIFY = 211;
	/**二维码支付结果查询**/
	public final int ACT_COFFEE_ASK_PAY_RESULT = 212;
	/**阿里声波支付**/
	public final int ACT_COFFEE_PAY_SONICWAVE = 213;
    /**取消咖啡订单**/
    public final int ACT_COFFEE_CANCEL_TRADE = 214;
	/**咖啡机状态报告**/
	public final int ACT_COFFEE_REPORT_STATUS = 220;
    /**同步咖啡机库存**/
    public final int ACT_COFFEE_STOCK_UPDATE = 230;
	/**咖啡机加料**/
	public final int ACT_COFFEE_STOCK_ADD = 231;
	/**购物车支付**/
	public final int ACT_COFFEE_PAY_QRCODE_CART = 240;
	/**购物车支付情况查询**/
	public final int ACT_COFFEE_ASK_CART_PAY_RESULT = 241;
	/**购物车交易撤销**/
	public final int ACT_COFFEE_CANCEL_TRADE_CART = 242;
	/**购物车退款**/
	public final int ACT_COFFEE_ROLL_BACK_CART = 243;
	/**获取咖啡机配置信息**/
	public final int ACT_COFFEE_GET_MACHINE_CONIFG = 244;
	/***获取支付优惠信息**/
	public final int ACT_COFFEE_GET_DISCOUNT = 245;
	/**获得广告海报列表***/
	public final int ACT_COFFEE_GET_ADV_PICS = 246;
	
	/**串口相关操作**/
	public final int ACT_COFFEE_SERIAL_PORT = 300;
	public final int ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE = 301;
	public final int ACT_COFFEE_SERIAL_PORT_INIT = 302;
	public final int ACT_COFFEE_SERIAL_PORT_INIT_RESULT = 303;
    public final int ACT_COFFEE_SERIAL_PORT_WASHING = 304;
	
	/**串口调试相关操作**/
	public final int ACT_COFFEE_SERIAL_PORT_TEST = 304;
	public final int ACT_COFFEE_SERIAL_PORT_TEST_SET_TEMP = 305;
	public final int ACT_COFFEE_SERIAL_PORT_TEST_CLEAN_BREW = 306;
	public final int ACT_COFFEE_SERIAL_PORT_TEST_WATER_OUT = 307;
	public final int ACT_COFFEE_SERIAL_PORT_TEST_BEAN_GRIND = 308;
	public final int ACT_COFFEE_SERIAL_PORT_TEST_STICK = 309;
	public final int ACT_COFFEE_SERIAL_PORT_TEST_CUP_TURN_IN_OUT = 310;
	public final int ACT_COFFEE_SERIAL_PORT_TEST_CUP_TURN = 312;
	public final int ACT_COFFEE_SERIAL_PORT_TEST_CUP_DROP = 313;
	public final int ACT_COFFEE_SERIAL_PORT_TEST_PUMP_OPEN_CLOSE = 314;
	public final int ACT_COFFEE_SERIAL_PORT_TEST_TEMP_GET = 316;
	public final int ACT_COFFEE_SERIAL_PORT_TEST_WASHING = 317;

	//-------------------------result code---------------------------
	public final static int RESULT_OK                            				=   10000;
	public final static int RESULT_FAILED                        				=   10001;
	public final static int RESULT_FAILED_WITH_DATA_NOT_READY    				=   10002;
	public final static int RESULT_DATA_READY_OK                 				=   10003;
	public final static int RESULT_DATA_CHANGE                                  =   10004;
	public final static int RESULT_DATA_CHANGEED_OK                             =   10005;
	public final static int RESULT_UPDATE_OK                                    =   10005;
	public final static int RESULT_LOAD_LOCAL_CONTACT_FAIL                      =   10006;
	
	//------------------------login status---------------------------
	public final static int STATUS_LOGINED			= 11000; // 正常状态
	public final static int STATUS_LOGGING			= 11001; // 正在登录状态
	public final static int STATUS_CONNECT_FAILED	= 11002; // 连接服务器失败
	public final static int STATUS_NO_NETWORK		= 11003; // 本地没有网络连接
	public final static int STATUS_KICKOUT			= 11004; // 被其他端登录踢下，需要重新注册
	public final static int STATUS_FORBIDDEN		= 11005; // 被禁言
	public final static int STATUS_UNLOGIN			= 11006; // 未登录状态
}
