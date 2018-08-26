package com.uds.sjec.utils;

import java.util.ArrayList;
import java.util.List;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMView;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class BomUtil {

	/**
	 * @param itemRev
	 *            版本
	 * @param strName
	 *            版本下的关系中想要在结构管理器中打开的BOM视图
	 * @return 在结构管理器中打开的BOM中的第一个
	 * @throws TCException
	 */
	public static TCComponentBOMLine getTopBomLine(TCComponentItemRevision itemRev, String strName) {

		try {
			if (itemRev == null) {
				return null;
			}
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
			TCSession session = (TCSession) app.getSession();

			// 获取BOM视图窗口
			String RevisionRuleName = "Latest Working";
			TCComponentRevisionRule rule = getGivenRule(RevisionRuleName);
			TCComponentBOMWindowType winType = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
			TCComponentBOMWindow bomWin = winType.create(rule);
			TCComponent revView = getRevView(itemRev, strName);
			if (revView == null) {
				return null;
			}
			// 这里应该就是通过BOMWin打开视图将某一条数据作为第一条
			TCComponentBOMLine proTopBOMLine = bomWin.setWindowTopLine(itemRev.getItem(), itemRev, revView, null);
			return proTopBOMLine;
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;

	}

	// 版本配置规则
	private static TCComponentRevisionRule getGivenRule(String RuleName) {
		try {
			TCSession session = (TCSession) AIFUtility.getDefaultSession();
			TCComponentRevisionRule rrs[] = TCComponentRevisionRule.listAllRules(session);
			for (int i = 0; i < rrs.length; i++) {
				if (rrs[i].getProperty("object_name").equals(RuleName)) {
					return rrs[i];
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据版本和BoM视图的关系来获得BOMView
	 * 
	 * @param ItemRev
	 * @param strName
	 * @return
	 */
	public static TCComponent getRevView(TCComponentItemRevision ItemRev, String strName) {
		try {
			TCComponent BOMView[] = ItemRev.getRelatedComponents("structure_revisions");
			for (int i = 0; i < BOMView.length; i++) {
				String bomview = BOMView[i].getProperty("object_name");
				System.out.println("bomview = " + bomview);
				if (BOMView[i].getProperty("object_name").contains(strName)) {
					return BOMView[i];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 通过版本创建视图
	public static TCComponentBOMLine setBOMViewForItemRev(TCComponentItemRevision itemRevision) {
		TCComponentBOMLine topBomline = null;
		try {
			AbstractAIFUIApplication app = AIFDesktop.getActiveDesktop().getCurrentApplication();
			TCSession session = (TCSession) app.getSession();
			String RevisionRuleName = "Latest Working";
			TCComponentRevisionRule rule = null;
			TCComponentRevisionRule rrs[] = TCComponentRevisionRule.listAllRules(session);
			for (int i = 0; i < rrs.length; i++) {
				if (rrs[i].getProperty("object_name").equals(RevisionRuleName)) {
					rule = rrs[i];
				}
			}
			TCComponentBOMWindowType winType = (TCComponentBOMWindowType) itemRevision.getSession().getTypeComponent("BOMWindow");
			TCComponentBOMWindow NewWin = winType.create(rule);
			topBomline = NewWin.setWindowTopLine(itemRevision.getItem(), itemRevision, null, null);
		} catch (Exception e) {
		}
		return topBomline;
	}

	/**
	 * @param itemRev
	 *            版本
	 * @param strName
	 *            版本下的关系中想要在结构管理器中打开的BOM视图
	 * @return 在结构管理器中打开的BOM中的第一个
	 * @throws TCException
	 */
	public static TCComponentBOMLine getTopBomLineAndBOMWin(TCComponentItemRevision itemRev, String strName, TCComponentBOMWindow bomWindow) {

		try {
			if (itemRev == null) {
				return null;
			}
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
			TCSession session = (TCSession) app.getSession();

			// 获取BOM视图窗口
			String RevisionRuleName = "Latest Working";
			TCComponentRevisionRule rule = getGivenRule(RevisionRuleName);
			TCComponentBOMWindowType winType = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
			TCComponentBOMWindow bomWin = winType.create(rule);
			bomWindow = bomWin;
			TCComponent revView = getRevView(itemRev, strName);
			if (revView == null) {
				return null;
			}

			// 这里应该就是通过BOMWin打开视图将某一条数据作为第一条
			TCComponentBOMLine proTopBOMLine = bomWin.setWindowTopLine(itemRev.getItem(), itemRev, revView, null);
			return proTopBOMLine;
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 返回组件的BOMView
	 * 
	 * @param targetComp
	 * @return
	 * 
	 */
	public static TCComponentBOMView[] getComponentBomViews(TCComponent targetComp) {
		if (targetComp != null) {
			try {
				AIFComponentContext[] bomObj = targetComp.getRelated("bom_view_tags");
				if (bomObj != null && bomObj.length > 0) {
					List<TCComponentBOMView> bomList = new ArrayList<TCComponentBOMView>();
					for (int i = 0; i < bomObj.length; i++) {
						InterfaceAIFComponent comp = bomObj[i].getComponent();
						if (comp instanceof TCComponentBOMView) {
							bomList.add((TCComponentBOMView) comp);
						}
					}
					if (bomList.size() > 0) {
						return bomList.toArray(new TCComponentBOMView[bomList.size()]);
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
