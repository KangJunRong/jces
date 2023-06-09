package com.ecp.jces.jctool.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ecp.jces.jctool.detection.model.BaseConstant;
import com.ecp.jces.jctool.detection.model.PackageConstant;
import com.ecp.jces.jctool.detection.model.Utf8Constant;
import com.ecp.jces.jctool.exception.ExpAnalysisException;

public class ExpUtil {

	public static void analysisExp(File expFile, Map<String, String> expMap) throws ExpAnalysisException {
		
		try (DataInputStream dataIn = new DataInputStream(new FileInputStream(expFile))){
			dataIn.readInt(); //magic
			dataIn.readByte(); //minjor
			dataIn.readByte(); //major
			
			int count = dataIn.readUnsignedShort();
			
			BaseConstant[] cp = new BaseConstant[count];
			for (int i = 0; i < count; i++) {
				BaseConstant bc = BaseConstant.newInstance(dataIn);
				cp[i] = bc;
				
				if (bc == null) {
					throw new ExpAnalysisException("分析exp文件出错！");
				}
				
				bc.analysis(dataIn);
			}
			
			int pkgIndex = dataIn.readUnsignedShort();
			
			if (cp[pkgIndex] instanceof PackageConstant) {
				PackageConstant pc = (PackageConstant) cp[pkgIndex];
//				System.out.println("AID: " + HexUtil.byteArr2HexStr(pc.getAid()));
				
				if (cp[pc.getNameIndex()] instanceof Utf8Constant) {
					Utf8Constant uc = (Utf8Constant) cp[pc.getNameIndex()];
//					System.out.println("name: " + new String(uc.getBytes(), "utf-8"));
					
					expMap.put(HexUtil.byteArr2HexStr(pc.getAid()), new String(uc.getBytes(), "utf-8"));
				}
				
			} else {
				throw new ExpAnalysisException("分析exp文件出错！");
			}
			
		} catch (FileNotFoundException ex) {
			throw new ExpAnalysisException("找不到exp文件！", ex);
		} catch (IOException ex) {
			throw new ExpAnalysisException("分析exp文件出错！", ex);
		} catch (ExpAnalysisException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ExpAnalysisException("未知错误", ex);
		}
		
	}
	
	public static void scanExp(File folder, Map<String, String> expMap) throws ExpAnalysisException {
		
		File[] files = folder.listFiles();
		String fileName;
		for (File file : files) {
			fileName = file.getName();
			if (file.isDirectory()) {
//				System.out.println(file.getPath());
				scanExp(file, expMap);
			} else {
				if (fileName != null && fileName.toLowerCase().endsWith(".exp")) {
//					System.out.println(file.getPath());
					
					analysisExp(file, expMap);
				}
			}
		}
	}
	
	public static Map<String, String> loadExp(String path) throws ExpAnalysisException {
		File rootDir = new File(path);
		Map<String, String> expMap = new HashMap<>();
		
		if (!rootDir.exists()) {
			return expMap;
		}
		
		
		scanExp(rootDir, expMap);
		return expMap;

	}

	public static void main(String[] args) {
		try {
			Map<String, String> expMap = loadExp("D:\\project\\jces\\code\\cmcc\\tools\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files_cmcc");
			
			Set<String> keySet = expMap.keySet();
			for (String key : keySet) {
				System.out.println("AID: " + key + "  name: " + expMap.get(key));
			}
		} catch (ExpAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
