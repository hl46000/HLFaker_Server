package com.hl46000.hlfaker.fakeinfo;

import android.location.Address;
import android.util.Log;

import com.hl46000.hlfaker.Common;
import com.hl46000.hlfaker.data.HookSharedPref;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by hl46000 on 9/21/17.
 */

public class FakeGPS {
    private final String LOG_TAG = "FakeGPS";
    public FakeGPS(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){
            fakeGPSInfo(sharedPkgParam);
            fakeAddress();
        }
    }

    /**
     * Fake GPS Info
     * @param loadPkgParam
     */
    public void fakeGPSInfo(XC_LoadPackage.LoadPackageParam loadPkgParam){
        hookLocation(loadPkgParam, "getLatitude", tryParseDouble(HookSharedPref.getXValue("Latitude")));
        hookLocation(loadPkgParam, "getLongitude", tryParseDouble(HookSharedPref.getXValue("Longitude")));
        hookLocation(loadPkgParam, "getAccuracy", randomAccuracy());
        //hookLocation(loadPkgParam, "getAltitude", Float.valueOf(Float.parseFloat(HookSharedPref.getXValue("Altitude"))));
        hookLocation(loadPkgParam, "getSpeed", randomSpeed());
    }

    public void fakeAddress(){
        hookAddress("getCountryCode", HookSharedPref.getXValue("CountryISO"));
        hookAddress("getLatitude", tryParseDouble(HookSharedPref.getXValue("Latitude")));
        final Locale fakeLocale = new Locale("en", HookSharedPref.getXValue("CountryISO"));
        hookAddress("getLocale", fakeLocale);
        hookAddress("getCountryName", fakeLocale.getDisplayCountry());
    }

    private void hookAddress(String funcName, final Object value){
        Class<?> addressClss = Address.class;
        try {
           XposedHelpers.findAndHookMethod(addressClss, funcName, new XC_MethodHook() {
               @Override
               protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                   super.beforeHookedMethod(param);
                   param.setResult(value);
               }
           });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook Address " + funcName + " ERROR: " + e.getMessage());
        }
    }

    private double tryParseDouble(String value){
        try {
            return Double.valueOf(Double.parseDouble(value));
        }catch (Exception e){
            return 27.82516672;
        }
    }

    private float randomAccuracy(){
        Random rnd = new Random();
        return rnd.nextFloat() * 10.0f;
    }

    private float randomSpeed(){
        Random rnd = new Random();
        return rnd.nextFloat();
    }

    /**
     * Hook Android Location
     * @param loadPkgParam
     * @param funcName
     * @param value
     */
    private void hookLocation(XC_LoadPackage.LoadPackageParam loadPkgParam, String funcName, final Object value){
        try {
            XposedHelpers.findAndHookMethod("android.location.Location", loadPkgParam.classLoader, funcName, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(value);
                }
            });
        }catch (Exception e){
            Log.d(LOG_TAG, "Hook Location " + funcName + " ERROR: " + e.getMessage());
        }
    }
}
