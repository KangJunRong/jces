package com.ecp.jces.jctool.capscript;

import lombok.Data;

import java.util.List;

@Data
public class ExeLoadFile {

    private String aid;
    private String fileName;

    private String loadParam;

    private String path;
    private String hash;

    private List<ExeLoadFile> importList;
    private List<ExeModule> exeModuleList;

    public boolean isLibPkg() {
        if (exeModuleList != null && exeModuleList.size() > 0) {
            return false;
        }
        return true;
    }

}
