package com.ecp.jces;

import com.ecp.jces.jctool.capscript.AppletInstance;
import com.ecp.jces.jctool.capscript.ExeLoadFile;
import com.ecp.jces.jctool.capscript.ExeModule;
import com.ecp.jces.jctool.detection.AppDetectionException;
import com.ecp.jces.jctool.detection.AppDetectionUtil;
import com.ecp.jces.jctool.detection.model.PackageInfo;
import com.ecp.jces.jctool.simulator.InstallItem;
import org.junit.Test;

import java.util.*;

public class TestAppDetectionUtil {

    @Test
    public void testNonStandardApi() {
        try {
//            Map<String, String> nExpMap = AppDetectionUtil.loadStandardExp("D:\\java\\eclipse_mars\\runtime-EclipseApplication\\test_cmcc\\bin");
            Map<String, String> nExpMap = AppDetectionUtil.loadStandardExp("D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files_cmcc");

            List<PackageInfo> pkgList = AppDetectionUtil.nonStandardApiDetection("D:\\project\\cmcc_jces_tools_test\\hbcx1");

            System.out.println("***************************");
            for (PackageInfo pkg : pkgList) {
                System.out.println("AID: " + pkg.getAid() + " Name: " + pkg.getName());
            }
        } catch (AppDetectionException e) {
            e.printStackTrace();
//        } catch (ExpAnalysisException e) {
//            e.printStackTrace();
        }
    }

