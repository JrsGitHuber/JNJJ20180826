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
					MessageBox.post(CommonFunction.m_errorMessage, "提示", MessageBox.INFORMATION);
					return;
				}
			}
			
			m_beanList = GetBeanList();
			if (!m_errorMessage.equals("")) {
				MessageBox.post(m_errorMessage, "提示", MessageBox.INFORMATION);
				return;
			}
			
			if (m_frame == null) {
				m_frame = new AssignTaskUI(rectangle);
			}
			m_frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post("出错：" + e.getMessage() + "\n详细信息请联系管理员查看控制台", "提示", MessageBox.INFORMATION);
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
			m_errorMessage = "操作数据库出错：" + e.getMessage();
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
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return null;
		}
		
		if (map.size() == 0) {
			m_errorMessage = "没有查询到数据：" + sqlStr;
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
			
			setTitle("任务分派");
			setAlwaysOnTop(true);
			setResizable(false);
			setType(Type.UTILITY);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			// 根据传来的Rectangle设置窗口位置
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
					// （鼠标右键是BUTTON3）
					if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
						int focusedRowIndex = table.rowAtPoint(e.getPoint());
						if (focusedRowIndex == -1) {
							return;
						}
						
						// 更新按钮状态
						DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
						String item_id = newTableModel.getValueAt(focusedRowIndex, 0).toString();
						if (item_id.equals("")) {
							btnNewButton.setEnabled(true);
							btnNewButton_1.setEnabled(false);
						} else {
							btnNewButton.setEnabled(false);
							btnNewButton_1.setEnabled(true);
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
						
						// 更新按钮状态
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
			
			btnNewButton = new JButton("新建配置单");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final int row = table.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(contentPane, "没有选中行", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					final DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
					final String EQUIPMENT_NO = newTableModel.getValueAt(row, 2).toString();
					if (EQUIPMENT_NO == null || EQUIPMENT_NO.equals("")) {
						JOptionPane.showMessageDialog(contentPane, "当前没有设备号", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					Thread thread = new Thread()
					{
						@Override
						public void run() {
							try {
								TCComponentItem newItem = CreateNewItem(EQUIPMENT_NO);
								if (!m_errorMessage.equals("")) {
									JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
									return;
								}
								if (newItem == null) {
									return;
								}
								String item_id = newItem.getProperty("item_id");
								
								// 更新数据库
								UpdateABDB(EQUIPMENT_NO, item_id);
								if (!m_errorMessage.equals("")) {
									JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
									return;
								}
								
								// 原数据库表存数据
								TableBean bean = m_beanList.get(row);
								InsertDataToDB(newItem, bean);
								if (!m_errorMessage.equals("")) {
									JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
									return;
								}
								
								JOptionPane.showMessageDialog(contentPane, "配置单创建成功", "提示", JOptionPane.INFORMATION_MESSAGE);
								btnNewButton.setEnabled(false);
								btnNewButton_1.setEnabled(true);
								
								// 设置界面显示
								newTableModel.setValueAt(item_id, row, 0);
							} catch (Exception e) {
								JOptionPane.showMessageDialog(TipsUI.contentPanel, "新建配置单出错：" + e.getMessage() + "\n详细信息请联系管理员查看控制台", "提示", JOptionPane.INFORMATION_MESSAGE);
								e.printStackTrace();
							}
//							finally {
//								m_frame.dispose();
//							}
						}
					};
					TipsUI.ShowUI("正在新建配置单...", thread, m_frame);
					
				}
			});
			btnNewButton.setBounds(186, 390, 93, 23);
			btnNewButton.setMargin(new Insets(0, 0, 0, 0));
			contentPane.add(btnNewButton);
			
			btnNewButton_1 = new JButton("分派到流程");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int row = table.getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(contentPane, "没有选中行", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					DefaultTableModel newTableModel = (DefaultTableModel)table.getModel();
					final String item_id = newTableModel.getValueAt(row, 1).toString();
					if (item_id == null || item_id.equals("")) {
						JOptionPane.showMessageDialog(contentPane, "选中行还未创建配置单", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					try {
						TCComponentQuery itemQuery = TCQueryUtil.GetTCComponentQuery(ConstDefine.TC_SESSION, "零组件 ID");
						TCComponent[] items0 = TCQueryUtil.GetItemByID(itemQuery, item_id);
						if (items0 == null || items0.length == 0) {
							JOptionPane.showMessageDialog(contentPane, "没有找到配置单" + item_id, "提示", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						TCComponentItem newItem = (TCComponentItem)items0[0];
						TCComponentItemRevision revision = newItem.getLatestItemRevision();
						
						// 发起流程
						InterfaceAIFComponent[] component = new InterfaceAIFComponent[] { revision };
						NewProcessCommand command = new NewProcessCommand(AIFUtility.getCurrentApplication().getDesktop(), 
								AIFUtility.getCurrentApplication(), component);
						new NewProcessDialog(command);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, "操作失败：" + e1.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
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
			
			JButton btnNewButton_2 = new JButton("刷新配置单");
			btnNewButton_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						m_beanList = GetBeanList();
						if (!m_errorMessage.equals("")) {
							JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
		                    return;
						}
						UpdateTableData(table, m_beanList);
						btnNewButton.setEnabled(true);
						btnNewButton_1.setEnabled(false);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, "操作失败：" + e1.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
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
					+ "')"; // 从梯种型号表里查找梯种型号代码;
			try (
					PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
					) {
				pstmt.executeUpdate();
				InsertDataToDB1(bean.map, item_id, createdDate, createdUser);
			} catch (Exception e) {
				m_errorMessage = "操作数据库出错：" + e.getMessage();
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
					m_errorMessage = "操作数据库出错：" + e.getMessage();
					e.printStackTrace();
					return;
				}
			}
		}
		
		private void InitTableData(List<TableBean> beanList) {
			Vector<Object> columnNames = new Vector<Object>();
			columnNames.add("配置单号");
			columnNames.add("合同号");
			columnNames.add("设备号");
			columnNames.add("客户名称");
			
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
			
			// 隐藏第一列
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
					
					try {
						new AssignTask1().ShowUI(m_frame, m_beanList.get(row));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			popupMenu.add(delMenItem);
	    }
		
		// 重写系统方法dispose，为了设置frame为null
		public void dispose() {
			if (m_connection != null) {
	 			try {
					m_connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(contentPane, "关闭数据库连接出错：" + e.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
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
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return;
		}
	}
	
	private TCComponentItem CreateNewItem(String EQUIPMENT_NO) throws Exception {
		// 从EAP获取配置单ID，特殊处理，出错也是继续执行程序
		String EAPID = GetEAPID(EQUIPMENT_NO);
		if (!m_errorMessage.equals("") || EAPID == null || EAPID.equals("")) {
			if (m_errorMessage.length() > 50) {
				m_errorMessage = m_errorMessage.substring(0, 50);
			}
			String errorMessage = m_errorMessage.length() == 0 ? "从EAP中获取配置单号失败" : "从EAP中获取配置单号失败：" + m_errorMessage;
			int n = JOptionPane.showConfirmDialog(m_frame.contentPane, errorMessage + "\n是否以流水码创建配置单？", "提示", JOptionPane.YES_NO_OPTION);
			m_errorMessage = "";
			if (n == 0) {
				EAPID = "";
			} else {
				return null;
			}
		}
		// 组织配置单
		TCComponentQuery itemQuery = TCQueryUtil.GetTCComponentQuery(ConstDefine.TC_SESSION, "零组件 ID");
		TCComponent[] items0 = TCQueryUtil.GetItemByID(itemQuery, EAPID);
		if (items0 != null && items0.length != 0) {
			m_errorMessage = "配置单" + EAPID + "已经存在";
			return null;
		}
		TCComponentItem newItem = TCItemUtil.CreateItem(ConstDefine.TC_SESSION, "S2_CFG", EAPID, "配置单", "");
		if (newItem == null) {
			m_errorMessage = "创建配置单失败";
			return null;
		}
		
		// 电器配置单
		TCComponentItem electricalItem = null;
		String electricalItemID = newItem.getProperty("item_id") + ConstDefine.TC_GROUP_ELECTRICAL;
		items0 = TCQueryUtil.GetItemByID(itemQuery, electricalItemID);
		if (items0 != null && items0.length != 0) {
			electricalItem = (TCComponentItem)items0[0];
		} else {
			electricalItem = TCItemUtil.CreateItem(ConstDefine.TC_SESSION, "S2_CFG", electricalItemID, "电气配置单VI", "");
			if (electricalItem == null) {
				m_errorMessage = "创电气建配置单失败";
				return null;
			}
		}
		// 机械配置单
		TCComponentItem mechanicalItem = null;
		String mechanicalItemID = newItem.getProperty("item_id") + ConstDefine.TC_GROUP_MECHANICAL;
		items0 = TCQueryUtil.GetItemByID(itemQuery, mechanicalItemID);
		if (items0 != null && items0.length != 0) {
			mechanicalItem = (TCComponentItem)items0[0];
		} else {
			mechanicalItem = TCItemUtil.CreateItem(ConstDefine.TC_SESSION, "S2_CFG", mechanicalItemID, "机械配置单VI", "");
			if (mechanicalItem == null) {
				m_errorMessage = "创建机械配置单失败";
				return null;
			}
		}
		
		// 搭建配置单BOM
		TCComponentBOMLine configListTopBomLine = TCBomUtil.GetTopBomLine(ConstDefine.TC_SESSION, newItem.getLatestItemRevision(), "视图");
		if (configListTopBomLine == null) {
			// 没有视图就去创建
			configListTopBomLine = TCBomUtil.SetBOMViewForItemRev(ConstDefine.TC_SESSION, newItem.getLatestItemRevision());
		}else{
			if(configListTopBomLine.hasChildren()){
				AIFComponentContext[] children = configListTopBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					bomLine.cut();
				}
				// 必须先关闭视图后再重新搭建，否则BomLine会按照之前的排序(查找编号属性)
				TCBomUtil.CloseWindow(configListTopBomLine);
				configListTopBomLine = TCBomUtil.GetTopBomLine(ConstDefine.TC_SESSION, newItem.getLatestItemRevision(), "视图");
			}
		}
		configListTopBomLine.add(electricalItem, "");
		configListTopBomLine.add(mechanicalItem, "");
		TCBomUtil.CloseWindow(configListTopBomLine);
		
		// 将配置单信息显示在窗口
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
				m_errorMessage = "没有从EAP数据库中获取到配置单";
				return null;
			}
		} catch (Exception e) {
			m_errorMessage = "操作EAP数据库出错：" + e.getMessage();
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
