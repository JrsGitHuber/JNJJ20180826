package com.uds.rac.custom.views;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.workflow.commands.newprocess.NewProcessCommand;
import com.teamcenter.rac.workflow.commands.newprocess.NewProcessDialog;
import com.uds.Jr.utils.TCBomUtil;
import com.uds.Jr.utils.TCItemUtil;
import com.uds.Jr.utils.TCQueryUtil;
import com.uds.sjec.bean.ExcelToDBBean;
import com.uds.sjec.common.CommonFunction;
import com.uds.sjec.common.ConstDefine;
import com.uds.sjec.utils.TipsUI;

public class AssignTask {
	public static String m_errorMessage;
	private static Connection m_connection;
	private static AssignTaskUI m_frame;
	private List<TableBean> m_beanList;
	
	public void ShowUI(Rectangle rectangle) {
		m_errorMessage = "";
		
		try {
			if (m_connection == null) {
				m_connection = CommonFunction.GetDBConnection();
				if (!CommonFunction.m_errorMessage.equals("")) {
					MessageBox.post(CommonFunction.m_errorMessage, "��ʾ", MessageBox.INFORMATION);
					return;
				}
			}
			
			m_beanList = GetBeanList();
			if (!m_errorMessage.equals("")) {
				MessageBox.post(m_errorMessage, "��ʾ", MessageBox.INFORMATION);
				return;
			}
			
			if (m_frame == null) {
				m_frame = new AssignTaskUI(rectangle);
			}
			m_frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post("����" + e.getMessage() + "\n��ϸ��Ϣ����ϵ����Ա�鿴����̨", "��ʾ", MessageBox.INFORMATION);
			return;
		}
	}
	
	private List<TableBean> GetBeanList() {
		m_errorMessage = "";
		List<TableBean> list = new ArrayList<TableBean>();
		
		String sqlStr = "select * from T_AB_ENTRY where AB_SIGN = 'Y' and CHANGE_SIGN = 'N' and ERP_SIGN = 'N'";
		try (
				PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
				ResultSet resultSet = pstmt.executeQuery();
				) {
			while (resultSet.next()) {
				TableBean bean = new TableBean();
				bean.EQUIPMENT_NO = resultSet.getString("EQUIPMENT_NO");
				bean.CONTRACT_NO = resultSet.getString("CONTRACT_NO");
				bean.CUSTOMER_NAME = resultSet.getString("CUSTOMER_NAME");
				bean.PRODUCT_MODEL = resultSet.getString("PRODUCT_MODEL");
				bean.PRODUCT_NAME = resultSet.getString("PRODUCT_NAME");
				bean.map = GetBeanList1(bean.EQUIPMENT_NO);
				if (!m_errorMessage.equals("")) {
					return null;
				}
				list.add(bean);
			}
		} catch (Exception e) {
			m_errorMessage = "�������ݿ����" + e.getMessage();
			e.printStackTrace();
			return null;
		}
		
		return list;
	}
	
	private Map<String, ExcelToDBBean> GetBeanList1(String EQUIPMENT_NO) {
		m_errorMessage = "";
		Map<String, ExcelToDBBean> map = new HashMap<String, ExcelToDBBean>();
		
		String sqlStr = "select * from T_AB_PARAMETER where EQUIPMENT_NO = '" + EQUIPMENT_NO + "'";
		try (
				PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
				ResultSet resultSet = pstmt.executeQuery();
				) {
			while (resultSet.next()) {
				ExcelToDBBean bean = new ExcelToDBBean();
				bean.paramCode = resultSet.getString("PARAM_CODE");
				bean.paramName = resultSet.getString("PARAM_NAME");
				bean.paramValue = resultSet.getString("PARAM_VALUE");
				bean.paramType = resultSet.getString("PARAM_TYPE");
				map.put(bean.paramCode, bean);
			}
		} catch (Exception e) {
			m_errorMessage = "�������ݿ����" + e.getMessage();
			e.printStackTrace();
			return null;
		}
		
		if (map.size() == 0) {
			m_errorMessage = "û�в�ѯ�����ݣ�" + sqlStr;
			return null;
		}
		return map;
	}
	
	class AssignTaskUI extends JDialog {
		
		private static final long serialVersionUID = 1L;
		private JPanel contentPane;
		private JTable table;
		private JPopupMenu popupMenu;
		private JButton btnNewButton;
		private JButton btnNewButton_1;

