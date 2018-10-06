package com.hl46000.hlfaker.remote;

import android.util.Log;

import com.hl46000.hlfaker.RunCommand;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Created by hl46000 on 10/24/17.
 */

public class HLProxy {
    private final String LOG_TAG = "HLProxy";

    private static final String BASE = "/data/data/com.hl46000.hlfaker/";

    private final static String CMD_IPTABLES_RETURN = "iptables -t nat -A OUTPUT -p tcp -d 0.0.0.0 -j RETURN\n";

    private final static String CMD_IPTABLES_REDIRECT_ADD_SOCKS = "iptables -t nat -A OUTPUT -p tcp -j REDIRECT --to 8123\n";

    public final static String DEFAULT_IPTABLES = "/data/data/com.hl46000.hlfaker/iptables";

    public final static String ALTERNATIVE_IPTABLES = "/system/bin/iptables";

    private static final String bypassIP = "192.168.0.0/16|127.0.0.1/32|10.0.0.0/16";
    private static final String SERVER_IP = "150.95.110.225";
    private static final String DROP_WEBRTC_UDP = "iptables -A INPUT -p udp --dport stunport -j DROP\n";
    private static final String DROP_WEBRTC_TCP = "iptables -A INPUT -p tcp --dport stunport -j DROP\n";
    private static final String ALLOW_WEBRTC = "iptables -D INPUT -p udp --dport stunport -j DROP\n";

    private RunCommand runCommand;

    public HLProxy(){
        runCommand = new RunCommand();
    }

    public boolean enableProxy(String host, String port, String type){
        try {
            if(!createConfig(host, port, type)){
                return false;
            }
            String redsocks = BASE + "redsocks -p " + BASE + "redsocks.pid -c " + BASE + "redsocks.conf\n";
            runCommand.runRootCommand(redsocks);
            runCommand.runRootCommand("iptables -A INPUT -i ap+ -p tcp --dport 8123 -j ACCEPT\n" +
                                        "iptables -A INPUT -i ap+ -p tcp --dport 8124 -j ACCEPT\n" +
                                        "iptables -A INPUT -i lo -p tcp --dport 8123 -j ACCEPT\n" +
                                        "iptables -A INPUT -i lo -p tcp --dport 8124 -j ACCEPT\n" +
                                        "iptables -A INPUT -p tcp --dport 8123 -j DROP\n" +
                                        "iptables -A INPUT -p tcp --dport 8124 -j DROP\n" +
                                        "iptables -t nat -A PREROUTING -i ap+ -p tcp -d 192.168.43.1/24 -j RETURN\n" +
                                        "iptables -t nat -A PREROUTING -i ap+ -p tcp -j REDIRECT --to 8123\n");

            StringBuilder cmd = new StringBuilder();

            cmd.append(CMD_IPTABLES_RETURN.replace("0.0.0.0", host));
            cmd.append(CMD_IPTABLES_RETURN.replace("0.0.0.0", SERVER_IP));

            if (bypassIP != null && !bypassIP.equals("")) {
                String[] addrs = decodeAddrs(bypassIP);
                for (String addr : addrs)
                    cmd.append(CMD_IPTABLES_RETURN.replace("0.0.0.0", addr));
                //cmd.append(CMD_IPTABLES_RETURN.replace("0.0.0.0", host));
            }

            String redirectCmd = CMD_IPTABLES_REDIRECT_ADD_SOCKS;

            cmd.append(redirectCmd);

            String rules = cmd.toString();

            rules = rules.replace("iptables", checkIptables());

            runCommand.runRootCommand(rules);
            /*
            runCommand.runRootCommand(DROP_WEBRTC_UDP.replace("stunport", "3478") + DROP_WEBRTC_UDP.replace("stunport", "19302") +
                    DROP_WEBRTC_UDP.replace("stunport", "19305") + DROP_WEBRTC_UDP.replace("stunport", "3478:19305") +
                    DROP_WEBRTC_UDP.replace("stunport", "49152:65535"));
            runCommand.runRootCommand(DROP_WEBRTC_TCP.replace("stunport", "3478") + DROP_WEBRTC_TCP.replace("stunport", "19302") + DROP_WEBRTC_TCP.replace("stunport", "19305"));
            */
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Enable Proxy ERROR: " + e.getMessage());
            return false;
        }

    }

