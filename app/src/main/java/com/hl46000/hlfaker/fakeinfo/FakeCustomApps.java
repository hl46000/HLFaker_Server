package com.hl46000.hlfaker.fakeinfo;

import android.content.Context;
import android.util.Log;

import com.hl46000.hlfaker.Common;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZEROETC on 3/9/2018.
 */

public class FakeCustomApps {
    private final String LOG_TAG = "FakeCustomApps";
    public FakeCustomApps(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !sharedPkgParam.packageName.equals(Common.XPOSED_PACKAGE) &&
                !sharedPkgParam.packageName.equals(Common.HLFAKER_PACKAGE) && !sharedPkgParam.packageName.equals(Common.SUPERSU_PACKAGE ) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){
            //hookAppLike(sharedPkgParam);
            hookAli(sharedPkgParam);
        }
    }

    public void hookAli(XC_LoadPackage.LoadPackageParam loadPkgParam){
        if(!loadPkgParam.packageName.toLowerCase().contains("com.alibaba")){
            return;
        }
        try {
            Class<?> deviceUtilsClss = XposedHelpers.findClassIfExists("com.taobao.wireless.security.preinstall.PreInstallSecurityGuardInitializer", loadPkgParam.classLoader);
            if(deviceUtilsClss != null){
                XposedHelpers.findAndHookMethod(deviceUtilsClss, "Initialize", Context.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d(LOG_TAG, "Initialize: " + param.getResult());
                    }
                });
            }
        }catch (Throwable e){
            Log.d(LOG_TAG, "HookAppLike isDeviceRooted ERROR: " + e.getMessage());
        }
    }

    public void hookAppLike(XC_LoadPackage.LoadPackageParam loadPkgParam){
        if(!loadPkgParam.packageName.toLowerCase().contains("de.mcoins.applike")){
            return;
        }
        try {
            Class<?> deviceUtilsClss = XposedHelpers.findClassIfExists("de.mcoins.applike.utils.DeviceUtils", loadPkgParam.classLoader);
            if(deviceUtilsClss != null){
                XposedHelpers.findAndHookMethod(deviceUtilsClss, "isDeviceRooted", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.setResult(false);
                    }
                });
            }
        }catch (Throwable e){
            Log.d(LOG_TAG, "HookAppLike isDeviceRooted ERROR: " + e.getMessage());
        }
    }
}
