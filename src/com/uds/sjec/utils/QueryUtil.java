package com.uds.sjec.utils;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class QueryUtil {

	// 获取查询器
	public static TCComponentQuery getTCComponentQuery(String query_class) {
		try {
			TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
			String QUERY_CLASS = query_class;
			TCComponentQueryType typeComponent = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
			TCComponentQuery query = null;
			TCComponent queryComponent = typeComponent.find(QUERY_CLASS);
			if (queryComponent == null) {
				MessageBox.post("获取查询器失败", "出错", MessageBox.ERROR);
				return null;
			}
			query = (TCComponentQuery) queryComponent;
			return query;
		} catch (TCException e) {
			MessageBox.post("获取查询器" + query_class + "异常", "出错", MessageBox.ERROR);
		}
		return null;

	}

	/**
	 * @param query
	 * @param values
	 *            属性名称数组
	 * @param values
	 *            属性值数组
	 * @return 返回的是查询得到的结果输租
	 * @throws TCException
	 */
	public static TCComponent[] getSearchResult(TCComponentQuery query, String[] propertyName, String[] values) {

		TCComponent[] results = null;
		try {
			results = query.execute(propertyName, values);
		} catch (TCException e) {
			MessageBox.post("查询结果异常", "出错", MessageBox.ERROR);
		}
		return results;
	}

}