    public boolean disableProxy(){

        try {
            final StringBuilder sb = new StringBuilder();
            sb.append(checkIptables()).append(" -t nat -F OUTPUT\n");
            runCommand.runRootCommand(sb.toString());
            /*
            runCommand.runRootCommand(checkIptables() + " -t nat -F PREROUTING\n" +
                    checkIptables() + " -t filter -F INPUT\n");
            */
            runCommand.runRootCommand("iptables -t nat -D PREROUTING -i ap+ -p tcp -d 192.168.43.1/24 -j RETURN\n" +
                            "iptables -t nat -D PREROUTING -i ap+ -p tcp -j REDIRECT --to 8123\n" +
                            "iptables -t nat -D PREROUTING -i ap+ -p tcp -j REDIRECT --to 8124\n" +
                            "iptables -D INPUT -i ap+ -p tcp --dport 8123 -j ACCEPT\n" +
                            "iptables -D INPUT -i ap+ -p tcp --dport 8124 -j ACCEPT\n" +
                            "iptables -D INPUT -i lo -p tcp --dport 8123 -j ACCEPT\n" +
                            "iptables -D INPUT -i lo -p tcp --dport 8124 -j ACCEPT\n" +
                            "iptables -D INPUT -p tcp --dport 8123 -j DROP\n" +
                            "iptables -D INPUT -p tcp --dport 8124 -j DROP\n" +
                            "killall -9 redsocks\n" +
                            "kill -9 `cat " + BASE + "redsocks.pid`\n" +
                            "rm " + BASE + "redsocks.pid\n" +
                            "rm " + BASE + "redsocks.conf\n");
            //runCommand.runRootCommand(ALLOW_WEBRTC.replace("stunport", "3478:19302") + ALLOW_WEBRTC.replace("stunport", "49152:65535"));
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Disable Proxy ERROR: " + e.getMessage());
            return false;
        }
    }

    private boolean createConfig(String host, String port, String type){
        try{
            runCommand.runRootCommand("rm -rf " + BASE + "redsocks.conf");
            StringBuilder configBuilder = new StringBuilder();
            configBuilder.append("\n");
            configBuilder.append("base {\n");
            configBuilder.append("  log_debug = off;\n");
            configBuilder.append("  log_info = off;\n");
            configBuilder.append("  log = stderr;\n");
            configBuilder.append("  daemon = on;\n");
            configBuilder.append("  redirector = iptables;\n");
            configBuilder.append("}\n");
            configBuilder.append("\n");
            configBuilder.append("redsocks {\n");
            configBuilder.append("  local_ip = 0.0.0.0;\n");
            configBuilder.append("  local_port = 8123;\n");
            configBuilder.append("  ip = " + host + ";\n");
            configBuilder.append("  port = " + port + ";\n");
            configBuilder.append("  type = " + type + ";\n");
            configBuilder.append("}\n");
            configBuilder.append("\n");
            if(writeFile(configBuilder, BASE + "redsocks.conf")){
                runCommand.runRootCommand("chmod 777 " + BASE + "redsocks.conf");
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            Log.d(LOG_TAG, "Create RedSocks Config ERROR: " + e.getMessage());
            return false;
        }
    }

    private String checkIptables() {

        File myIPTables = new File(ALTERNATIVE_IPTABLES);
        if(myIPTables.exists()){
            return ALTERNATIVE_IPTABLES;
        }else{
            return DEFAULT_IPTABLES;
        }
    }

    private boolean writeFile(StringBuilder text, String filelPath){
        try {
            Writer fileWriter = new OutputStreamWriter(new FileOutputStream(filelPath));
            fileWriter.write(text.toString());
            fileWriter.close();
            return true;
        }catch (Exception e){
            Log.d(LOG_TAG, "Write File ERROR: " + e.getMessage());
            return false;
        }
    }

    private String[] decodeAddrs(String addrs) {
        String[] list = addrs.split("\\|");
        Vector<String> ret = new Vector<String>();
        for (String addr : list) {
            String ta = validateAddr(addr);
            if (ta != null)
                ret.add(ta);
        }
        return ret.toArray(new String[ret.size()]);
    }

    private String validateAddr(String ia) {

        boolean valid1 = Pattern.matches(
                "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}/[0-9]{1,2}",
                ia);
        boolean valid2 = Pattern.matches(
                "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}", ia);

        if (valid1 || valid2) {

            return ia;

        } else {

            String addrString = null;

            try {
                InetAddress addr = InetAddress.getByName(ia);
                addrString = addr.getHostAddress();
            } catch (Exception ignore) {
                addrString = null;
            }

            if (addrString != null) {
                boolean valid3 = Pattern.matches(
                        "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}",
                        addrString);
                if (!valid3)
                    addrString = null;
            }

            return addrString;
        }
    }
}
