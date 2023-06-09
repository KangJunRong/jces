package com.ecp.jces.jctool.util;

import com.ecp.jces.jctool.capscript.AppletInstance;
import com.ecp.jces.jctool.capscript.ExeLoadFile;
import com.ecp.jces.jctool.capscript.ExeModule;
import com.ecp.jces.jctool.capscript.exception.ApplicationCheckException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class CapUtil {

    public static void main(String[] args) {
        try {
//            analysisAppPackage("D:\\temp\\jces4\\test_cmcc\\test_cmcc.zip");
            LinkedList<ExeLoadFile> loadFiles = analysisAppPackage("D:\\project\\cmcc_jces_tools_test\\mtcall.zip");
            System.out.println(loadFiles);
        } catch (ApplicationCheckException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("rawtypes")
    private static void _unZipFiles(File zipFile, String descDir) throws IOException {
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        //解决zip文件中有中文目录或者中文文件
        ZipFile zip = new ZipFile(zipFile);
        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            File outFile = new File(descDir, zipEntryName);
//            String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
            ;
            //判断路径是否存在,不存在则创建文件路径
            File file = outFile.getParentFile();
            if (!file.exists()) {
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (outFile.isDirectory()) {
                continue;
            }
            //输出文件路径信息
//            System.out.println(outPath);
            OutputStream out = new FileOutputStream(outFile);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        zip.close();
    }

    private static String _getExtension(String path) {
        if (StringUtil.isEmpty(path) || path.indexOf(".") < 1) {
            return "";
        }

        return path.substring(path.lastIndexOf(".") + 1);
    }

    private static byte[] dataRead(DataInputStream dataIn, int len) throws IOException {
        byte[] bytes = new byte[len];

        for (int i = 0; i < len; i++) {
            bytes[i] = dataIn.readByte();
        }

        return bytes;
    }

    private static ExeLoadFile _loadCapInfo(File capFile) throws ApplicationCheckException {

        ZipEntry capEntry;
        ZipInputStream zis = null;

        ExeLoadFile loadFile = new ExeLoadFile();
        loadFile.setHash(HashCodeUtil.hashCode(capFile));
        loadFile.setFileName(capFile.getName());

        List<ExeModule> modules = new ArrayList<>();
        loadFile.setExeModuleList(modules);

        List<ExeLoadFile> importLoadFiles = new ArrayList<>();
        loadFile.setImportList(importLoadFiles);

        int len;
        try (InputStream in = new BufferedInputStream(new FileInputStream(capFile));
             ZipFile zf = new ZipFile(capFile);) {

            zis = new ZipInputStream(in);

            while ((capEntry = zis.getNextEntry()) != null) {
                if (capEntry.isDirectory()) {
                } else {

                    if (capEntry.getName().toLowerCase().endsWith("header.cap")) {
                        try (DataInputStream dataIn = new DataInputStream(zf.getInputStream(capEntry))) {
                            dataIn.skipBytes(12);

                            len = dataIn.readUnsignedByte();

//                            byte[] aid = new byte[len];
//                            dataIn.read(aid);
                            byte[] aid = dataRead(dataIn, len);

                            loadFile.setAid(HexUtil.byteArr2HexStr(aid));
                        } catch (Exception ex) {
                            throw new ApplicationCheckException("分析 Header Component 出错!", ex);
                        }

                    } else if (capEntry.getName().toLowerCase().endsWith("applet.cap")) {
                        try (DataInputStream dataIn = new DataInputStream(zf.getInputStream(capEntry))) {
                            dataIn.skipBytes(3);

                            int count = dataIn.readUnsignedByte();

                            for (int i = 0; i < count; i++) {
                                len = dataIn.readUnsignedByte();

                                byte[] maid = dataRead(dataIn, len);
                                dataIn.skipBytes(2);

                                ExeModule module = new ExeModule();
                                module.setAid(HexUtil.byteArr2HexStr(maid));
                                module.setInstanceAid(module.getAid());
                                modules.add(module);
                            }
                        } catch (Exception ex) {
                            throw new ApplicationCheckException("分析 Applet Component 出错!", ex);
                        }
                    } else if (capEntry.getName().toLowerCase().endsWith("import.cap")) {
                        try (DataInputStream dataIn = new DataInputStream(zf.getInputStream(capEntry))) {
                            dataIn.skipBytes(3);

                            int count = dataIn.readUnsignedByte();

                            for (int i = 0; i < count; i++) {
                                dataIn.skipBytes(2);
                                len = dataIn.readUnsignedByte();

//                                byte[] aid = new byte[len];
//                                dataIn.read(aid);
                                byte[] aid = dataRead(dataIn, len);

                                ExeLoadFile importLoadFile = new ExeLoadFile();
                                importLoadFile.setAid(HexUtil.byteArr2HexStr(aid));
                                importLoadFiles.add(importLoadFile);
                            }
                        } catch (Exception ex) {
                            throw new ApplicationCheckException("分析 Import Component 出错!", ex);
                        }
                    }
                }
            }

            if (StringUtil.isEmpty(loadFile.getAid())) {
                throw new ApplicationCheckException("分析 CAP 文件出错！");
            }
        } catch (ApplicationCheckException ex) {
            throw ex;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zis != null) {
                try {
                    zis.closeEntry();
                } catch (IOException ex) {
                    throw new ApplicationCheckException("关闭文件出错！!", ex);
                }
            }
        }

        return loadFile;
    }

    private static LinkedList<ExeLoadFile> _loadAppXml(File file) throws ApplicationCheckException {
        LinkedList<ExeLoadFile> loadFiles = new LinkedList<>();

        try (FileInputStream fis = new FileInputStream(file)){
            SAXReader reader = new SAXReader();
            Document doc = reader.read(file);

            Element application = doc.getRootElement();

            Iterator<Element> it = application.elementIterator();

            while (it.hasNext()) {
                Element element = it.next();

                if (!"exe-load-file".equalsIgnoreCase(element.getName())) {
                    throw new ApplicationCheckException("application.xml 格式不正确，不支持 " + element.getName() + " 元素！");
                }

                ExeLoadFile loadFile = new ExeLoadFile();
                loadFile.setAid(element.attributeValue("aid"));

                loadFile.setFileName(element.attributeValue("file"));
                loadFile.setLoadParam(element.attributeValue("load-param"));

                loadFiles.addLast(loadFile);


                List<ExeModule> modules = new ArrayList<>();
                loadFile.setExeModuleList(modules);

                Iterator<Element> sit = element.elementIterator();
                while (sit.hasNext()) {
                    Element subElement = sit.next();

                    if (!"exe-module".equalsIgnoreCase(subElement.getName())) {
                        throw new ApplicationCheckException("application.xml 格式不正确，不支持 " + element.getName() + " 元素！");
                    }


                    ExeModule module = new ExeModule();
                    module.setAid(subElement.attributeValue("aid"));
//                    module.setInstanceAid(subElement.attributeValue("instance-aid"));

                    modules.add(module);


                    //instance
                    List<AppletInstance> insts = new ArrayList<>();
                    module.setInstanceList(insts);

                    Iterator<Element> instElemets = subElement.elementIterator();
                    while (instElemets.hasNext()) {
                        Element instElement = instElemets.next();

                        if (!"instance".equalsIgnoreCase(instElement.getName())) {
                            throw new ApplicationCheckException("application.xml 格式不正确，不支持 " + instElement.getName() + " 元素！");
                        }

                        AppletInstance inst = new AppletInstance();
                        inst.setAid(instElement.attributeValue("aid"));
                        inst.setInstallParam(instElement.attributeValue("install-param"));

                        insts.add(inst);
                    }
                }
            }

        } catch (Exception ex) {
            throw new ApplicationCheckException("分析 application.xml文件出错！", ex);
        }

        return loadFiles;
    }

    private static String _getCapPath(File path) {

        return null;
    }

    private static File scanAppXml(File root) {
        File[] files = root.listFiles();

        for (File file : files) {
            if ("application.xml".equalsIgnoreCase(file.getName())) {
                return file;
            }

            if (file.isDirectory()) {
                scanAppXml(file);
            }
        }

        return null;
    }

    private static void checkAid(String label, String aid) throws ApplicationCheckException {
        if (!StringUtil.isHexString(aid)) {
            throw new ApplicationCheckException(label + " AID(" + aid + ") 不符合Hex String 格式！");
        }

        if (aid.length() < 10 || aid.length() > 32) {
            throw new ApplicationCheckException(label + " AID(" + aid + ") 长度必须是5-32个字节！");
        }
    }

    private static void checkParam(String label, String param) throws ApplicationCheckException {
        if (StringUtil.isEmpty(param)) {
            return ;
        }

        if (!StringUtil.isHexString(param)) {
            throw new ApplicationCheckException(label + "(" + param + ") 不符合Hex String 格式！");
        }
    }

    private static void check(ExeLoadFile lf) throws ApplicationCheckException {
        checkAid("exe-load-file", lf.getAid());
        checkParam("load-param", lf.getLoadParam());

        List<ExeModule> modules = lf.getExeModuleList();

        if (modules == null || modules.size() <=0) {
            return ;
        }

        for (ExeModule module : modules) {
            checkAid("exe-load-file -> module", module.getAid());

            List<AppletInstance> instances = module.getInstanceList();

            if (instances == null || instances.size() <= 0) {
                continue;
            }

            for (AppletInstance inst : instances) {
                checkAid("instance", inst.getAid());
                checkParam("install-param", inst.getInstallParam());
            }
        }

    }

    private static LinkedList<ExeLoadFile> _analysisZip(File zipFile) throws ApplicationCheckException {

        String tempDir = "jcesCapTemp";

        File outDir = new File(zipFile.getParent(), tempDir);

        try {
            outDir.delete();

            _unZipFiles(zipFile, outDir.getPath());
        } catch (Exception ex) {
            throw new ApplicationCheckException("解压zip文件出错！", ex);
        }

        File appXml = scanAppXml(outDir);
        if (appXml == null) {
            throw new ApplicationCheckException("应用包不存在application.xml文件！");
        }

//        ZipInputStream zin = null;

        String fileName;

        List<ExeLoadFile> loadFiles = new ArrayList<>() ;
        LinkedList<ExeLoadFile> configLoadFiles = new LinkedList<>();
        LinkedList<ExeLoadFile> rLoadFiles = new LinkedList<>();


//        File[] files = appXml.getParentFile().listFiles();

//        if (files == null || files.length < 1) {
//            throw new ApplicationCheckException("zip文件内容为空！");
//        }

//        if (files.length == 1 && files[0].isDirectory()) {
//            outDir = files[0];
//        }

        File[] appFiles = appXml.getParentFile().listFiles();

        for (int i = 0; i < appFiles.length; i++) {
            fileName = appFiles[i].getName();
            if (fileName.equals("application.xml")) {
                // 解释xml文件
                configLoadFiles = _loadAppXml(appFiles[i]);

            } else if (fileName.endsWith("cap")) {
                loadFiles.add(_loadCapInfo(appFiles[i]));
            }
        }

        //检查应用包
        if (loadFiles.size() > 12) {
            throw new ApplicationCheckException("应用包最多包含12个cap文件！");
        }

        if (configLoadFiles.size() > 0) {
            if (loadFiles.size() != configLoadFiles.size()) {
                throw new ApplicationCheckException("application.xml 配置文件的 exe-load-file 数量与cap的数量不一致！");
            }

            for (ExeLoadFile lf : configLoadFiles) {
                check(lf);
                rLoadFiles.add(_getExeLoadFile(lf, loadFiles));
            }
        } else {
            throw new ApplicationCheckException("application.xml 格式不正确，必须最少包含一个exe-load-file元素！");
        }

        return rLoadFiles;
    }

    private static ExeLoadFile _getExeLoadFile(ExeLoadFile loadFile, List<ExeLoadFile> loadFiles) throws ApplicationCheckException {
        for (ExeLoadFile sLoadFile : loadFiles) {
            if (loadFile.getAid().equalsIgnoreCase(sLoadFile.getAid())) {
                List<ExeModule> moduleList = loadFile.getExeModuleList();

                if (moduleList != null && moduleList.size() > 0) { //应用包
                    if (sLoadFile.getExeModuleList() == null || sLoadFile.getExeModuleList().size() != moduleList.size()) {
                        throw new ApplicationCheckException("application.xml 配置文件的 exe-module 数量与cap的数量不一致！");
                    }

                    for (ExeModule module : moduleList) {
                        ExeModule sModule = _getExeModule(module, sLoadFile.getExeModuleList());

                        if (sModule == null) {
                            throw new ApplicationCheckException("applicat.xml 文件中, exe-load-file(AID: " +  loadFile.getAid() + ") 找不到对应用的exe-module(AID:" + module.getAid()  +")");
                        }
                    }
                } else { //lib 包
                    if (sLoadFile.getExeModuleList() != null && sLoadFile.getExeModuleList().size() > 0) {
                        throw new ApplicationCheckException("application.xml 配置文件的 exe-module 数量与cap的数量不一致！");
                    }
                }

                loadFile.setHash(sLoadFile.getHash());
                return loadFile;
            }
        }
        throw new ApplicationCheckException("分析cap出错,请查看AID是否匹配");

    }

    private static ExeModule _getExeModule(ExeModule module, List<ExeModule> moduleList) {

        if (moduleList == null || moduleList.size() <= 0) {
            return null;
        }

        for (ExeModule sModule : moduleList) {
            if (module.getAid().equalsIgnoreCase(sModule.getAid())) {
                return sModule;
            }
        }

        return null;
    }

    private static LinkedList<ExeLoadFile> _analysisCap(File capFile) throws ApplicationCheckException {
//        ZipInputStream zin = null;
//        String fileName;

        LinkedList<ExeLoadFile> loadFiles = new LinkedList<>() ;

        loadFiles.add(_loadCapInfo(capFile));

//        try (InputStream in = new BufferedInputStream(new FileInputStream(capPath))) {
//
//            File file = new File(capPath);
//            fileName = file.getName();
//
//            zin = new ZipInputStream(in);
//
//            ZipFile zf = new ZipFile(capPath);
//            loadFiles.add(_loadCapInfo(capFile));
//        } catch (ApplicationCheckException ex) {
//            throw ex;
//        } catch (IOException ex) {
//            throw new ApplicationCheckException("读取应用包数据出错！", ex);
//        } finally {
//            if (zin != null) {
//                try {
//                    zin.closeEntry();
//                } catch (IOException ex) {
////                    log.error(ex.getMessage(), ex);
//                }
//            }
//        }

        return loadFiles;
    }

    /**
     *
     * @param capPath
     * @return
     * @throws ApplicationCheckException
     */
    public static LinkedList<ExeLoadFile> analysisAppPackage(String capPath) throws ApplicationCheckException {
        File appFile = new File(capPath);

        if (!appFile.isFile()) {
            throw new ApplicationCheckException(capPath + "不是合法的文件！");
        }

        String ext = _getExtension(capPath);

        if ("zip".equalsIgnoreCase(ext)) {
            return _analysisZip(appFile);
        } else {
            throw new ApplicationCheckException("应用包必须是zip文件！");
        }
    }


}
