package com.hl46000.hlfaker.fakeinfo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Binder;
import android.os.Process;
import android.util.Log;

import com.hl46000.hlfaker.Common;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by hl46000 on 11/3/17.
 */

public class FakeAccount {
    private final String AccManager = "android.accounts.AccountManager";
    private final String AccManagerService = "com.android.server.accounts.AccountManagerService";
    private final String LOG_TAG = "FakeAccount";

    public FakeAccount(ClassLoader classLoader){
        hookAccountManager(classLoader);
        hookAccountManagerService(classLoader);
    }

    public static void fakeGmail(XC_LoadPackage.LoadPackageParam sharedPkgParam){
        if(!HookUntils.isSystemPackage(sharedPkgParam.appInfo) && !HookUntils.isMyPackages(sharedPkgParam.packageName) &&
                !sharedPkgParam.packageName.equals(Common.PLAYSERVICE_PACKAGE) && !sharedPkgParam.packageName.equals(Common.PLAYSTORE_PACKAGE)){

            try {

                XposedHelpers.findAndHookMethod("android.accounts.AccountManager", sharedPkgParam.classLoader, "getAccounts", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        param.setResult(new Account[]{new Account(randomMail(), "com.google")});
                    }
                });

            } catch (Throwable e) {
                XposedBridge.log("Fake Email ERROR: " + e.getMessage());
            }

            try {

                XposedHelpers.findAndHookMethod("android.accounts.AccountManager", sharedPkgParam.classLoader, "getAccountsByType", String.class, new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        param.setResult(new Account[]{new Account(randomMail(), "com.google")});
                    }

                });
            } catch (Throwable e) {
                XposedBridge.log("Fake Email ERROR: " + e.getMessage());
            }

            /*
            try {

                XposedHelpers.findAndHookMethod("android.accounts.AccountManager", sharedPkgParam.classLoader, "getAccountsByTypeForPackage", String.class, String.class, new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        //param.setResult(new Account[]{new Account(SharedPref.getXValue("Email"), "com.google")});
                        param.setResult(new Account[]{});
                    }

                });
            } catch (Throwable e) {
                XposedBridge.log("Fake Email ERROR: " + e.getMessage());
            }
            */
        }
    }

    public static String randomMail(){
        Random rnd = new Random();
        String ch = "qwertyuiopasdfghjklzxcvbnnmQWERTYUIOPASDFGHJKLZXCVBNM";
        String mail = "";
        while (mail.length() <= 8){
            mail = mail + ch.charAt(rnd.nextInt(ch.length()-1));
        }
        return mail + "@gmail.com";
    }

    public void hookAccountManager(ClassLoader clssLoader){
        try {
            Class<?> accManagerClass = XposedHelpers.findClassIfExists(AccManager, clssLoader);
            if(accManagerClass == null){
                return;
            }
            for(Method findMethod : accManagerClass.getDeclaredMethods()){
                if(findMethod.getName().equals("getAccountsByTypeAndFeatures")){
                    XposedBridge.hookMethod(findMethod, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if (param.args.length > 2 && param.args[2] != null){
                                AccountManagerCallback<Account[]> callback = (AccountManagerCallback<Account[]>) param.args[2];
                                param.args[2] = new XAccountManagerCallbackAccount(callback, Binder.getCallingUid());
                            }
                        }
                    });
                }else if (findMethod.getName().equals("getAccounts")){
                    XposedBridge.hookMethod(findMethod, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            int uid = Binder.getCallingUid();
                            if (param.getResult() != null) {
                                Account[] accounts = (Account[]) param.getResult();
                                param.setResult(filterAccounts(accounts, uid));
                            }
                        }
                    });
                }else if (findMethod.getName().equals("getAccountsByTypeForPackage")){
                    XposedBridge.hookMethod(findMethod, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            int uid = Binder.getCallingUid();
                            if (param.getResult() != null && param.args.length > 0){
                                Account[] accounts = (Account[]) param.getResult();
                                param.setResult(filterAccounts(accounts, uid));
                            }
                        }
                    });
                }else if (findMethod.getName().equals("getAccountsByTypeAndFeatures")){
                    XposedBridge.hookMethod(findMethod, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            int uid = Binder.getCallingUid();
                            if (param.getResult() != null && param.args.length > 0){
                                AccountManagerFuture<Account[]> future = (AccountManagerFuture<Account[]>) param.getResult();
                                param.setResult(new XFutureAccount(future, uid));
                            }
                        }
                    });
                }
            }
        }catch (Throwable e){
            Log.d(LOG_TAG, "AccountManager Class Not Found: " + e.getMessage());
        }
    }

    public void hookAccountManagerService(ClassLoader clssLoader){
        try {
            Class<?> accManagerServiceClass = XposedHelpers.findClassIfExists(AccManagerService, clssLoader);
            if(accManagerServiceClass == null){
                return;
            }
            for(Method findMethod : accManagerServiceClass.getDeclaredMethods()){
                if(findMethod.getName().equals("getAccountsByFeatures")){
                    XposedBridge.hookMethod(findMethod, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            param.setResult(null);
                        }
                    });
                }else if (findMethod.getName().equals("getSharedAccountsAsUser")){
                    XposedBridge.hookMethod(findMethod, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            int uid = Binder.getCallingUid();
                            if (param.getResult() instanceof Account[]){
                                Account[] accounts = (Account[]) param.getResult();
                                param.setResult(filterAccounts(accounts, uid));
                            }
                        }
                    });
                }
            }
        }catch (Throwable e){
            Log.d(LOG_TAG, "AccountManagerService Class Not Found: " + e.getMessage());
        }
    }

    private Account[] filterAccounts(Account[] original, int uid) {
        List<Account> listAccount = new ArrayList<Account>();
        for (Account account : original)
            if (isAccountAllowed(uid)) {
                listAccount.add(account);
            }else{
                listAccount.add(new Account(randomMail(), "com.google"));
            }
        return listAccount.toArray(new Account[0]);
    }

    public static boolean isAccountAllowed(int uid) {
        try {
            if (checkUID(uid)){
                return false;
            }else {
                return true;
            }
        } catch (Throwable ex) {
            return true;
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

    private class XAccountManagerCallbackAccount implements AccountManagerCallback<Account[]> {
        private AccountManagerCallback<Account[]> mCallback;
        private int mUid;

        public XAccountManagerCallbackAccount(AccountManagerCallback<Account[]> callback, int uid) {
            mCallback = callback;
            mUid = uid;
        }

        @Override
        public void run(AccountManagerFuture<Account[]> future) {
            mCallback.run(new FakeAccount.XFutureAccount(future, mUid));
        }
    }

    private class XFutureAccount implements AccountManagerFuture<Account[]> {
        private AccountManagerFuture<Account[]> mFuture;
        private int mUid;

        public XFutureAccount(AccountManagerFuture<Account[]> future, int uid) {
            mFuture = future;
            mUid = uid;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return mFuture.cancel(mayInterruptIfRunning);
        }

        @Override
        public Account[] getResult() throws OperationCanceledException, IOException, AuthenticatorException {
            Account[] account = mFuture.getResult();
            return FakeAccount.this.filterAccounts(account, mUid);
        }

        @Override
        public Account[] getResult(long timeout, TimeUnit unit) throws OperationCanceledException, IOException,
                AuthenticatorException {
            Account[] account = mFuture.getResult(timeout, unit);
            return FakeAccount.this.filterAccounts(account, mUid);
        }

        @Override
        public boolean isCancelled() {
            return mFuture.isCancelled();
        }

        @Override
        public boolean isDone() {
            return mFuture.isDone();
        }
    }
}
