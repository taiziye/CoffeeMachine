package com.netease.vendor.util.multicard;

public class LimitSpaceUnwriteException extends Exception {

	private static final long serialVersionUID = -360543214883668013L;

	public LimitSpaceUnwriteException() {
        super("存储空间不足，无法写入");
    }
}
