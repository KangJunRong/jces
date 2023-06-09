package com.ecp.jces.jctool.shell.action;

import com.ecp.jces.jctool.Const;
import com.ecp.jces.jctool.shell.CmdProcessException;
import com.ecp.jces.jctool.shell.JcesShell;
import com.ecp.jces.jctool.shell.ScriptCommand;
import com.ecp.jces.jctool.util.CryptoException;
import com.ecp.jces.jctool.util.HexUtil;
import com.ecp.jces.jctool.util.StringUtil;

import java.util.StringTokenizer;

public class GpPutKeyAction extends GpAction {

	public void execute(String input) throws CmdProcessException {
		String[] params = input.split("\\s+");
		ISCP scp = getCurCad().getScp();
		
		if (scp == null) {
			getJcsv().appendLine("jcesshell: Command failed: No Auth.", JcesShell.CMD_STYLE_ERROR);
			return ;
		}
		
		if (params.length >= 2) {
			StringBuffer keySetData = new StringBuffer();
			byte keyVersion = -1;
			int keyCount = 0;
			byte starKeyId = -1;
			byte curkeyId = -1;
			StringTokenizer st = null;
			
			for (int i = 1; i < params.length; i++) {
				String param = params[i].trim();
				st = new StringTokenizer(param, "/");
				if (st.countTokens() != 4 || param.startsWith("/") || param.endsWith("/")) {
					getJcsv().appendLine("jcesshell: put-key: Malformed key - expecting: SET/ID/TYPE/DATA", JcesShell.CMD_STYLE_ERROR);
					return ;
				}
				
				String toKen = null;
				int index = 0;
				int set = 0;
				int id = 0;
				byte keyType = (byte)0x80;
				String key = null;
				byte[] keyData;
				int keyLen = 0;
				byte[] checkValueData;
				
				while (st.hasMoreTokens()) {
					toKen = st.nextToken().trim().toLowerCase();
					index++;
					switch (index) {
					case 1:
						try {
							set = Integer.parseInt(toKen);
							if (set > 127 || set < 0) {
								getJcsv().appendLine("jcesshell: Command failed: Illegal key set version (01-7F): " + set, JcesShell.CMD_STYLE_ERROR);
								return ;
							}
							
							if (keyVersion == -1) {
								keyVersion = (byte)set;
							} else {
								if (keyVersion != set) {
									getJcsv().appendLine("jcesshell: Command failed: All keys must be in key set " + set, JcesShell.CMD_STYLE_ERROR);
									return ;
								}
							}
						} catch (Exception ex) {
							getJcsv().appendLine("jcesshell: put-key: Malformed set:" + ex.getMessage(), JcesShell.CMD_STYLE_ERROR);
							return ;
						}
						break;
					case 2:
						try {
							id = Integer.parseInt(toKen);
							if (id > 127 || id < 0) {
								getJcsv().appendLine("jcesshell: Command failed: Illegal key index (01-7F): " + id, JcesShell.CMD_STYLE_ERROR);
								return ;
							}
							
							if (starKeyId == -1) {
								starKeyId = (byte)id;
								curkeyId = (byte)id;
							} else {
								if ((curkeyId + 1) != id) {
									getJcsv().appendLine("jcesshell: Command failed: Key are not sorted by index (lowest first)", JcesShell.CMD_STYLE_ERROR);
									return ;
								}
								curkeyId = (byte)id;
							}
						} catch (Exception ex) {
							getJcsv().appendLine("jcesshell: put-key: Malformed id:" + ex.getMessage(), JcesShell.CMD_STYLE_ERROR);
							return ;
						}
						break;
					case 3:
						if (scp instanceof SCP01 || scp instanceof SCP02) {
							if (!"des-cbc".equals(toKen) && !"des-ecb".equals(toKen)) {
								getJcsv().appendLine("jcesshell: put-key: Bad key type: " + toKen, JcesShell.CMD_STYLE_ERROR);
								return ;
							}
							keyType = (byte)0x80;
							
						} else if (scp instanceof SCP03) {
							if (!"aes".equals(toKen)) {
								getJcsv().appendLine("jcesshell: put-key: Bad key type: " + toKen, JcesShell.CMD_STYLE_ERROR);
								return ;
							}
							keyType = (byte)0x88;
						} else {
							getJcsv().appendLine("jcesshell: put-key: Bad key type: " + toKen, JcesShell.CMD_STYLE_ERROR);
							return ;
						}

						break;
					case 4:
						if (toKen.length() % 2 != 0) {
							getJcsv().appendLine("jcesshell: put-key: Illegal length of Hex encoding: 1 (not n*2)", JcesShell.CMD_STYLE_ERROR);
							return ;
						}
						
						if (!StringUtil.isHexString(toKen)) {
							getJcsv().appendLine("jcesshell: put-key: Illegal hex digits", JcesShell.CMD_STYLE_ERROR);
							return ;
						}
						
						if (scp instanceof SCP03) {
							if (toKen.length() != 32 && toKen.length() != 48 && toKen.length() != 64) {
								getJcsv().appendLine("jcesshell: Command failed: keysize must be equal to 128, 192 or 256.", JcesShell.CMD_STYLE_ERROR);
								return ;
							}
						} else {
							if (toKen.length() != 32) {
								getJcsv().appendLine("jcesshell: Command failed: 128 bit key required.", JcesShell.CMD_STYLE_ERROR);
								return ;
							}
						}
						key = toKen;
						break;
					}
				}
				keyCount++;
				//组装数据
				keySetData.append(HexUtil.byteToHex(keyType));
				try {
					keyLen = key.length() / 2;
					keyData = scp.keyEncryption(scp.padKey(hexStringToBytes(key))); 
					checkValueData = scp.keyCheckValue(hexStringToBytes(key));
					
					if (scp instanceof SCP03) {
						keySetData.append(HexUtil.intToHex(1 + keyData.length));
					}
					
					keySetData.append(HexUtil.intToHex(keyLen));
					keySetData.append(HexUtil.byteArr2HexStr(keyData));
					
					keySetData.append(HexUtil.intToHex(checkValueData.length));
					keySetData.append(HexUtil.byteArr2HexStr(checkValueData));
					
				} catch (CryptoException ex) {
					getJcsv().appendLine("jcesshell: Command failed: " + ex.getMessage(), JcesShell.CMD_STYLE_ERROR); 
					return ;
				}
			}
			
			StringBuffer apdu1 = new StringBuffer();
			apdu1.append("80D800");
			apdu1.append(HexUtil.byteToHex(getP2(starKeyId, keyCount)));
			apdu1.append(HexUtil.intToHex(keySetData.length() / 2 + 1));
			apdu1.append(HexUtil.byteToHex(keyVersion));
			apdu1.append(keySetData);
			
			ScriptCommand cmd = process(apdu1.toString());
			if (isNormalStatus(cmd.getStatus())) {
				getJcsv().appendLine("Add new key set didn't work, try modify ...",JcesShell.CMD_STYLE_ERROR);
				StringBuffer apdu2 = new StringBuffer();
				apdu2.append("80D8");
				apdu2.append(HexUtil.byteToHex(keyVersion));
				apdu2.append(HexUtil.byteToHex(getP2(starKeyId, keyCount)));
				apdu2.append(HexUtil.intToHex(keySetData.length() / 2 + 1));
				apdu2.append("00");
				apdu2.append(keySetData);
				cmd = process(apdu2.toString());
			}
		} else {
			getJcsv().appendLine("jcesshell: put-key: Missing mandatory argument: key", JcesShell.CMD_STYLE_ERROR);
		}
		

	}

	private byte getP2(byte keyId, int keyCount) {
		if (keyCount > 1) {
			return (byte)((byte)0x80 | keyId);
		} else {
			return keyId;
		}
	}
	
	public String getAction() {
		return Const.GP_CMD_PUT_KEY;
	}

	public void usage() {
		outUsage("put-key keydef|keyref..");
	}

}
