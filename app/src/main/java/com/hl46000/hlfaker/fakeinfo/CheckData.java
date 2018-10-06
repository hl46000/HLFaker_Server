package com.hl46000.hlfaker.fakeinfo;

import android.content.ContentResolver;
import android.util.Log;

import com.hl46000.hlfaker.Common;

import java.io.File;
import java.io.RandomAccessFile;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZEROETC on 12/14/2017.
 */

public class CheckData {
    public CheckData(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !sharedPkgParam.packageName.equals(Common.XPOSED_PACKAGE) &&
                !sharedPkgParam.packageName.equals(Common.HLFAKER_PACKAGE) && !sharedPkgParam.packageName.equals(Common.SUPERSU_PACKAGE ) &&
                !sharedPkgParam.packageName.equals(Common.KINGUSER_PACKAGE)){
            checkRedWriteSystem(sharedPkgParam);
            //checkFileClass();
        }
    }

    public void checkPGameServices(XC_LoadPackage.LoadPackageParam loadPkgParam){

    }

    public void checkRedWriteSystem(final XC_LoadPackage.LoadPackageParam loadPkgParam){
        String sysClass = "android.provider.Settings.System";
        XposedHelpers.findAndHookMethod(sysClass, loadPkgParam.classLoader, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("Sys key " + loadPkgParam.packageName + ": " + param.args[1].toString() + "|" + param.getResult());
            }
        });
    }

    public void checkFileClass(){
        try{
            XposedBridge.hookAllConstructors(File.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (param.args.length == 1) {
                        XposedBridge.log("File Path: " + param.args[0].toString());
                    } else if (param.args.length == 2 && !File.class.isInstance(param.args[0])) {
                        int i = 0;
                        while (i < 2) {
                            XposedBridge.log("File Path i: " + param.args[i].toString());
                            i++;
                        }
                    }
                }
            });
        }catch (Throwable e){
            //Log.d(LOG_TAG, "FakeCPU File Class ERROR: " + e.getMessage());
        }

        try{
            XposedBridge.hookAllConstructors(File.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if(String.class.isInstance(param.args[0])){
                        XposedBridge.log("RFile: " + param.args[0].toString());
                    }
                }
            });
        }catch (Throwable e){
            //Log.d(LOG_TAG, "FakeCPU File Class ERROR: " + e.getMessage());
        }
    }
}
