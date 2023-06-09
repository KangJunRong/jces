package com.eastcompeace.capAnalysis;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eastcompeace.capAnalysis.doman.API;
import com.eastcompeace.capAnalysis.doman.AnalysResult;
import com.eastcompeace.capAnalysis.doman.CapCheckException;
import com.eastcompeace.capAnalysis.doman.ConstantPoolCap;
import com.eastcompeace.capAnalysis.doman.Exp;
import com.eastcompeace.capAnalysis.doman.ImportCap;
import com.eastcompeace.capAnalysis.doman.ConstantPoolCap.CpInfo;
import com.eastcompeace.capAnalysis.doman.Exp.ClassInfo;
import com.eastcompeace.capAnalysis.doman.Exp.MethodInfo;

public class CapAnalysisUtil {
	
	/*
	
	public static void main(String[] args) {
		//String capFilePathString = "F:\\runtime-EclipseApplication\\wallet\\bin\\com\\eastcompeace\\wallet\\javacard\\wallet.cap";
		String capFilePathString = "F:\\runtime-EclipseApplication\\test3.0\\bin\\com\\ecp\\test30\\javacard";
		//String capFilePathString2 = "F:\\runtime-EclipseApplication\\test3.0\\bin\\com\\ecp\\test30\\javacard\\jcesCapTemp\\com\\ecp\\test30\\javacard";
		String expFilePath = "F:\\workspace_ecp\\develop\\ide\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files_cmcc";

		
		try {
			long start = System.currentTimeMillis();
			List<CpInfo> cpInfos = loadCap(capFilePathString, expFilePath);
			if(cpInfos.size()>0) {
				File file = new File(capFilePathString);
				File[] listFiles = file.listFiles();
				for(File cFile:listFiles) {
				Log.println(cFile.delete());
				}
			}
			Log.println("tast time:"+(System.currentTimeMillis()-start)/1000);
			Log.println(cpInfos.size());
		} catch (CapCheckException e) {
			Log.println(e);
		}
		
		//checkCap(capFilePathString, expFilePath, disApis);
		
	}*/
	
	public static AnalysResult checkCap(String capfilePath, String expFilePath, List<API> disableApis) {

		// 比对licence
		// 删除解压文件？依赖java是否需要全部解析
		try {
			Set<API> useDisableApis = new HashSet<>();
			List<CpInfo> cpInfos = loadCap(capfilePath, expFilePath);

			for (API disableApi : disableApis) {
				String classNameString = disableApi.getPackageName()+"/"+disableApi.getClassName();
				Log.println("classNameString: "+classNameString);
				for (CpInfo cpInfo : cpInfos) {
					Log.println("----cpInfo: "+cpInfo.toString());
					if (!disableApi.getPackageName().equals(cpInfo.packageName)) {
						continue;
					}
					
					
					if(disableApi.getClassName().equals("*")) {
						API api = new API(cpInfo.packageName, cpInfo.className, cpInfo.methodName, cpInfo.methodDescriptor);
						useDisableApis.add(api);
						continue;
					}
					
					if (!classNameString.equals(cpInfo.className)) {
						continue;
					}
					
					if(disableApi.getMethodName().equals("*")) {
						API api = new API(cpInfo.packageName, cpInfo.className, cpInfo.methodName,  cpInfo.methodDescriptor);
						useDisableApis.add(api);
						continue;
					}
					if (!disableApi.getMethodName().equals(cpInfo.methodName)) {
						continue;
					}
					
					if(disableApi.getDescriptor().equals("*")||disableApi.getDescriptor().equals("")||disableApi.getDescriptor()==null) {
						API api = new API(cpInfo.packageName, cpInfo.className, cpInfo.methodName,   cpInfo.methodDescriptor);
						useDisableApis.add(api);
						continue;
					}
					if (!disableApi.getDescriptor().equals(cpInfo.methodDescriptor)) {
						continue;
					}

					API api = new API(cpInfo.packageName, cpInfo.className, cpInfo.methodName,   cpInfo.methodDescriptor);
					useDisableApis.add(api);
				}
			}
			return new AnalysResult(0, "", useDisableApis);
		} catch (CapCheckException e) {
			Log.println(e);
			return new AnalysResult(1, e.getMessage(), null);
		}
	}

