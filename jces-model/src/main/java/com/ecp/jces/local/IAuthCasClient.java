package com.ecp.jces.local;


import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.vo.UserVo;
import com.ecp.jces.exception.FrameworkRuntimeException;

public interface IAuthCasClient {
	default LocalObj get() throws FrameworkRuntimeException {
		return AuthCasClient.get();
	}
	default UserVo getUser() throws FrameworkRuntimeException {
		return AuthCasClient.get().getUser();
	}
	default String getUserId() throws FrameworkRuntimeException {
		return getUser().getId();
	}
}
