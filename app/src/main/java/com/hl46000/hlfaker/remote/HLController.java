package com.hl46000.hlfaker.remote;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hl46000.hlfaker.Common;
import com.hl46000.hlfaker.RunCommand;
import com.hl46000.hlfaker.data.HookSharedPref;
import com.hl46000.hlfaker.data.ProxySharedPref;
import com.hl46000.hlfaker.fakeinfo.HookUntils;
import com.hl46000.hlfaker.fakeinfo.UpdateProp;
import com.hl46000.hlfaker.security.algorithm.md5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


/**
 * Created by LONG-iOS Dev on 9/27/2017.
 */

public class HLController {
    private final String LOG_TAG = "HLController";
    private final String WIPE_APP = "pm clear package\n";
    private final String CLEAR_SYS_SETTINGS = "sqlite3 /data/data/com.android.providers.settings/databases/settings.db \"DELETE FROM system WHERE _id > 36;\"\n";
    private final String CLEAR_ALL_SYS_SETTINGS = "sqlite3 \"/data/data/com.android.providers.settings/databases/settings.db \\\"DELETE FROM system WHERE _id IN (SELECT _id FROM system LIMIT 0 , 100);\\\"\"\n";
    private final String OPEN_APP = "monkey -p package -c android.intent.category.LAUNCHER 1\n";
    private final String STOP_APP = "am force-stop package\n";
    private final String KILL_APP = "am kill all package\n";
    private final String LAYOUT_FILE_PATH = "/sdcard/layout.xml";
    private final String INPUT_TAP = "input touchscreen tap x y\n";
    private final String INPUT_SWIPE = "input swipe x1 y1 x2 y2\n";
    private final String INPUT_TEXT = "input text \"txt\"\n";
    private final String INPUT_KEYEVENT = "input keyevent key_code\n";
    private final String DELETE_FILE = "rm -rf file\n";
    private final String UNINSTALL_APP = "pm uninstall package\n";
    private final String ENABLE_APP = "pm action package\n";
    private final String CHMOD_FILE = "chmod permission file\n";
    private final String CHATTR_FILE = "chattr permission file\n";
    private final String INSTALL_APP = "pm install -r package\n";
    private final String INSTALL_REFERRER = "am broadcast -a com.android.vending.INSTALL_REFERRER -f 32 --es referrer \"my_referrer\" --el referrer_timestamp_seconds referrer_ts --exclude-stopped-packages\n";
    //private final String APK_FOLDER = "/sdcard/HLDATA/APK/";
    //private final String OBB_FOLDER = "/sdcard/HLDATA/OBB/";
    private Context appContext;
    private HLCursor myCursor;
    private ProxySharedPref proxyPref;
    //private ProxyDroidService mProxyDroid;
    private HLProxy myHLProxy;
    private ParserXML layoutParser;
    private HookSharedPref hookPref;
    //private HLRRSManager myRRSManager;
    private RunCommand myRunCommand;
    private String customReferer;
    private String userAgent;
    private String fakeIP;
    private String originIMEI;
    private String referrerPkg;
    private long referrer_ts;
    static {
        try {
            System.loadLibrary("scrtData");
        } catch (final UnsatisfiedLinkError e) {
            Log.d("HLController", "Failed to load scrtData!");
        }
    }

    public HLController(Context sharedContext){
        appContext = sharedContext;
        myCursor = new HLCursor(appContext);
        proxyPref = new ProxySharedPref(appContext);
        layoutParser = new ParserXML();
        hookPref = new HookSharedPref(appContext);
        //myRRSManager = new HLRRSManager(appContext);
        myRunCommand = new RunCommand();
        myHLProxy = new HLProxy();
        customReferer = "https://www.google.com";
        userAgent = "";
        fakeIP = "";
        referrerPkg = "";
    }

    /**
     * Get Device Infomation
     * @return
     */
    public String getDeviceInfo(){
        try {
            String serial = md5.encodeMD5(getSerial().getBytes()); //getSerial();
            String androidVersion = Build.VERSION.RELEASE;
            String hlfakerVersion = Common.HLFAKER_VERSION;
            return serial + ":" + androidVersion + ":" + hlfakerVersion + ":" + myCursor.windowsX + ":" + myCursor.windowsY;
        }catch (Exception e){
            Log.d(LOG_TAG, "Get Device Info ERROR: " + e.getMessage());
            return null;
        }
    }