    @Test
    public void TestSensitiveApiDetection() {
        try {
            Set<String> sensitiveApiSet = AppDetectionUtil.sensitiveApiDetection("D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\jctools", "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files_cmcc", "D:\\java\\eclipse_rcp_2021\\runtime-EclipseApplication\\test_cmcc\\bin", "D:\\temp\\jces9");
        } catch (AppDetectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInstallProcessAnalysis_ydjtykt() {
        LinkedList<ExeLoadFile> loadFiles = new LinkedList<>();

        ExeLoadFile loadFile1 = new ExeLoadFile();
        loadFile1.setAid("A00000062801000400000000F2CE0200");
        loadFile1.setPath("D:\\project\\cmcc_jces_tools_test\\test_11\\ydjtykt\\A00000062801000400000000F2CE0200.cap");
        loadFiles.add(loadFile1);


        ExeLoadFile loadFile2 = new ExeLoadFile();
        loadFile2.setAid("A00000062801000400000000F2CE0000");
        loadFile2.setPath("D:\\project\\cmcc_jces_tools_test\\test_11\\ydjtykt\\A00000062801000400000000F2CE0000.cap");
        loadFiles.add(loadFile2);


        List<ExeModule> modules = new ArrayList<>();
        loadFile2.setExeModuleList(modules);
        ExeModule module = new ExeModule();
        module.setAid("A00000062801000400000000F2CE0001");
        modules.add(module);

        List<AppletInstance> insts = new ArrayList<>();
        module.setInstanceList(insts);
        AppletInstance inst1 = new AppletInstance();
        inst1.setAid("A00000063201010693979E343AB32224");
        insts.add(inst1);



        try {
            List<InstallItem> installList = AppDetectionUtil.installProcessAnalysis(loadFiles, "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\initScript\\init.jcsh", "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\jces.exe", null);

            for (InstallItem item : installList) {
                System.out.println("***************** DTR:" + item.getDtrSpace());
            }

        } catch (AppDetectionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInstallProcessAnalysis() {
        LinkedList<ExeLoadFile> loadFiles = new LinkedList<>();

        ExeLoadFile loadFile1 = new ExeLoadFile();
        loadFile1.setAid("74657374636D6363506B67");
        //loadFile1.setLoadParam("EF1011223344556677889900");
        loadFile1.setPath("D:\\project\\cmcc_jces_tools_test\\test\\testapp\\test.cap");
        loadFiles.add(loadFile1);


        ExeLoadFile loadFile2 = new ExeLoadFile();
        loadFile2.setAid("74657374636D6363506B673131");
        //loadFile2.setLoadParam("EF1011223344556677889900");
        loadFile2.setPath("D:\\project\\cmcc_jces_tools_test\\test\\testapp\\test11.cap");
        loadFiles.add(loadFile2);


        List<ExeModule> modules = new ArrayList<>();
        loadFile1.setExeModuleList(modules);
        ExeModule module = new ExeModule();
        module.setAid("74657374636D63634170706C6574");
        modules.add(module);

        List<AppletInstance> insts = new ArrayList<>();
        module.setInstanceList(insts);
        AppletInstance inst1 = new AppletInstance();
        inst1.setAid("74657374636D63634170706C657401");
        //inst1.setInstallParam("C900EF1011223344556677889900");
        insts.add(inst1);

        AppletInstance inst2 = new AppletInstance();
        inst2.setAid("74657374636D63634170706C657402");
        //inst2.setInstallParam("C900EF1011223344556677889900");
        insts.add(inst2);


        //22
        List<ExeModule> modules2 = new ArrayList<>();
        loadFile2.setExeModuleList(modules2);
        ExeModule module2 = new ExeModule();
        module2.setAid("74657374636D63634170706C6574");
        modules2.add(module2);

        List<AppletInstance> insts2 = new ArrayList<>();
        module2.setInstanceList(insts2);
        AppletInstance inst3 = new AppletInstance();
        inst3.setAid("74657374636D63634170706C65743201");
        //inst3.setInstallParam("C900EF1011223344556677889900");
        insts2.add(inst3);


        List<AppletInstance> insts3 = new ArrayList<>();
        ExeModule module3 = new ExeModule();
        module3.setAid("74657374636D63634170706C657431");
        module3.setInstanceList(insts3);
        modules2.add(module3);

        AppletInstance inst4 = new AppletInstance();
        inst4.setAid("74657374636D63634170706C65743101");
        //inst4.setInstallParam("C900EF1011223344556677889900");
        insts3.add(inst4);


        try {
            List<InstallItem> installList = AppDetectionUtil.installProcessAnalysis(loadFiles, "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\initScript\\init.jcsh", "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\jces.exe", "EF24C7020200C8023800D7020200D8023800A112A3104F0E325041592E5359532E4444463031C91902DF0130004000000015BC00010F0303020704000D01000A01");

            for (InstallItem item : installList) {
                System.out.println("***************** DTR:" + item.getDtrSpace());
            }

        } catch (AppDetectionException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testInstallProcessAnalysis_mobilesign() {
        LinkedList<ExeLoadFile> loadFiles = new LinkedList<>();

        ExeLoadFile loadFile1 = new ExeLoadFile();
        loadFile1.setAid("d15600010180000000000003b0005100");
        //loadFile1.setLoadParam("EF1011223344556677889900");
        loadFile1.setPath("D:\\project\\cmcc_jces_tools_test\\test1\\mobilesign_cab406c3.cap");
        loadFiles.add(loadFile1);





        List<ExeModule> modules = new ArrayList<>();
        loadFile1.setExeModuleList(modules);
        ExeModule module = new ExeModule();
        module.setAid("d15600010180000000000003b0005101");
        modules.add(module);

        List<AppletInstance> insts = new ArrayList<>();
        module.setInstanceList(insts);
        AppletInstance inst1 = new AppletInstance();
        inst1.setAid("d15600010180000000000003b0005101");
        inst1.setInstallParam("C900EF10C7020C00C8025800D7020C00D8025800EA1D800FFF00110100000102010203B0005200810400010000820400010000");
        insts.add(inst1);

        AppletInstance inst2 = new AppletInstance();
        inst2.setAid("d15600010180000000000003b0005102");
//        inst2.setInstallParam("C900EF10C7020C00C8025800D7020C00D8025800EA1D800FFF00110100000102010203B0005200810400010000820400010000");
        insts.add(inst2);


        try {
            List<InstallItem> installList = AppDetectionUtil.installProcessAnalysis(loadFiles, "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\initScript\\init.jcsh", "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\jces.exe", "EF24C7020200C8023800D7020200D8023800A112A3104F0E325041592E5359532E4444463031C91902DF0130004000000015BC00010F0303020704000D01000A01");

            for (InstallItem item : installList) {
                System.out.println("***************** DTR:" + item.getDtrSpace());
            }

        } catch (AppDetectionException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testInstallProcessAnalysis_3() {
        LinkedList<ExeLoadFile> loadFiles = new LinkedList<>();

        ExeLoadFile loadFile1 = new ExeLoadFile();
        loadFile1.setAid("A00000062800000400000000F2CE0200");
        loadFile1.setLoadParam("EF0CC6020200C7020000C8020000");
        loadFile1.setPath("D:\\project\\cmcc_jces_tools_test\\test1\\cmcc-mul\\A00000062800000400000000F2CE0200.cap");
        loadFiles.add(loadFile1);



        ExeLoadFile loadFile2 = new ExeLoadFile();
        loadFile2.setAid("A00000062800000400000000F2CE0000");
        loadFile2.setLoadParam("EF0EC6040000AF00C7020000C8020000");
        loadFile2.setPath("D:\\project\\cmcc_jces_tools_test\\test1\\cmcc-mul\\A00000062800000400000000F2CE0000.cap");
        loadFiles.add(loadFile2);

        List<ExeModule> modules = new ArrayList<>();
        loadFile2.setExeModuleList(modules);
        ExeModule module = new ExeModule();
        module.setAid("A00000062800000400000000F2CE0001");
        modules.add(module);

        List<AppletInstance> insts = new ArrayList<>();
        module.setInstanceList(insts);
        AppletInstance inst1 = new AppletInstance();
        inst1.setAid("A00000063201010693979E343AB32223");
        inst1.setInstallParam("EF3AC7020500C8024000A003810101A123A3104F0E325041592E5359532E4444463031A60F500A4D4F545F545F43415348870101D7020500D8024000C93630000103000202080002030600020405000205030002060100020A0300021A1200421E1E3043151400420B0A2D030C0A3B0308010002");
        insts.add(inst1);



        ExeLoadFile loadFile3 = new ExeLoadFile();
        loadFile3.setAid("A00000062800000400000000F2CE0100");
        loadFile3.setLoadParam("EF0CC6027200C7020000C8020000");
        loadFile3.setPath("D:\\project\\cmcc_jces_tools_test\\test1\\cmcc-mul\\A00000062800000400000000F2CE0100.cap");
        loadFiles.add(loadFile3);

        List<ExeModule> modules3 = new ArrayList<>();
        loadFile3.setExeModuleList(modules3);
        ExeModule module3 = new ExeModule();
        module3.setAid("A00000062800000400000000F2CE0101");
        modules3.add(module3);

        List<AppletInstance> insts3 = new ArrayList<>();
        module3.setInstanceList(insts3);
        AppletInstance inst3 = new AppletInstance();
        inst3.setAid("A00000063201010593979E343AB32223");
        inst3.setInstallParam("EF38C7020260C8023000A003810101A121A3104F0E325041592E5359532E4444463031A60D50084D4F545F545F4550870102D7020260D8023000C903650101");
        insts3.add(inst3);


        try {
            List<InstallItem> installList = AppDetectionUtil.installProcessAnalysis(loadFiles, "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\initScript\\init.jcsh", "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\jces.exe", "EF24C7020200C8023800D7020200D8023800A112A3104F0E325041592E5359532E4444463031C91902DF0130004000000015BC00010F0303020704000D01000A01");

            for (InstallItem item : installList) {
                System.out.println("***************** DTR:" + item.getDtrSpace());
            }

        } catch (AppDetectionException e) {
            e.printStackTrace();
        }
    }

}
