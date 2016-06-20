package com.netease.vendor.common.dbhelper;

import com.netease.vendor.common.database.IDataset;
import com.netease.vendor.common.database.TDataset;
import com.netease.vendor.common.database.TException;
import com.netease.vendor.domain.DBM;
import com.netease.vendor.domain.AppSet;


public class SettingDbHelper {

	public static void saveAppSet(String key, String value, String remark) {
		AppSet appset = new AppSet();
		appset.setPkey(key);
		appset.setPvalue(value);
		appset.setPreamrk(remark);

		IDataset service = TDataset.newInstance();
		try {
			service.insert("setAppSet", appset);
		} catch (TException e) {
			e.printStackTrace();
		}
	}

	public static String queryAppSet(String key) {
		IDataset service = TDataset.newInstance();
		AppSet appSet = new AppSet();
		appSet.setPkey(key);
		try {
			appSet = (AppSet) service.queryForObject("searchAppSet",
					appSet);
		} catch (TException e) {
			e.printStackTrace();
		}
		return (appSet == null ? "" : appSet.getPvalue());
	}
	
	public static String queryAppSet(String key, String remark) {
		IDataset service = TDataset.newInstance();
		AppSet appSet = new AppSet();
		appSet.setPkey(key);
		appSet.setPreamrk(remark);
		try {
			appSet = (AppSet) service.queryForObject("searchAppSetEx",
					appSet);
		} catch (TException e) {
			e.printStackTrace();
		}
		return (appSet == null ? "" : appSet.getPvalue());
	}
	
	public static boolean doesTableExist(String tablename) {
		IDataset service = TDataset.newInstance();
		DBM dbm = new DBM();
		dbm.setTablename(tablename);
		boolean exist = false;
		try {
			Object obj = service.queryForObject("tableIsExist", dbm);
			exist = (obj == null ? false : (Integer)obj == 1);
		} catch (TException e) {
			e.printStackTrace();
		}
		return exist;
	}
}
