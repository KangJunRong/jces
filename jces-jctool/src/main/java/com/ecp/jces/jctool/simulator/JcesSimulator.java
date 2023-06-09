package com.ecp.jces.jctool.simulator;

import com.ecp.jces.jctool.shell.JcesShell;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JcesSimulator {

    public static class Simulator implements Runnable {

        private InputStream is;

        public Simulator() {

        }
        public Simulator(Process p) {
            this.is = p.getErrorStream();
        }

        @Override
        public void run() {

            try (BufferedReader reader =  new BufferedReader(new InputStreamReader(is));) {

                String data = null;
                while ((data = reader.readLine()) != null) {
                    //System.out.println(data);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {

        Map<String, String> params = new HashMap();
        params.put(SimulatorProcess.PARAM_PORT_KEY, "29500");
        params.put(SimulatorProcess.PARAM_EVENT_PORT_KEY, "29501");
        params.put(SimulatorProcess.PARAM_DEBUG_PORT_KEY, "9027");



        try {
            SimulatorProcess sp = SimulatorManager.newInstance("D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\jces.exe", params);
            Process p = sp.exec();

            Simulator sim = new Simulator(p);
            Thread sThread = new Thread(sim);
            sThread.start();

            Thread.sleep(100);
            JcesShell.lineInput("/terminal Simulator|Jces:9027");

            JcesShell.runJcshCmd(new File("D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\initScript\\init.jcsh"), "init.jcsh");

            JcesShell.lineInput("/card");
            JcesShell.lineInput("set-key 255/1/DES-ECB/404142434445464748494a4b4c4d4e4f 255/2/DES-ECB/404142434445464748494a4b4c4d4e4f 255/3/DES-ECB/404142434445464748494a4b4c4d4e4f");
            JcesShell.lineInput("init-update 255");
            JcesShell.lineInput("ext-auth mac");

            JcesShell.lineInput("delete -r 74657374636d6363506b67");
            JcesShell.lineInput("upload D:\\java\\eclipse_mars\\runtime-EclipseApplication\\test_cmcc\\bin\\com\\ecp\\cmcc\\javacard\\cmcc.cap");

            JcesShell.lineInput("install -i 74657374636d63634170706c6574 -q C9#() 74657374636d6363506b67 74657374636d63634170706c6574");
            JcesShell.lineInput("/card");


            JcesShell.lineInput("delete -r 74657374636d6363506b67");


            InstallItem item = JcesShell.getInstallItem();

            if (item != null) {
                System.out.println("DTR: " + item.getDtrSpace());
            }

            SimulatorManager.remove(sp.getName());
            while (true) {
                Thread.sleep(1000);
            }
            //close
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
