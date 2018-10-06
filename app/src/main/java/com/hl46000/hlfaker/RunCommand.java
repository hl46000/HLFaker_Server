package com.hl46000.hlfaker;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by hl46000 on 10/24/17.
 */

public class RunCommand {
    private final String LOG_TAG = "RunCommand";
    public RunCommand(){

    }

    public void runRootCommand(String cmds){
        RootCommandRunner cmdRunner = new RootCommandRunner(cmds);
        Thread runThread = new Thread(cmdRunner);
        runThread.start();
        long startTime = System.currentTimeMillis();
        while (true){
            if(cmdRunner.getRunDone()){
                try{
                    runThread.interrupt();
                    break;
                }catch (Exception e){
                    Log.d(LOG_TAG, "Kill RunCommand Thread ERROR: " + e.getMessage());
                    break;
                }
            }
            long runTime = System.currentTimeMillis() - startTime;
            if(runTime >= 10000){
                try{
                    runThread.interrupt();
                    Log.d(LOG_TAG, "RunCommand Thread Timeout.");
                    break;
                }catch (Exception e){
                    Log.d(LOG_TAG, "Kill RunCommand Thread ERROR: " + e.getMessage());
                    break;
                }
            }
        }
    }

    public void runCommand(String cmds){
        CommandRunner cmdRunner = new CommandRunner(cmds);
        Thread runThread = new Thread(cmdRunner);
        runThread.start();
        long startTime = System.currentTimeMillis();
        while (true){
            if(cmdRunner.getRunDone()){
                try{
                    runThread.interrupt();
                    break;
                }catch (Exception e){
                    Log.d(LOG_TAG, "Kill RunCommand Thread ERROR: " + e.getMessage());
                    break;
                }
            }
            long runTime = System.currentTimeMillis() - startTime;
            if(runTime >= 10000){
                try{
                    runThread.interrupt();
                    Log.d(LOG_TAG, "RunCommand Thread Timeout.");
                    break;
                }catch (Exception e){
                    Log.d(LOG_TAG, "Kill RunCommand Thread ERROR: " + e.getMessage());
                    break;
                }
            }
        }
    }

    public class RootCommandRunner implements Runnable{

        private boolean runDone;
        private String commands;
        public RootCommandRunner(String cmds){
            runDone = false;
            commands = cmds;
        }

        public boolean getRunDone(){
            return runDone;
        }

        @Override
        public void run() {
            try{
                Process suProcess = Runtime.getRuntime().exec("su");
                DataOutputStream outputStream = new DataOutputStream(suProcess.getOutputStream());
                DataInputStream inputStream = new DataInputStream((suProcess.getInputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                if(commands.contains("\n")){
                    String[] listCmds = commands.split("\n");
                    for (String cmd : listCmds) {
                        outputStream.write((cmd + "\n").getBytes("UTF-8"));
                        outputStream.flush();
                        //Log.d(LOG_TAG, "Command: " + cmd);
                    }
                }else {
                    outputStream.write((commands + "\n").getBytes("UTF-8"));
                    outputStream.flush();
                }

                outputStream.writeBytes("exit\n");
                outputStream.flush();
                suProcess.waitFor();
                runDone = true;
                reader.close();
                inputStream.close();
                outputStream.close();
            }catch (Exception e){
                Log.d("CommandRunner", "Run Command ERROR: " + e.getMessage());
                runDone = true;
            }
        }

    }

    public class CommandRunner implements Runnable{

        private boolean runDone;
        private String commands;
        public CommandRunner(String cmds){
            runDone = false;
            commands = cmds;
        }

        public boolean getRunDone(){
            return runDone;
        }

        @Override
        public void run() {
            try{
                Process suProcess = Runtime.getRuntime().exec("sh");
                DataOutputStream outputStream = new DataOutputStream(suProcess.getOutputStream());
                //DataInputStream inputStream = new DataInputStream((suProcess.getInputStream()));
                //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                if(commands.contains("\n")){
                    String[] listCmds = commands.split("\n");

                    for (String cmd : listCmds) {
                        //outputStream.write((cmd + "\n").getBytes("UTF-8"));
                        outputStream.writeBytes(cmd + "\n");
                        outputStream.flush();
                        //Log.d(LOG_TAG, "Command: " + cmd);
                    }

                }else {

                    outputStream.write((commands + "\n").getBytes("UTF-8"));
                    outputStream.flush();

                }

                outputStream.writeBytes("exit\n");
                outputStream.flush();
                suProcess.waitFor();
                runDone = true;
                //reader.close();
                //inputStream.close();
                outputStream.close();

            }catch (Exception e){
                Log.d("CommandRunner", "Run Command ERROR: " + e.getMessage());
                runDone = true;
            }
        }
    }
}
