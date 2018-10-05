package com.uds.sjec.base;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class BaseModuleOperation extends AbstractAIFOperation {
	protected com.teamcenter.rac.kernel.TCSession m_session;
	protected AbstractAIFUIApplication m_app;
	protected void init(){
		m_app = AIFUtility.getCurrentApplication();		
		m_session = (TCSession) m_app.getSession();	
	}

	protected String GetSelectedComponentUid(){
		//用户选择的对象
		InterfaceAIFComponent selComp = m_app.getTargetComponent();
		String selUid = selComp.getUid();
		return selUid;
	}
	protected InterfaceAIFComponent GetSelectedComponent(){
		//用户选择的对象
		InterfaceAIFComponent selComp = m_app.getTargetComponent();
		return selComp;
	}
	protected String GetCurrentUserUid(){
		//当前用户
		TCComponentUser currentUser = this.m_session.getUser();
		String userUid = currentUser.getUid();
		return userUid;
	}
	protected String GetCurrentUserId(){
		//当前用户
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
	 * 获得Ctrl多选的对象
	 * @return
	 */
	protected InterfaceAIFComponent[] GetSelectedComponents(){
		InterfaceAIFComponent[] comps = m_app.getTargetComponents();
		return comps;
	}
	
	@Override
	public void executeOperation() throws Exception {
		init();
		
		DoUserTask();
	}
}