		/**
		 * Create the frame.
		 */
		public AssignTaskUI(Rectangle rectangle) {
			CreatePopupMenu();
			
			setTitle("�������");
			setAlwaysOnTop(true);
			setResizable(false);
			setType(Type.UTILITY);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			// ���ݴ�����Rectangle���ô���λ��
	        int centerX = rectangle.x + rectangle.width / 2;
	        int centerY = rectangle.y + rectangle.height / 2;
	        setBounds(centerX - 200, centerY - 225, 400, 450);
//			setBounds(100, 100, 400, 450);
	        
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			table = new JTable();
			InitTableData(m_beanList);
			table.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					// ������Ҽ���BUTTON3��
					if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
						int focusedRowIndex = table.rowAtPoint(e.getPoint());
						if (focusedRowIndex == -1) {
							return;
						}
						
						// ���°�ť״̬
						DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
						String item_id = newTableModel.getValueAt(focusedRowIndex, 0).toString();
						if (item_id.equals("")) {
							btnNewButton.setEnabled(true);
							btnNewButton_1.setEnabled(false);
						} else {
							btnNewButton.setEnabled(false);
							btnNewButton_1.setEnabled(true);
						}
						
						// �������ѡ����Ϊ��ǰ�Ҽ��������
						table.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
						// �����˵�
						popupMenu.show(table, e.getX(), e.getY());
					}
					// ����������BUTTON1��
					if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
						int focusedRowIndex = table.rowAtPoint(e.getPoint());
						if (focusedRowIndex == -1) {
							return;
						}
						
