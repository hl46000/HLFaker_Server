package com.hl46000.hlfaker.fakeinfo;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Process;
import android.util.Log;

import com.hl46000.hlfaker.Common;
import com.hl46000.hlfaker.data.HookSharedPref;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZEROETC on 2/1/2018.
 */

public class FakeAsyncAdapter {
    private final String LOG_TAG  = "FakeAsyncAdapter";
    public FakeAsyncAdapter(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !sharedPkgParam.packageName.equals(Common.XPOSED_PACKAGE) &&
                !sharedPkgParam.packageName.equals(Common.HLFAKER_PACKAGE) && !sharedPkgParam.packageName.equals(Common.SUPERSU_PACKAGE ) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){
            hookContentResolver(sharedPkgParam);
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
                                Cursor cursor = (Cursor) param.getResult();
                                //Log.d(LOG_TAG, "URI: " + uri.toString());
                                if (!uri.startsWith("content://com.google.android.gsf.gservices")) {
                                    if (param.args.length > 3 && param.args[3] != null) {
                                        int ikey = cursor.getColumnIndex("key");
                                        int ivalue = cursor.getColumnIndex("value");
                                        if (ikey == 0 && ivalue == 1 && cursor.getColumnCount() == 2) {
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
                            }
                        }
                    });
                }
            }
        }catch (Throwable e){
            Log.d(LOG_TAG, "FakeAsyncAdapter ContentResolver Class Not Found: " + e.getMessage());
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
}
