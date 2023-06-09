package com.ecp.jces.form.extra;

import com.ecp.jces.vo.AppletExeLoadFileVo;
import com.ecp.jces.vo.extra.SendTestDataVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * KJR
 */
@Getter
@Setter
public class StartTestForm extends SendTestDataVo {
	private String appletId;
	private String appletVersionId;
	private String capPath;
	private Integer capVersion;
	private String capName;
	private String commonScriptPath;
	private Integer commonScriptVersion;
	private String commonScriptName;
	private String commonLogPath;
	private String customizeScriptPath;
	private Integer customizeScriptVersion;
	private String customizeScriptName;
	private String customizeLogPath;
	private String taskResult;
	private String commonResult;
	private String customizeResult;

	private String matrixId;

	private Long timeFlag;
	private String errorMsg;
	private List<AppletExeLoadFileVo> loadFiles;
}
