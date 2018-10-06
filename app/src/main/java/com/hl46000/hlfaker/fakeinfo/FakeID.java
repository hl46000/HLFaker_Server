package com.hl46000.hlfaker.fakeinfo;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.util.Log;

import com.hl46000.hlfaker.Common;
import com.hl46000.hlfaker.data.HookSharedPref;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by hl46000 on 9/21/17.
 */

public class FakeID {
    private final String LOG_TAG = "FakeID";
    public FakeID(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName)){
            fakeIMEI(sharedPkgParam);
            fakeAndroidID(sharedPkgParam);
            fakeAndroidSerial(sharedPkgParam);
            fakeMAC(sharedPkgParam);
            fakeGFSID(sharedPkgParam);
            fakeGAID(sharedPkgParam);
            fakeNetworkInterfaceMAC(sharedPkgParam);
        }else if(!HookUntils.isMyPackages(sharedPkgParam.packageName)) {
            fakeIMEI(sharedPkgParam);
            fakeAndroidID(sharedPkgParam);
            fakeAndroidSerial(sharedPkgParam);
            //fakeGFSID(sharedPkgParam);
            //fakeMAC(sharedPkgParam);
        }
    }

    public void fakeIMEI(XC_LoadPackage.LoadPackageParam loadPkgParam){
        String telephonyClass = "android.telephony.TelephonyManager";
        hookTelephony(telephonyClass, loadPkgParam, "getDeviceId", HookSharedPref.getXValue("IMEI"));
        hookTelephony2(telephonyClass, loadPkgParam, "getDeviceId", HookSharedPref.getXValue("IMEI"));
        hookTelephony(telephonyClass, loadPkgParam, "getSubscriberId", HookSharedPref.getXValue("IMSI"));
        hookTelephony(telephonyClass, loadPkgParam, "getSimSerialNumber", HookSharedPref.getXValue("SimSerial"));
    }

    public void fakeAndroidID(XC_LoadPackage.LoadPackageParam loadPkgParam){

        try {
            XposedHelpers.findAndHookMethod("android.provider.Settings.Secure", loadPkgParam.classLoader, "getString", ContentResolver.class, String.class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    if (HookUntils.isContains(param.args[1].toString(), "android_id")) {
                        param.setResult(HookSharedPref.getXValue("AndroidID"));
                    }
                }
            });

        } catch (Exception e) {
            Log.d(LOG_TAG, "Fake Android ID: " + e.getMessage());
        }
    }

    public void fakeAndroidSerial(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            Class<?> classSP = Class.forName("android.os.SystemProperties");
            Method get1Arg = classSP.getDeclaredMethod("get", new Class[]{String.class});
            Method get2Arg = classSP.getDeclaredMethod("get", new Class[]{String.class, String.class});
            hookSystemProperties(get1Arg);
            hookSystemProperties(get2Arg);
        }catch (Exception e){
            Log.d(LOG_TAG, "Fake SystemProperties: " + e.getMessage());
        }

        try {
            XposedHelpers.findField(Build.class, "SERIAL").set(null, HookSharedPref.getXValue("AndroidSerial"));
        }catch (Exception e){
            Log.d(LOG_TAG, "Fake Build Class: " + e.getMessage());
        }
    }

    public void fakeGAID(XC_LoadPackage.LoadPackageParam loadPkgParam){

        try {
            XposedHelpers.findAndHookMethod("android.os.Binder", loadPkgParam.classLoader, "execTransact", int.class, long.class, long.class, Integer.TYPE, new XC_MethodHook() {
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    IBinder binder = (IBinder) param.thisObject;
                    if(binder == null){
                        return;
                    }

                    String interfaceBinder = binder.getInterfaceDescriptor();

                    if(interfaceBinder == null){
                        return;
                    }

                    if (interfaceBinder.equals("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService")) {

                        int code = (Integer) param.args[0];

                        if (code == 1) {

                            Method methodObtain = Parcel.class.getDeclaredMethod("obtain", long.class);
                            methodObtain.setAccessible(true);

                            Parcel reply = (Parcel) methodObtain.invoke(null, param.args[2]);

                            reply.setDataPosition(0);
                            reply.writeNoException();

                            reply.writeString(HookSharedPref.getXValue("GAID"));

                            param.setResult(true);
                        }
                    }
                }
            });
        } catch(NullPointerException e){
            Log.d(LOG_TAG, "Fake GAID: " + e.getMessage());
        } catch (Throwable e) {
            Log.d(LOG_TAG, "Fake GAID: " + e.getMessage());
        }

        String className = "com.google.android.gms.ads.identifier.AdvertisingIdClient$Info";
        try{
            Class<?> gaidClss = XposedHelpers.findClass(className, loadPkgParam.classLoader);
            if(gaidClss == null){
                return;
            }
            XposedHelpers.findAndHookMethod(gaidClss, "getId", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(HookSharedPref.getXValue("GAID"));
                }
            });
            XposedHelpers.findAndHookMethod(gaidClss, "toString", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(HookSharedPref.getXValue("GAID"));
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake AdvertisingIdClient ERROR: " + e.getMessage());
        }

    }

    public void fakeGFSID(XC_LoadPackage.LoadPackageParam loadPkgParam){
        //hookGServiceProvider(loadPkgParam);
        //hookContentProvider(loadPkgParam);
        hookContentResolver(loadPkgParam);
    }

    private void hookGServiceProvider(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try{
            String gServiceProviderClass = "com.google.android.gsf.gservices.GservicesProvider";
            Class<?> clssGService = XposedHelpers.findClassIfExists(gServiceProviderClass, loadPkgParam.classLoader);
            if(clssGService == null){
                return;
            }
            for(Method findMethod : clssGService.getDeclaredMethods()){
                if (findMethod.getName().equals("query")){
                    XposedBridge.hookMethod(findMethod, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            if (param.args.length > 1 && param.args[0] instanceof Uri && param.getResult() != null) {
                                String uri = ((Uri) param.args[0]).toString().toLowerCase();
                                String[] projection = (param.args[1] instanceof String[] ? (String[]) param.args[1] : null);
                                String selection = (param.args[2] instanceof String ? (String) param.args[2] : null);
                                Cursor cursor = (Cursor) param.getResult();
                                if (uri.startsWith("content://com.google.android.gsf.gservices")) {
                                    // Google services provider: block only android_id
                                    if (param.args.length > 3 && param.args[3] != null) {
                                        List<String> listSelection = Arrays.asList((String[]) param.args[3]);
                                        if (listSelection.contains("android_id")) {
                                            int ikey = cursor.getColumnIndex("key");
                                            int ivalue = cursor.getColumnIndex("value");
                                            if (ikey == 0 && ivalue == 1 && cursor.getColumnCount() == 2) {
                                                MatrixCursor result = new MatrixCursor(cursor.getColumnNames());
                                                while (cursor.moveToNext()) {
                                                    if ("android_id".equals(cursor.getString(ikey)) && cursor.getString(ivalue) != null) {
                                                        String origID = cursor.getString(ivalue);
                                                        if(checkUID(Binder.getCallingUid())){
                                                            result.addRow(new Object[]{"android_id", HookSharedPref.getXValue("GSFID")});
                                                        }else {
                                                            result.addRow(new Object[]{"android_id", origID});
                                                        }
                                                    }
                                                    else
                                                        copyColumns(cursor, result);
                                                }
                                                result.respond(cursor.getExtras());
                                                param.setResult(result);
                                                cursor.close();
                                            }
                                        }else if(listSelection.contains("device_country")){
                                            int ikey = cursor.getColumnIndex("key");
                                            int ivalue = cursor.getColumnIndex("value");
                                            if (ikey == 0 && ivalue == 1 && cursor.getColumnCount() == 2) {
                                                MatrixCursor result = new MatrixCursor(cursor.getColumnNames());
                                                while (cursor.moveToNext()) {
                                                    if ("device_country".equals(cursor.getString(ikey)) && cursor.getString(ivalue) != null) {
                                                        String origID = cursor.getString(ivalue);
                                                        if(checkUID(Binder.getCallingUid())){
                                                            result.addRow(new Object[]{"device_country", HookSharedPref.getXValue("CountryISO").toLowerCase()});
                                                        }else {
                                                            result.addRow(new Object[]{"device_country", origID});
                                                        }
                                                    }
                                                    else
                                                        copyColumns(cursor, result);
                                                }
                                                result.respond(cursor.getExtras());
                                                param.setResult(result);
                                                cursor.close();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                    break;
                }
            }
        }catch (Throwable e){
            Log.d(LOG_TAG, "GServiceProvider Class Not Found: " + e.getMessage());
        }
    }

    private void hookContentProvider(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try{
            String gServiceProviderClass = "android.content.ContentProviderClient";
            Class<?> clssGService = XposedHelpers.findClassIfExists(gServiceProviderClass, loadPkgParam.classLoader);
            if(clssGService == null){
                return;
            }
            for(Method findMethod : clssGService.getDeclaredMethods()){
                if (findMethod.getName().equals("query")){
                    XposedBridge.hookMethod(findMethod, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            if (param.args.length > 1 && param.args[0] instanceof Uri && param.getResult() != null) {
                                String uri = ((Uri) param.args[0]).toString().toLowerCase();
                                String[] projection = (param.args[1] instanceof String[] ? (String[]) param.args[1] : null);
                                String selection = (param.args[2] instanceof String ? (String) param.args[2] : null);
                                Cursor cursor = (Cursor) param.getResult();
                                if (uri.startsWith("content://com.google.android.gsf.gservices")) {
                                    // Google services provider: block only android_id
                                    if (param.args.length > 3 && param.args[3] != null) {
                                        List<String> listSelection = Arrays.asList((String[]) param.args[3]);
                                        if (listSelection.contains("android_id")) {
                                            int ikey = cursor.getColumnIndex("key");
                                            int ivalue = cursor.getColumnIndex("value");
                                            if (ikey == 0 && ivalue == 1 && cursor.getColumnCount() == 2) {
                                                MatrixCursor result = new MatrixCursor(cursor.getColumnNames());
                                                while (cursor.moveToNext()) {
                                                    if ("android_id".equals(cursor.getString(ikey)) && cursor.getString(ivalue) != null) {
                                                        String origID = cursor.getString(ivalue);
                                                        if(checkUID(Binder.getCallingUid())){
                                                            result.addRow(new Object[]{"android_id", HookSharedPref.getXValue("GSFID")});
                                                        }else {
                                                            result.addRow(new Object[]{"android_id", origID});
                                                        }
                                                    }
                                                    else
                                                        copyColumns(cursor, result);
                                                }
                                                result.respond(cursor.getExtras());
                                                param.setResult(result);
                                                cursor.close();
                                            }
                                        }else if(listSelection.contains("device_country")){
                                            int ikey = cursor.getColumnIndex("key");
                                            int ivalue = cursor.getColumnIndex("value");
                                            if (ikey == 0 && ivalue == 1 && cursor.getColumnCount() == 2) {
                                                MatrixCursor result = new MatrixCursor(cursor.getColumnNames());
                                                while (cursor.moveToNext()) {
                                                    if ("device_country".equals(cursor.getString(ikey)) && cursor.getString(ivalue) != null) {
                                                        String origID = cursor.getString(ivalue);
                                                        if(checkUID(Binder.getCallingUid())){
                                                            result.addRow(new Object[]{"device_country", HookSharedPref.getXValue("CountryISO").toLowerCase()});
                                                        }else {
                                                            result.addRow(new Object[]{"device_country", origID});
                                                        }
                                                    }
                                                    else
                                                        copyColumns(cursor, result);
                                                }
                                                result.respond(cursor.getExtras());
                                                param.setResult(result);
                                                cursor.close();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                    break;
                }
            }
        }catch (Throwable e){
            Log.d(LOG_TAG, "ContentProviderClient Class Not Found: " + e.getMessage());
        }
    }

    private void hookContentResolver(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try{
            String gServiceProviderClass = "android.content.ContentResolver";
            Class<?> clssGService = XposedHelpers.findClass(gServiceProviderClass, loadPkgParam.classLoader);
            if(clssGService == null){
                return;
            }
            for(Method findMethod : clssGService.getDeclaredMethods()){
                if (findMethod.getName().equals("query")){
                    XposedBridge.hookMethod(findMethod, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            if (param.args.length > 1 && param.args[0] instanceof Uri && param.getResult() != null) {
                                String uri = ((Uri) param.args[0]).toString().toLowerCase();
                                //String[] projection = (param.args[1] instanceof String[] ? (String[]) param.args[1] : null);
                                //String selection = (param.args[2] instanceof String ? (String) param.args[2] : null);
                                Cursor cursor = (Cursor) param.getResult();
                                if (uri.startsWith("content://com.google.android.gsf.gservices")) {
                                    // Google services provider: block only android_id
                                    if (param.args.length > 3 && param.args[3] != null) {
                                        List<String> listSelection = Arrays.asList((String[]) param.args[3]);
                                        if (listSelection.contains("android_id")) {
                                            int ikey = cursor.getColumnIndex("key");
                                            int ivalue = cursor.getColumnIndex("value");
                                            if (ikey == 0 && ivalue == 1 && cursor.getColumnCount() == 2) {
                                                MatrixCursor result = new MatrixCursor(cursor.getColumnNames());
                                                while (cursor.moveToNext()) {
                                                    if ("android_id".equals(cursor.getString(ikey)) && cursor.getString(ivalue) != null) {
                                                        String origID = cursor.getString(ivalue);
                                                        if(checkUID(Binder.getCallingUid())){
                                                            result.addRow(new Object[]{"android_id", HookSharedPref.getXValue("GSFID")});
                                                        }else {
                                                            result.addRow(new Object[]{"android_id", origID});
                                                            //result.addRow(new Object[]{"android_id", HookSharedPref.getXValue("GSFID")});
                                                        }
                                                    }
                                                    else{
                                                        if(checkUID(Binder.getCallingUid())){
                                                            continue;
                                                        }else {
                                                            copyColumns(cursor, result);
                                                        }
                                                        //copyColumns(cursor, result);
                                                    }
                                                }
                                                result.respond(cursor.getExtras());
                                                param.setResult(result);
                                                cursor.close();
                                            }
                                        }else if(listSelection.contains("device_country")){
                                            int ikey = cursor.getColumnIndex("key");
                                            int ivalue = cursor.getColumnIndex("value");
                                            if (ikey == 0 && ivalue == 1 && cursor.getColumnCount() == 2) {
                                                MatrixCursor result = new MatrixCursor(cursor.getColumnNames());
                                                while (cursor.moveToNext()) {
                                                    if ("device_country".equals(cursor.getString(ikey)) && cursor.getString(ivalue) != null) {
                                                        String origID = cursor.getString(ivalue);
                                                        if(checkUID(Binder.getCallingUid())){
                                                            result.addRow(new Object[]{"device_country", HookSharedPref.getXValue("CountryISO").toLowerCase()});
                                                        }else {
                                                            //result.addRow(new Object[]{"device_country", origID});
                                                            result.addRow(new Object[]{"device_country", HookSharedPref.getXValue("CountryISO").toLowerCase()});
                                                        }
                                                    }
                                                    else{
                                                        if(checkUID(Binder.getCallingUid())){
                                                            continue;
                                                        }else {
                                                            copyColumns(cursor, result);
                                                        }
                                                        //copyColumns(cursor, result);

                                                    }
                                                }
                                                result.respond(cursor.getExtras());
                                                param.setResult(result);
                                                cursor.close();
                                            }
                                        }else if(listSelection.contains("auth_proximity_features_bluetooth_server_uuid")){
                                            int ikey = cursor.getColumnIndex("key");
                                            int ivalue = cursor.getColumnIndex("value");
                                            if (ikey == 0 && ivalue == 1 && cursor.getColumnCount() == 2) {
                                                MatrixCursor result = new MatrixCursor(cursor.getColumnNames());
                                                while (cursor.moveToNext()) {
                                                    if ("auth_proximity_features_bluetooth_server_uuid".equals(cursor.getString(ikey)) && cursor.getString(ivalue) != null) {
                                                        String origID = cursor.getString(ivalue);
                                                        if(checkUID(Binder.getCallingUid())){
                                                            result.addRow(new Object[]{"auth_proximity_features_bluetooth_server_uuid", HookSharedPref.getXValue("GAID")});
                                                        }else {
                                                            result.addRow(new Object[]{"auth_proximity_features_bluetooth_server_uuid", origID});
                                                        }
                                                    }
                                                    else{
                                                        if(checkUID(Binder.getCallingUid())){
                                                            continue;
                                                        }else {
                                                            copyColumns(cursor, result);
                                                        }
                                                        //copyColumns(cursor, result);

                                                    }
                                                }
                                                result.respond(cursor.getExtras());
                                                param.setResult(result);
                                                cursor.close();
                                            }
                                        }else{
                                            MatrixCursor result = new MatrixCursor(cursor.getColumnNames());
                                            if(checkUID(Binder.getCallingUid())){
                                                param.setResult(result);
                                                cursor.close();
                                            }else {
                                                cursor.close();
                                            }
                                        }
                                    }
                                }
                                /*
                                else {
                                    if(checkUID(Binder.getCallingUid())){
                                        MatrixCursor result = new MatrixCursor(cursor.getColumnNames());
                                        result.respond(cursor.getExtras());
                                        param.setResult(result);
                                        cursor.close();
                                    }else {
                                        cursor.close();
                                    }
                                }
                                */
                            }
                        }
                    });
                    //break;
                }
            }
        }catch (Throwable e){
            Log.d(LOG_TAG, "ContentResolver Class Not Found: " + e.getMessage());
        }
    }


    private static boolean checkUID(int uid){
        int _uid = HookUntils.getAppId(uid);
        if(uid <= 0){
            return false;
        }
        if((_uid == Process.SYSTEM_UID)){
            return false;
        }

        if(!isApplication(uid)){
            return false;
        }
        return true;
    }

    public static boolean isApplication(int uid) {
        uid = HookUntils.getAppId(uid);
        return (uid >= Process.FIRST_APPLICATION_UID && uid <= Process.LAST_APPLICATION_UID);
    }

    private void copyColumns(Cursor cursor, MatrixCursor result) {
        copyColumns(cursor, result, cursor.getColumnCount());
    }

    private void copyColumns(Cursor cursor, MatrixCursor result, int count) {
        try {
            Object[] columns = new Object[count];
            for (int i = 0; i < count; i++)
                switch (cursor.getType(i)) {
                    case Cursor.FIELD_TYPE_NULL:
                        columns[i] = null;
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        columns[i] = cursor.getInt(i);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        columns[i] = cursor.getFloat(i);
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        columns[i] = cursor.getString(i);
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        columns[i] = cursor.getBlob(i);
                        break;
                    default:
                        //Util.log(this, Log.WARN, "Unknown cursor data type=" + cursor.getType(i));
                }
            result.addRow(columns);
        } catch (Throwable ex) {
            //Util.bug(this, ex);
        }
    }

    public void fakeNetworkInterfaceMAC(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try{
            XposedHelpers.findAndHookMethod("java.net.NetworkInterface", loadPkgParam.classLoader, "getHardwareAddress", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(HookSharedPref.getXValue("MAC").getBytes());
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake NetworkInterfaceMAC ERROR: " + e.getMessage());
        }
    }

    public void fakeMAC(XC_LoadPackage.LoadPackageParam loadPkgParam){

        try {
            XposedBridge.hookAllConstructors(File.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (param.args.length == 1 && String.class.isInstance(param.args[0])) {
                        if (param.args[0].equals("/sys/class/net")) {
                            param.args[0] = "/sdcard/HLDATA/net";
                        } else if (HookUntils.isContains(param.args[0].toString(), "/sys/class/net")) {
                            param.args[0] = param.args[0].toString().replace("/sys/class/net", "/sdcard/HLDATA/net");
                        } else if (HookUntils.isContains(param.args[0].toString(), "/block/mmcblk0/device/cid")) {
                            param.args[0] = "/sdcard/HLDATA/cid";
                        } else if (HookUntils.isContains(param.args[0].toString(), "/kernel/random/uuid") || HookUntils.isContains(param.args[0].toString(), "/kernel/random/boot_id")) {
                            param.args[0] = "/sdcard/HLDATA/uuid";
                        }

                    } else if (param.args.length == 2 && String.class.isInstance(param.args[0])) {
                        if (param.args[0].equals("/sys/class/net")) {
                            param.args[0] = "/sdcard/HLDATA/net";
                        } else if (HookUntils.isContains(param.args[0].toString(), "/sys/class/net")) {
                            param.args[0] = param.args[0].toString().replace("/sys/class/net", "/sdcard/HLDATA/net");
                        } else if (HookUntils.isContains(param.args[0].toString(), "/block/mmcblk0/device/cid")) {
                            param.args[0] = "/sdcard/HLDATA/cid";
                        } else if (HookUntils.isContains(param.args[0].toString(), "/kernel/random/uuid") || HookUntils.isContains(param.args[0].toString(), "/kernel/random/boot_id")) {
                            param.args[0] = "/sdcard/HLDATA/uuid";
                        }
                    }
                }
            });
        }catch (Exception e) {
            Log.d(LOG_TAG, "Fake NetPath File ERROR: " + e.getMessage());
        }

        /*
        try {
            XposedHelpers.findAndHookMethod("java.lang.Runtime", loadPkgParam.classLoader, "exec", String[].class, String[].class, File.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (param.args.length == 1) {
                        if (param.args[0].equals("/sys/class/net")) {
                            param.args[0] = "/sdcard/HLDATA/net";
                        }else if(HookUntils.isContains(param.args[0].toString(), "/sys/class/net")){
                            param.args[0] = param.args[0].toString().replace("/sys/class/net", "/sdcard/HLDATA/net");
                        }else if(HookUntils.isContains(param.args[0].toString(), "/block/mmcblk0/device/cid")){
                            param.args[0] = "/sdcard/HLDATA/cid";
                        }else if(HookUntils.isContains(param.args[0].toString(), "/kernel/random/uuid") || HookUntils.isContains(param.args[0].toString(), "/kernel/random/boot_id")){
                            param.args[0] = "/sdcard/HLDATA/uuid";
                        }
                    } else if (param.args.length == 2 && !File.class.isInstance(param.args[0])) {
                        int i = 0;
                        while (i < 2) {
                            if (param.args[i] != null) {
                                if (HookUntils.isContains(param.args[i].toString(), "/sys/class/net")) {
                                    param.args[i] = param.args[i].toString().replace("/sys/class/net", "/sdcard/HLDATA/net");
                                }else if (HookUntils.isContains(param.args[i].toString(), "/block/mmcblk0/device/cid")) {
                                    param.args[i] = "/sdcard/HLDATA/cid";
                                }else if (HookUntils.isContains(param.args[i].toString(), "/kernel/random/uuid") || HookUntils.isContains(param.args[i].toString(), "/kernel/random/boot_id")) {
                                    param.args[i] = "/sdcard/HLDATA/uuid";
                                }
                            }
                            i++;
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.d(LOG_TAG, "Fake NetPath Runtime ERROR: " + e.getMessage());
        }

        try {
            XposedBridge.hookAllConstructors(ProcessBuilder.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (param.args[0] != null && String[].class.isInstance(param.args[0])) {
                        String[] strArr = (String[]) param.args[0];
                        List<String> args = new ArrayList<String>();
                        //String str = "";
                        for (String str : strArr) {
                            //str = new StringBuilder(String.valueOf(str)).append(str2).append(":").toString();
                            if (str.equals("/sys/class/net")) {
                                //strArr[1] = "/sdcard/HLDATA/net";
                                args.add("/sdcard/HLDATA/net");
                            }else if(HookUntils.isContains(str, "/sys/class/net")){
                                //strArr[1] = strArr[1].replace("/sys/class/net", "/sdcard/HLDATA/net");
                                args.add(str.replace("/sys/class/net", "/sdcard/HLDATA/net"));
                            }else if(HookUntils.isContains(str, "/block/mmcblk0/device/cid")) {
                                args.add("/sdcard/HLDATA/cid");
                            }else if(HookUntils.isContains(str, "/kernel/random/uuid") || HookUntils.isContains(str, "/kernel/random/boot_id")) {
                                args.add("/sdcard/HLDATA/uuid");
                            }else {
                                args.add(str);
                            }
                        }
                        param.args[0] = args.toArray();
                    }else if(param.args[0] != null && List.class.isInstance(param.args[0])) {
                        List<String> strArr = (List<String>) param.args[0];
                        List<String> args = new ArrayList<String>();
                        //String str = "";
                        for (String str : strArr) {
                            //str = new StringBuilder(String.valueOf(str)).append(str2).append(":").toString();
                            if (str.equals("/sys/class/net")) {
                                //strArr[1] = "/sdcard/HLDATA/net";
                                args.add("/sdcard/HLDATA/net");
                            }else if(HookUntils.isContains(str, "/sys/class/net")){
                                //strArr[1] = strArr[1].replace("/sys/class/net", "/sdcard/HLDATA/net");
                                args.add(str.replace("/sys/class/net", "/sdcard/HLDATA/net"));
                            }else if(HookUntils.isContains(str, "/block/mmcblk0/device/cid")) {
                                args.add("/sdcard/HLDATA/cid");
                            }else if(HookUntils.isContains(str, "/kernel/random/uuid") || HookUntils.isContains(str, "/kernel/random/boot_id")) {
                                args.add("/sdcard/HLDATA/cid");
                            }else {
                                args.add(str);
                            }
                        }
                        param.args[0] = args;
                    }
                }
            });
        } catch (Exception e) {
            Log.d(LOG_TAG, "Fake NetPath ProcessBuilder ERROR: " + e.getMessage());
        }
        */
        try {
            XposedBridge.hookAllConstructors(RandomAccessFile.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (param.args.length == 2) {
                        if (param.args[0].equals("/sys/class/net")) {
                            param.args[0] = "/sdcard/HLDATA/net";
                        }else if(HookUntils.isContains(param.args[0].toString(), "/sys/class/net")){
                            param.args[0] = param.args[0].toString().replace("/sys/class/net", "/sdcard/HLDATA/net");
                        }else if(HookUntils.isContains(param.args[0].toString(), "/block/mmcblk0/device/cid")){
                            param.args[0] = "/sdcard/HLDATA/cid";
                        }else if(HookUntils.isContains(param.args[0].toString(), "/kernel/random/uuid") || HookUntils.isContains(param.args[0].toString(), "/kernel/random/boot_id")){
                            param.args[0] = "/sdcard/HLDATA/uuid";
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.d(LOG_TAG, "Fake NetPath RandomAccessFile ERROR: " + e.getMessage());
        }

        try {
            XposedBridge.hookAllConstructors(FileReader.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (String.class.isInstance(param.args[0])) {
                        if (param.args[0].equals("/sys/class/net")) {
                            param.args[0] = "/sdcard/HLDATA/net";
                        }else if(HookUntils.isContains(param.args[0].toString(), "/sys/class/net")){
                            param.args[0] = param.args[0].toString().replace("/sys/class/net", "/sdcard/HLDATA/net");
                        }else if(HookUntils.isContains(param.args[0].toString(), "/block/mmcblk0/device/cid")){
                            param.args[0] = "/sdcard/HLDATA/cid";
                        }else if(HookUntils.isContains(param.args[0].toString(), "/kernel/random/uuid") || HookUntils.isContains(param.args[0].toString(), "/kernel/random/boot_id")){
                            param.args[0] = "/sdcard/HLDATA/cid";
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.d(LOG_TAG, "Fake NetPath FileReader ERROR: " + e.getMessage());
        }

        try {
            XposedBridge.hookAllConstructors(FileInputStream.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (param.args.length == 2) {
                        if (param.args[0].equals("/sys/class/net")) {
                            param.args[0] = "/sdcard/HLDATA/net";
                        }else if(HookUntils.isContains(param.args[0].toString(), "/sys/class/net")){
                            param.args[0] = param.args[0].toString().replace("/sys/class/net", "/sdcard/HLDATA/net");
                        }else if(HookUntils.isContains(param.args[0].toString(), "/block/mmcblk0/device/cid")){
                            param.args[0] = "/sdcard/HLDATA/cid";
                        }else if(HookUntils.isContains(param.args[0].toString(), "/kernel/random/uuid") || HookUntils.isContains(param.args[0].toString(), "/kernel/random/boot_id")){
                            param.args[0] = "/sdcard/HLDATA/cid";
                        }
                    }
                }

            });
        } catch (Exception e) {
            Log.d(LOG_TAG, "Fake NetPath FileInputStream ERROR: " + e.getMessage());
        }
        /*
        try {
            XposedHelpers.findAndHookMethod("java.util.regex.Pattern", loadPkgParam.classLoader, "matcher", CharSequence.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.beforeHookedMethod(param);
                    if (param.args.length == 1) {
                        if (param.args[0].equals("/sys/class/net")) {
                            param.args[0] = "/sdcard/HLDATA/net";
                        }else if(HookUntils.isContains(param.args[0].toString(), "/sys/class/net")){
                            param.args[0] = param.args[0].toString().replace("/sys/class/net", "/sdcard/HLDATA/net");
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.d(LOG_TAG, "Fake NetPath Pattern ERROR: " + e.getMessage());
        }
        */
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
                    if(prop.equals("ro.serialno")
                            || prop.equals("ro.boot.serialno")
                            || prop.equals("ril.serialnumber")
                            || prop.equals("sys.serialnumber")) {
                        param.setResult(HookSharedPref.getXValue("AndroidSerial"));
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

    private void hookTelephony2(String hookClass, XC_LoadPackage.LoadPackageParam loadPkgParam, String funcName, final String value){
        try {
            XposedHelpers.findAndHookMethod(hookClass, loadPkgParam.classLoader, funcName, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(value);
                }
            });
        }catch (Exception e){
            Log.d(LOG_TAG, "Hook Telephony2 String: " + e.getMessage());
        }
    }

}
