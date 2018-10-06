package com.hl46000.hlfaker.fakeinfo;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;

import com.hl46000.hlfaker.Common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XCallback;

/**
 * Created by hl46000 on 10/20/17.
 */

public class FakeRoot {
    private final String LOG_TAG = "FakeRoot";
    public FakeRoot(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){
            hookFile();
            //hookActivityManager(sharedPkgParam);
            hookRuntime(sharedPkgParam);
            //hookProcessBuilder();
            hookSettingsGlobal();
        }
    }

    public void hookFile(){
        try {
            Constructor<?> fileConstructor = XposedHelpers.findConstructorBestMatch(File.class, String.class);
            XposedBridge.hookMethod(fileConstructor, new XC_MethodHook(XCallback.PRIORITY_HIGHEST) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String filePath = (String) param.args[0];
                    if(filePath != null){
                        if(filePath.contains("/system/xbin/su") || filePath.contains("/system/bin/su") || filePath.contains("/system/xbin/busybox") ||
                                filePath.contains("vbox") || filePath.contains("intel") || filePath.contains("microvirt") || filePath.contains("memusf") || filePath.contains("memud") ||
                                filePath.contains("_x86") || filePath.contains("memuguest") || filePath.contains("superuser") || filePath.contains("xposed")){
                            param.args[0] = "/data/dkmm/check/cc";
                        }
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook File Class 1 arg ERROR: " + e.getMessage());
        }
    }

    public void hookActivityManager(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            XposedHelpers.findAndHookMethod("android.app.ActivityManager", loadPkgParam.classLoader, "getRunningServices", int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    List<ActivityManager.RunningServiceInfo> services = (List<ActivityManager.RunningServiceInfo>) param.getResult(); // Get the results from the method call
                    if(services != null){
                        Iterator<ActivityManager.RunningServiceInfo> iter = services.iterator();
                        String tempProcessName;
                        List<ActivityManager.RunningServiceInfo> safeService = new ArrayList<ActivityManager.RunningServiceInfo>();
                        // Iterate through the list of RunningServiceInfo and remove any mentions that match a keyword in the keywordSet
                        while (iter.hasNext()) {
                            ActivityManager.RunningServiceInfo tempService = iter.next();
                            tempProcessName = tempService.process.toLowerCase();
                            if (tempProcessName != null) {
                                if(tempProcessName.contains("supersu") || tempProcessName.contains("hlfaker") ||
                                        tempProcessName.contains("xposed") || tempProcessName.contains("su")){
                                    continue;
                                }else {
                                    safeService.add(tempService);
                                }
                            }
                        }
                        param.setResult(safeService);
                    }

                }
            });
        }catch (Throwable e){

        }

        try {
            XposedHelpers.findAndHookMethod("android.app.ActivityManager", loadPkgParam.classLoader, "getRunningTasks", int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    List<ActivityManager.RunningTaskInfo> services = (List<ActivityManager.RunningTaskInfo>) param.getResult(); // Get the results from the method call
                    if(services != null){
                        Iterator<ActivityManager.RunningTaskInfo> iter = services.iterator();
                        String tempBaseActivity;
                        List<ActivityManager.RunningTaskInfo> safeTaskInfo = new ArrayList<ActivityManager.RunningTaskInfo>();
                        while (iter.hasNext()) {
                            ActivityManager.RunningTaskInfo tempTask = iter.next();
                            tempBaseActivity = tempTask.baseActivity.flattenToString().toLowerCase(); // Need to make it a string for comparison
                            if (tempBaseActivity != null) {
                                if(tempBaseActivity.contains("supersu") || tempBaseActivity.contains("hlfaker") ||
                                        tempBaseActivity.contains("xposed") || tempBaseActivity.contains("su")){
                                    continue;
                                }else {
                                    safeTaskInfo.add(tempTask);
                                }
                            }
                        }
                        param.setResult(safeTaskInfo);
                    }
                }
            });
        }catch (Throwable e){

        }

        try {
            XposedHelpers.findAndHookMethod("android.app.ActivityManager", loadPkgParam.classLoader, "getRunningAppProcesses", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    List<ActivityManager.RunningAppProcessInfo> processes = (List<ActivityManager.RunningAppProcessInfo>) param.getResult(); // Get the results from the method call
                    if(processes != null){
                        Iterator<ActivityManager.RunningAppProcessInfo> iter = processes.iterator();
                        String tempProcessName;
                        List<ActivityManager.RunningAppProcessInfo> safeProcess = new ArrayList<ActivityManager.RunningAppProcessInfo>();
                        // Iterate through the list of RunningAppProcessInfo and remove any mentions that match a keyword in the keywordSet
                        while (iter.hasNext()) {
                            ActivityManager.RunningAppProcessInfo tempProcess = iter.next();
                            tempProcessName = tempProcess.processName.toLowerCase();
                            if (tempProcessName != null) {
                                if(tempProcessName.contains("supersu") || tempProcessName.contains("hlfaker") ||
                                        tempProcessName.contains("xposed") || tempProcessName.contains("su")){
                                    continue;
                                }else {
                                    safeProcess.add(tempProcess);
                                }
                            }else {
                                continue;
                            }
                        }
                        param.setResult(safeProcess);
                    }
                }
            });
        }catch (Throwable e){

        }

        try {
            XposedHelpers.findAndHookMethod("android.app.ActivityManager", loadPkgParam.classLoader, "getAppTasks", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    List<ActivityManager.AppTask> appTasks = (List<ActivityManager.AppTask>) param.getResult(); // Get the results from the method call
                    if(appTasks != null){
                        List<ActivityManager.AppTask> safeAppTasks = new ArrayList<ActivityManager.AppTask>();
                        param.setResult(safeAppTasks);
                    }else {
                        param.setResult(appTasks);
                    }
                }
            });
        }catch (Throwable e){

        }
    }

    public void hookRuntime(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            XposedHelpers.findAndHookMethod("java.lang.Runtime", loadPkgParam.classLoader, "exec", String[].class, String[].class, File.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    String[] execArray = (String[]) param.args[0]; // Grab the tokenized array of commands
                    if ((execArray != null) && (execArray.length >= 1)) { // Do some checking so we don't break anything
                        String firstParam = execArray[0]; // firstParam is going to be the main command/program being run

                        if (firstParam.contains("su") || firstParam.contains("which") ||
                                firstParam.contains("busybox") || firstParam.contains("pm") ||
                                firstParam.contains("am") || firstParam.contains("sh") || firstParam.contains("ps")) { // Check if the firstParam is one of the keywords we want to filter

                            // A bunch of logic follows since the solution depends on which command is being called
                            // TODO: ***Clean up this logic***
                            if (firstParam.equals("su") || firstParam.endsWith("/su")) { // If its su or ends with su (/bin/su, /xbin/su, etc)
                                param.setThrowable(new IOException()); // Throw an exception to imply the command was not found
                            } else if ((firstParam.equals("pm") || firstParam.endsWith("/pm"))) {
                                // Trying to run the pm (package manager) using exec. Now let's deal with the subcases
                                if (execArray.length >= 3 && execArray[1].equalsIgnoreCase("list") && execArray[2].equalsIgnoreCase("packages")) {
                                    // Trying to list out all of the packages, so we will filter out anything that matches the keywords
                                    //param.args[0] = new String[] {"pm", "list", "packages", "-v", "grep", "-v", "\"su\""};
                                    param.args[0] = buildGrepArraySingle(execArray, true);
                                } else if (execArray.length >= 3 && (execArray[1].equalsIgnoreCase("dump") || execArray[1].equalsIgnoreCase("path"))) {
                                    // Trying to either dump package info or list the path to the APK (both will tell the app that the package exists)
                                    // If it matches anything in the keywordSet, stop it from working by using a fake package name

                                    if (execArray[2].contains("su") || execArray[2].contains("which") ||
                                            execArray[2].contains("busybox") || execArray[2].contains("pm") ||
                                            execArray[2].contains("am") || execArray[2].contains("sh") || execArray[2].contains("ps")) {
                                        param.args[0] = new String[]{execArray[0], execArray[1], "dkmm.check.cc"};
                                    }
                                }
                            } else if ((firstParam.equals("ps") || firstParam.endsWith("/ps"))) { // This is a process list command
                                // Trying to run the ps command to see running processes (e.g. looking for things running as su or daemonsu). Filter this out.
                                param.args[0] = buildGrepArraySingle(execArray, true);
                            } else if ((firstParam.equals("which") || firstParam.endsWith("/which"))) {
                                // Busybox "which" command. Thrown an excepton
                                param.setThrowable(new IOException());
                            } else if (anyWordEndingWithKeyword("busybox", execArray)) {
                                param.setThrowable(new IOException());
                            } else if ((firstParam.equals("sh") || firstParam.endsWith("/sh"))) {
                                param.setThrowable(new IOException());
                            } else {
                                param.setThrowable(new IOException());
                            }
                        }


                    }
                }
            });
        }catch (Throwable e){

        }
    }

    public void hookProcessBuilder(){
        try {
            Constructor<?> processBuilderConstructor2 = XposedHelpers.findConstructorBestMatch(java.lang.ProcessBuilder.class, String[].class);
            XposedBridge.hookMethod(processBuilderConstructor2, new XC_MethodHook(XCallback.PRIORITY_HIGHEST) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    //XposedBridge.log("Hooked ProcessBuilder");
                    if (param.args[0] != null) {
                        String[] cmdArray = (String[]) param.args[0];
                        if (cmdArray[0].toLowerCase().contains("xposed") || cmdArray[0].toLowerCase().contains("supersu") || cmdArray[0].toLowerCase().contains("su")) {
                            cmdArray[0] = "checkcc";
                            param.args[0] = cmdArray;
                        }
                    }
                }
            });
        }catch (Throwable e){

        }
    }

    public void hookSettingsGlobal(){
        XposedHelpers.findAndHookMethod(Settings.Global.class, "getInt", ContentResolver.class, String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                String setting = (String) param.args[1];
                if (setting != null && Settings.Global.ADB_ENABLED.equals(setting)) { // Hide ADB being on from an app
                    param.setResult(0);
                }
            }
        });
    }

    private String[] buildGrepArraySingle(String[] original, boolean addSH) {
        StringBuilder builder = new StringBuilder();
        ArrayList<String> originalList = new ArrayList<String>();
        if (addSH) {
            originalList.add("sh");
            originalList.add("-c");
        }
        for (String temp : original) {
            builder.append(" ");
            builder.append(temp);
        }
        //originalList.addAll(Arrays.asList(original));
        // ***TODO: Switch to using -e with alternation***
        builder.append(" | grep -v ");
        builder.append("supersu");
        builder.append(" | grep -v ");
        builder.append("xposed");
        builder.append(" | grep -v ");
        builder.append("XposedBridge");
        builder.append(" | grep -v ");
        builder.append("superuser");
        builder.append(" | grep -v ");
        builder.append("Superuser");
        builder.append(" | grep -v ");
        builder.append("su");
        builder.append(" | grep -v ");
        builder.append("busybox");
        builder.append(" | grep -v ");
        builder.append("root");
        //originalList.addAll(Common.DEFAULT_GREP_ENTRIES);
        originalList.add(builder.toString());
        return originalList.toArray(new String[0]);
    }

    private Boolean anyWordEndingWithKeyword(String keyword, String[] wordArray) {
        for (String tempString : wordArray) {
            if (tempString.endsWith(keyword)) {
                return true;
            }
        }
        return false;
    }
}
