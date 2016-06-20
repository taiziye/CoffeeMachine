package com.netease.vendor.common.dbhelper;

import com.netease.vendor.common.database.IDataset;
import com.netease.vendor.common.database.TDataset;
import com.netease.vendor.common.database.TException;
import com.netease.vendor.service.domain.CoffeeIndent;

public class CoffeeIndentDbHelper {

	/// ------------------ insert --------------------
	public static void insertCoffeeIndent(CoffeeIndent indent) {
		IDataset service = TDataset.newInstance();
		try {
			service.insert("insertCoffeeIndent", indent);
		} catch (TException e) {
			e.printStackTrace();
		}
	}
	
	/// ------------------ delete --------------------
	
	/// ------------------ query ----------------------
	public static CoffeeIndent getCoffeeIndent(String coffeeIndent){
		IDataset service = TDataset.newInstance();
		try {
			CoffeeIndent indent = (CoffeeIndent) service
					.queryForObject("getCoffeeIndent", coffeeIndent);

			return indent;
		} catch (TException e) {
			e.printStackTrace();
		}

		return null;
	} 
	
	/// ------------------ update --------------------
	public static void updateCoffeeIndentStatus(CoffeeIndent indent){
		try {
			IDataset service = TDataset.newInstance();
			try {
				service.update("updateCoffeeIndentStatus", indent);
			} catch (TException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
