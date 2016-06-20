package com.netease.vendor.serialport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import com.netease.vendor.common.action.TViewWatcher;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.Remote;
import com.netease.vendor.util.log.LogUtil;

public class SerialPortInstance {

	private static SerialPortInstance instance = null;

	private SerialPort mSerialPort = null;
	private OutputStream mOutputStream = null;
	private InputStream mInputStream = null;
	private ReadThread mReadThread;

	private char[] readBufferToChar;
	private String receivedData = "";

	private SerialPortInstance() {
	}

	public static SerialPortInstance getInstance() {
		if (instance == null) {
			synchronized (SerialPortInstance.class) {
				if (instance == null) {
					instance = new SerialPortInstance();
					instance.initSerialPort();
				}
			}
		}
		return instance;
	}

	private void processResult(String result, int what, int action) {
        LogUtil.vendor("processResult -> " + result);

		Remote remote = new Remote();
		remote.setWhat(what);
		remote.setAction(action);
		remote.setBody(result);
		TViewWatcher.newInstance().notifyAll(remote);

		if (receivedData.length() > result.length()) {
			receivedData = receivedData.substring(result.length());
		} else {
			receivedData = "";
		}
	}

    private void dispatchResult(String result){
        LogUtil.vendor("result = " + result);
        if (result.length() == 16 && result.substring(4, 6).equals("30")) {
            // 设置温度指令结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_INIT,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_INIT_RESULT);
        }else if ((result.length() == 16 || result.length() == 14)
                && result.substring(4, 6).equals("3f")) {
            // 查询最后一条指令结果
            if(result.length() == 16){
                processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT,
                        ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE);
            }else if(receivedData.substring(14, 16).equals("ee")){
                result = receivedData.substring(0, 16);
                processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT,
                        ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE);
            }
        }else if (result.length() == 14
                && result.substring(4, 6).equals("46")) {
            // 打混合饮料结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_MAKE_COFFEE);
        }else if ((result.length() == 16 || result.length() == 14)
                && result.substring(4, 6).equals("30")) {
            // 设置水温温度结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_TEST,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_SET_TEMP);
        }else if (result.substring(4, 6).equals("36")
                && result.length() == 14) {
            // 清洗磨豆装置结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_TEST,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_CLEAN_BREW);
        }else if (result.substring(4, 6).equals("35")
                && result.length() == 14) {
            // 出水结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_TEST,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_WATER_OUT);
        }else if (result.substring(4, 6).equals("37")
                && result.length() == 14) {
            // 打磨豆咖啡结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_TEST,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_BEAN_GRIND);
        }else if (result.substring(4, 6).equals("11")
                && result.length() == 14) {
            // 落棒结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_TEST,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_STICK);
        }else if (result.substring(4, 6).equals("25")
                && result.length() == 14) {
            // 杯嘴转动结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_TEST,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_CUP_TURN_IN_OUT);
        }else if (result.substring(4, 6).equals("23")
                && result.length() == 14) {
            // 杯筒转动结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_TEST,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_CUP_TURN);
        }else if (result.substring(4, 6).equals("21")
                && result.length() == 14) {
            // 落杯结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_TEST,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_CUP_DROP);
        }else if (result.substring(4, 6).equals("31")
                && result.length() == 14) {
            // 开关水泵结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_TEST,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_PUMP_OPEN_CLOSE);
        }else if (result.substring(4, 6).equals("38")
                && result.length() == 14) {
            // 获取温度结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_TEST,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_TEMP_GET);
        }else if (result.substring(4, 6).equals("40")
                && result.length() == 14) {
            // 清洗结果
            processResult(result, ITranCode.ACT_COFFEE_SERIAL_PORT_TEST,
                    ITranCode.ACT_COFFEE_SERIAL_PORT_TEST_WASHING);
        }
    }

	private void processReceivedData(final byte[] buffer, final int size) {
		for (int i = 0; i < size; i++) {
			readBufferToChar[i] = (char) buffer[i];
		}
		char[] ch = String.copyValueOf(readBufferToChar, 0, size).toCharArray();
		String temp;
		StringBuilder tmpSB = new StringBuilder();
		for (int i = 0; i < ch.length; i++) {
			temp = String.format("%02x", (int) ch[i]);
			if (temp.length() == 4) {
				tmpSB.append(temp.substring(2, 4));
			} else {
				tmpSB.append(temp);
			}
		}
		temp = tmpSB.toString();
        LogUtil.vendor("temp = " + temp);

		if (temp.startsWith("aa")) {
			receivedData = "" + temp;

            if(temp.contains("ee")){
                String result = receivedData.substring(0,
                        receivedData.indexOf("ee") + 2);
                if(result.length()<receivedData.length()){
                    if(result.length()%2==1&&receivedData.substring(receivedData.indexOf("ee") + 2, receivedData.indexOf("ee") + 3).equals("e")){
                        result += "e";
                    }else if((receivedData.length()-result.length())>=2&&result.length()%2==0&&receivedData.substring(receivedData.indexOf("ee") + 2, receivedData.indexOf("ee") + 4).equals("ee")){
                        result += "ee";
                    }
                }

                dispatchResult(result);
            }

		} else if (temp.contains("ee")) {
			receivedData += temp;

			String result = receivedData.substring(0,
					receivedData.indexOf("ee") + 2);
            if(result.length()<receivedData.length()){
                if(result.length()%2==1&&receivedData.substring(receivedData.indexOf("ee") + 2, receivedData.indexOf("ee") + 3).equals("e")){
                    result += "e";
                }else if((receivedData.length()-result.length())>=2&&result.length()%2==0&&receivedData.substring(receivedData.indexOf("ee") + 2, receivedData.indexOf("ee") + 4).equals("ee")){
                    result += "ee";
                }
            }

            dispatchResult(result);

		} else if (!receivedData.equals("")) {
			receivedData += temp;
		}
	}

	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
                LogUtil.e("vendor", "Thread name is " + getName());
				int size;
				try {
					byte[] buffer = new byte[64];
					if (mInputStream == null)
						return;
					size = mInputStream.read(buffer);
					if (size > 0) {
						processReceivedData(buffer, size);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	private SerialPort getSerialPort() throws SecurityException, IOException,
			InvalidParameterException {
		if (mSerialPort == null) {
			/* Read serial port parameters */
			String path = "/dev/ttymxc1"; // EMB-4500
//			String path = "/dev/ttyS1";   // RK3188 YuSong
			int baudrate = 9600;
			/* Check parameters */
			if ((path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}
			/* Open the serial port */
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
		}
		return mSerialPort;
	}

	// private void closeSerialPort() {
	// if (mSerialPort != null) {
	// mSerialPort.close();
	// mSerialPort = null;
	// }
	// }

	public OutputStream getOutputStream() {
		return mOutputStream;
	}

	private void initSerialPort() {
		// 串口相关
		try {
			readBufferToChar = new char[2048];
			getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
	}
}
