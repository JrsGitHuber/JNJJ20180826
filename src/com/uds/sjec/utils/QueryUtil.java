package com.uds.sjec.utils;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class QueryUtil {

	// ��ȡ��ѯ��
	public static TCComponentQuery getTCComponentQuery(String query_class) {
		try {
			TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
			String QUERY_CLASS = query_class;
			TCComponentQueryType typeComponent = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
			TCComponentQuery query = null;
			TCComponent queryComponent = typeComponent.find(QUERY_CLASS);
			if (queryComponent == null) {
				MessageBox.post("��ȡ��ѯ��ʧ��", "����", MessageBox.ERROR);
				return null;
			}
			query = (TCComponentQuery) queryComponent;
			return query;
		} catch (TCException e) {
			MessageBox.post("��ȡ��ѯ��" + query_class + "�쳣", "����", MessageBox.ERROR);
		}
		return null;

	}

	/**
	 * @param query
	 * @param values
	 *            ������������
	 * @param values
	 *            ����ֵ����
	 * @return ���ص��ǲ�ѯ�õ��Ľ������
	 * @throws TCException
	 */
	public static TCComponent[] getSearchResult(TCComponentQuery query, String[] propertyName, String[] values) {

		TCComponent[] results = null;
		try {
			results = query.execute(propertyName, values);
		} catch (TCException e) {
			MessageBox.post("��ѯ����쳣", "����", MessageBox.ERROR);
		}
		return results;
	}

}
