package com.hl46000.hlfaker.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hl46000.hlfaker.RunCommand;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by LONG-iOS Dev on 9/20/2017.
 */

public class HLServer {
    private static final String ERROR_TAG = "HLServer";
    private ServerSocket myServerSocket;
    private int serverPort;
    private Context appContext;
    private Thread startServerThread;
    private String msg;
    private boolean starting;
    private HLParser myParser;

    public HLServer(int port, Context sharedContext){
        this.serverPort = port;
        appContext = sharedContext;
        myParser = new HLParser(appContext);
        msg = "Starting";
        starting = false;
    }

    /**
     * Return Server Socket Port
     * @return
     */
    public int getServerPort(){
        return this.serverPort;
    }

    /**
     * Set Server Socket Port
     * @param port
     */
    public void setServerPort(int port){
        this.serverPort = port;
    }

    /**
     * Return Server Socket Started
     * @return
     */
    public boolean isStarting(){
        return starting;
    }

    /**
     * Get Sefl Server Socket
     * @return
     */
    public String getMsg(){
        return msg;
    }

    /**
     * Start Server Socket
     * @return
     */
    public boolean start(){
        try{
            //Toast.makeText(appContext, "HLServer Start!", Toast.LENGTH_SHORT).show();
            starting = true;
            startServerThread = new Thread(new ServerThread());
            startServerThread.start();
            starting = false;
            return true;
        }catch (Throwable e){
            Log.d(ERROR_TAG, "Start HLServer ERROR: " + e.getMessage());
            starting = false;
            return false;
        }
    }

