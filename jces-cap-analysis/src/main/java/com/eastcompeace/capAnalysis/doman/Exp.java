package com.eastcompeace.capAnalysis.doman;

import java.util.List;
/**
 * ExportFile {
	u4 magic
	u1 minor_version
	u1 major_version
	u2 constant_pool_count
	cp_info constant_pool[constant_pool_count]
	u2 this_package
	u1 export_class_count
	class_info classes[export_class_count]
	}

 * @author LiQing
 *
 */
public class Exp {
	
	
	//public Map constantPool;

	public byte[] magic;
	public byte[] minor_version;
	public byte[] major_version;
	public byte[] constant_pool_count;
	public List<CpInfo> constant_pool;
	public byte[] this_package;
	//自定义
	public byte[] this_package_AID;
	public byte[] this_package_name;
	
	public byte[] export_class_count;
	public List<ClassInfo> classes;
	
	public String this_package_name_str;
	
	/**
	 cp_info {
		u1 tag
		u1 info[]
		}
		CONSTANT_Package 13
		CONSTANT_Classref 7
		CONSTANT_Integer 3
		CONSTANT_Utf8 1
	 */
	public class CpInfo{
		public byte[] tag;
		public byte[] info;
		
		public ConstantPackageInfo CONSTANT_Package;
		public ConstantClassrefInfo CONSTANT_Classref;
		public ConstantIntegerInfo CONSTANT_Integer;
		public ConstantUtf8Info CONSTANT_Utf8;
	}
	
	
	/**
	 CONSTANT_Package_info {
		u1 tag
		u1 flags
		u2 name_index
		u1 minor_version
		u1 major_version
		u1 aid_length
		u1 aid[aid_length]
	 } 
	 */
	public class ConstantPackageInfo{
		public byte[] tag;
		public byte[] flags;
		public byte[] name_index;
		public byte[] minor_version;
		public byte[] major_version;
		public byte[] aid_length;
		public byte[] aid;
	}
	
	/**
	 CONSTANT_Classref_info {
		u1 tag
		u2 name_index
		}

	 */
	public class ConstantClassrefInfo{
		public byte[] tag;
		public byte[] name_index;
	}
	
	/**
	 CONSTANT_Integer_info {
		u1 tag
		u4 bytes
		}
	 */
	public class ConstantIntegerInfo{
		public byte[] tag;
		public byte[] bytes;
	}
	
	/**
	 CONSTANT_Utf8_info {
		u1 tag
		u2 length
		u1 bytes[length]
		}
	 */
	public class ConstantUtf8Info{
		public byte[] tag;
		public byte[] length;
		public byte[] bytes;
		public String bytesStr;
	}
	
	/**
	 class_info {
		u1 token
		u2 access_flags
		u2 name_index
		u2 export_supers_count
		u2 supers[export_supers_count]
		u1 export_interfaces_count
		u2 interfaces[export_interfaces_count]
		u2 export_fields_count
		field_info fields[export_fields_count]
		u2 export_methods_count
		method_info methods[export_methods_count]
		}
	 */
	public class ClassInfo{
		public byte[] token;
		public byte[] access_flags;
		public byte[] name_index;
		//自定义
		public byte[] class_name;
		public byte[] export_supers_count;
		public List<byte[]> supers ;
		public byte[] export_interfaces_count;
		public List<byte[]> interfaces;
		public byte[] export_fields_count;
		public List fields;
		public byte[] export_methods_count;
		public List<MethodInfo> methods;
		
		public String class_name_str;
	}
	
	/**field_info {
	   u1 token
		u2 access_flags
		u2 name_index
		u2 descriptor_index
		u2 attributes_count1
		attribute_info attributes[attributes_count]
		}
		The only attribute defined for the attributes table of a field_info structure by
		this specification is the ConstantValue attribute
	 */
	public class FileInfo{
		public byte[] token;
		public byte[] access_flags;
		public byte[] name_index;
		public byte[] descriptor_index;
		public byte[] attributes_count;
		public List<ConstantValueAttribute> attributes;
	}
	
	
	/**
	 method_info {
		u1 token
		u2 access_flags
		u2 name_index
		u2 descriptor_index
		}
	 */
	public class MethodInfo{
		public byte[] token;
		public byte[] access_flags;
		public byte[] name_index;
		public byte[] method_name;
		public byte[] descriptor_index;
		public byte[] method_descriptor;
		
		public String method_name_str;
		public String method_descriptor_str;
	}
	
	/**
	  attribute_info {
		u2 attribute_name_index
		u4 attribute_length
		u1 info[attribute_length]
		}
	 */
	public class AttributeInfo{
		public byte[] attribute_name_index;
		public byte[] attribute_length;
		public byte[] info;
	}
	
	/**
	 * ConstantValue_attribute {
			u2 attribute_name_index
			u4 attribute_length
			u2 constantvalue_index
			}
	 */
	public class ConstantValueAttribute{
		public byte[] attribute_name_index;
		public byte[] attribute_length;
		public byte[] constantvalue_index;
	}
}
