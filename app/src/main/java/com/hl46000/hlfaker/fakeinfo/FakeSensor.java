package com.hl46000.hlfaker.fakeinfo;

import android.hardware.Sensor;
import android.os.Build;
import android.util.Log;

import com.hl46000.hlfaker.Common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZEROETC on 12/19/2017.
 */

public class FakeSensor {
    public static final String LOG_TAG = "FakeSensor";

    public FakeSensor(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        boolean allowHook = false;
        String ABI = "";
        if(Build.VERSION.SDK_INT <= 19){
            ABI = Build.CPU_ABI;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ABI = Build.SUPPORTED_32_BIT_ABIS[0];
        }
        if(ABI.equals("x86")){
            allowHook = true;
        }
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !sharedPkgParam.packageName.equals(Common.XPOSED_PACKAGE) &&
                !sharedPkgParam.packageName.equals(Common.HLFAKER_PACKAGE) && !sharedPkgParam.packageName.equals(Common.SUPERSU_PACKAGE ) &&
                !sharedPkgParam.packageName.equals(Common.KINGUSER_PACKAGE) && allowHook){
            hookSensorClass(sharedPkgParam);
        }
    }

    public void hookSensorClass(XC_LoadPackage.LoadPackageParam loadPkgParam){
        /*
        try{
            final Class<?> sensorClass = XposedHelpers.findClass("android.hardware.Sensor", loadPkgParam.classLoader);

            XposedBridge.hookAllConstructors(sensorClass, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if(param.thisObject != null && Sensor.class.isInstance(param.thisObject)){
                        Sensor mSensor = (Sensor) param.thisObject;
                        if(mSensor.getName() != null){
                            if(mSensor.getName().toLowerCase().contains("acceleration")){
                                Field mName = XposedHelpers.findField(sensorClass, "mName");
                                Field mVendor = XposedHelpers.findField(sensorClass, "mVendor");
                                mName.set(param.thisObject, "K2HH Acceleration");
                                mVendor.set(param.thisObject, "STM");
                            }else if(mSensor.getName().toLowerCase().contains("proximity")){
                                Field mName = XposedHelpers.findField(sensorClass, "mName");
                                Field mVendor = XposedHelpers.findField(sensorClass, "mVendor");
                                mName.set(param.thisObject, "GP2A002 Proximity Sensor");
                                mVendor.set(param.thisObject, "SHARP");
                            }else if(mSensor.getName().toLowerCase().contains("orientation")){
                                Field mName = XposedHelpers.findField(sensorClass, "mName");
                                Field mVendor = XposedHelpers.findField(sensorClass, "mVendor");
                                mName.set(param.thisObject, "Screen Orientation Sensor");
                                mVendor.set(param.thisObject, "Samsung Electronics");
                            }else if(mSensor.getName().toLowerCase().contains("emulator")){
                                Field mName = XposedHelpers.findField(sensorClass, "mName");
                                Field mVendor = XposedHelpers.findField(sensorClass, "mVendor");
                                mName.set(param.thisObject, "K2HH Acceleration");
                                mVendor.set(param.thisObject, "STM");
                            }
                        }
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake Sensor ERROR: " + e.getMessage());
        }
        */

        try{
            XposedHelpers.findAndHookMethod("android.hardware.Sensor", loadPkgParam.classLoader, "getName", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    String mSensor = param.getResult().toString();
                    if(mSensor != null){
                        if(mSensor.toLowerCase().contains("acceleration") || mSensor.toLowerCase().contains("accelerometer")){
                            param.setResult("K2HH Acceleration");
                        }else if(mSensor.toLowerCase().contains("proximity")){
                            param.setResult("GP2A002 Proximity Sensor");
                        }else if(mSensor.toLowerCase().contains("orientation")){
                            param.setResult("Screen Orientation Sensor");
                        }else if(mSensor.toLowerCase().contains("emulator")){
                            param.setResult("K2HH Acceleration");
                        }
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake Sensor ERROR: " + e.getMessage());
        }

        try{
            XposedHelpers.findAndHookMethod("android.hardware.Sensor", loadPkgParam.classLoader, "getVendor", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult("STM");
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake Sensor ERROR: " + e.getMessage());
        }

    }
}
