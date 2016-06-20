package com.netease.vendor.util.log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.netease.vendor.application.AppConfig;
import com.netease.vendor.application.MyApplication;
import com.netease.vendor.util.EncryptUtil;
import com.netease.vendor.util.StorageUtil;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class LectekLog {

	private static String TAG = "vendor";
	public static boolean DEBUG = AppConfig.isDebugMode();
	public static boolean ENCRYPT = false;
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"MM-dd HH:mm:ss.ms", Locale.getDefault());
	private static String LOG_FILE = "";
	
	static
	{
		init(MyApplication.Instance().getApplicationContext(), DEBUG, false);
	}
	
	public static void init(Context context, boolean debug, boolean encrypt) {
		DEBUG = debug;
		ENCRYPT = encrypt;
		SimpleDateFormat curSDF = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		String storedDiretory = StorageUtil.getWritePathIgnoreError(context, "vendor_log_"
				+ curSDF.format(new Date()) + ".log");
		if(TextUtils.isEmpty(storedDiretory)) {
			return;
		}
		File mFile = new File(storedDiretory);
		File pFile = mFile.getParentFile();
		if (!pFile.exists()) {// 如果文件夹不存在，则先创建文件夹
			pFile.mkdirs();
		}
		
		LOG_FILE = storedDiretory;
	}

	public static void toFile(String msg) {
		if (DEBUG) {
			outMessage(TAG, msg, null);
		}
	}
	
	public static void i(String msg) {
		if (DEBUG) {
			i(TAG, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			i(tag, msg, null);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.i(tag, msg, tr);
			outMessage(tag, msg, tr);
		}
	}

	public static void v(String msg) {
		if (DEBUG) {
			v(TAG, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (DEBUG) {
			v(tag, msg, null);
		}
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.v(tag, msg, tr);
			outMessage(tag, msg, tr);
		}
	}

	public static void e(String msg) {
		if (DEBUG) {
			e(TAG, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (DEBUG) {
			e(tag, msg, null);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.e(tag, msg, tr);
			outMessage(tag, msg, tr);
		}
	}

	public static void d(String msg) {
		if (DEBUG) {
			d(TAG, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (DEBUG) {
			d(tag, msg, null);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.d(tag, msg, tr);
			outMessage(tag, msg, tr);
		}
	}

	public static void w(String msg) {
		if (DEBUG) {
			w(TAG, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			w(tag, msg, null);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.w(tag, msg, tr);
			outMessage(tag, msg, tr);
		}
	}
	
	private static String encryptionString(String sb) {	
		try {
			String enString = EncryptUtil.doEncryptByCustomize(sb, "a");
			return enString;
		} catch (Exception e) {
			return sb;
		}
	}
	
	private static void outMessage(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			if (TextUtils.isEmpty(LOG_FILE)) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			sb.append(sdf.format(new Date()));
			sb.append(": ");
			sb.append(tag);
			sb.append(": ");
			sb.append(msg);
			sb.append("\n");
			if (tr != null) {
				sb.append(Log.getStackTraceString(tr));
				sb.append("\n");
			}
			outPutToFile(sb.toString(), LOG_FILE);
		}
	}

	private static boolean outPutToFile(String str, String filePath) {
		if (!StorageUtil.isSDcardExist()) {
			return false;
		}
		
		if (TextUtils.isEmpty(str)) {
			return false;
		}
		
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		
		try {
			FileWriter fw = new FileWriter(filePath, true);
			if(!ENCRYPT) {
				fw.write(str);
			} else {
				fw.write(encryptionString(str));
			}
			fw.flush();
			fw.close();
			return true;
		} catch (Exception e) {
		}
		return false;
	}
}