	public static List<CpInfo> loadCap(String capfilePath, String expFilePath) throws CapCheckException {

		// 1、解压，获取默认解压路径
		
		String unZipPath = unZipAppPackage(capfilePath);

		// 2、直接获取解析需要的文件
		List<File> loadFiles = getuseCapFiles(unZipPath);

		// 3、精确取出文件
		File constantPoolCapFile = null;
		File imporCapFile = null;

		for (int i = 0; i < loadFiles.size(); i++) {
			File file = loadFiles.get(i);
			if (file.getName().endsWith("ConstantPool.cap")) {
				constantPoolCapFile = file;

			} else if (file.getName().endsWith("Import.cap")) {
				imporCapFile = file;
			}
		}

		// 4、对ConstantPool.cap 和 Import.cap 进行分析
		ImportCap importantCap = ImportCapAnalysisUtil.analysis(imporCapFile);
		ConstantPoolCap constantPoolCap = ConstantPoolCapAnalysisUtil.analysis(constantPoolCapFile, importantCap);

		// 5、获取 exp类型的引用文件
		List<File> allexpFiles = getAllexpFiles(expFilePath);

		List<CpInfo> cpInfos = constantPoolCap.constant_pool;

		// constantPoolCap与exp解析文件比对，获取方法名和参数

		// package层

		for (File expFile : allexpFiles) {
			Exp exp = ExpAnalysisUtil.analysis(expFile);
			for (CpInfo cpInfo : cpInfos) {				
				if (Arrays.equals(cpInfo.package_AID, exp.this_package_AID)) {
					// 关联packageName
					cpInfo.packageName = exp.this_package_name_str;

					Log.println("cpInfo_package_AID:" + HexUtil.byteArr2HexStr(cpInfo.package_AID) + "=="
							+ HexUtil.byteArr2HexStr(exp.this_package_AID));
					// 关联class
					List<ClassInfo> expClassInfos = exp.classes;
					if (expClassInfos != null && expClassInfos.size() > 0) {
						for (ClassInfo classInfo : expClassInfos) {
							if (Arrays.equals(cpInfo.class_token, classInfo.token)) {
								cpInfo.className = classInfo.class_name_str;

								// 关联方法与方法描述（参数/返回值）
								List<MethodInfo> methods = classInfo.methods;
								if (methods != null && methods.size() > 0) {
									for (MethodInfo methodInfo : methods) {
										if (Arrays.equals(cpInfo.token, methodInfo.token)) {
											if(Arrays.equals( methodInfo.token, new byte[]{0x00})) {
												
												if (cpInfo.methodName != null && cpInfo.methodName != "") {
													cpInfo.methodName += "/" + methodInfo.method_name_str;
												}else {
													cpInfo.methodName = methodInfo.method_name_str;
												}

												if (cpInfo.methodDescriptor != null && cpInfo.methodDescriptor != "") {
													cpInfo.methodDescriptor += "/" + methodInfo.method_descriptor_str;
												}else {
													cpInfo.methodDescriptor = methodInfo.method_descriptor_str;
												}

												continue;
											}
											cpInfo.methodName = methodInfo.method_name_str;
											cpInfo.methodDescriptor = methodInfo.method_descriptor_str;
										}
									}
								}

							}
						}
					}

					Log.println(cpInfo.toString());
				}
			}
		}
		ZipUtil.remove(new File(unZipPath));
		return cpInfos;

	}

   	/**
	 * 解压文件
	 * 
	 * @param capPath
	 * @return
	 * @throws CapCheckException
	 */
	public static String unZipAppPackage(String capPath) throws CapCheckException {
		File file = new File(capPath);
		String outDir = capPath +File.separator+"jcesCapTemp";
		if (!file.exists()) {
			throw new CapCheckException("目录不存在");
		}
		if (!file.isDirectory()) {
			throw new CapCheckException("capPath需为.cap文件所在目录");
		}

		File[] listFiles = file.listFiles();
		for (File childFile : listFiles) {
			if (childFile.getPath().endsWith(".cap")) {
				try {
					// 解压文件
					ZipUtil.unzip(childFile, outDir);
					// 加载文件
					return outDir;
				} catch (Exception ex) {
					throw new CapCheckException("解压cap文件出错！", ex);
				}
			}
		}

		return outDir;

	}
   

	/**
	 * 分析cap包
	 * 
	 * @param capPath
	 * @return
	 * @throws CapCheckException
	 */
	private static List<File> getuseCapFiles(String capString) throws CapCheckException {

		File capFile = new File(capString);
		List<File> loadFiles = new ArrayList<>();
		List<File> allFileList = new ArrayList<>();

		getAllFile(capFile, allFileList);

		for (int i = 0; i < allFileList.size(); i++) {
			File file = allFileList.get(i);
			String fileName = file.getName();
			if (fileName.endsWith("ConstantPool.cap") | fileName.endsWith("Import.cap")) {
				loadFiles.add(file);
			}
		}
		// 检查应用包
		if (loadFiles.size() > 12) {
			throw new CapCheckException("应用包最多包含12个cap文件！");
		}
		return loadFiles;
	}

	public static void getAllFile(File outDir, List<File> allFileList) {
		// 获取文件列表
		File[] fileList = outDir.listFiles();
		assert fileList != null;
		for (File file : fileList) {
			if (file.isDirectory()) {
				// 递归处理文件夹
				// 如果不想统计子文件夹则可以将下一行注释掉
				getAllFile(file, allFileList);
			} else {
				// 如果是文件则将其加入到文件数组中
				allFileList.add(file);
			}
		}
	}

	/**
	 * 获取所有的expFile
	 * @param outDir
	 * @return
	 */
	public static List<File> getAllexpFiles(String expDir) {
		File expFile = new File(expDir);
		List<File> expFiles = new ArrayList<>();
		List<File> allFileList = new ArrayList<>();

		getAllFile(expFile, allFileList);

		for (int i = 0; i < allFileList.size(); i++) {
			File file = allFileList.get(i);
			String fileName = file.getName();
			if (fileName.endsWith(".exp")) {
				expFiles.add(file);
			}
		}
		return expFiles;
	}
   
	
	

}
