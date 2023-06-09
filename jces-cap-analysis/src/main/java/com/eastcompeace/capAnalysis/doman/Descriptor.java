package com.eastcompeace.capAnalysis.doman;

import java.util.List;

public class Descriptor {

	/**
	 * descriptor_component {u1 tag
		u2 size
		u1 class_count
		class_descriptor_info classes[class_count]
		type_descriptor_info types
		 }
	 */
	public byte[] size;
	public byte[] tag;
	public byte[] class_count;
	public List<Class_descriptor_info> class_descriptor_info;
	public type_descriptor_info types;

	/**
	 * class_descriptor_info { _descriptor_info {
		u1 token
		u1 access_flags
		class_ref this_class_ref
		u1 interface_count
		u2 field_count
		u2 method_count
		class_ref interfaces [interface_count]
		field_descriptor_info fields[field_count]
		method_descriptor_info methods[method_count] }
	 * 
	 */
	public static class Class_descriptor_info {
	

		public byte[] token;
		public byte[] access_flags;
		public Class_ref this_class_ref;
		public byte[] interface_count;
		public byte[] field_count;
		public byte[] method_count;
		public List<Class_ref> interfaces;
		public List<Field_descriptor_info> fields;
		public List<Method_descriptor_info> methods;
	}

	/**
	 *
	 * union { u2 internal_class_ref { u1 package_token u1 class_token }
	 * external_class_ref } class_ref 一个对象占两个字节
	 */

	public class Class_ref {

		public byte[] internal_class_ref;
		public byte[] package_token;
		public byte[] class_token;

	}

	/**
	 *
	
	 * */
	public class type_descriptor_info {

	}

	/**
	 *
		field_descriptor_info {
		u1 token
		u1 access_flags
		union {
		static_field_ref static_field
		{
		class_ref class
		u1 token
		} instance_field
		} field_ref
		union {
		u2 primitive_type
		u2 reference_type
		} type
		}

	 * */
	public static class Field_descriptor_info {
		public byte[] token;
		public byte[] access_flags;
		public Field_ref field_ref;
		public  byte[] type;
	}

	/**
	 *
		method_descriptor_info {
		u1 token
		u1 access_flags
		u2 method_offset
		u2 type_offset
		u2 bytecode_count
		u2 exception_handler_count
		u2 exception_handler_index
		}

	 * */
	public class Method_descriptor_info {
		public byte[] token;
		public byte[] access_flags;
		public byte[] method_offset;
		public byte[] type_offset;
		public byte[] bytecode_count;
		public byte[] exception_handler_count;
		public byte[] exception_handler_index;
	}
	
	
	public interface Field_ref {
	}
	
	public class static_field_ref implements Field_ref {
		public byte[] static_field;
	}
	
	public class instance_field implements Field_ref {
		public Class_ref class_; 
		public byte[] token;
	}

}
