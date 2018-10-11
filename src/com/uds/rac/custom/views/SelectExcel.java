package com.uds.rac.custom.views;

import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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

public class SelectExcel extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JButton btnNewButton;
	
	public String m_errorMessage;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
	public static void ShowUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final SelectExcel frame = new SelectExcel();
					frame.setAlwaysOnTop(true);
					frame.setVisible(true);
					frame.addMouseListener(new MouseListener() {

			            @Override
			            public void mouseClicked(MouseEvent e) {
			            }

			            @Override
			            public void mousePressed(MouseEvent e) {
			            	
			            }

			            @Override
			            public void mouseReleased(MouseEvent e) {}

			            @Override
			            public void mouseEntered(MouseEvent e) {
			            	frame.setAlwaysOnTop(false);
			            }

			            @Override
			            public void mouseExited(MouseEvent e) {}
			            
			        });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SelectExcel() {
		m_errorMessage = "";
		
		setTitle("参数录入");
		setAlwaysOnTop(true);
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 160);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("请选择路径：");
		label.setBounds(10, 10, 179, 15);
		contentPane.add(label);
		
		textField = new JTextField();
		textField.setBounds(10, 36, 281, 23);
		textField.setEditable(false);
		contentPane.add(textField);
//		textField.addKeyListener(new KeyAdapter() {
//			public void keyPressed(KeyEvent e) {
//				if (e.getKeyChar() == KeyEvent.VK_ENTER) // 按回车键执行相应操作;
//				{
//				}
//			}
//		});
		
		btnNewButton = new JButton("选择");
		btnNewButton.setFocusable(true);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 选择文件保存的目录
                String filePath = "";
                JFileChooser fileChooser  = new JFileChooser();
                FileSystemView fsv = FileSystemView.getFileSystemView();
                fileChooser .setCurrentDirectory(fsv.getHomeDirectory());
