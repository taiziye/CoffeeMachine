package com.netease.vendor.instructions;

import com.netease.vendor.service.protocol.MachineStatusCode;

public class CoffeeMachineResultProcess {

	public static int processMixedCoffeeResult(String input) {
		if (input.substring(10, 12).equals("00")
				&& input.substring(4, 6).equals("3f")
				&& input.substring(8,10).equals("46")) {
            return MachineStatusCode.SUCCESS;
        } else if(input.substring(10, 12).equals("20")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.MACHINE_WARM_UP;
        } else if(input.substring(10, 12).equals("10")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.DUPLICATE_SERIAL_NUM;
        } else if(input.substring(10, 12).equals("11")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.GRINDER_SYSTEM_BUSY;
        } else if(input.substring(10, 12).equals("12")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.HEAT_TEMPERATURE_NOT_CHANGED;
        } else if(input.substring(10, 12).equals("13")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.TEMPERATURE_SENSOR_ERROR;
        } else if(input.substring(10, 12).equals("14")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.BOILER_TEMPERATURE_TO_HIGH;
        } else if(input.substring(10, 12).equals("17")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.BREW_MOTOR_TIMEOUT;
        } else if(input.substring(10, 12).equals("18")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.POWDER_SOLENOID_ABNORMAL;
        } else if(input.substring(10, 12).equals("19")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.PUMPING_TIMEOUT;
        } else if(input.substring(10, 12).equals("1a")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.WASTEWATER_TANK_EXCEED;
        } else if(input.substring(10, 12).equals("71")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.NO_CUP;
        } else if(input.substring(10, 12).equals("72")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.FOLL_CUP_SYSTEM_BUSY;
        } else if(input.substring(10, 12).equals("74")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.FOLL_CUP_MOTOR_RUN_TIMEOUT;
        } else if(input.substring(10, 12).equals("75")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.BLOCK_CUP;
        } else if(input.substring(10, 12).equals("7f")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.FOLL_CUP_SYSTEM_EXCEPTION;
        } else if(input.substring(10, 12).equals("80")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.MACHINE_BUSY;
        } else if(input.substring(10, 12).equals("81")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.MOVE_MOUTH_INSTRUCTION_LOCATION_INCORRECT;
        } else if(input.substring(10, 12).equals("82")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.MOVE_MOUTH_MOTOR_BUSY;
        } else if(input.substring(10, 12).equals("84")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.MOVE_MOUTH_MOTOR_RUN_TIMEOUT;
        } else if(input.substring(10, 12).equals("8f")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.MOVE_MOUTH_SYSTEM_EXCEPTION;
        } else if(input.substring(10, 12).equals("92")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.SPOON_MOTOR_BUSY;
        } else if(input.substring(10, 12).equals("94")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.SPOON_MOTOR_RUN_TIMEOUT;
        } else if(input.substring(10, 12).equals("9f")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.SPOON_SYSTEM_EXCEPTION;
        } else if(input.substring(10, 12).equals("f0")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.DATA_EXTRACTION_TIMEOUT;
        } else if(input.substring(10, 12).equals("ff")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.TASK_BUSY;
        } else if(input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("46")){
            return MachineStatusCode.UNKNOW_ERROR;
        }
        return MachineStatusCode.UNRELEVANT_RESULT;
	}

