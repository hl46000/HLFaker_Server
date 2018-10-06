package com.hl46000.hlfaker.fakeinfo;

import android.util.Log;

import com.hl46000.hlfaker.Common;
import com.hl46000.hlfaker.data.HookSharedPref;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZEROETC on 1/21/2018.
 */

public class NaviteHook {
    public NaviteHook(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        String activeAH;
        try{
            activeAH = HookSharedPref.getXValue("AndHook");
        }catch (Exception e){
            activeAH = "";
        }

        if(activeAH == null || activeAH.isEmpty() || activeAH.equals("false")){
            return;
        }else if (activeAH.equals("true")){
            if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName) &&
                    !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE) &&
                    !sharedPkgParam.packageName.equals(Common.HLBROWSER_PACKAGE)){
                loadHLHooker(sharedPkgParam.packageName);
            }
        }
    }

    public void loadHLHooker(String packageName){

        try {
            try{
                andhook.lib.AndHook.getVersionInfo();
            }catch (Throwable e){
                System.load("/data/data/com.hl46000.hlfaker/lib/libAK.so");
            }
            //System.loadLibrary("AK");
            //System.load("/data/data/com.hl46000.hlfaker/lib/libAK.so");
            //XposedBridge.log("AndHook Loaded");
            //Log.d("NaviteHook", "Load AK");
        } catch (Throwable e0) {
            //Log.d("NaviteHook", "Load AK ERROR: " +e0.getMessage());
            /*
            try {
                // compatible with libhoudini
                //System.loadLibrary("AKCompat");
                System.load("/data/local/libAKCompat.so");
                //XposedBridge.log("AndHookCompat Loaded");
            } catch (Throwable e1) {
                // still failed, YunOS?
                //XposedBridge.log("AndHook Load Failed: " + e0.getMessage());
                Log.d("NaviteHook", "Load AndHook ERROR: " +e1.getMessage());
                //throw new UnsatisfiedLinkError("incompatible platform, "+ e0.getMessage());
            }
            */
        }

        try {
            //System.loadLibrary("hlhooker");
            System.load("/data/data/com.hl46000.hlfaker/lib/libhlhooker.so");
            //XposedBridge.log("HLHooker Loaded");
            //Log.d("NaviteHook", "HLHooker Loaded");
        } catch (Throwable e) {
            //XposedBridge.log("HLHooker Load Failed: " + e.getMessage());
            //Log.d("NaviteHook", "Failed to load hlhooker: " + e.getMessage());
        }
    }

}
