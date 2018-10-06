package com.hl46000.hlfaker.fakeinfo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.UserHandle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;

import com.hl46000.hlfaker.Common;
import com.hl46000.hlfaker.RunCommand;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by hl46000 on 10/5/17.
 */

public class HookUntils {

    public static List<String> hideFiles = new ArrayList<String>();
    static {
        hideFiles.add("Supersuer");
        hideFiles.add("Suser");
        hideFiles.add("superuser");
        hideFiles.add("supersu");
        hideFiles.add("XposedBridge");
        hideFiles.add("xposed");
        hideFiles.add("busybox");
        //hideFiles.add("daemonsu");
        hideFiles.add("chainfire");
        hideFiles.add("/sbin/su");
        hideFiles.add("/bin/su");
        hideFiles.add("/xbin/su");
        hideFiles.add("/vendor/su");
        //hideFiles.add("/qemu");
        hideFiles.add("intel");
        hideFiles.add("microvirt");
        hideFiles.add("memusf");
        hideFiles.add("memud");
        hideFiles.add("_x86");
        hideFiles.add("vbox");
        hideFiles.add("memuguest");
        hideFiles.add("memufp");
        hideFiles.add("modalias");
    }

    public static boolean needHideFile(String filePath){
        for (String keywork : hideFiles){
            if(filePath.contains(keywork)){
                return true;
            }
        }
        return false;
    }

    public static boolean isMyPackages(String packageName){
        if(packageName.equals(Common.XPOSED_PACKAGE) || packageName.equals(Common.HLFAKER_PACKAGE) ||
                packageName.equals(Common.SUPERSU_PACKAGE) || packageName.equals(Common.MAGISK_PACKAGE) || packageName.equals(Common.ROOTXPLOER_PACKAGE)){
            return true;
        }else {
            return false;
        }
    }

