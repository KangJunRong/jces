package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.core.Key;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.util.StringUtil;

import java.util.StringTokenizer;

public class GpSetKeyAction extends GpAction {

	public void execute(String input) throws CmdProcessException {
		if (getCurCad() == null)
			return;

		String[] inputAry = input.split("\\s+");
		if (inputAry.length >= 2) {
			if (validate(inputAry)) {
				for (int i = 1; i < inputAry.length; i++) {
					Key key = new Key(inputAry[i]);
					getCurCad().setKey(key);
				}
			}

		} else {
			getJcsv().appendLine("jcesshell: set-key: Expecting at least 1 keydef arguments", JcesShell.CMD_STYLE_RES);
		}
	}

	public boolean validate(String[] params) {
		StringTokenizer st = null;
		for (int i = 1; i < params.length; i++) {
			String param = params[i].trim();
			st = new StringTokenizer(param, "/");
			if (st.countTokens() != 4 || param.startsWith("/")
					|| param.endsWith("/")) {
				getJcsv().appendLine(
								"jcesshell: set-key: Malformed key - expecting: SET/ID/TYPE/DATA",
								JcesShell.CMD_STYLE_ERROR);
				return false;
			}

			String toKen = null;
			int index = 0;
			int set = 0;
			int id = 0;
			while (st.hasMoreTokens()) {
				toKen = st.nextToken().trim().toLowerCase();
				index++;
				switch (index) {
				case 1:
					try {
						set = Integer.parseInt(toKen);
						if (set > 255 || set < 1) {
							getJcsv().appendLine("jcesshell: Command failed: Key identifier out of range", JcesShell.CMD_STYLE_ERROR);
							return false;
						}
					} catch (Exception ex) {
						getJcsv().appendLine("jcesshell: set-key: Malformed set:"
								+ ex.getMessage(), JcesShell.CMD_STYLE_ERROR);
						return false;
					}
					break;
				case 2:
					try {
						id = Integer.parseInt(toKen);
						if (id > 255 || id < 1) {
							getJcsv().appendLine(
									"jcesshell: Command failed: Illegal key index (1-255): "
											+ id, JcesShell.CMD_STYLE_ERROR);
							return false;
						}

					} catch (Exception ex) {
						getJcsv().appendLine("jcesshell: set-key: Malformed id:"
								+ ex.getMessage(), JcesShell.CMD_STYLE_ERROR);
						return false;
					}
					break;
				case 3:
					if (!"des-cbc".equals(toKen) && !"des-ecb".equals(toKen) && !"aes".equals(toKen)) {
						getJcsv().appendLine("jcesshell: set-key: Bad key type: "
								+ toKen, JcesShell.CMD_STYLE_ERROR);
						return false;
					}
					break;
				case 4:
					if (toKen.length() % 2 != 0) {
						getJcsv().appendLine(
										"jcesshell: set-key: Illegal length of Hex encoding: 1 (not n*2)",
										JcesShell.CMD_STYLE_ERROR);
						return false;
					}

					if (!StringUtil.isHexString(toKen)) {
						getJcsv().appendLine(
								"jcesshell: set-key: Illegal hex digits",
								JcesShell.CMD_STYLE_ERROR);
						return false;
					}

					if (!(toKen.length() != 32 || toKen.length() != 16 || toKen.length() != 24)) {
						getJcsv().appendLine("jcesshell: Command failed: keysize must be equal to 128, 192 or 256.",
										JcesShell.CMD_STYLE_ERROR);
						return false;
					}
					break;
				}
			}

		}

		return true;
	}

	public String getAction() {
		return Const.GP_CMD_SET_KEY;
	}

	public void usage() {
		outUsage("set-key keydef..");
	}

}
