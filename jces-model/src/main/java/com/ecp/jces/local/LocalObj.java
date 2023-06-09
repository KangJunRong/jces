package com.ecp.jces.local;


import com.ecp.jces.vo.UserVo;
import lombok.Data;

import java.util.Date;

@Data
public class LocalObj {

	private UserVo user;

	private Date curr;
}
