package com.hl46000.hlfaker.data;

import org.json.JSONObject;

/**
 * Created by LONG-iOS Dev on 8/14/2017.
 */

public class DeviceInfo {

    private String vendor;
    private String model;
    private String versionCode;
    private String androidVersion;
    private String cpuName;
    private String gpuName;
    private String ram;
    private String displayW;
    private String displayH;
    private String dpi;
    private String mainCameraW;
    private String mainCameraH;
    private String frontCameraW;
    private String frontCameraH;
    private String userAgent;

    public DeviceInfo(JSONObject deviceJSON){

    }

    public String getVendor(){
        return this.vendor;
    }

    public String getModel(){
        return this.model;
    }

    public String getVersionCode(){
        return this.versionCode;
    }

    public String getAndroidVersion(){
        return this.androidVersion;
    }

    public String getCpuName(){
        return this.cpuName;
    }

    public String getGpuName(){
        return this.gpuName;
    }

    public String getRam(){
        return this.ram;
    }

    public String getDisplayW(){
        return this.displayW;
    }

    public String getDisplayH(){
        return this.displayH;
    }

    public String getDpi(){
        return this.dpi;
    }

    public String getMainCameraW(){
        return this.mainCameraW;
    }

    public String getMainCameraH(){
        return this.mainCameraH;
    }

    public String getFrontCameraW(){
        return this.frontCameraW;
    }

    public String getFrontCameraH(){
        return this.frontCameraH;
    }
}
