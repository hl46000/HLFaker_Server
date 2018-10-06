package com.hl46000.hlfaker.remote;

import android.content.Context;
import android.util.Log;
import java.util.regex.Pattern;
import com.hl46000.hlfaker.security.algorithm.*;
/**
 * Created by hl46000 on 9/20/17.
 */

public class HLParser {

    private final String LOG_TAG = "HLParser";

    private Context appContext;
    private HLController myController;
    private HLSecurity mySecurity;
    private boolean deviceRegisted;
    public HLParser(Context sharedContext){
        appContext = sharedContext;
        myController = new HLController(appContext);
        String serialNumber = md5.encodeMD5(myController.getSerial().getBytes());
        mySecurity = new HLSecurity(serialNumber);
        mySecurity.sPubKey = myController.getSPubKey();
        mySecurity.cPriKey = myController.getCPriKey();
        deviceRegisted = false;
    }

    /**
     * Parser String Command From Client
     * @param command
     * @return
     */
    public String parserCommand(String command){
        String result = "failed;";
        String[] cmdArgs = null;
        String action = "";
        String arg = "";
        try{
            if(command == null || command == "" || command.isEmpty()){
                return result;
            }
            cmdArgs = command.split("=");
            if(cmdArgs == null || cmdArgs.length < 2){
                Log.d(LOG_TAG, "Command Syntax: " + cmdArgs);
                return result;
            }else {
                int index = command.indexOf("=");
                action = command.substring(0, index);
                arg = command.substring(index + 1, command.length());
            }
        }catch (Exception e){
            //Log.d(LOG_TAG, "Splipt Command ERROR: " + e.getMessage());
            return result;
        }

        if(!deviceRegisted){
            if(!action.equals("getDeviceInfo") && !action.equals("getProxy")){
                if(mySecurity.checkDevice()){
                    deviceRegisted = mySecurity.status;
                }else {
                    return "failed;";
                }
            }
        }

        switch (action){
            case "getDeviceInfo":
                result = getDeviceInfo();
                break;
            case "enableMouse":
                enableMouse(arg);
                result = "done;";
                break;
            case "mouseMove":
                mouseMove(arg);
                break;
            case "enableProxy":
                result = enableProxy(arg);
                break;
            case "getProxy":
                result = getProxy();
                break;
            case "setProxy":
                result = setProxy(arg);
                break;
            case "getApps":
                result = getApps();
                break;
            case "wipeApp":
                result = wipeApp(arg);
                break;
            case "wipeSystem":
                result = wipeSystem();
                break;
            case "uninstallApp":
                result = uninstallApp(arg);
                break;
            case "installApp":
                result = installApp(arg);
                break;
            case "openApp":
                result = openApp(arg);
                break;
            case "closeApp":
                result = closeApp(arg);
                break;
            case "existApp":
                result = existApp(arg);
                break;
            case "openLink":
                result = openLink(arg);
                break;
            case "getFrontApp":
                result = getFrontApp();
                break;
            case "getFrontApp2":
                result = getFrontApp2();
                break;
            case "getLayout":
                result = getLayout();
                break;
            case "findText":
                result = findText(arg);
                break;
            case "findTextClick":
                result = findTextClick(arg);
                break;
            case "touch":
                touch(arg);
                result = "done;";
                break;
            case "swipe":
                swipe(arg);
                result = "done;";
                break;
            case "inputText":
                result = inputText(arg);
                break;
            case "inputKeyevent":
                result = inputKeyevent(arg);
                break;
            case "deleteFile":
                result = deleteFile(arg);
                break;
            case "randomID":
                result = randomID();
                break;
            case "changeTimeZone":
                result = changeTimezone(arg);
                break;
            case "changeCarrier":
                result = changeCarrier(arg);
                break;
            case "changeCountry":
                result = changeCountry(arg);
                break;
            case "changeGPS":
                result = changeGPS(arg);
                break;
            case "saveRRS":
                result = saveRRS(arg);
                break;
            case "restoreRRS":
                result = restoreRRS(arg);
                break;
            case "enableApp":
                result = enableApp(arg);
                break;
            case "getRRS":
                result = getRRS();
                break;
            case "changeDevice":
                result = changeDevice(arg);
                break;
            case "setReferer":
                result = setReferer(arg);
                break;
            case "setUA":
                result = setUA(arg);
                break;
            case "setFakeIP":
                result = setFakeIP(arg);
                break;
            case "chmodFile":
                result = chmodFile(arg);
                break;
            case "deleteSD":
                result = deleteSD();
                break;
            case "appUpdate":
                result = appUpdate(arg);
                break;
            case "activeAndHook":
                result = activeAndHook(arg);
                break;
            case "getGAID":
                result = getGAID();
                break;
            case "updateInstallTime":
                result = updateInstallTime();
                break;
            case "changeRRSPath":
                result = changeRRSPath(arg);
                break;
            case "getRRSPath":
                result = getRRSPath();
                break;
            case "updateRRSName":
                result = updateRRSName(arg);
                break;
            case "runCommand":
                result = runCommand(arg);
                break;
            case "saveAPK":
                result = saveAPK(arg);
                break;
            default:
                break;
        }
        return  result;
    }

