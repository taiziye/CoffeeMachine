package com.netease.vendor.service.bean.result;

import com.netease.vendor.service.ITranCode;
import com.netease.vendor.service.bean.BeanAncestor;
import com.netease.vendor.service.domain.CoffeeDosingInfo;

import java.util.ArrayList;
import java.util.List;

public class GetDosingResult extends BeanAncestor {

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    private int resCode;

    public List<CoffeeDosingInfo> getDosings() {
        return dosings;
    }

    public void setDosings(List<CoffeeDosingInfo> coffees) {
        this.dosings = coffees;
    }

    private List<CoffeeDosingInfo> dosings;

    public void addDosing(CoffeeDosingInfo dosing){
        if (dosings == null) {
            dosings = new ArrayList<CoffeeDosingInfo>();
        }
        dosings.add(dosing);
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    private boolean auto;

	@Override
	public int getWhat() {
		return ITranCode.ACT_COFFEE;
	}

	@Override
	public int getAction() {
		return ITranCode.ACT_COFFEE_DOSING_LIST;
	}
}
