package com.ecp.jces.vo;

import com.ecp.jces.jctool.detection.model.PackageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TestCheckReportVo extends BaseVo {
    /**
     * 主键
     */
    private String id;

    /**
     * 应用ID
     */
    private String appletId;

    /**
     * CAP版本ID
     */
    private String appletVersionId;

    /**
     * dtr实际大小
     */
    private Integer memoryDtrSize;

    /**
     * dtr预期最大值
     */
    private Integer memoryDtrSizeExpect;

    /**
     * rtr实际大小
     */
    private Integer memoryRtrSize;

    /**
     * rtr预期最大值
     */
    private Integer memoryRtrSizeExpect;

    /**
     * 代码空间大小
     */
    private Integer memoryCodeSize;

    /**
     * 代码空间预期最大值
     */
    private Integer memoryCodeSizeExpect;

    /**
     * 静态引用类型->基本类型数组初始化的变量实际数量
     */
    private Integer staticReferenceArrayInitAmount;

    /**
     * 静态引用类型->基本类型数组初始化的变量数量预期最大值
     */
    private Integer staticReferenceArrayInitAmountExpect;

    /**
     * 静态引用类型->基本类型数组初始化的变量实际空间
     */
    private Integer staticReferenceArrayInitSpace;

    /**
     * 静态引用类型->基本类型数组初始化的变量预期最大空间
     */
    private Integer staticReferenceArrayInitSpaceExpect;

    /**
     * null值静态引用类型的变量实际数量
     */
    private Integer staticReferenceNullAmount;

    /**
     * null值静态引用类型的变量预期最大数量
     */
    private Integer staticReferenceNullAmountExpect;

    /**
     * null值静态引用类型的变量实际空间
     */
    private Integer staticReferenceNullSpace;

    /**
     * null值静态引用类型的变量预期最大空间
     */
    private Integer staticReferenceNullSpaceExpect;

    /**
     * 基本类型->初始化为缺省值的变量实际数量
     */
    private Integer staticPrimitiveDefaultAmount;

    /**
     * 基本类型->初始化为缺省值的变量预期最大数量
     */
    private Integer staticPrimitiveDefaultAmountExpect;

    /**
     * 基本类型->初始化为缺省值的变量实际空间
     */
    private Integer staticPrimitiveDefaultSpace;

    /**
     * 基本类型->初始化为缺省值的变量预期最大空间
     */
    private Integer staticPrimitiveDefaultSpaceExpect;

    /**
     * 基本类型->初始化为非缺省值的变量实际数量
     */
    private Integer staticPrimitiveNonDefaultAmount;

    /**
     * 基本类型->初始化为非缺省值的变量预期最大数量
     */
    private Integer staticPrimitiveNonDefaultAmountExpect;

    /**
     * 基本类型->初始化为非缺省值的变量实际空间
     */
    private Integer staticPrimitiveNonDefaultSpace;

    /**
     * 基本类型->初始化为非缺省值的变量预期最大空间
     */
    private Integer staticPrimitiveNonDefaultSpaceExpect;

    /**
     * install过程new 对象的实际数量
     */
    private Integer installNewAmount;

    /**
     * install过程new 对象的预期最大数量
     */
    private Integer installNewAmountExpect;

    /**
     * install过程new 对象的实际空间
     */
    private Integer installNewSpace;

    /**
     * install过程new 对象的预期最大空间
     */
    private Integer installNewSpaceExpect;

    /**
     * install过程new 数组的实际数量
     */
    private Integer installNewArrayAmount;

    /**
     * install过程new 数组的预期最大数量
     */
    private Integer installNewArrayAmountExpect;

    /**
     * install过程new 数组的实际空间
     */
    private Integer installNewArraySpace;

    /**
     * install过程new 数组的预期最大空间
     */
    private Integer installNewArraySpaceExpect;

    /**
     * 非标API
     */
    private String nonstandardApi;

    /**
     * 非标API(冗余)
     */
    private List<PackageInfo> nonstandardApiList;

    /**
     * 删除标记(0=正常,1=删除)
     */
    private Short delFlg;

    /**
     * 更新用户
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 敏感API
     */
    private String sensitiveApi;
    private List<String> sensitiveApis;

    /**
     * 应用调用禁用注册toolkit 事件
     */
    private String eventList;
    private List<String> eventToolKit;

    private Integer downloadInstallTimeExpect;
    private Integer downloadMaxTimeExpect;
    private Integer loadC7MaxExpect;
    private Integer loadC8MaxExpect;
    private Integer installC7MaxExpect;
    private Integer installC8MaxExpect;
}