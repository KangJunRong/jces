package com.ecp.jces.jctool.detection.model;

import lombok.Data;

@Data
public class PerformanceAnalysis {

    private Integer referenceArrayInitAmount; //静态引用类型->基本类型数组初始化的变量实际数量
    private Integer referenceArrayInitSpace; //静态引用类型->基本类型数组初始化的变量实际空间

    private Integer referenceNullAmount; //null值静态引用类型的变量实际数量
    private Integer referenceNullSpace; //null值静态引用类型的变量实际空间

    private Integer referenceAmount; // = referenceArrayInitAmount + referenceNullAmount
    private Integer referenceSpace; // = referenceArrayInitSpace + referenceNullSpace

    private Integer primitiveDefaultAmount; //基本类型->初始化为缺省值的变量实际数量
    private Integer primitiveDefaultSpace; //基本类型->初始化为缺省值的变量实际空间

    private Integer primitiveNonDefaultAmount; //基本类型->初始化为非缺省值的变量实际数量
    private Integer primitiveNonDefaultSpace; //基本类型->初始化为非缺省值的变量实际空间

    private Integer primitiveAmount;
    private Integer primitiveSpace;

//    private Integer installNewAmount;
//    private Integer installNewSpace;

}
