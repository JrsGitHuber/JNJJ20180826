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
		
		// 获取BOM视图窗口
		String RevisionRuleName = "Latest Working";
		TCComponentRevisionRule rule = getGivenRule(session, RevisionRuleName);
		TCComponentBOMWindowType winType = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
		TCComponentBOMWindow bomWin = winType.create(rule);

		TCComponent revView = getRevView(itemRev, strName);
		if (revView == null) {
			return null;
		}

		// 这里应该就是通过BOMWin打开视图将某一条数据作为第一条
		TCComponentBOMLine proTopBOMLine = bomWin.setWindowTopLine(itemRev.getItem(), itemRev, revView, null);
		return proTopBOMLine;
	}
	
	// 版本配置规则
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
	 * 根据版本和BoM视图的关系来获得BOMView
	 * 
	 * @param ItemRev
	 * @param strName
	 * @return
	 * @throws Exception 
	 */
	public static TCComponent getRevView(TCComponentItemRevision ItemRev, String strName) throws Exception {
		//版本下面的是BOM类型的对象
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
	
	//通过版本创建视图
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

		// 关闭并保存Bom View
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