//                fileChooser .setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xls;*.xlsx", "xls", "xlsx");
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showDialog(contentPane, "选择");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    filePath= fileChooser.getSelectedFile().getAbsolutePath();
                }
                if (!filePath.equals("")) {
                	textField.setText(filePath);
                }
                if (!JudgeExcelFile(filePath)) {
                	textField.setText("");
                	JOptionPane.showMessageDialog(contentPane, "请选择正确的参数录入表格", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
			}
		});
		btnNewButton.setBounds(300, 36, 74, 23);
		contentPane.add(btnNewButton);
		this.getRootPane().setDefaultButton(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("确定");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String textFieldStr = textField.getText();
				List<ExcelToDBBean> beanList = null;
				try {
					beanList = GetExcelToDBBean(textFieldStr);
					if (!m_errorMessage.equals("")) {
						JOptionPane.showMessageDialog(contentPane, m_errorMessage, "提示", JOptionPane.INFORMATION_MESSAGE);
	                    return;
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(contentPane, "获取参数录入表数据失败：" + e1.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
                    e1.printStackTrace();
                    return;
				}
				
				
			}
		});
		btnNewButton_1.setBounds(236, 79, 64, 23);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("取消");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnNewButton_2.setBounds(310, 79, 64, 23);
		contentPane.add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("重新录入");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_3.setBounds(10, 79, 64, 23);
		contentPane.add(btnNewButton_3);
		btnNewButton_3.setMargin(new Insets(0, 0, 0, 0));
		
		JButton btnNewButton_4 = new JButton("变更录入");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_4.setBounds(84, 79, 64, 23);
		contentPane.add(btnNewButton_4);
		btnNewButton_4.setMargin(new Insets(0, 0, 0, 0));
	}
	
	private List<ExcelToDBBean> GetExcelToDBBean(String excelpath) throws Exception {
		m_errorMessage = "";
		List<ExcelToDBBean> list = new ArrayList<ExcelToDBBean>();
		
		CommonFunction.GetExcelLicense();
		Workbook wb = new Workbook(excelpath);
		com.aspose.cells.WorksheetCollection worksheets =  wb.getWorksheets();
		com.aspose.cells.Worksheet worksheet = worksheets.get("UDS");
        if (worksheet == null) {
        	m_errorMessage = "在参数录入表中没有找到 UDS 页";
        	return null;
        }
        
        Cells cells = worksheet.getCells();
        Cell cell = cells.get(9, 3);
    	String cellValue = cell == null ? "" : cell.getStringValue().trim();
    	if (cellValue.equals("")) {
    		m_errorMessage = "在参数录入表中没有找到产品型号";
        	return null;
    	}
    	// 判断数据库中是否存在该产品型号
    	boolean ifModelTypeInDB = GetIfModelTypeInDB(cellValue);
    	if (!m_errorMessage.equals("")) {
    		return null;
		}
    	if (!ifModelTypeInDB) {
    		m_errorMessage = "数据库中不存在产品型号为" + cellValue + "的数据";
        	return null;
    	}
    	
    	Map<String, String> map = new HashMap<String, String>();
        for (int rowNum = 1; rowNum < 65536; rowNum++) {
        	cell = cells.get(rowNum, 1);
        	cellValue = cell == null ? "" : cell.getStringValue().trim();
        	if (cellValue.equals("")) {
        		break;
        	}
        	
        	ExcelToDBBean bean = new ExcelToDBBean();
        	bean.paramName = cellValue;
        	cell = cells.get(rowNum, 2);
        	cellValue = cell == null ? "" : cell.getStringValue().trim();
        	bean.paramCode = cellValue;
        	if (map.containsKey(bean.paramCode)) {
        		m_errorMessage = "参数录入表中含有重复参数代码：" + bean.paramCode + "（行" + ++rowNum + "列" + 3 + "）";
        		return null;
        	}
        	cell = cells.get(rowNum, 3);
        	cellValue = cell == null ? "" : cell.getStringValue().trim();
        	bean.paramValue = cellValue;
        	cell = cells.get(rowNum, 4);
        	cellValue = cell == null ? "" : cell.getStringValue().trim();
        	bean.paramType = cellValue;
        	list.add(bean);
        }
    	
    	// 查询AB录入表
    	// TODO...
    	cell = cells.get(2, 3);
    	cellValue = cell == null ? "" : cell.getStringValue().trim();
    	if (cellValue.equals("")) {
    		m_errorMessage = "在参数录入表中没有找到设备号";
        	return null;
    	}
    	ABTableBean currentBean = GetCurrentABTableBean();
    	GetABMessage(cellValue, list, currentBean);
    	if (!m_errorMessage.equals("")) {
    		return null;
		}
    	
        
		
		return list;
	}
	
	private ABTableBean GetCurrentABTableBean() throws Exception {
		ABTableBean bean = new ABTableBean();
		bean.USER = ConstDefine.TC_SESSION.toString();
		bean.DATE = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss SSS").format(new Date());
		bean.PC_MAC = CommonFunction.GetLocalMac();
		bean.AB_SIGN = "N";
		bean.CHANGE_SIGN = "N";
		bean.ERP_SIGN = "N";
		return null;
	}

	private String GetABMessage(String equipmentNo, List<ExcelToDBBean> list, ABTableBean currentBean) throws Exception {
		m_errorMessage = "";
		
		Class.forName(ConstDefine.TCDB_CLASSNAME);
		String sqlStr = "select * from T_CONFIG_PARAMETER_AB where EQUIPMENT_NO = " + equipmentNo;
		try (
				Connection connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
				PreparedStatement pstmt = connection.prepareStatement(sqlStr);
				ResultSet resultSet = pstmt.executeQuery();
				ResultSet resultSet1 = null;
				) {
			ABTableBean bean = new ABTableBean();
			if (resultSet.next()) {
				bean.EQUIPMENT_NO = resultSet.getString("EQUIPMENT_NO");
				bean.CONFIGURATIONLIST_ID = resultSet.getString("CONFIGURATIONLIST_ID");
				bean.USER = resultSet.getString("USER");
				bean.DATE = resultSet.getString("DATE");
				bean.PC_MAC = resultSet.getString("PC_MAC");
				bean.AB_SIGN = resultSet.getString("AB_SIGN");
				bean.CHANGE_SIGN = resultSet.getString("CHANGE_SIGN");
				bean.ERP_SIGN = resultSet.getString("ERP_SIGN");
			} else {
				return "设备号" + equipmentNo + "没有查询到数据";
			}
			
			if (bean.CHANGE_SIGN.equals("N")) {
				if (bean.AB_SIGN.equals("N")) {
					Map<String, ExcelToDBBean> map = GetABMessage2(connection, equipmentNo);
					// TODO 开始进行数据对比...
					String dataStr = "";
					if((dataStr = CompareData(list, map)).equals("")) {
						if (bean.PC_MAC.equals(currentBean.PC_MAC)) {
							return "请分别进行参数录入";
						} else {
							return "AB录入完成";
						}
					} else {
						String txtName = equipmentNo + "_" + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date()) + ".txt";
						TxtUtil.GetNewFile(txtName, dataStr);
						return "AB录入不一致，详见日志";
					}
				} else {
					return "AB录入已完成，勿重复录入";
				}
			} else {
				return "";
			}
			
			
//			if (resultSet.next()) {
//				ABTableBean bean = new ABTableBean();
//				bean.EQUIPMENT_NO = resultSet.getString("EQUIPMENT_NO");
//				bean.CONFIGURATIONLIST_ID = resultSet.getString("CONFIGURATIONLIST_ID");
//				bean.USER = resultSet.getString("USER");
//				bean.DATE = resultSet.getString("DATE");
//				bean.PC_MAC = resultSet.getString("PC_MAC");
//				bean.AB_SIGN = resultSet.getString("AB_SIGN");
//				bean.CHANGE_SIGN = resultSet.getString("CHANGE_SIGN");
//				bean.ERP_SIGN = resultSet.getString("ERP_SIGN");
//				
//				
//				
//				String CONFIGURATIONLIST_ID = resultSet.getString("CONFIGURATIONLIST_ID");
//				ABTableBean bean = GetABMessage1(connection, CONFIGURATIONLIST_ID);
//				if (!m_errorMessage.equals("")) {
//		    		return null;
//				}
//				if (bean.CHANGE_SIGN.equals("N")) {
//					if (bean.AB_SIGN.equals("N")) {
//						Map<String, ExcelToDBBean> map = GetABMessage2(connection, CONFIGURATIONLIST_ID);
//						// TODO 开始进行数据对比...
//						String dataStr = "";
//						if((dataStr = CompareData(list, map)).equals("")) {
//							if (bean.PC_MAC.equals(currentBean.PC_MAC)) {
//								return "请分别进行参数录入";
//							} else {
//								return "AB录入完成";
//							}
//						} else {
//							String txtName = equipmentNo + "_" + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date()) + ".txt";
//							TxtUtil.GetNewFile(txtName, dataStr);
//							return "AB录入不一致，详见日志";
//						}
//					} else {
//						return "AB录入已完成，勿重复录入";
//					}
//				} else {
//					return "";
//				}
//			}
		} catch (Exception e) {
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return "";
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

	private Map<String, ExcelToDBBean> GetABMessage2(Connection connection, String equipmentNo) {
		m_errorMessage = "";
		Map<String, ExcelToDBBean> map = new HashMap<String, ExcelToDBBean>();
		
		String sqlStr = "select * from T_CONFIGURATIONLIST_PARAMETER where EQUIPMENT_NO = " + equipmentNo;
		try (
				PreparedStatement pstmt = connection.prepareStatement(sqlStr);
				ResultSet resultSet = pstmt.executeQuery();
				) {
			while (resultSet.next()) {
				ExcelToDBBean bean = new ExcelToDBBean();
				bean.paramCode = resultSet.getString("PARAM_CODE");
				bean.paramName = resultSet.getString("PARAM_NAME");
				bean.paramValue = resultSet.getString("PARAM_VALUE");
				bean.paramType = resultSet.getString("PARAM_CLASSIFICATION");
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
	
	private ABTableBean GetABMessage1(Connection connection, String CONFIGURATIONLIST_ID) {
		m_errorMessage = "";
		
		String sqlStr = "select * from T_CONFIG_PARAMETER_AB where CONFIGURATIONLIST_ID = " + CONFIGURATIONLIST_ID;
		try (
				PreparedStatement pstmt = connection.prepareStatement(sqlStr);
				ResultSet resultSet = pstmt.executeQuery();
				) {
			if (resultSet.next()) {
				ABTableBean bean = new ABTableBean();
				bean.CONFIGURATIONLIST_ID = CONFIGURATIONLIST_ID;
				bean.USER = resultSet.getString("USER");
				bean.DATE = resultSet.getString("DATE");
				bean.PC_MAC = resultSet.getString("PC_MAC");
				bean.AB_SIGN = resultSet.getString("AB_SIGN");
				bean.CHANGE_SIGN = resultSet.getString("CHANGE_SIGN");
				bean.ERP_SIGN = resultSet.getString("ERP_SIGN");
				return bean;
			} else {
				m_errorMessage = "没有查询到数据：" + sqlStr;
				return null;
			}
		} catch (Exception e) {
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	private boolean GetIfModelTypeInDB(String modelType) throws Exception {
		m_errorMessage = "";
		
		Class.forName(ConstDefine.TCDB_CLASSNAME);
		String sqlStr = "select * from t_elevator_model where ELEVATOR_MODEL_CODE = " + modelType;
		try (
				Connection connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
				PreparedStatement pstmt = connection.prepareStatement(sqlStr);
				ResultSet resultSet = pstmt.executeQuery();
				) {
			if (resultSet.next()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			m_errorMessage = "操作数据库出错：" + e.getMessage();
			e.printStackTrace();
			return false;
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
	public String CONFIGURATIONLIST_ID = "";
	public String USER = "";
	public String DATE = "";
	public String PC_MAC = "";
	public String AB_SIGN = "";
	public String CHANGE_SIGN = "";
	public String ERP_SIGN = "";
}
