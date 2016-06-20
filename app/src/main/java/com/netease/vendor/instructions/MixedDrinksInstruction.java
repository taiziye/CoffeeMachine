package com.netease.vendor.instructions;

import com.netease.vendor.util.HexUtil;

/**
 * @author hzyemaowei
 *         <p/>
 *         打混合咖啡指令
 */
public class MixedDrinksInstruction {

    private static final int ORDER = 70;
    private static final int DATA_LENGTH = 0x28;
    private static final int CONFIG_BYTE1 = 0x04;
    private static final int CONFIG_BYTE2 = 0x05;
    private static final int SUGER = 0;
    private static final int SERIAL_NUM = 1;

    private static final int MAX_WATER = 550;
    private static final int MAX_SUGER = 250;
    private static final int MAX_ACCESSORY = 9;
    private static final int MAX_ACCESSORY_TIME = 250;
    private static final int MAX_CONFIG = 0xFF;
    private static final int MAX_SERIALNUM = 0xFFFF;
    private static final int MAX_STIRRING_TIME = 0xFF;
    private static final int MAX_RATE = 50;
    // 1byte,糖量
    int suger;
    // 料盒编号，1byte
    int accessoryNum1;
    int accessoryNum2;
    int accessoryNum3;
    int accessoryNum4;
    int accessoryNum5;
    // 料盒出料时间，1byte
    int accessoryNum1Time;
    int accessoryNum2Time;
    int accessoryNum3Time;
    int accessoryNum4Time;
    int accessoryNum5Time;
    // 料盒水量，2byte
    int accessoryNum1Water;
    int accessoryNum2Water;
    int accessoryNum3Water;
    int accessoryNum4Water;
    int accessoryNum5Water;
    // 料盒搅拌延时，1byte
    int accessoryNum1StirringTime;
    int accessoryNum2StirringTime;
    int accessoryNum3StirringTime;
    int accessoryNum4StirringTime;
    int accessoryNum5StirringTime;
    // 料盒搅拌调速比，1byte
    int accessoryNum1StirringRate;
    int accessoryNum2StirringRate;
    int accessoryNum3StirringRate;
    int accessoryNum4StirringRate;
    int accessoryNum5StirringRate;
    // 料盒出料速度比，1byte
    int accessoryNum1Rate;
    int accessoryNum2Rate;
    int accessoryNum3Rate;
    int accessoryNum4Rate;
    int accessoryNum5Rate;
    int serialNum;

    public MixedDrinksInstruction(int accessoryNum1,
                                  int accessoryNum2, int accessoryNum3, int accessoryNum4,
                                  int accessoryNum5, int accessoryNum1Time, int accessoryNum2Time,
                                  int accessoryNum3Time, int accessoryNum4Time,
                                  int accessoryNum5Time, int accessoryNum1Water, int accessoryNum2Water,
                                  int accessoryNum3Water, int accessoryNum4Water, int accessoryNum5Water) {
        this.suger = SUGER;
        this.accessoryNum1 = accessoryNum1;
        this.accessoryNum2 = accessoryNum2;
        this.accessoryNum3 = accessoryNum3;
        this.accessoryNum4 = accessoryNum4;
        this.accessoryNum5 = accessoryNum5;
        this.accessoryNum1Time = accessoryNum1Time;
        this.accessoryNum2Time = accessoryNum2Time;
        this.accessoryNum3Time = accessoryNum3Time;
        this.accessoryNum4Time = accessoryNum4Time;
        this.accessoryNum5Time = accessoryNum5Time;
        this.accessoryNum1Water = accessoryNum1Water;
        this.accessoryNum2Water = accessoryNum2Water;
        this.accessoryNum3Water = accessoryNum3Water;
        this.accessoryNum4Water = accessoryNum4Water;
        this.accessoryNum5Water = accessoryNum5Water;
        this.accessoryNum1StirringTime = 0;
        this.accessoryNum2StirringTime = 0;
        this.accessoryNum3StirringTime = 0;
        this.accessoryNum4StirringTime = 0;
        this.accessoryNum5StirringTime = 0;
        this.accessoryNum1StirringRate = 0;
        this.accessoryNum2StirringRate = 0;
        this.accessoryNum3StirringRate = 0;
        this.accessoryNum4StirringRate = 0;
        this.accessoryNum5StirringRate = 0;
        this.accessoryNum1Rate = 0;
        this.accessoryNum2Rate = 0;
        this.accessoryNum3Rate = 0;
        this.accessoryNum4Rate = 0;
        this.accessoryNum5Rate = 0;
        this.serialNum = SERIAL_NUM;
    }

