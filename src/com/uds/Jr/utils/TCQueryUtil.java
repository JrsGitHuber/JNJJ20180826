package com.uds.Jr.utils;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCSession;

public class TCQueryUtil {
	/**
	 * 获取查询器
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
	 * 通过itemID查询Item
	 */
	public static TCComponent[] GetItemByID(TCComponentQuery query, String itemID) throws Exception {
		TCComponent[] results = query.execute(new String[]{ "零组件 ID" }, new String[]{ itemID });
		return results;
	}
}
