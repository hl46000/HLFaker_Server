package com.hl46000.hlfaker.fakeinfo;

import android.util.Log;

import com.hl46000.hlfaker.Common;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZEROETC on 12/13/2017.
 */

public class FakeCPU {
    private final String LOG_TAG = "FakeCPU";

    public FakeCPU(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){
            changePath(sharedPkgParam);
            //fakeFileExists();
        }
    }

    public void fakeFileExists(){
        try {

            XposedHelpers.findAndHookMethod(File.class, "exists", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if(File.class.isInstance(param.thisObject)){
                        File checkFile = (File)param.thisObject;
                        if(checkFile != null && HookUntils.needHideFile(checkFile.getPath())){
                            param.setResult(false);
                        }
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake File Exists ERROR: " + e.getMessage());
        }
    }

    public void changePath(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try{
            XposedBridge.hookAllConstructors(File.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (param.args.length == 1 && String.class.isInstance(param.args[0])) {
                        /*
                        if (param.args[0].toString().equals("/proc/cpuinfo")) {
                            param.args[0] = "/sdcard/HLDATA/cpuinfo";
                        }
                        if (param.args[0].toString().equals("/proc/version")) {
                            param.args[0] = "/sdcard/HLDATA/version";
                        }
                        if (param.args[0].toString().equals("/system/build.prop")) {
                            param.args[0] = "/sdcard/HLDATA/build.prop";
                        }
                        if (param.args[0].toString().contains("/cmdline") && param.args[0].toString().contains("/proc")) {
                            param.args[0] = "/sdcard/HLDATA/cmdline";
                        }

                        if (param.args[0].toString().equals("/proc")) {
                            param.args[0] = "/sdcard/HLDATA";
                        }
                        if (param.args[0].toString().equals("/system")) {
                            param.args[0] = "/sdcard/HLDATA";
                        }
                        */
                        if (param.args[0].toString().contains("/maps") && param.args[0].toString().contains("/proc")) {
                            param.args[0] = "/sdcard/HLDATA/maps";
                        }

                        if(HookUntils.needHideFile(param.args[0].toString())){
                            File fakeFile = new File("/dkm/check/cc");
                            param.args[0] = "/dkm/check/cc";
                            //param.setResult(fakeFile);
                        }

                    }else if (param.args.length == 1 && URI.class.isInstance(param.args[0])){
                        /*
                        URI path = (URI) param.args[0];
                        if (path.getPath().equals("/proc/cpuinfo")) {
                            param.args[0] = new URI("file:///sdcard/HLDATA/cpuinfo");
                        }
                        if (path.getPath().equals("/proc/version")) {
                            param.args[0] = new URI("file:///sdcard/HLDATA/version");
                        }
                        if (path.getPath().equals("/system/bin/setprop")) {
                            param.args[0] = new URI("file:///sdcard/HLDATA/setprop");
                        }
                        if (path.getPath().equals("/system/bin/getprop")) {
                            param.args[0] = new URI("file:///sdcard/HLDATA/getprop");
                        }
                        if (path.getPath().equals("/system/bin/monkey")) {
                            param.args[0] = new URI("file:///sdcard/HLDATA/monkey");
                        }
                        if (path.getPath().equals("/system/build.prop")) {
                            param.args[0] = new URI("file:///sdcard/HLDATA/build.prop");
                        }
                        if (path.getPath().contains("/cmdline")) {
                            param.args[0] = new URI("file:///sdcard/HLDATA/cmdline");
                        }
                        if (path.getPath().equals("/proc")) {
                            param.args[0] = new URI("file:///sdcard/HLDATA");
                        }
                        if (path.getPath().equals("/system")) {
                            param.args[0] = new URI("file:///sdcard/HLDATA");
                        }
                        if (path.getPath().contains("/maps") && path.getPath().contains("/proc")) {
                            param.args[0] = new URI("file:///sdcard/HLDATA/maps");
                        }
                        if(HookUntils.needHideFile(path.getPath())){
                            param.args[0] = new URI("file:///dkm/check/cc");
                        }
                        */
                    }else if (param.args.length == 2 && String.class.isInstance(param.args[1])) {
                        /*
                        int i = 0;
                        String str = "";
                        while (i < 2) {
                            String stringBuilder;
                            if (param.args[i] != null) {
                                if (param.args[i].toString().contains("/proc")) {
                                    param.args[i] = "/sdcard/HLDATA";
                                }
                                if (param.args[i].toString().contains("/system")) {
                                    param.args[i] = "/sdcard/HLDATA";
                                }
                                if(HookUntils.needHideFile(param.args[i].toString())){
                                    param.args[i] = "/dkm/check/cc";
                                }
                                stringBuilder = new StringBuilder(String.valueOf(str)).append(param.args[i]).append(":").toString();
                            } else {
                                stringBuilder = str;
                            }
                            i++;
                            str = stringBuilder;
                        }
                        if (param.args[0] != null) {
                            if (param.args[0].toString().equals("/proc")) {
                                param.args[0] = "/sdcard/HLDATA";
                            }
                            if (param.args[0].toString().contains("/system")) {
                                param.args[0] = "/sdcard/HLDATA";
                            }
                            if(HookUntils.needHideFile(param.args[0].toString())){
                                param.args[0] = "/dkm/check/cc";
                            }
                        }
                        */
                        if (param.args[0] != null) {
                            if (param.args[0].toString().equals("/proc")) {
                                param.args[0] = "/sdcard/HLDATA";
                            }
                            /*
                            if (param.args[0].toString().equals("/system")) {
                                param.args[0] = "/sdcard/HLDATA";
                            }
                            */
                            if (param.args[0].toString().equals("/vendor")) {
                                param.args[0] = "/sdcard/HLDATA";
                            }
                        }
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "FakeCPU File Class ERROR: " + e.getMessage());
        }

        try{
            XposedBridge.hookAllConstructors(RandomAccessFile.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (param.args.length == 2 && String.class.isInstance(param.args[0])) {
                        /*
                        if (param.args[0].toString().equals("/proc/cpuinfo")) {
                            param.args[0] = "/sdcard/HLDATA/cpuinfo";
                        }
                        if (param.args[0].toString().equals("/proc/version")) {
                            param.args[0] = "/sdcard/HLDATA/version";
                        }
                        if (param.args[0].toString().equals("/system/build.prop")) {
                            param.args[0] = "/sdcard/HLDATA/build.prop";
                        }
                        if (param.args[0].toString().contains("/cmdline") && param.args[0].toString().contains("/proc")) {
                            param.args[0] = "/sdcard/HLDATA/cmdline";
                        }
                        */

                        if (param.args[0].toString().contains("/maps") && param.args[0].toString().contains("/proc")) {
                            param.args[0] = "/sdcard/HLDATA/maps";
                        }
                        if(HookUntils.needHideFile(param.args[0].toString())){
                            param.args[0] = "/dkm/check/cc";
                        }
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "FakeCPU RandomAccessFile Class ERROR: " + e.getMessage());
        }

        /*
        try{
            XposedHelpers.findAndHookMethod("java.util.regex.Pattern", loadPkgParam.classLoader, "matcher", CharSequence.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (param.args.length == 1) {
                        if (param.args[0].toString().equals("/proc/cpuinfo")) {
                            param.args[0] = "/sdcard/HLDATA/cpuinfo";
                        }
                        if (param.args[0].toString().equals("/proc/version")) {
                            param.args[0] = "/sdcard/HLDATA/version";
                        }

                        if (param.args[0].toString().equals("/system/bin/setprop")) {
                            param.args[0] = "/sdcard/HLDATA/setprop";
                        }
                        if (param.args[0].toString().equals("/system/bin/getprop")) {
                            param.args[0] = "/sdcard/HLDATA/getprop";
                        }
                        if (param.args[0].toString().equals("/system/bin/monkey")) {
                            param.args[0] = "/sdcard/HLDATA/monkey";
                        }
                        if (param.args[0].toString().equals("/system/build.prop")) {
                            param.args[0] = "/sdcard/HLDATA/build.prop";
                        }
                        if (param.args[0].toString().contains("/cmdline")) {
                            param.args[0] = "/sdcard/HLDATA/cmdline";
                        }
                        if (param.args[0].toString().contains("/maps") && param.args[0].toString().contains("/proc")) {
                            param.args[0] = "/sdcard/HLDATA/maps";
                        }
                        if(HookUntils.needHideFile(param.args[0].toString())){
                            param.args[0] = "/dkm/check/cc";
                        }
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "FakeCPU Pattern Class ERROR: " + e.getMessage());
        }
        */
    }

}
