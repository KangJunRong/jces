package com.ecp.jces.vo.extra;

import com.ecp.jces.jctool.detection.model.PackageInfo;
import com.ecp.jces.jctool.detection.model.PerformanceAnalysis;
import com.ecp.jces.jctool.simulator.InstallItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

/**
 * Java Card 数据
 * @author kangjunrong
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JavaCardDataVo {
    private InstallItem installItem;
    private PerformanceAnalysis performanceAnalysis;
    private List<PackageInfo> packageInfoList;
    private String testTaskId;
    private String matrixId;
    private String matrixStatus;
    /**敏感API*/
    private Set<String> sensitiveApiSet;

}
