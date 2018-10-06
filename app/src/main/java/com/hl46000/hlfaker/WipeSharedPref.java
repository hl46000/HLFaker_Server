package com.hl46000.hlfaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LONG-iOS Dev on 8/2/2017.
 */

public class WipeSharedPref {
    private Context myContext;
    private SharedPreferences wipePref;

    public WipeSharedPref(Context mContext){
        this.myContext = mContext;
        wipePref = myContext.getSharedPreferences(Common.WIPE_PREF_FILE, 1);
    }

    /**
     * Write list wipe packages to Shared Preferences
     * @param key pref key
     * @param value List packages
     */
    public void setValue(String key, List<String> value){
        try{
            wipePref.edit().clear().commit();
            if (value.isEmpty()){
                return;
            }
            Set<String> pkgSet = new HashSet<String>();
            pkgSet.addAll(value);
            wipePref.edit().putStringSet(key, pkgSet).commit();
        }catch (Exception e){
            Log.d("WipeSharedPref", "Set Value ERROR: " + key);
        }
    }

    /**
     * Get list wipe packages from Shared Preferences
     * @param key pref key
     * @return
     */
    public List<String> getValue(String key){
        if(!wipePref.contains(key)){
            List<String> value = new ArrayList<String>();
            return value;
        }
        List<String> value = null;
        try{
            Set<String> pkgSet = wipePref.getStringSet(key, null);
            value = new ArrayList<String>(pkgSet);
        }catch (Exception e){
            Log.d("WipeSharedPref", "Get Value ERROR: " + key);
        }
        return value;
    }



}