    public static native String getSerial();
    public static native String getSPubKey();
    public static native String getCPriKey();
    /**
     * Enable/Disable Mouse Cursor
     * @param enable
     */
    public void enableCursor(boolean enable){
        if(myCursor == null){
            Log.d(LOG_TAG, "Cursor Object NULL!");
            return;
        }
        if(enable){
            if(!myCursor.enableCursor(enable)){
                Log.d(LOG_TAG, "Enable Cursor ERROR!");
            }
        }else {
            if (!myCursor.enableCursor(enable)){
                Log.d(LOG_TAG, "Disable Cursor ERROR!");
            }
        }
    }

    /**
     * Update Cursor Coordinates
     * @param x
     * @param y
     */
    public void moveCursor(int x, int y){
        if(myCursor == null){
            Log.d(LOG_TAG, "Cursor Object NULL!");
            return;
        }
        myCursor.updateCoordinates(x, y);
    }

    /**
     * Enable/Disable ProxyDroid
     * @param enable
     * @return
     */
    public boolean enableProxy(boolean enable){
        if(enable){
            String host = proxyPref.getValue("IP");
            String port = proxyPref.getValue("Port");
            String type = proxyPref.getValue("Type");
            if(host == "" || host == null){
                host = "192.168.1.17";
                port = "1994";
                proxyPref.setValue("IP", host);
                proxyPref.setValue("Port", port);
            }
            if(myHLProxy == null){
                //mProxyDroid = new ProxyDroidService(appContext, host, port, "socks5");
                myHLProxy = new HLProxy();
            }
            if(myHLProxy.enableProxy(host, port, type)){
                proxyPref.setValue("Enable", "true");
                return true;
            }else {
                proxyPref.setValue("Enable", "false");
                return false;
            }
        }else {
            proxyPref.setValue("Enable", "false");
            //ProxyDroidService.disHandleCommand();
            myHLProxy.disableProxy();
            return true;
        }
    }

