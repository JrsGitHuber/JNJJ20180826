package com.uds.sjec.base;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.sjec.common.ConstDefine;
import com.uds.sjec.common.Const.PreferenceService;

public class BaseModuleOperation extends AbstractAIFOperation {
	protected com.teamcenter.rac.kernel.TCSession m_session;
	protected AbstractAIFUIApplication m_app;
	protected void init(){
		m_app = AIFUtility.getCurrentApplication();		
		m_session = (TCSession) m_app.getSession();	
	}

	protected String GetSelectedComponentUid(){
		//�û�ѡ��Ķ���
		InterfaceAIFComponent selComp = m_app.getTargetComponent();
		String selUid = selComp.getUid();
		return selUid;
	}
	protected InterfaceAIFComponent GetSelectedComponent(){
		//�û�ѡ��Ķ���
		InterfaceAIFComponent selComp = m_app.getTargetComponent();
		return selComp;
	}
	protected String GetCurrentUserUid(){
		//��ǰ�û�
		TCComponentUser currentUser = this.m_session.getUser();
		String userUid = currentUser.getUid();
		return userUid;
	}
	protected String GetCurrentUserId(){
		//��ǰ�û�
		TCComponentUser currentUser = this.m_session.getUser();
		String userId = null;
		try {
			userId = currentUser.getUserId();
		} catch (TCException e) {
			e.printStackTrace();
		}
		return userId;
	}
	protected void DoUserTask() {}
	
	/**
	 * ���Ctrl��ѡ�Ķ���
	 * @return
	 */
	protected InterfaceAIFComponent[] GetSelectedComponents(){
		InterfaceAIFComponent[] comps = m_app.getTargetComponents();
		return comps;
	}
	
	@Override
	public void executeOperation() throws Exception {
		init();
		
		// ������ѡ��Ļ�ȡ
		AbstractAIFUIApplication application = AIFDesktop.getActiveDesktop().getCurrentApplication();
		final TCSession session = (TCSession) application.getSession();
		
		final TCPreferenceService preferenceService = session.getPreferenceService();
		@SuppressWarnings("deprecation")
		String[] dbMessage = preferenceService.getStringArray(TCPreferenceService.TC_preference_site,
				PreferenceService.UDS_SJEC_DBMESSAGE);
		if (dbMessage.length == 0) {
			MessageBox.post("û���ҵ���ѡ�����ã�" + PreferenceService.UDS_SJEC_DBMESSAGE, "��ʾ", MessageBox.ERROR);
			return;
		}
		String[] strs = dbMessage[0].split(":");
		ConstDefine.TCDB_IP = strs[0];
		ConstDefine.TCDB_PORT = strs[1];
		ConstDefine.TCDB_UID = strs[2];
		ConstDefine.TCDB_USER = strs[3];
		ConstDefine.TCDB_PASSWORD = strs[4];
		ConstDefine.TCDB_URL = "jdbc:oracle:thin:@" + ConstDefine.TCDB_IP + ":" + ConstDefine.TCDB_PORT + ":" + ConstDefine.TCDB_UID;
		
		DoUserTask();
	}
}
