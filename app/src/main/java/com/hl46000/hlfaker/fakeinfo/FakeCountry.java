package com.hl46000.hlfaker.fakeinfo;

import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hl46000.hlfaker.data.HookSharedPref;

import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by hl46000 on 9/21/17.
 */

public class FakeCountry {
    private final String LOG_TAG = "FakeCountry";

    public FakeCountry(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        fakeLocale(sharedPkgParam);
    }

    /**
     * Fake Locale Info
     * @param loadPkgParam
     */
    public void fakeLocale(XC_LoadPackage.LoadPackageParam loadPkgParam){
        try {
            Locale fakeLocale = null;
            try {
                fakeLocale = new Locale("en", HookSharedPref.getXValue("CountryISO"));
            }catch (Exception ex){
                fakeLocale = new Locale("en", "US");
            }

            final Locale finalFakeLocale = fakeLocale;
            XposedHelpers.findAndHookMethod("java.util.Locale", loadPkgParam.classLoader, "getDefault", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.afterHookedMethod(param);
                    if(param.getResult() != null){
                        param.setResult(finalFakeLocale);
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook Locale ERROR: " + e.getMessage());
        }

        try {
            Locale fakeLocale = null;
            try {
                fakeLocale = new Locale("en", HookSharedPref.getXValue("CountryISO"));
            }catch (Exception ex){
                fakeLocale = new Locale("en", "US");
            }

            final Locale finalFakeLocale = fakeLocale;
            XposedHelpers.findAndHookMethod("android.content.res.Resources", loadPkgParam.classLoader, "getConfiguration", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    // TODO Auto-generated method stub
                    super.afterHookedMethod(param);
                    Configuration cf = (Configuration) param.getResult();
                    //cf.locale = fakeLocale;
                    cf.mcc = tryParseInt(HookSharedPref.getXValue("MCC"));
                    cf.mnc = tryParseInt(HookSharedPref.getXValue("MNC"));

                    if(Build.VERSION.SDK_INT < 24){
                        cf.locale = finalFakeLocale;
                    }else {
                        LocaleList fakeList = new LocaleList(finalFakeLocale);
                        cf.setLocales(fakeList);
                    }
                    //cf.screenLayout = tryParseInt(SharedPref.getXValue("ScreenLayout"));
                    if(param.getResult() != null){
                        param.setResult(cf);
                    }
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Hook Resources ERROR: " + e.getMessage());
        }

    }

    public int tryParseInt(String value){
        try {
            return Integer.parseInt(value);
        }catch (Exception e){
            return 0;
        }
    }
}