    /**
     * Set Proxy Info into ProxySharedPref
     * @param ip
     * @param port
     * @return
     */
    public boolean setProxy(String ip, String port, String type){
        try{
            enableProxy(false);
            proxyPref.setValue("IP", ip);
            proxyPref.setValue("Port", port);
            proxyPref.setValue("Type", type);
            //proxyPref.setValue("Enable", "false");
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Save Proxy Info ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get Proxy Info
     * @return
     */
    public String getProxy(){
        String host = proxyPref.getValue("IP");
        String port = proxyPref.getValue("Port");
        String enable = proxyPref.getValue("Enable");
        if(host == null || host == "" || port == null || port == ""){
            return null;
        }else {
            return host + ":" + port + ":" + enable;
        }
    }

    /**
     * Get List Installed Apps by User
     * @return
     */
    public String getApps(){
        try {
            int flags = PackageManager.GET_META_DATA |
                        PackageManager.GET_SHARED_LIBRARY_FILES;

            PackageManager pkgManager = appContext.getPackageManager();
            List<ApplicationInfo> listApps = pkgManager.getInstalledApplications(flags);
            String result = "";
            for (ApplicationInfo appInfo : listApps){
                if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1){
                    continue;
                }else if(appInfo.packageName.contains("hlfaker") || appInfo.packageName.contains("xposed") || appInfo.packageName.contains("supersu") || appInfo.packageName.contains("kinguser"))  {
                    continue;
                }else {
                    result += (appInfo.loadLabel(pkgManager).toString() + ":" + appInfo.packageName + "|");
                }
            }
            if(result == ""){
                return null;
            }
            return result;
        }catch (Exception e){
            Log.d(LOG_TAG, "Get Applications ERROR: " + e.getMessage());
            return null;
        }
    }

    /**
     * Clear App Data
     * @param packageName
     * @return
     */
    public boolean wipeApp(String packageName){
        try{
            if(packageName.contains("hlfaker") || packageName.contains("xposed") || packageName.contains("supersu")){
                return false;
            }
            String stopCmd = STOP_APP.replace("package", packageName);
            //String killCmd = KILL_APP.replace("package", packageName);
            String wipeCmd = WIPE_APP.replace("package", packageName);
            myRunCommand.runRootCommand(stopCmd);
            //myRunCommand.runRootCommand(killCmd);
            myRunCommand.runRootCommand(wipeCmd);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Wipe App ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Use with Android <= 5.1
     * @return
     */
    public boolean wipeSystemData(){
        try{
            myRunCommand.runRootCommand(CLEAR_SYS_SETTINGS);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Wipe App ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Open Application use Package Name
     * @param packageName
     * @return
     */
    public boolean openApp(String packageName){
        try{
            String cmd = OPEN_APP.replace("package", packageName);
            myRunCommand.runRootCommand(cmd);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Open App ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close Application use Package Name
     * @param packageName
     * @return
     */
    public boolean closeApp(String packageName){
        try{
            String cmd = STOP_APP.replace("package", packageName);
            myRunCommand.runRootCommand(cmd);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Close App ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check Exist App with Package Name
     * @param packageName
     * @return
     */
    public boolean existApp(String packageName){
        try {
            boolean isExist = false;
            int flags = PackageManager.GET_META_DATA |
                    PackageManager.GET_SHARED_LIBRARY_FILES;

            PackageManager pkgManager = appContext.getPackageManager();
            List<ApplicationInfo> listApps = pkgManager.getInstalledApplications(flags);

            for (ApplicationInfo app : listApps){
                if(app.packageName.equals(packageName)){
                    isExist = true;
                    break;
                }
            }

            return isExist;
        }catch (Exception e){
            Log.d(LOG_TAG, "Check Exist App ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Uninstall Package App
     * @param packageName
     * @return
     */
    public boolean uninstallApp(String packageName){
        try {
            String uninstallCmd = UNINSTALL_APP.replace("package", packageName);
            myRunCommand.runRootCommand(uninstallCmd);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Uninstall App Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Iinstall Package App
     * @param packagePath
     * @return
     */
    public boolean installApp(String packagePath){
        try {
            File apkFile = new File(packagePath);
            if(!apkFile.exists()){
                return false;
            }
            String installCmd = INSTALL_APP.replace("package", packagePath);
            myRunCommand.runRootCommand(installCmd);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Install App Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check Exist Package contrains Activity
     * @param packageName
     * @return
     */
    private boolean existActivityPackage(String packageName){
        PackageManager pkgManager = appContext.getPackageManager();
        try {
            pkgManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Open Link with Origin Browser
     * @param link
     * @return
     */
    public boolean openLink(String link){
        Intent browserIntent = new Intent(Intent.ACTION_MAIN);
        String browserPkg = "com.hl46000.hlbrowser";
        String browserActivity = "com.hl46000.hlbrowser.MainActivity";
        if(existActivityPackage(browserPkg)){
            browserIntent.putExtra("Link", link);
            browserIntent.putExtra("Referer", customReferer);
            browserIntent.putExtra("UserAgent", userAgent);
            browserIntent.putExtra("FakeIP", fakeIP);
        }else if(existActivityPackage("com.android.browser")){
            browserPkg = "com.android.browser";
            browserActivity = "com.android.browser.BrowserActivity";
        }else if(existActivityPackage("com.sec.android.app.sbrowser")){
            browserPkg = "com.sec.android.app.sbrowser";
            browserActivity = "com.sec.android.app.sbrowser.SBrowserMainActivity";
        }else if (existActivityPackage("com.android.chrome")){
            browserPkg = "com.android.chrome";
            browserActivity = "com.google.android.apps.chrome.Main";
        }
        browserIntent.setComponent(new ComponentName(browserPkg, browserActivity));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        try{
            browserIntent.setData(Uri.parse(link));
            appContext.startActivity(browserIntent);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Open Link ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get Front App Windows
     * @return
     */
    public String getFrontApp(){
        if(!dumpLayout()){
            return null;
        }
        if(!layoutParser.parserLayout()){
            return null;
        }
        if(layoutParser.listViewItem.isEmpty()){
            return null;
        }
        String pkgName = "";
        for (ParserXML.ViewItem item : layoutParser.listViewItem){
            if(item.getPackagename() != ""){
                pkgName = item.getPackagename();
                break;
            }
        }
        if (pkgName == ""){
            return null;
        }else {
            return pkgName;
        }
    }

    public String getFrontApp2(){
        try{
            String mPackageName = "";
            ActivityManager mActivityManager =(ActivityManager)appContext.getSystemService(Context.ACTIVITY_SERVICE);

            if(Build.VERSION.SDK_INT > 20){
                mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
            }
            else{
                //mPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
                mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
            }

            return mPackageName;
        }catch (Exception e){
            return "";
        }
    }

    /**
     * Get Windows Layout Info
     * @return
     */
    public boolean getLayout(){
        if(!dumpLayout()){
            return false;
        }
        if(!layoutParser.parserLayout()){
            return false;
        }
        if(layoutParser.listViewItem.isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * Find text in Viewlayout
     * @param text
     * @param isContains
     * @return
     */
    public boolean findText(String text, boolean isContains){
        if(layoutParser.listViewItem.isEmpty()){
            return false;
        }

        try{
            ParserXML.ViewItem foundItem = null;
            for (ParserXML.ViewItem item : layoutParser.listViewItem){
                if(isContains){
                    if(item.getText().contains(text) || item.getContentDesception().contains(text)){
                        foundItem = item;
                        break;
                    }
                }else {
                    if(item.getText().equals(text) || item.getContentDesception().equals(text)){
                        foundItem = item;
                        break;
                    }
                }
            }
            if(foundItem != null){
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Find Text ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find text in Viewlayout and Click it if Found
     * @param text
     * @param isContains
     * @return
     */
    public boolean findTextAndClick(String text, boolean isContains){
        if(layoutParser.listViewItem.isEmpty()){
            return false;
        }

        try{
            ParserXML.ViewItem foundItem = null;
            for (ParserXML.ViewItem item : layoutParser.listViewItem){
                if(isContains){
                    if(item.getText().contains(text) || item.getContentDesception().contains(text)){
                        foundItem = item;
                        break;
                    }
                }else {
                    if(item.getText().equals(text) || item.getContentDesception().equals(text)){
                        foundItem = item;
                        break;
                    }
                }
            }
            if(foundItem != null){
                touch(foundItem.getCenterCoordinates()[0], foundItem.getCenterCoordinates()[1]);
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Find Text And Click ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Type a text
     * @param text
     * @return
     */
    public boolean inputText(String text){
        try{
            text = text.replace(" ", "%s");
            String cmd = INPUT_TEXT.replace("txt", text);
            myRunCommand.runRootCommand(cmd);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Input Text ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Send a Keyevent
     * @param keyEvent
     * @return
     */
    public boolean inputKeyEvent(String keyEvent){
        try{
            String cmd = INPUT_KEYEVENT.replace("key_code", keyEvent);
            myRunCommand.runRootCommand(cmd);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Input Keyevent ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Send Touch Event
     * @param x
     * @param y
     */
    public void touch(int x, int y){
        try {
            String cmd = INPUT_TAP.replace("x", Integer.toString(x));
            cmd = cmd.replace("y", Integer.toString(y));
            myRunCommand.runRootCommand(cmd);
        }catch (Exception e){
            Log.d(LOG_TAG, "Input Touch ERROR: " + e.getMessage());
        }
    }

    /**
     * Send Swipe Event
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void swipe(int x1, int y1, int x2, int y2){
        try {
            String cmd = INPUT_SWIPE.replace("x1", Integer.toString(x1));
            cmd = cmd.replace("y1", Integer.toString(y1));
            cmd = cmd.replace("x2", Integer.toString(x2));
            cmd = cmd.replace("y2", Integer.toString(y2));
            myRunCommand.runRootCommand(cmd);
        }catch (Exception e){
            Log.d(LOG_TAG, "Input Swipe ERROR: " + e.getMessage());
        }
    }

    /**
     * Delete File/Folder
     * @param filePath
     * @return
     */
    public boolean deleteFile(String filePath){
        try {
            String cmd = DELETE_FILE.replace("file", filePath);
            myRunCommand.runRootCommand(cmd);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Input Swipe ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Random ID: IMEI, IMSI,...
     * @return
     */
    public boolean randomID(){
        String MCC = hookPref.getValue("MCC");
        String MNC = hookPref.getValue("MNC");

        if(originIMEI == null || originIMEI.isEmpty()){
            try {
                TelephonyManager telephony = (TelephonyManager)appContext.getSystemService(Context.TELEPHONY_SERVICE);
                originIMEI = telephony.getDeviceId();
            }catch (Exception e){
                Log.d(LOG_TAG, "Get Origin IMEI ERROR: " + e.getMessage());
                originIMEI = "49015420323751";
            }
        }

        String IMEI = HookUntils.randomIMEI(originIMEI);
        String IMSI = HookUntils.randomIMSI(MCC+MNC);
        String SimSerial = HookUntils.randomSimSerial();
        String AndroidID = HookUntils.randomAndroidID();
        String AndroidSerial = HookUntils.randomAndroidSerial();
        //String GAID = HookUntils.randomGAID();
        String MAC = HookUntils.randomMACAddress();
        String BSSID = HookUntils.randomMACAddress();
        String GSFID = HookUntils.randomGSFID();
        String phoneNumber = HookUntils.randomPhoneNumber();
        HookUntils.createNetFile(MAC);
        try {
            HookUntils.randomUUID();
            HookUntils.randomMMC();
        }catch (Exception e){
            Log.d(LOG_TAG, "Random File ID ERROR: " + e.getMessage());
        }
        try {
            hookPref.setValue("IMEI", IMEI);
            hookPref.setValue("IMSI", IMSI);
            hookPref.setValue("SimSerial", SimSerial);
            hookPref.setValue("AndroidID", AndroidID);
            hookPref.setValue("AndroidSerial", AndroidSerial);
            hookPref.setValue("GAID", getPrvGAID());
            hookPref.setValue("MAC", MAC);
            hookPref.setValue("BSSID", BSSID);
            hookPref.setValue("GSFID", GSFID);
            hookPref.setValue("PhoneNumber", phoneNumber);
            UpdateProp.updateID(AndroidSerial, hookPref.getValue("BaseBand"));

            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Random Device ID ERROR: " + e.getMessage());
            return false;
        }
    }

    public boolean changeDevice(String board, String brand, String device, String display, String hardware, String id, String manufac,
                                     String model, String product, String bootloader, String host, String incremen, String release, String baseband, String imei){
        try {
            originIMEI = imei;
            hookPref.setValue("BOARD", board);
            hookPref.setValue("BRAND", brand);
            hookPref.setValue("DEVICE", device);
            hookPref.setValue("DISPLAY", display);
            hookPref.setValue("FINGERPRINT", manufac + "/" + product + "/" + id + ":" + release + "/" + display + "/" + incremen + ":user/release-keys");
            hookPref.setValue("HARDWARE", hardware);
            hookPref.setValue("ID", id);
            hookPref.setValue("MANUFACTURER", manufac);
            hookPref.setValue("MODEL", model);
            hookPref.setValue("PRODUCT", product);
            hookPref.setValue("BOOTLOADER", bootloader);
            hookPref.setValue("HOST", host);
            hookPref.setValue("TYPE", "user");
            hookPref.setValue("INCREMENTAL", incremen);
            hookPref.setValue("RELEASE", release);
            hookPref.setValue("BaseBand", baseband);
            hookPref.setValue("DESCRIPTION", product + "-user " + release + " " + id + " " + incremen + " release=keys");
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Change Device Model ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Change TimeZone
     * @param timeZone
     * @return
     */
    public boolean changeTimeZone(String timeZone){
        try {
            if(timeZone == ""){
                return false;
            }else {
                hookPref.setValue("TimeZone", timeZone);
                ((AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE)).setTimeZone(timeZone);
                return true;
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Change TimeZone ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Change Carrier Info
     * @param carrierName
     * @param MCC
     * @param MNC
     * @return
     */
    public boolean changeCarrier(String carrierName, String MCC, String MNC){
        try {
            if(carrierName == "" || MCC == "" || MNC == ""){
                return false;
            }else {
                hookPref.setValue("CarrierName", carrierName);
                hookPref.setValue("MCC", MCC);
                hookPref.setValue("MNC", MNC);
                UpdateProp.updateCarrier(carrierName, MCC+MNC);
                return true;
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Change Carrier ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Change Country and Language
     * @param countryISO
     * @param languageCode
     * @return
     */
    public boolean changeCountry(String countryISO, String languageCode){
        try {
            if(countryISO == "" || languageCode == ""){
                return false;
            }else {
                hookPref.setValue("CountryISO", countryISO);
                hookPref.setValue("LanguageCode", languageCode);
                UpdateProp.updateCountry(countryISO);
                return true;
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Change Country ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bypass Origin IP
     * @param ip
     * @return
     */
    public boolean bypassOriginIP(String ip){
        try {
            if(ip == ""){
                return false;
            }else {
                hookPref.setValue("BypassIP", ip);
                return true;
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Bypass IP ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Change GPS Latitude, Longitude
     * @param lat
     * @param lon
     * @return
     */
    public boolean changeGPS(String lat, String lon){
        try {
            if(lat == "" || lon == ""){
                return false;
            }else {
                Double.parseDouble(lat);
                Double.parseDouble(lon);
                hookPref.setValue("Latitude", lat);
                hookPref.setValue("Longitude", lon);
                return true;
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Change GPS ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Dump ViewLayout to Xml File
     * @return
     */
    private boolean dumpLayout(){
        try {
            String dumpCmd = "uiautomator dump " + LAYOUT_FILE_PATH + "\n";
            //String chmodCmd = "chmod 777 " + LAYOUT_FILE_PATH;
            myRunCommand.runRootCommand(dumpCmd);
            //myRunCommand.runCommand(dumpCmd);
            //myRunCommand.runRootCommand(chmodCmd);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Dump Layout ERROR: " + e.getMessage());
            return false;
        }
    }

    public boolean enableApp(String packageName, String action){
        try {
            String cmd = ENABLE_APP.replace("action", action);
            cmd = cmd.replace("package", packageName);
            myRunCommand.runRootCommand(cmd);
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Enable/Disable App ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Set HLBrowser User Agent
     * @param ua
     * @return
     */
    public boolean setUA(String ua){
        if(ua == null || ua == ""){
            return false;
        }else{
            try {
                userAgent = HookUntils.base64Decode(ua);
                Writer out = new OutputStreamWriter(new FileOutputStream("/sdcard/ua"));
                out.write(userAgent);
                out.close();
                return true;
            } catch (UnsupportedEncodingException e) {
                return false;
            } catch (Exception e){
                return false;
            }
        }
    }

    public boolean setFakeIP(String ipAdd){
        if(ipAdd == null){
            fakeIP = "";
        }else {
            fakeIP = ipAdd;
        }
        return true;
    }

    public boolean chmodFile(String permission, String filePath){
        if(permission == "" || filePath == ""){
            return false;
        }else {
            String cmd = CHMOD_FILE.replace("permission", permission);
            cmd = cmd.replace("file", filePath);
            myRunCommand.runRootCommand(cmd);
            return true;
        }
    }

    public boolean deleteSD(){
        try{

            File sdCard = new File("/sdcard");
            File[] listFiles = sdCard.listFiles();
            String cmd = "";
            for (File delFile : listFiles){
                if(delFile.getName().equals("Android")){
                    cmd += DELETE_FILE.replace("file", "/sdcard/Android/data");
                    continue;
                }
                if(delFile.getName().contains("HLDATA")){
                    continue;
                }
                cmd += DELETE_FILE.replace("file", delFile.getPath());
            }
            myRunCommand.runRootCommand(cmd);
            myRunCommand.runRootCommand("logcat -c\n");
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Delete SD ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Enable/Disable AndHook Module
     * @param enable
     * @return
     */
    public boolean activeAndHook(boolean enable){
        try {
            if(enable){
                hookPref.setValue("AndHook", "true");
            }else {
                hookPref.setValue("AndHook", "false");
            }
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Set AndHook ERROR: " + e.getMessage());
            return false;
        }
    }

    private String getPrvGAID(){
        myRunCommand.runRootCommand("cp -f /data/data/com.google.android.gms/shared_prefs/adid_settings.xml /sdcard/gaid.xml\n");
        File xmlFile = new File("/sdcard/gaid.xml");
        if(!xmlFile.exists() && !xmlFile.canRead()){
            return HookUntils.randomGAID();
        }

        try{
            XmlPullParserFactory xmlPactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlParser = xmlPactory.newPullParser();
            xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlParser.setInput(new FileInputStream(xmlFile), null);
            while (xmlParser.next() != XmlPullParser.END_DOCUMENT){
                if (xmlParser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                if(xmlParser.getName().equals("string")){
                    xmlParser.require(XmlPullParser.START_TAG, null, "string");
                    if(xmlParser.getAttributeValue(null, "name").equals("adid_key")){
                        if (xmlParser.next() == XmlPullParser.TEXT) {
                            return xmlParser.getText();
                        }
                    }
                }
            }
            return HookUntils.randomGAID();
        }catch (Exception e){
            return HookUntils.randomGAID();
        }
    }

    public String getGAID() {
        myRunCommand.runRootCommand("cp -f /data/data/com.google.android.gms/shared_prefs/adid_settings.xml /sdcard/gaid.xml\n");
        File xmlFile = new File("/sdcard/gaid.xml");
        if(!xmlFile.exists() && !xmlFile.canRead()){
            return "Permission-Denied";
        }

        try{
            XmlPullParserFactory xmlPactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlParser = xmlPactory.newPullParser();
            xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlParser.setInput(new FileInputStream(xmlFile), null);
            while (xmlParser.next() != XmlPullParser.END_DOCUMENT){
                if (xmlParser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                if(xmlParser.getName().equals("string")){
                    xmlParser.require(XmlPullParser.START_TAG, null, "string");
                    if(xmlParser.getAttributeValue(null, "name").equals("adid_key")){
                        if (xmlParser.next() == XmlPullParser.TEXT) {
                            String imei = hookPref.getValue("IMEI");
                            String imsi = hookPref.getValue("IMSI");
                            //String simserial = hookPref.getValue("SimSerial");
                            String androidid = hookPref.getValue("AndroidID");
                            String androidserial = hookPref.getValue("AndroidSerial");
                            //String mac = hookPref.getValue("MAC");
                            //String bssid = hookPref.getValue("BSSID");
                            //String gsfid = hookPref.getValue("GSFID");
                            String device = hookPref.getValue("DEVICE");
                            return xmlParser.getText() + "|" + imei + "|" + imsi + "|" + androidid + "|" + androidserial + "|" + device;
                        }
                    }
                }
            }
            return "Not-Found";
        }catch (Exception e){
            return "ERROR";
        }
    }

    public boolean updateInstallTime(){
        try {
            long time = new Date().getTime();
            hookPref.setValue("InstallTime", String.valueOf(time));
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Update Install Time ERROR: " + e.getMessage());
            return false;
        }
    }

    public boolean runCommand(String cmd){
        try {
            myRunCommand.runRootCommand(cmd + "\n");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean appUpdate(String packageName, boolean enable){
        if(packageName == null || packageName == ""){
            return false;
        }
        try {
            String apkPath = "";
            int flags = PackageManager.GET_META_DATA |
                    PackageManager.GET_SHARED_LIBRARY_FILES;

            PackageManager pkgManager = appContext.getPackageManager();
            List<ApplicationInfo> listApps = pkgManager.getInstalledApplications(flags);

            for (ApplicationInfo app : listApps){
                if(app.packageName.equals(packageName)){
                    apkPath = app.sourceDir;
                    break;
                }
            }

            if(apkPath == ""){
                return false;
            }

            if(enable){
                String cmd = CHATTR_FILE.replace("permission", "-i");
                cmd = cmd.replace("file", apkPath);
                myRunCommand.runRootCommand(cmd);
                return true;
            }else {
                String cmd = CHATTR_FILE.replace("permission", "+i");
                cmd = cmd.replace("file", apkPath);
                myRunCommand.runRootCommand(cmd);
                return true;
            }

        }catch (Exception e){
            Log.d(LOG_TAG, "App Update - Check Exist App ERROR: " + e.getMessage());
            return false;
        }
    }

}
