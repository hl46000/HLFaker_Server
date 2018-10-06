package com.hl46000.hlfaker;

import com.hl46000.hlfaker.fakeinfo.FakeAccount;
import com.hl46000.hlfaker.fakeinfo.FakeCPU;
import com.hl46000.hlfaker.fakeinfo.FakeCarrier;
import com.hl46000.hlfaker.fakeinfo.FakeCountry;
import com.hl46000.hlfaker.fakeinfo.FakeDevice;
import com.hl46000.hlfaker.fakeinfo.FakeGPS;
import com.hl46000.hlfaker.fakeinfo.FakeID;
import com.hl46000.hlfaker.fakeinfo.FakeInfo;
import com.hl46000.hlfaker.fakeinfo.FakePackage;
import com.hl46000.hlfaker.fakeinfo.FakeRoot;
import com.hl46000.hlfaker.fakeinfo.NaviteHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by LONG-iOS Dev on 6/29/2017.
 */

public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static final String LOG_TAG = "MainHook";
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        new FakeCarrier(loadPackageParam);
        new FakeID(loadPackageParam);
        new FakeCountry(loadPackageParam);
        new FakeInfo(loadPackageParam);
        new FakePackage(loadPackageParam);
        new FakeGPS(loadPackageParam);
        new FakeCPU(loadPackageParam);
        new FakeRoot(loadPackageParam);
        new FakeDevice(loadPackageParam);
        FakeAccount.fakeGmail(loadPackageParam);
        new NaviteHook(loadPackageParam);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        /*
        try {
            Class<?> at = Class.forName("android.app.ActivityThread");
            XposedBridge.hookAllMethods(at, "systemMain", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    try {
                        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
                        Class<?> am = Class.forName("com.android.server.am.ActivityManagerService", false, loader);
                        XposedBridge.hookAllConstructors(am, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                try {
                                    //FakeIntentFireWall.hookIntentFireWall(loader);
                                    //new FakeAccount(loader);
                                } catch (Throwable ex) {
                                    Log.d(LOG_TAG, "Call to Hook Faker Account ERROR: " + ex.getMessage());
                                }
                            }
                        });
                    } catch (Throwable ex) {
                        Log.d(LOG_TAG, "Call to ActivityManagerService ERROR: " + ex.getMessage());
                    }
                }
            });
        }catch (Throwable ex){
            Log.d(LOG_TAG, "Call to ActivityThread ERROR: " + ex.getMessage());
        }
        */

    }
}