	public static int processBeginMixedCoffeeResult(String input) {
		if (input.substring(8, 10).equals("00")) {
			return MachineStatusCode.SUCCESS;
		} else if(input.substring(8, 10).equals("20")){
			return MachineStatusCode.MACHINE_WARM_UP;
		}else if(input.substring(8, 10).equals("76")){
			return MachineStatusCode.ALREADY_HAVE_CUP;
		} else if(input.substring(8, 10).equals("10")){
			return MachineStatusCode.DUPLICATE_SERIAL_NUM;
        } else if(input.substring(8, 10).equals("11")){
            return MachineStatusCode.GRINDER_SYSTEM_BUSY;
        } else if(input.substring(8, 10).equals("12")){
            return MachineStatusCode.HEAT_TEMPERATURE_NOT_CHANGED;
        } else if(input.substring(8, 10).equals("13")){
            return MachineStatusCode.TEMPERATURE_SENSOR_ERROR;
        } else if(input.substring(8, 10).equals("14")){
            return MachineStatusCode.BOILER_TEMPERATURE_TO_HIGH;
        } else if(input.substring(8, 10).equals("17")){
            return MachineStatusCode.BREW_MOTOR_TIMEOUT;
        } else if(input.substring(8, 10).equals("18")){
            return MachineStatusCode.POWDER_SOLENOID_ABNORMAL;
        } else if(input.substring(8, 10).equals("19")){
            return MachineStatusCode.PUMPING_TIMEOUT;
        } else if(input.substring(8, 10).equals("1a")){
            return MachineStatusCode.WASTEWATER_TANK_EXCEED;
        } else if(input.substring(8, 10).equals("2f")){
            return MachineStatusCode.TEMPERATURE_CONTROL_SYSTEM_ABNORMAL;
        } else if(input.substring(8, 10).equals("71")){
            return MachineStatusCode.NO_CUP;
        } else if(input.substring(8, 10).equals("72")){
            return MachineStatusCode.FOLL_CUP_SYSTEM_BUSY;
        } else if(input.substring(8, 10).equals("74")){
            return MachineStatusCode.FOLL_CUP_MOTOR_RUN_TIMEOUT;
        } else if(input.substring(8, 10).equals("75")){
            return MachineStatusCode.BLOCK_CUP;
        } else if(input.substring(8, 10).equals("7f")){
            return MachineStatusCode.FOLL_CUP_SYSTEM_EXCEPTION;
        } else if(input.substring(8, 10).equals("80")){
            return MachineStatusCode.MACHINE_BUSY;
        } else if(input.substring(8, 10).equals("81")){
            return MachineStatusCode.MOVE_MOUTH_INSTRUCTION_LOCATION_INCORRECT;
        } else if(input.substring(8, 10).equals("82")){
            return MachineStatusCode.MOVE_MOUTH_MOTOR_BUSY;
        } else if(input.substring(8, 10).equals("84")){
            return MachineStatusCode.MOVE_MOUTH_MOTOR_RUN_TIMEOUT;
        } else if(input.substring(8, 10).equals("8f")){
            return MachineStatusCode.MOVE_MOUTH_SYSTEM_EXCEPTION;
        } else if(input.substring(8, 10).equals("92")){
            return MachineStatusCode.SPOON_MOTOR_BUSY;
        } else if(input.substring(8, 10).equals("94")){
            return MachineStatusCode.SPOON_MOTOR_RUN_TIMEOUT;
        } else if(input.substring(8, 10).equals("9f")){
            return MachineStatusCode.SPOON_SYSTEM_EXCEPTION;
        } else if(input.substring(8, 10).equals("f0")){
            return MachineStatusCode.DATA_EXTRACTION_TIMEOUT;
        } else if(input.substring(8, 10).equals("ff")){
            return MachineStatusCode.TASK_BUSY;
        } else {
            return MachineStatusCode.UNKNOW_ERROR;
        }
	}
	
	public static String processSetTempResult(String input){
		if(input.substring(6, 8).equals("02")){
			return "success";
		}else{
			return "error";
		}
	}
	
	public static String processCleanBrewResult(String input){
		if(input.substring(8, 10).equals("00")){
			return "success";
		}else{
			return "error";
		}
	}
	
	public static String processWaterOutResult(String input){
		if(input.substring(8, 10).equals("00")){
			return "success";
		}else{
			return "error";
		}
	}
	
	public static String processBeanGrindResult(String input){
		if(input.substring(8, 10).equals("00")){
			return "success";
		}else{
			return "error";
		}
	}
	
	public static String processStickResult(String input){
		if(input.substring(8, 10).equals("00")){
			return "success";
		}else{
			return "error";
		}
	}
	
	public static String processCupTurnInOutResult(String input){
		if(input.substring(8, 10).equals("00")){
			return "success";
		}else{
			return "error";
		}
	}
	
	public static String processCupTurnResult(String input){
		if(input.substring(8, 10).equals("00")){
			return "success";
		}else{
			return "error";
		}
	}
	
	public static String processCupDropResult(String input){
		if(input.substring(8, 10).equals("00")){
			return "success";
		}else{
			return "error";
		}
	}
	
	public static String processPumpOpenCloseResult(String input){
		if(input.substring(8, 10).equals("00")){
			return "success";
		}else{
			return "error";
		}
	}
	
	public static String processTempGetResult(String input){
		if(input.substring(8, 10).equals("f0")||input.substring(8, 10).equals("ff")){
			return "error";
		}else{
			String temp = input.substring(8, 10);
            int res = -1;
            try{
                res = Integer.parseInt(temp, 16);
            }catch(Exception e){
                e.printStackTrace();
                return "error";
            }

			return res + "";
		}
	}
	
	public static String processWashingResult(String input){
		if(input.substring(8, 10).equals("00")){
			return "success";
		}else{
			return "error";
		}
	}

    public static int processQueryWashingResult(String input){
        if (input.substring(10, 12).equals("00")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("40")) {
            return MachineStatusCode.SUCCESS;
        }else if(input.substring(10, 12).equals("01")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("40")){
            return MachineStatusCode.ERROR;
        }else if(input.substring(10, 12).equals("11")
                && input.substring(4, 6).equals("3f")
                && input.substring(8,10).equals("40")){
            return MachineStatusCode.ERROR;
        }

        return MachineStatusCode.UNRELEVANT_RESULT;
    }
}
