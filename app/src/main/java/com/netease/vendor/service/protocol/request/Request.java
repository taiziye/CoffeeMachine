package com.netease.vendor.service.protocol.request;

import com.netease.vendor.service.protocol.LinkFrame;
import com.netease.vendor.service.protocol.pack.AutoPackHelper;
import com.netease.vendor.service.protocol.pack.Pack;


/**
 * User: jingege
 * Date: 1/13/12
 * Time: 10:21 AM
 */
public abstract class Request extends AutoPackHelper{
    
    protected String uid;
    protected LinkFrame header = null;

    public Request(String uid){
        this.uid = uid;
    }

    public LinkFrame getLinkFrame(){
    	if (header == null) {
    		header = new LinkFrame(getServiceId(),getCommandId(),uid);
    	}
        return header;
    }
    
    public Pack packRequest() {
    	return pack();
    }

    public abstract short getServiceId();

    public abstract short getCommandId();
   
}
