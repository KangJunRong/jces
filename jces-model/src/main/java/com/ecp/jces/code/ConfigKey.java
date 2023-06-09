package com.ecp.jces.code;

/**
 * @program: jces
 * @description: 参数配置Key
 * @author: KJR
 * @create: 2021-04-27 10:25
 **/
public class ConfigKey {
    public static final String PrivateKey = "7pA1OhvF7mi0rySCmnXQxZUpMdntbBeyEMtQ8860HfYh4qx9CTOpIkKN6YxUxoiP";
    /** dtr预期最大值*/
    public static final String MemoryDtrSizeExpect = "memoryDtrSizeExpect";
    /** rtr预期最大值*/
    public static final String MemoryRtrSizeExpect = "memoryRtrSizeExpect";
    /** 代码空间预期最大值*/
    public static final String MemoryCodeSizeExpect = "memoryCodeSizeExpect";
    /** 静态引用类型->基本类型数组初始化的变量数量预期最大值*/
    public static final String StaticReferenceArrayInitAmountExpect = "staticReferenceArrayInitAmountExpect";
    /** 静态引用类型->基本类型数组初始化的变量预期最大空间*/
    public static final String StaticReferenceArrayInitSpaceExpect = "staticReferenceArrayInitSpaceExpect";
    /** null值静态引用类型的变量预期最大数量*/
    public static final String StaticReferenceNullAmountExpect = "staticReferenceNullAmountExpect";
    /** null值静态引用类型的变量预期最大空间*/
    public static final String StaticReferenceNullSpaceExpect = "staticReferenceNullSpaceExpect";
    /** 基本类型->初始化为缺省值的变量预期最大数量*/
    public static final String StaticPrimitiveDefaultAmountExpect = "staticPrimitiveDefaultAmountExpect";
    /** 基本类型->初始化为缺省值的变量预期最大空间*/
    public static final String StaticPrimitiveDefaultSpaceExpect = "staticPrimitiveDefaultSpaceExpect";
    /** 基本类型->初始化为非缺省值的变量预期最大数量*/
    public static final String StaticPrimitiveNonDefaultAmountExpect = "staticPrimitiveNonDefaultAmountExpect";
    /** 基本类型->初始化为非缺省值的变量预期最大空间*/
    public static final String StaticPrimitiveNonDefaultSpaceExpect = "staticPrimitiveNonDefaultSpaceExpect";
    /** install过程new 对象的预期最大数量*/
    public static final String InstallNewAmountExpect = "installNewAmountExpect";
    /** install过程new 对象的预期最大空间*/
    public static final String InstallNewSpaceExpect = "installNewSpaceExpect";
    /** install过程new 数组的预期最大数量*/
    public static final String InstallNewArrayAmountExpect = "installNewArrayAmountExpect";
    /** install过程new 数组的预期最大空间*/
    public static final String InstallNewArraySpaceExpect = "installNewArraySpaceExpect";
    /** 开发者账号在同一时间内最多能提交多少个测试限制*/
    public static final String CommitTestMax = "commitTestMax";

    /** 应用加载、安装预期总时间*/
    public static final String downloadInstallTimeExpect = "downloadInstallTimeExpect";
    /** 应用下载单条指令最大时间*/
    public static final String downloadMaxTimeExpect = "downloadMaxTimeExpect";
    /** 应用加载参数C7*/
    public static final String loadC7MaxExpect = "loadC7MaxExpect";
    /** 应用加载参数C8*/
    public static final String loadC8MaxExpect = "loadC8MaxExpect";
    /** 应用安装参数C7*/
    public static final String installC7MaxExpect = "installC7MaxExpect";
    /** 应用安装参数C8*/
    public static final String installC8MaxExpect = "installC8MaxExpect";
}
