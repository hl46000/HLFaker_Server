package com.hl46000.hlfaker.remote;

import android.util.Log;
import com.hl46000.hlfaker.security.algorithm.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by hl46000 on 11/4/17.
 */

public class HLSecurity {

    private final String LOG_TAG = "HLSecurity";
    public String serialNumber;
    public String sPubKey;
    public String cPriKey;
    public boolean status;

    public HLSecurity(String serial){
        serialNumber = serial;
    }

    public boolean checkDevice(){
        CheckDevice check = new CheckDevice();
        Thread checkThread = new Thread(check);
        checkThread.start();
        boolean registed;
        long startTime = System.currentTimeMillis();
        while (true){
            if(check.isRegisted()){
                registed = true;
                break;
            }
            long runTime = System.currentTimeMillis() - startTime;
            if(runTime >= 10000){
                registed = false;
                break;
            }
        }
        status = registed;
        return registed;
    }

    public class CheckDevice implements Runnable{

        private final String SERVER_URL = "http://yourserver"; //another domain
        private boolean registed;

        public boolean isRegisted(){
            return registed;
        }

        @Override
        public void run() {
            try{
                byte[] encodeSerial = rsa.encryptByPublicKey(serialNumber.getBytes(), sPubKey);

                String serialBase64 = base64.encryptBASE64(encodeSerial);
                String serialBase64Url = base64url.encode(serialBase64);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(SERVER_URL.replace("serial", serialBase64Url)).build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                if(result.contains("ERROR")){
                    registed = false;
                }else {
                    byte[] resultByte = base64.decryptBASE64(result);

                    byte[] data = rsa.decryptByPrivateKey(resultByte, cPriKey);

                    String plaintData = new String(data);
                    if(plaintData.contains("ERROR")){
                        registed = false;
                    }else if(plaintData.contains(serialNumber)){
                        registed = true;
                    }else {
                        registed = false;
                    }
                }
            }catch (Exception e){
                Log.d(LOG_TAG, "Check Device ERROR: " + e.getMessage());
                registed = false;
            }
        }
    }
}
