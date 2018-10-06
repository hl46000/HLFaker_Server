package com.hl46000.hlfaker.fakeinfo;

import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hl46000.hlfaker.Common;
import com.hl46000.hlfaker.data.HookSharedPref;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by hl46000 on 9/21/17.
 */

public class FakeCarrier {
    private final String LOG_TAG = "FakeCarrier";

    public FakeCarrier(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName)){
            fakeCarrierSystemProperties();
            fakeCarrierTelephony(sharedPkgParam);
        }
    }

    /**
     * Fake Carrier Info when user call Telephony Class
     * @param loadPkgParam
     */
    public void fakeCarrierTelephony(XC_LoadPackage.LoadPackageParam loadPkgParam){
        String telephonyClass = "android.telephony.TelephonyManager";

        hookTelephony(telephonyClass, loadPkgParam, "getLine1Number", HookSharedPref.getXValue("PhoneNumber"));
        hookTelephony(telephonyClass, loadPkgParam, "getNetworkCountryIso", HookSharedPref.getXValue("CountryISO"));
        hookTelephony(telephonyClass, loadPkgParam, "getNetworkOperator", HookSharedPref.getXValue("MCC") + HookSharedPref.getXValue("MNC"));
        hookTelephony(telephonyClass, loadPkgParam, "getNetworkOperatorName", HookSharedPref.getXValue("CarrierName"));
        hookTelephony(telephonyClass, loadPkgParam, "getSimCountryIso", HookSharedPref.getXValue("CountryISO"));
        hookTelephony(telephonyClass, loadPkgParam, "getSimOperator", HookSharedPref.getXValue("MCC") + HookSharedPref.getXValue("MNC"));
        hookTelephony(telephonyClass, loadPkgParam, "getSimOperatorName", HookSharedPref.getXValue("CarrierName"));

        hookTelephony(telephonyClass, loadPkgParam, "getPhoneType", TelephonyManager.PHONE_TYPE_GSM);
        hookTelephony(telephonyClass, loadPkgParam, "getSimState", TelephonyManager.SIM_STATE_READY);
        hookTelephony(telephonyClass, loadPkgParam, "getNetworkType", TelephonyManager.NETWORK_TYPE_LTE);
    }

    /**
     * Fake Carrier Info when user call SystemProperties Class
     */
    public void fakeCarrierSystemProperties(){
        try {
            Class<?> classSP = Class.forName("android.os.SystemProperties");
            Method get1Arg = classSP.getDeclaredMethod("get", new Class[]{String.class});
            Method get2Arg = classSP.getDeclaredMethod("get", new Class[]{String.class, String.class});
            hookSystemProperties(get1Arg);
            hookSystemProperties(get2Arg);
        }catch (Exception e){
            Log.d(LOG_TAG, "Fake SystemProperties: " + e.getMessage());
        }
    }

    /**
     * Hook SystemProperties
     * @param hookMethod
     */
    private void hookSystemProperties(Method hookMethod){
        try {
            XposedBridge.hookMethod(hookMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String prop = (String) param.args[0];
                    if(prop.equals("gsm.sim.operator.numeric")){
                        param.setResult(HookSharedPref.getXValue("MNC"));
                    }else if(prop.equals("gsm.operator.alpha")){
                        param.setResult(HookSharedPref.getXValue("CarrierName"));
                    }else if(prop.equals("gsm.operator.iso-country")){
                        param.setResult(HookSharedPref.getXValue("CountryISO").toLowerCase());
                    }else if(prop.equals("persist.sys.timezone")){
                        param.setResult(HookSharedPref.getXValue("TimeZone"));
                    }else if(prop.equals("gsm.sim.state")){
                        param.setResult("5");
                    }
                }
            });
        }catch (Exception e){
            Log.d(LOG_TAG, "Hook SystemProperties: " + e.getMessage());
        }
    }

    /**
     * Hook Telephony Class with String Value
     * @param hookClass
     * @param loadPkgParam
     * @param funcName
     * @param value
     */
    private void hookTelephony(String hookClass, XC_LoadPackage.LoadPackageParam loadPkgParam, String funcName, final String value){
        try {
            XposedHelpers.findAndHookMethod(hookClass, loadPkgParam.classLoader, funcName, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(value);
                }
            });
        }catch (Exception e){
            Log.d(LOG_TAG, "Hook Telephony String: " + e.getMessage());
        }
    }

    /**
     * Hook Telephony Class with Int Value
     * @param hookClass
     * @param loadPkgParam
     * @param funcName
     * @param value
     */
    private void hookTelephony(String hookClass, XC_LoadPackage.LoadPackageParam loadPkgParam, String funcName, final int value){
        try {
            XposedHelpers.findAndHookMethod(hookClass, loadPkgParam.classLoader, funcName, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(value);
                }
            });
        }catch (Exception e){
            Log.d(LOG_TAG, "Hook Telephony Int: " + e.getMessage());
        }
    }
}
