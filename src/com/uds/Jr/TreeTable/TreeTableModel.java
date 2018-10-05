package com.uds.Jr.TreeTable;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import com.uds.sjec.bean.BomToPreviewBean;

public class TreeTableModel extends DefaultTreeTableModel {
	// ��������
	static protected String[] _names = { "BOM��", "װ����", "�����ID", "���������", "��������",
		"�÷�����", "������ʽ", "����ͺ�", "�汾����", "�汾��������", "�汾����", "������", "��ע",
		"�༭��" };

	// ��������
	@SuppressWarnings("rawtypes")
	static protected Class[] _types = { String.class, String.class, String.class,
		String.class, String.class, String.class, String.class, String.class, String.class,
		String.class, String.class, String.class, String.class, String.class };

	public TreeTableModel(TreeTableNode node) {
		super(node);
	}

    /**
     * �е�����
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Class getColumnClass(int col) {
        return _types[col];
    }

    /**
     * �е�����
     */
    @Override
    public int getColumnCount() {
        return _names.length;
    }

    /**
     * ��ͷ��ʾ������
     */
    @Override
    public String getColumnName(int column) {
        return _names[column];
    }

    /**
     * �����ڵ�Ԫ������ʾ��Object
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
     * �������е�Ԫ�񶼲��ܱ༭
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
