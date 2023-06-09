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
public class CapUploadVo {
    private LinkedList<ExeLoadFile> loadFiles;
    private String path;


}
