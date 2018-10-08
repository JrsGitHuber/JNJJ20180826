package com.uds.sjec.common;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.aspose.cells.License;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.uds.sjec.common.Const.PreferenceService;
import common.Jr.utils.AsposeExcelUtils;

public class CommonFunction {
	public static String m_errorMessage = "";
	
	public static void GetTCSession() {
		AbstractAIFUIApplication application = AIFDesktop.getActiveDesktop().getCurrentApplication();
		ConstDefine.TC_SESSION = (TCSession) application.getSession();
	}
	
	public static InterfaceAIFComponent[] GetTargetComponents() {
		AbstractAIFUIApplication application = AIFDesktop.getActiveDesktop().getCurrentApplication();
		InterfaceAIFComponent[] selComps = application.getTargetComponents();
		if (selComps != null && selComps.length != 0) {
			System.out.println(selComps[0].getType());
		}
		return selComps;
	}
	
	public static void InitSomeConst() {
		m_errorMessage = "";
		
		if (!ConstDefine.TimePoint.equals("")) {
			return;
		}
		try {
			GetTCSession();
			
			// 获取当前操作的时间点
			ConstDefine.TimePoint = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
			// 构造本次操作的临时目录
			ConstDefine.JNJJTempPath = ConstDefine.JNJJPath + ConstDefine.TC_SESSION.toString() + "_" + ConstDefine.TimePoint;
			File file = new File(ConstDefine.JNJJTempPath);
			if (!file.mkdirs()) {
				m_errorMessage = "创建临时目录" + ConstDefine.JNJJTempPath + "失败，请检查当前Windows登录用户的权限";
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			m_errorMessage = "InitSomeConst Error：" + e.getMessage();
			return;
		}
	}
	
	public static void GetDBMessage() {
		m_errorMessage = "";
		
		try {
			GetTCSession();
			
			TCPreferenceService preferenceService = ConstDefine.TC_SESSION.getPreferenceService();
			@SuppressWarnings("deprecation")
			String[] dbMessage = preferenceService.getStringArray(TCPreferenceService.TC_preference_site, PreferenceService.UDS_SJEC_DBMESSAGE);
			if (dbMessage == null || dbMessage.length == 0) {
				m_errorMessage = "没有找到首选项配置：" + PreferenceService.UDS_SJEC_DBMESSAGE;
				return;
			} else if (dbMessage.length != 2) {
				m_errorMessage = "首选项" + PreferenceService.UDS_SJEC_DBMESSAGE + "配置不正确";
				return;
			}
			for (String message : dbMessage) {
				if (message.startsWith("TCDB")) {
					message = message.replace("TCDB:", "");
					String[] strs = message.split(":");
					ConstDefine.TCDB_IP = strs[0];
					ConstDefine.TCDB_PORT = strs[1];
					ConstDefine.TCDB_UID = strs[2];
					ConstDefine.TCDB_USER = strs[3];
					ConstDefine.TCDB_PASSWORD = strs[4].substring(0, 6);
					ConstDefine.TCDB_URL = "jdbc:oracle:thin:@" + ConstDefine.TCDB_IP + ":" + ConstDefine.TCDB_PORT + ":" + ConstDefine.TCDB_UID;
				} else if (message.startsWith("EAPDB")) {
					message = message.replace("EAPDB:", "");
					String[] strs1 = message.split(":");
					ConstDefine.EAPDB_IP = strs1[0];
					ConstDefine.EAPDB_PORT = strs1[1];
					ConstDefine.EAPDB_UID = strs1[2];
					ConstDefine.EAPDB_USER = strs1[3];
					ConstDefine.EAPDB_PASSWORD = strs1[4];
					ConstDefine.EAPDB_URL = "jdbc:sqlserver://" + ConstDefine.EAPDB_IP + ":" + ConstDefine.EAPDB_PORT + ";DatabaseName=" + ConstDefine.EAPDB_UID;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			m_errorMessage = "GetDBMessage Error: " + e.getMessage();
			return;
		}
	}
	
	public static void GetExportMessage(List<String> columnNameList, List<String> propertyNameList) {
		m_errorMessage = "";
		
		try {
			GetTCSession();
			
			TCPreferenceService preferenceService = ConstDefine.TC_SESSION.getPreferenceService();
			@SuppressWarnings("deprecation")
			String[] exportMessage = preferenceService.getStringArray(TCPreferenceService.TC_preference_site, PreferenceService.UDS_SJEC_EXPORT_EXCEL);
			if (exportMessage == null || exportMessage.length == 0) {
				m_errorMessage = "没有找到首选项配置：" + PreferenceService.UDS_SJEC_EXPORT_EXCEL;
				return;
			}
			for (String message : exportMessage) {
				String[] strs = message.split(":");
				columnNameList.add(strs[0]);
				propertyNameList.add(strs[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			m_errorMessage = "GetDBMessage Error: " + e.getMessage();
			return;
		}
	}
	
	public static void GetGroupSuffix() {
		m_errorMessage = "";
		
		try {
			GetTCSession();
			
			String groupName = ConstDefine.TC_SESSION.getGroup().getLocalizedFullName();
			if (groupName.contains("电气") || groupName.contains("dba")) {
				ConstDefine.TC_GROUP_SUFFIX = ConstDefine.TC_GROUP_ELECTRICAL;
			} else if (groupName.contains("机械")) {
				ConstDefine.TC_GROUP_SUFFIX = ConstDefine.TC_GROUP_MECHANICAL;
			} else {
				ConstDefine.IFCANDO = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			m_errorMessage = "GetGroupSuffix Error: " + e.getMessage();
			return;
		}
	}
	
	public static void GetExcelLicense() {
		InputStream license = AsposeExcelUtils.class.getClassLoader().getResourceAsStream("\\license.xml"); // license路径
		License aposeLic = new License();
		aposeLic.setLicense(license);
	}
	
	public static String RemoveEndZero(String data) {
		if (data == null || data.equals("")) {
			return "";
		}
		
    	if (data.contains(".")) {
    		int length = data.length();
    		for (int i = length - 1; i >= 0; i--) {
    			if (data.charAt(i) == '.') {
    				return data.substring(0, i);
    			} else if (data.charAt(i) != '0') {
    				return data.substring(0, i+1);
    			}
    		}
    	}
    	
    	return data;
	}
	
	@SuppressWarnings("deprecation")
	public static String JudgeUserPermission(String userName) {
		m_errorMessage = "";
		GetTCSession();
		TCPreferenceService preferenceService = ConstDefine.TC_SESSION.getPreferenceService();
		String[] values = preferenceService.getStringArray(TCPreferenceService.TC_preference_site,
				PreferenceService.PARAM_CONFIG_USER);
		if (values == null || values.length == 0) {
			m_errorMessage = "没有找到首选项配置：" + PreferenceService.PARAM_CONFIG_USER;
			return "";
		}
		String[] permissions = new String[] { "write", "read" };
		for (String value : values) {
			for (String permission : permissions) {
				if (value.startsWith(permission)) {
					String[] users = value.replace(permission+":", "").split(",");
					for (String user : users) {
						if (userName.equals(user)) {
							return permission;
						}
					}
				}
			}
		}
		
		return "noPermission";
	}
}
