package com.netease.vendor.common.database;

public class TDataset extends Dataset implements IDataset{

	public static IDataset newInstance() {
		IDataset service = new TDataset();
		return service;
	} 
}
