package com.hl46000.hlfaker.data;

/**
 * Created by LONG-iOS Dev on 8/11/2017.
 */

public class GetDeviceInfo {

    private String API_URL;

    /**
     * Contructor Object
     * Use Public API
     */
    public GetDeviceInfo(){
        API_URL = "http://devices.zeroetc.com/android/DevicesInfo/getdevice/public/cHVibGlj/";
    }

    /**
     * Contructor Object
     * Use Private API
     * @param privateAPI
     */
    public GetDeviceInfo(String privateAPI){
        API_URL = privateAPI;
    }

    public void getAndSaveDevice(){

    }

    public void getDevice(){

    }

    public void getListModel(){

    }

    public void getListVendor(){

    }


}
