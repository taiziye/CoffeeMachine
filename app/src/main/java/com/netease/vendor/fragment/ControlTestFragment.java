package com.netease.vendor.fragment;

import java.util.HashMap;
import java.util.Map;

import com.netease.vendor.R;
import com.netease.vendor.common.fragment.TFragment;
import com.netease.vendor.instructions.BeanGrindInstruction;
import com.netease.vendor.instructions.CleanBrewInstruction;
import com.netease.vendor.instructions.CoffeeMachineInstruction;
import com.netease.vendor.instructions.CoffeeMachineResultProcess;
import com.netease.vendor.instructions.SetTempInstruction;
import com.netease.vendor.instructions.WaterOutInstruction;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.ui.GenericSettingDialog;
import com.netease.vendor.util.SerialPortDataWritter;
import com.netease.vendor.util.ToastUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ControlTestFragment extends TFragment implements OnClickListener {

	private static final String TEMP = "温度(100-115)";
	private static final Integer TEMP_VALUE = 113;
	private static final Integer TEMP_VALUE_MAX = 115;
	private static final Integer TEMP_VALUE_MIN = 100;
	private static final String CONSTANT_TEMP = "恒温(95-103)";
	private static final Integer CONSTANT_TEMP_VALUE = 95;
	private static final Integer CONSTANT_TEMP_VALUE_MAX = 103;
	private static final Integer CONSTANT_TEMP_VALUE_MIN = 95;

	private static final String CLEAN_BREW_WATER = "清洗水量(1-550)";
	private static final Integer CLEAN_BREW_WATER_VALUE = 10;
	private static final Integer CLEAN_BREW_WATER_VALUE_MAX = 550;
	private static final Integer CLEAN_BREW_WATER_VALUE_MIN = 1;

	private static final String BEAN_GRIND = "咖啡水量(1-550)";
	private static final Integer BEAN_GRIND_VALUE = 10;
	private static final Integer BEAN_GRIND_VALUE_MAX = 550;
	private static final Integer BEAN_GRIND_VALUE_MIN = 1;

	private Button mSetTemp;
	private Button mCleanBrew;
	private Button mWaterInjectionOn;
    private Button mWaterInjectionOff;
	private Button mBeanGrind;
	private Button mStick;
	private Button mCupTurnIn;
	private Button mCupTurnOut;
	private Button mCupTurn;
	private Button mCupDrop;
	private Button mPumpOpen;
	private Button mPumpClose;
	private Button mTempGet;
	private Button mWashing;

	public ControlTestFragment() {
		this.setFragmentId(R.id.machine_debug_fragment);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_control_test, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
	}

	private void initView() {
		mSetTemp = (Button) getView().findViewById(
				R.id.control_test_set_temp_instruction);
		mSetTemp.setOnClickListener(mSetTempOnClick);

		mCleanBrew = (Button) getView().findViewById(
				R.id.control_test_clean_brew_instruction);
		mCleanBrew.setOnClickListener(mCleanBrewOnClick);

		mWaterInjectionOn = (Button) getView().findViewById(
				R.id.control_test_water_injection_on_instruction);
        mWaterInjectionOn.setOnClickListener(this);

        mWaterInjectionOff = (Button) getView().findViewById(
                R.id.control_test_water_injection_off_instruction);
        mWaterInjectionOff.setOnClickListener(this);

		mBeanGrind = (Button) getView().findViewById(
				R.id.control_test_bean_grind_instruction);
		mBeanGrind.setOnClickListener(mBeanGrindOnClick);

		mStick = (Button) getView().findViewById(
				R.id.control_test_stick_instruction);
		mStick.setOnClickListener(this);

		mCupTurnIn = (Button) getView().findViewById(
				R.id.control_test_cup_turn_in_instruction);
		mCupTurnIn.setOnClickListener(this);

		mCupTurnOut = (Button) getView().findViewById(
				R.id.control_test_cup_turn_out_instruction);
		mCupTurnOut.setOnClickListener(this);

		mCupTurn = (Button) getView().findViewById(
				R.id.control_test_cup_turn_instruction);
		mCupTurn.setOnClickListener(this);

		mCupDrop = (Button) getView().findViewById(
				R.id.control_test_cup_drop_instruction);
		mCupDrop.setOnClickListener(this);

		mPumpOpen = (Button) getView().findViewById(
				R.id.control_test_pump_open_instruction);
		mPumpOpen.setOnClickListener(this);

		mPumpClose = (Button) getView().findViewById(
				R.id.control_test_pump_close_instruction);
		mPumpClose.setOnClickListener(this);

		mTempGet = (Button) getView().findViewById(
				R.id.control_test_temp_get_instruction);
		mTempGet.setOnClickListener(this);

		mWashing = (Button) getView().findViewById(
				R.id.control_test_washing_instruction);
		mWashing.setOnClickListener(this);
	}

	private OnClickListener mSetTempOnClick = new OnClickListener() {

		private Map<String, String> map;

		@Override
		public void onClick(View v) {
			map = new HashMap<String, String>();
			map.put(TEMP, TEMP_VALUE.toString());
			map.put(CONSTANT_TEMP, CONSTANT_TEMP_VALUE.toString());
			GenericSettingDialog dialog = new GenericSettingDialog(
					getActivity(), map,
					new GenericSettingDialog.OnGenericSettingDialog() {

						@Override
						public void onCancel() {
						}

						@Override
						public boolean onConfirm(Map<String, String> resultMap) {
                            try{
                                int temp = Integer.parseInt(resultMap.get(TEMP));
                                int constantTemp = Integer.parseInt(resultMap.get(CONSTANT_TEMP));
                                if (temp < TEMP_VALUE_MIN || temp > TEMP_VALUE_MAX) {
                                    ToastUtil.showToast(getActivity(), TEMP
                                            + "设置超出范围");
                                    return false;
                                }
                                if (constantTemp < CONSTANT_TEMP_VALUE_MIN
                                        || constantTemp > CONSTANT_TEMP_VALUE_MAX) {
                                    ToastUtil.showToast(getActivity(),
                                            CONSTANT_TEMP + "设置超出范围");
                                    return false;
                                }
                                SetTempInstruction instruction = new SetTempInstruction(
                                        temp, constantTemp);
                                SerialPortDataWritter.writeData(instruction
                                        .getSetTempOrder());
                                ToastUtil.showToast(getActivity(),
                                        instruction.getSetTempOrder());
                                return true;
                            }catch(Exception e){
                                ToastUtil.showToast(getActivity(), "输入格式错误");
                            }
                            return false;
						}
					});
			dialog.show();
		}

	};

	private OnClickListener mCleanBrewOnClick = new OnClickListener() {

		private Map<String, String> map;

		@Override
		public void onClick(View v) {
			map = new HashMap<String, String>();
			map.put(CLEAN_BREW_WATER, CLEAN_BREW_WATER_VALUE.toString());
			GenericSettingDialog dialog = new GenericSettingDialog(
					getActivity(), map,
					new GenericSettingDialog.OnGenericSettingDialog() {

						@Override
						public void onCancel() {
						}

						@Override
						public boolean onConfirm(Map<String, String> resultMap) {
                            try{
                                int water = Integer.parseInt(resultMap.get(CLEAN_BREW_WATER));
                                if (water < CLEAN_BREW_WATER_VALUE_MIN
                                        || water > CLEAN_BREW_WATER_VALUE_MAX) {
                                    ToastUtil.showToast(getActivity(),
                                            CLEAN_BREW_WATER + "设置超出范围");
                                    return false;
                                }
                                CleanBrewInstruction instruction = new CleanBrewInstruction(
                                        water);
                                SerialPortDataWritter.writeData(instruction
                                        .getCleanBrewOrder());
                                ToastUtil.showToast(getActivity(),
                                        instruction.getCleanBrewOrder());
                                return true;
                            }catch(Exception e){
                                ToastUtil.showToast(getActivity(), CLEAN_BREW_WATER
                                        + "输入格式错误");
                            }
                            return false;
						}
					});
			dialog.show();
		}

	};

	private OnClickListener mBeanGrindOnClick = new OnClickListener() {

		private Map<String, String> map;

		@Override
		public void onClick(View v) {
			map = new HashMap<String, String>();
			map.put(BEAN_GRIND, BEAN_GRIND_VALUE.toString());
			GenericSettingDialog dialog = new GenericSettingDialog(
					getActivity(), map,
					new GenericSettingDialog.OnGenericSettingDialog() {

						@Override
						public void onCancel() {
						}

						@Override
						public boolean onConfirm(Map<String, String> resultMap) {
                            try{
                                int water = Integer.parseInt(resultMap.get(BEAN_GRIND));
                                if (water < BEAN_GRIND_VALUE_MIN
                                        || water > BEAN_GRIND_VALUE_MAX) {
                                    ToastUtil.showToast(getActivity(), BEAN_GRIND
                                            + "设置超出范围");
                                    return false;
                                }
                                BeanGrindInstruction instruction = new BeanGrindInstruction(
                                        water);
                                SerialPortDataWritter.writeData(instruction
                                        .getBeanGrindOrder());
                                ToastUtil.showToast(getActivity(),
                                        instruction.getBeanGrindOrder());
                                return true;
                            }catch(Exception e){
                                ToastUtil.showToast(getActivity(), BEAN_GRIND
                                        + "输入格式错误");
                            }
                            return false;
						}
					});
			dialog.show();
		}

	};

	/**
	 * 这个里面的所有的操作都是在本地完成的,为什么最后会处理onReceive的事件
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.control_test_water_injection_on_instruction:
				SerialPortDataWritter
						.writeData(CoffeeMachineInstruction.WATER_INJECTION_ON);
				ToastUtil.showToast(getActivity(),
						CoffeeMachineInstruction.WATER_INJECTION_ON);
				break;
			case R.id.control_test_water_injection_off_instruction:
				SerialPortDataWritter
						.writeData(CoffeeMachineInstruction.WATER_INJECTION_OFF);
				ToastUtil.showToast(getActivity(),
						CoffeeMachineInstruction.WATER_INJECTION_OFF);
				break;
			case R.id.control_test_stick_instruction:
			SerialPortDataWritter.writeData(CoffeeMachineInstruction.STICK);
			ToastUtil.showToast(getActivity(), CoffeeMachineInstruction.STICK);
			break;
			case R.id.control_test_cup_turn_in_instruction:
			SerialPortDataWritter
					.writeData(CoffeeMachineInstruction.CUP_TURN_IN);
			ToastUtil.showToast(getActivity(),
					CoffeeMachineInstruction.CUP_TURN_IN);
			break;
			case R.id.control_test_cup_turn_out_instruction:
			SerialPortDataWritter
					.writeData(CoffeeMachineInstruction.CUP_TURN_OUT);
			ToastUtil.showToast(getActivity(),
					CoffeeMachineInstruction.CUP_TURN_OUT);
			break;
		case R.id.control_test_cup_turn_instruction:
			SerialPortDataWritter.writeData(CoffeeMachineInstruction.CUP_TURN);
			ToastUtil.showToast(getActivity(),
					CoffeeMachineInstruction.CUP_TURN);
			break;
		case R.id.control_test_cup_drop_instruction:
			SerialPortDataWritter.writeData(CoffeeMachineInstruction.CUP_DROP);
			ToastUtil.showToast(getActivity(),
					CoffeeMachineInstruction.CUP_DROP);
			break;
		case R.id.control_test_pump_open_instruction:
			SerialPortDataWritter.writeData(CoffeeMachineInstruction.PUMP_OPEN);
			ToastUtil.showToast(getActivity(),
					CoffeeMachineInstruction.PUMP_OPEN);
			break;
		case R.id.control_test_pump_close_instruction:
			SerialPortDataWritter
					.writeData(CoffeeMachineInstruction.PUMP_CLOSE);
			ToastUtil.showToast(getActivity(),
					CoffeeMachineInstruction.PUMP_CLOSE);
			break;
		case R.id.control_test_temp_get_instruction:
			SerialPortDataWritter.writeData(CoffeeMachineInstruction.TEMP_GET);
			ToastUtil.showToast(getActivity(),
					CoffeeMachineInstruction.TEMP_GET);
			break;
		case R.id.control_test_washing_instruction:
			SerialPortDataWritter.writeData(CoffeeMachineInstruction.WASHING);
			ToastUtil
					.showToast(getActivity(), CoffeeMachineInstruction.WASHING);
			break;
		default:
			break;
		}
	}

	@Override
	public void onReceive(Remote remote) {
		if (remote.getWhat() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST) {
			if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_SET_TEMP) {
				String res = remote.getBody();
				String result = CoffeeMachineResultProcess
						.processSetTempResult(res);
				if (result.equals("success")) {
					ToastUtil.showToast(getActivity(), "设置温度成功");
				} else {
					ToastUtil.showToast(getActivity(), "设置温度失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_CLEAN_BREW) {
				String res = remote.getBody();
				String result = CoffeeMachineResultProcess
						.processCleanBrewResult(res);
				if (result.equals("success")) {
					ToastUtil.showToast(getActivity(), "清洗成功");
				} else {
					ToastUtil.showToast(getActivity(), "清洗失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_WATER_OUT) {
				String res = remote.getBody();
				String result = CoffeeMachineResultProcess
						.processWaterOutResult(res);
				if (result.equals("success")) {
					ToastUtil.showToast(getActivity(), "出水成功");
				} else {
					ToastUtil.showToast(getActivity(), "出水失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_BEAN_GRIND) {
				String res = remote.getBody();
				String result = CoffeeMachineResultProcess
						.processBeanGrindResult(res);
				if (result.equals("success")) {
					ToastUtil.showToast(getActivity(), "打磨豆咖啡成功");
				} else {
					ToastUtil.showToast(getActivity(), "打磨豆咖啡失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_STICK) {
				String res = remote.getBody();
				String result = CoffeeMachineResultProcess
						.processStickResult(res);
				if (result.equals("success")) {
					ToastUtil.showToast(getActivity(), "出棒成功");
				} else {
					ToastUtil.showToast(getActivity(), "出棒失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_CUP_TURN_IN_OUT) {
				String res = remote.getBody();
				String result = CoffeeMachineResultProcess
						.processCupTurnInOutResult(res);
				if (result.equals("success")) {
					ToastUtil.showToast(getActivity(), "杯嘴转向面成功");
				} else {
					ToastUtil.showToast(getActivity(), "杯嘴转向面失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_CUP_TURN) {
				String res = remote.getBody();
				String result = CoffeeMachineResultProcess
						.processCupTurnResult(res);
				if (result.equals("success")) {
					ToastUtil.showToast(getActivity(), "杯筒转动成功");
				} else {
					ToastUtil.showToast(getActivity(), "杯筒转动失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_CUP_DROP) {
				String res = remote.getBody();
				String result = CoffeeMachineResultProcess
						.processCupDropResult(res);
				if (result.equals("success")) {
					ToastUtil.showToast(getActivity(), "落杯成功");
				} else {
					ToastUtil.showToast(getActivity(), "落杯失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_PUMP_OPEN_CLOSE) {
				String res = remote.getBody();
				String result = CoffeeMachineResultProcess
						.processPumpOpenCloseResult(res);
				if (result.equals("success")) {
					ToastUtil.showToast(getActivity(), "开关水泵成功");
				} else {
					ToastUtil.showToast(getActivity(), "开关水泵失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_TEMP_GET) {
				String res = remote.getBody();
				String result = CoffeeMachineResultProcess
						.processTempGetResult(res);
				if (!result.equals("error")) {
					ToastUtil.showToast(getActivity(), "锅炉当前温度："+result);
				} else {
					ToastUtil.showToast(getActivity(), "获取锅炉温度失败");
				}
			} else if (remote.getAction() == ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_WASHING) {
				String res = remote.getBody();
				String result = CoffeeMachineResultProcess
						.processWashingResult(res);
				if (result.equals("success")) {
					ToastUtil.showToast(getActivity(), "清洗成功");
				} else {
					ToastUtil.showToast(getActivity(), "清洗失败");
				}
			}
		}
	}
}
