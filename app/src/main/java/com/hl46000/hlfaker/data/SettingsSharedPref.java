package com.hl46000.hlfaker.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hl46000.hlfaker.Common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LONG-iOS Dev on 9/23/2017.
 */

public class SettingsSharedPref {
    private Context myContext;
    private SharedPreferences settingsPref;
    private final String LOG_TAG = "SettingsSharedPref";

    public SettingsSharedPref(Context mContext){
        this.myContext = mContext;
        settingsPref = myContext.getSharedPreferences(Common.SETTING_PREF_FILE, 1);
    }

    /**
     * Write key/value to Settings SharedPref
     * @param key pref key
     * @param value List packages
     */
    public boolean setValue(String key, String value){
        try{
            //settingsPref.edit().clear().commit();
            if (value == null || value == ""){
                return false;
            }
            //Set<String> pkgSet = new HashSet<String>();
            // pkgSet.addAll(value);
            //settingsPref.edit().putStringSet(key, pkgSet).commit();
            settingsPref.edit().putString(key, value).commit();
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Set Value ERROR: " + key);
            return false;
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
            value = settingsPref.getString(key, null);
        }catch (Exception e){
            Log.d(LOG_TAG, "Get Value ERROR: " + key);
        }
        return value;
    }
}