    /**
     * Stop Server Socket
     * @return
     */
    public boolean stop(){
        try{
            if(myServerSocket == null){
                return true;
            }
            try{
                startServerThread.interrupt();
            }catch (Exception ex){
                Log.d(ERROR_TAG, "Stop Server Thread ERROR: " + ex.getMessage());
            }
            myServerSocket.close();
            return true;
        }catch (Throwable e){
            Log.d(ERROR_TAG, "Stop HLServer ERROR: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check State of Server Thread
     * @return
     */
    public boolean serverIsRunning(){
        if(startServerThread == null){
            return false;
        }
        if(startServerThread.getState() == Thread.State.TERMINATED){
            return false;
        }
        if (startServerThread.getState() == Thread.State.BLOCKED){
            return false;
        }
        if((myServerSocket == null) || myServerSocket.isClosed()){
            return false;
        }

        ConnectivityManager connManager  = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && !activeNetwork.isConnected()){
            return false;
        }

        if(pingToRouter()){
            return true;
        }else {
            return false;
        }
    }

    private boolean pingToRouter(){
        try{
            InetAddress myAddress = myServerSocket.getInetAddress();
            if(myAddress.isReachable(5000)){
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Main Server Thread
     */
    private class ServerThread implements Runnable{

        @Override
        public void run() {
            try {
                ConnectivityManager connManager  = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                while (true){
                    NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

                    if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnected()){
                        break;
                    }else {
                        msg = "Waiting for Wifi";
                        Thread.sleep(1000);
                    }
                }
                myServerSocket = new ServerSocket(serverPort);
                while (true){
                    if(myServerSocket.isClosed()){
                        break;
                    }
                    try{
                        Socket client = myServerSocket.accept();
                        new Thread(new ClientThread(client)).start();
                    }catch (Throwable e){
                        Log.d(ERROR_TAG, "Accept Client ERROR: " + e.getMessage());
                    }
                }
            }catch (Throwable e){
                Log.d(ERROR_TAG, "Server Thread ERROR: " + e.getMessage());
            }
        }
    }

    /**
     * Client Thread
     */
    private class ClientThread implements Runnable{

        private Socket myClient;

        public ClientThread(Socket client) throws SocketException {
            myClient = client;
            myClient.setKeepAlive(true);
            //myClient.setSendBufferSize(16384);
        }

        @Override
        public void run() {
            while (true){
                if(myClient.isClosed() || !myClient.isConnected()){
                    break;
                }

                try{
                    OutputStream clientOutput = myClient.getOutputStream();
                    InputStream clientInput = myClient.getInputStream();
                    //String clientMsg = new BufferedReader(new InputStreamReader(clientInput)).readLine();
                    BufferedReader msgReader = new BufferedReader(new InputStreamReader(clientInput, "UTF-8"));
                    String clientMsg = msgReader.readLine();
                    //msgReader.close();
                    /*
                    String clientMsg = null;
                    MessageThread readMsg = new MessageThread(msgReader);
                    Thread readerThread = new Thread(readMsg);
                    readerThread.start();
                    long startTime = System.currentTimeMillis();
                    //boolean timeout = false;
                    while (true){
                        clientMsg = readMsg.getMsg();
                        if(clientMsg == null){
                            continue;
                        }else if (clientMsg == ""){
                            try{
                                readerThread.interrupt();
                                msgReader.close();
                                clientInput.close();
                                clientOutput.close();
                            }catch (Exception e){
                            }
                            break;
                        }else if((System.currentTimeMillis()-startTime) >= 5000){
                            //timeout = true;
                            try{
                                readerThread.interrupt();
                                msgReader.close();
                                msgReader.close();
                                clientInput.close();
                                clientOutput.close();
                            }catch (Exception e){
                            }
                            break;
                        }else {
                            try{
                                readerThread.interrupt();
                                msgReader.close();
                            }catch (Exception e){
                            }
                            break;
                        }
                    }
                    */
                    if(clientMsg == null){
                        continue;
                    }
                    if(clientMsg.isEmpty()){
                        continue;
                    }
                    if (clientMsg.equals("exit")){
                        break;
                    }else if(clientMsg.equals("downloadRRS")){
                        sendRRS(clientOutput);
                        continue;
                    }else if (clientMsg.equals("uploadRRS")){
                        receiverRRS(clientInput);
                        continue;
                    }
                    String result = myParser.parserCommand(clientMsg);
                    if(result != null && !result.isEmpty()){
                        PrintStream printResult = new PrintStream(clientOutput);
                        printResult.print(result);
                        printResult.flush();
                        clientOutput.flush();
                        //new PrintStream(clientOutput).print(result);
                    }else {
                        continue;
                    }
                }catch (Throwable e){
                    Log.d(ERROR_TAG, "Client Thread ERROR: " + e.getMessage());
                    continue;
                }
            }

            try{
                myClient.shutdownInput();
                myClient.shutdownOutput();
                myClient.close();
            }catch (Throwable e){
                Log.d(ERROR_TAG, "Client Close ERROR: " + e.getMessage());
            }
        }

        private boolean sendRRS(OutputStream rrsOutputStream){
            try{
                File rrsFile = new File("/sdcard/rrs.zip");
                if(!rrsFile.exists()){
                    return false;
                }
                byte[] rrsBytes = new byte[(int)rrsFile.length()];
                FileInputStream fInputStream = new FileInputStream(rrsFile);
                BufferedInputStream buffInputStream = new BufferedInputStream(fInputStream);
                buffInputStream.read(rrsBytes,0,rrsBytes.length);
                rrsOutputStream.write(rrsBytes,0,rrsBytes.length);
                rrsOutputStream.flush();
                buffInputStream.close();
                return true;
            }catch (Exception e){
                Log.d(ERROR_TAG, "Send RRS ERROR: " + e.getMessage());
                return false;
            }
        }

        private boolean receiverRRS(InputStream rrsInputStream){
            try {
                int FILE_SIZE = 20000000;
                String restoreFilePath = "/sdcard/restore.zip";
                RunCommand runCommand = new RunCommand();
                runCommand.runRootCommand("rm -rf " + restoreFilePath + "\n");
                byte[] byteArray = new byte[FILE_SIZE];
                FileOutputStream fOutputStream = new FileOutputStream(restoreFilePath);
                BufferedOutputStream buffOutputStream = new BufferedOutputStream(fOutputStream);

                int bytesRead = rrsInputStream.read(byteArray,0,byteArray.length);

                int current = bytesRead;

                do {
                    bytesRead =
                            rrsInputStream.read(byteArray, current, (byteArray.length-current));
                    if(bytesRead >= 0) current += bytesRead;
                } while(bytesRead > -1);

                buffOutputStream.write(byteArray, 0 , current);
                buffOutputStream.flush();
                fOutputStream.close();
                buffOutputStream.close();
                return true;
            }catch (Exception e){
                Log.d(ERROR_TAG, "Receiver RRS ERROR: " + e.getMessage());
                return false;
            }
        }

        private class MessageThread implements Runnable{
            private BufferedReader msgReader;
            private String clientMsg;

            public MessageThread(BufferedReader clientMsg){
                msgReader = clientMsg;
                clientMsg = null;
            }

            public String getMsg(){
                return clientMsg;
            }
            @Override
            public void run() {
                try{
                    clientMsg = msgReader.readLine();
                }catch (Exception e){
                    Log.d("MessageThread", "Read Client Message ERROR: " + e.getMessage());
                    clientMsg = "";
                }
            }
        }
    }
}
