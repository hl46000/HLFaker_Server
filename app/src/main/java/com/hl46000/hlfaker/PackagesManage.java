package com.hl46000.hlfaker;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LONG-iOS Dev on 7/31/2017.
 */

enum PackagesType {
    SYSTEM_PACKAGES,
    ACTIVITIES_PACKAGES,
    ALL_PACKAGES
}

enum AppsType {
    SYSTEM_APPS,
    USER_APP,
    ALL_APPS
}

public class PackagesManage {

    private Context myContext;
    private static final String TAG = "PackagesManage";
    private List<String> listWipedApps;
    /**
     *Public contructor with application context
     */
    public PackagesManage (Context mContext){
        myContext = mContext;
        listWipedApps = new ArrayList<String>();
        loadListWipeApps();
    }

    /**
     * Get Installed Packages with Package Type
     * @param pkgType
     * @return List Package
     */
    public List<WipeApps> getInstalledPackages (PackagesType pkgType){
        PackageManager pkgManager = myContext.getApplicationContext().getPackageManager();
        List<PackageInfo> packages;
        if(pkgType == PackagesType.SYSTEM_PACKAGES){
            List<PackageInfo> allPackages;
            packages = new ArrayList<PackageInfo>();
            allPackages = pkgManager.getInstalledPackages(0);
            for (PackageInfo pkgInfo:allPackages
                 ) {
                if (pkgInfo.applicationInfo.flags == ApplicationInfo.FLAG_SYSTEM){
                    packages.add(pkgInfo);
                }
            }

        }else if(pkgType == PackagesType.ACTIVITIES_PACKAGES){
            packages = pkgManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        }else{
            packages = pkgManager.getInstalledPackages(0);
        }

        List<WipeApps> listWipeApps = new ArrayList<WipeApps>();

        for (PackageInfo pkgInfo:packages
             ) {
            try {
                String pkg = pkgInfo.packageName;
                if(pkg.contains("hlfaker") || pkg.contains("xposed") || pkg.contains("supersu")){
                    continue;
                }
                String name = pkgInfo.applicationInfo.loadLabel(pkgManager).toString();
                Drawable appIco = pkgInfo.applicationInfo.loadIcon(pkgManager);
                if (name == null || name == ""){
                    name = pkg;
                }
                boolean isWipe = false;
                if (!listWipedApps.isEmpty()){
                    if (listWipedApps.contains(pkg)){
                        isWipe = true;
                    }
                }
                WipeApps wipeApp = new WipeApps(name, pkg, appIco, isWipe);
                listWipeApps.add(wipeApp);
            }catch (Exception e) {
                Log.d(TAG, "Add Wipe Pkg ERROR");
                continue;
            }

        }
        return listWipeApps;
    }

    public List getInstalledApplications (AppsType appType){
        int allFlags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;
        PackageManager pkgManager = myContext.getApplicationContext().getPackageManager();
        List<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();
        if(appType == AppsType.SYSTEM_APPS){
            //apps = new ArrayList<ApplicationInfo>();
            List<ApplicationInfo> allApps = pkgManager.getInstalledApplications(allFlags);
            for (ApplicationInfo app:allApps
                    ) {
                if (app.flags == ApplicationInfo.FLAG_SYSTEM){
                    apps.add(app);
                }
            }
        }else if(appType == AppsType.USER_APP){
            //apps = new ArrayList<ApplicationInfo>();
            List<ApplicationInfo> allApps = pkgManager.getInstalledApplications(allFlags);
            for (ApplicationInfo app:allApps
                 ) {
                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 1){
                    apps.add(app);
                }
            }
        }else {
            apps.addAll(pkgManager.getInstalledApplications(allFlags));
        }

        List<WipeApps> listWipeApps = new ArrayList<WipeApps>();

        for (ApplicationInfo app:apps
             ) {
            try{
                String pkg = app.packageName;
                if(pkg.contains("hlfaker") || pkg.contains("xposed") || pkg.contains("supersu") || pkg.contains("kinguser")){
                    continue;
                }
                String name = app.loadLabel(pkgManager).toString();
                Drawable appIco = app.loadIcon(pkgManager);
                if (name == null || name == ""){
                    name = pkg;
                }
                boolean isWipe = false;
                if (!listWipedApps.isEmpty()){
                    if (listWipedApps.contains(pkg)){
                        isWipe = true;
                    }
                }
                WipeApps wipeApp = new WipeApps(name, pkg, appIco, isWipe);
                listWipeApps.add(wipeApp);
            }catch (Exception e){
                Log.d(TAG, "Add Wipe App ERROR");
                continue;
            }
        }

        return  listWipeApps;
    }

    public void loadListWipeApps(){
        WipeSharedPref wsp = new WipeSharedPref(myContext);
        listWipedApps.addAll(wsp.getValue(Common.WIPE_PREF_KEY));
        //Log.d("PackagesManage", "List Wipe Apps Size: " + listWipeApps.size());
    }

    public boolean clearPackages(String pkgName){
        return false;
    }

    public boolean clearPackages(List listPackagesName){
        return false;
    }


}
