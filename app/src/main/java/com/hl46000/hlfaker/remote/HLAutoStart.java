package com.hl46000.hlfaker.remote;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.hl46000.hlfaker.RunCommand;
import com.hl46000.hlfaker.data.SettingsSharedPref;
import com.hl46000.hlfaker.fakeinfo.UpdateProp;

import java.math.BigInteger;
import java.net.InetAddress;

/**
 * Created by LONG-iOS Dev on 9/21/2017.
 */

public class HLAutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(serviceStarted(context) && !intent.getAction().contains("com.hl46000")){
            return;
        }
        //Thread.setDefaultUncaughtExceptionHandler(new HandlerStop());

        //copyFiles();
        //UpdateProp.firstStart();
        enableWifi(context);
        //enableWifiADB(context);

        try{
            Intent remoteIntent = new Intent(context, HLService.class);
            remoteIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.startService(remoteIntent);
        }catch (Exception e){
            Log.d("HLAutoStart", e.getMessage());
        }
    }

    private boolean serviceStarted(Context appContext){
        ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(HLService.class.getName().equals(serviceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    private void copyFiles(){
        RunCommand myRunCommand = new RunCommand();

        myRunCommand.runRootCommand("cp -f /data/data/com.hl46000.hlfaker/cpuinfo /storage/cpuinfo\n"
                + "cp -f /data/data/com.hl46000.hlfaker/version /storage/version\n"
                + "cp -f /data/data/com.hl46000.hlfaker/build.prop /storage/build.prop\n"
                + "cp -f /data/data/com.hl46000.hlfaker/cmdline /storage/cmdline\n"
                + "cp -f /data/data/com.hl46000.hlfaker/maps /storage/maps\n");

        myRunCommand.runRootCommand("chmod 755 /storage/cpuinfo\n"
                + "chmod 755 /storage/version\n"
                + "chmod 755 /storage/build.prop\n"
                + "chmod 755 /storage/cmdline\n"
                + "chmod 755 /storage/maps\n");
    }

    private void enableWifi(Context context){
        try {
            WifiManager _wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            _wifi.setWifiEnabled(true);
        } catch (Exception e) {
            new RunCommand().runRootCommand("svc wifi enable\n");
            Log.d("HLAutoStart", "Enable Wifi ERROR: " + e.getMessage());
        }
        RunCommand myRunCommand = new RunCommand();
        myRunCommand.runRootCommand("stop adbd\n"
                + "setprop service.adb.tcp.port 5555\n"
                + "start adbd\n");
    }

    private void enableWifiADB(Context context){
        final Context appContext = context;
        new Thread(){
            @Override
            public synchronized void start() {
                SettingsSharedPref _settingsSharedPref = new SettingsSharedPref(appContext);
                String _port =_settingsSharedPref.getValue("AdbPort");
                if(_port == "" || _port == null || _port == "ADB Port" || _port.isEmpty()){
                    _port = "5555";
                }
                try {
                    WifiInfo wifiInfo = ((WifiManager) appContext.getSystemService(appContext.WIFI_SERVICE)).getConnectionInfo();
                    int ipAddr = wifiInfo.getIpAddress();
                    String wifiIP = InetAddress.getByAddress(BigInteger.valueOf(ipAddr).toByteArray()).getHostAddress();
                    String[] ipArr = wifiIP.split("\\.");

                    if(ipArr.length == 4){
                        wifiIP = ipArr[3] + "." + ipArr[2] + "." + ipArr[1] + "." + ipArr[0];
                    }
                    new RunCommand().runRootCommand("stop adbd\n"
                            + "setprop service.adb.tcp.port " + _port + "\n"
                            + "start adbd\n");
                    Toast.makeText(appContext, "WifiADB ENABLE!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("HLAutoStart", "Enable WifiADB ERROR: " + e.getMessage());
                }
            }
        }.start();
    }
}