    public MixedDrinksInstruction(int accessoryNum1,
                                  int accessoryNum2, int accessoryNum3, int accessoryNum4,
                                  int accessoryNum5, int accessoryNum1Time, int accessoryNum2Time,
                                  int accessoryNum3Time, int accessoryNum4Time,
                                  int accessoryNum5Time, int accessoryNum1Water, int accessoryNum2Water,
                                  int accessoryNum3Water, int accessoryNum4Water, int accessoryNum5Water,
                                  int accessoryNum1StirringTime, int accessoryNum2StirringTime, int accessoryNum3StirringTime,
                                  int accessoryNum4StirringTime, int accessoryNum5StirringTime, int accessoryNum1StirringRate,
                                  int accessoryNum2StirringRate, int accessoryNum3StirringRate, int accessoryNum4StirringRate,
                                  int accessoryNum5StirringRate, int accessoryNum1Rate, int accessoryNum2Rate,
                                  int accessoryNum3Rate, int accessoryNum4Rate, int accessoryNum5Rate) {
        this.suger = SUGER;
        this.accessoryNum1 = accessoryNum1;
        this.accessoryNum2 = accessoryNum2;
        this.accessoryNum3 = accessoryNum3;
        this.accessoryNum4 = accessoryNum4;
        this.accessoryNum5 = accessoryNum5;
        this.accessoryNum1Time = accessoryNum1Time;
        this.accessoryNum2Time = accessoryNum2Time;
        this.accessoryNum3Time = accessoryNum3Time;
        this.accessoryNum4Time = accessoryNum4Time;
        this.accessoryNum5Time = accessoryNum5Time;
        this.accessoryNum1Water = accessoryNum1Water;
        this.accessoryNum2Water = accessoryNum2Water;
        this.accessoryNum3Water = accessoryNum3Water;
        this.accessoryNum4Water = accessoryNum4Water;
        this.accessoryNum5Water = accessoryNum5Water;
        this.accessoryNum1StirringTime = accessoryNum1StirringTime;
        this.accessoryNum2StirringTime = accessoryNum2StirringTime;
        this.accessoryNum3StirringTime = accessoryNum3StirringTime;
        this.accessoryNum4StirringTime = accessoryNum4StirringTime;
        this.accessoryNum5StirringTime = accessoryNum5StirringTime;
        this.accessoryNum1StirringRate = accessoryNum1StirringRate;
        this.accessoryNum2StirringRate = accessoryNum2StirringRate;
        this.accessoryNum3StirringRate = accessoryNum3StirringRate;
        this.accessoryNum4StirringRate = accessoryNum4StirringRate;
        this.accessoryNum5StirringRate = accessoryNum5StirringRate;
        this.accessoryNum1Rate = accessoryNum1Rate;
        this.accessoryNum2Rate = accessoryNum2Rate;
        this.accessoryNum3Rate = accessoryNum3Rate;
        this.accessoryNum4Rate = accessoryNum4Rate;
        this.accessoryNum5Rate = accessoryNum5Rate;
        this.serialNum = SERIAL_NUM;
    }

