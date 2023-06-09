package com.ecp.jces.jctool.detection;

import com.ecp.jces.jctool.capscript.AppletInstance;
import com.ecp.jces.jctool.capscript.ExeLoadFile;
import com.ecp.jces.jctool.capscript.ExeModule;
import com.ecp.jces.jctool.capscript.exception.ApplicationCheckException;
import com.ecp.jces.jctool.detection.model.Memory;
import com.ecp.jces.jctool.detection.model.PackageInfo;
import com.ecp.jces.jctool.detection.model.PerformanceAnalysis;
import com.ecp.jces.jctool.exception.ExpAnalysisException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.simulator.*;
import com.ecp.jces.jctool.util.ExpUtil;
import com.ecp.jces.jctool.util.FileUtil;
import com.ecp.jces.jctool.util.HexUtil;
import com.ecp.jces.jctool.util.StringUtil;

import java.io.*;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class AppDetectionUtil {

//    private static final Logger log = LoggerFactory.getLogger(AppDetectionUtil.class);

    public static final int ACC_PUBLIC = 0x01;
    public static final int ACC_PRIVATE = 0x02;
    public static final int ACC_PROTECTED = 0x04;
    public static final int ACC_STATIC = 0x08;
    public static final int ACC_FINAL = 0x10;

    public static final int PRIMITIVE_TYPE = 0x8000;

    public static Map<String, String> expMap;

    public static Memory memoryDetection() throws AppDetectionException {
        return null;
    }

    private static void _staticFieldComponentAnalysis(PerformanceAnalysis perf, ZipFile zFile, ZipEntry entry) throws AppDetectionException {

        try (DataInputStream dataIn = new DataInputStream(zFile.getInputStream(entry))) {
            byte tag = dataIn.readByte();
            int len = dataIn.readUnsignedShort();
            int imageSize = dataIn.readUnsignedShort();

            int referenceCount = dataIn.readUnsignedShort();
            int arrayInitCount = dataIn.readUnsignedShort();

            byte type;
            int count;
            int arrayInitSpace = 0;
            for (int i = 0; i < arrayInitCount; i++) {
                type = dataIn.readByte();
                count = dataIn.readUnsignedShort();

                arrayInitSpace += count;
                dataIn.skipBytes(count);
            }

            int defaultValueCount = dataIn.readUnsignedShort();
            int nonDefaultValueCount = dataIn.readUnsignedShort();

            if (referenceCount < arrayInitCount) {
                throw new AppDetectionException("Static variable Analysis error!");
            }

            perf.setReferenceNullAmount(referenceCount - arrayInitCount);
            perf.setReferenceNullSpace((referenceCount - arrayInitCount) * 2);

            perf.setReferenceArrayInitAmount(arrayInitCount);
            perf.setReferenceArrayInitSpace(arrayInitSpace);

            //基本类型 空间
            perf.setPrimitiveDefaultSpace(defaultValueCount);
            perf.setPrimitiveNonDefaultSpace(nonDefaultValueCount);
        } catch (IOException ex) {
            throw new AppDetectionException("Static variable Analysis error!");
        }
    }

    private static void _descriptorComponentAnalysis(PerformanceAnalysis perf, ZipFile zFile, ZipEntry entry) throws AppDetectionException {

        if (perf.getReferenceNullAmount() == null ||
                perf.getReferenceArrayInitAmount() == null ||
                perf.getPrimitiveDefaultSpace() == null ||
                perf.getPrimitiveNonDefaultSpace() == null) {
            return ;
        }

        int defaultStart = (perf.getReferenceNullAmount() + perf.getReferenceArrayInitAmount()) * 2;
        int defaultEnd = defaultStart + perf.getPrimitiveDefaultSpace();
        int nonDefaultEnd = defaultEnd + perf.getPrimitiveNonDefaultSpace();

        int defaultCount = 0;
        int nonDefaultCount = 0;

        try (DataInputStream dataIn = new DataInputStream(zFile.getInputStream(entry))) {
            byte tag = dataIn.readByte();
            int size = dataIn.readUnsignedShort();
            int classCount = dataIn.readUnsignedByte();


            //class descriptor info
            int accessFlgs;
            int infCount;
            int fieldCount;
            int methodCount;
            int type;
            int offset;
            for (int i = 0; i < classCount; i++) {
                dataIn.readUnsignedByte(); //token

                accessFlgs = dataIn.readUnsignedByte();
                dataIn.readUnsignedShort(); // class ref

                infCount = dataIn.readUnsignedByte();
                fieldCount = dataIn.readUnsignedShort();
                methodCount = dataIn.readUnsignedShort();


                //skip interfaces
                dataIn.skipBytes(infCount * 2);


                //field descriptor
                for (int f = 0; f < fieldCount; f++) {
                    dataIn.readUnsignedByte(); //token
                    accessFlgs = dataIn.readUnsignedByte(); //access flags

                    if ((accessFlgs & ACC_STATIC) == ACC_STATIC) {
                        dataIn.skipBytes(1);
                        offset = dataIn.readUnsignedShort();
                        type = dataIn.readUnsignedShort();

                        System.out.println("offset: " + offset);
                        if ((type & PRIMITIVE_TYPE) == PRIMITIVE_TYPE) {
                            if (offset >= defaultStart && offset < defaultEnd) {
                                defaultCount++;
                            } else if (offset >= defaultEnd && offset < nonDefaultEnd) {
                                nonDefaultCount++;
                            }
                        }
                    } else {
                        dataIn.skipBytes(5);
                    }
                }


                //method descriptor
                dataIn.skipBytes(12 * methodCount);
            }

            perf.setPrimitiveDefaultAmount(defaultCount);
            perf.setPrimitiveNonDefaultAmount(nonDefaultCount);
        } catch (IOException ex) {
            throw new AppDetectionException("Static variable Analysis error!");
        }

    }

    public static void staticVarAnalysis(String capPath, PerformanceAnalysis perf) throws  AppDetectionException {

        InputStream in = null;
        ZipInputStream zin = null;

        try {
            in = new BufferedInputStream(new FileInputStream(capPath));
            zin = new ZipInputStream(in);

            ZipFile zf = new ZipFile(capPath);

            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                } else {

                    if (ze.getName().endsWith("StaticField.cap") && ze.getSize() > 0) {
                        _staticFieldComponentAnalysis(perf, zf, ze);
                    } else if (ze.getName().endsWith("Descriptor.cap") && ze.getSize() > 0) {
                        _descriptorComponentAnalysis(perf, zf, ze);
                    }
                }
            }

            if (perf.getPrimitiveDefaultAmount() == null ||
                    perf.getPrimitiveNonDefaultAmount() == null ||
                    perf.getReferenceNullAmount() == null ||
                    perf.getReferenceArrayInitAmount() == null) {
                throw new AppDetectionException("Static variable Analysis error!");
            }

            perf.setPrimitiveAmount(perf.getPrimitiveDefaultAmount() +  perf.getPrimitiveNonDefaultAmount());
            perf.setPrimitiveSpace(perf.getPrimitiveDefaultSpace() +  perf.getPrimitiveNonDefaultSpace());

            perf.setReferenceAmount(perf.getReferenceNullAmount() + perf.getReferenceArrayInitAmount());
            perf.setReferenceSpace(perf.getReferenceNullSpace() + perf.getReferenceArrayInitSpace());
        } catch (IOException ex) {
            throw new AppDetectionException("read cap file error.");
        } finally {
            if (zin != null) {
                try {
                    zin.closeEntry();
                } catch (IOException ex) {
//                    log.error(ex.getMessage(), ex);
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
//                    log.error(ex.getMessage(), ex);
                }
            }

        }
    }

    private static void _installNew(String capPath, PerformanceAnalysis perf) throws  AppDetectionException {

    }

    /**
     *分析应用包的静态变量
     *
     * @param capPath
     * @return
     * @throws AppDetectionException
     */
    public static PerformanceAnalysis staticVarAnalysis(String capPath) throws AppDetectionException {
        PerformanceAnalysis perf = new PerformanceAnalysis();

        staticVarAnalysis(capPath, perf);

//        _installNew(capPath, perf);

        return perf;
    }

    private static void _analysisImportPackage(File capFile, List<PackageInfo> importPackage, Map<String, String> expMap) throws AppDetectionException {

        InputStream in = null;
        ZipInputStream zin = null;

        try {
            in = new BufferedInputStream(new FileInputStream(capFile));
            zin = new ZipInputStream(in);

            ZipFile zf = new ZipFile(capFile);

            ZipEntry ze;
            ZipEntry headerEntry = null;
            ZipEntry importEntry = null;
            ZipEntry appletEntry = null;

            while ((ze = zin.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                } else {

                    if (ze.getName().endsWith("Header.cap") && ze.getSize() > 0) {
                        headerEntry = ze;
                    } else if (ze.getName().endsWith("Import.cap") && ze.getSize() > 0) {
                        importEntry = ze;
                    } else if (ze.getName().endsWith("Applet.cap") && ze.getSize() > 0) {
                        appletEntry = ze;
                    }
                }
            }

            if (headerEntry == null) {
                throw new AppDetectionException("not found Header component.");
            }

            try (DataInputStream dataIn = new DataInputStream(zf.getInputStream(headerEntry))) {
                dataIn.skipBytes(10);

                // package info
                dataIn.skipBytes(2);
                int aidLen = dataIn.readUnsignedByte();
                byte[] aid = new byte[aidLen];
                dataIn.read(aid);

                // package name info
                if (dataIn.available() > 0) {
                    int nameLen = dataIn.readUnsignedByte();
                    byte[] name = new byte[nameLen];
                    dataIn.read(name);
                    expMap.put(HexUtil.byteArr2HexStr(aid), new String(name, "utf-8"));
                }
            } catch (IOException ex) {
                throw new AppDetectionException("Analysis Header component error!", ex);
            }


            if (importEntry == null || appletEntry == null) {
                return ;
            }

            if (appletEntry != null) {
                int appCount = 0;
                try (DataInputStream dataIn = new DataInputStream(zf.getInputStream(appletEntry))) {
                    dataIn.skipBytes(3);
                    appCount = dataIn.readUnsignedByte();
                } catch (IOException ex) {
                    throw new AppDetectionException("Analysis Applet component error!", ex);
                }

                if (appCount <= 0) {
                    return ;
                }

                try (DataInputStream dataIn = new DataInputStream(zf.getInputStream(importEntry))) {
                    dataIn.skipBytes(3);
                    appCount = dataIn.readUnsignedByte();

                    for (int i = 0; i < appCount; i++) {
                        dataIn.skipBytes(2);
                        int aidLen = dataIn.readUnsignedByte();
                        byte[] aid = new byte[aidLen];
                        dataIn.read(aid);

                        PackageInfo pkg = new PackageInfo();
                        pkg.setAid(HexUtil.byteArr2HexStr(aid));

                        if (importPackage.indexOf(pkg) < 0) {
                            importPackage.add(pkg);
                        }
                    }
                } catch (IOException ex) {
                    throw new AppDetectionException("Analysis Import component error!", ex);
                }
            }

        } catch (IOException ex) {
            throw new AppDetectionException("read cap file error.");
        } finally {
            if (zin != null) {
                try {
                    zin.closeEntry();
                } catch (IOException ex) {
//                    log.error(ex.getMessage(), ex);
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
//                    log.error(ex.getMessage(), ex);
                }
            }

        }
    }

    public static void analysisImportPackage(File capDir, List<PackageInfo> importPackage, Map<String, String> expMap) throws AppDetectionException {

        File[] files = capDir.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                analysisImportPackage(file, importPackage, expMap);
            } else {
                if (file.getName().toLowerCase().endsWith(".cap")) {
                    _analysisImportPackage(file, importPackage, expMap);
                }
            }
        }
    }


    public static Map<String, String> loadStandardExp(String expDir) throws AppDetectionException {

        try {
            expMap = ExpUtil.loadExp(expDir);
            return expMap;
        } catch (ExpAnalysisException e) {
            expMap = null;
            throw new AppDetectionException( "加载EXP文件出错（" + expDir + ").");
        }
    }

    public static Map<String, String> getStandardExpMap() {
        return expMap;
    }

    /**
     * @param capDir
     * @param standardExp
     * @param nonStandardExp
     * @return
     * @throws AppDetectionException
     */
    public static List<PackageInfo> nonStandardApiDetection(String capDir, Map<String, String> standardExp, Map<String, String> nonStandardExp) throws AppDetectionException {

        //扫描目录，查找出应用，并且分析import package AID
        List<PackageInfo> pkgList = new ArrayList<>();

        File file = new File(capDir);

        if (!file.isDirectory()) {
            throw new AppDetectionException( capDir + " 不是合法的目录。");
        }

        analysisImportPackage(file, pkgList, nonStandardExp);


        //分析非标 api
        List<PackageInfo> nonPkgList = new ArrayList<>();
        for (PackageInfo pkg : pkgList) {
            if (standardExp.get(pkg.getAid()) == null) {
                if (nonPkgList.indexOf(pkg) < 0) {
                    nonPkgList.add(pkg);
                }
            }
        }

        return nonPkgList;
    }

    public static List<PackageInfo> nonStandardApiDetectionByCapFiles(List<String> capFiles, Map<String, String> standardExp, Map<String, String> nonStandardExp) throws AppDetectionException {

        //扫描目录，查找出应用，并且分析import package AID
        List<PackageInfo> pkgList = new ArrayList<>();

        if (capFiles == null || capFiles.size() < 1) {
            throw new AppDetectionException( capFiles + " 不能为空。");
        }

//        analysisImportPackage(file, pkgList, nonStandardExp);

        File cap = null;
        for (String fileStr : capFiles) {
            cap = new File(fileStr);

            if (!cap.isFile()) {
                throw new AppDetectionException( fileStr + " 不是有效的cap文件。");
            }

            _analysisImportPackage(cap, pkgList, nonStandardExp);
        }

        //分析非标 api
        List<PackageInfo> nonPkgList = new ArrayList<>();
        for (PackageInfo pkg : pkgList) {
            if (standardExp.get(pkg.getAid()) == null) {
                if (nonPkgList.indexOf(pkg) < 0) {
                    nonPkgList.add(pkg);
                }
            }
        }

        for (PackageInfo pkg : nonPkgList) {
            String name = nonStandardExp.get(pkg.getAid());

            if (!StringUtil.isEmpty(name)) {
                pkg.setName(name);
            }
        }

        return nonPkgList;
    }

    /**
     *
     * 分析应用引用的非标API
     * 调用之前必须调用 ExpUtil.loadExp(expDir),该方法整个生生命周期只调用一次.
     * @param capDir
     * @return
     * @throws AppDetectionException
     */
    public static List<PackageInfo>  nonStandardApiDetection(String capDir) throws AppDetectionException {
        try {
            Map<String, String> nExpMap = new HashMap<>();

            List<PackageInfo> pkgList = nonStandardApiDetection(capDir, getStandardExpMap(), nExpMap);

            //补包信息
            for (PackageInfo pkg : pkgList) {
                String name = nExpMap.get(pkg.getAid());

                if (!StringUtil.isEmpty(name)) {
                    pkg.setName(name);
                }
            }

            return pkgList;
        } catch (AppDetectionException ex) {
            throw ex;
        }
    }

    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
        }
        return -1;
    }


    /**
     * 分析安装过程消耗的空间
     * @param loadFiles
     * @param jcshPath
     * @param simulatorPath
     * @return
     * @throws AppDetectionException
     */
    public static List<InstallItem> installProcessAnalysis(LinkedList<ExeLoadFile> loadFiles, String jcshPath, String simulatorPath) throws AppDetectionException {
        return installProcessAnalysis(loadFiles, jcshPath, simulatorPath, null);
    }


    public static List<InstallItem> installProcessAnalysis(LinkedList<ExeLoadFile> loadFiles, String jcshPath, String simulatorPath, String instParam) throws AppDetectionException {

        List<InstallItem> installList = new ArrayList<>();

        Map<String, String> params = new HashMap();

        int port = findFreePort();
        int eport = findFreePort();
        int dport = findFreePort();

        if (port == -1 || eport == -1 || dport == -1) {
            throw new AppDetectionException("Could not find any free ports.");
        }

        params.put(SimulatorProcess.PARAM_PORT_KEY, Integer.toString(port));
        params.put(SimulatorProcess.PARAM_EVENT_PORT_KEY, Integer.toString(eport));
        params.put(SimulatorProcess.PARAM_DEBUG_PORT_KEY, Integer.toString(dport));


        try {
            SimulatorProcess sp = SimulatorManager.newInstance(simulatorPath, params);
            Process p = sp.exec();

            JcesSimulator.Simulator sim = new JcesSimulator.Simulator(p);
            Thread sThread = new Thread(sim);
            sThread.start();


            VirtualReader vReader = sp.getVirtualReader();
            if (vReader != null) {

                boolean isConnn = vReader.isConnection();

                int retry = 100;
                while (!isConnn && retry > 0) {
                    Thread.sleep(100L);
                    isConnn = vReader.isConnection();
                    retry--;
                }

                if (!isConnn) {
                    throw new AppDetectionException("connection Simulator time out!");
                }
            } else {
                throw new AppDetectionException("Simulator init Error!");
            }


            JcesShell.lineInput("/terminal Simulator|Jces:" + dport);

            JcesShell.runJcshCmd(new File(jcshPath), "init.jcsh");

            JcesShell.lineInput("/card");
            JcesShell.lineInput("set-key 255/1/DES-ECB/404142434445464748494a4b4c4d4e4f 255/2/DES-ECB/404142434445464748494a4b4c4d4e4f 255/3/DES-ECB/404142434445464748494a4b4c4d4e4f");
            JcesShell.lineInput("init-update 255");
            JcesShell.lineInput("ext-auth mac");


            //delete package
            for (ExeLoadFile loadFile : loadFiles) {
                JcesShell.lineInput("delete -r " + loadFile.getAid());
            }


            //load package
            for (ExeLoadFile loadFile : loadFiles) {
                JcesShell.lineInput("upload " + loadFile.getPath());
            }


            //install
            for (ExeLoadFile loadFile : loadFiles) {

                if (loadFile.isLibPkg()) {
                    continue;
                }

                List<ExeModule> modules = loadFile.getExeModuleList();

                for (ExeModule module : modules) {
                    List<AppletInstance> instances = module.getInstanceList();

                    if (instances != null && instances.size() > 0) {
                        for (AppletInstance inst : instances) {
                            if (StringUtil.isEmpty(inst.getInstallParam())) {
                                String installData = "install -i " + inst.getAid() + " -q C9#() " + loadFile.getAid() + " " + module.getAid();
                                JcesShell.lineInput(installData);
                            } else {
                                JcesShell.lineInput("install -i " + inst.getAid() + " -q " + inst.getInstallParam() + " " + loadFile.getAid() + " " + module.getAid());
                            }

                            InstallItem item = JcesShell.getInstallItem();
                            if (item != null) {
                                installList.add(item);
                            }
                        }
                    }
                }
            }

            SimulatorManager.remove(sp.getName());


            return installList;
        } catch (AppDetectionException ex) {
            throw ex;
        } catch (IOException e) {
            throw new AppDetectionException(e.getMessage(), e);
        } catch (Exception e) {
            throw new AppDetectionException(e.getMessage(), e);
        }
    }


    /**
     * 解压到指定目录
     */
    public static void unZipFiles(String zipPath,String descDir)throws IOException
    {
        unZipFiles(new File(zipPath), new File(descDir));
    }
    /**
     * 解压文件到指定目录
     */
    @SuppressWarnings("rawtypes")
    public static void unZipFiles(File zipFile,File descDir)throws IOException
    {
//        File pathFile = new File(descDir);
        if(!descDir.exists()) {
            descDir.mkdirs();
        }
        //解决zip文件中有中文目录或者中文文件
        ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));
        for(Enumeration entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry)entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);

            File outPath = new File(descDir, zipEntryName);
            //判断路径是否存在,不存在则创建文件路径
            File file = outPath.getParentFile();
            if(!file.exists()) {
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if(outPath.isDirectory()) {
                continue;
            }
            //输出文件路径信息
//            System.out.println(outPath);
            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while((len=in.read(buf1))>0) {
                out.write(buf1,0,len);
            }
            in.close();
            out.close();
        }
