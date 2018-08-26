package com.uds.sjec.view;

import java.util.ArrayList;
import java.util.Map;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.uds.common.exceptions.CalculateException;
import com.uds.common.utils.MathUtil;

public class BOMViewModel extends AbstractTreeTableModel implements TreeTableModel {

	// 属性名称
	static protected String[] colsName = { "BOM行", "装配编号", "零组件ID", "零组件名称", "变量条件", "用法数量", "用量公式", "规格型号", "版本材料", "版本制造类型", "版本重量",
			"总重量", "备注", "编辑人" };

	// 属性类型
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
				return bomLine.getProperty("bl_indented_title");// BOM行
			case 1:
				return bomLine.getProperty("S2_bl_asmno"); // 装配编号
			case 2:
				return bomLine.getProperty("bl_item_item_id");// 零组件ID
			case 3:
				return bomLine.getProperty("bl_item_object_name"); // 零组件名称
			case 4:
				String note1 = bomLine.getProperty("S2_bl_vc");
				String note2 = bomLine.getProperty("S2_bl_vc1");
				String note3 = bomLine.getProperty("S2_bl_vc2");
				String note4 = bomLine.getProperty("S2_bl_vc3");
				String note5 = bomLine.getProperty("S2_bl_vc4");
				String variableCondition = note1 + note2 + note3 + note4 + note5;
				return variableCondition;// 变量条件
			case 5:
				return bomLine.getProperty("Usage_Quantity");// 用法数量
			case 6:
				return bomLine.getProperty("S2_bl_ylgsl"); // 用量公式
			case 7:
				return bomLine.getProperty("bl_item_s2_Spec");// 规格型号
			case 8:
				return bomLine.getProperty("bl_item_s2_Rev_Matl"); // 版本材料
			case 9:
				return bomLine.getProperty("bl_item_s2_Rev_Manu_Type");// 版本制造类型
			case 10:
				return bomLine.getProperty("bl_item_s2_Rev_Weight"); // 版本重量
			case 11:
				bomLine.getProperty("S2_bl_zz");// 总重量
			case 12:
				bomLine.getProperty("bl_item_s2_Note"); // 备注
			case 13:
				bomLine.getProperty("bl_rev_s2_Rev_Name_BJ"); // 编辑人
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * BOMLineNode是BOMLine类的派生——尽管我们将它委托给BOMLine对象而不是子类化它。
	 * 它用于维护目录子目录的缓存，因此避免在呈现期间重复访更下层的BOMLine.
	 * 
	 * @author DDL
	 * 
	 */
	public static class BOMLineNode {

		/**
		 * 超级Bom的bomLine
		 */
		TCComponentBOMLine bomLine;
		Map<String, String> map;
		Object[] children;

		public BOMLineNode(TCComponentBOMLine bomLine, Map<String, String> map) {
			this.bomLine = bomLine;
			this.map = map;
		}

		/**
		 * 返回用于在JTree中显示此叶子的字符串.
		 */
		public String toString() {
			return bomLine.toString();
		}

		public TCComponentBOMLine getBOMLine() {
			return bomLine;
		}

		/**
		 * 加载孩子，将结果缓存在孩子 ivar.
		 */
		protected Object[] getChildren() {
			if (children != null) {
				return children;
			}
			try {
				AIFComponentContext[] context = bomLine.getChildren();
				if (context != null) {
					// 先遍历一遍,将BOM符合的存储起来
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
					
					// TODO Jr children或为界面需要，需要研究下
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
