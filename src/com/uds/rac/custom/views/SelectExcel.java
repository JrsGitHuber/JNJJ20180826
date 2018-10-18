package com.uds.rac.custom.views;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.teamcenter.rac.util.MessageBox;
import com.uds.Jr.utils.TxtUtil;
import com.uds.sjec.bean.ExcelToDBBean;
import com.uds.sjec.common.CommonFunction;
import com.uds.sjec.common.ConstDefine;

public class SelectExcel {
	public static String m_errorMessage;
	private static Connection m_connection;
	private static SelectExcelUI m_frame;
	
//	public static void main(String[] args) {
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
			
			if(m_frame == null){
				m_frame = new SelectExcelUI(rectangle);
			}
			m_frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post("出错：" + e.getMessage() + "\n详细信息请联系管理员查看控制台", "提示", MessageBox.INFORMATION);
			return;
		}
	}
	
	class SelectExcelUI extends JFrame {
		
		private static final long serialVersionUID = 1L;
		private JPanel contentPane;
		private JTextField textField;
		private JButton btnNewButton;

		/**
		 * Create the frame.
		 */
		public SelectExcelUI(Rectangle rectangle) {
			
			setTitle("参数录入");
			setAlwaysOnTop(true);
			setType(Type.UTILITY);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
			// 根据传来的Rectangle设置窗口位置
	        int centerX = rectangle.x + rectangle.width / 2;
	        int centerY = rectangle.y + rectangle.height / 2;
	        setBounds(centerX - 225, centerY - 80, 450, 160);
//			setBounds(100, 100, 450, 160);
	        
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel label = new JLabel("请选择路径：");
			label.setBounds(10, 10, 179, 15);
			contentPane.add(label);
			
			textField = new JTextField();
			textField.setBounds(10, 36, 330, 23);
			textField.setEditable(false);
			contentPane.add(textField);
			
			btnNewButton = new JButton("选择");
			btnNewButton.setFocusable(true);
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 选择文件保存的目录
	                String filePath = "";
	                JFileChooser fileChooser  = new JFileChooser();
	                FileSystemView fsv = FileSystemView.getFileSystemView();
	                fileChooser .setCurrentDirectory(fsv.getHomeDirectory());
//	                fileChooser .setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xls;*.xlsx", "xls", "xlsx");
	                fileChooser.setFileFilter(filter);
	                int returnVal = fileChooser.showDialog(contentPane, "选择");
	                if (returnVal == JFileChooser.APPROVE_OPTION) {
	                    filePath= fileChooser.getSelectedFile().getAbsolutePath();
	                }
	                if (filePath.equals("")) {
	                	return;
	                } else {
	                	textField.setText(filePath);
	                }
	                if (!JudgeExcelFile(filePath)) {
	                	textField.setText("");
	                	JOptionPane.showMessageDialog(contentPane, "请选择正确的参数录入表格", "提示", JOptionPane.INFORMATION_MESSAGE);
	                    return;
	                }
				}
			});
			btnNewButton.setBounds(350, 36, 74, 23);
			contentPane.add(btnNewButton);
			this.getRootPane().setDefaultButton(btnNewButton);
			
			JButton btnNewButton_1 = new JButton("确定");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String textFieldStr = textField.getText();
					if (textFieldStr.equals("")) {
						JOptionPane.showMessageDialog(contentPane, "请先选择参数录入表", "提示", JOptionPane.INFORMATION_MESSAGE);
	                    return;
					}
					try {
						// 从参数录入表中获取数据
						List<ExcelToDBBean> list = new ArrayList<ExcelToDBBean>();
						List<String> valueList = GetExcelToDBBean(textFieldStr, list);
						if (!m_errorMessage.equals("")) {
							JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
		                    return;
						}
						
						// 对比数据库中的参数数据
						CompareParamData(list, valueList.get(3));
						if (!m_errorMessage.equals("")) {
							JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
		                    return;
						}
						
						// 对数据进行处理
						String resultMessage = HandleExcelData(valueList, list);
						if (!m_errorMessage.equals("")) {
							JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
		                    return;
						}
						if (resultMessage != null) {
							JOptionPane.showMessageDialog(contentPane, resultMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, "操作失败：" + e1.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
	                    e1.printStackTrace();
	                    return;
					}
				}
			});
			btnNewButton_1.setBounds(286, 79, 64, 23);
			contentPane.add(btnNewButton_1);
			
			JButton btnNewButton_2 = new JButton("取消");
			btnNewButton_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			btnNewButton_2.setBounds(360, 79, 64, 23);
			contentPane.add(btnNewButton_2);
			
			JButton btnNewButton_3 = new JButton("重新录入");
			btnNewButton_3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String textFieldStr = textField.getText();
					if (textFieldStr.equals("")) {
						JOptionPane.showMessageDialog(contentPane, "请先选择参数录入表", "提示", JOptionPane.INFORMATION_MESSAGE);
	                    return;
					}
					try {
						List<ExcelToDBBean> list = new ArrayList<ExcelToDBBean>();
						List<String> valueList = GetExcelToDBBean(textFieldStr, list);
						if (!m_errorMessage.equals("")) {
							JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
		                    return;
						}
						String resultMessage = HandleExcelData1(valueList, list);
						if (!m_errorMessage.equals("")) {
							JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
		                    return;
						}
						if (resultMessage != null) {
							JOptionPane.showMessageDialog(contentPane, resultMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, "操作失败：" + e1.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
	                    e1.printStackTrace();
	                    return;
					}
				}
			});
			btnNewButton_3.setBounds(10, 79, 64, 23);
			contentPane.add(btnNewButton_3);
			btnNewButton_3.setMargin(new Insets(0, 0, 0, 0));
			
			JButton btnNewButton_4 = new JButton("变更录入");
			btnNewButton_4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String textFieldStr = textField.getText();
					if (textFieldStr.equals("")) {
						JOptionPane.showMessageDialog(contentPane, "请先选择参数录入表", "提示", JOptionPane.INFORMATION_MESSAGE);
	                    return;
					}
					try {
						List<ExcelToDBBean> list = new ArrayList<ExcelToDBBean>();
						List<String> valueList = GetExcelToDBBean(textFieldStr, list);
						if (!m_errorMessage.equals("")) {
							JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
		                    return;
						}
						String resultMessage = HandleExcelData2(valueList, list);
						if (!m_errorMessage.equals("")) {
							JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
		                    return;
						}
						if (resultMessage != null) {
							JOptionPane.showMessageDialog(contentPane, resultMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, "操作失败：" + e1.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
	                    e1.printStackTrace();
	                    return;
					}
				}
			});
			btnNewButton_4.setBounds(84, 79, 64, 23);
			contentPane.add(btnNewButton_4);
			btnNewButton_4.setMargin(new Insets(0, 0, 0, 0));
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
	
	private void CompareParamData(List<ExcelToDBBean> list, String PRODUCT_MODEL) throws Exception {
		m_errorMessage = "";
		
		Map<String, String> map = GetParamMap(PRODUCT_MODEL);
		if (!m_errorMessage.equals("")) {
    		return;
		}
		
		String dateStr = "";
		for (ExcelToDBBean bean : list) {
			if (!map.containsKey(bean.paramCode)) {
				dateStr = dateStr + "数据库中不存在参数代码为" + bean.paramCode + "的数据\n";
				continue;
			}
		}
		m_errorMessage = dateStr;
	}
	
	private Map<String, String> GetParamMap(String PRODUCT_MODEL) throws Exception {
		m_errorMessage = "";
		
		String ELEVATOR_MODEL_ID = GetIfModelTypeInDB(PRODUCT_MODEL);
		if (!m_errorMessage.equals("")) {
    		return null;
		}
    	if (ELEVATOR_MODEL_ID.equals("")) {
    		m_errorMessage = "数据库中不存在产品型号为" + PRODUCT_MODEL + "的数据";
        	return null;
    	}
    	List<String> EPCIDList = GetEPCID(ELEVATOR_MODEL_ID);
		if (!m_errorMessage.equals("")) {
    		return null;
		}
    	if (EPCIDList.size() == 0) {
    		m_errorMessage = "数据库中不存在elevator_model_id为" + ELEVATOR_MODEL_ID + "的数据";
        	return null;
    	}
		
		Map<String, String> map = new HashMap<String, String>();
		for (String EPCID : EPCIDList) {
			String sqlStr = "select PARAM_CODE from t_elevator_param where epc_id ='" + EPCID + "'";
			try (
					PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
					ResultSet resultSet = pstmt.executeQuery();
					) {
				while (resultSet.next()) {
					String PARAM_CODE = resultSet.getString("PARAM_CODE");
					if (!map.containsKey(PARAM_CODE)) {
						map.put(PARAM_CODE, "");
					}
				}
			} catch (Exception e) {
				m_errorMessage = "操作数据库出错：" + e.getMessage();
				e.printStackTrace();
				return null;
			}
		}
		if (map.size() == 0) {
			m_errorMessage = "没有查询到参数数据";
			return null;
		}
		return map;
	}
	
	private List<String> GetEPCID(String ELEVATOR_MODEL_ID) throws Exception {
		m_errorMessage = "";
		
		List<String> EPCIDList = new ArrayList<String>();
		
		String sqlStr = "select epc_id from t_epc where elevator_model_id ='" + ELEVATOR_MODEL_ID + "'";
		try (
				PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
				ResultSet resultSet = pstmt.executeQuery();
				) {
			while (resultSet.next()) {
				EPCIDList.add(resultSet.getString("epc_id"));
			}
		} catch (Exception e) {
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return null;
		}
		
		return EPCIDList;
	}
	
	/**
	 * 【变更录入】按钮
	 */
	private String HandleExcelData2(List<String> valueList, List<ExcelToDBBean> list) throws Exception {
		m_errorMessage = "";
		
		ABTableBean currentBean = GetCurrentABTableBean(valueList);
		if (!m_errorMessage.equals("")) {
			return null;
		}
		
		// 根据excel中的【设备号+变更标识N】在表中查询
		ABTableBean DBBean = GetABTableBeanMessage(valueList.get(0));
		if (!m_errorMessage.equals("")) {
    		return null;
		}
		// 若查询不到，则弹出提示“尚未录入”
		if (DBBean == null) {
			return "尚未录入";
		}
		// 若查询到，则继续检查AB录入标识
		if (DBBean.AB_SIGN.equals("N")) {
			// AB录入标识若为N，则弹出提示“尚未完成AB录入”
			return "尚未完成AB录入";
		} else {
			// AB录入标识若为Y，则继续检查EAP标识
			if (DBBean.ERP_SIGN.equals("N")) {
				// EAP标识若为N，则弹出提示“是否确认变更”，确认后，将当前变更标识改为Y，然后执行第一次录入
				int n = JOptionPane.showConfirmDialog(m_frame.contentPane, "是否确认变更", "提示", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					ChangeCHANGESign(valueList.get(0));
					if (!m_errorMessage.equals("")) {
						return null;
					}
					InsertDataToDB(list, currentBean);
					if (!m_errorMessage.equals("")) {
						return null;
					}
					return "数据录入成功";
				} else {
					return null;
				}
			} else {
				// EAP标识若为Y，则弹出提示“配置已开始，请联系管理员”
				return "配置已开始，请联系管理员";
			}
		}
	}
	
	/**
	 * 【重新录入】按钮
	 */
	private String HandleExcelData1(List<String> valueList, List<ExcelToDBBean> list) throws Exception {
		m_errorMessage = "";
		
//		return GetABMessage(list, currentBean);
		ABTableBean currentBean = GetCurrentABTableBean(valueList);
		if (!m_errorMessage.equals("")) {
    		return null;
		}
		
		// 根据excel中的【设备号+变更标识N】在表中查询
		ABTableBean DBBean = GetABTableBeanMessage(valueList.get(0));
		if (!m_errorMessage.equals("")) {
    		return null;
		}
		// 若查询不到，则弹出提示“尚未录入”
		if (DBBean == null) {
			return "尚未录入";
		}
		// 若查询到，则继续检查AB录入标识
		if (DBBean.AB_SIGN.equals("N")) {
			// AB录入标识若为N，则继续匹配当前机器名称和表中记录的机器名称是否一致
			if (DBBean.PC_MAC.equals(currentBean.PC_MAC)) {
				// 若机器名一致，则删除当前表中当前设备号相关的数据，然后执行第一次录入
				DeleteDBData(valueList.get(0));
				if (!m_errorMessage.equals("")) {
					return null;
				}
				InsertDataToDB(list, currentBean);
				if (!m_errorMessage.equals("")) {
					return null;
				}
				return "数据录入成功";
			} else {
				// 若机器名不一致，则弹出提示“无法重新录入他人数据”
				return "无法重新录入他人数据";
			}
		} else {
			// AB录入标识若为Y，则弹出提示“AB录入已完成，无法重新录入”
			return "AB录入已完成，无法重新录入";
		}
	}
	
	private void DeleteDBData(String EQUIPMENT_NO) {
		m_errorMessage = "";
		
		String sqlStr = "delete from T_AB_ENTRY where EQUIPMENT_NO = '" + EQUIPMENT_NO + "'";
		try (
				PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
				) {
			pstmt.executeUpdate();
			DeleteDBData1(EQUIPMENT_NO);
		} catch (Exception e) {
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return;
		}
	}
	
	private void DeleteDBData1(String EQUIPMENT_NO) {
		m_errorMessage = "";
		
		String sqlStr = "delete from T_AB_PARAMETER where EQUIPMENT_NO = '" + EQUIPMENT_NO + "'";
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

	/**
	 * 【确认】按钮
	 */
	private String HandleExcelData(List<String> valueList, List<ExcelToDBBean> list) throws Exception {
		m_errorMessage = "";
		
//		return GetABMessage(list, currentBean);
		ABTableBean currentBean = GetCurrentABTableBean(valueList);
		if (!m_errorMessage.equals("")) {
    		return null;
		}
		
		// 根据excel中的【设备号+变更标识N】在表中查询
		ABTableBean DBBean = GetABTableBeanMessage(valueList.get(0));
		if (!m_errorMessage.equals("")) {
    		return null;
		}
		// 若查询不到，则属于第一次录入，将excel内容写入，此时录入标识均为N
		if (DBBean == null) {
			InsertDataToDB(list, currentBean);
			if (!m_errorMessage.equals("")) {
				return null;
			}
			return "数据录入成功";
		}
		// 若查询到，则继续检查AB录入标识
		if (DBBean.AB_SIGN.equals("N")) {
			// AB录入标识若为N，则将excel中的内容，与表中的内容进行对比
			Map<String, ExcelToDBBean> map = GetABMessage2(valueList.get(0));
			if (!m_errorMessage.equals("")) {
	    		return null;
			}
			String dataStr = "";
			if((dataStr = CompareData(list, map)).equals("")) {
				// 若一致，则继续匹配当前机器名称和表中记录的机器名称是否一致
				if (DBBean.PC_MAC.equals(currentBean.PC_MAC)) {
					// 若机器名称一致，则弹出提示“请分别进行参数录入”，表中内容不发生变化
					return "请分别进行参数录入";
				} else {
					// 若机器名称不一致，则弹出提示“AB录入完成”，并将AB录入标识改为Y，表示AB录入完成
					ChangeABSign(valueList.get(0));
					if (!m_errorMessage.equals("")) {
						return null;
					}
					return "AB录入完成";
				}
			} else {
				// 若不一致，则弹出提示“AB录入不一致”，并将不一致的内容输出log到本地，log名称为“设备号+年月日分秒”
				String txtName = currentBean.EQUIPMENT_NO + "_" + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date()) + ".txt";
				TxtUtil.GetNewFile(txtName, dataStr);
				return "AB录入不一致，详见日志";
			}
		} else {
			// 若为Y，则忽略excel中的内容，弹出提示“AB录入已完成，勿重复录入”
			return "AB录入已完成，勿重复录入";
		}
	}
	
	private List<String> GetExcelToDBBean(String excelpath, List<ExcelToDBBean> list) throws Exception {
		m_errorMessage = "";
		
		CommonFunction.GetExcelLicense();
		Workbook wb = new Workbook(excelpath);
		com.aspose.cells.WorksheetCollection worksheets =  wb.getWorksheets();
		com.aspose.cells.Worksheet worksheet = worksheets.get("UDS");
        if (worksheet == null) {
        	m_errorMessage = "在参数录入表中没有找到 UDS 页";
        	return null;
        }
        
        Cells cells = worksheet.getCells();
    	Map<String, String> map = new HashMap<String, String>();
    	String paramType = "";
        for (int rowNum = 1; rowNum < 65536; rowNum++) {
        	Cell cell = cells.get(rowNum, 1);
        	String cellValue = cell == null ? "" : cell.getStringValue().trim();
        	if (cellValue.equals("")) {
        		break;
        	}
        	
        	ExcelToDBBean bean = new ExcelToDBBean();
        	bean.paramName = cellValue;
        	
        	cell = cells.get(rowNum, 2);
        	cellValue = cell == null ? "" : cell.getStringValue().trim();
        	if (cellValue.equals("")) {
        		m_errorMessage = "参数录入表中第" + (rowNum+1) + "行没有数据";
        		return null;
        	}
        	bean.paramCode = cellValue;
        	if (map.containsKey(bean.paramCode)) {
        		m_errorMessage = "参数录入表中含有重复参数代码：" + bean.paramCode + "（行" + ++rowNum + "列" + 3 + "）";
        		return null;
        	}
        	
        	cell = cells.get(rowNum, 3);
        	cellValue = cell == null ? "" : cell.getStringValue().trim();
        	if (cellValue.equals("")) {
        		m_errorMessage = "参数录入表中第" + (rowNum+1) + "行没有数据";
        		return null;
        	}
        	bean.paramValue = cellValue;
        	map.put(bean.paramCode, bean.paramValue);
        	
        	cell = cells.get(rowNum, 4);
        	cellValue = cell == null ? "" : cell.getStringValue().trim();
        	if (!cellValue.equals("")) {
        		paramType = cellValue;
        	}
        	if (paramType.equals("")) {
        		m_errorMessage = "参数录入表中第" + (rowNum+1) + "行没有数据";
            	return null;
        	}
        	bean.paramType = paramType;
        	list.add(bean);
        }
    	
        List<String> valueList = new ArrayList<String>();
        String key = "CT_Devi_Num_All"; // 0 设备号
        if (map.containsKey(key)) {
        	valueList.add(map.get(key));
        } else {
        	m_errorMessage = "在参数录入表中没有找到设备号";
        	return null;
        }
        key = "CT_Cont_Num"; // 1 合同号
        if (map.containsKey(key)) {
        	valueList.add(map.get(key));
        } else {
        	m_errorMessage = "在参数录入表中没有找到合同号";
        	return null;
        }
        key = "CT_User_Name"; // 2 客户名称
        if (map.containsKey(key)) {
        	valueList.add(map.get(key));
        } else {
        	valueList.add("");
        }
        key = "CT_Prod_Model"; // 3 产品型号
        if (map.containsKey(key)) {
        	String value = map.get(key);
        	// 判断数据库中是否存在该产品型号
        	String ELEVATOR_MODEL_ID = GetIfModelTypeInDB(value);
        	if (!m_errorMessage.equals("")) {
        		return null;
    		}
        	if (ELEVATOR_MODEL_ID.equals("")) {
        		m_errorMessage = "数据库中不存在产品型号为" + value + "的数据";
            	return null;
        	}
        	valueList.add(map.get(key));
        } else {
        	m_errorMessage = "在参数录入表中没有找到产品型号";
        	return null;
        }
        key = "CT_Proj_Name"; // 4 项目名称
        if (map.containsKey(key)) {
        	valueList.add(map.get(key));
        } else {
        	valueList.add("");
        }
    	
		return valueList;
	}
	
	private ABTableBean GetCurrentABTableBean(List<String> valueList) throws Exception {
		ABTableBean bean = new ABTableBean();
		Date date = new Date();
		bean.EQUIPMENT_NO = valueList.get(0);
		bean.CONTRACT_NO = valueList.get(1);
		bean.CUSTOMER_NAME = valueList.get(2);
		bean.PRODUCT_MODEL = valueList.get(3);
		bean.PRODUCT_NAME = valueList.get(4);
		bean.USER = ConstDefine.TC_SESSION.toString();
		bean.DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm ssSSS").format(date);
		bean.PC_MAC = CommonFunction.GetLocalMac();
//		bean.PC_MAC = CommonFunction.GetLocalMac() + "A";
		if (!CommonFunction.m_errorMessage.equals("")) {
			m_errorMessage = CommonFunction.m_errorMessage;
			return null;
		}
		bean.AB_SIGN = "N";
		bean.CHANGE_SIGN = "N";
		bean.ERP_SIGN = "N";
		return bean;
	}

	private ABTableBean GetABTableBeanMessage(String EQUIPMENT_NO) {
		m_errorMessage = "";
		
		String sqlStr = "select * from T_AB_ENTRY where EQUIPMENT_NO = '" + EQUIPMENT_NO + "' and CHANGE_SIGN = 'N'";
		try (
				PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
				ResultSet resultSet = pstmt.executeQuery();
				) {
			ABTableBean bean = new ABTableBean();
			if (resultSet.next()) {
				bean.EQUIPMENT_NO = resultSet.getString("EQUIPMENT_NO");
				bean.CONFIG_ID = resultSet.getString("CONFIG_ID");
				bean.CONTRACT_NO = resultSet.getString("CONTRACT_NO");
				bean.CUSTOMER_NAME = resultSet.getString("CUSTOMER_NAME");
				bean.PRODUCT_MODEL = resultSet.getString("PRODUCT_MODEL");
				bean.PRODUCT_NAME = resultSet.getString("PRODUCT_NAME");
				bean.USER = resultSet.getString("THEUSER");
				bean.DATE = resultSet.getString("THEDATE");
				bean.PC_MAC = resultSet.getString("PC_MAC");
				bean.AB_SIGN = resultSet.getString("AB_SIGN");
				bean.CHANGE_SIGN = resultSet.getString("CHANGE_SIGN");
				bean.ERP_SIGN = resultSet.getString("ERP_SIGN");
				return bean;
			} else {
				return null;
			}
		} catch (Exception e) {
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	private String GetABMessage(List<ExcelToDBBean> list, ABTableBean currentBean) throws Exception {
		m_errorMessage = "";
		
		// 根据excel中的【设备号+变更标识N】在表中查询
		String sqlStr = "select * from T_AB_ENTRY where EQUIPMENT_NO = " + currentBean.EQUIPMENT_NO + " and CHANGE_SIGN = 'N'";
		try (
				PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
				ResultSet resultSet = pstmt.executeQuery();
				ResultSet resultSet1 = null;
				) {
			ABTableBean bean = new ABTableBean();
			if (resultSet.next()) {
				// 若查询到，则继续检查AB录入标识
				bean.EQUIPMENT_NO = resultSet.getString("EQUIPMENT_NO");
				bean.CONFIG_ID = resultSet.getString("CONFIG_ID");
				bean.USER = resultSet.getString("THEUSER");
				bean.DATE = resultSet.getString("THEDATE");
				bean.PC_MAC = resultSet.getString("PC_MAC");
				bean.AB_SIGN = resultSet.getString("AB_SIGN");
				bean.CHANGE_SIGN = resultSet.getString("CHANGE_SIGN");
				bean.ERP_SIGN = resultSet.getString("ERP_SIGN");
			} else {
				// 若查询不到，则属于第一次录入，将excel内容写入，此时录入标识均为N
				InsertDataToDB(list, currentBean);
				if (!m_errorMessage.equals("")) {
					return "";
				}
				return "数据录入成功";
			}
			
			if (bean.AB_SIGN.equals("N")) {
				// AB录入标识若为N，则将excel中的内容，与表中的内容进行对比
				Map<String, ExcelToDBBean> map = GetABMessage2(currentBean.EQUIPMENT_NO);
				String dataStr = "";
				if((dataStr = CompareData(list, map)).equals("")) {
					// 若一致，则继续匹配当前机器名称和表中记录的机器名称是否一致
					if (bean.PC_MAC.equals(currentBean.PC_MAC)) {
						// 若机器名称一致，则弹出提示“请分别进行参数录入”，表中内容不发生变化
						return "请分别进行参数录入";
					} else {
						// 若机器名称不一致，则弹出提示“AB录入完成”，并将AB录入标识改为Y，表示AB录入完成
						ChangeABSign(currentBean.EQUIPMENT_NO);
						if (!m_errorMessage.equals("")) {
							return "";
						}
						return "AB录入完成";
					}
				} else {
					// 若不一致，则弹出提示“AB录入不一致”，并将不一致的内容输出log到本地，log名称为“设备号+年月日分秒”
					String txtName = currentBean.EQUIPMENT_NO + "_" + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date()) + ".txt";
					TxtUtil.GetNewFile(txtName, dataStr);
					return "AB录入不一致，详见日志";
				}
			} else {
				// 若为Y，则忽略excel中的内容，弹出提示“AB录入已完成，勿重复录入”
				return "AB录入已完成，勿重复录入";
			}
		} catch (Exception e) {
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return "";
		}
	}
	
	private void ChangeCHANGESign(String EQUIPMENT_NO) {
		m_errorMessage = "";
		
		String sqlStr = "update T_AB_ENTRY set CHANGE_SIGN = ? where EQUIPMENT_NO = ?";
		try (
				PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
				) {
			pstmt.setString(1, "Y");
            pstmt.setString(2, EQUIPMENT_NO);
			pstmt.executeUpdate();
			
			DeleteDBData1(EQUIPMENT_NO);
		} catch (Exception e) {
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return;
		}
	}
	
	private void ChangeABSign(String EQUIPMENT_NO) {
		m_errorMessage = "";
		
		String sqlStr = "update T_AB_ENTRY set AB_SIGN = ? where EQUIPMENT_NO = ?";
		try (
				PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
				) {
			pstmt.setString(1, "Y");
            pstmt.setString(2, EQUIPMENT_NO);
			pstmt.executeUpdate();
		} catch (Exception e) {
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return;
		}
	}

	private void InsertDataToDB(List<ExcelToDBBean> list, ABTableBean currentBean) {
		m_errorMessage = "";
		
		String sqlStr = "insert into T_AB_ENTRY (EQUIPMENT_NO, CONTRACT_NO, CUSTOMER_NAME, PRODUCT_MODEL, PRODUCT_NAME, THEUSER, THEDATE, PC_MAC, AB_SIGN, CHANGE_SIGN, ERP_SIGN) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (
				PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
				) {
			pstmt.setString(1, currentBean.EQUIPMENT_NO);
			pstmt.setString(2, currentBean.CONTRACT_NO);
			pstmt.setString(3, currentBean.CUSTOMER_NAME);
			pstmt.setString(4, currentBean.PRODUCT_MODEL);
			pstmt.setString(5, currentBean.PRODUCT_NAME);
            pstmt.setString(6, currentBean.USER);
            pstmt.setString(7, currentBean.DATE);
            pstmt.setString(8, currentBean.PC_MAC);
            pstmt.setString(9, currentBean.AB_SIGN);
            pstmt.setString(10, currentBean.CHANGE_SIGN);
            pstmt.setString(11, currentBean.ERP_SIGN);
			pstmt.executeUpdate();
			
			InsertDataToDB1(list, currentBean.EQUIPMENT_NO);
			if (!m_errorMessage.equals("")) {
				return;
			}
		} catch (Exception e) {
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return;
		}
	}
	
	private void InsertDataToDB1(List<ExcelToDBBean> list, String EQUIPMENT_NO) {
		m_errorMessage = "";
		
		for (ExcelToDBBean bean : list) {
			String sqlStr = "insert into T_AB_PARAMETER (EQUIPMENT_NO, PARAM_CODE, PARAM_NAME, PARAM_VALUE, PARAM_TYPE) values (?, ?, ?, ?, ?)";
			try (
					PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
					) {
				pstmt.setString(1, EQUIPMENT_NO);
	            pstmt.setString(2, bean.paramCode);
	            pstmt.setString(3, bean.paramName);
	            pstmt.setString(4, bean.paramValue);
	            pstmt.setString(5, bean.paramType);
				pstmt.executeUpdate();
			} catch (Exception e) {
				m_errorMessage = "操作数据库出错：" + e.getMessage();
				e.printStackTrace();
				return;
			}
		}
	}

	private String CompareData(List<ExcelToDBBean> list, Map<String, ExcelToDBBean> map) {
		String dateStr = "";
		for (ExcelToDBBean bean : list) {
			if (map.containsKey(bean.paramCode)) {
				ExcelToDBBean tempBean = map.get(bean.paramCode);
				if (bean.paramName.equals(tempBean.paramName) && bean.paramType.equals(tempBean.paramType)
						&& bean.paramValue.equals(tempBean.paramValue)) {
					continue;
				} else {
					dateStr = dateStr + "参数代码" + bean.paramCode + "的数据不匹配："
							+ bean.paramName + "|" + tempBean.paramName + " "
							+ bean.paramType + "|" + tempBean.paramType + " "
							+ bean.paramValue + "|" + tempBean.paramValue + "\r\n";
					continue;
				}
			} else {
				dateStr = dateStr + "数据库中不存在参数代码为" + bean.paramCode + "的数据\r\n";
				continue;
			}
		}
		
		return dateStr;
	}

	private Map<String, ExcelToDBBean> GetABMessage2(String EQUIPMENT_NO) {
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
	
	private String GetIfModelTypeInDB(String PRODUCT_MODEL) throws Exception {
		m_errorMessage = "";
		
		String sqlStr = "select ELEVATOR_MODEL_ID from t_elevator_model where ELEVATOR_MODEL_CODE = '" + PRODUCT_MODEL + "'";
		try (
				PreparedStatement pstmt = m_connection.prepareStatement(sqlStr);
				ResultSet resultSet = pstmt.executeQuery();
				) {
			if (resultSet.next()) {
				return resultSet.getString("ELEVATOR_MODEL_ID");
			} else {
				return "";
			}
		} catch (Exception e) {
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return "";
		}
	}
	
	private boolean JudgeExcelFile(String filePath) {
		File file = new File(filePath);
		String fileName = file.getName();
		if (fileName.contains("参数录入表")) {
			if (fileName.toLowerCase().endsWith(".xls") || fileName.toLowerCase().endsWith(".xlsx")) {
	        	return true;
	        }
    	}
		
		return false;
	}
}

class ABTableBean
{
	public String EQUIPMENT_NO = "";
	public String CONTRACT_NO = "";
	public String CUSTOMER_NAME = "";
	public String PRODUCT_MODEL = "";
	public String PRODUCT_NAME = "";
	public String CONFIG_ID = "";
	public String USER = "";
	public String DATE = "";
	public String PC_MAC = "";
	public String AB_SIGN = "";
	public String CHANGE_SIGN = "";
	public String ERP_SIGN = "";
}
