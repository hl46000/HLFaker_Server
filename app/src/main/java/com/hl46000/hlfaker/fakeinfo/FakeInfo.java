package com.hl46000.hlfaker.fakeinfo;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hl46000.hlfaker.Common;
import com.hl46000.hlfaker.data.HookSharedPref;

import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by hl46000 on 9/21/17.
 */

public class FakeInfo {
    private final String LOG_TAG = "FakeInfo";

    public FakeInfo(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName)){
            fakeWifiInfo(sharedPkgParam);
        }
    }

    public void fakeBattery(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            XposedHelpers.findAndHookMethod("android.content.Intent", loadPkgParam.classLoader, "getIntExtra", String.class, Integer.TYPE, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (param.args[0] != null) {
                        if (param.args[0] == "temperature") {
                            param.setResult(Integer.valueOf(HookSharedPref.getXValue("Temp")));
                        }
                        if (param.args[0] == "level") {
                            param.setResult(Integer.valueOf(HookSharedPref.getXValue("Level")));
                        }
                        if (param.args[0] == "plugged") {
                            param.setResult(Integer.valueOf(random02()));
                        }
                        if (param.args[0] == NotificationCompat.CATEGORY_STATUS) {
                            param.setResult(Integer.valueOf(random24()));
                        }
                        if (param.args[0] == "health") {
                            param.setResult(Integer.valueOf("2"));
                        }
                    }
                }

            });
        } catch (Throwable e) {
            Log.d(LOG_TAG, "Fake Battery ERROR: " + e.getMessage());
        }
    }

    private String random02() {
        String[] arrayValue = new String[]{"0", "1", "2"};
        return arrayValue[new Random().nextInt(arrayValue.length)];
    }

    private String random24() {
        String[] arrayValue = new String[]{"2", "3", "4"};
        return arrayValue[new Random().nextInt(arrayValue.length)];
    }

    /**
     * Fake Wifi Infomation
     * @param loadPkgParam
     */
    public void fakeWifiInfo(XC_LoadPackage.LoadPackageParam loadPkgParam){
        hookWifiInfo(loadPkgParam, "getMacAddress", HookSharedPref.getXValue("MAC")); //Wifi Card MAC Address
        hookWifiInfo(loadPkgParam, "getSSID", HookSharedPref.getXValue("BSSID"));
        hookWifiInfo(loadPkgParam, "getBSSID", HookSharedPref.getXValue("BSSID")); //Access Point MAC Address
    }

    /**
     * Hook WifiInfo
     * @param loadPkgParam
     * @param funcName
     * @param value
     */
    private void hookWifiInfo(XC_LoadPackage.LoadPackageParam loadPkgParam, String funcName, final Object value){
        try {
            XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo", loadPkgParam.classLoader, funcName, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(value);
                }
            });
        }catch (Exception e){
            Log.d(LOG_TAG, "Hook WifiInfo " + funcName + " ERROR " + e.getMessage());
        }
    }
}