    // 根据配置好的参数，返回打混合饮料的指令
    public String getMixedDrinksOrder() {
        if (checkValues()) {
            StringBuilder res = new StringBuilder();
            res.append(CoffeeMachineInstruction.START_TAG).append(" ");
            res.append(HexUtil.Int2HexString(CoffeeMachineInstruction.ADDRESS)).append(" ");
            res.append(HexUtil.Int2HexString(ORDER)).append(" ");
            res.append(HexUtil.Int2HexString(DATA_LENGTH)).append(" ");
            // 数据块
            res.append(HexUtil.Int2HexString(suger)).append(" ");

            res.append(HexUtil.Int2HexString(accessoryNum1)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum1Time)).append(" ");
            if (accessoryNum1Water <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(accessoryNum1Water)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum1StirringTime)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum1StirringRate)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum1Rate)).append(" ");

            res.append(HexUtil.Int2HexString(accessoryNum2)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum2Time)).append(" ");
            if (accessoryNum2Water <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(accessoryNum2Water)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum2StirringTime)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum2StirringRate)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum2Rate)).append(" ");

            res.append(HexUtil.Int2HexString(accessoryNum3)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum3Time)).append(" ");
            if (accessoryNum3Water <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(accessoryNum3Water)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum3StirringTime)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum3StirringRate)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum3Rate)).append(" ");

            res.append(HexUtil.Int2HexString(accessoryNum4)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum4Time)).append(" ");
            if (accessoryNum4Water <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(accessoryNum4Water)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum4StirringTime)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum4StirringRate)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum4Rate)).append(" ");

            res.append(HexUtil.Int2HexString(accessoryNum5)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum5Time)).append(" ");
            if (accessoryNum5Water <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(accessoryNum5Water)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum5StirringTime)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum5StirringRate)).append(" ");
            res.append(HexUtil.Int2HexString(accessoryNum5Rate)).append(" ");

            res.append(HexUtil.Int2HexString(CONFIG_BYTE1)).append(" ");
            res.append(HexUtil.Int2HexString(CONFIG_BYTE2)).append(" ");
            if (serialNum <= 0xFF) {
                res.append("00 ");
            }
            res.append(HexUtil.Int2HexString(serialNum)).append(" ");
            res.append(HexUtil.Int2HexString(getVerify())).append(" ");
            res.append(CoffeeMachineInstruction.END_TAG);
            return res.toString();
        } else {
            // 配置的参数出错，无法获取命令
            return "";
        }
    }

    private int getVerify() {
        return CoffeeMachineInstruction.ADDRESS ^ ORDER ^ DATA_LENGTH ^ suger ^ accessoryNum1
                ^ accessoryNum1Time ^ accessoryNum2 ^ accessoryNum2Time
                ^ accessoryNum3 ^ accessoryNum3Time ^ accessoryNum4
                ^ accessoryNum4Time ^ accessoryNum5 ^ accessoryNum5Time
                ^ (accessoryNum1Water >> 8) ^ (accessoryNum1Water & 0xff)
                ^ (accessoryNum2Water >> 8) ^ (accessoryNum2Water & 0xff)
                ^ (accessoryNum3Water >> 8) ^ (accessoryNum3Water & 0xff)
                ^ (accessoryNum4Water >> 8) ^ (accessoryNum4Water & 0xff)
                ^ (accessoryNum5Water >> 8) ^ (accessoryNum5Water & 0xff)
                ^ accessoryNum1StirringTime ^ accessoryNum2StirringTime ^ accessoryNum3StirringTime ^ accessoryNum4StirringTime ^ accessoryNum5StirringTime
                ^ accessoryNum1StirringRate ^ accessoryNum2StirringRate ^ accessoryNum3StirringRate ^ accessoryNum4StirringRate ^ accessoryNum5StirringRate
                ^ accessoryNum1Rate ^ accessoryNum2Rate ^ accessoryNum3Rate ^ accessoryNum4Rate ^ accessoryNum5Rate
                ^ CONFIG_BYTE1 ^ CONFIG_BYTE2 ^ serialNum;
    }

    private boolean checkValues() {
        if (suger < 0 || suger > MAX_SUGER) {
            return false;
        }
        if (accessoryNum1 < 0 || accessoryNum1 > MAX_ACCESSORY) {
            return false;
        }
        if (accessoryNum2 < 0 || accessoryNum2 > MAX_ACCESSORY) {
            return false;
        }
        if (accessoryNum3 < 0 || accessoryNum3 > MAX_ACCESSORY) {
            return false;
        }
        if (accessoryNum4 < 0 || accessoryNum4 > MAX_ACCESSORY) {
            return false;
        }
        if (accessoryNum5 < 0 || accessoryNum5 > MAX_ACCESSORY) {
            return false;
        }
        if (accessoryNum1Time < 0 || accessoryNum1Time > MAX_ACCESSORY_TIME) {
            return false;
        }
        if (accessoryNum2Time < 0 || accessoryNum2Time > MAX_ACCESSORY_TIME) {
            return false;
        }
        if (accessoryNum3Time < 0 || accessoryNum3Time > MAX_ACCESSORY_TIME) {
            return false;
        }
        if (accessoryNum4Time < 0 || accessoryNum4Time > MAX_ACCESSORY_TIME) {
            return false;
        }
        if (accessoryNum5Time < 0 || accessoryNum5Time > MAX_ACCESSORY_TIME) {
            return false;
        }
        if (accessoryNum1Water < 0 || accessoryNum1Water > MAX_WATER) {
            return false;
        }
        if (accessoryNum2Water < 0 || accessoryNum2Water > MAX_WATER) {
            return false;
        }
        if (accessoryNum3Water < 0 || accessoryNum3Water > MAX_WATER) {
            return false;
        }
        if (accessoryNum4Water < 0 || accessoryNum4Water > MAX_WATER) {
            return false;
        }
        if (accessoryNum5Water < 0 || accessoryNum5Water > MAX_WATER) {
            return false;
        }
        if (accessoryNum1StirringTime < 0 || accessoryNum1StirringTime > MAX_STIRRING_TIME) {
            return false;
        }
        if (accessoryNum2StirringTime < 0 || accessoryNum2StirringTime > MAX_STIRRING_TIME) {
            return false;
        }
        if (accessoryNum3StirringTime < 0 || accessoryNum3StirringTime > MAX_STIRRING_TIME) {
            return false;
        }
        if (accessoryNum4StirringTime < 0 || accessoryNum4StirringTime > MAX_STIRRING_TIME) {
            return false;
        }
        if (accessoryNum5StirringTime < 0 || accessoryNum5StirringTime > MAX_STIRRING_TIME) {
            return false;
        }
        if (accessoryNum1StirringRate < 0 || accessoryNum1StirringRate > MAX_RATE) {
            return false;
        }
        if (accessoryNum2StirringRate < 0 || accessoryNum2StirringRate > MAX_RATE) {
            return false;
        }
        if (accessoryNum3StirringRate < 0 || accessoryNum3StirringRate > MAX_RATE) {
            return false;
        }
        if (accessoryNum4StirringRate < 0 || accessoryNum4StirringRate > MAX_RATE) {
            return false;
        }
        if (accessoryNum5StirringRate < 0 || accessoryNum5StirringRate > MAX_RATE) {
            return false;
        }
        if (accessoryNum1Rate < 0 || accessoryNum1Rate > MAX_RATE) {
            return false;
        }
        if (accessoryNum2Rate < 0 || accessoryNum2Rate > MAX_RATE) {
            return false;
        }
        if (accessoryNum3Rate < 0 || accessoryNum3Rate > MAX_RATE) {
            return false;
        }
        if (accessoryNum4Rate < 0 || accessoryNum4Rate > MAX_RATE) {
            return false;
        }
        if (accessoryNum5Rate < 0 || accessoryNum5Rate > MAX_RATE) {
            return false;
        }
        if (serialNum < 0 || serialNum > MAX_SERIALNUM) {
            return false;
        }
        return true;
    }

    public int getSuger() {
        return suger;
    }

    public void setSuger(int suger) {
        this.suger = suger;
    }

    public int getAccessoryNum1() {
        return accessoryNum1;
    }

    public void setAccessoryNum1(int accessoryNum1) {
        this.accessoryNum1 = accessoryNum1;
    }

    public int getAccessoryNum2() {
        return accessoryNum2;
    }

    public void setAccessoryNum2(int accessoryNum2) {
        this.accessoryNum2 = accessoryNum2;
    }

    public int getAccessoryNum3() {
        return accessoryNum3;
    }

    public void setAccessoryNum3(int accessoryNum3) {
        this.accessoryNum3 = accessoryNum3;
    }

    public int getAccessoryNum4() {
        return accessoryNum4;
    }

    public void setAccessoryNum4(int accessoryNum4) {
        this.accessoryNum4 = accessoryNum4;
    }

    public int getAccessoryNum5() {
        return accessoryNum5;
    }

    public void setAccessoryNum5(int accessoryNum5) {
        this.accessoryNum5 = accessoryNum5;
    }

    public int getAccessoryNum1Time() {
        return accessoryNum1Time;
    }

    public void setAccessoryNum1Time(int accessoryNum1Time) {
        this.accessoryNum1Time = accessoryNum1Time;
    }

    public int getAccessoryNum2Time() {
        return accessoryNum2Time;
    }

    public void setAccessoryNum2Time(int accessoryNum2Time) {
        this.accessoryNum2Time = accessoryNum2Time;
    }

    public int getAccessoryNum3Time() {
        return accessoryNum3Time;
    }

    public void setAccessoryNum3Time(int accessoryNum3Time) {
        this.accessoryNum3Time = accessoryNum3Time;
    }

    public int getAccessoryNum4Time() {
        return accessoryNum4Time;
    }

    public void setAccessoryNum4Time(int accessoryNum4Time) {
        this.accessoryNum4Time = accessoryNum4Time;
    }

    public int getAccessoryNum5Time() {
        return accessoryNum5Time;
    }

    public void setAccessoryNum5Time(int accessoryNum5Time) {
        this.accessoryNum5Time = accessoryNum5Time;
    }

    public int getAccessoryNum1Water() {
        return accessoryNum1Water;
    }

    public void setAccessoryNum1Water(int accessoryNum1Water) {
        this.accessoryNum1Water = accessoryNum1Water;
    }

    public int getAccessoryNum2Water() {
        return accessoryNum2Water;
    }

    public void setAccessoryNum2Water(int accessoryNum2Water) {
        this.accessoryNum2Water = accessoryNum2Water;
    }

    public int getAccessoryNum3Water() {
        return accessoryNum3Water;
    }

    public void setAccessoryNum3Water(int accessoryNum3Water) {
        this.accessoryNum3Water = accessoryNum3Water;
    }

    public int getAccessoryNum4Water() {
        return accessoryNum4Water;
    }

    public void setAccessoryNum4Water(int accessoryNum4Water) {
        this.accessoryNum4Water = accessoryNum4Water;
    }

    public int getAccessoryNum5Water() {
        return accessoryNum5Water;
    }

    public void setAccessoryNum5Water(int accessoryNum5Water) {
        this.accessoryNum5Water = accessoryNum5Water;
    }

    public int getAccessoryNum1StirringTime() {
        return accessoryNum1StirringTime;
    }

    public void setAccessoryNum1StirringTime(int accessoryNum1StirringTime) {
        this.accessoryNum1StirringTime = accessoryNum1StirringTime;
    }

    public int getAccessoryNum2StirringTime() {
        return accessoryNum2StirringTime;
    }

    public void setAccessoryNum2StirringTime(int accessoryNum2StirringTime) {
        this.accessoryNum2StirringTime = accessoryNum2StirringTime;
    }

    public int getAccessoryNum3StirringTime() {
        return accessoryNum3StirringTime;
    }

    public void setAccessoryNum3StirringTime(int accessoryNum3StirringTime) {
        this.accessoryNum3StirringTime = accessoryNum3StirringTime;
    }

    public int getAccessoryNum4StirringTime() {
        return accessoryNum4StirringTime;
    }

    public void setAccessoryNum4StirringTime(int accessoryNum4StirringTime) {
        this.accessoryNum4StirringTime = accessoryNum4StirringTime;
    }

    public int getAccessoryNum5StirringTime() {
        return accessoryNum5StirringTime;
    }

    public void setAccessoryNum5StirringTime(int accessoryNum5StirringTime) {
        this.accessoryNum5StirringTime = accessoryNum5StirringTime;
    }

    public int getAccessoryNum1StirringRate() {
        return accessoryNum1StirringRate;
    }

    public void setAccessoryNum1StirringRate(int accessoryNum1StirringRate) {
        this.accessoryNum1StirringRate = accessoryNum1StirringRate;
    }

    public int getAccessoryNum2StirringRate() {
        return accessoryNum2StirringRate;
    }

    public void setAccessoryNum2StirringRate(int accessoryNum2StirringRate) {
        this.accessoryNum2StirringRate = accessoryNum2StirringRate;
    }

    public int getAccessoryNum3StirringRate() {
        return accessoryNum3StirringRate;
    }

    public void setAccessoryNum3StirringRate(int accessoryNum3StirringRate) {
        this.accessoryNum3StirringRate = accessoryNum3StirringRate;
    }

    public int getAccessoryNum4StirringRate() {
        return accessoryNum4StirringRate;
    }

    public void setAccessoryNum4StirringRate(int accessoryNum4StirringRate) {
        this.accessoryNum4StirringRate = accessoryNum4StirringRate;
    }

    public int getAccessoryNum5StirringRate() {
        return accessoryNum5StirringRate;
    }

    public void setAccessoryNum5StirringRate(int accessoryNum5StirringRate) {
        this.accessoryNum5StirringRate = accessoryNum5StirringRate;
    }

    public int getAccessoryNum1Rate() {
        return accessoryNum1Rate;
    }

    public void setAccessoryNum1Rate(int accessoryNum1Rate) {
        this.accessoryNum1Rate = accessoryNum1Rate;
    }

    public int getAccessoryNum2Rate() {
        return accessoryNum2Rate;
    }

    public void setAccessoryNum2Rate(int accessoryNum2Rate) {
        this.accessoryNum2Rate = accessoryNum2Rate;
    }

    public int getAccessoryNum3Rate() {
        return accessoryNum3Rate;
    }

    public void setAccessoryNum3Rate(int accessoryNum3Rate) {
        this.accessoryNum3Rate = accessoryNum3Rate;
    }

    public int getAccessoryNum4Rate() {
        return accessoryNum4Rate;
    }

    public void setAccessoryNum4Rate(int accessoryNum4Rate) {
        this.accessoryNum4Rate = accessoryNum4Rate;
    }

    public int getAccessoryNum5Rate() {
        return accessoryNum5Rate;
    }

    public void setAccessoryNum5Rate(int accessoryNum5Rate) {
        this.accessoryNum5Rate = accessoryNum5Rate;
    }

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }
}
