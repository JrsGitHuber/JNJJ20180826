package com.uds.sjec.utils;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCSession;

public class ItemUtil {
	/**
	 * 创建一个Item
	 */
	public static TCComponentItem createtItem(String type, String name, String desc) {

		try {
			AbstractAIFUIApplication app = AIFDesktop.getActiveDesktop().getCurrentApplication();
			TCSession session = (TCSession) app.getSession();
			TCComponentItemType item_type = (TCComponentItemType) session.getTypeComponent(type);
			String newID = item_type.getNewID();
			String newRev = item_type.getNewRev(null);
			// String type = "U8_Formula";
			// String name = "测试";
			// String desc = "";
			TCComponentItem newItem = item_type.create(newID, newRev, type, name, desc, null);
			return newItem;
		} catch (Exception e) {
			return null;
		}
	}

	public static TCComponentItem createtItem(String type, String itemId, String name, String desc) {

		try {
			AbstractAIFUIApplication app = AIFDesktop.getActiveDesktop().getCurrentApplication();
			TCSession session = (TCSession) app.getSession();
			TCComponentItemType item_type = (TCComponentItemType) session.getTypeComponent(type);
			String newRev = item_type.getNewRev(null);
			TCComponentItem newItem = item_type.create(itemId, newRev, type, name, desc, null); // itemId已存在就会报错
			return newItem;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
