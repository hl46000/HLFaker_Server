package com.hl46000.hlfaker.remote;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.hl46000.hlfaker.RunCommand;
import com.hl46000.hlfaker.data.SettingsSharedPref;

/**
 * Created by LONG-iOS Dev on 9/20/2017.
 */

public class HLService extends Service {
    private static final String ERROR_TAG = "HLService";
    private Context appContext;
    private boolean isStarted;
    public HLServer remoteServer;
    private SettingsSharedPref settingsPref;
    public Notification myNotification;
    public String errorMsg;
    private HLAutoStart myHLAutoStart;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        appContext = getApplicationContext();
        try {
            regReceiver();
        }catch (Exception e){
            Log.d(ERROR_TAG, "Register Receiver ERROR: " + e.getMessage());
        }
        settingsPref = new SettingsSharedPref(appContext);
        isStarted = false;
        if(remoteServer != null && remoteServer.serverIsRunning()){
            isStarted = true;
            return START_STICKY;
        }else if(remoteServer == null){
            try {
                int port = tryParsePort(settingsPref.getValue("RemotePort"));
                remoteServer = new HLServer(port, appContext);
                runningNotification();
            }catch (Exception e){
                runningNotification();
                Log.d(ERROR_TAG, "Create Remote ERROR: " + e.getMessage());
            }
            return START_STICKY;
        }else {
            try {
                int port = tryParsePort(settingsPref.getValue("RemotePort"));
                remoteServer.setServerPort(port);
                runningNotification();
            }catch (Exception e){
                runningNotification();
                Log.d(ERROR_TAG, "Set Remote ERROR: " + e.getMessage());
            }
            return START_STICKY;
        }

    }

    private void regReceiver(){
        myHLAutoStart = new HLAutoStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.hl46000.hlfaker.PushAlive");
        registerReceiver(myHLAutoStart, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(getApplicationContext(), "Servie Destroy", Toast.LENGTH_SHORT).show();
        //new RunCommand().runRootCommand("am startservice -n com.hl46000.hlfaker/.remote.HLService");
        Intent pushAliveIntent = new Intent("com.hl46000.hlfaker.PushAlive");
        pushAliveIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(pushAliveIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        settingsPref = new SettingsSharedPref(appContext);
        isStarted = false;
        if(remoteServer != null && remoteServer.serverIsRunning()){
            isStarted = true;
        }else if(remoteServer == null){
            try {
                int port = tryParsePort(settingsPref.getValue("RemotePort"));
                remoteServer = new HLServer(port, appContext);
                runningNotification();
            }catch (Throwable e){
                runningNotification();
                Log.d(ERROR_TAG, "Create Server ERROR: " + e.getMessage());
            }
        }else {
            try {
                int port = tryParsePort(settingsPref.getValue("RemotePort"));
                remoteServer.setServerPort(port);
                runningNotification();
            }catch (Throwable e){
                runningNotification();
                Log.d(ERROR_TAG, "Restart Server ERROR: " + e.getMessage());
            }
        }
        //Thread.setDefaultUncaughtExceptionHandler(new HandlerStop());
    }

    private void runningNotification(){
        if(myNotification == null){
            myNotification = new NotificationCompat.Builder(this)
                    .setContentTitle("HLFaker Remote Server")
                    .setTicker("Starting...").build();
        }
        try {
            remoteServer.start();
            startForeground(101, myNotification);
        }catch (Throwable e){
            errorMsg = "HLServer ERROR!";
            Log.d(ERROR_TAG, "Start Server ERROR: " + e.getMessage());
        }
        new Thread(new Running(myNotification)).start();
    }

    private class Running implements Runnable{
        private Notification appNotification;
        public Running(Notification notifi){
            appNotification = notifi;
        }

        @Override
        public void run() {
            while (true){
                if(remoteServer.isStarting()){
                    try {
                        Thread.sleep(1000);
                    }catch (Throwable e){
                        //break;
                    }
                    continue;
                }else {
                    if(remoteServer.serverIsRunning()){
                        try {
                            Thread.sleep(1000);
                        }catch (Throwable e){
                            //break;
                        }
                        continue;
                    }else {
                        try {
                            remoteServer.stop();
                            remoteServer.start();
                        }catch (Throwable e){
                            continue;   
                        }
                    }
                }
            }
        }
    }

    private int tryParsePort(String port){
        try {
            return Integer.parseInt(port);
        }catch(Exception e){
            Log.d(ERROR_TAG, "Parse Port ERROR: " + e.getMessage());
            return 1709;
        }
    }
}
