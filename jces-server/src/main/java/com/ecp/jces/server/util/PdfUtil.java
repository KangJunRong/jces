package com.ecp.jces.server.util;

import com.ecp.jces.code.ConstantCode;
import com.ecp.jces.jctool.detection.model.PackageInfo;
import com.ecp.jces.vo.TemplateConfigVo;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PdfUtil {

/*    public static void main(String[] args) {

        try (FileOutputStream out = new FileOutputStream("d:\\temp\\testpdf.pdf")) {
            TestReport report = new TestReport();
            report.setTitle("SIM盾V1.0-测试报告");

            List<CapFile> capFiles = new ArrayList<>();
            capFiles.add(new CapFile("base.cap", ""));
            capFiles.add(new CapFile("app.cap", HashCodeUtil.hashCode("E:\\2020\\jces\\doc\\cmcc\\Jces 集成开发平台.xmind")));
            report.setCapFileList(capFiles);

            List<String> senApiList = new ArrayList<>();
            senApiList.add("com.ecp.Api1");
            senApiList.add("com.ecp.Api2");
            senApiList.add("com.ecp.Api3");
            senApiList.add("com.ecp.Api4");
            report.setSensitiveApiList(senApiList);

            List<String> toolkitEventList = new ArrayList<>();
            toolkitEventList.add("com.ecp.toolkit.api1");
            toolkitEventList.add("com.ecp.toolkit.api2");
            toolkitEventList.add("com.ecp.toolkit.api3");
            toolkitEventList.add("com.ecp.toolkit.api4");
            toolkitEventList.add("com.ecp.toolkit.api5");
            report.setToolkitEventList(toolkitEventList);

            report.setMaxPerTime("1ms");
            report.setTotalTime("160s");

            report.setLoadParam("FFDDFFIED");
            report.setInstallParam("EEFFFDDGGDD");

            report.setTestResult("通过");
            report.setTestTime("2021-09-06 12:34:11");

            genTestReport(out, report);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }*/

    public static void genTestReport(OutputStream out, TestReport report) throws PdfException {

        Document doc = new Document();
        try {
            doc = new Document();

            PdfWriter.getInstance(doc, out);
            BaseFont font = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font fontChina18 = new Font(font, 16, Font.BOLD);
            Font bFontChina12 = new Font(font, 12, Font.BOLD);
            Font fontChina12 = new Font(font, 12);

            // 空行
            Paragraph blank = new Paragraph(" ");


            // title
            Paragraph title = new Paragraph(report.getTitle(), fontChina18);
            title.setAlignment(Element.ALIGN_CENTER);

            //cap file
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);// 表格宽度为100%
            table.setSpacingBefore(5);

            // 购货单位
            PdfPCell cell1 = new PdfPCell();
            cell1.setBorderWidth(1);// Border宽度为1
            cell1.setPhrase(new Paragraph("CAP文件名", bFontChina12));
            cell1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
//            cell1.setExtraParagraphSpace(10);
            cell1.setFixedHeight(30);
            table.addCell(cell1);


            PdfPCell cell2 = new PdfPCell();
            cell2.setBorderWidth(1);
            cell2.setColspan(2);
            cell2.setPhrase(new Paragraph("HASH值", bFontChina12));
            cell2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
//            cell2.setExtraParagraphSpace(10);
            table.addCell(cell2);


            List<CapFile> capFileList = report.getCapFileList();
            if (capFileList != null) {
                for (CapFile capFile : report.getCapFileList()) {
                    PdfPCell capFileCell = new PdfPCell();
                    capFileCell.setBorderWidth(1);
                    capFileCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                    capFileCell.setPhrase(new Paragraph(capFile.getName(), fontChina12));
                    capFileCell.setFixedHeight(30);
                    table.addCell(capFileCell);

                    PdfPCell capFileCell1 = new PdfPCell();
                    capFileCell1.setBorderWidth(1);
                    capFileCell1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                    capFileCell1.setColspan(3);
                    capFileCell1.setPhrase(new Paragraph(capFile.getHash(), fontChina12));
                    table.addCell(capFileCell1);
                }
            }


            //检测信息
            PdfPTable testTable = new PdfPTable(3);
            testTable.setWidthPercentage(100);// 表格宽度为100%
            testTable.setSpacingBefore(5);

            // 兼容性测试结果
            StringBuilder cardBuf = new StringBuilder();
            List<String> cardList = report.getCardTestInfo();
            if (cardList != null && cardList.size() > 0) {
                for (String info : cardList) {
                    cardBuf.append(info).append("\n");
                }
            }
            _addTestItem(testTable, "兼容性测试：", cardBuf.toString());

            // 应用调用敏感API
            StringBuilder apiBuf = new StringBuilder();
            List<String> apiList = report.getSensitiveApiList();
            if (apiList != null && apiList.size() > 0) {
                for (String api : apiList) {
                    apiBuf.append(api).append("\n");
                }
            }
            _addTestItem(testTable, "应用调用敏感API：", apiBuf.toString());

            StringBuilder eventBuf = new StringBuilder();
            List<String> eventList = report.getToolkitEventList();
            if (eventList != null && eventList.size() > 0) {
                for (String event : eventList) {
                    eventBuf.append(event).append("\n");
                }
            }
            _addTestItem(testTable, "应用注册禁用toolkit事件：", eventBuf.toString());

            _addTestItem(testTable, "RAM(C7<1KB)：", report.getRam() + " byte");
            _addTestItem(testTable, "NVM(C6+C8<64KB)：", report.getNvm()+ " byte");

            _addTestItem(testTable, "应用下载单条指令最大时间(<5s)：", report.getMaxPerTime());
            _addTestItem(testTable, "应用加载、安装总时间(<60s)：", report.getTotalTime());
            _addTestItem(testTable, "应用加载参数(C7=0,C8<32KB)：", report.getLoadParam());
            _addTestItem(testTable, "应用安装参数(C7<1KB,C8<32KB)：", report.getInstallParam());
            _addTestItem(testTable, "检测结果：", report.getTestResult());
            _addTestItem(testTable, "检测时间", report.getTestTime());


            Chunk c1 = new Chunk("CAP文件信息：", bFontChina12);
            Paragraph capTitle = new Paragraph();
            capTitle.add(c1);
            capTitle.setAlignment(Element.ALIGN_LEFT);

            Chunk c2 = new Chunk("检测信息：", bFontChina12);
            Paragraph testTitle = new Paragraph();
            testTitle.add(c2);
            testTitle.setAlignment(Element.ALIGN_LEFT);


            doc.open();
            doc.add(title);

            doc.add(blank);
            doc.add(capTitle);
            doc.add(table);
            doc.add(blank);
            doc.add(testTitle);
            doc.add(testTable);

        } catch (DocumentException | IOException e) {
            throw new PdfException(e);
        } finally {
            doc.close();
        }
    }

    public static void genTestReport(OutputStream out, TestReport report, TemplateConfigVo templateConfigVo) throws PdfException {

        Document doc = new Document();
        try {
            doc = new Document();

            PdfWriter.getInstance(doc, out);
            BaseFont font = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font fontChina18 = new Font(font, 16, Font.BOLD);
            Font bFontChina12 = new Font(font, 12, Font.BOLD);
            Font fontChina12 = new Font(font, 12);

            // 空行
            Paragraph blank = new Paragraph(" ");


            // title
            Paragraph title = new Paragraph(report.getTitle(), fontChina18);
            title.setAlignment(Element.ALIGN_CENTER);

            //cap file
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);// 表格宽度为100%
            table.setSpacingBefore(5);

            // 购货单位
            PdfPCell cell1 = new PdfPCell();
            cell1.setBorderWidth(1);// Border宽度为1
            cell1.setPhrase(new Paragraph("CAP文件名", bFontChina12));
            cell1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
//            cell1.setExtraParagraphSpace(10);
            cell1.setFixedHeight(30);
            table.addCell(cell1);


            PdfPCell cell2 = new PdfPCell();
            cell2.setBorderWidth(1);
            cell2.setColspan(2);
            cell2.setPhrase(new Paragraph("HASH值", bFontChina12));
            cell2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
//            cell2.setExtraParagraphSpace(10);
            table.addCell(cell2);


            List<CapFile> capFileList = report.getCapFileList();
            if (capFileList != null) {
                for (CapFile capFile : report.getCapFileList()) {
                    PdfPCell capFileCell = new PdfPCell();
                    capFileCell.setBorderWidth(1);
                    capFileCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                    capFileCell.setPhrase(new Paragraph(capFile.getName(), fontChina12));
                    capFileCell.setFixedHeight(30);
                    table.addCell(capFileCell);

                    PdfPCell capFileCell1 = new PdfPCell();
                    capFileCell1.setBorderWidth(1);
                    capFileCell1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                    capFileCell1.setColspan(3);
                    capFileCell1.setPhrase(new Paragraph(capFile.getHash(), fontChina12));
                    table.addCell(capFileCell1);
                }
            }


            //检测信息
            PdfPTable testTable = new PdfPTable(3);
            testTable.setWidthPercentage(100);// 表格宽度为100%
            testTable.setSpacingBefore(5);

            // 非标API测试结果
            if(templateConfigVo.getCollects().contains(ConstantCode.NON_STANDARD_API_FLAG)){
                StringBuilder nonBuf = new StringBuilder();
                List<PackageInfo> nonList = report.getNonstandardApiList();
                if (nonList != null && nonList.size() > 0) {
                    for (PackageInfo pkg : nonList) {
                        nonBuf.append("AID: " + pkg.getAid() + " Name: " + pkg.getName()).append("\n");
                    }
                }
                _addTestItem(testTable, "非标API：", nonBuf.toString());
            }

            // 禁用API测试结果
            if(templateConfigVo.getCollects().contains(ConstantCode.DISABLED_API_FLAG)) {
                StringBuilder denyBuf = new StringBuilder();
                List<String> denyList = report.getDenyApiList();
                if (denyList != null && denyList.size() > 0) {
                    for (String info : denyList) {
                        denyBuf.append(info).append("\n");
                    }
                }
                _addTestItem(testTable, "禁用API：", denyBuf.toString());
            }
            // 应用调用敏感API
            if(templateConfigVo.getCollects().contains(ConstantCode.SENSITIVE_API_FLAG)) {
                StringBuilder apiBuf = new StringBuilder();
                List<String> apiList = report.getSensitiveApiList();
                if (apiList != null && apiList.size() > 0) {
                    for (String api : apiList) {
                        apiBuf.append(api).append("\n");
                    }
                }
                _addTestItem(testTable, "应用调用敏感API：", apiBuf.toString());
            }

            if(templateConfigVo.getCollects().contains(ConstantCode.TOOLKIT_FLAG)) {
                StringBuilder eventBuf = new StringBuilder();
                List<String> eventList = report.getToolkitEventList();
                if (eventList != null && eventList.size() > 0) {
                    for (String event : eventList) {
                        eventBuf.append(event).append("\n");
                    }
                }
                _addTestItem(testTable, "应用注册禁用toolkit事件：", eventBuf.toString());
            }
            if(templateConfigVo.getCollects().contains(ConstantCode.MVN_FLAG)) {
                _addTestItem(testTable, "RAM(C7<1KB)：", report.getRam() + " byte");
                _addTestItem(testTable, "NVM(C6+C8<64KB)：", report.getNvm() + " byte");
            }

            _addTestItem(testTable, "检测结果：", report.getTestResult());
            _addTestItem(testTable, "检测时间", report.getTestTime());


            Chunk c1 = new Chunk("CAP文件信息：", bFontChina12);
            Paragraph capTitle = new Paragraph();
            capTitle.add(c1);
            capTitle.setAlignment(Element.ALIGN_LEFT);

            Chunk c2 = new Chunk("检测信息：", bFontChina12);
            Paragraph testTitle = new Paragraph();
            testTitle.add(c2);
            testTitle.setAlignment(Element.ALIGN_LEFT);


            doc.open();
            doc.add(title);

            doc.add(blank);
            doc.add(capTitle);
            doc.add(table);
            doc.add(blank);
            doc.add(testTitle);
            doc.add(testTable);

        } catch (DocumentException | IOException e) {
            throw new PdfException(e);
        } finally {
            doc.close();
        }
    }



    private static void _addTestItem(PdfPTable table, String label, String val) throws IOException, DocumentException {

        if (StringUtils.isEmpty(label)) {
            return ;
        }

        BaseFont font = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font bFontChina12 = new Font(font, 12, Font.BOLD);
        Font fontChina12 = new Font(font, 12);

        PdfPCell labelCell = new PdfPCell();
        labelCell.setBorderWidth(1); // Border宽度为1
        labelCell.setPhrase(new Paragraph(label, bFontChina12));
        labelCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
//        labelCell.setFixedHeight(30);
        labelCell.setPadding(5);
        table.addCell(labelCell);


        PdfPCell valCell = new PdfPCell();
        valCell.setBorderWidth(1);
        valCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        valCell.setColspan(2);

        if (!StringUtils.isEmpty(val)) {
            valCell.setPhrase(new Paragraph(val, fontChina12));
        } else {
            valCell.setPhrase(new Paragraph("", fontChina12));
        }
        valCell.setPadding(5);
//        valCell.setFixedHeight(30);
        table.addCell(valCell);
    }

    public static class CapFile {
        private String name;
        private String hash;

        public CapFile(String name) {
            this.name = name;
        }

        public CapFile() {

        }

        public CapFile(String name, String hash) {
            this.name = name;
            this.hash = hash;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }
    }

    public static class TestReport {

        private String title; //报告标题，云平台上的应用名称+“测试报告”
        private List<CapFile> capFileList; //CAP文件
        private List<String> denyApiList; //禁用API
        private List<String> sensitiveApiList; //应用调用敏感API
        private List<String> toolkitEventList; //应用注册禁用toolkit事件
        private List<PackageInfo> nonstandardApiList;//非标API
        private String maxPerTime; //应用下载单条指令最大时间
        private String totalTime; //应用加载、安装总时间

        private String loadParam; //应用加载参数
        private String installParam; //应用安装参数

        private String testResult; //检测结果
        private String testTime; //检测时间

        private String ram; //RAM(C7)超过1KB为不通过
        private String nvm; //NVM(C6+C8)超过64KB为不通过

        private List<String> cardTestInfo; //兼容性测试结果

        public TestReport() {
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<CapFile> getCapFileList() {
            return capFileList;
        }

        public void setCapFileList(List<CapFile> capFileList) {
            this.capFileList = capFileList;
        }

        public List<String> getSensitiveApiList() {
            return sensitiveApiList;
        }

        public void setSensitiveApiList(List<String> sensitiveApiList) {
            this.sensitiveApiList = sensitiveApiList;
        }

        public List<String> getToolkitEventList() {
            return toolkitEventList;
        }

        public void setToolkitEventList(List<String> toolkitEventList) {
            this.toolkitEventList = toolkitEventList;
        }

        public String getMaxPerTime() {
            return maxPerTime;
        }

        public void setMaxPerTime(String maxPerTime) {
            this.maxPerTime = maxPerTime;
        }

        public String getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(String totalTime) {
            this.totalTime = totalTime;
        }

        public String getLoadParam() {
            return loadParam;
        }

        public void setLoadParam(String loadParam) {
            this.loadParam = loadParam;
        }

        public String getInstallParam() {
            return installParam;
        }

        public void setInstallParam(String installParam) {
            this.installParam = installParam;
        }

        public String getTestResult() {
            return testResult;
        }

        public void setTestResult(String testResult) {
            this.testResult = testResult;
        }

        public String getTestTime() {
            return testTime;
        }

        public void setTestTime(String testTime) {
            this.testTime = testTime;
        }

        public String getRam() {
            return ram;
        }

        public void setRam(String ram) {
            this.ram = ram;
        }

        public String getNvm() {
            return nvm;
        }

        public void setNvm(String nvm) {
            this.nvm = nvm;
        }

        public List<String> getCardTestInfo() {
            return cardTestInfo;
        }

        public void setCardTestInfo(List<String> cardTestInfo) {
            this.cardTestInfo = cardTestInfo;
        }

        public List<String> getDenyApiList() {
            return denyApiList;
        }

        public void setDenyApiList(List<String> denyApiList) {
            this.denyApiList = denyApiList;
        }

        public List<PackageInfo> getNonstandardApiList() {
            return nonstandardApiList;
        }

        public void setNonstandardApiList(List<PackageInfo> nonstandardApiList) {
            this.nonstandardApiList = nonstandardApiList;
        }
    }
}
