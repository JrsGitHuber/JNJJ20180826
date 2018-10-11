package com.uds.rac.custom.views;

import java.awt.EventQueue;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AssignTask extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private static AssignTask dialog;
	private JPanel contentPane;
	private JTable table;
	private JPopupMenu popupMenu;
	
	private List<TableBean> m_beanList;

	/**
	 * Launch the application.
	 */
	//public static void main(String[] args) {
	public static void ShowUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if (dialog == null) {
						dialog = new AssignTask();
					}
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AssignTask() {
		CreatePopupMenu();
		
		setTitle("任务分派");
		setResizable(false);
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		table = new JTable();
		m_beanList = new ArrayList<TableBean>();
		m_beanList.add(new TableBean("00012001", "A"));
		m_beanList.add(new TableBean("00012002", "B"));
		IntiTableData(m_beanList);
		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// （鼠标右键是BUTTON3）
				if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
					int focusedRowIndex = table.rowAtPoint(e.getPoint());
					if (focusedRowIndex == -1) {
						return;
					}
					
					// 将表格所选项设为当前右键点击的行
					table.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
					// 弹出菜单
					popupMenu.show(table, e.getX(), e.getY());
				}
				// （鼠标左键是BUTTON1）
				if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
					int focusedRowIndex = table.rowAtPoint(e.getPoint());
					if (focusedRowIndex == -1) {
						return;
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) { }

			@Override
			public void mouseReleased(MouseEvent e) { }

			@Override
			public void mouseEntered(MouseEvent e) { }

			@Override
			public void mouseExited(MouseEvent e) { }
			
		});
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 10, 374, 373);
		contentPane.add(scrollPane);
		
		JButton btnNewButton = new JButton("新建配置单");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(186, 390, 93, 23);
		btnNewButton.setMargin(new Insets(0, 0, 0, 0));
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("分派到流程");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_1.setBounds(291, 390, 93, 23);
		btnNewButton_1.setMargin(new Insets(0, 0, 0, 0));
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("刷新配置单");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_2.setBounds(10, 390, 93, 23);
		btnNewButton_2.setMargin(new Insets(0, 0, 0, 0));
		contentPane.add(btnNewButton_2);
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
	
	private void CreatePopupMenu() {  
		popupMenu = new JPopupMenu();

		JMenuItem delMenItem = new JMenuItem();
		delMenItem.setText("  参数查看");
		delMenItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int row = table.getSelectedRow();
				if (row == -1) {
					JOptionPane.showMessageDialog(contentPane, "没有选中行", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				
				DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
				String value = newTableModel.getValueAt(row, 0).toString();
				System.out.println(value);
				
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							AssignTask1 dialog1 = new AssignTask1(dialog);
							dialog1.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		popupMenu.add(delMenItem);
    }
	
	// 重写系统方法dispose，为了设置frame为null
	public void dispose() {
 		super.dispose();
 		dialog = null;
 	}
}

class TableBean {
	public String item_id = "";
	public String item_revision_id = "";
	
	public TableBean(String item_id, String item_revision_id) {
		this.item_id = item_id;
		this.item_revision_id = item_revision_id;
	}
}
