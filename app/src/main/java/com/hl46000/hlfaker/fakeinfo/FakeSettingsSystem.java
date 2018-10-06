package com.hl46000.hlfaker.fakeinfo;

import android.content.ContentResolver;
import android.util.Log;

import com.hl46000.hlfaker.Common;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZEROETC on 5/22/2018.
 */

public class FakeSettingsSystem {
    private final String LOG_TAG  = "FakeSettingsSystem";
    public FakeSettingsSystem(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){
            hookProviderSettingsSystem(sharedPkgParam);
        }
    }

    public void hookProviderSettingsSystem(final XC_LoadPackage.LoadPackageParam loadPkgParam){

        try{
            String sysClass = "android.provider.Settings.System";
            XposedHelpers.findAndHookMethod(sysClass, loadPkgParam.classLoader, "putString", ContentResolver.class, String.class, String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.args[1] = "DCMM";
                    param.args[2] = "DCMM";
                    //XposedBridge.log("Sys key " + loadPkgParam.packageName + ": " + param.args[1].toString() + "|" + param.getResult());
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook ProviderSettingsSystem$putString ERROR: " + e.getMessage());
        }

        try{
            String sysClass = "android.provider.Settings.System";
            XposedHelpers.findAndHookMethod(sysClass, loadPkgParam.classLoader, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.args[1] = "DCMM";
                    //XposedBridge.log("Sys key " + loadPkgParam.packageName + ": " + param.args[1].toString() + "|" + param.getResult());
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook ProviderSettingsSystem$getString ERROR: " + e.getMessage());
        }

    }
}
