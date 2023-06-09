package com.eastcompeace.capAnalysis;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.eastcompeace.capAnalysis.doman.CapCheckException;
import com.eastcompeace.capAnalysis.doman.ImportCap;
import com.eastcompeace.capAnalysis.doman.ImportCap.PackageInfo;

public class ImportCapAnalysisUtil {

	/**
	
	public static void main(String[] args) {
		try {
			analysis(new File("F:\\runtime-EclipseApplication\\test3.0\\bin\\com\\ecp\\test30\\javacard\\javacard\\Import.cap"));
		} catch (CapCheckException e) {
			Log.println(e);
		}
	}*/
	
	
	public static ImportCap analysis(File file) throws CapCheckException {
		//分析ImporCap.cap
		
		FileInputStream fileInputStream = null;
		DataInputStream dataInputStream = null;
		try {
		if(file==null) {
			throw new CapCheckException("Import.Cap文件不能为空");
		}
		if(!file.exists()) {
			throw new CapCheckException("Import.Cap文件不存在");
		}
		 fileInputStream = new FileInputStream(file);
		
		 dataInputStream = new DataInputStream(fileInputStream);
		 
		 ImportCap importCap = new ImportCap();
		 //跳过3个字节 tag、size
		 importCap.tag = StreamUtils.dataRead(dataInputStream, 1);
		 importCap.size = StreamUtils.dataRead(dataInputStream, 2);
		//读取2个字节 count
		 importCap.count= StreamUtils.dataRead(dataInputStream, 1);
		 //byte转int
		 int count = HexUtil.byteArrayToInt(importCap.count);
		 
		 importCap.packages = new ArrayList<>();
		 
		 for(int ic=0;ic<count;ic++) {			
			 PackageInfo packageInfo = importCap.new PackageInfo();
			 packageInfo.minor_version = StreamUtils.dataRead(dataInputStream, 1);
			 packageInfo.major_version = StreamUtils.dataRead(dataInputStream, 1);
			 packageInfo.AID_length = StreamUtils.dataRead(dataInputStream, 1);
			 int AID_length = HexUtil.byteArrayToInt(packageInfo.AID_length);
			 packageInfo.AID = StreamUtils.dataRead(dataInputStream, AID_length);
			 importCap.packages.add(packageInfo);
			 Log.println("packageInfo.AID:" +HexUtil.byteArr2HexStr(packageInfo.AID));
		 }
		 return importCap;
		 
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
}
