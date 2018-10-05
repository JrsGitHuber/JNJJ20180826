package com.uds.Jr.TreeTable;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import com.uds.sjec.bean.BomToPreviewBean;

public class TreeTableModel extends DefaultTreeTableModel {
	// 属性名称
	static protected String[] _names = { "BOM行", "装配编号", "零组件ID", "零组件名称", "变量条件",
		"用法数量", "用量公式", "规格型号", "版本材料", "版本制造类型", "版本重量", "总重量", "备注",
		"编辑人" };

	// 属性类型
	@SuppressWarnings("rawtypes")
	static protected Class[] _types = { String.class, String.class, String.class,
		String.class, String.class, String.class, String.class, String.class, String.class,
		String.class, String.class, String.class, String.class, String.class };

	public TreeTableModel(TreeTableNode node) {
		super(node);
	}

    /**
     * 列的类型
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Class getColumnClass(int col) {
        return _types[col];
    }

    /**
     * 列的数量
     */
    @Override
    public int getColumnCount() {
        return _names.length;
    }

    /**
     * 表头显示的内容
     */
    @Override
    public String getColumnName(int column) {
        return _names[column];
    }

    /**
     * 返回在单元格中显示的Object
     */
    @Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof DefaultMutableTreeTableNode) {
            DefaultMutableTreeTableNode mutableNode = (DefaultMutableTreeTableNode) node;
            Object o = mutableNode.getUserObject();
            if (o != null && o instanceof BomToPreviewBean) {
            	BomToPreviewBean bean = (BomToPreviewBean) o;
            	return bean.propertyList.get(column);
            }
        }
        return "";
    }

    /**
     * 设置所有单元格都不能编辑
     *
     * @param the node (i.e. row) for which editing is to be determined
     * @param the column for which editing is to be determined
     * @return false
     */
    @Override
    public boolean isCellEditable(Object node, int column) {
        return false;
    }
}
