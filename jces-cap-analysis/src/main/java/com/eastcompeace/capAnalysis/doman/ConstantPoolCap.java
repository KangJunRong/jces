package com.eastcompeace.capAnalysis.doman;

import java.util.Arrays;
import java.util.List;

/**
 * constant_pool_component {
	u1 tag
	u2 size
	u2 count
	cp_info constant_pool[count]
	}
 * @author LiQing
 *
 */
public class ConstantPoolCap {
	public byte[] tag;
	public byte[] size;
	public byte[] count;

	public List<CpInfo> constant_pool;
	
	
	/**
	 *cp_info {
		 u1 tag
		 u1 info[3]
		}
		
		CONSTANT_Classref 1
		CONSTANT_InstanceFieldref 2
		CONSTANT_VirtualMethodref 3
		CONSTANT_SuperMethodref 4
		CONSTANT_StaticFieldref 5
		CONSTANT_StaticMethodref 6
		
		CONSTANT_Classref_info {
			u1 tag
			union {
				u2 internal_class_ref
				{ u1 package_token
				 u1 class_token
				} external_class_ref
			} class_ref
			u1 padding
		}
		
		CONSTANT_InstanceFieldref_info {
		u1 tag
			class_ref class
			u1 token
		}
		CONSTANT_VirtualMethodref_info {
			u1 tag
			class_ref class
			u1 token
		}
		CONSTANT_SuperMethodref_info {
			u1 tag
			class_ref class
			u1 token
		}
		
		CONSTANT_StaticFieldref_info {
			u1 tag
			union {
				{ 
					u1 padding
					u2 offset
				} internal_ref
				{ 
					u1 package_token
					u1 class_token
					u1 token
				} external_ref
			} static_field_ref
		}
		CONSTANT_StaticMethodref_info {
			u1 tag
			union {
				{ 
					u1 padding
					u2 offset
				} internal_ref
				{ 
					u1 package_token
					u1 class_token
					u1 token
				} external_ref
			} static_method_ref
		}
		
		经分析以上结构，发现外部引用方法都含以下结构，故其它结构不做分析
	 */
	public class CpInfo {
		public byte[] tag;
		public byte[] package_token;		
		public byte[] class_token;
		public byte[] token;
	
		//额外项
		public byte[] package_AID;
		public String packageName;
		public String className;
		public String methodName;
		public String methodDescriptor;
		@Override
		public String toString() {
			return "CpInfo [package_AID=" + Arrays.toString(package_AID) + ", packageName=" + packageName
					+ ", className=" + className + ", methodName=" + methodName + ", methodDescriptor="
					+ methodDescriptor + "]";
		}
		
	}
	
	public class Class_ref {
		
		
		
	}
}
