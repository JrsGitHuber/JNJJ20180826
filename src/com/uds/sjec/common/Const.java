package com.uds.sjec.common;

public interface Const {
//	public interface OracleConnection {// Oracle���ݿ�����
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

//	public interface SQLServerConnection {// SQLServer���ݿ�����
//		String USER = "qc";
//		String PASSWORD = "qc@sjec5";
//		String CLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//		String URL = "jdbc:sqlserver://10.0.0.12:1433;DatabaseName=SJCE-EAP9";
//	}

	public interface PreferenceService {// ��ѡ��
		String CONFIG_WORkFLOW = "SJEC_CFG_WF";// ���õ���������
		String UDSSERV_IP_PORT = "UDS_ENV_UDSSERVER_IP_PORT";// ǩ����������ַ�Ͷ˿�
		String PARAM_CONFIG_USER = "SJEC_PARAMCONFIG_USER";// ���������û�
		String USER_GROUP_JUDGE = "SJEC_USER_GROUP_JUDGE";// �û��������ж�
		String UDS_SJEC_DBMESSAGE = "UDS_SJEC_DBMESSAGE";// ���ݿ���Ϣ
		String UDS_SJEC_EXPORT_EXCEL = "UDS_SJEC_EXPORT_EXCEL";// ��������BOM����Ϣ
		String PARAM_CONFIG_PATH = "UDS_PARAMCONFIG_PATH";// ��������exe��·��
		String ELEVATOR_COMPUTATION_LIST_PATH = "SJEC_COMPUTATION_LIST_PATH";// ���ݼ������б�·��
	}
}
