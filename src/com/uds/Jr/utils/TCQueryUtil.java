package com.uds.Jr.utils;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCSession;

public class TCQueryUtil {
	/**
	 * ��ȡ��ѯ��
	 */
	public static TCComponentQuery GetTCComponentQuery(TCSession session, String queryName) throws Exception {
		TCComponentQueryType typeComponent = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
		TCComponent queryComponent = typeComponent.find(queryName);
		if (queryComponent == null) {
			return null;
		}
		return (TCComponentQuery) queryComponent;
	}
	
	/**
	 * ͨ��itemID��ѯItem
	 */
	public static TCComponent[] GetItemByID(TCComponentQuery query, String itemID) throws Exception {
		TCComponent[] results = query.execute(new String[]{ "����� ID" }, new String[]{ itemID });
		return results;
	}
}
