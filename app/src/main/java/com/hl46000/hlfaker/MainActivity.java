package com.hl46000.hlfaker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.installreferrer.api.InstallReferrerClient;
import com.hl46000.hlfaker.data.HookSharedPref;
import com.hl46000.hlfaker.data.ProxySharedPref;
import com.hl46000.hlfaker.data.SettingsSharedPref;
import com.hl46000.hlfaker.fakeinfo.HookUntils;
import com.hl46000.hlfaker.remote.HLController;
import com.hl46000.hlfaker.remote.HLProxy;
import com.hl46000.hlfaker.remote.HLServer;
import com.hl46000.hlfaker.remote.HLService;
import com.hl46000.hlfaker.remote.HandlerStop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "MainActivity";
    private ListView listAppsView;
    WipeAppsAdapter appsAdapter = null;
    PackagesManage myPkgManage;
    public List<WipeApps> listInstalledApps;
    private Button wipeRandomButton;
    private Button randomButton;
    private CheckBox proxyCheckBox;
    private TextView proxyIPPortTextView;
    private WipeSharedPref _wipeSharedPref;
    private SettingsSharedPref _settingsSharedPref;
    private List<String> listWipeApps;
    private ProgressDialog wipeProgress;
    private EditText adbPortEditText;
    private EditText remotePortEditText;
    private HLServer mySocketServer;
    private HookSharedPref hookPref;
    private HLController myController;
    private RunCommand myRunCommand;
    private HLProxy myHLProxy;
    private Button saveSettingsButton;
    private InstallReferrerClient mReferrerClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thread.setDefaultUncaughtExceptionHandler(new HandlerStop());
        setContentView(R.layout.activity_main);
        myRunCommand = new RunCommand();
        myHLProxy = new HLProxy();
        myPkgManage = new PackagesManage(this.getApplicationContext());
        myController = new HLController(getApplicationContext());
        createDataFolder();
        CopyAssets();
        loadInstalledPackage();
        _settingsSharedPref = new SettingsSharedPref(getApplicationContext());
        adbPortEditText = (EditText)findViewById(R.id.adbWiFiEditText);
        remotePortEditText = (EditText)findViewById(R.id.remoteEditText);
        wipeRandomButton = (Button) findViewById(R.id.wipeRandomButton);
        randomButton = (Button)findViewById(R.id.randomButton);
        proxyCheckBox = (CheckBox)findViewById(R.id.proxyCheckBox);
        proxyIPPortTextView = (TextView)findViewById(R.id.proxyIPPort);
        wipeRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wipeRandom();
            }
        });
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myController.randomID()){
                    Toast.makeText(getApplicationContext(), "RANDOM SUCCESS!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "RANDOM ERROR!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        saveSettingsButton = (Button) findViewById(R.id.saveSettingButton);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableWifiADB();
            }
        });
        proxyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProxy();
            }
        });
        //UpdateProp.firstStart();
        getSettings();
        createHookSharedPrefData();
        enableWifi();
        HookUntils.createNetFolder();
        HookUntils.createNetFile("6C:C4:08:BB:B1:28");
        try {

            Intent serviceIntent = new Intent(this, HLService.class);
            serviceIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            startService(serviceIntent);
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "HLServer ERROR!", Toast.LENGTH_SHORT).show();
        }
        /*
        try {

            Intent serviceIntent = new Intent(this, LayoutService.class);
            serviceIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            startService(serviceIntent);
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "Layout Service ERROR!", Toast.LENGTH_SHORT).show();
        }
        */
        //requestPermission();
        //Toast.makeText(this.getApplicationContext(), "Access: " + myController.getAccess(), Toast.LENGTH_SHORT).show();
        //enableWifiADB();
        //PackagesXMLParser.updateInfoToXML("null", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInstalledPackage();
        enableWifi();
        if (this.mySocketServer == null) {
            try {
                this.mySocketServer = new HLServer(2020, this);
                if (this.mySocketServer.start()) {
                    Toast.makeText(getApplicationContext(), "HLServer STARTED!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "HLServer START ERROR!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "HLServer ERROR!", Toast.LENGTH_SHORT).show();
            }
        } else if (!this.mySocketServer.serverIsRunning()) {
            try {
                if (this.mySocketServer.start()) {
                    Toast.makeText(getApplicationContext(), "HLServer STARTED!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "HLServer START ERROR!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e2) {
                Toast.makeText(getApplicationContext(), "HLServer ERROR!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //myHLProxy.disableProxy();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
    }

    private void createDataFolder(){
        File dataFolder = new File("/sdcard/HLDATA/");
        if(!dataFolder.exists()){
            myRunCommand.runRootCommand("mkdir /sdcard/HLDATA/\n" +
                    "chmod -R 777 /sdcard/HLDATA/\n");
        }
    }

    private void createHookSharedPrefData(){
        try {
            hookPref = new HookSharedPref(getApplicationContext());
            hookPref.setValue("AndHook", "true");
            hookPref.setValue("PhoneNumber", "84934869148");
            hookPref.setValue("CountryISO", "US");
            hookPref.setValue("LanguageCode", "en");
            hookPref.setValue("MCC", "452");
            hookPref.setValue("MNC", "01");
            hookPref.setValue("CarrierName", "Mobifone");
            hookPref.setValue("TimeZone", "Asia/Ho_Chi_Minh");
            hookPref.setValue("IMEI", "506066104722640");
            hookPref.setValue("IMSI", "506066104722640");
            hookPref.setValue("SimSerial", "36066104722647215170");
            hookPref.setValue("AndroidID", "6c0bb208c33b8c40");
            hookPref.setValue("AndroidSerial", "6c0bb208c30b");
            hookPref.setValue("GAID", "f741b85f-fbab-4eb3-8e44-358e07c3bc51");
            hookPref.setValue("GSFID", "1999999999999999999");
            hookPref.setValue("MAC", "6C:C4:08:BB:B1:28");
            hookPref.setValue("Latitude", "27.82516672");
            hookPref.setValue("Longitude", "125.06788613");
            hookPref.setValue("WifiName", "MyWifi");
            hookPref.setValue("BSSID", "6C:C4:08:28:B2:06");
            hookPref.setValue("BypassIP", "192.168.1.17");
            hookPref.setValue("BOARD", Build.BOARD);
            hookPref.setValue("BRAND", Build.BRAND);
            hookPref.setValue("DEVICE", Build.DEVICE);
            hookPref.setValue("DISPLAY", Build.DISPLAY);
            hookPref.setValue("FINGERPRINT", Build.FINGERPRINT);
            hookPref.setValue("HARDWARE", Build.HARDWARE);
            hookPref.setValue("ID", Build.ID);
            hookPref.setValue("MANUFACTURER", Build.MANUFACTURER);
            hookPref.setValue("MODEL", Build.MODEL);
            hookPref.setValue("PRODUCT", Build.PRODUCT);
            hookPref.setValue("BOOTLOADER", Build.BOOTLOADER);
            hookPref.setValue("HOST", Build.HOST);
            hookPref.setValue("TYPE", "user");
            hookPref.setValue("INCREMENTAL", Build.VERSION.INCREMENTAL);
            hookPref.setValue("RELEASE", Build.VERSION.RELEASE);
            hookPref.setValue("CODENAME", Build.VERSION.CODENAME);
            hookPref.setValue("SERIAL", Build.SERIAL);
            hookPref.setValue("BaseBand", Build.getRadioVersion());
            long time = new Date().getTime();
            hookPref.setValue("InstallTime", String.valueOf(time));
            try {
                Class SystemProperties = getClassLoader().loadClass("android.os.SystemProperties");
                Method getKey = SystemProperties.getDeclaredMethod("get", String.class);
                String descript = getKey.invoke(SystemProperties, new String("ro.build.description")).toString();
                hookPref.setValue("DESCRIPTION", descript);
            } catch (Exception e) {
                hookPref.setValue("DESCRIPTION", Build.MODEL + "-user " + Build.VERSION.RELEASE + " " + Build.DISPLAY + " " + Build.VERSION.INCREMENTAL + " release=keys");
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Create HookSharedPref Data ERROR: " + e.getMessage());
        }

        try {
            HookUntils.randomUUID();
            HookUntils.randomMMC();
        }catch (Exception e){
            Log.d(LOG_TAG, "Create ID File ERROR: " + e.getMessage());
        }
    }

    /**
     * Create/Disable Proxy
     */
    private void createProxy(){
        if(!proxyCheckBox.isChecked()){
            myHLProxy.disableProxy();
            return;
        }

        String proxyInfo = proxyIPPortTextView.getText().toString();

        if(!proxyInfo.contains(":") || !proxyInfo.contains(".")){
            myHLProxy.disableProxy();
            proxyCheckBox.setChecked(false);
            return;
        }

        ProxySharedPref proxyPref = new ProxySharedPref(getApplicationContext());
        try{
            String ipPort[] = proxyInfo.split(":");
            Integer.parseInt(ipPort[1]);
            if(!validIP(ipPort[0])){
                myHLProxy.disableProxy();
                proxyCheckBox.setChecked(false);
                Toast.makeText(this.getApplicationContext(), "IP INVALID!", Toast.LENGTH_SHORT).show();
                return;
            }
            proxyPref.setValue("IP", ipPort[0]);
            proxyPref.setValue("Port", ipPort[1]);
            proxyPref.setValue("Enable", "true");
        }catch (Exception e){
            myHLProxy.disableProxy();
            proxyCheckBox.setChecked(false);
            Toast.makeText(this.getApplicationContext(), "IP:PORT INVALID!", Toast.LENGTH_SHORT).show();
            return;
        }


        String host = proxyPref.getValue("IP");
        String port = proxyPref.getValue("Port");
        String enable = proxyPref.getValue("Enable");
        String type = proxyPref.getValue("Type");
        if(host == "" || host == null){
            host = "192.168.1.17";
            port = "1080";
            type = "socks5";
            enable = "true";

            proxyPref.setValue("IP", host);
            proxyPref.setValue("Port", port);

            proxyPref.setValue("Enable", enable);

            proxyIPPortTextView.setText(host + ":" + port);
        }

        if (enable.equals("true")){
            if(type == null || type == ""){
                type = "socks5";
            }
            if(myHLProxy.enableProxy(host, port, type)){
                Toast.makeText(this.getApplicationContext(), "PROXY ENABLE!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this.getApplicationContext(), "PROXY ERROR!", Toast.LENGTH_SHORT).show();
            }
        }else {
            myHLProxy.disableProxy();
            Toast.makeText(this.getApplicationContext(), "PROXY DISABLE!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check IP is Valid
     * @param ip
     * @return
     */
    private boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private void CopyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            if (Build.VERSION.SDK_INT >= 21)
                files = assetManager.list("api-16");
            else
                files = assetManager.list("");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Copy Assets ERROR: " + e.getMessage());
        }
        if (files != null) {
            for (String file : files) {
                InputStream in = null;
                OutputStream out = null;
                try {

                    if (Build.VERSION.SDK_INT >= 21)
                        in = assetManager.open("api-16/" + file);
                    else
                        in = assetManager.open(file);
                    out = new FileOutputStream("/data/data/com.hl46000.hlfaker/" + file);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Copy File Assests ERROR: " + e.getMessage());
                }
            }
        }
        
        myRunCommand.runRootCommand("chmod 700 /data/data/com.hl46000.hlfaker/iptables\n"
                //+ "chmod 700 /data/data/com.hl46000.hlfaker/shrpx\n"
                //+ "chmod 700 /data/data/com.hl46000.hlfaker/proxy.sh\n"
                //+ "chmod 700 /data/data/com.hl46000.hlfaker/cntlm\n"
                //+ "chmod 700 /data/data/com.hl46000.hlfaker/stunnel\n"
                + "chmod 700 /data/data/com.hl46000.hlfaker/redsocks\n");

        myRunCommand.runRootCommand("mount -o rw,remount /system\n"
                + "cp -f /data/data/com.hl46000.hlfaker/cpuinfo /sdcard/HLDATA/cpuinfo\n"
                //+ "cp -f /data/data/com.hl46000.hlfaker/cpuinfo /system/lib/arm/cpuinfo\n"
                + "cp -f /data/data/com.hl46000.hlfaker/version /sdcard/HLDATA/version\n"
                //+ "cp -f /data/data/com.hl46000.hlfaker/build.prop /sdcard/HLDATA/build.prop\n"
                + "cp -f /data/data/com.hl46000.hlfaker/cmdline /sdcard/HLDATA/cmdline\n"
                //+ "cp -f /data/data/com.hl46000.hlfaker/lib/libAK.so /system/lib/libAK.so\n"
                //+ "cp -f /data/data/com.hl46000.hlfaker/lib/libAKCompat.so /system/lib/libAKCompat.so\n"
                //+ "cp -f /data/data/com.hl46000.hlfaker/lib/libhlhooker.so /system/lib/libhlhooker.so\n"
                //+ "cp -f /data/data/com.hl46000.hlfaker/build_momo.prop /system/build.prop\n"
                + "cp -f /data/data/com.hl46000.hlfaker/maps /sdcard/HLDATA/maps\n");

        myRunCommand.runRootCommand(//"mount -o rw,remount /system\n"
                "chmod 755 /sdcard/HLDATA/cpuinfo\n"
                //+ "chmod 775 /system/lib/arm/cpuinfo\n"
                + "chmod 775 /sdcard/HLDATA/version\n"
                + "chmod 775 /sdcard/HLDATA/build.prop\n"
                + "chmod 775 /sdcard/HLDATA/cmdline\n"
                + "chmod 775 /sdcard/HLDATA/maps\n");
                //+ "chmod 644 /system/build.prop\n"
                //+ "chmod 777 /system/lib/libAK.so\n"
                //+ "chmod 777 /system/lib/libAKCompat.so\n"
                //+ "chmod 777 /system/lib/libhlhooker.so\n");

        copyBusyBox();
    }

    private void copyBusyBox(){
        String xbinFolder = "";
        if((new File("/su/xbin/").exists())){
            xbinFolder = "/su/xbin/";
        }else {
            xbinFolder = "/system/xbin/";
        }

        String busyBoxPath = xbinFolder + "bbox";
        if((new File(busyBoxPath).exists())){
            return;
        }

        myRunCommand.runRootCommand("mount -o rw,remount,rw /system\n"
                + "cp -f /data/data/com.hl46000.hlfaker/bbox " + busyBoxPath + "\n"
                + "chmod 777 " + busyBoxPath + "\n");

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void getSettings(){
        String adbPort = _settingsSharedPref.getValue("AdbPort");
        String remotePort = _settingsSharedPref.getValue("RemotePort");
        if(adbPort == "" || adbPort == null){
            adbPort = "5555";
            _settingsSharedPref.setValue("AdbPort", adbPort);
        }

        if(remotePort == "" || remotePort == null){
            remotePort = "1709";
            _settingsSharedPref.setValue("RemotePort", remotePort);
        }

        adbPortEditText.setText(adbPort);
        remotePortEditText.setText(remotePort);
    }

    private void enableWifi(){
        try {
            WifiManager _wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            _wifi.setWifiEnabled(true);
        } catch (Exception e) {
            myRunCommand.runRootCommand("svc wifi enable");
            Log.d(LOG_TAG, "Enable Wifi ERROR: " + e.getMessage());
        }
    }

    private void enableWifiADB(){
        new Thread(){
            @Override
            public synchronized void start() {
                String _port =_settingsSharedPref.getValue("AdbPort");
                if(_port == "" || _port == null || _port == "ADB Port" || _port.isEmpty()){
                    _port = "5555";
                }
                try {
                    WifiInfo wifiInfo = ((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE)).getConnectionInfo();
                    int ipAddr = wifiInfo.getIpAddress();
                    String wifiIP = InetAddress.getByAddress(BigInteger.valueOf(ipAddr).toByteArray()).getHostAddress();
                    String[] ipArr = wifiIP.split("\\.");

                    if(ipArr.length == 4){
                        wifiIP = ipArr[3] + "." + ipArr[2] + "." + ipArr[1] + "." + ipArr[0];
                    }

                    myRunCommand.runRootCommand("stop adbd\n"
                            + "setprop service.adb.tcp.port " + _port + "\n"
                            + "start adbd\n");

                    Toast.makeText(getApplicationContext(), "WifiADB ENABLE!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Enable WifiADB ERROR: " + e.getMessage());
                }
            }
        }.start();
    }

    /**
     * Wipe Package, Data and Random ID
     */
    private void wipeRandom(){
        if(_wipeSharedPref == null){
            _wipeSharedPref = new WipeSharedPref(this.getApplicationContext());
        }
        listWipeApps = new ArrayList<String>();
        listWipeApps.addAll(_wipeSharedPref.getValue(Common.WIPE_PREF_KEY));
        if(listWipeApps.isEmpty()){
            return;
        }
        wipeProgress = new ProgressDialog(this);
        wipeProgress.setTitle("WIPE APP DATA.");
        wipeProgress.setMessage("Running...");
        wipeProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //wipeProgress.setIndeterminate(true);
        wipeProgress.setCancelable(false);
        wipeProgress.setMax(100);
        //wipeProgress.setProgress(0);
        wipeProgress.show();
        final Handler handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                wipeProgress.setMessage(msg.getData().getString("pkg"));
                wipeProgress.incrementProgressBy(msg.getData().getInt("set"));
                if(msg.getData().getInt("set") == 100){
                    myController.deleteFile("/data/misc/keystore/user_0/*");
                    myController.deleteFile("/data/misc/keychain/metadata/*");
                    myController.deleteSD();
                    if(myController.randomID()){
                        Toast.makeText(getApplicationContext(), "RANDOM SUCCESS!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "RANDOM ERROR!", Toast.LENGTH_SHORT).show();
                    }
                    wipeProgress.dismiss();
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int totalJump = listWipeApps.toArray().length;
                    int jump = 0;
                    for (String pkg:listWipeApps) {
                        try{
                            myController.wipeApp(pkg);
                            jump += 1;
                            float set = (float) jump/(float) totalJump;
                            Bundle bnd = new Bundle();
                            bnd.putInt("set", (int) (set*100));
                            bnd.putString("pkg", pkg);
                            Message msg = new Message();
                            msg.setData(bnd);
                            handle.sendMessage(msg);
                        }catch (Exception e){
                            Log.d("MainActivity", "Send Command ERROR: " + e.getMessage());
                            continue;
                        }
                    }

                } catch (Exception e) {
                    Log.d("MainActivity", "Create RunCommand ERROR: " + e.getMessage());
                }
            }
        }).start();


    }

    private void loadInstalledPackage(){
        listInstalledApps = new ArrayList<WipeApps>();
        listInstalledApps.addAll(myPkgManage.getInstalledApplications(AppsType.ALL_APPS));
        appsAdapter = new WipeAppsAdapter(this, R.layout.wipe_app_info, listInstalledApps);
        listAppsView = (ListView) findViewById(R.id.listAppsListView);
        listAppsView.setAdapter(appsAdapter);

        /*
        listAppsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WipeApps app = (WipeApps) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Package: " + app.getPackage(), Toast.LENGTH_LONG).show();
                Log.d("MainActivity", "Package: " + app.getPackage());
            }
        });
        */
    }

}
