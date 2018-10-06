package com.hl46000.hlfaker.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hl46000.hlfaker.Common;

/**
 * Created by LONG-iOS Dev on 9/24/2017.
 */

public class ProxySharedPref {
    private Context myContext;
    private SharedPreferences proxyPref;
    private final String LOG_TAG = "ProxySharedPref";

    public ProxySharedPref(Context mContext){
        this.myContext = mContext;
        proxyPref = myContext.getSharedPreferences(Common.PROXY_PREF_FILE, 1);
    }

    /**
     * Write a Key and Value
     * @param key pref key
     * @param value List packages
     */
    public boolean setValue(String key, String value){
        try{
            if (value == null || value == ""){
                return false;
            }
            proxyPref.edit().putString(key, value).commit();
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Set Value ERROR: " + key);
            return false;
        }
    }

    /**
     * Get Value of Key
     * @param key pref key
     * @return
     */
    public String getValue(String key){
        String value = "";
        try{
            value = proxyPref.getString(key, null);
        }catch (Exception e){
            Log.d(LOG_TAG, "Get Value ERROR: " + key);
        }
        return value;
    }
}
