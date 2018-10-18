package com.uds.sjec.common;

public interface Const {
//	public interface OracleConnection {// Oracle数据库连接
//		String IP = "";
//		String PORT = "";
//		String UID = "";
//		String USER = "";
//		String PASSWORD = "";
//		
//		String CLASSNAME = "oracle.jdbc.driver.OracleDriver";
//		String URL = "jdbc:oracle:thin:@192.168.111.153:1521:orcl";
//		
//	}

//	public interface SQLServerConnection {// SQLServer数据库连接
//		String USER = "qc";
//		String PASSWORD = "qc@sjec5";
//		String CLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//		String URL = "jdbc:sqlserver://10.0.0.12:1433;DatabaseName=SJCE-EAP9";
//	}

	public interface PreferenceService {// 首选项
		String CONFIG_WORkFLOW = "SJEC_CFG_WF";// 配置单流程名称
		String UDSSERV_IP_PORT = "UDS_ENV_UDSSERVER_IP_PORT";// 签名服务器地址和端口
		String PARAM_CONFIG_USER = "SJEC_PARAMCONFIG_USER";// 参数管理用户
		String USER_GROUP_JUDGE = "SJEC_USER_GROUP_JUDGE";// 用户所在组判断
		String UDS_SJEC_DBMESSAGE = "UDS_SJEC_DBMESSAGE";// 数据库信息
		String UDS_SJEC_EXPORT_EXCEL = "UDS_SJEC_EXPORT_EXCEL";// 导出整体BOM表信息
		String PARAM_CONFIG_PATH = "UDS_PARAMCONFIG_PATH";// 参数管理exe的路径
		String ELEVATOR_COMPUTATION_LIST_PATH = "SJEC_COMPUTATION_LIST_PATH";// 电梯计算项列表路径
	}
}
