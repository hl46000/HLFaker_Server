package com.hl46000.hlfaker.fakeinfo;

import android.accounts.Account;
import android.util.Log;
import com.hl46000.hlfaker.Common;

import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZEROETC on 2/16/2018.
 */

public class FakeGoogleSignIn {
    private final String LOG_TAG = "FakeGoogleSignIn";
    //private Player mPlayer;
    public FakeGoogleSignIn(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){
            hookGoogleSignInAccount(sharedPkgParam);
            //mPlayer.
        }
    }

    public void hookGoogleSignInAccount(XC_LoadPackage.LoadPackageParam loadPkgParam){
        String className = "com.google.android.gms.auth.api.signin.GoogleSignInAccount";
        try{
            Class<?> credentialClss = XposedHelpers.findClass(className, loadPkgParam.classLoader);
            if(credentialClss == null){
                return;
            }
            XposedHelpers.findAndHookMethod(credentialClss, "getAccount", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Account fakeAcc = new Account(randomMail(), "com.google");
                    param.setResult(fakeAcc);
                }
            });
            XposedHelpers.findAndHookMethod(credentialClss, "getDisplayName", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(randomMail().replace("@gmail.com", ""));
                }
            });
            XposedHelpers.findAndHookMethod(credentialClss, "getEmail", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(randomMail());
                }
            });
            XposedHelpers.findAndHookMethod(credentialClss, "getFamilyName", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(randomMail().replace("@gmail.com", ""));
                }
            });
            XposedHelpers.findAndHookMethod(credentialClss, "getGivenName", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(randomMail().replace("@gmail.com", ""));
                }
            });
            XposedHelpers.findAndHookMethod(credentialClss, "getId", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(randomMail().replace("@gmail.com", ""));
                }
            });
            XposedHelpers.findAndHookMethod(credentialClss, "getIdToken", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(randomMail().replace("@gmail.com", ""));
                }
            });
        }catch (Throwable e){
            Log.d(LOG_TAG, "Fake GoogleSignInAccount ERROR: " + e.getMessage());
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
