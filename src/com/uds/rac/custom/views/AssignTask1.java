package com.uds.rac.custom.views;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class AssignTask1 extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	
	private List<TableBean> m_beanList;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					AssignTask1 frame = new AssignTask1();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public AssignTask1(JDialog parentFrame) {
		super(parentFrame, "", true);
		
		setTitle("参数查看");
		setResizable(false);
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Rectangle rectangle = parentFrame.getBounds();
		setBounds(rectangle.x + rectangle.width + 10, rectangle.y, 400, 450);
//		setBounds(100, 100, 400, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		table = new JTable();
		m_beanList = new ArrayList<TableBean>();
		m_beanList.add(new TableBean("00012001", "A"));
		m_beanList.add(new TableBean("00012002", "B"));
		IntiTableData(m_beanList);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 10, 374, 401);
		contentPane.add(scrollPane);
	}
	
	private void IntiTableData(List<TableBean> beanList) {
		Vector<Object> columnNames = new Vector<Object>();
		columnNames.add("物料ID");
		columnNames.add("当前版本");
		
		Vector<Vector<Object>> rowDatas = new Vector<Vector<Object>>();
		for (TableBean bean : beanList) {
			Vector<Object> vector1 = new Vector<Object>();
			vector1.add(bean.item_id);
			vector1.add(bean.item_revision_id);
			rowDatas.add(vector1);
		}
		
		DefaultTableModel newTableModel = new DefaultTableModel(rowDatas, columnNames) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		table = new JTable();
		table.setModel(newTableModel);
		table.getColumnModel().getColumn(1).setMaxWidth(55);
		
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, r);
	}
	
//	// 重写系统方法dispose，为了设置frame为null
//	public void dispose() {
// 		super.dispose();
// 		m_frame = null;
// 	}
}
