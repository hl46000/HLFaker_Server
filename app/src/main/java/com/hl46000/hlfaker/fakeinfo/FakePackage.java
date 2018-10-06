package com.hl46000.hlfaker.fakeinfo;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.os.Parcel;
import android.util.Log;

import com.hl46000.hlfaker.Common;
import com.hl46000.hlfaker.PackagesManage;
import com.hl46000.hlfaker.data.HookSharedPref;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by hl46000 on 10/17/17.
 */

public class FakePackage {
    private final String LOG_TAG = "FakePackage";

    public FakePackage(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){
            hookPackageInfo(sharedPkgParam);
            hookApplicationInfo(sharedPkgParam);
            //hookPackageItemInfo(sharedPkgParam);
            //launchFake(sharedPkgParam);
            //hideClass(sharedPkgParam);
        }
    }

    public void hookPackageInfo(final XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            final Class<?> pkgInfoClass = XposedHelpers.findClass("android.content.pm.PackageInfo", loadPkgParam.classLoader);
            Constructor<?> pkgInfoConstructor = XposedHelpers.findConstructorBestMatch(pkgInfoClass, Parcel.class);
            XposedBridge.hookMethod(pkgInfoConstructor, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    PackageInfo pkgInfo = (PackageInfo) param.thisObject;
                    if(pkgInfo.packageName.equals(Common.HLFAKER_PACKAGE) ||
                            pkgInfo.packageName.equals(Common.KINGUSER_PACKAGE) ||
                            pkgInfo.packageName.equals(Common.SUPERSU_PACKAGE) ||
                            pkgInfo.packageName.equals(Common.XPOSED_PACKAGE)){
                        Field pkgNameField = XposedHelpers.findField(pkgInfoClass, "packageName");
                        pkgNameField.set(param.thisObject, "dkm.check.cc");
                        ApplicationInfo appInfo = pkgInfo.applicationInfo;
                        appInfo.packageName = "dkm.check.cc";
                        appInfo.processName = "dkm.check.cc";
                        appInfo.className = "dkm.check.cc";
                        appInfo.name = "DKMM";
                        Field appInfoField = XposedHelpers.findField(pkgInfoClass, "applicationInfo");
                        appInfoField.set(param.thisObject, appInfo);
                    }else {
                        /*
                        //long time = new Date().getTime();
                        long time = HookUntils.getParseITime(HookSharedPref.getXValue("InstallTime"));
                        Field fInstallTime = XposedHelpers.findField(pkgInfoClass, "firstInstallTime");
                        Field lUpdateTime = XposedHelpers.findField(pkgInfoClass, "lastUpdateTime");
                        fInstallTime.set(param.thisObject, time);
                        lUpdateTime.set(param.thisObject, time);
                        */
                    }
                    /*
                    else if(pkgInfo.packageName.equals("com.google.android.gms")){
                        Field versionCode = XposedHelpers.findField(pkgInfoClass, "versionCode");
                        Field versionName = XposedHelpers.findField(pkgInfoClass, "versionName");
                        versionCode.set(param.thisObject, 11975436);
                        versionName.set(param.thisObject, "11.9.75 (436-182402865)");
                    }else if(pkgInfo.packageName.equals("com.android.vending")){
                        Field versionCode = XposedHelpers.findField(pkgInfoClass, "versionCode");
                        Field versionName = XposedHelpers.findField(pkgInfoClass, "versionName");
                        versionCode.set(param.thisObject, 80871000);
                        versionName.set(param.thisObject, "8.7.10-all [0] [PR] 181799446");
                    }else {

                        //long time = new Date().getTime();
                        long time = HookUntils.getParseITime(HookSharedPref.getXValue("InstallTime"));
                        Field fInstallTime = XposedHelpers.findField(pkgInfoClass, "firstInstallTime");
                        Field lUpdateTime = XposedHelpers.findField(pkgInfoClass, "lastUpdateTime");
                        fInstallTime.set(param.thisObject, time);
                        lUpdateTime.set(param.thisObject, time);

                    }
                    */
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook PackageInfo ERROR: " + e.getMessage());
        }
    }

    public void hookPackageItemInfo(final XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            Class<?> pkgItemInfoClass = PackageItemInfo.class;
            XposedHelpers.findAndHookMethod(pkgItemInfoClass, "loadLabel", PackagesManage.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if(param.getResult() == null){
                        return;
                    }
                    String label = param.getResult().toString().toLowerCase();
                    if (label.contains("hlfaker") || label.contains("xposed") || label.contains("supersu")){
                        CharSequence appLabel = "DKMM";
                        param.setResult(appLabel);
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook PackageItemInfo$loadLabel ERROR: " + e.getMessage());
        }

        try {
            final Class<?> pkgItemInfoClass = XposedHelpers.findClass("android.content.pm.PackageItemInfo", loadPkgParam.classLoader);
            Constructor<?> pkgItemInfoConstructor = XposedHelpers.findConstructorBestMatch(pkgItemInfoClass, Parcel.class);
            XposedBridge.hookMethod(pkgItemInfoConstructor, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    PackageItemInfo pkgItemInfo = (PackageItemInfo) param.thisObject;
                    if(pkgItemInfo.packageName.equals(Common.HLFAKER_PACKAGE) ||
                            pkgItemInfo.packageName.equals(Common.KINGUSER_PACKAGE) ||
                            pkgItemInfo.packageName.equals(Common.SUPERSU_PACKAGE) ||
                            pkgItemInfo.packageName.equals(Common.XPOSED_PACKAGE)){
                        Field pkgNameField = XposedHelpers.findField(pkgItemInfoClass, "packageName");
                        pkgNameField.set(param.thisObject, "dkm.check.cc");
                        Field nameField = XposedHelpers.findField(pkgItemInfoClass, "name");
                        nameField.set(param.thisObject, "DKMM");
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook PackageItemInfo$Constructor protected ERROR: " + e.getMessage());
        }

        try {
            final Class<?> pkgItemInfoClass = XposedHelpers.findClass("android.content.pm.PackageItemInfo", loadPkgParam.classLoader);
            Constructor<?> pkgItemInfoConstructor = XposedHelpers.findConstructorBestMatch(pkgItemInfoClass, PackageItemInfo.class);
            XposedBridge.hookMethod(pkgItemInfoConstructor, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    PackageItemInfo pkgItemInfo = (PackageItemInfo) param.args[0];
                    if(pkgItemInfo.packageName.equals(Common.HLFAKER_PACKAGE) ||
                            pkgItemInfo.packageName.equals(Common.KINGUSER_PACKAGE) ||
                            pkgItemInfo.packageName.equals(Common.SUPERSU_PACKAGE) ||
                            pkgItemInfo.packageName.equals(Common.XPOSED_PACKAGE)){
                        pkgItemInfo.packageName = "dkm.check.cc";
                        param.args[0] = pkgItemInfo;
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook PackageItemInfo$Constructor public ERROR: " + e.getMessage());
        }
    }

    public void hookApplicationInfo(final XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            final Class<?> appInfoClass = XposedHelpers.findClass("android.content.pm.ApplicationInfo", loadPkgParam.classLoader);
            Constructor<?> appInfoConstructor = XposedHelpers.findConstructorBestMatch(appInfoClass, ApplicationInfo.class);
            XposedBridge.hookMethod(appInfoConstructor, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    ApplicationInfo appInfo = (ApplicationInfo) param.args[0];
                    if(appInfo == null || appInfo.processName == null){
                        return;
                    }
                    if (appInfo.processName.contains("hlfaker") || appInfo.processName.contains("xposed") || appInfo.processName.contains("supersu")){
                        appInfo.processName = "dkmm.check.cc";
                        appInfo.className = "com.dkmm.check.cc";
                        appInfo.name = "DKMM";
                        param.args[0] = appInfo;
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook ApplicationInfo ERROR: " + e.getMessage());
        }
    }

    public void launchFake(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            Class<?> launchClass = XposedHelpers.findClass("android.support.v4.app.AppLaunchChecker", loadPkgParam.classLoader);
            XposedHelpers.findAndHookMethod(launchClass, "hasStartedFromLauncher", Activity.class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.afterHookedMethod(param);
                    param.setResult(false);
                }

            });
        }catch(NoSuchMethodError e){
            //XposedBridge.log("Fake AppLaunch ERROR: " + e.getMessage());
        }
        catch(XposedHelpers.ClassNotFoundError e){
            //XposedBridge.log("Fake AppLaunch ERROR: " + e.getMessage());
        } catch (Throwable e) {
            //XposedBridge.log("Fake AppLaunch ERROR: " + e.getMessage());
        }


    }

    public void hideClass(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            String classClass = "java.lang.Class";
            XposedHelpers.findAndHookMethod(classClass, loadPkgParam.classLoader, "forName", String.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if(param.args[0].toString().contains("xposed") || param.args[0].toString().contains("supersu") ||
                            param.args[0].toString().contains("hlfaker") || param.args[0].toString().contains("XposedBridge") ||
                            param.args[0].toString().contains("ZygoteIni") || param.args[0].toString().contains("vbox")){
                        param.setThrowable(new ClassNotFoundException());
                    }else{
                        return;
                    }
                }

            });
        } catch (XposedHelpers.ClassNotFoundError e) {
            // TODO: handle exception
        } catch (NoSuchMethodError e){

        } catch (Throwable e){

        }

        try {
            String classClass = "java.lang.Class";
            XposedHelpers.findAndHookMethod(classClass, loadPkgParam.classLoader, "forName", String.class, boolean.class, ClassLoader.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if(param.args[0].toString().contains("xposed") || param.args[0].toString().contains("supersu") ||
                            param.args[0].toString().contains("hlfaker") || param.args[0].toString().contains("XposedBridge") ||
                            param.args[0].toString().contains("ZygoteIni") || param.args[0].toString().contains("vbox")){
                        param.setThrowable(new ClassNotFoundException());
                    }else{
                        return;
                    }
                }

            });
        } catch (XposedHelpers.ClassNotFoundError e) {
            // TODO: handle exception
        } catch (NoSuchMethodError e){

        } catch (Throwable e){

        }
    }
}
