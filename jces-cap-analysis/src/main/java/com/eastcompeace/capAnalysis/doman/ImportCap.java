package com.eastcompeace.capAnalysis.doman;

import java.util.List;

/**
 * import_component {
	u1 tag
	u2 size
	u1 count
	package_info packages[count]
	}
 * @author LiQing
 *
 */
public class ImportCap {
	
	public byte[] tag;
	public byte[] size;
	public byte[] count;
	public List<PackageInfo> packages;
	
	
	/**
	 * package_info {
		u1 minor_version
		u1 major_version
		u1 AID_length
		u1 AID[AID_length]
		}
	 */
	public class PackageInfo{
		public byte[] minor_version;
		public byte[] major_version;
		public byte[] AID_length;
		public byte[] AID;
	}
}
