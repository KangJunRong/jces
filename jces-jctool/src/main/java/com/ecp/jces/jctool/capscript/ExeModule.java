package com.ecp.jces.jctool.capscript;

import lombok.Data;

import java.util.List;

@Data
public class ExeModule {

    private String aid;
    private String instanceAid;

    private List<AppletInstance> instanceList;

}
