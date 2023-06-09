package com.ecp.jces.vo.extra;

import com.ecp.jces.jctool.capscript.ExeLoadFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;

/**
 * cap报上传返回的信息
 * @author kangjunrong
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AppletLoadFileVo {
    private String aid;
    private Integer type;
    private String moduleAid;
    private String instanceAid;
}
