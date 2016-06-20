package com.netease.vendor.application;

public class AppConfig {

    public enum Build {
        TEST,
        PRE_REL,
        REL,
    }

    //
    // BUILDS
    // DEFAULT Build.REL
    //
    public static final Build BUILD_SERVER = Build.REL;

    //
    // SERIALPORT
    // DEFAULT true
    //
    private static final boolean SERIALPORT_SYSNC = true;

	public static boolean isSerialportSysnc(){
        return SERIALPORT_SYSNC;
    }

    //
    // DEBUG MODE
    // DEFAULT false
    //
    private static final boolean DEBUG_MODE = false;

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }
}
