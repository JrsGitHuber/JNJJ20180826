package com.uds.sjec.view;

import java.util.ArrayList;
import java.util.Map;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.uds.common.exceptions.CalculateException;
import com.uds.common.utils.MathUtil;

public class BOMViewModel extends AbstractTreeTableModel implements TreeTableModel {

	// ��������
	static protected String[] colsName = { "BOM��", "װ����", "�����ID", "���������", "��������", "�÷�����", "������ʽ", "����ͺ�", "�汾����", "�汾��������", "�汾����",
			"������", "��ע", "�༭��" };

	// ��������
	@SuppressWarnings("rawtypes")
	static protected Class[] colsType = { TreeTableModel.class, String.class, String.class, String.class, String.class, String.class,
			String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };

	public BOMViewModel(TCComponentBOMLine bomLine, Map<String, String> map) {
		super(new BOMLineNode(bomLine, map));
	}

	protected TCComponentBOMLine getBomLine(Object node) {
		BOMLineNode bomlineNode = ((BOMLineNode) node);
		return bomlineNode.getBOMLine();
	}

	protected Object[] getChildren(Object node) {
		BOMLineNode bomlLineNode = (BOMLineNode) node;
		return bomlLineNode.getChildren();
	}

	@Override
	public int getChildCount(Object node) {
		Object[] children = getChildren(node);
		return (children == null) ? 0 : children.length;
	}

	@Override
	public Object getChild(Object node, int i) {
		return getChildren(node)[i];
	}

	@Override
	public int getColumnCount() {
		return colsName.length;
	}

	@Override
	public String getColumnName(int column) {
		return colsName[column];
	}

	@SuppressWarnings("rawtypes")
	public Class getColumnClass(int column) {
		return colsType[column];
	}

	@Override
	public Object getValueAt(Object node, int column) {
		TCComponentBOMLine bomLine = getBomLine(node);
		try {
			switch (column) {
			case 0:
				return bomLine.getProperty("bl_indented_title");// BOM��
			case 1:
				return bomLine.getProperty("S2_bl_asmno"); // װ����
			case 2:
				return bomLine.getProperty("bl_item_item_id");// �����ID
			case 3:
				return bomLine.getProperty("bl_item_object_name"); // ���������
			case 4:
				String note1 = bomLine.getProperty("S2_bl_vc");
				String note2 = bomLine.getProperty("S2_bl_vc1");
				String note3 = bomLine.getProperty("S2_bl_vc2");
				String note4 = bomLine.getProperty("S2_bl_vc3");
				String note5 = bomLine.getProperty("S2_bl_vc4");
				String variableCondition = note1 + note2 + note3 + note4 + note5;
				return variableCondition;// ��������
			case 5:
				return bomLine.getProperty("Usage_Quantity");// �÷�����
			case 6:
				return bomLine.getProperty("S2_bl_ylgsl"); // ������ʽ
			case 7:
				return bomLine.getProperty("bl_item_s2_Spec");// ����ͺ�
			case 8:
				return bomLine.getProperty("bl_item_s2_Rev_Matl"); // �汾����
			case 9:
				return bomLine.getProperty("bl_item_s2_Rev_Manu_Type");// �汾��������
			case 10:
				return bomLine.getProperty("bl_item_s2_Rev_Weight"); // �汾����
			case 11:
				bomLine.getProperty("S2_bl_zz");// ������
			case 12:
				bomLine.getProperty("bl_item_s2_Note"); // ��ע
			case 13:
				bomLine.getProperty("bl_rev_s2_Rev_Name_BJ"); // �༭��
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * BOMLineNode��BOMLine������������������ǽ���ί�и�BOMLine������������໯����
	 * ������ά��Ŀ¼��Ŀ¼�Ļ��棬��˱����ڳ����ڼ��ظ��ø��²��BOMLine.
	 * 
	 * @author DDL
	 * 
	 */
	public static class BOMLineNode {

		/**
		 * ����Bom��bomLine
		 */
		TCComponentBOMLine bomLine;
		Map<String, String> map;
		Object[] children;

		public BOMLineNode(TCComponentBOMLine bomLine, Map<String, String> map) {
			this.bomLine = bomLine;
			this.map = map;
		}

		/**
		 * ����������JTree����ʾ��Ҷ�ӵ��ַ���.
		 */
		public String toString() {
			return bomLine.toString();
		}

		public TCComponentBOMLine getBOMLine() {
			return bomLine;
		}

		/**
		 * ���غ��ӣ�����������ں��� ivar.
		 */
		protected Object[] getChildren() {
			if (children != null) {
				return children;
			}
			try {
				AIFComponentContext[] context = bomLine.getChildren();
				if (context != null) {
					// �ȱ���һ��,��BOM���ϵĴ洢����
					ArrayList<TCComponentBOMLine> bomLineList = new ArrayList<TCComponentBOMLine>();
					for (int i = 0; i < context.length; i++) {
						TCComponentBOMLine bomLine = (TCComponentBOMLine) context[i].getComponent();
						String note1 = bomLine.getProperty("S2_bl_vc");
						String note2 = bomLine.getProperty("S2_bl_vc1");
						String note3 = bomLine.getProperty("S2_bl_vc2");
						String note4 = bomLine.getProperty("S2_bl_vc3");
						String note5 = bomLine.getProperty("S2_bl_vc4");
						String variableCondition = note1 + note2 + note3 + note4 + note5;
						if (variableCondition.equals("")) {
							bomLineList.add(bomLine);
						} else {
							for (String key : map.keySet()) {
								String keyStr = key + "==";
								if (variableCondition.contains(keyStr)) {
									variableCondition = variableCondition.replaceAll(keyStr, map.get(key)+"==");
								}
							}
							try {
								if (MathUtil.PassCondition(variableCondition)) {
									bomLineList.add(bomLine);
								}
							} catch (CalculateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
					// TODO Jr children��Ϊ������Ҫ����Ҫ�о���
					children = new BOMLineNode[bomLineList.size()];
					for (int i = 0; i < bomLineList.size(); i++) {
						BOMLineNode bomlLineNode = new BOMLineNode(bomLineList.get(i), map);
						children[i] = bomlLineNode;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return children;
		}
	}
}
