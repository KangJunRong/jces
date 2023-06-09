package com.ecp.jces.vo.extra;
import com.ecp.jces.vo.TestCardVo;
import com.ecp.jces.vo.TestEngineReaderVo;
import com.ecp.jces.vo.TestEngineVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SendTestDataVo{
    private String testScheduleId;
    private String testTaskId;
    /**卡信息 **/
    private TestCardVo testCardVo;
    /**读卡器 **/
    private TestEngineReaderVo testEngineReaderVo;
    /**测试引擎信息 **/
    private TestEngineVo testEngineVo;
    /**1=通用脚本测试，2=业务脚本测试，3=两者都测 **/
    private Integer testContent;
    /**冗余 当前测试脚本类型 1=通用脚本测试，2=业务脚本测试**/
    private Integer currentType;

}
