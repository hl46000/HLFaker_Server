package com.hl46000.hlfaker.fakeinfo;

import com.hl46000.hlfaker.RunCommand;

/**
 * Created by ZEROETC on 12/23/2017.
 */

public class UpdateProp {

    public static void firstStart(){
        RunCommand runCmd = new RunCommand();
        runCmd.runRootCommand("setprop ro.hardware qcom\n" +
                "setprop ro.boot.hardware qcom\n");
    }

    public static void updateCarrier(String carrierName, String numeric){
        RunCommand runCmd = new RunCommand();
        runCmd.runRootCommand("setprop gsm.operator.alpha " + carrierName + "\n" +
                "setprop gsm.operator.numeric " + numeric + "\n" +
                "setprop gsm.sim.operator.alpha " + carrierName + "\n" +
                "setprop gsm.sim.operator.numeric " + numeric + "\n");
    }

    public static void updateCountry(String countryISO){
        RunCommand runCmd = new RunCommand();
        runCmd.runRootCommand("setprop gsm.operator.iso-country " + countryISO + "\n" +
                "setprop gsm.sim.operator.iso-country " + countryISO + "\n");
    }

    public static void updateID(String serialno, String baseband){
        RunCommand runCmd = new RunCommand();
        runCmd.runRootCommand("setprop ro.serialno " + serialno + "\n" +
                "setprop ro.baseband " + baseband + "\n");
    }
}
