package com.uds.rac.custom.views;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
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

import com.teamcenter.rac.util.MessageBox;
import com.uds.sjec.bean.ExcelToDBBean;

public class AssignTask1 {
	private static AssignTask1UI m_frame;
	
	private List<TableBean> m_beanList;
	
	public void ShowUI(JDialog parentFrame, TableBean tableBean) {
		try {
			if (m_frame == null) {
				m_frame = new AssignTask1UI(parentFrame, tableBean);
			} else {
				m_frame.UpdateTableData(tableBean);
			}
			m_frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post("出错：" + e.getMessage() + "\n详细信息请联系管理员查看控制台", "提示", MessageBox.INFORMATION);
			return;
		}
	}
	
	class AssignTask1UI extends JDialog {
		
		private static final long serialVersionUID = 1L;
		private JPanel contentPane;
		private JTable table;
		
		public AssignTask1UI(JDialog parentFrame, TableBean tableBean) {
			super(parentFrame, "", true);
			
			setTitle("参数查看");
			setResizable(false);
			setType(Type.UTILITY);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Rectangle rectangle = parentFrame.getBounds();
			setBounds(rectangle.x + rectangle.width + 10, rectangle.y, 400, 450);
//			setBounds(100, 100, 400, 450);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			table = new JTable();
			m_beanList = new ArrayList<TableBean>();
			m_beanList.add(null);
			m_beanList.add(null);
			IntiTableData(tableBean);
			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setBounds(10, 10, 374, 401);
			contentPane.add(scrollPane);
		}
		
		// 重写系统方法dispose，为了设置frame为null
		public void dispose() {
	 		super.dispose();
	 		m_frame = null;
	 	}
		
		private void IntiTableData(TableBean tableBean) {
			Vector<Object> columnNames = new Vector<Object>();
			columnNames.add("序号");
			columnNames.add("参数代号");
			columnNames.add("参数名称");
			columnNames.add("参数值");
			columnNames.add("参数类型");
			
			Vector<Vector<Object>> rowDatas = new Vector<Vector<Object>>();
			List<ExcelToDBBean> list = new ArrayList<ExcelToDBBean>(tableBean.map.values());
			Collections.sort(list);
			int index = 1;
			for (ExcelToDBBean bean : list) {
				Vector<Object> vector1 = new Vector<Object>();
				vector1.add(index++ + "");
				vector1.add(bean.paramCode);
				vector1.add(bean.paramName);
				vector1.add(bean.paramValue);
				vector1.add(bean.paramType);
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
			table.getColumnModel().getColumn(0).setMaxWidth(40);
			
			DefaultTableCellRenderer r = new DefaultTableCellRenderer();
			r.setHorizontalAlignment(JLabel.LEFT);
			table.setDefaultRenderer(Object.class, r);
		}
		
		public void UpdateTableData(TableBean tableBean) {
			UpdateTableData(table, tableBean);
		}
		
		private void UpdateTableData(JTable table, TableBean tableBean) {
			EmptyTable(table);
			
			DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
			List<ExcelToDBBean> list = new ArrayList<ExcelToDBBean>(tableBean.map.values());
			Collections.sort(list);
			int index = 1;
			for (ExcelToDBBean bean : list) {
				Vector<Object> vector = new Vector<Object>();
				vector.add(index++ + "");
				vector.add(bean.paramCode);
				vector.add(bean.paramName);
				vector.add(bean.paramValue);
				vector.add(bean.paramType);
				newTableModel.insertRow(newTableModel.getRowCount(), vector);
			}
		}
		
		private void EmptyTable(JTable table) {
			DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
			while(newTableModel.getRowCount() != 0){
				newTableModel.removeRow(newTableModel.getRowCount() - 1);
			}
		}
	}
}
