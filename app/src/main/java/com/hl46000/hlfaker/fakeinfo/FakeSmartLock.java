package com.hl46000.hlfaker.fakeinfo;

import android.util.Log;

import com.hl46000.hlfaker.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
/**
 * Created by ZEROETC on 2/8/2018.
 */

public class FakeSmartLock {
    private final String LOG_TAG = "FakeSmartLock";
    public FakeSmartLock(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){
            hookCredential(sharedPkgParam);
            hookIdToken(sharedPkgParam);
            hookHintRequestBuilder(sharedPkgParam);
            hookCredentialRequestResponse(sharedPkgParam);
        }

    }

    public void hookCredential(XC_LoadPackage.LoadPackageParam loadPkgParam){
        String className = "com.google.android.gms.auth.api.credentials.Credential";
        try{
            Class<?> credentialClss = XposedHelpers.findClass(className, loadPkgParam.classLoader);
            if(credentialClss == null){
                return;
            }
            XposedHelpers.findAndHookMethod(credentialClss, "getId", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Log.d(LOG_TAG, "getID: " + param.getResult());
                    param.setResult(randomMail());
                }
            });
            XposedHelpers.findAndHookMethod(credentialClss, "getName", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Log.d(LOG_TAG, "getName: " + param.getResult());
                    param.setResult("DKMM");
                }
            });
            XposedHelpers.findAndHookMethod(credentialClss, "getPassword", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult("dcmmCheckCC");
                }
            });
            XposedHelpers.findAndHookMethod(credentialClss, "getGeneratedPassword", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult("dcmmCheckCC");
                }
            });
            XposedHelpers.findAndHookMethod(credentialClss, "getIdTokens", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    List<Object> emptyList = new ArrayList<Object>();
                    param.setResult(emptyList);
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake Credential ERROR: " + e.getMessage());
        }
    }

    public void hookIdToken(XC_LoadPackage.LoadPackageParam loadPkgParam){
        String className = "com.google.android.gms.auth.api.credentials.IdToken";
        try{
            Class<?> credentialClss = XposedHelpers.findClass(className, loadPkgParam.classLoader);
            if(credentialClss == null){
                return;
            }
            XposedHelpers.findAndHookMethod(credentialClss, "getIdToken", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Log.d(LOG_TAG, "getIdToken: " + param.getResult());
                    param.setResult("");
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake IdToken ERROR: " + e.getMessage());
        }
    }

    public void hookHintRequestBuilder(XC_LoadPackage.LoadPackageParam loadPkgParam){
        String className = "com.google.android.gms.auth.api.credentials.HintRequest$Builder";
        try{
            Class<?> credentialClss = XposedHelpers.findClass(className, loadPkgParam.classLoader);
            if(credentialClss == null){
                return;
            }
            XposedHelpers.findAndHookMethod(credentialClss, "setIdTokenNonce", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Log.d(LOG_TAG, "setIdToken: " + param.args[0]);
                    param.args[0] = "DKMM";
                }
            });
            XposedHelpers.findAndHookMethod(credentialClss, "setServerClientId", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Log.d(LOG_TAG, "setServerClientId: " + param.args[0]);
                    param.args[0] = "DKMM";
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake HintRequestBuilder ERROR: " + e.getMessage());
        }
    }

    public void hookCredentialRequestResponse(XC_LoadPackage.LoadPackageParam loadPkgParam){
        String className = "com.google.android.gms.auth.api.credentials.CredentialRequestResponse";
        try{
            Class<?> credentialClss = XposedHelpers.findClass(className, loadPkgParam.classLoader);
            if(credentialClss == null){
                return;
            }
            XposedHelpers.findAndHookMethod(credentialClss, "getCredential", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(null);
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake CredentialRequestResponse ERROR: " + e.getMessage());
        }
    }

    public String randomMail(){
        Random rnd = new Random();
        String ch = "qwertyuiopasdfghjklzxcvbnnmQWERTYUIOPASDFGHJKLZXCVBNM";
        String mail = "";
        while (mail.length() <= 8){
            mail = mail + ch.charAt(rnd.nextInt(ch.length()-1));
        }
        return mail + "@gmail.com";
    }
}
