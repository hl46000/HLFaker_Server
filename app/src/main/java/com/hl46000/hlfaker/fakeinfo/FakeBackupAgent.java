package com.hl46000.hlfaker.fakeinfo;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;

import com.hl46000.hlfaker.Common;

import java.lang.reflect.Constructor;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZEROETC on 5/20/2018.
 */

public class FakeBackupAgent {
    private final String LOG_TAG  = "FakeBackupAgent";
    public FakeBackupAgent(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !sharedPkgParam.packageName.equals(Common.XPOSED_PACKAGE) &&
                !sharedPkgParam.packageName.equals(Common.HLFAKER_PACKAGE) && !sharedPkgParam.packageName.equals(Common.SUPERSU_PACKAGE ) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){
            hookSharedPreferencesBackupHelper(sharedPkgParam);
        }
    }

    public void hookSharedPreferencesBackupHelper(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try{
            final Class<?> hookClss = XposedHelpers.findClass("android.app.backup.SharedPreferencesBackupHelper", loadPkgParam.classLoader);
            Constructor<?> hookConstructor = XposedHelpers.findConstructorBestMatch(hookClss, Context.class, String[].class);
            XposedBridge.hookMethod(hookConstructor, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if(param.args.length >= 2){
                        for (int i = 1; i < param.args.length; i++){
                            if(String[].class.isInstance(param.args[i])){
                                param.args[i] = new String[] {"check_cc"};
                            }
                            if(String.class.isInstance(param.args[i])){
                                param.args[i] = "check_cc";
                            }
                        }
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook SharedPreferencesBackupHelper ERROR: " + e.getMessage());
        }
    }
}
