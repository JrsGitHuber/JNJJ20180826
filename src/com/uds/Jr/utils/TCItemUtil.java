package com.uds.Jr.utils;

import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCSession;

public class TCItemUtil {
	public static TCComponentItem CreateItem(TCSession session, String itemType, String itemID, String itemName, String itemDesc) throws Exception {
		TCComponentItemType item_type = (TCComponentItemType) session.getTypeComponent(itemType);
		String newRev = item_type.getNewRev(null);
		TCComponentItem newItem = item_type.create(itemID, newRev, itemType, itemName, itemDesc, null);
		return newItem;
	}
}