    //------Touch/Mouse/Input/Get/Set Group--------//

    /**
     * Get Device Infomation
     * @return
     */
    private String getDeviceInfo(){
        String info = myController.getDeviceInfo();
        if(info != null){
           return "getDeviceInfo=" + info + ";";
        }else {
            return "getDeviceInfo=failed;";
        }
    }

    /**
     * Enable/Disable Mouse Cursor
     * @param arg
     */
    private void enableMouse(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return;
        }
        if(arg.equals("1")){
            myController.enableCursor(true);
        }else {
            myController.enableCursor(false);
        }
    }

    /**
     * Send Touch Event on Screen
     * @param arg
     */
    private void touch(String arg){
        arg = arg.replace(";", "");
        if(arg == "" || !arg.contains(",")){
            return;
        }

        try {
            String[] xy = arg.split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            myController.touch(x,y);
        }catch (Exception e){
            Log.d(LOG_TAG, "Touch ERROR: " + e.getMessage());
        }
    }

    /**
     * Move Mouse Cursor
     * @param arg
     */
    private void mouseMove(String arg){
        arg = arg.replace(";", "");
        try{
            String[] coors = arg.split(",");
            if(coors.length < 2){
                return;
            }
            int x = Integer.parseInt(coors[0]);
            int y = Integer.parseInt(coors[1]);
            myController.moveCursor(x, y);
        }catch (Exception e){
            Log.d(LOG_TAG, "Mouse Move ERROR: " + e.getMessage());
        }
    }

    private String mouseDown(String arg){
        return "";
    }
    private String mouseUp(String arg){
        return "";
    }

    /**
     * Send Swipe Event to Screen
     * @param arg
     * @return
     */
    private void swipe(String arg){
        arg = arg.replace(";", "");
        if(arg == "" || !arg.contains(",")){
            return;
        }

        try {
            String[] xy = arg.split(",");
            int x1 = Integer.parseInt(xy[0]);
            int y1 = Integer.parseInt(xy[1]);
            int x2 = Integer.parseInt(xy[2]);
            int y2 = Integer.parseInt(xy[3]);
            myController.swipe(x1,y1,x2,y2);
        }catch (Exception e){
            Log.d(LOG_TAG, "Swipe ERROR: " + e.getMessage());
        }
    }

    /**
     * Send a text to Device
     * @param arg
     * @return
     */
    private String inputText(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "inputText=failed;";
        }
        if(myController.inputText(arg)){
            return "inputText=done;";
        }else {
            return "inputText=failed;";
        }
    }

    /**
     * Send a keyevent to Device
     * @param arg
     * @return
     */
    private String inputKeyevent(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "inputKeyevent=failed;";
        }
        if(myController.inputKeyEvent(arg)){
            return "inputKeyevent=done;";
        }else {
            return "inputKeyevent=failed;";
        }
    }

    /**
     * Enable Proxy Socks5 Client
     * @param arg
     * @return
     */
    private String enableProxy(String arg){
        arg = arg.replace(";", "");
        if (!arg.equals("1") && !arg.equals("0")){
            return null;
        }
        if (arg.equals("1")){
            if(myController.enableProxy(true)){
                return "enableProxy=done;";
            }else {
                return "enableProxy=failed;";
            }
        }else {
            myController.enableProxy(false);
            return "enableProxy=done;";
        }
    }

    /**
     * Get Proxy Info
     * @return
     */
    private String getProxy(){
        String proxyInfo = myController.getProxy();
        if (proxyInfo == null){
            return "getProxy=failed;";
        }else {
            return "getProxy=" + proxyInfo + ";";
        }
    }

    /**
     * Set Proxy Info
     * @return
     */
    private String setProxy(String arg){
        arg = arg.replace(";", "");
        try {
            String[] proxy = arg.split(":");
            String type = "socks5";
            if(proxy.length >= 3){
                type = proxy[2];
            }
            if (type == null || type == ""){
                type = "socks5";
            }
            if(myController.setProxy(proxy[0], proxy[1], type)){
                return "setProxy=done;";
            }else {
                return "setProxy=failed;";
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Split and Set Proxy ERROR: " + e.getMessage());
            return "setProxy=failed;";
        }
    }

    /**
     * Get List Apps Install by User
     * @return
     */
    private String getApps(){
        String apps = myController.getApps();
        if(apps == null){
            return "getApps=failed;";
        }else {
            return "getApps=" + apps + ";";
        }
    }

    /**
     * Clear Package Data
     * @param arg
     * @return
     */
    private String wipeApp(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "wipeApp=failed;";
        }
        if(myController.wipeApp(arg)){
            return "wipeApp=done;";
        }else {
            return "wipeApp=failed;";
        }
    }

    /**
     * Maybe error
     * @return
     */
    private String wipeSystem(){
        if(myController.wipeSystemData()){
            return "wipeSystem=done;";
        }else {
            return "wipeSystem=failed;";
        }
    }

    /**
     * Uninstall App
     * @param arg
     * @return
     */
    private String uninstallApp(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "uninstallApp=failed;";
        }
        if(myController.uninstallApp(arg)){
            return "uninstallApp=done;";
        }else {
            return "uninstallApp=failed;";
        }
    }

    /**
     * Iinstall App
     * @param arg
     * @return
     */
    private String installApp(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "installApp=failed;";
        }
        if(myController.installApp(arg)){
            return "installApp=done;";
        }else {
            return "installApp=failed;";
        }
    }

    /**
     * Save APK with OBB and SData
     * @param arg
     * @return
     */
    private String saveAPK(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "saveAPK=failed;";
        }
        /*
        if(myController.saveAPK(arg)){
            return "saveAPK=done;";
        }else {
            return "saveAPK=failed;";
        }
        */
        return "saveAPK=done;";
    }

    /**
     * Open Application use Package Name
     * @param arg
     * @return
     */
    private String openApp(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "openApp=failed;";
        }
        if(myController.openApp(arg)){
            return "openApp=done;";
        }else {
            return "openApp=failed;";
        }
    }

    /**
     * Close Application use Package Name
     * @param arg
     * @return
     */
    private String closeApp(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "closeApp=failed;";
        }
        if(myController.closeApp(arg)){
            return "closeApp=done;";
        }else {
            return "closeApp=failed;";
        }
    }

    /**
     * Check Exist App use Package Name
     * @param arg
     * @return
     */
    private String existApp(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "existApp=failed;";
        }
        if(myController.existApp(arg)){
            return "existApp=done;";
        }else {
            return "existApp=failed;";
        }
    }

    /**
     * Open Link Address
     * @param arg
     * @return
     */
    private String openLink(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "openLink=failed;";
        }
        if(myController.openLink(arg)){
            return "openLink=done;";
        }else {
            return "openLink=failed;";
        }
    }

    /**
     * Get Front App
     * @return
     */
    private String getFrontApp(){
        String pkgName = myController.getFrontApp();
        if(pkgName == null){
            return "getFrontApp=failed;";
        }else {
            return "getFrontApp=" + pkgName + ";";
        }
    }

    /**
     * Get Front App Way 2.
     * @return
     */
    private String getFrontApp2(){
        String pkgName = myController.getFrontApp2();
        if(pkgName == ""){
            return "getFrontApp2=failed;";
        }else {
            return "getFrontApp2=" + pkgName + ";";
        }
    }

    /**
     * Get Windows Layout Info
     * @return
     */
    private String getLayout(){
        if(myController.getLayout()){
            return "getLayout=done;";
        }else {
            return "getLayout=failed;";
        }
    }

    /**
     * Find text inside Windows Layout
     * @param arg
     * @return
     */
    private String findText(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "findText=failed;";
        }

        try {
            String[] args = arg.split(":");
            boolean isContains = Boolean.parseBoolean(args[1]);
            if(myController.findText(args[0], isContains)){
                return "findText=done;";
            }else {
                return "findText=failed;";
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Find Text ERROR: " + e.getMessage());
            return "findText=failed;";
        }
    }

    /**
     * Find text inside Windows Layout and Click it if Found
     * @param arg
     * @return
     */
    private String findTextClick(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "findTextClick=failed;";
        }

        try {
            String[] args = arg.split(":");
            boolean isContains = Boolean.parseBoolean(args[1]);
            if(myController.findTextAndClick(args[0], isContains)){
                return "findTextClick=done;";
            }else {
                return "findTextClick=failed;";
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Find Text ERROR: " + e.getMessage());
            return "findTextClick=failed;";
        }
    }

    /**
     * Delete File/Folder
     * @param arg
     * @return
     */
    private String deleteFile(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "deleteFile=failed;";
        }
        if(myController.deleteFile(arg)){
            return "deleteFile=done;";
        }else {
            return "deleteFile=failed;";
        }
    }
    //----------------END------------------------//

    //--------------Change Info Group------------//

    /**
     * Random Device ID
     * @return
     */
    private String randomID(){
        if(myController.randomID()){
            return "randomID=done;";
        }else {
            return "randomID=failed;";
        }
    }

    /**
     * Change Device Model: Model, DeviceCode, Manufacture v.v...
     * @param arg
     * @return
     */
    private String changeDevice(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "changeDevice=failed;";
        }
        try {
            String[] info = arg.split(Pattern.quote("|"));
            String TAC = "";
            if (info.length < 14){
                return "changeDevice=failed;";
            }

            if(info.length == 15){
                TAC = info[14];
            }

            if(myController.changeDevice(info[0], info[1], info[2], info[3], info[4], info[5], info[6], info[7],
                    info[8], info[9], info[10], info[11], info[12], info[13], TAC)){
                return "changeDevice=done;";
            }else {
                return "changeDevice=failed;";
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Change Device ERROR: " + e.getMessage());
            return "changeDevice=failed;";
        }
    }

    /**
     * Change GPS info
     * @param arg
     * @return
     */
    private String changeGPS(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "changeGPS=failed;";
        }
        try{
            String[] latlon = arg.split(":");
            if(latlon.length < 2){
                return "changeGPS=failed;";
            }
            if(myController.changeGPS(latlon[0], latlon[1])){
                return "changeGPS=done;";
            }else {
                return "changeGPS=failed;";
            }
        }catch (Exception e){
            return "changeGPS=failed;";
        }
    }

    /**
     * Change Carrier Info
     * @param arg
     * @return
     */
    private String changeCarrier(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "changeCarrier=failed;";
        }
        try{
            String[] carrier = arg.split(":");
            if(carrier.length < 3){
                return "changeCarrier=failed;";
            }
            if(myController.changeCarrier(carrier[0], carrier[1], carrier[2])){
                return "changeCarrier=done;";
            }else {
                return "changeCarrier=failed;";
            }
        }catch (Exception e){
            return "changeCarrier=failed;";
        }
    }

    /**
     * Change Country ISO, Language Code
     * @param arg
     * @return
     */
    private String changeCountry(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "changeCountry=failed;";
        }
        try{
            String[] country = arg.split(":");
            if(country.length < 2){
                return "changeCountry=failed;";
            }
            if(myController.changeCountry(country[0], country[1])){
                return "changeCountry=done;";
            }else {
                return "changeCountry=failed;";
            }
        }catch (Exception e){
            return "changeCountry=failed;";
        }
    }

    /**
     * Change TimeZone
     * @param arg
     * @return
     */
    private String changeTimezone(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "changeTimeZone=failed;";
        }
        if (myController.changeTimeZone(arg)){
            return "changeTimeZone=done;";
        }else {
            return "changeTimeZone=failed;";
        }
    }

    private String saveRRS(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "saveRRS=failed;";
        }
        try {
            String[] rrsInfo = arg.split(":");
            if (rrsInfo.length < 2){
                return "saveRRS=failed;";
            }
            /*
            if(myController.saveRRS(rrsInfo[0], rrsInfo[1])){
                return "saveRRS=done;";
            }else {
                return "saveRRS=failed;";
            }
            */
            return "saveRRS=failed;";
        }catch (Exception e){
            return "saveRRS=failed;";
        }
    }

    private String restoreRRS(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "restoreRRS=failed;";
        }
        /*
        if (myController.restoreRRS(arg)){
            return "restoreRRS=done;";
        }else {
            return "restoreRRS=failed;";
        }
        */
        return "restoreRRS=failed;";
    }

    private String enableApp(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "enableApp=failed;";
        }
        try {
            String[] info = arg.split(":");
            if (info.length < 2){
                return "enableApp=failed;";
            }
            if(myController.enableApp(info[0], info[1])){
                return "enableApp=done;";
            }else {
                return "enableApp=failed;";
            }
        }catch (Exception e){
            return "enableApp=failed;";
        }
    }

    private String getRRS(){
        /*
        String listRRS = myController.getRRS();
        if(listRRS != null){
            return "getRRS=" + listRRS + ";";
        }else {
            return "getRRS=failed;";
        }
        */
        return "getRRS=failed;";
    }

    private String setReferer(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "setReferer=failed;";
        }
        /*
        if(myController.setReferer(arg)){
            return "setReferer=done;";
        }else {
            return "setReferer=failed;";
        }
        */
        return "setReferer=failed;";
    }

    private String setUA(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "setUA=failed;";
        }
        if(myController.setUA(arg)){
            return "setUA=done;";
        }else {
            return "setUA=failed;";
        }
    }

    private String setFakeIP(String arg){
        arg = arg.replace(";", "");
        if(myController.setFakeIP(arg)){
            return "setFakeIP=done;";
        }else {
            return "setFakeIP=failed;";
        }
    }

    private String chmodFile(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "chmodFile=failed;";
        }
        try {
            String[] args = arg.split(":");
            String filePath = args[0];
            String permission = args[1];
            if(myController.chmodFile(permission, filePath)){
                return "chmodFile=done;";
            }else {
                return "chmodFile=failed;";
            }
        }catch (Exception e){
            return "chmodFile=failed;";
        }
    }

    private String deleteSD(){
        if(myController.deleteSD()){
            return "deleteSD=done;";
        }else {
            return "deleteSD=failed;";
        }
    }

    private String activeAndHook(String arg){
        arg = arg.replace(";", "");
        if (!arg.equals("1") && !arg.equals("0")){
            return null;
        }
        if (arg.equals("1")){
            if(myController.activeAndHook(true)){
                return "activeAndHook=done;";
            }else {
                return "activeAndHook=failed;";
            }
        }else {
            myController.activeAndHook(false);
            return "activeAndHook=done;";
        }
    }

    private String getGAID(){
        return "getGAID=" + myController.getGAID() + ";";
    }

    private String updateInstallTime(){
        if(myController.updateInstallTime()){
            return "updateInstallTime=done;";
        }else {
            return "updateInstallTime=failed;";
        }
    }

    private String changeRRSPath(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "changeRRSPath=ERROR;";
        }
        //return myController.changeRRSPath(arg) + ";";
        return "changeRRSPath=ERROR;";
    }

    private String getRRSPath(){
        return ""; //myController.getRRSPath() + ";";
    }

    private String updateRRSName(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "updateRRSName=failed;";
        }

        try {
            String[] args = arg.split(Pattern.quote("|"));
            if (args.length < 2){
                return "updateRRSName=failed;";
            }
            String oldName = args[0];
            String newName =  args[1];
            if(oldName.isEmpty() || newName.isEmpty()){
                return "updateRRSName=failed;";
            }
            /*
            if(myController.updateRRSName(oldName, newName)){
                return "updateRRSName=done;";
            }else {
                return "updateRRSName=failed;";
            }
            */
            return "updateRRSName=failed;";
        }catch (Exception e){
            return "updateRRSName=failed;";
        }
    }

    private String runCommand(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "runCommand=failed;";
        }
        if(myController.runCommand(arg)){
            return "runCommand=done;";
        }else {
            return "runCommand=failed;";
        }
    }

    private String appUpdate(String arg){
        arg = arg.replace(";", "");
        if(arg == ""){
            return "appUpdate=failed;";
        }

        try {
            String[] args = arg.split(":");
            if (args.length < 2){
                return "appUpdate=failed;";
            }
            String packageName = args[0];
            String enable =  args[1];

            if(enable.equals("0")){
                if(myController.appUpdate(packageName, false)){
                    return "appUpdate=done;";
                }else {
                    return "appUpdate=failed;";
                }
            }else {
                if(myController.appUpdate(packageName, true)){
                    return "appUpdate=done;";
                }else {
                    return "appUpdate=failed;";
                }
            }
        }catch (Exception e){
            return "appUpdate=failed;";
        }
    }

    //--------------------END--------------------//

}
