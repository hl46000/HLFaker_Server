package com.hl46000.hlfaker;


import android.graphics.drawable.Drawable;

/**
 * Created by LONG-iOS Dev on 6/18/2017.
 */

public class WipeApps{
    private String appName;
    private String packageName;
    private Drawable appIcon;
    private boolean isWipe;

    public WipeApps(String name, String pkgName, Drawable appIco, boolean wipe){
        this.appName = name;
        this.packageName = pkgName;
        this.appIcon = appIco;
        this.isWipe = wipe;
    }


    public Drawable getAppIcon() { return this.appIcon; }

    public String getName(){
        return this.appName;
    }

    public void setName(String name){
        this.appName = name;
    }

    public String getPackage(){
        return this.packageName;
    }

    public void setPackage(String pkgName){
        this.packageName = pkgName;
    }

    public boolean getIsWipe(){
        return this.isWipe;
    }

    public void setIsWipe(boolean wipe){
        this.isWipe = wipe;
    }
}

