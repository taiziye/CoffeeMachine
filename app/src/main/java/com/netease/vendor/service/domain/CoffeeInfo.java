package com.netease.vendor.service.domain;

import java.util.ArrayList;

public class CoffeeInfo extends Ancestor{

	private static final long serialVersionUID = -5113394451363486332L;
	
	private int coffeeId = -1;
	private String coffeeTitle;
	private double price;
    private double discount;
	private String imgUrl ;
	private int soldNum;
	private String type ;

//    private double discount_wx;
//    private double discount_ali;
    private boolean isHot;
    private boolean isNew;
    private double volume;
	
	private ArrayList<CoffeeDosingInfo> dosingList = new ArrayList<CoffeeDosingInfo>();

    public boolean isLackMaterials() {
        return isLackMaterials;
    }

    public void setLackMaterials(boolean isLackMaterials) {
        this.isLackMaterials = isLackMaterials;
    }

    private boolean isLackMaterials;
	
	public int getCoffeeId() {
		return coffeeId;
	}
	
	public void setCoffeeId(int coffeeId) {
		this.coffeeId = coffeeId;
	}
	
	public String getCoffeeTitle() {
		return coffeeTitle;
	}
	
	public void setCoffeeTitle(String coffeeTitle) {
		this.coffeeTitle = coffeeTitle;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public String getImgUrl() {
		return imgUrl;
	}
	
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public int getSoldNum() {
		return soldNum;
	}
	
	public void setSoldNum(int soldNum) {
		this.soldNum = soldNum;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

//    public double getDiscount_wx() {
//        return discount_wx;
//    }

//    public void setDiscount_wx(double discount_wx) {
//        this.discount_wx = discount_wx;
//    }

//    public double getDiscount_ali() {
//        return discount_ali;
//    }

//    public void setDiscount_ali(double discount_ali) {
//        this.discount_ali = discount_ali;
//    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean isHot) {
        this.isHot = isHot;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

	public ArrayList<CoffeeDosingInfo> getDosingList() {
		return dosingList;
	}

	public void addDosingInfo(CoffeeDosingInfo info){
		if(dosingList == null){
			dosingList = new ArrayList<CoffeeDosingInfo>();
		}
		
		dosingList.add(info);
	}
}
