package com.uds.sjec.common;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.sjec.common.Const.PreferenceService;

public class CommonFunction {
	
	public static void Init() {
		try {
			InitSomeConst();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post("初始化参数失败：" + e.getMessage(), "提示", MessageBox.INFORMATION);
		}
	}
	
	public static void InitSomeConst() throws Exception {
		if (ConstDefine.IFTEST || !ConstDefine.IFDEFINE) {
			AbstractAIFUIApplication application = AIFDesktop.getActiveDesktop().getCurrentApplication();
			ConstDefine.TC_SESSION = (TCSession) application.getSession();
			
			String groupName = ConstDefine.TC_SESSION.getGroup().getLocalizedFullName();
			if (groupName.contains("电气") || groupName.contains("dba")) {
				ConstDefine.TC_GROUP_SUFFIX = ConstDefine.TC_GROUP_ELECTRICAL;
			} else if (groupName.contains("机械")) {
				ConstDefine.TC_GROUP_SUFFIX = ConstDefine.TC_GROUP_MECHANICAL;
			} else {
				ConstDefine.IFCANDO = false;
			}
			
			ConstDefine.TC_PREFERENCESERVICE = ConstDefine.TC_SESSION.getPreferenceService();
			@SuppressWarnings("deprecation")
			String[] dbMessage = ConstDefine.TC_PREFERENCESERVICE.getStringArray(TCPreferenceService.TC_preference_site, PreferenceService.UDS_SJEC_DBMESSAGE);
			if (dbMessage == null || dbMessage.length == 0) {
				MessageBox.post("没有找到首选项配置：" + PreferenceService.UDS_SJEC_DBMESSAGE, "提示", MessageBox.ERROR);
				return;
			} else if (dbMessage.length != 2) {
				MessageBox.post("首选项" + PreferenceService.UDS_SJEC_DBMESSAGE + "配置不正确", "提示", MessageBox.ERROR);
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
					ConstDefine.TCDB_PASSWORD = strs[4];
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
			
			ConstDefine.IFDEFINE = true;
		}
	}
}