						// ���°�ť״̬
						DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
						String item_id = newTableModel.getValueAt(focusedRowIndex, 0).toString();
						if (item_id.equals("")) {
							btnNewButton.setEnabled(true);
							btnNewButton_1.setEnabled(false);
						} else {
							btnNewButton.setEnabled(false);
							btnNewButton_1.setEnabled(true);
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
			
			btnNewButton = new JButton("�½����õ�");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final int row = table.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(contentPane, "û��ѡ����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					final DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
					final String EQUIPMENT_NO = newTableModel.getValueAt(row, 2).toString();
					if (EQUIPMENT_NO == null || EQUIPMENT_NO.equals("")) {
						JOptionPane.showMessageDialog(contentPane, "��ǰû���豸��", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					Thread thread = new Thread()
					{
						@Override
						public void run() {
							try {
								TCComponentItem newItem = CreateNewItem(EQUIPMENT_NO);
								if (!m_errorMessage.equals("")) {
									JOptionPane.showMessageDialog(contentPane, m_errorMessage, "��ʾ", JOptionPane.INFORMATION_MESSAGE);
									return;
								}
								if (newItem == null) {
									return;
								}
								String item_id = newItem.getProperty("item_id");
								
								// �������ݿ�
								UpdateABDB(EQUIPMENT_NO, item_id);
								if (!m_errorMessage.equals("")) {
									JOptionPane.showMessageDialog(contentPane, m_errorMessage, "��ʾ", JOptionPane.INFORMATION_MESSAGE);
									return;
								}
								
								// ԭ���ݿ�������
								TableBean bean = m_beanList.get(row);
								InsertDataToDB(newItem, bean);
								if (!m_errorMessage.equals("")) {
									JOptionPane.showMessageDialog(contentPane, m_errorMessage, "��ʾ", JOptionPane.INFORMATION_MESSAGE);
									return;
								}
								
								JOptionPane.showMessageDialog(contentPane, "���õ������ɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
								btnNewButton.setEnabled(false);
								btnNewButton_1.setEnabled(true);
								
								// ���ý�����ʾ
								newTableModel.setValueAt(item_id, row, 0);
							} catch (Exception e) {
								JOptionPane.showMessageDialog(TipsUI.contentPanel, "�½����õ�����" + e.getMessage() + "\n��ϸ��Ϣ����ϵ����Ա�鿴����̨", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
								e.printStackTrace();
							}
//							finally {
//								m_frame.dispose();
//							}
						}
					};
					TipsUI.ShowUI("�����½����õ�...", thread, m_frame);
					
				}
			});
			btnNewButton.setBounds(186, 390, 93, 23);
			btnNewButton.setMargin(new Insets(0, 0, 0, 0));
			contentPane.add(btnNewButton);
			
			btnNewButton_1 = new JButton("���ɵ�����");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int row = table.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(contentPane, "û��ѡ����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
					final String item_id = newTableModel.getValueAt(row, 1).toString();
					if (item_id == null || item_id.equals("")) {
						JOptionPane.showMessageDialog(contentPane, "ѡ���л�δ�������õ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					try {
						TCComponentQuery itemQuery = TCQueryUtil.GetTCComponentQuery(ConstDefine.TC_SESSION, "����� ID");
						TCComponent[] items0 = TCQueryUtil.GetItemByID(itemQuery, item_id);
						if (items0 == null || items0.length == 0) {
							JOptionPane.showMessageDialog(contentPane, "û���ҵ����õ�" + item_id, "��ʾ", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						TCComponentItem newItem = (TCComponentItem)items0[0];
						TCComponentItemRevision revision = newItem.getLatestItemRevision();
						
						// ��������
						InterfaceAIFComponent[] component = new InterfaceAIFComponent[] { revision };
						NewProcessCommand command = new NewProcessCommand(AIFUtility.getCurrentApplication().getDesktop(), 
								AIFUtility.getCurrentApplication(), component);
						new NewProcessDialog(command);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, "����ʧ�ܣ�" + e1.getMessage(), "��ʾ", JOptionPane.INFORMATION_MESSAGE);
	                    e1.printStackTrace();
	                    return;
					}
					
					dispose();
				}
			});
			btnNewButton_1.setBounds(291, 390, 93, 23);
			btnNewButton_1.setMargin(new Insets(0, 0, 0, 0));
			btnNewButton_1.setEnabled(false);
			contentPane.add(btnNewButton_1);
			
			JButton btnNewButton_2 = new JButton("ˢ�����õ�");
			btnNewButton_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						m_beanList = GetBeanList();
						if (!m_errorMessage.equals("")) {
							JOptionPane.showMessageDialog(contentPane, m_errorMessage, "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		                    return;
						}
						UpdateTableData(table, m_beanList);
						btnNewButton.setEnabled(true);
						btnNewButton_1.setEnabled(false);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, "����ʧ�ܣ�" + e1.getMessage(), "��ʾ", JOptionPane.INFORMATION_MESSAGE);
	                    e1.printStackTrace();
	                    return;
					}
				}
			});
			btnNewButton_2.setBounds(10, 390, 93, 23);
			btnNewButton_2.setMargin(new Insets(0, 0, 0, 0));
			contentPane.add(btnNewButton_2);
		}
		
		private void InsertDataToDB(TCComponentItem newItem, TableBean bean) throws Exception {
			m_errorMessage = "";
			
			String item_id = newItem.getProperty("item_id");
			String item_revision_id = newItem.getLatestItemRevision().getProperty("item_revision_id");
			String createdDate = newItem.getProperty("creation_date");
			String createdUser = ConstDefine.TC_SESSION.getUserName();
			
			String sqlStr = "insert into t_configurationlist(configurationlist_id,configurationlist_rev,contract_no,device_no,"
					+ "project_name,product_type,creation_user,creation_date) values('" + item_id + "','" + item_revision_id + "','"
					+ bean.CONTRACT_NO + "','" + bean.EQUIPMENT_NO + "','" + bean.PRODUCT_NAME + "','" + bean.PRODUCT_MODEL + "','" + createdUser + "','" + createdDate
					+ "')"; // �������ͺű�����������ͺŴ���;
			try (
					PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
					) {
				pstmt.executeUpdate();
				InsertDataToDB1(bean.map, item_id, createdDate, createdUser);
			} catch (Exception e) {
				m_errorMessage = "�������ݿ����" + e.getMessage();
				e.printStackTrace();
				return;
			}
		}
		
		private void InsertDataToDB1(Map<String, ExcelToDBBean> map, String item_id, String createdDate, String createdUser) {
			m_errorMessage = "";
			
			for (ExcelToDBBean bean : map.values()) {
				String sqlStr = "insert into t_configurationlist_parameter(param_code,configurationlist_id,param_name,"
						+ "param_value,param_classification,creation_user,creation_date) values('"
						+ bean.paramCode + "','" + item_id + "','" + bean.paramName + "','" + bean.paramValue + "','"
						+ bean.paramType + "','" + createdUser + "','" + createdDate + "')";
				try (
						PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
						) {
					pstmt.executeUpdate();
				} catch (Exception e) {
					m_errorMessage = "�������ݿ����" + e.getMessage();
					e.printStackTrace();
					return;
				}
			}
		}
		
		private void InitTableData(List<TableBean> beanList) {
			Vector<Object> columnNames = new Vector<Object>();
			columnNames.add("���õ���");
			columnNames.add("��ͬ��");
			columnNames.add("�豸��");
			columnNames.add("�ͻ�����");
			
			Vector<Vector<Object>> rowDatas = new Vector<Vector<Object>>();
			for (TableBean bean : beanList) {
				Vector<Object> vector1 = new Vector<Object>();
				vector1.add("");
				vector1.add(bean.CONTRACT_NO);
				vector1.add(bean.EQUIPMENT_NO);
				vector1.add(bean.CUSTOMER_NAME);
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
			
			// ���ص�һ��
//			TableColumn column = table.getColumnModel().getColumn(0);
//			column.setMinWidth(0);
//			column.setMaxWidth(0);
			
			DefaultTableCellRenderer r = new DefaultTableCellRenderer();
			r.setHorizontalAlignment(JLabel.LEFT);
			table.setDefaultRenderer(Object.class, r);
		}
		
		private void CreatePopupMenu() {
			popupMenu = new JPopupMenu();

			JMenuItem delMenItem = new JMenuItem();
			delMenItem.setText("  �����鿴");
			delMenItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					int row = table.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(contentPane, "û��ѡ����", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
					String value = newTableModel.getValueAt(row, 0).toString();
					System.out.println(value);
					
					try {
						new AssignTask1().ShowUI(m_frame, m_beanList.get(row));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			popupMenu.add(delMenItem);
	    }
		
		// ��дϵͳ����dispose��Ϊ������frameΪnull
		public void dispose() {
			if (m_connection != null) {
	 			try {
					m_connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(contentPane, "�ر����ݿ����ӳ���" + e.getMessage(), "��ʾ", JOptionPane.INFORMATION_MESSAGE);
				}
	 			m_connection = null;
	 		}
			
	 		super.dispose();
	 		m_frame = null;
	 	}
	}
	
	private void UpdateABDB(String EQUIPMENT_NO, String item_id) {
		m_errorMessage = "";
		
		String sqlStr = "update T_AB_ENTRY set CONFIG_ID = ?, ERP_SIGN = ? where EQUIPMENT_NO = ?";
		try (
				PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
				) {
			pstmt.setString(1, item_id);
			pstmt.setString(2, "Y");
            pstmt.setString(3, EQUIPMENT_NO);
			pstmt.executeUpdate();
		} catch (Exception e) {
			m_errorMessage = "�������ݿ����" + e.getMessage();
			e.printStackTrace();
			return;
		}
	}
	
	private TCComponentItem CreateNewItem(String EQUIPMENT_NO) throws Exception {
		// ��EAP��ȡ���õ�ID�����⴦������Ҳ�Ǽ���ִ�г���
		String EAPID = GetEAPID(EQUIPMENT_NO);
		if (!m_errorMessage.equals("") || EAPID == null || EAPID.equals("")) {
			if (m_errorMessage.length() > 50) {
				m_errorMessage = m_errorMessage.substring(0, 50);
			}
			String errorMessage = m_errorMessage.length() == 0 ? "��EAP�л�ȡ���õ���ʧ��" : "��EAP�л�ȡ���õ���ʧ�ܣ�" + m_errorMessage;
			int n = JOptionPane.showConfirmDialog(m_frame.contentPane, errorMessage + "\n�Ƿ�����ˮ�봴�����õ���", "��ʾ", JOptionPane.YES_NO_OPTION);
			m_errorMessage = "";
			if (n == 0) {
				EAPID = "";
			} else {
				return null;
			}
		}
		// ��֯���õ�
		TCComponentQuery itemQuery = TCQueryUtil.GetTCComponentQuery(ConstDefine.TC_SESSION, "����� ID");
		TCComponent[] items0 = TCQueryUtil.GetItemByID(itemQuery, EAPID);
		if (items0 != null && items0.length != 0) {
			m_errorMessage = "���õ�" + EAPID + "�Ѿ�����";
			return null;
		}
		TCComponentItem newItem = TCItemUtil.CreateItem(ConstDefine.TC_SESSION, "S2_CFG", EAPID, "���õ�", "");
		if (newItem == null) {
			m_errorMessage = "�������õ�ʧ��";
			return null;
		}
		
		// �������õ�
		TCComponentItem electricalItem = null;
		String electricalItemID = newItem.getProperty("item_id") + ConstDefine.TC_GROUP_ELECTRICAL;
		items0 = TCQueryUtil.GetItemByID(itemQuery, electricalItemID);
		if (items0 != null && items0.length != 0) {
			electricalItem = (TCComponentItem)items0[0];
		} else {
			electricalItem = TCItemUtil.CreateItem(ConstDefine.TC_SESSION, "S2_CFG", electricalItemID, "�������õ�VI", "");
			if (electricalItem == null) {
				m_errorMessage = "�����������õ�ʧ��";
				return null;
			}
		}
		// ��е���õ�
		TCComponentItem mechanicalItem = null;
		String mechanicalItemID = newItem.getProperty("item_id") + ConstDefine.TC_GROUP_MECHANICAL;
		items0 = TCQueryUtil.GetItemByID(itemQuery, mechanicalItemID);
		if (items0 != null && items0.length != 0) {
			mechanicalItem = (TCComponentItem)items0[0];
		} else {
			mechanicalItem = TCItemUtil.CreateItem(ConstDefine.TC_SESSION, "S2_CFG", mechanicalItemID, "��е���õ�VI", "");
			if (mechanicalItem == null) {
				m_errorMessage = "������е���õ�ʧ��";
				return null;
			}
		}
		
		// ����õ�BOM
		TCComponentBOMLine configListTopBomLine = TCBomUtil.GetTopBomLine(ConstDefine.TC_SESSION, newItem.getLatestItemRevision(), "��ͼ");
		if (configListTopBomLine == null) {
			// û����ͼ��ȥ����
			configListTopBomLine = TCBomUtil.SetBOMViewForItemRev(ConstDefine.TC_SESSION, newItem.getLatestItemRevision());
		}else{
			if(configListTopBomLine.hasChildren()){
				AIFComponentContext[] children = configListTopBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					bomLine.cut();
				}
				// �����ȹر���ͼ�������´������BomLine�ᰴ��֮ǰ������(���ұ������)
				TCBomUtil.CloseWindow(configListTopBomLine);
				configListTopBomLine = TCBomUtil.GetTopBomLine(ConstDefine.TC_SESSION, newItem.getLatestItemRevision(), "��ͼ");
			}
		}
		configListTopBomLine.add(electricalItem, "");
		configListTopBomLine.add(mechanicalItem, "");
		TCBomUtil.CloseWindow(configListTopBomLine);
		
		// �����õ���Ϣ��ʾ�ڴ���
		TCComponentFolder componentFolder = new TCComponentFolder();
		@SuppressWarnings({ "deprecation", "static-access" })
		TCComponentFolder newstuffFolder = componentFolder.getNewStuffFolder(ConstDefine.TC_SESSION);
		newstuffFolder.add("contents", newItem);
		return newItem;
	}
	
	private String GetEAPID(String EQUIPMENT_NO) throws Exception {
		m_errorMessage = "";
		
		Class.forName(ConstDefine.EAPDB_CLASSNAME);
		String sqlStr = "select Code from view_contract_BasicInformationEle where SONo= '" + EQUIPMENT_NO + "'";
		try (
				Connection connection = DriverManager.getConnection(ConstDefine.EAPDB_URL, ConstDefine.EAPDB_USER, ConstDefine.EAPDB_PASSWORD);
				PreparedStatement pstmt = connection.prepareStatement(sqlStr);
				ResultSet resultSet = pstmt.executeQuery();
				) {
			if (resultSet.next()) {
				return resultSet.getString("CODE");
			} else {
				m_errorMessage = "û�д�EAP���ݿ��л�ȡ�����õ�";
				return null;
			}
		} catch (Exception e) {
			m_errorMessage = "����EAP���ݿ����" + e.getMessage();
			e.printStackTrace();
			return null;
		}
	}
	
	private void UpdateTableData(JTable table, List<TableBean> beanList) {
		EmptyTable(table);
		
		DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
		for (TableBean bean : beanList) {
			Vector<Object> vector = new Vector<Object>();
			vector.add("");
			vector.add(bean.CONTRACT_NO);
			vector.add(bean.EQUIPMENT_NO);
			vector.add(bean.CUSTOMER_NAME);
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

class TableBean {
	public String EQUIPMENT_NO = "";
	public String CONTRACT_NO = "";
	public String CUSTOMER_NAME = "";
	public String PRODUCT_MODEL = "";
	public String PRODUCT_NAME = "";
	
	public Map<String, ExcelToDBBean> map;
	
	public TableBean() {
		map = new HashMap<String, ExcelToDBBean>();
	}
}
