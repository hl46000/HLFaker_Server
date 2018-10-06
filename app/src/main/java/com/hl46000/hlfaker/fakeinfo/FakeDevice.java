package com.hl46000.hlfaker.fakeinfo;

import android.os.Build;
import android.util.Log;

import com.hl46000.hlfaker.Common;
import com.hl46000.hlfaker.data.HookSharedPref;

import java.lang.reflect.Member;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by hl46000 on 9/21/17.
 */

public class FakeDevice {
    private final String LOG_TAG = "FakeDevice";
    public FakeDevice(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){
            fakeBaseBand(sharedPkgParam);
            hookBuildClass();
            hookSystemProperties(sharedPkgParam);
            hookSystem();
        }else {
            hookSystemProperties(sharedPkgParam);
            hookSystem();
        }
        //hookSystemProperties();
    }

    public void fakeBaseBand(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            if (Build.VERSION.SDK_INT <= 14){
                Class<?> classBuild = XposedHelpers.findClass("android.os.Build", loadPkgParam.classLoader);
                XposedHelpers.setStaticObjectField(classBuild, "RADIO", HookSharedPref.getXValue("BaseBand"));
            }else {
                XposedHelpers.findAndHookMethod("android.os.Build",
                        loadPkgParam.classLoader, "getRadioVersion", new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult(HookSharedPref.getXValue("BaseBand"));
                            }
                        });
            }
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake BaseBand ERROR: " + e.getMessage());
        }
    }

    public void hookSystem(){
        Class<?> systemClss = System.class;

        try{
            XposedHelpers.findAndHookMethod(systemClss, "getProperty", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String name = param.args[0].toString().toLowerCase();
                    if(name != null && !name.isEmpty()){
                        if(name.equals("ro.serialno")
                                || name.equals("ro.boot.serialno")
                                || name.equals("ril.serialnumber")
                                || name.equals("sys.serialnumber")){
                            param.setResult(HookSharedPref.getXValue("AndroidSerial"));
                        }
                        /*
                        if(name.equals("os.arch")){
                            param.setResult("armv7l");
                        }
                        if(name.equals("os.version")){
                            param.setResult("4.0.9");
                        }
                        if(name.equals("ro.hardware")){
                            param.setResult("qcom");
                        }
                        if(name.equals("ro.chipname")){
                            param.setResult("MSM8216");
                        }
                        if(name.equals("ro.board.platform")){
                            param.setResult("msm8916");
                        }
                        if(name.equals("ro.product.board")){
                            param.setResult("MSM8916");
                        }
                        if(name.equals("ro.product.cpu.abi")){
                            param.setResult("armeabi");
                        }
                        */
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook Java System ERROR: " + e.getMessage());
        }

        try{
            XposedHelpers.findAndHookMethod(systemClss, "getProperty", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String name = param.args[0].toString().toLowerCase();
                    if(name != null && !name.isEmpty()){
                        if(name.equals("ro.serialno")
                                || name.equals("ro.boot.serialno")
                                || name.equals("ril.serialnumber")
                                || name.equals("sys.serialnumber")){
                            param.setResult(HookSharedPref.getXValue("AndroidSerial"));
                        }
                        /*
                        if(name.equals("os.arch")){
                            param.setResult("armv7l");
                        }
                        if(name.equals("os.version")){
                            param.setResult("4.0.9");
                        }
                        if(name.equals("ro.hardware")){
                            param.setResult("qcom");
                        }
                        if(name.equals("ro.chipname")){
                            param.setResult("MSM8216");
                        }
                        if(name.equals("ro.board.platform")){
                            param.setResult("msm8916");
                        }
                        if(name.equals("ro.product.board")){
                            param.setResult("MSM8916");
                        }
                        if(name.equals("ro.product.cpu.abi")){
                            param.setResult("armeabi");
                        }
                        */
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook Java System 2 Param ERROR: " + e.getMessage());
        }
    }

    public void hookSystemProperties(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            XposedHelpers.findAndHookMethod("android.os.SystemProperties", loadPkgParam.classLoader, "get", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);

                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.build.description")) {
                        param.setResult(HookSharedPref.getXValue("DESCRIPTION"));
                    }
                    /*
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.board")) {
                        param.setResult("MSM8916");
                    }
                    //Risk
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.cpu.abi")) {
                        param.setResult("armeabi-v7a");
                    }
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.cpu.abi2")) {
                        param.setResult("armeabi");
                    }
                    //End Risk
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.cpu.abilist")) {
                        param.setResult("armeabi,armeabi-v7a");
                    }
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.cpu.abilist32")) {
                        param.setResult("armeabi,armeabi-v7a");
                    }

                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.cpu.abilist64")) {
                        param.setResult("");
                    }
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.board.platform")) {
                        param.setResult("msm8916");
                    }
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.chipname")) {
                        param.setResult("MSM8216");
                    }
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.hardware")) {
                        param.setResult("qcom");
                    }
                    */
                }
            });

        } catch (Throwable e) {
            Log.d(LOG_TAG, "Fake SystemProperties ERROR: " + e.getMessage());
        }

        try {
            XposedHelpers.findAndHookMethod("android.os.SystemProperties", loadPkgParam.classLoader, "get", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);

                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.build.description")) {
                        param.setResult(HookSharedPref.getXValue("DESCRIPTION"));
                    }
                    /*
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.board")) {
                        param.setResult("MSM8916");
                    }
                    // Risk
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.cpu.abi")) {
                        param.setResult("armeabi-v7a");
                    }
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.cpu.abi2")) {
                        param.setResult("armeabi");
                    }
                    //End Risk
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.cpu.abilist")) {
                        param.setResult("armeabi,armeabi-v7a");
                    }
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.cpu.abilist32")) {
                        param.setResult("armeabi,armeabi-v7a");
                    }

                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.product.cpu.abilist64")) {
                        param.setResult("");
                    }
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.board.platform")) {
                        param.setResult("msm8916");
                    }
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.chipname")) {
                        param.setResult("MSM8216");
                    }
                    if (param.args.length > 0 && param.args[0] != null && param.args[0].equals("ro.hardware")) {
                        param.setResult("qcom");
                    }
                    */
                }
            });

        } catch (Throwable e) {
            Log.d(LOG_TAG, "Fake SystemProperties 2 Param ERROR: " + e.getMessage());
        }
    }

    public void hookBuildClass(){
        try {
            XposedHelpers.findField(Build.class, "BOARD").set(null, HookSharedPref.getXValue("BOARD"));
            XposedHelpers.findField(Build.class, "BRAND").set(null, HookSharedPref.getXValue("BRAND"));
            XposedHelpers.findField(Build.class, "DEVICE").set(null, HookSharedPref.getXValue("DEVICE"));
            XposedHelpers.findField(Build.class, "DISPLAY").set(null, HookSharedPref.getXValue("DISPLAY"));
            XposedHelpers.findField(Build.class, "FINGERPRINT").set(null, HookSharedPref.getXValue("FINGERPRINT"));
            XposedHelpers.findField(Build.class, "ID").set(null, HookSharedPref.getXValue("ID"));
            XposedHelpers.findField(Build.class, "MANUFACTURER").set(null, HookSharedPref.getXValue("MANUFACTURER"));
            XposedHelpers.findField(Build.class, "MODEL").set(null, HookSharedPref.getXValue("MODEL"));
            XposedHelpers.findField(Build.class, "PRODUCT").set(null, HookSharedPref.getXValue("PRODUCT"));
            XposedHelpers.findField(Build.class, "BOOTLOADER").set(null, HookSharedPref.getXValue("BOOTLOADER"));
            XposedHelpers.findField(Build.class, "HOST").set(null, HookSharedPref.getXValue("HOST"));

            /**
             * Code for Emulator
             */
            /*
            XposedHelpers.findField(Build.class, "HARDWARE").set(null, "qcom");
            XposedHelpers.findField(Build.class, "TYPE").set(null, "user");
            if(Build.VERSION.SDK_INT <= 20){
                XposedHelpers.findField(Build.class, "CPU_ABI").set(null, "armeabi");
                XposedHelpers.findField(Build.class, "CPU_ABI2").set(null, "armeabi-v7a");
            }else {
                XposedHelpers.findField(Build.class, "SUPPORTED_ABIS").set(null, new String[] {"armeabi", "armeabi-v7a"});
                XposedHelpers.findField(Build.class, "SUPPORTED_32_BIT_ABIS").set(null, new String[] {"armeabi", "armeabi-v7a"});
                XposedHelpers.findField(Build.class, "SUPPORTED_64_BIT_ABIS").set(null, new String[] {});
            }
            XposedHelpers.findField(Build.VERSION.class, "CODENAME").set(null, "REL");
            */
            XposedHelpers.findField(Build.VERSION.class, "INCREMENTAL").set(null, HookSharedPref.getXValue("INCREMENTAL"));
            XposedHelpers.findField(Build.VERSION.class, "RELEASE").set(null, HookSharedPref.getXValue("RELEASE"));

            XposedHelpers.findField(Build.class, "SERIAL").set(null, HookSharedPref.getXValue("AndroidSerial"));
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake Build Class ERROR: " + e.getMessage());
        }
    }

    public int parserInt(String value){
        try {
            return Integer.parseInt(value);
        }catch (Exception e){
            return 21;
        }
    }
}
