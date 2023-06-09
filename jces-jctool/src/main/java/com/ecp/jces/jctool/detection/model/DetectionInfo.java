package com.ecp.jces.jctool.detection.model;

import lombok.Data;

@Data
public class DetectionInfo {

    private PerformanceAnalysis performanceAnalysis;
    private Memory memory;
    private NonStandardApi nonStandardApi;

}
