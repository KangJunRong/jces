package com.eastcompeace.capAnalysis;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import com.eastcompeace.capAnalysis.doman.Exp;
import com.eastcompeace.capAnalysis.doman.Exp.ClassInfo;
import com.eastcompeace.capAnalysis.doman.Exp.ConstantClassrefInfo;
import com.eastcompeace.capAnalysis.doman.Exp.ConstantIntegerInfo;
import com.eastcompeace.capAnalysis.doman.Exp.ConstantPackageInfo;
import com.eastcompeace.capAnalysis.doman.Exp.ConstantUtf8Info;
import com.eastcompeace.capAnalysis.doman.Exp.ConstantValueAttribute;
import com.eastcompeace.capAnalysis.doman.Exp.CpInfo;
import com.eastcompeace.capAnalysis.doman.Exp.FileInfo;
import com.eastcompeace.capAnalysis.doman.Exp.MethodInfo;

public class ExpAnalysisUtil {

	/**
	public static void main(String[] args) {
		//String testFilePathString = "F:\\runtime-EclipseApplication\\com.ecp.testcap\\bin\\com\\ecp\\testcap\\javacard\\testcap.exp";
		//String testFilePathString = "F:\\UpdateSite\\testjar\\testcap.exp";
		//String testFilePathString = "F:\\workspace_ecp\\develop\\ide\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files\\org\\globalplatform\\javacard\\globalplatform.exp";
		
		//String testFilePathString = "D:\\测试完删除\\api\\javacardAPI\\javacard\\framework\\javacard\\framework.exp";
//		String testFilePathString = "F:\\demo\\captest\\javacardAPI\\javacard\\framework\\javacard\\framework.exp";
//		String stringsString = "D:\\DEVELOPE\\JCES1.3\\plugins\\com.eastcompeace.jces.eclipse_2.2.9.202206221534\\apis\\jc221\\api_export_files\\javacard\\framework\\javacard\\framework.exp";
//		String string ="D:\\DEVELOPE\\JCES1.3\\plugins\\com.eastcompeace.jces.eclipse_2.2.9.202206221534\\apis\\jc30\\api_export_files\\javacard\\framework\\javacard\\framework.exp";
//		String string2 = "F:\\workspace_ecp\\develop\\ide\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files_cmcc\\uicc\\toolkit\\javacard\\toolkit.exp";
//		String rmiexpString = "F:\\workspace_ecp\\develop\\ide\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files_cmcc\\java\\rmi\\javacard\\rmi.exp";
//		String serviceExpString = "F:\\workspace_ecp\\develop\\ide\\Jces_ide_2.1.0\\simuls\\JCWDE\\api_export_files_cmcc\\javacard\\framework\\service\\javacard\\service.exp";
//		Exp analysis = analysis(new File(serviceExpString));
		
	}*/
	
	
	public static Exp analysisWithCapPackageAid(File file, byte[] package_AID) {
		FileInputStream fileInputStream = null;
		DataInputStream dataInputStream = null;
			try {
				Exp exp = new Exp();
				fileInputStream = new FileInputStream(file);
				dataInputStream = new DataInputStream(fileInputStream);
				exp.magic = StreamUtils.dataRead(dataInputStream, 4);
				exp.minor_version = StreamUtils.dataRead(dataInputStream, 1);
				exp.major_version = StreamUtils.dataRead(dataInputStream, 1);
				exp.constant_pool_count = StreamUtils.dataRead(dataInputStream, 2);
				
				println(0,"magic:"+HexUtil.byteArr2HexStr(exp.magic));
				println(0,"minor_version:"+HexUtil.byteArr2HexStr(exp.minor_version));
				println(0,"major_version:"+HexUtil.byteArr2HexStr(exp.major_version));
				println(0,"constant_pool_count:"+HexUtil.byteArr2HexStr(exp.constant_pool_count));

				int constant_pool_count = HexUtil.byteArrayToInt(exp.constant_pool_count);
				println(1,"constantPool:"+constant_pool_count);
				exp.constant_pool = new ArrayList<>();
				for (int i = 0; i < constant_pool_count; i++) {
					CpInfo cpInfo = exp.new CpInfo();
					byte[] tag = StreamUtils.dataRead(dataInputStream, 1);
					cpInfo.tag = tag;
					println(0,"  tag:"+HexUtil.byteArr2HexStr(tag)+" -"+i);
					
					if(tag[0]==0x0d) {
						ConstantPackageInfo constantPackageInfo = exp.new ConstantPackageInfo();
						constantPackageInfo.tag=tag;
						constantPackageInfo.flags=StreamUtils.dataRead(dataInputStream, 1);
						constantPackageInfo.name_index=StreamUtils.dataRead(dataInputStream, 2);
						constantPackageInfo.minor_version=StreamUtils.dataRead(dataInputStream, 1);
						constantPackageInfo.major_version=StreamUtils.dataRead(dataInputStream, 1);
						constantPackageInfo.aid_length=StreamUtils.dataRead(dataInputStream, 1);
						
						int aidLength = HexUtil.byteArrayToInt(constantPackageInfo.aid_length);
						constantPackageInfo.aid=StreamUtils.dataRead(dataInputStream, aidLength);
						cpInfo.CONSTANT_Package = constantPackageInfo;
					}else if(tag[0]==0x07) {
						ConstantClassrefInfo constantClassrefInfo = exp.new ConstantClassrefInfo();
						constantClassrefInfo.tag = tag;
						constantClassrefInfo.name_index = StreamUtils.dataRead(dataInputStream, 2);
						cpInfo.CONSTANT_Classref = constantClassrefInfo; 
					}else if(tag[0]==0x03) {
						ConstantIntegerInfo constantIntegerInfo = exp.new ConstantIntegerInfo();
						constantIntegerInfo.tag = tag;
						constantIntegerInfo.tag = StreamUtils.dataRead(dataInputStream, 4);
						cpInfo.CONSTANT_Integer = constantIntegerInfo;
					}else if(tag[0]==0x01) {
						ConstantUtf8Info constantUtf8Info = exp.new ConstantUtf8Info();
						constantUtf8Info.tag = tag;
						constantUtf8Info.length = StreamUtils.dataRead(dataInputStream, 2);
						int length = HexUtil.byteArrayToInt(constantUtf8Info.length);
						constantUtf8Info.bytes = StreamUtils.dataRead(dataInputStream,length);
						constantUtf8Info.bytesStr = new String(constantUtf8Info.bytes,StandardCharsets.UTF_8);
						println(0,"  bytesStr:"+constantUtf8Info.bytesStr);
						cpInfo.CONSTANT_Utf8 = constantUtf8Info;
					}
					exp.constant_pool.add(cpInfo);
				}
				//把packageAid,packageName获取到
				exp.this_package = StreamUtils.dataRead(dataInputStream, 2);
				
				CpInfo packageAidCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(exp.this_package));
				if (packageAidCpInfo != null && packageAidCpInfo.tag[0] == 0x0d) {
					exp.this_package_AID = packageAidCpInfo.CONSTANT_Package.aid;
					//如果与传入的packageAid不一致，就终止比对 
					if (!Arrays.equals(package_AID, exp.this_package_AID)) {
						return null;
					}
					println(1,"this_package_AID:" + HexUtil.byteArr2HexStr(exp.this_package_AID));
					CpInfo packageNameCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(packageAidCpInfo.CONSTANT_Package.name_index));
					if (packageNameCpInfo != null && packageNameCpInfo.tag[0] == 0x01) {
						exp.this_package_name = packageNameCpInfo.CONSTANT_Utf8.bytes;
						exp.this_package_name_str = new String(exp.this_package_name,StandardCharsets.UTF_8);
						println(0,"this_package_name:" + HexUtil.byteArr2HexStr(exp.this_package_name));
						println(1,"this_package_name_str:" + new String(exp.this_package_name,StandardCharsets.UTF_8));
					}
				}

