package com.netease.vendor.service.bean.result;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.bean.BeanAncestor;
import com.netease.vendor.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class GetAdvPicsResult extends BeanAncestor {

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    private int resCode;

	private String advImgUrls;

	public void setAdvImgUrls(String advUrls){
		advImgUrls = advUrls;
	}

	public List<String> getAdvImgList(){
		if(TextUtils.isEmpty(advImgUrls)){
			return null;
		}

		List<String> advPicUrlsList = new ArrayList<String>();
		JSONArray array = JSONArray.parseArray(advImgUrls);
		if(array != null){
			int size = array.size();
			for(int i = 0; i < size; i++){
				String picURL = array.getString(i);
				advPicUrlsList.add(picURL);
			}
		}

		return advPicUrlsList;
	}

	public String getAdvImgUrls(){
		return advImgUrls;
	}

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_GET_ADV_PICS;
	}
}
