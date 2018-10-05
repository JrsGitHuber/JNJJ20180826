package com.uds.sjec.common;

import com.teamcenter.rac.kernel.TCSession;

public class ConstDefine {
	// 判断是否是在测试
	public static boolean IFTEST = false;
	
	public static boolean IFDEFINE = false;
	public static boolean IFCANDO = true;
	
	public static String TCDB_IP = "";
	public static String TCDB_PORT = "";
	public static String TCDB_UID = "";
	public static String TCDB_USER = "";
	public static String TCDB_PASSWORD = "";
	public static String TCDB_CLASSNAME = "oracle.jdbc.driver.OracleDriver";
	public static String TCDB_URL = "jdbc:oracle:thin:@192.168.111.153:1521:orcl";
	
	public static String EAPDB_IP = "";
	public static String EAPDB_PORT = "";
	public static String EAPDB_UID = "";
	public static String EAPDB_USER = "";
	public static String EAPDB_PASSWORD = "";
	public static String EAPDB_CLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static String EAPDB_URL = "jdbc:sqlserver://10.0.0.12:1433;DatabaseName=SJCE-EAP9";
	
	
	public static TCSession TC_SESSION = null;
	public static String TC_GROUP_ELECTRICAL = "-E";
	public static String TC_GROUP_MECHANICAL = "-M";
	public static String TC_GROUP_SUFFIX = "";
	
	// 本次操作的时间点（用于建立目录）
	public static String TimePoint = "";
	// 本次操作的临时文件夹的位置（还要根据时间点进行构造）
	public static String JNJJPath = "C:\\TEMP\\JNJJTemp\\";
	public static String JNJJTempPath = "";
}