				exp.export_class_count = StreamUtils.dataRead(dataInputStream, 1);
				int export_class_count = HexUtil.byteArrayToInt(exp.export_class_count);
				println(1,"ClassInfo:"+export_class_count);
				exp.classes = new ArrayList<>();
				for (int i = 0; i < export_class_count; i++) {
					ClassInfo classInfo = exp.new ClassInfo();
					classInfo.token = StreamUtils.dataRead(dataInputStream, 1);
					classInfo.access_flags = StreamUtils.dataRead(dataInputStream, 2);
					classInfo.name_index = StreamUtils.dataRead(dataInputStream, 2);
					
					println(1,"  ========");
					println(1,"  classInfo.token:"+HexUtil.byteArr2HexStr(classInfo.token));
					println(0,"  classInfo.access_flags:"+HexUtil.byteArr2HexStr(classInfo.access_flags));
					println(0,"  classInfo.name_index:"+HexUtil.byteArr2HexStr(classInfo.name_index));
					
					//constantpoll中通过classInfo.name_index找到CONSTANT_Classref
					//constantpoll中通过CONSTANT_Classref.name_index找到CONSTANT_Utf8.bytes
					CpInfo classNameIndexCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(classInfo.name_index));
					if (classNameIndexCpInfo != null && classNameIndexCpInfo.tag[0] == 0x07) {
						byte[] class_name_index = classNameIndexCpInfo.CONSTANT_Classref.name_index;
						CpInfo classNameCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(class_name_index));
						if (classNameCpInfo != null && classNameCpInfo.tag[0] == 0x01) {
							classInfo.class_name = classNameCpInfo.CONSTANT_Utf8.bytes;
							classInfo.class_name_str = new String(classInfo.class_name,StandardCharsets.UTF_8);
							println(0,"  classInfo.class_name:" + HexUtil.byteArr2HexStr(classInfo.class_name));
							println(1,"  classInfo.class_name_str:" + new String(classInfo.class_name,StandardCharsets.UTF_8));
						}
						
					}
					
					
					
					
					classInfo.export_supers_count = StreamUtils.dataRead(dataInputStream, 2);				
					int export_supers_count = HexUtil.byteArrayToInt(classInfo.export_supers_count);
					classInfo.supers = new ArrayList<>();
					for(int a=0;a<export_supers_count;a++) {
						byte[] superByte = StreamUtils.dataRead(dataInputStream, 2);
						classInfo.supers.add(superByte);
					}
					
					classInfo.export_interfaces_count = StreamUtils.dataRead(dataInputStream, 1);				
					int export_interfaces_count = HexUtil.byteArrayToInt(classInfo.export_interfaces_count);
					classInfo.interfaces = new ArrayList<>();
					for(int a=0;a<export_interfaces_count;a++) {
						byte[] interfaceByte = StreamUtils.dataRead(dataInputStream, 2);
						classInfo.interfaces.add(interfaceByte);
					}
					
					classInfo.export_fields_count = StreamUtils.dataRead(dataInputStream, 2);				
					int export_fields_count = HexUtil.byteArrayToInt(classInfo.export_fields_count);
					classInfo.fields = new ArrayList<>();
					for(int a=0;a<export_fields_count;a++) {
						FileInfo fileInfo = exp.new FileInfo();
						fileInfo.token = StreamUtils.dataRead(dataInputStream, 1);	
						fileInfo.access_flags = StreamUtils.dataRead(dataInputStream, 2);	
						fileInfo.name_index = StreamUtils.dataRead(dataInputStream, 2);	
						fileInfo.descriptor_index = StreamUtils.dataRead(dataInputStream, 2);	
						fileInfo.attributes_count = StreamUtils.dataRead(dataInputStream, 2);	

						int attributes_count = HexUtil.byteArrayToInt(fileInfo.attributes_count);
						fileInfo.attributes = new ArrayList<>();
						for(int b=0;b<attributes_count;b++ ) {
							ConstantValueAttribute attributeInfo = exp.new ConstantValueAttribute();
							attributeInfo.attribute_name_index = StreamUtils.dataRead(dataInputStream, 2);	
							attributeInfo.attribute_length = StreamUtils.dataRead(dataInputStream, 4);
							attributeInfo.constantvalue_index = StreamUtils.dataRead(dataInputStream, 2);
							fileInfo.attributes.add(attributeInfo);
						}
						classInfo.fields.add(fileInfo);
					}
					
					classInfo.export_methods_count = StreamUtils.dataRead(dataInputStream, 2);				
					int export_methods_count = HexUtil.byteArrayToInt(classInfo.export_methods_count);
					classInfo.methods = new ArrayList<>();
					for(int a=0;a<export_methods_count;a++) {
						MethodInfo methodInfo = exp.new MethodInfo();
						methodInfo.token = StreamUtils.dataRead(dataInputStream, 1);
						methodInfo.access_flags = StreamUtils.dataRead(dataInputStream, 2);		
						methodInfo.name_index = StreamUtils.dataRead(dataInputStream, 2);
						methodInfo.descriptor_index = StreamUtils.dataRead(dataInputStream, 2);
						
						println(1,"    -------");
						println(1,"    methodInfo.token:"+HexUtil.byteArr2HexStr(methodInfo.token));
						println(0,"    methodInfo.access_flags:"+HexUtil.byteArr2HexStr(methodInfo.access_flags));
						println(0,"    methodInfo.name_index:"+HexUtil.byteArr2HexStr(methodInfo.name_index));
						
						CpInfo methodNameCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(methodInfo.name_index));
						if (methodNameCpInfo != null && methodNameCpInfo.tag[0] == 0x01) {
							methodInfo.method_name = methodNameCpInfo.CONSTANT_Utf8.bytes;
							methodInfo.method_name_str = new String(methodInfo.method_name,StandardCharsets.UTF_8);
							println(0,"    methodInfo.method_name:" + HexUtil.byteArr2HexStr(methodInfo.method_name));
							println(1,"    methodInfo.method_name_str:" + new String(methodInfo.method_name,StandardCharsets.UTF_8));
						}
						
						println(1,"    methodInfo.descriptor_index:"+HexUtil.byteArr2HexStr(methodInfo.descriptor_index));						
						CpInfo descriptorCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(methodInfo.descriptor_index));
						if (descriptorCpInfo != null && descriptorCpInfo.tag[0] == 0x01) {
							methodInfo.method_descriptor = descriptorCpInfo.CONSTANT_Utf8.bytes;
							methodInfo.method_descriptor_str = new String(methodInfo.method_descriptor,StandardCharsets.UTF_8);
							println(0,"    methodInfo.method_descriptor:" + HexUtil.byteArr2HexStr(methodInfo.method_descriptor));
							println(1,"    methodInfo.method_descriptor_str:" + new String(methodInfo.method_descriptor,StandardCharsets.UTF_8));
						}
						
						
						classInfo.methods.add(methodInfo);
					}
					exp.classes.add(classInfo);
				}
				return exp;

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
	
	
	public static Exp analysis(File file) {
		FileInputStream fileInputStream = null;
		DataInputStream dataInputStream = null;
			try {
				Exp exp = new Exp();
				fileInputStream = new FileInputStream(file);
				dataInputStream = new DataInputStream(fileInputStream);
				exp.magic = StreamUtils.dataRead(dataInputStream, 4);
				exp.minor_version = StreamUtils.dataRead(dataInputStream, 1);
				exp.major_version = StreamUtils.dataRead(dataInputStream, 1);
				exp.constant_pool_count = StreamUtils.dataRead(dataInputStream, 2);
				
				println(0,"magic:"+HexUtil.byteArr2HexStr(exp.magic));
				println(0,"minor_version:"+HexUtil.byteArr2HexStr(exp.minor_version));
				println(0,"major_version:"+HexUtil.byteArr2HexStr(exp.major_version));
				println(0,"constant_pool_count:"+HexUtil.byteArr2HexStr(exp.constant_pool_count));

				int constant_pool_count = HexUtil.byteArrayToInt(exp.constant_pool_count);
				println(1,"constantPool:"+constant_pool_count);
				exp.constant_pool = new ArrayList<>();
				for (int i = 0; i < constant_pool_count; i++) {
					CpInfo cpInfo = exp.new CpInfo();
					byte[] tag = StreamUtils.dataRead(dataInputStream, 1);
					cpInfo.tag = tag;
					println(0,"  tag:"+HexUtil.byteArr2HexStr(tag)+" -"+i);
					
					if(tag[0]==0x0d) {
						ConstantPackageInfo constantPackageInfo = exp.new ConstantPackageInfo();
						constantPackageInfo.tag=tag;
						constantPackageInfo.flags=StreamUtils.dataRead(dataInputStream, 1);
						constantPackageInfo.name_index=StreamUtils.dataRead(dataInputStream, 2);
						constantPackageInfo.minor_version=StreamUtils.dataRead(dataInputStream, 1);
						constantPackageInfo.major_version=StreamUtils.dataRead(dataInputStream, 1);
						constantPackageInfo.aid_length=StreamUtils.dataRead(dataInputStream, 1);
						
						int aidLength = HexUtil.byteArrayToInt(constantPackageInfo.aid_length);
						constantPackageInfo.aid=StreamUtils.dataRead(dataInputStream, aidLength);
						cpInfo.CONSTANT_Package = constantPackageInfo;
					}else if(tag[0]==0x07) {
						ConstantClassrefInfo constantClassrefInfo = exp.new ConstantClassrefInfo();
						constantClassrefInfo.tag = tag;
						constantClassrefInfo.name_index = StreamUtils.dataRead(dataInputStream, 2);
						cpInfo.CONSTANT_Classref = constantClassrefInfo; 
					}else if(tag[0]==0x03) {
						ConstantIntegerInfo constantIntegerInfo = exp.new ConstantIntegerInfo();
						constantIntegerInfo.tag = tag;
						constantIntegerInfo.tag = StreamUtils.dataRead(dataInputStream, 4);
						cpInfo.CONSTANT_Integer = constantIntegerInfo;
					}else if(tag[0]==0x01) {
						ConstantUtf8Info constantUtf8Info = exp.new ConstantUtf8Info();
						constantUtf8Info.tag = tag;
						constantUtf8Info.length = StreamUtils.dataRead(dataInputStream, 2);
						int length = HexUtil.byteArrayToInt(constantUtf8Info.length);
						constantUtf8Info.bytes = StreamUtils.dataRead(dataInputStream,length);
						constantUtf8Info.bytesStr = new String(constantUtf8Info.bytes,StandardCharsets.UTF_8);
						println(0,"  bytesStr:"+constantUtf8Info.bytesStr);
						cpInfo.CONSTANT_Utf8 = constantUtf8Info;
					}
					exp.constant_pool.add(cpInfo);
				}
				//把packageAid,packageName获取到
				exp.this_package = StreamUtils.dataRead(dataInputStream, 2);
				
				CpInfo packageAidCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(exp.this_package));
				if (packageAidCpInfo != null && packageAidCpInfo.tag[0] == 0x0d) {
					exp.this_package_AID = packageAidCpInfo.CONSTANT_Package.aid;
					println(1,"this_package_AID:" + HexUtil.byteArr2HexStr(exp.this_package_AID));
					CpInfo packageNameCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(packageAidCpInfo.CONSTANT_Package.name_index));
					if (packageNameCpInfo != null && packageNameCpInfo.tag[0] == 0x01) {
						exp.this_package_name = packageNameCpInfo.CONSTANT_Utf8.bytes;
						exp.this_package_name_str = new String(exp.this_package_name,StandardCharsets.UTF_8);
						println(0,"this_package_name:" + HexUtil.byteArr2HexStr(exp.this_package_name));
						println(1,"this_package_name_str:" + new String(exp.this_package_name,StandardCharsets.UTF_8));
					}
				}

				exp.export_class_count = StreamUtils.dataRead(dataInputStream, 1);
				int export_class_count = HexUtil.byteArrayToInt(exp.export_class_count);
				println(1,"ClassInfo:"+export_class_count);
				exp.classes = new ArrayList<>();
				for (int i = 0; i < export_class_count; i++) {
					ClassInfo classInfo = exp.new ClassInfo();
					classInfo.token = StreamUtils.dataRead(dataInputStream, 1);
					classInfo.access_flags = StreamUtils.dataRead(dataInputStream, 2);
					classInfo.name_index = StreamUtils.dataRead(dataInputStream, 2);
					
					println(1,"  ========");
					println(1,"  classInfo.token:"+HexUtil.byteArr2HexStr(classInfo.token));
					println(0,"  classInfo.access_flags:"+HexUtil.byteArr2HexStr(classInfo.access_flags));
					println(0,"  classInfo.name_index:"+HexUtil.byteArr2HexStr(classInfo.name_index));
					
					//constantpoll中通过classInfo.name_index找到CONSTANT_Classref
					//constantpoll中通过CONSTANT_Classref.name_index找到CONSTANT_Utf8.bytes
					CpInfo classNameIndexCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(classInfo.name_index));
					if (classNameIndexCpInfo != null && classNameIndexCpInfo.tag[0] == 0x07) {
						byte[] class_name_index = classNameIndexCpInfo.CONSTANT_Classref.name_index;
						CpInfo classNameCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(class_name_index));
						if (classNameCpInfo != null && classNameCpInfo.tag[0] == 0x01) {
							classInfo.class_name = classNameCpInfo.CONSTANT_Utf8.bytes;
							classInfo.class_name_str = new String(classInfo.class_name,StandardCharsets.UTF_8);
							println(0,"  classInfo.class_name:" + HexUtil.byteArr2HexStr(classInfo.class_name));
							println(1,"  classInfo.class_name_str:" + new String(classInfo.class_name,StandardCharsets.UTF_8));
						}
						
					}
					
					
					
					
					classInfo.export_supers_count = StreamUtils.dataRead(dataInputStream, 2);				
					int export_supers_count = HexUtil.byteArrayToInt(classInfo.export_supers_count);
					classInfo.supers = new ArrayList<>();
					for(int a=0;a<export_supers_count;a++) {
						byte[] superByte = StreamUtils.dataRead(dataInputStream, 2);
						classInfo.supers.add(superByte);
					}
					
					classInfo.export_interfaces_count = StreamUtils.dataRead(dataInputStream, 1);				
					int export_interfaces_count = HexUtil.byteArrayToInt(classInfo.export_interfaces_count);
					classInfo.interfaces = new ArrayList<>();
					for(int a=0;a<export_interfaces_count;a++) {
						byte[] interfaceByte = StreamUtils.dataRead(dataInputStream, 2);
						classInfo.interfaces.add(interfaceByte);
					}
					
					classInfo.export_fields_count = StreamUtils.dataRead(dataInputStream, 2);				
					int export_fields_count = HexUtil.byteArrayToInt(classInfo.export_fields_count);
					classInfo.fields = new ArrayList<>();
					for(int a=0;a<export_fields_count;a++) {
						FileInfo fileInfo = exp.new FileInfo();
						fileInfo.token = StreamUtils.dataRead(dataInputStream, 1);	
						fileInfo.access_flags = StreamUtils.dataRead(dataInputStream, 2);	
						fileInfo.name_index = StreamUtils.dataRead(dataInputStream, 2);	
						fileInfo.descriptor_index = StreamUtils.dataRead(dataInputStream, 2);	
						fileInfo.attributes_count = StreamUtils.dataRead(dataInputStream, 2);	

						int attributes_count = HexUtil.byteArrayToInt(fileInfo.attributes_count);
						fileInfo.attributes = new ArrayList<>();
						for(int b=0;b<attributes_count;b++ ) {
							ConstantValueAttribute attributeInfo = exp.new ConstantValueAttribute();
							attributeInfo.attribute_name_index = StreamUtils.dataRead(dataInputStream, 2);	
							attributeInfo.attribute_length = StreamUtils.dataRead(dataInputStream, 4);
							attributeInfo.constantvalue_index = StreamUtils.dataRead(dataInputStream, 2);
							fileInfo.attributes.add(attributeInfo);
						}
						classInfo.fields.add(fileInfo);
					}
					
					classInfo.export_methods_count = StreamUtils.dataRead(dataInputStream, 2);				
					int export_methods_count = HexUtil.byteArrayToInt(classInfo.export_methods_count);
					classInfo.methods = new ArrayList<>();
					for(int a=0;a<export_methods_count;a++) {
						MethodInfo methodInfo = exp.new MethodInfo();
						methodInfo.token = StreamUtils.dataRead(dataInputStream, 1);
						methodInfo.access_flags = StreamUtils.dataRead(dataInputStream, 2);		
						methodInfo.name_index = StreamUtils.dataRead(dataInputStream, 2);
						methodInfo.descriptor_index = StreamUtils.dataRead(dataInputStream, 2);
						
						println(1,"    -------");
						println(1,"    methodInfo.token:"+HexUtil.byteArr2HexStr(methodInfo.token));
						println(0,"    methodInfo.access_flags:"+HexUtil.byteArr2HexStr(methodInfo.access_flags));
						println(0,"    methodInfo.name_index:"+HexUtil.byteArr2HexStr(methodInfo.name_index));
						
						CpInfo methodNameCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(methodInfo.name_index));
						if (methodNameCpInfo != null && methodNameCpInfo.tag[0] == 0x01) {
							methodInfo.method_name = methodNameCpInfo.CONSTANT_Utf8.bytes;
							methodInfo.method_name_str = new String(methodInfo.method_name,StandardCharsets.UTF_8);
							println(0,"    methodInfo.method_name:" + HexUtil.byteArr2HexStr(methodInfo.method_name));
							println(1,"    methodInfo.method_name_str:" + new String(methodInfo.method_name,StandardCharsets.UTF_8));
						}
						
						println(1,"    methodInfo.descriptor_index:"+HexUtil.byteArr2HexStr(methodInfo.descriptor_index));						
						CpInfo descriptorCpInfo = exp.constant_pool.get(HexUtil.byteArrayToInt(methodInfo.descriptor_index));
						if (descriptorCpInfo != null && descriptorCpInfo.tag[0] == 0x01) {
							methodInfo.method_descriptor = descriptorCpInfo.CONSTANT_Utf8.bytes;
							methodInfo.method_descriptor_str = new String(methodInfo.method_descriptor,StandardCharsets.UTF_8);
							println(0,"    methodInfo.method_descriptor:" + HexUtil.byteArr2HexStr(methodInfo.method_descriptor));
							println(1,"    methodInfo.method_descriptor_str:" + new String(methodInfo.method_descriptor,StandardCharsets.UTF_8));
						}
						
						
						classInfo.methods.add(methodInfo);
					}
					exp.classes.add(classInfo);
				}
				return exp;

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
	
	
	private static void println(int flag,Object o) {
		if(flag==1) {
			Log.println(o);
		}
		
	}



}
