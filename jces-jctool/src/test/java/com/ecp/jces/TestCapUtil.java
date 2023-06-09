package com.ecp.jces;

import com.ecp.jces.jctool.capscript.exception.ApplicationCheckException;
import com.ecp.jces.jctool.util.CapUtil;
import org.junit.Test;

public class TestCapUtil {

    @Test
    public void testAnalysisAppPackage() {
        try {
            CapUtil.analysisAppPackage("D:\\project\\cmcc_jces_tools_test\\hbcx1\\hbcx1.zip");
        } catch (ApplicationCheckException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testAnalysisAppPackage_() {
        try {
            CapUtil.analysisAppPackage("D:\\project\\cmcc_jces_tools_test\\test_11\\ydjtykt.zip");
        } catch (ApplicationCheckException e) {
            e.printStackTrace();
        }
    }
}
