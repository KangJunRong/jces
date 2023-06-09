package com.eastcompeace.capAnalysis;

import java.io.*;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {
	
	/**
	public static void main(String[] args) {
		String fileString = "F://ecpfile//cap";
		System.getenv(fileString);
		System.out.println(filter("F://ecpfile//cap"));
		File file = new File("F:\\\\runtime-EclipseApplication\\\\test3.0\\\\bin\\\\com\\\\ecp\\\\test30\\\\javacard\\\\test30.cap");
		String outPutPathString =  "F:\\runtime-EclipseApplication\\test3.0\\bin\\com\\ecp\\test30\\javacard\\test30unzip";
		String outPutPathString1 =  "F:\\runtime-EclipseApplication\\test3.011\\bin\\com\\ecp\\test30\\javacard\\test30unzip";

		unzip(file,outPutPathString1);
		
	}*/
	private static int BUFFER = 1024;

	public static void unzip(File zipFile, String zipOutDir) {


		if (!zipOutDir.endsWith(File.separator)) {
			zipOutDir += File.separator;
		}

		String name = "";
		File zipOutDirFile = new File(zipOutDir);
		if (zipOutDirFile.exists()) {
			remove(zipOutDirFile);
		}
		boolean mkdirs = zipOutDirFile.mkdirs();
		if(!mkdirs) {
			return ;
		}
		

		ZipFile zipfile  = null;
		try {
			ZipEntry entry;
			zipfile = new ZipFile(zipFile);

			Enumeration dir = zipfile.entries();
			while (dir.hasMoreElements()) {
				entry = (ZipEntry) dir.nextElement();

				if (entry.isDirectory()) {
					name = entry.getName();
					name = name.substring(0, name.length() - 1);
					File fileObject = new File(zipOutDir + name);
					fileObject.mkdir();
				}
			}

			Enumeration e = zipfile.entries();
			while (e.hasMoreElements()) {
				entry = (ZipEntry) e.nextElement();
				if (entry.isDirectory()) {
					continue;
				} else {
					BufferedOutputStream dest = null;
					BufferedInputStream is = null;
					FileOutputStream fos = null;
					try {

						is = new BufferedInputStream(zipfile.getInputStream(entry));
						int count;
						byte[] dataByte = new byte[BUFFER];
						File file = new File(zipOutDir + entry.getName());
						if (!file.getParentFile().exists()) {
							boolean mkdirsP = file.getParentFile().mkdirs();
							if (!mkdirsP) {
								continue;
							}
						}

						fos = new FileOutputStream(zipOutDir + entry.getName());
						dest = new BufferedOutputStream(fos, BUFFER);
						while ((count = is.read(dataByte, 0, BUFFER)) != -1) {
							dest.write(dataByte, 0, count);
						}
						dest.flush();
					} catch (IOException ec) {

					} finally {
						dest.close();
						is.close();
						fos.close();
					}

				}
			}

		} catch (IOException e) {
			Log.println(e);
		} finally {

			try {
				if (zipFile != null) {
					zipfile.close();
					zipFile = null;
				}
			} catch (IOException e) {
				Log.println(e);
			}
		}
	}

	

	/**
	 * 删除指定文件夹下的全部内容
	 * 
	 * @param file
	 */
	public static void remove(File file) {
		File[] files = file.listFiles();// 将file子目录及子文件放进文件数组
		if (files != null) {// 如果包含文件进行删除操作
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {// 删除子文件
					boolean delete = files[i].delete();
				} else if (files[i].isDirectory()) {// 通过递归方法删除子目录的文件
					remove(files[i]);					
				}
				boolean delete = files[i].delete();// 删除子目录
			}
		}
		boolean delete = file.delete();
	}
	
	
	public static String filter(String data) {
		Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
		Matcher matcher = pattern.matcher(data);
		data = matcher.replaceAll("");
		return data;
	}

}
