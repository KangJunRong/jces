package com.eastcompeace.capAnalysis;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.eastcompeace.capAnalysis.doman.ConstantPoolCap;
import com.eastcompeace.capAnalysis.doman.ImportCap;
import com.eastcompeace.capAnalysis.doman.ConstantPoolCap.CpInfo;

public class ConstantPoolCapAnalysisUtil {



	public static ConstantPoolCap analysis(File file, ImportCap importantCap) {
		// 分析ConstantPool.cap
		FileInputStream fileInputStream = null;
		DataInputStream dataInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			
			dataInputStream = new DataInputStream(fileInputStream);

			ConstantPoolCap constantPoolCap = new ConstantPoolCap();
			// 跳过3个字节 tag、size
			constantPoolCap.tag = StreamUtils.dataRead(dataInputStream, 1);
			constantPoolCap.size = StreamUtils.dataRead(dataInputStream, 2);
			// 读取2个字节 count
			constantPoolCap.count = StreamUtils.dataRead(dataInputStream, 2);
			// byte转int
			int count = HexUtil.byteArrayToInt(constantPoolCap.count);

			constantPoolCap.constant_pool = new ArrayList<>();
			
			for (int ic = 0; ic < count; ic++) {
				CpInfo cpInfo = constantPoolCap.new CpInfo();
				cpInfo.tag = StreamUtils.dataRead(dataInputStream, 1);
				cpInfo.package_token = StreamUtils.dataRead(dataInputStream, 1);
				println(1,"tag:"+HexUtil.byteArr2HexStr(cpInfo.tag));
				
				if (cpInfo.tag[0] == 0x01|cpInfo.tag[0] == 0x03 | cpInfo.tag[0] == 0x04 | cpInfo.tag[0] == 0x06) {
					// CONSTANT_VirtualMethodref、CONSTANT_SuperMethodref
					if ("1".equals(HexUtil.max(cpInfo.package_token[0]))) {
						// 最高位为1，外部引用，分析import.cap,0为内部引用，不用分析
						cpInfo.class_token = StreamUtils.dataRead(dataInputStream, 1);
						cpInfo.token = StreamUtils.dataRead(dataInputStream, 1);
						byte[] package_token = { (byte) (cpInfo.package_token[0] ^ 0x80) };
						int packageIndex = HexUtil.byteArrayToInt(package_token);
						cpInfo.package_AID = importantCap.packages.get(packageIndex).AID;
						constantPoolCap.constant_pool.add(cpInfo);

						println(1,"---------"); 
						println(0,"package_token:" +HexUtil.byteArr2HexStr(cpInfo.package_token));
						println(0,"packageIndex:"+packageIndex);

						
						println(1,"package_AID:" +HexUtil.byteArr2HexStr(cpInfo.package_AID));
						println(1,"class_token:" +HexUtil.byteArr2HexStr(cpInfo.class_token));
						println(1,"token:"+HexUtil.byteArr2HexStr(cpInfo.token));

					}else {
						 StreamUtils.dataRead(dataInputStream, 2);
					}
				}else {
					StreamUtils.dataRead(dataInputStream, 2);
				}
			}
			return constantPoolCap;

		} catch (FileNotFoundException e) {
			Log.println(e);
		} catch (IOException e) {
			Log.println(e);
		}finally {
			
			try {
				if (dataInputStream != null) {
					dataInputStream.close();
					dataInputStream = null;
				}
			} catch (IOException e) {
				Log.println(e);
			}
			
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
					fileInputStream = null;
				}
			} catch (IOException e) {
				Log.println(e);
			}

			
		}
		return null;
	}

	private static void println(int flag, Object o) {
		if (flag == 1) {
			Log.println(o);
		}

	}

}
