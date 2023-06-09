package com.ecp.jces.file.service.auth;

import com.eastcompeace.capAnalysis.doman.API;
import com.ecp.jces.exception.FrameworkRuntimeException;
import com.ecp.jces.form.extra.FileAuth;
import com.ecp.jces.form.extra.VmCos;

import java.util.List;

public interface AuthService {

    void auth(FileAuth fileAuth) throws FrameworkRuntimeException;

    List<API> authAndCheckApi(FileAuth fileAuth) throws FrameworkRuntimeException;

    Boolean checkIp(String ip);

    void vmCosDownAuth(VmCos vmCos);
}