    /**
     * Check MainString Contains SubString
     * @param mainStr
     * @param subStr
     * @return
     */
    public static boolean isContains(String mainStr, String subStr){
        boolean contains = false;
        int subIndex = 0;
        int match = 0;
        try {
            for (int i = 0; i < mainStr.length(); i++) {
                if(match == subStr.length()){
                    contains = true;
                    break;
                }
                if(mainStr.charAt(i) == subStr.charAt(subIndex)){
                    match += 1;
                    subIndex += 1;
                }else{
                    subIndex = 0;
                }
                if(match == subStr.length()){
                    contains = true;
                    break;
                }
            }
            return contains;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check Application is System
     * @param applicationInfo
     * @return
     */
    public static boolean isSystemPackage(ApplicationInfo applicationInfo) {
        try {
            return (applicationInfo.flags & 1) != 0;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Base64 Encode String
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String base64Encode(String text) throws UnsupportedEncodingException {
        byte[] data = text.getBytes("UTF-8");
        String endcode = Base64.encodeToString(data, Base64.DEFAULT);
        return endcode;
    }

    /**
     * Base64 Decode String
     * @param encode
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String base64Decode(String encode) throws UnsupportedEncodingException{
        byte[] data = Base64.decode(encode, Base64.DEFAULT);
        String text = new String(data, "UTF-8");
        return text;
    }

    /**
     * Create Fake Net Folder
     * @return
     */
    public static boolean createNetFolder(){
        try {

            File netFolder = new File("/sdcard/HLDATA/net");
            if(!netFolder.exists()){
                netFolder.mkdir();
            }
            new File("/sdcard/HLDATA/net/ccmni0").mkdir();
            new File("/sdcard/HLDATA/net/ccmni1").mkdir();
            new File("/sdcard/HLDATA/net/ccmni2").mkdir();
            new File("/sdcard/HLDATA/net/ifb0").mkdir();
            new File("/sdcard/HLDATA/net/ifb1").mkdir();
            new File("/sdcard/HLDATA/net/lo").mkdir();
            new File("/sdcard/HLDATA/net/sit0").mkdir();
            new File("/sdcard/HLDATA/net/wlan0").mkdir();
            /*
            RunCommand runCmd = new RunCommand();
            runCmd.runRootCommand("chmod 777 /data/local/\n");
           
            runCmd.runRootCommand("mkdir /data/local/net\n");
           
            runCmd.runRootCommand("chmod 777 /data/local/net\n");
           
            runCmd.runRootCommand("mkdir /data/local/net/ccmni0\n");
           
            runCmd.runRootCommand("chmod 777 /data/local/net/ccmni0\n");
           
            runCmd.runRootCommand("mkdir /data/local/net/ccmni1\n");
           
            runCmd.runRootCommand("chmod 777 /data/local/net/ccmni1\n");
           
            runCmd.runRootCommand("mkdir /data/local/net/ccmni2\n");
           
            runCmd.runRootCommand("chmod 777 /data/local/net/ccmni2\n");
           
            runCmd.runRootCommand("mkdir /data/local/net/ifb0\n");
           
            runCmd.runRootCommand("chmod 777 /data/local/net/ifb0\n");
           
            runCmd.runRootCommand("mkdir /data/local/net/ifb1\n");
           
            runCmd.runRootCommand("chmod 777 /data/local/net/ifb1\n");
           
            runCmd.runRootCommand("mkdir /data/local/net/lo\n");
           
            runCmd.runRootCommand("chmod 777 /data/local/net/lo\n");
           
            runCmd.runRootCommand("mkdir /data/local/net/sit0\n");
           
            runCmd.runRootCommand("chmod 777 /data/local/net/sit0\n");
           
            runCmd.runRootCommand("mkdir /data/local/net/wlan0\n");
           
            runCmd.runRootCommand("chmod 777 /data/local/net/wlan0\n");
            */
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Create Fake address file
     * @param wifiMAC
     * @return
     */
    public static boolean createNetFile(String wifiMAC){
        try {
            Writer out = new OutputStreamWriter(new FileOutputStream("/sdcard/HLDATA/net/ccmni0/address"));
            out.write(randomMACAddress());
            out.close();
            out = new OutputStreamWriter(new FileOutputStream("/sdcard/HLDATA/net/ccmni1/address"));
            out.write(randomMACAddress());
            out.close();
            out = new OutputStreamWriter(new FileOutputStream("/sdcard/HLDATA/net/ccmni2/address"));
            out.write(randomMACAddress());
            out.close();
            out = new OutputStreamWriter(new FileOutputStream("/sdcard/HLDATA/net/ifb0/address"));
            out.write(randomMACAddress());
            out.close();
            out = new OutputStreamWriter(new FileOutputStream("/sdcard/HLDATA/net/ifb1/address"));
            out.write(randomMACAddress());
            out.close();
            out = new OutputStreamWriter(new FileOutputStream("/sdcard/HLDATA/net/lo/address"));
            out.write(randomMACAddress());
            out.close();
            out = new OutputStreamWriter(new FileOutputStream("/sdcard/HLDATA/net/sit0/address"));
            out.write(randomMACAddress());
            out.close();
            out = new OutputStreamWriter(new FileOutputStream("/sdcard/HLDATA/net/wlan0/address"));
            out.write(wifiMAC);
            out.close();
            /*
            RunCommand runCmd = new RunCommand();
            runCmd.runRootCommand("chmod 777 /data/misc/sys/net/wlan0/address\n");
            runCmd.runRootCommand("chmod 777 /data/local/net/ccmni0/address\n");
            runCmd.runRootCommand("chmod 777 /data/local/net/ccmni1/address\n");
            runCmd.runRootCommand("chmod 777 /data/local/net/ccmni2/address\n");
            runCmd.runRootCommand("chmod 777 /data/local/net/ifb0/address\n");
            runCmd.runRootCommand("chmod 777 /data/local/net/ifb1/address\n");
            runCmd.runRootCommand("chmod 777 /data/local/net/lo/address\n");
            runCmd.runRootCommand("chmod 777 /data/local/net/sit0/address\n");
            */
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static boolean randomMMC(){
        try {
            Writer out = new OutputStreamWriter(new FileOutputStream("/sdcard/HLDATA/cid"));
            out.write(randomCID());
            out.close();
            out = new OutputStreamWriter(new FileOutputStream("/sdcard/HLDATA/csd"));
            out.write(randomCID());
            out.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static boolean randomUUID(){
        try {
            Writer out = new OutputStreamWriter(new FileOutputStream("/sdcard/HLDATA/uuid"));
            out.write(UUID.randomUUID().toString());
            out.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static String randomGSFID(){
        Random rnd = new Random();
        String dg = "1234567890";
        String gsfid = "";
        while (gsfid.length() < 19){
            gsfid = gsfid + dg.charAt(rnd.nextInt(10));
        }
        return gsfid;
    }

    private static String randomCID(){
        Random rnd = new Random();
        String ch = "0123456789abcdef";
        String cid = "1";
        while (cid.length() < 32){
            cid = cid + ch.charAt(rnd.nextInt(16));
        }
        return cid;
    }

    /**
     * Get List System Package Name
     * @param mContext
     * @return
     */
    public static List<String> getSystemPackages(Context mContext){
        int allFlags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;
        PackageManager pkgManager = mContext.getApplicationContext().getPackageManager();
        List<String> systemPackages = new ArrayList<String>();
        List<PackageInfo> packages;
        packages = new ArrayList<PackageInfo>();
        packages = pkgManager.getInstalledPackages(allFlags);
        for (PackageInfo pkgInfo:packages) {
            if ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1){
                systemPackages.add(pkgInfo.packageName);
            }else if(pkgInfo.packageName.contains("hlfaker") || pkgInfo.packageName.contains("xposed") || pkgInfo.packageName.contains("supersu") || pkgInfo.packageName.contains("kinguser"))  {
                systemPackages.add(pkgInfo.packageName);
            }
        }
        //Log.d("SystemPackages", "Packages: " + systemPackages.size());
        return systemPackages;
    }


    /**
     * Random WiFi MAC Address
     * @return
     */
    public static String randomMACAddress(){
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);

        macAddr[0] = (byte)(macAddr[0] & (byte)254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

        StringBuilder sb = new StringBuilder(18);

        for(byte b : macAddr){

            if(sb.length() > 0)
                sb.append(":");

            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    /**
     * Random Android ID
     * @return
     */
    public static String randomAndroidID(){
        String digitAndChar = "0123456789abcdef";
        String androidID = "";
        Random rnd = new Random();
        while (androidID.length() < 16)
        {
            androidID = androidID + digitAndChar.charAt(rnd.nextInt(16));
        }
        return androidID;
    }

    /**
     * Random Goolge Ads ID
     * @return
     */
    public static String randomGAID(){
        return UUID.randomUUID().toString();
    }

    /**
     * Random Android Serial
     * @return
     */
    public static String randomAndroidSerial(){
        String digitAndChar = "0123456789abcdef";
        String serialNo = "";
        Random rnd = new Random();
        while (serialNo.length() < 12)
        {
            serialNo = serialNo + digitAndChar.charAt(rnd.nextInt(16));
        }
        return serialNo;
    }

    /**
     * Random IMEI
     * @param originIMEI
     * @return
     */
    public static String randomIMEI(String originIMEI){
        String TAC = "";

        if(originIMEI.length() > 8){
            TAC = originIMEI.substring(0,8);
        }else if(originIMEI.length() == 8){
            TAC = originIMEI;
        }

        String IMEI = TAC;
        Random rnd = new Random();
        while (IMEI.length() < 14)
        {
            IMEI = IMEI + Integer.toString(rnd.nextInt(10));
        }
        return IMEI + LuhnCheck(IMEI);
    }

    /**
     * Random IMSI
     * @param MCCMNC
     * @return
     */
    public static String randomIMSI(String MCCMNC){
        String IMSI = MCCMNC;
        Random rnd = new Random();
        while (IMSI.length() < 15)
        {
            IMSI = IMSI + Integer.toString(rnd.nextInt(10));
        }
        return IMSI + LuhnCheck(IMSI);
    }

    public static String randomPhoneNumber(){
        String digit = "0123456789";
        String phone = "09";
        Random rnd = new Random();
        while (phone.length() < 9)
        {
            phone = phone + digit.charAt(rnd.nextInt(10));
        }
        return phone;
    }

    /**
     * Random Sim Serial
     * @return
     */
    public static String randomSimSerial(){
        Random rnd = new Random();
        String iccidNo = "";
        while (iccidNo.length() < 19){
            iccidNo = iccidNo + Integer.toString(rnd.nextInt(10));
        }
        return iccidNo + LuhnCheck(iccidNo);
    }

    /**
     * Calulator Luhn Number
     * @param digitString
     * @return
     */
    private static String LuhnCheck(String digitString)
    {
        int i = 0;
        int i2 = 0;
        int digitIndex =  digitString.length() - 1;
        while (i < digitString.length())
        {
            int digit = Integer.parseInt(String.valueOf(digitString.charAt(digitIndex-i)));
            if (i % 2 == 0)
            {
                digit *= 2;
                if (digit > 9)
                {
                    digit -= 9;
                }
            }
            i2 += digit;
            i++;
        }
        return Integer.toString(((i2 * 9) % 10));
    }


    public static int getAppId(int uid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            try {
                // UserHandle: public static final int getAppId(int uid)
                Method method = (Method) UserHandle.class.getDeclaredMethod("getAppId", int.class);
                uid = (Integer) method.invoke(null, uid);
            } catch (Throwable ex) {
                //Util.log(null, Log.WARN, ex.toString());
            }
        return uid;
    }


    public static int getUserId(int uid) {
        int userId = 0;
        if (uid > 99) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                try {
                    // UserHandle: public static final int getUserId(int uid)
                    Method method = (Method) UserHandle.class.getDeclaredMethod("getUserId", int.class);
                    userId = (Integer) method.invoke(null, uid);
                } catch (Throwable ex) {
                    //Util.log(null, Log.WARN, ex.toString());
                }
        } else
            userId = uid;
        return userId;
    }

    public static long getParseITime(String stringTime){
        try {
            stringTime = stringTime.trim();
            return Long.parseLong(stringTime);
        }catch (Exception e){
            return new Date().getTime();
        }
    }

}
