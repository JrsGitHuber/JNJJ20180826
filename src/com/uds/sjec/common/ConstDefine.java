package com.uds.sjec.common;

import com.teamcenter.rac.kernel.TCSession;

public class ConstDefine {
	// �ж��Ƿ����ڲ���
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
	
	// ���β�����ʱ��㣨���ڽ���Ŀ¼��
	public static String TimePoint = "";
	// ���β�������ʱ�ļ��е�λ�ã���Ҫ����ʱ�����й��죩
	public static String JNJJPath = "C:\\TEMP\\JNJJTemp\\";
	public static String JNJJTempPath = "";
}
