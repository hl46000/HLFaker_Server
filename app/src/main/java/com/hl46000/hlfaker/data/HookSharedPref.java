package com.hl46000.hlfaker.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hl46000.hlfaker.Common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by LONG-iOS Dev on 8/11/2017.
 */

public class HookSharedPref {
    private Context appContext;
    private SharedPreferences hookSharedPref;
    private static XSharedPreferences xHookSharedPref;
    private static final String LOG_TAG = "HookShreadPref";
    public HookSharedPref(Context mContext){
        this.appContext = mContext;
        hookSharedPref = appContext.getSharedPreferences(Common.HOOK_PREF_FILE, 1);
    }

    /**
     * Write key/value to Settings SharedPref
     * @param key pref key
     * @param value List packages
     */
    public boolean setValue(String key, String value){
        try{
            if (value == null || value == ""){
                return false;
            }
            hookSharedPref.edit().putString(key, value).commit();
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Set Value ERROR: " + key);
            return false;
        }
    }

    public void setSystemPackages(List<String> value){
        try{
            if (value.isEmpty()){
                return;
            }
            Set<String> pkgSet = new HashSet<String>();
            pkgSet.addAll(value);
            hookSharedPref.edit().putStringSet("SystemPackages", pkgSet).commit();
        }catch (Exception e){
            Log.d(LOG_TAG, "Set Value ERROR: SystemPackages");
        }
    }

    /**
     * Get value of key from Settings SharedPref
     * @param key pref key
     * @return
     */
    public String getValue(String key){
        String value = "";
        try{
            value = hookSharedPref.getString(key, null);
        }catch (Exception e){
            Log.d(LOG_TAG, "Get Value ERROR: " + key);
        }
        return value;
    }

    /**
     * Reload XHookSharedPref Data
     */
    private static void reloadXHookSharedPref(){
        if(xHookSharedPref == null){
            xHookSharedPref = new XSharedPreferences(Common.HLFAKER_PACKAGE, Common.HOOK_PREF_FILE);
            xHookSharedPref.makeWorldReadable();
        }else {
            xHookSharedPref.makeWorldReadable();
            xHookSharedPref.reload();
        }
    }

    public static String getXValue(String key){
        String value = "";
        if(key == null || key == ""){
            return value;
        }
        try {
            reloadXHookSharedPref();
            value = xHookSharedPref.getString(key, null);
        }catch (Exception e){
            Log.d(LOG_TAG, "getXValue: " + e.getMessage());
        }
        return value;
    }

    public static List<String> getXSystemPackage(){
        List<String> value = null;
        try{
            reloadXHookSharedPref();
            Set<String> pkgSet = xHookSharedPref.getStringSet("SystemPackages", null);
            value = new ArrayList<String>(pkgSet);
        }catch (Exception e){
            Log.d("WipeSharedPref", "Get Value ERROR: SystemPackages");
        }
        return value;
    }

}
