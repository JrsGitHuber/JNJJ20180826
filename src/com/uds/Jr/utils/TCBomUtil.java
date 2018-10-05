package com.uds.Jr.utils;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCSession;

public class TCBomUtil {
	public static TCComponentBOMLine GetTopBomLine(TCSession session, TCComponentItemRevision itemRev, String strName) throws Exception {
		if (itemRev == null) {
			return null;
		}
		
		// ��ȡBOM��ͼ����
		String RevisionRuleName = "Latest Working";
		TCComponentRevisionRule rule = getGivenRule(session, RevisionRuleName);
		TCComponentBOMWindowType winType = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
		TCComponentBOMWindow bomWin = winType.create(rule);

		TCComponent revView = getRevView(itemRev, strName);
		if (revView == null) {
			return null;
		}

		// ����Ӧ�þ���ͨ��BOMWin����ͼ��ĳһ��������Ϊ��һ��
		TCComponentBOMLine proTopBOMLine = bomWin.setWindowTopLine(itemRev.getItem(), itemRev, revView, null);
		return proTopBOMLine;
	}
	
	// �汾���ù���
	private static TCComponentRevisionRule getGivenRule(TCSession session, String RuleName) throws Exception {
		TCComponentRevisionRule rrs[] = TCComponentRevisionRule.listAllRules(session);
		for (int i = 0; i < rrs.length; i++) {
			if (rrs[i].getProperty("object_name").equals(RuleName)) {
				return rrs[i];
			}
		}
		return null;
	}
	
	/**
	 * ���ݰ汾��BoM��ͼ�Ĺ�ϵ�����BOMView
	 * 
	 * @param ItemRev
	 * @param strName
	 * @return
	 * @throws Exception 
	 */
	public static TCComponent getRevView(TCComponentItemRevision ItemRev, String strName) throws Exception {
		//�汾�������BOM���͵Ķ���
		TCComponent BOMView[] = ItemRev.getRelatedComponents("structure_revisions");
		
		for (int i = 0; i < BOMView.length; i++) {
			TCComponent referenceProperty = BOMView[i].getReferenceProperty("bom_view");
			TCComponent referenceProperty2 = referenceProperty.getReferenceProperty("view_type");
			String name = referenceProperty2.toDisplayString();
			if (name.contains(strName)) {
				return BOMView[i];
			}
		}
		return null;
	}
	
	//ͨ���汾������ͼ
	public static TCComponentBOMLine SetBOMViewForItemRev(TCSession session, TCComponentItemRevision itemRevision) throws Exception {
		String RevisionRuleName = "Latest Working";
		TCComponentRevisionRule rule = null;
		TCComponentRevisionRule rrs[] = TCComponentRevisionRule.listAllRules(session);
		for (int i = 0; i < rrs.length; i++) {
			if (rrs[i].getProperty("object_name").equals(RevisionRuleName)) {
				rule= rrs[i];
			}
		}
		TCComponentBOMWindowType winType = (TCComponentBOMWindowType) itemRevision.getSession().getTypeComponent("BOMWindow");
		TCComponentBOMWindow NewWin = winType.create(rule);
		TCComponentBOMLine topBomline = NewWin.setWindowTopLine(itemRevision.getItem(), itemRevision, null, null);
		return topBomline;
	}
	
	public static void CloseWindow(TCComponentBOMLine topBomLine) {

		// �رղ�����Bom View
		if (topBomLine != null) {
			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			try {
				bomWindow.save();
				bomWindow.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