//        System.out.println("******************解压完毕********************");
    }

    public static void scanCap(File dir, Vector<File> outFiles, File tempDir) {

        File[] files = dir.listFiles();

        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                scanCap(file, outFiles, tempDir);
            } else {
                if (fileName.toLowerCase().endsWith(".cap")) {
                    outFiles.add(file);

                    //unCap
                    try {
                        unZipFiles(file, new File(tempDir, fileName.substring(0, fileName.lastIndexOf("."))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static Set<String> sensitiveApiDetection(String jctoolsPath, String exportFilePath, String appHomePath, String tempPath) throws AppDetectionException {
        Process sensitiveProcess = null;
        File tempDir = null;
        try {

            File jcHomePath = new File(jctoolsPath);
            if (!jcHomePath.isDirectory()) {
                throw new AppDetectionException("jctools 目录不存在。");
            }

            File exportFileDir = new File(exportFilePath);
            if (!exportFileDir.isDirectory()) {
                throw new AppDetectionException("export file 目录不存在。");
            }

            File appHomeDir = new File(appHomePath);
            if (!appHomeDir.isDirectory()) {
                throw new AppDetectionException("app home 目录不存在。");
            }

            tempDir = new File(tempPath);
            if (!tempDir.isDirectory()) {
                throw new ApplicationCheckException("tempPath 不是目录。");
            }

            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

            File libDir = new File(jcHomePath, "lib");
            if (!libDir.isDirectory()) {
                throw new ApplicationCheckException("lib 目录不存在。");
            }
            File[] jars = libDir.listFiles();

            StringBuilder classPath = new StringBuilder();
            for (File file : jars) {
                if (!file.getName().endsWith(".jar") || file.getName().startsWith("jc-converter")) {
                    continue;
                }

                classPath.append(file.getPath()).append(File.pathSeparator);
            }


            File jdkDir = new File(jcHomePath, "jdk");
            if (!jdkDir.isDirectory()) {
                throw new ApplicationCheckException("jdk 目录不存在。");
            }
            String javaHome = jdkDir.getPath();

            StringBuilder cmd = new StringBuilder();
            cmd.append(javaHome).append(File.separator).append("bin").append(File.separator).append("java");
            cmd.append(" -Djc.home=").append(javaHome);
            cmd.append(" -classpath").append(" ").append(classPath.toString());
            cmd.append(" com.sun.javacard.offcardverifier.VerifyMethod");
            cmd.append(" ").append(appHomePath).append(" ").append(exportFilePath).append(" ").append(tempPath).append(" ");


            Vector<File> caps = new Vector<>();
            scanCap(appHomeDir, caps, tempDir);

            Map<String, String> apiMap = new HashMap<>();
            for (File cap : caps) {

                sensitiveProcess = Runtime.getRuntime().exec(cmd.toString() + cap.getPath());

                BufferedReader reader = new BufferedReader(new InputStreamReader(sensitiveProcess.getErrorStream()));

                String data = null;
                while ((data = reader.readLine()) != null) {
                    if (data.startsWith("Sensitive API:")) {
//                        System.out.println(data);
                        apiMap.put(data.substring(15), null);
                    }
                }
            }

            return apiMap.keySet();
        } catch (Exception ex) {
            throw new AppDetectionException("分析敏感API出错，" + ex.getMessage(), ex);
        } finally {
            if (tempDir != null && tempDir.isDirectory()) {
                File[] temps = tempDir.listFiles();

                for (File temp: temps) {
                    FileUtil.delete(temp.getAbsolutePath());
                }
            }

            if (sensitiveProcess != null) {
                sensitiveProcess.destroy();
            }
        }

    }

    public static Set<String> sensitiveApiDetectionByCapFiles(String jctoolsPath, String exportFilePath, List<String> capFiles) throws AppDetectionException {
        Process sensitiveProcess = null;
        File tempDir = null;
        try {

            File jcHomePath = new File(jctoolsPath);
            if (!jcHomePath.isDirectory()) {
                throw new AppDetectionException("jctools 目录不存在。");
            }

            File exportFileDir = new File(exportFilePath);
            if (!exportFileDir.isDirectory()) {
                throw new AppDetectionException("export file 目录不存在。");
            }

            if (capFiles == null || capFiles.size() < 1) {
                throw new AppDetectionException("cap Files 不能为空。");
            }

//            tempDir = new File(tempPath);
//            if (!tempDir.isDirectory()) {
//                throw new ApplicationCheckException("tempPath 不是目录。");
//            }
//
//            if (!tempDir.exists()) {
//                tempDir.mkdirs();
//            }

            File libDir = new File(jcHomePath, "lib");
            if (!libDir.isDirectory()) {
                throw new ApplicationCheckException("lib 目录不存在。");
            }
            File[] jars = libDir.listFiles();

            StringBuilder classPath = new StringBuilder();
            for (File file : jars) {
                if (!file.getName().endsWith(".jar") || file.getName().startsWith("jc-converter")) {
                    continue;
                }

                classPath.append(file.getPath()).append(File.pathSeparator);
            }


            File jdkDir = new File(jcHomePath, "jdk");
            if (!jdkDir.isDirectory()) {
                throw new ApplicationCheckException("jdk 目录不存在。");
            }
            String javaHome = jdkDir.getPath();

            StringBuilder cmd = new StringBuilder();
            cmd.append(javaHome).append(File.separator).append("bin").append(File.separator).append("java");
            cmd.append(" -Djc.home=").append(javaHome);
            cmd.append(" -classpath").append(" ").append(classPath.toString());
            cmd.append(" com.sun.javacard.offcardverifier.VerifyMethod");
            cmd.append(" ").append(exportFilePath).append(" ");


            Map<String, String> apiMap = new HashMap<>();
            for (String cap : capFiles) {

                sensitiveProcess = Runtime.getRuntime().exec(cmd.toString() + cap);

                BufferedReader reader = new BufferedReader(new InputStreamReader(sensitiveProcess.getErrorStream()));

                String data = null;
                while ((data = reader.readLine()) != null) {
                    if (data.startsWith("Sensitive API:")) {
//                        System.out.println(data);
                        apiMap.put(data.substring(15), null);
                    }
                }
            }

            return apiMap.keySet();
        } catch (Exception ex) {
            throw new AppDetectionException("分析敏感API出错，" + ex.getMessage(), ex);
        } finally {
            if (tempDir != null && tempDir.isDirectory()) {
                File[] temps = tempDir.listFiles();

                for (File temp: temps) {
                    FileUtil.delete(temp.getAbsolutePath());
                }
            }

            if (sensitiveProcess != null) {
                sensitiveProcess.destroy();
            }
        }

    }

    public static void testToolkit1() {
        LinkedList<ExeLoadFile> loadFiles = new LinkedList<>();

        ExeLoadFile loadFile = new ExeLoadFile();
        loadFile.setAid("d15600010180000000000003b0005100");
        loadFile.setPath("D:\\project\\cmcc_jces_tools_test\\mobilesign_cab406c3.cap");

        List<ExeModule> modules = new ArrayList<>();
        ExeModule module = new ExeModule();
        module.setAid("d15600010180000000000003b0005101");
        module.setInstanceAid("d15600010180000000000003b0005101");
        modules.add(module);
        loadFile.setExeModuleList(modules);

        loadFiles.add(loadFile);

        try {
            List<InstallItem> installList = AppDetectionUtil.installProcessAnalysis(loadFiles, "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\initScript\\init.jcsh", "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\jces.exe", "C900EF10C7020C00C8025800D7020C00D8025800EA1D800FFF00110100000102010203B0005100810400010000820400010000");

            for (InstallItem item : installList) {
                System.out.println("***************** DTR:" + item.getDtrSpace());
            }

        } catch (AppDetectionException e) {
            e.printStackTrace();
        }
    }

    public static void testToolkit2() {
        LinkedList<ExeLoadFile> loadFiles = new LinkedList<>();

        ExeLoadFile loadFile = new ExeLoadFile();
        loadFile.setAid("74657374636d6363506b67");
        loadFile.setPath("D:\\java\\eclipse_rcp_2021\\runtime-EclipseApplication\\test_cmcc11\\bin\\com\\ecp\\test\\javacard\\test.cap");

        List<ExeModule> modules = new ArrayList<>();
        ExeModule module = new ExeModule();
        module.setAid("74657374636d63634170706c6574");
        module.setInstanceAid("74657374636d63634170706c6574");
        modules.add(module);
        loadFile.setExeModuleList(modules);

        loadFiles.add(loadFile);

        try {
            List<InstallItem> installList = AppDetectionUtil.installProcessAnalysis(loadFiles, "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\initScript\\init.jcsh", "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\jces.exe", "C900EA0C800A01000A01000000000000");

            for (InstallItem item : installList) {
                System.out.println("***************** DTR:" + item.getDtrSpace());
            }

        } catch (AppDetectionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        testToolkit2();
        /*
        try {
            Set<String> sensitiveApiSet = AppDetectionUtil.sensitiveApiDetection("D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\jctools", "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files_cmcc", "D:\\java\\eclipse_rcp_2021\\runtime-EclipseApplication\\test_cmcc\\bin", "D:\\temp\\jces9");
            Set<String> sensitiveApiSet = AppDetectionUtil.sensitiveApiDetection("D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\jctools", "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files_cmcc", "D:\\java\\eclipse_rcp_2021\\runtime-EclipseApplication\\test_cmcc11\\bin", "D:\\temp\\jces9");





            for (String api : sensitiveApiSet) {
                System.out.println(api);
            }
        } catch (AppDetectionException e) {
            e.printStackTrace();
        }
         */


        /*
        LinkedList<ExeLoadFile> loadFiles = new LinkedList<>();

        ExeLoadFile loadFile = new ExeLoadFile();
        loadFile.setAid("d15600010180000000000003b0005100");
        loadFile.setPath("D:\\project\\cmcc_jces_tools_test\\mobilesign_cab406c3.cap");

        List<ExeModule> modules = new ArrayList<>();
        ExeModule module = new ExeModule();
        module.setAid("d15600010180000000000003b0005101");
        module.setInstanceAid("d15600010180000000000003b0005101");
        modules.add(module);
        loadFile.setExeModuleList(modules);

        loadFiles.add(loadFile);

        try {
            List<InstallItem> installList = AppDetectionUtil.installProcessAnalysis(loadFiles, "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\initScript\\init.jcsh", "D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\jces.exe", "C900EF10C7020C00C8025800D7020C00D8025800EA1D800FFF00110100000102010203B0005100810400010000820400010000");

            for (InstallItem item : installList) {
                System.out.println("***************** DTR:" + item.getDtrSpace());
            }

        } catch (AppDetectionException e) {
            e.printStackTrace();
        }
        */


        /*
        try {
//            Map<String, String> nExpMap = AppDetectionUtil.loadStandardExp("D:\\java\\eclipse_mars\\runtime-EclipseApplication\\test_cmcc\\bin");
            Map<String, String> nExpMap = AppDetectionUtil.loadStandardExp("D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files_cmcc");

            List<PackageInfo> pkgList = AppDetectionUtil.nonStandardApiDetection("D:\\project\\cmcc_jces_tools_test\\cap");

            System.out.println("***************************");
            for (PackageInfo pkg : pkgList) {
                System.out.println("AID: " + pkg.getAid() + " Name: " + pkg.getName());
            }
        } catch (AppDetectionException e) {
            e.printStackTrace();
//        } catch (ExpAnalysisException e) {
//            e.printStackTrace();
        }
*/

//        try {
//
//            loadStandardExp("D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files_cmcc");
//
//            List<PackageInfo> pkgList = nonStandardApiDetection("D:\\java\\eclipse_mars\\runtime-EclipseApplication\\test_cmcc\\bin");
//
//            System.out.println("***************************");
//            for (PackageInfo pkg : pkgList) {
//                System.out.println("AID: " + pkg.getAid() + " Name: " + pkg.getName());
//            }
//
//        } catch (AppDetectionException e) {
//            e.printStackTrace();
//        }

//        try {
//            performanceAnalysis("D:\\java\\eclipse_mars\\runtime-EclipseApplication\\test\\bin\\com\\ecp\\test\\javacard\\test.cap");
//        } catch (AppDetectionException e) {
//            e.printStackTrace();
//        }

//        try {
//            AppDetectionUtil.staticVarAnalysis("D:\\temp\\jces8\\mobilesignature.cap");
//        } catch (AppDetectionException e) {
//            e.printStackTrace();
//        }
    }
}