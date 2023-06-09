/*
 * Copyright ? 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

//+
// Workfile:@(#)Download.java	1.8
// Version:1.8
// Date:06/06/03
//
// Archive:  /Products/Europa/Tools/installer/com/sun/javacard/scriptgen/Download.java
// Modified:06/06/03 17:06:08
// Original author: Joe Chen
//-
package com.ecp.jces.jctool.capfiledownload;

//
// This class collects constants used by the off-card installer
//
public class Download {
	
	public final static byte GOP_FORMAT = 0;
	
	public final static byte SUN_FORMAT = 1;

	public final static int MAJOR = 0;

	public final static int MINOR = 11;

	public final static String HEADER_FILE_NAME = "header.cap";

	public final static String DIRECTORY_FILE_NAME = "directory.cap";

	public final static String IMPORT_FILE_NAME = "import.cap";

	public final static String APPLET_FILE_NAME = "applet.cap";

	public final static String CONSTANTPOOL_FILE_NAME = "constantpool.cap";

	public final static String CLASS_FILE_NAME = "class.cap";

	public final static String METHOD_FILE_NAME = "method.cap";

	public final static String STATICFIELD_FILE_NAME = "staticfield.cap";

	public final static String REFERENCELOCATION_FILE_NAME = "reflocation.cap";

	public final static String EXPORT_FILE_NAME = "export.cap";

	public final static String DESCRIPTOR_FILE_NAME = "descriptor.cap";
	
	public final static String DEBUG_FILE_NAME = "debug.cap";

	/**
	 * CLS for the installer
	 */
	public final static byte INSTALLER_CLA = (byte) 0x80;

	// INS for the installer
	public final static byte INS_CAP_BEGIN = (byte) 0xb0;

	public final static byte INS_CAP_END = (byte) 0xba;

	public final static byte INS_COMPONENT_BEGIN = (byte) 0xb2;

	public final static byte INS_COMPONENT_END = (byte) 0xbc;

	public final static byte INS_COMPONENT_DATA = (byte) 0xb4;

	public final static byte INS_APPLET_INSTALL = (byte) 0xb8;

	public final static byte INS_CAP_ABORT = (byte) 0xbe;
	
	public final static byte INS_INSTALL_ForLoad = (byte) 0xE6;

	public final static byte COMPONENT_HEADER = 1;

	public final static byte COMPONENT_DIRECTORY = 2;

	public final static byte COMPONENT_APPLET = 3;

	public final static byte COMPONENT_IMPORT = 4;

	public final static byte COMPONENT_CONSTANTPOOL = 5;

	public final static byte COMPONENT_CLASS = 6;

	public final static byte COMPONENT_METHOD = 7;

	public final static byte COMPONENT_STATICFIELD = 8;

	public final static byte COMPONENT_REFERENCELOCATION = 9;

	public final static byte COMPONENT_EXPORT = 10;

	public final static byte COMPONENT_DESCRIPTOR = 11;
	public final static byte COMPONENT_DEBUG = 12;

	// number of (required) components + 1
	public final static byte INSTALLER_MAX = 12;

	public final static byte COMPONENT_MAX = 12;

	public final static short ON_CARD_PKG_MAX = 32;

	// CAP file download order
	public final static byte ORDER_HEADER = 1;

	public final static byte ORDER_DIRECTORY = 2;

	public final static byte ORDER_IMPORT = 3;

	public final static byte ORDER_APPLET = 4;

	public final static byte ORDER_CLASS = 5;

	public final static byte ORDER_METHOD = 6;

	public final static byte ORDER_STATICFIELD = 7;

	public final static byte ORDER_EXPORT = 8;

	public final static byte ORDER_CONSTANTPOOL = 9;

	public final static byte ORDER_REFERENCELOCATION = 10;

	public final static byte ORDER_DESCRIPTOR = 11;
	
	public final static byte ORDER_DEBUG = 12;

	// component download order
	public final static byte[] DOWNLOAD_ORDER = { 0, ORDER_HEADER,
			ORDER_DIRECTORY, ORDER_APPLET, ORDER_IMPORT, ORDER_CONSTANTPOOL,
			ORDER_CLASS, ORDER_METHOD, ORDER_STATICFIELD,
			ORDER_REFERENCELOCATION, ORDER_EXPORT, ORDER_DESCRIPTOR, ORDER_DEBUG,};

	public final static byte[] ORDER_TO_TAG = { 0, COMPONENT_HEADER, // ORDER_Header
																		// = 1;
			COMPONENT_DIRECTORY, // ORDER_Directory = 2;
			COMPONENT_IMPORT, // ORDER_Import = 4;
			COMPONENT_APPLET, // ORDER_Applet = 3;
			COMPONENT_CLASS, // ORDER_Class = 5;
			COMPONENT_METHOD, // ORDER_Method = 6;
			COMPONENT_STATICFIELD, // ORDER_StaticField = 7;
			COMPONENT_EXPORT, // ORDER_Export = 8;
			COMPONENT_CONSTANTPOOL, // ORDER_ConstantPool = 9;
			COMPONENT_REFERENCELOCATION, // ORDER_ReferenceLocation = 10;
			COMPONENT_DESCRIPTOR, // ORDER_Descriptor = 11;
			COMPONENT_DEBUG, //ORDER_DEBUG = 12;
	};

	// component tag map
	public final static byte[] COMPONENT_TAGS = { 0, COMPONENT_HEADER,
			COMPONENT_DIRECTORY, COMPONENT_APPLET, COMPONENT_IMPORT,
			COMPONENT_CONSTANTPOOL, COMPONENT_CLASS, COMPONENT_METHOD,
			COMPONENT_STATICFIELD, COMPONENT_REFERENCELOCATION,
			COMPONENT_EXPORT, COMPONENT_DESCRIPTOR, COMPONENT_DEBUG,};

	// misc.
	public final static byte CAP_MAJOR = 2;

	public final static byte CAP_MINOR = 2;

	public final static byte PKG_MAJOR = 1;

	public final static byte PKG_MINOR = 0;

	public final static byte MIN_AID_LEN = 5;

	public final static byte MAX_AID_LEN = 16;

	public final static short INSTANCE_MAX = (short) 255;

	public final static short BAD_ADDRESS = -1;

	public final static short FFFF = (short) 0xffff;

	public final static short ILLEGAL_ADDRESS = (short) 0;

	public final static byte ILLEGAL_ID = -1;

	public final static byte ILLEGAL_INDEX = -1;

	public final static byte ILLEGAL_TOKEN = -1;

	public final static byte CAP_MAGIC1 = (byte) 0xDE;

	public final static byte CAP_MAGIC2 = (byte) 0xCA;

	public final static byte CAP_MAGIC3 = (byte) 0xFF;

	public final static byte CAP_MAGIC4 = (byte) 0xED;

	public final static byte TYPE_BOOLEAN = (byte) 0;

	public final static byte TYPE_BYTE = (byte) 1;

	public final static byte TYPE_SHORT = (byte) 2;

	public final static byte TYPE_INT = (byte) 4;

	public final static short COMP_HEADER_SIZE = (short) 3;

	public final static short U1_SIZE = (short) 1;

	public final static short U2_SIZE = (short) 2;

	public final static short U4_SIZE = (short) 4;

	public final static short EXPORT_COUNT_SIZE = (short) 1;

	//
	// level of on-card CAP file verification (0 being the lowest level)
	//
	public final static boolean VERIFICATION_LEVEL_0 = true;

	public final static boolean VERIFICATION_LEVEL_1 = true;

	public final static boolean VERIFICATION_LEVEL_2 = true;

	public final static boolean VERIFICATION_LEVEL_3 = true;

	public final static boolean VERIFICATION_LEVEL_4 = false;

	public final static boolean DEBUG = false;

	public final static boolean CHECK_PKG_VER = false;

	public final static boolean INTEGER_MODE = true;

	public final static boolean NATIVE_METHODS_SUPPORT = true;

	public final static boolean STATIC_ARRAY_INIT_SUPPORT = false;

	public final static boolean FAKE_OBJECT_CLASS = true;

	public final static boolean SUPORT_ROM_PKG = false;

	public final static boolean FAKE_IMPL_PKG = true;

	public final static byte[] JAVALANG_AID = { (byte) 0xA0, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x62, (byte) 0x00, (byte) 0x01, };

	// CAP file masks
	public final static byte ACC_INT = (byte) 0x01;

	public final static byte ACC_EXPORT = (byte) 0x02;

	public final static byte ACC_APPLET = (byte) 0x04;

	public final static byte ACC_INTERFACE = (byte) 0x80;

	public final static byte ACC_SHAREABLE = (byte) 0x40;

	public final static byte ACC_EXTENDED = (byte) 0x80;

	public final static byte ACC_ABSTRACT = (byte) 0x40;

	public final static byte ACC_PUBLIC = (byte) 0x01;

	public final static byte ACC_FINAL = (byte) 0x10;

	public final static byte MASK_EXTERNAL = (byte) 0x80;

	public final static byte MASK_HIGH_BIT_ON = (byte) 0x80;

	public final static byte MASK_IS_PKG_METHOD = (byte) 0x80;

	public final static byte MASK_IS_ROM = (byte) 0x80;

	public final static byte MASK_IS_EEPROM = (byte) 0x7F;

	public final static byte MASK_HIGH_BIT_OFF = (byte) 0x7F;

	public final static byte MASK_INTERFACE_COUNT = (byte) 0x0F;

	public final static short OFFSET_ZERO = 0;

	public final static short OFFSET_COMPONENT_TAG = 0;

	public final static short OFFSET_COMPONENT_SIZE = 1;

	// header_component offsets
	public final static short OFFSET_HEADER_MAGIC = 3;

	public final static short OFFSET_HEADER_MAGIC1 = 3;

	public final static short OFFSET_HEADER_MAGIC2 = 4;

	public final static short OFFSET_HEADER_MAGIC3 = 5;

	public final static short OFFSET_HEADER_MAGIC4 = 6;

	public final static short OFFSET_HEADER_MINOR = 7;

	public final static short OFFSET_HEADER_MAJOR = 8;

	public final static short OFFSET_HEADER_FLAGS = 9;

	public final static short OFFSET_HEADER_PKG = 10;

	// package_info offsets
	public final static short OFFSET_PKG_MINOR = 0;

	public final static short OFFSET_PKG_MAJOR = 1;

	public final static short OFFSET_PKG_AID_LEN = 2;

	public final static short OFFSET_PKG_AID = 3;

	public final static short OFFSET_PACKAGE_INFO_MINOR = 0;

	public final static short OFFSET_PACKAGE_INFO_MAJOR = 1;

	public final static short OFFSET_PACKAGE_INFO_AID_LENGTH = 2;

	public final static short OFFSET_PACKAGE_INFO_AID = 3;

	// directory_component offsets
	public final static short OFFSET_DIRECTORY_BASIC_COUNT = 3;

	public final static short OFFSET_DIRECTORY_CUSTOM_COUNT = 4;

	public final static short OFFSET_DIRECTORY_BASIC_COMPONENTS = 5;

	// basic_components
	public final static short OFFSET_BASIC_COMPONENTS_TAG = 0;

	public final static short OFFSET_BASIC_COMPONENTS_SIZE = 1;

	public final static short BASIC_COMPONENTS_MEMBER_SIZE = 3;

	// custom_components
	public final static short OFFSET_CUSTOM_COMPONENTS_TAG = 0;

	public final static short OFFSET_CUSTOM_COMPONENTS_SIZE = 1;

	public final static short OFFSET_CUSTOM_COMPONENTS_AID_LENGTH = 3;

	public final static short OFFSET_CUSTOM_COMPONENTS_AID = 4;

	// applet_component offsets
	public final static short OFFSET_APPLET_COUNT = 3;

	public final static short OFFSET_APPLET_APPLETS = 4;

	public final static short OFFSET_APPLETS_AID_LENGTH = 0;

	public final static short OFFSET_APPLETS_AID = 1;

	// imports_component offsets
	public final static short OFFSET_IMPORT_COUNT = 3;

	public final static short OFFSET_IMPORT_PACKAGE_INFO = 4;

	// constant_pool_component offsets
	public final static short OFFSET_CP_COUNT = 3;

	public final static short OFFSET_CP_INFO = 5;

	public final static short CAP_CP_CELL_SIZE = 4;

	// public static final short CP_CELL_SIZE = 2;
	public final static short CP_CELL_SIZE = 4;

	public final static short TABLE_CELL_SIZE = 2;

	// class_component offsets
	public final static short OFFSET_CLASS_INFO = 0;

	public final static short OFFSET_CLASS_FLAGS = 0;

	public final static short OFFSET_CLASS_SUPER_REF = 1;

	public final static short OFFSET_CLASS_DEC_INST_SIZE = 3;

	public final static short OFFSET_CLASS_REF_INDEX = 4;

	public final static short OFFSET_CLASS_REF_COUNT = 5;

	public final static short OFFSET_CLASS_PUB_METH_BASE = 6;

	public final static short OFFSET_CLASS_PUB_METH_COUNT = 7;

	public final static short OFFSET_CLASS_PKG_METH_BASE = 8;

	public final static short OFFSET_CLASS_PKG_METH_COUNT = 9;

	public final static short OFFSET_CLASS_PUB_METH_TABLE = 10;

	public final static short OFFSET_CLASS_INTERFACE_INFO = 0;

	public final static short OFFSET_CLASS_INTERFACE_INFO_CLASS_REF = 0;

	public final static short OFFSET_CLASS_INTERFACE_INFO_COUNT = 2;

	public final static short OFFSET_CLASS_INTERFACE_INFO_INDEX = 3;

	public final static short INTERFACE_INFO_SIZE = 1;

	// method_component offsets
	public final static short OFFSET_METHOD_EXCEPTION_HANDLER_COUNT = 3;

	public final static short OFFSET_METHOD_EXCEPTION_HANDLER_INFO = 4;

	public final static short EXCEPTION_HANDLER_INFO_SIZE = 8;

	public final static short OFFSET_EXCEPTION_HANDLER_START_OFFSET = 0;

	public final static short OFFSET_EXCEPTION_HANDLER_ACTIVE_LENGTH = 2;

	public final static short OFFSET_EXCEPTION_HANDLER_HANDLER_OFFSET = 4;

	public final static short OFFSET_EXCEPTION_HANDLER_CATCH_TYPE_INDEX = 6;

	public final static short OFFSET_METHOD_NARGS = 1;

	public final static short OFFSET_EXTENDED_METHOD_NARGS = 2;

	// static_field_component offsets
	public final static short OFFSET_STATICFIELD_BYTE_COUNT = 3;

	public final static short OFFSET_STATICFIELD_REFERENCE_COUNT = 5;

	public final static short OFFSET_STATICFIELD_ARRAY_INIT_COUNT = 7;

	public final static short OFFSET_STATICFIELD_ARRAY_INIT_INFO = 9;

	public final static short[] STATICFIELD_TYPE_SIZE = { 0, // padding
			0, // padding
			1, // boolean
			1, // byte
			2, // short
			4, // int
	};

	// array_init_info offsets
	public final static short OFFSET_ARRAY_INIT_INFO_TYPE = 0;

	public final static short OFFSET_ARRAY_INIT_INFO_COUNT = 1;

	public final static short OFFSET_ARRAY_INIT_INFO_VALUES = 3;

	// export_component offsets
	public final static short OFFSET_EXPORT_CLASS_COUNT = 3;

	public final static short OFFSET_EXPORT_CLASS_EXPORT_INFO = 4;

	public final static short OFFSET_EXPORT_COUNT = 0;

	public final static short OFFSET_EXPORT_CLASS_INFO = 1;

	public final static short OFFSET_EXPORT_INFO_CLASS_OFFSET = 0;

	public final static short OFFSET_EXPORT_INFO_STATIC_FIELD_COUNT = 2;

	public final static short OFFSET_EXPORT_INFO_STATIC_METHOD_COUNT = 3;

	public final static short OFFSET_EXPORT_INFO_STATIC_FIELD_OFFSETS = 4;

	public final static short EXPORT_INFO_STATIC_FIELD_OFFSETS_SIZE = 2;

	public final static short EXPORT_INFO_STATIC_METHOD_OFFSETS_SIZE = 2;

	public final static short CLASS_EXPORT_INFO_CELL_SIZE = 6;

	public final static short OFFSET_CONSTANT_TAG = 0;

	public final static short OFFSET_CONSTANT_COUNT = 3;

	public final static short OFFSET_ON_CARD_CONSTANT = 2;

	public final static short OFFSET_CONSTANT_CLASSREF = 1;

	public final static short OFFSET_CONSTANT_PKG_TOKEN = 1;

	public final static short OFFSET_CONSTANT_CLASS_TOKEN = 2;

	public final static short OFFSET_CONSTANT_OFFSET = 2;

	public final static short OFFSET_CONSTANT_TOKEN = 3;

	public final static short OFFSET_CONSTANT_FLD_TOKEN = 3;

	public final static short OFFSET_CONSTANT_METH_TOKEN = 3;

	public final static short CLASS_REF_SIZE = 2;

	// constant pool tags
	public final static byte CONSTANT_CLASSREF = 1;

	public final static byte CONSTANT_INSTANCEFIELDREF = 2;

	public final static byte CONSTANT_VIRTUALMETHODREF = 3;

	public final static byte CONSTANT_SUPERMETHODREF = 4;

	public final static byte CONSTANT_STATICFIELDREF = 5;

	public final static byte CONSTANT_STATICMETHODREF = 6;

	// reference_location_component offsets
	public final static short OFFSET_REFLOC_BYTE_INDEX_COUNT = 3;

	public final static short OFFSET_REFLOC_BYTE_INDICES = 5;

	// super block definitions
	// private final static short BASIC_COMP_MAX = 12;
	public final static short EE_SUPER_BLOCK_SIZE = (short) 0x20;

	final static byte EE_SUPER_BLOCK_INDEX = (byte) 0;

	final static short EE_OFFSET_SUPER_BLOCK_PKG_ID = (short) 0;

	final static short EE_OFFSET_SUPER_BLOCK_PKG_MICRO = (short) 1;

	final static short EE_OFFSET_SUPER_BLOCK_PKG_MINOR = (short) 2;

	final static short EE_OFFSET_SUPER_BLOCK_PKG_MAJOR = (short) 3;

	final static short EE_OFFSET_SUPER_BLOCK_PKG_AID_LENGTH = (short) 4;

	final static short EE_OFFSET_SUPER_BLOCK_PKG_AID = (short) 5;

	final static short EE_OFFSET_SUPER_BLOCK_EXPORT = (short) 21;

	final static short EE_OFFSET_SUPER_BLOCK_EXCEPT_TABLE_OFFSET = (short) 23;

	final static short EE_OFFSET_SUPER_BLOCK_PKG_SIZE = (short) 25;

	// error codes
	public final static short ERROR_SW1 = (short) 0x6000;

	public final static short ERROR_COMPONENT_TAG = 0x01;

	public final static short ERROR_COMPONENT_SIZE = 0x01;

	public final static short ERROR_CAP_MAGIC = 0x02;

	public final static short ERROR_CAP_MINOR = 0x03;

	public final static short ERROR_CAP_MAJOR = 0x04;

	public final static short ERROR_CAP_FLAGS = 0x05;

	public final static short ERROR_PKG_MINOR = 0x06;

	public final static short ERROR_PKG_MAJOR = 0x07;

	public final static short ERROR_PKG_AID = 0x08;

	public final static short ERROR_CORRUPT_EE = 0x09;

	public final static short ERROR_SIZE = 0x0a;

	public final static short ERROR_INTEGER_UNSUPPORTED = 0x0b;

	public final static short ERROR_DUP_PKG_AID = 0x0c;

	public final static short ERROR_DUP_APPLET_AID = 0x0d;

	public final static short ERROR_ABORTED = 0x0f;

	public final static short ERROR_METHOD = 0x12;

	public final static short ERROR_BAD_TYPE = 0x13;

	public final static short ERROR_BAD_OFFSET = 0x14;

	public final static short ERROR_BAD_TOKEN = 0x15;

	public final static short ERROR_TBD = 0x16;

	public final static short ERROR_PKG_TOKEN = 0x17;

	public final static short ERROR_ILLEGAL_ADDRESS = 0x18;

	public final static short ERROR_ILLEGAL_TAG = 0x19;

	public final static short ERROR_PKG_NOT_FOUND = 0x20;

	public final static short ERROR_STATE = 0x21;

	public final static short ERROR_COMP_ORDER = 0x22;

	public final static short ERROR_RUNTIME = 0x23;

	public final static short ERROR_EXCEPTION = 0x24;

	public final static short ERROR_COMMAND_STATE = 0x25;

	public final static short ERROR_IMPORT_COUNT = 0x26;

	public final static short ERROR_MODE = 0x27;

	public final static short ERROR_TAG = 0x28;

	public final static short ERROR_COMP_SIZE = 0x29;

	public final static short ERROR_DUP_AID = 0x30;

	public final static short ERROR_OBJ_REF = 0x31;

	public final static short ERROR_NO_SUPER_CLASS = 0x32;

	public final static short ERROR_SUPER_INTERFACE = 0x33;

	public final static short ERROR_METHOD_ADDRESS = 0x34;

	public final static short ERROR_PKG_INDEX = 0x35;

	public final static short ERROR_INSTRUCTION = 0x36;

	public final static short ERROR_ALLOC = 0x37;

	public final static short ERROR_IMPORT_NOT_FOUND = 0x38;

	public final static short ERROR_PKG_ID = 0x39;

	public final static short ERROR_EXPORT_CLASS_COUNT = 0x40;

	public final static short ERROR_STATIC_FIELD_NOT_FOUND = 0x41;

	public final static short ERROR_STATIC_METHOD_NOT_FOUND = 0x42;

	public final static short ERROR_APPLET_NOT_FOUND = 0x43;

	public final static short ERROR_METHOD_TOKEN = 0x44;

	public final static short ERROR_APPLET_CREATION = 0x45;

	public final static short ERROR_INSTANCE_MAX_EXCEEDED = 0x45;
}
