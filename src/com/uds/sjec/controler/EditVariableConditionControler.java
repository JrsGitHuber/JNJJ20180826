package com.uds.sjec.controler;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.Jr.utils.StringUtil;
import com.uds.common.exceptions.CalculateException;
import com.uds.common.utils.MathUtil;
import com.uds.sjec.bean.VariabelConditionTableBean;
import com.uds.sjec.common.ConstDefine;
import com.uds.sjec.service.IEditVariableConditionService;
import com.uds.sjec.service.impl.EditVariableConditionServiceImpl;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.swing.table.DefaultTableCellHeaderRenderer;

public class EditVariableConditionControler {
	public IEditVariableConditionService editVariableConditionService = new EditVariableConditionServiceImpl();

	public void userTask(final TCComponentBOMLine topBomLine, final TCComponentBOMLine bomLine) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EditVariableConditionFrame frame = new EditVariableConditionFrame(topBomLine, bomLine);
					frame.setResizable(false);
					frame.setAlwaysOnTop(true);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the application.
	 */
	class EditVariableConditionFrame extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JPanel panel;
		private JTextField textField_elevatorType;
		private JTextField textField_rev;
		private JTextField textField_BOMLine;
//		private JTextArea textArea_varibleCondition;
		private JTextPane textArea_varibleCondition;
		private String elevatorType = null; // 梯型
		private String elevatorRev = null;// 版本
		private String defaultVariableCondition = null;
		private JTable table_paramOption;
		private DefaultTableModel paramTableModel;
		private JTextField textField_paramCode;
		private JTextField textField_paramName;
		private Connection connection;
		private ResultSet resultSet;
		private Statement statement;
		private ResultSetMetaData resultSetMetaData;
		private String columName;
		private String elevatorModelID;// 梯型ID
		private String EPCID;// 梯型参数分类ID
		private String EPCName;// 梯型参数分类名称
		private List<VariabelConditionTableBean> paramInfoList;
		private JTree tree;
		private DefaultTableModel tableModel;
		private int currentPosition;// 光标指定位置
		private String tailString = "";// 光标之后的字符串
		private String headString = "";// 光标之前的字符串
		private int setPosition;// 输入后光标位置
		private String tipText;
		private Map<String, String> paramCodeAndTypeMap;// 存储参数代号和参数类型
		private JButton button_refresh;
		private SimpleAttributeSet attrSet;
		private SimpleAttributeSet attrSet1;

		public EditVariableConditionFrame(final TCComponentBOMLine topBomLine, final TCComponentBOMLine bomLine) {
			InitSimpleAttributeSet();
			
			setBounds(100, 100, 571, 726);
			setTitle("编辑变量条件");
			setResizable(false);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			setLocationRelativeTo(null);

			panel = new JPanel();
			panel.setBounds(149, 10, 406, 677);
			getContentPane().add(panel);
			panel.setBorder(BorderFactory.createTitledBorder(""));
			panel.setLayout(null);

			JLabel label_elevatorType = new JLabel("梯型");
			label_elevatorType.setBounds(10, 16, 38, 15);
			panel.add(label_elevatorType);

			textField_elevatorType = new JTextField();
			textField_elevatorType.setBounds(65, 13, 123, 21);
			panel.add(textField_elevatorType);
			textField_elevatorType.setColumns(10);
			textField_elevatorType.setEditable(false);

			JLabel label_revision = new JLabel("版本");
			label_revision.setBounds(213, 16, 29, 15);
			panel.add(label_revision);

			textField_rev = new JTextField();
			textField_rev.setEditable(false);
			textField_rev.setColumns(10);
			textField_rev.setBounds(252, 13, 145, 21);
			panel.add(textField_rev);

			JLabel label_varibleCondition = new JLabel("变量条件");
			label_varibleCondition.setBounds(10, 82, 54, 15);
			panel.add(label_varibleCondition);

//			textArea_varibleCondition = new JTextArea();
			textArea_varibleCondition = new MyJTextPane();
//			panel.add(textArea_varibleCondition);
			textArea_varibleCondition.setBounds(10, 100, 385, 112);
//			textArea_varibleCondition.setLineWrap(true);
//			textArea_varibleCondition.setWrapStyleWord(true);

			JScrollPane scrollPane_textArea = new JScrollPane();
			scrollPane_textArea.setBounds(10, 100, 385, 112);
			panel.add(scrollPane_textArea);
			scrollPane_textArea.setViewportView(textArea_varibleCondition);

			JLabel lblBom = new JLabel("BOM行");
			lblBom.setBounds(10, 50, 45, 15);
			panel.add(lblBom);

			textField_BOMLine = new JTextField();
			textField_BOMLine.setEditable(false);
			textField_BOMLine.setColumns(10);
			textField_BOMLine.setBounds(65, 47, 241, 21);
			String bomlineName;
			try {
				bomlineName = bomLine.getProperty("bl_indented_title");
				textField_BOMLine.setText(bomlineName);
			} catch (TCException e3) {
				e3.printStackTrace();
			}

			panel.add(textField_BOMLine);

			button_refresh = new JButton("刷新");
			button_refresh.setBounds(334, 46, 63, 23);
			panel.add(button_refresh);

			JButton button_greater = new JButton(">");
			button_greater.setBounds(10, 222, 45, 23);
			panel.add(button_greater);

			JButton button_less = new JButton("<");
			button_less.setBounds(65, 222, 45, 23);
			panel.add(button_less);

			JButton button_lessOrEqualTo = new JButton("<=");
			button_lessOrEqualTo.setBounds(188, 222, 54, 23);
			panel.add(button_lessOrEqualTo);

			JButton button_leftParenthesis = new JButton("(");
			button_leftParenthesis.setBounds(10, 255, 45, 23);
			panel.add(button_leftParenthesis);

			JButton button_rightParenthesis = new JButton(")");
			button_rightParenthesis.setBounds(65, 255, 45, 23);
			panel.add(button_rightParenthesis);

			JButton button_or = new JButton("||");
			button_or.setBounds(188, 255, 55, 23);
			panel.add(button_or);

			JButton button_equalTo = new JButton("==");
			button_equalTo.setBounds(252, 222, 54, 23);
			panel.add(button_equalTo);

			JButton button_nor = new JButton("!=");
			button_nor.setBounds(253, 255, 53, 23);
			panel.add(button_nor);

			JButton button_clear = new JButton("清除");
			button_clear.setBounds(334, 255, 63, 23);
//			button_clear.setBounds(334, 222, 63, 55);
			panel.add(button_clear);

			JButton button_confirm = new JButton("确认");
			button_confirm.setBounds(261, 644, 63, 23);
			panel.add(button_confirm);

			JButton button_cancel = new JButton("取消");
			button_cancel.setBounds(334, 644, 63, 23);
			panel.add(button_cancel);

			JButton button_greaterOrEqualTo = new JButton(">=");
			button_greaterOrEqualTo.setBounds(120, 222, 54, 23);
			panel.add(button_greaterOrEqualTo);

			JButton button_with = new JButton("&&");
			button_with.setBounds(120, 255, 54, 23);
			panel.add(button_with);

			JButton button_getValue = new JButton("标记");
			button_getValue.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ChangeTextColor();
				}
			});
			button_getValue.setBounds(334, 222, 63, 23);
			panel.add(button_getValue);
//			button_getValue.setVisible(false);

			JPanel panel_param = new JPanel();
			panel_param.setBounds(10, 288, 386, 351);
			panel.add(panel_param);
			panel_param.setBorder(BorderFactory.createTitledBorder(""));
			panel_param.setLayout(null);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setBounds(0, 41, 386, 310);
			panel_param.add(scrollPane);

			// 定义table并不可编辑
			tableModel = new DefaultTableModel();
			table_paramOption = new JTable(tableModel) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public boolean isCellEditable(int row, int column) {
					return false;
				}

				public String getToolTipText(MouseEvent e) {
					int row = table_paramOption.rowAtPoint(e.getPoint());
					int col = table_paramOption.columnAtPoint(e.getPoint());

					if (row > -1 && col > -1) {
						Object value = table_paramOption.getValueAt(row, col);
						if (value != null && !value.equals("")) {
							tipText = value.toString();
						} else {
							return null;
						}
					}
					return tipText;
				}
			};
			table_paramOption.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			table_paramOption.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "参数代号", "参数名称", "参数类型", "参数范围" }));
			table_paramOption.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			scrollPane.setViewportView(table_paramOption);
			DefaultTableCellHeaderRenderer renderer = new DefaultTableCellHeaderRenderer();
			renderer.setHorizontalAlignment(JLabel.CENTER);
			table_paramOption.setDefaultRenderer(Object.class, renderer);
			paramTableModel = (DefaultTableModel) table_paramOption.getModel();

			JLabel label_paramCode = new JLabel("参数代号");
			label_paramCode.setBounds(12, 13, 54, 15);
			panel_param.add(label_paramCode);

			textField_paramCode = new JTextField();
			textField_paramCode.setColumns(10);
			textField_paramCode.setBounds(76, 10, 88, 21);
			panel_param.add(textField_paramCode);

			JLabel label_paramName = new JLabel("参数名称");
			label_paramName.setBounds(182, 13, 54, 15);
			panel_param.add(label_paramName);

			textField_paramName = new JTextField();
			textField_paramName.setColumns(10);
			textField_paramName.setBounds(246, 10, 88, 21);
			panel_param.add(textField_paramName);

			try {
				elevatorType = topBomLine.getProperty("bl_item_item_id");
				if (elevatorType.endsWith(ConstDefine.TC_GROUP_ELECTRICAL) || elevatorType.endsWith(ConstDefine.TC_GROUP_MECHANICAL)) {
					elevatorType = elevatorType.substring(0, elevatorType.length() - 2);
				}
				elevatorRev = topBomLine.getProperty("bl_rev_item_revision_id");
			} catch (TCException e2) {
				e2.printStackTrace();
			}
			textField_elevatorType.setText(elevatorType);
			textField_rev.setText(elevatorRev);

			// 创建根节点
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(elevatorType);
			paramInfoList = new ArrayList<VariabelConditionTableBean>();
			// 通过梯型代号找到对应的梯型ID-elevatorModelID
			try {
				Class.forName(ConstDefine.TCDB_CLASSNAME);
				connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
				// 找梯型ID
				String queryElevatorModelIDSql = "select elevator_model_id from t_elevator_model where elevator_model_code='"
						+ elevatorType + "'";
				if (connection == null) {
					MessageBox.post("数据库连接失败。", "出错", MessageBox.ERROR);
					return;
				}
				statement = connection.createStatement();
				resultSet = statement.executeQuery(queryElevatorModelIDSql);
				resultSetMetaData = resultSet.getMetaData();
				while (resultSet.next()) {
					columName = resultSetMetaData.getColumnLabel(1);
					elevatorModelID = resultSet.getString(columName);
				}
				// 查找梯型ID对应的梯型参数分类二级
				String queryEPCSql = "select epc_id,epc_name from t_epc where elevator_model_id='" + elevatorModelID + "'";
				statement = connection.createStatement();
				ResultSet resultSet2 = statement.executeQuery(queryEPCSql);
				ResultSetMetaData resultSetMetaData2 = resultSet2.getMetaData();
				while (resultSet2.next()) {
					columName = resultSetMetaData2.getColumnLabel(1);
					EPCID = resultSet2.getString(columName);
					columName = resultSetMetaData2.getColumnLabel(2);
					EPCName = resultSet2.getString(columName);
					DefaultMutableTreeNode secondNode = new DefaultMutableTreeNode(EPCName);
					rootNode.add(secondNode);
					// 三级
					String queryElevatorParamSql = "select elevator_param_id,param_code,param_name,param_type from t_elevator_param where epc_id='"
							+ EPCID + "'";
					connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
					statement = connection.createStatement();
					ResultSet resultSet3 = statement.executeQuery(queryElevatorParamSql);
					ResultSetMetaData resultSetMetaData3 = resultSet3.getMetaData();
					while (resultSet3.next()) {
						VariabelConditionTableBean bean = new VariabelConditionTableBean();
						columName = resultSetMetaData3.getColumnLabel(1);
						String elevatorParamID = resultSet3.getString(columName);
						String queryParamRange = "select range_value from t_elevator_param_range where elevator_param_id ='"
								+ elevatorParamID + "'";
						connection = DriverManager.getConnection(ConstDefine.TCDB_URL, ConstDefine.TCDB_USER, ConstDefine.TCDB_PASSWORD);
						statement = connection.createStatement();
						ResultSet resultSet4 = statement.executeQuery(queryParamRange);
						ResultSetMetaData resultSetMetaData4 = resultSet4.getMetaData();
						String paramRange = "";
						while (resultSet4.next()) {
							columName = resultSetMetaData4.getColumnLabel(1);
							paramRange = paramRange + resultSet4.getString(columName) + ";";
						}
						if (paramRange != null && !paramRange.equals("")){
							bean.rangeOfParamValue = paramRange.substring(0, paramRange.length() - 1);
						}
						bean.paramClassification = EPCName;
						columName = resultSetMetaData3.getColumnLabel(2);
						bean.paramCode = resultSet3.getString(columName);
						columName = resultSetMetaData3.getColumnLabel(3);
						bean.paramName = resultSet3.getString(columName);
						columName = resultSetMetaData3.getColumnLabel(4);
						bean.paramType = resultSet3.getString(columName);
						if (bean.paramType.equals("Double")) {
							bean.paramType = "实数";
						} else if (bean.paramType.equals("Int")) {
							bean.paramType = "整数";
						} else if (bean.paramType.equals("Text")) {
							bean.paramType = "文本";
						}
						DefaultMutableTreeNode ThirdNode = new DefaultMutableTreeNode(bean.paramCode);
						secondNode.add(ThirdNode);
						paramInfoList.add(bean);
					}
				}
			} catch (ClassNotFoundException e2) {
				e2.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				try {
					if (resultSet != null) {
						resultSet.close();
						resultSet = null;
					}
					if (statement != null) {
						statement.close();
						statement = null;
					}
					if (connection != null) {
						connection.close();
						connection = null;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			tree = new JTree(rootNode);
			getContentPane().add(tree);
			// 设置显示根节点句柄
			tree.setShowsRootHandles(true);
			// 单击节点
			tree.addTreeSelectionListener(new TreeSelectionListener() {
				@Override
				public void valueChanged(TreeSelectionEvent arg0) {
					if (arg0.getPath().getPathCount() == 3) {
						// 显示在表格中
						paramTableModel.setRowCount(0);
						String paramCode = arg0.getPath().getLastPathComponent().toString();
						for (int i = 0; i < paramInfoList.size(); i++) {
							if (paramCode.equals(paramInfoList.get(i).paramCode)) {
								paramTableModel.addRow(new String[] { paramCode, paramInfoList.get(i).paramName,
										paramInfoList.get(i).paramType, paramInfoList.get(i).rangeOfParamValue });
							}
						}
					}
				}
			});

			// 将数据库中的参数代号和参数类型存到MAP里
			paramCodeAndTypeMap = new HashMap<String, String>();
			paramCodeAndTypeMap = editVariableConditionService.getParamCodeAndTypeMap(paramInfoList);
			JScrollPane scrollPane2 = new JScrollPane(tree);
			scrollPane2.setBounds(10, 10, 129, 677);
			getContentPane().add(scrollPane2);

			// 双击监听
			tree.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent arg0) {
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
				}

				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (arg0.getClickCount() == 2 && tree.getPathForLocation(arg0.getX(), arg0.getY()).getPathCount() == 3) {
						currentPosition = textArea_varibleCondition.getCaretPosition();
						tailString = textArea_varibleCondition.getText().substring(currentPosition);
						headString = textArea_varibleCondition.getText().substring(0, currentPosition);
						textArea_varibleCondition.setText(headString
								+ tree.getPathForLocation(arg0.getX(), arg0.getY()).getLastPathComponent().toString() + tailString);
						// TODO Jr 获取编辑位置
						setPosition = headString.length()
								+ tree.getPathForLocation(arg0.getX(), arg0.getY()).getLastPathComponent().toString().length();
						textArea_varibleCondition.setCaretPosition(setPosition);
						textArea_varibleCondition.grabFocus();
					}
				}
			});

			// 双击表格
			table_paramOption.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					if (event.getButton() == java.awt.event.MouseEvent.BUTTON1) {
						if (table_paramOption.rowAtPoint(event.getPoint()) < 0) {
							return;
						}
						if (table_paramOption.getSelectedColumn() == 0 && event.getClickCount() == 2) {
							String paramCode = (String) table_paramOption.getValueAt(table_paramOption.getSelectedRow(), 0); // 参数代号
							currentPosition = textArea_varibleCondition.getCaretPosition();
							tailString = textArea_varibleCondition.getText().substring(currentPosition);
							headString = textArea_varibleCondition.getText().substring(0, currentPosition);
							textArea_varibleCondition.setText(headString + paramCode + tailString);
							setPosition = headString.length() + paramCode.length();
							textArea_varibleCondition.setCaretPosition(setPosition);
							textArea_varibleCondition.grabFocus();
						}
					}
				}
			});

			button_refresh.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					AbstractAIFUIApplication application = AIFUtility.getCurrentApplication();
					InterfaceAIFComponent selComp = application.getTargetComponent();
					if (selComp != null && selComp instanceof TCComponentBOMLine) {
						TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
						try {
							textArea_varibleCondition.setText("");
							textField_BOMLine.setText(bomLine.getProperty("bl_indented_title"));
							
							String note1 = bomLine.getProperty("S2_bl_vc");
							String note2 = bomLine.getProperty("S2_bl_vc1");
							String note3 = bomLine.getProperty("S2_bl_vc2");
							String note4 = bomLine.getProperty("S2_bl_vc3");
							String note5 = bomLine.getProperty("S2_bl_vc4");
							defaultVariableCondition = note1 + note2 + note3 + note4 + note5;
							
							TextBean bean = new TextBean(defaultVariableCondition, attrSet);
							InsertToJTextPane(bean);
//							textArea_varibleCondition.setText(defaultVariableCondition);
						} catch (TCException e1) {
							e1.printStackTrace();
						}
//						textArea_varibleCondition.grabFocus();
					} else {
						MessageBox.post("请选择BOM行。", "编辑变量条件", com.teamcenter.rac.util.MessageBox.ERROR);
					}
				}
			});

			button_greater.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					currentPosition = textArea_varibleCondition.getCaretPosition();
					tailString = textArea_varibleCondition.getText().substring(currentPosition);
					headString = textArea_varibleCondition.getText().substring(0, currentPosition);
					textArea_varibleCondition.setText(headString + ">" + tailString);
					setPosition = headString.length() + 1;
					textArea_varibleCondition.setCaretPosition(setPosition);
					textArea_varibleCondition.grabFocus();
				}
			});

			button_less.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					currentPosition = textArea_varibleCondition.getCaretPosition();
					tailString = textArea_varibleCondition.getText().substring(currentPosition);
					headString = textArea_varibleCondition.getText().substring(0, currentPosition);
					textArea_varibleCondition.setText(headString + "<" + tailString);
					setPosition = headString.length() + 1;
					textArea_varibleCondition.setCaretPosition(setPosition);
					textArea_varibleCondition.grabFocus();
				}
			});

			button_greaterOrEqualTo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					currentPosition = textArea_varibleCondition.getCaretPosition();
					tailString = textArea_varibleCondition.getText().substring(currentPosition);
					headString = textArea_varibleCondition.getText().substring(0, currentPosition);
					textArea_varibleCondition.setText(headString + ">=" + tailString);
					setPosition = headString.length() + 2;
					textArea_varibleCondition.setCaretPosition(setPosition);
					textArea_varibleCondition.grabFocus();
				}
			});

			button_lessOrEqualTo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					currentPosition = textArea_varibleCondition.getCaretPosition();
					tailString = textArea_varibleCondition.getText().substring(currentPosition);
					headString = textArea_varibleCondition.getText().substring(0, currentPosition);
					textArea_varibleCondition.setText(headString + "<=" + tailString);
					setPosition = headString.length() + 2;
					textArea_varibleCondition.setCaretPosition(setPosition);
					textArea_varibleCondition.grabFocus();
				}
			});

			button_equalTo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					currentPosition = textArea_varibleCondition.getCaretPosition();
					tailString = textArea_varibleCondition.getText().substring(currentPosition);
					headString = textArea_varibleCondition.getText().substring(0, currentPosition);
					textArea_varibleCondition.setText(headString + "==" + tailString);
					setPosition = headString.length() + 2;
					textArea_varibleCondition.setCaretPosition(setPosition);
					textArea_varibleCondition.grabFocus();
				}
			});

			button_leftParenthesis.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					currentPosition = textArea_varibleCondition.getCaretPosition();
					tailString = textArea_varibleCondition.getText().substring(currentPosition);
					headString = textArea_varibleCondition.getText().substring(0, currentPosition);
					textArea_varibleCondition.setText(headString + "(" + tailString);
					setPosition = headString.length() + 1;
					textArea_varibleCondition.setCaretPosition(setPosition);
					textArea_varibleCondition.grabFocus();
				}
			});

			button_rightParenthesis.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					currentPosition = textArea_varibleCondition.getCaretPosition();
					tailString = textArea_varibleCondition.getText().substring(currentPosition);
					headString = textArea_varibleCondition.getText().substring(0, currentPosition);
					textArea_varibleCondition.setText(headString + ")" + tailString);
					setPosition = headString.length() + 1;
					textArea_varibleCondition.setCaretPosition(setPosition);
					textArea_varibleCondition.grabFocus();
				}
			});

			button_with.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					currentPosition = textArea_varibleCondition.getCaretPosition();
					tailString = textArea_varibleCondition.getText().substring(currentPosition);
					headString = textArea_varibleCondition.getText().substring(0, currentPosition);
					textArea_varibleCondition.setText(headString + "&&" + tailString);
					setPosition = headString.length() + 2;
					textArea_varibleCondition.setCaretPosition(setPosition);
					textArea_varibleCondition.grabFocus();
				}
			});

			button_or.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					currentPosition = textArea_varibleCondition.getCaretPosition();
					tailString = textArea_varibleCondition.getText().substring(currentPosition);
					headString = textArea_varibleCondition.getText().substring(0, currentPosition);
					textArea_varibleCondition.setText(headString + "||" + tailString);
					setPosition = headString.length() + 2;
					textArea_varibleCondition.setCaretPosition(setPosition);
					textArea_varibleCondition.grabFocus();
				}
			});

			button_nor.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					currentPosition = textArea_varibleCondition.getCaretPosition();
					tailString = textArea_varibleCondition.getText().substring(currentPosition);
					headString = textArea_varibleCondition.getText().substring(0, currentPosition);
					textArea_varibleCondition.setText(headString + "!=" + tailString);
					setPosition = headString.length() + 2;
					textArea_varibleCondition.setCaretPosition(setPosition);
					textArea_varibleCondition.grabFocus();

				}
			});

//			// 获取
//			button_getValue.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					AbstractAIFUIApplication application = AIFUtility.getCurrentApplication();
//					InterfaceAIFComponent selComp = application.getTargetComponent();
//					TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
//					try {
//						String note1 = bomLine.getProperty("S2_bl_vc");
//						String note2 = bomLine.getProperty("S2_bl_vc1");
//						String note3 = bomLine.getProperty("S2_bl_vc2");
//						String note4 = bomLine.getProperty("S2_bl_vc3");
//						String note5 = bomLine.getProperty("S2_bl_vc4");
//						defaultVariableCondition = note1 + note2 + note3 + note4 + note5;
//						textArea_varibleCondition.setText(defaultVariableCondition);
//					} catch (TCException e1) {
//						e1.printStackTrace();
//					}
//					textArea_varibleCondition.grabFocus();
//				}
//			});
			// 清除
			button_clear.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					TextBean bean = new TextBean("0", attrSet);
					InsertToJTextPane(bean);
					textArea_varibleCondition.setText("");
					textArea_varibleCondition.grabFocus();
				}
			});
			// 搜索
			textField_paramCode.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent documentevent) {
					editVariableConditionService.searchParam(paramTableModel, paramInfoList, textField_paramCode.getText(),
							textField_paramName.getText());
				}

				@Override
				public void insertUpdate(DocumentEvent documentevent) {
					editVariableConditionService.searchParam(paramTableModel, paramInfoList, textField_paramCode.getText(),
							textField_paramName.getText());
				}

				@Override
				public void changedUpdate(DocumentEvent documentevent) {
					editVariableConditionService.searchParam(paramTableModel, paramInfoList, textField_paramCode.getText(),
							textField_paramName.getText());
				}
			});
			textField_paramName.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent documentevent) {
					editVariableConditionService.searchParam(paramTableModel, paramInfoList, textField_paramCode.getText(),
							textField_paramName.getText());
				}

				@Override
				public void insertUpdate(DocumentEvent documentevent) {
					editVariableConditionService.searchParam(paramTableModel, paramInfoList, textField_paramCode.getText(),
							textField_paramName.getText());
				}

				@Override
				public void changedUpdate(DocumentEvent documentevent) {
					editVariableConditionService.searchParam(paramTableModel, paramInfoList, textField_paramCode.getText(),
							textField_paramName.getText());
				}
			});

			// 确认
			button_confirm.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					AbstractAIFUIApplication application = AIFUtility.getCurrentApplication();
					InterfaceAIFComponent selComp = application.getTargetComponent();
					boolean isSelectedOk = false;
					if (selComp != null && selComp instanceof TCComponentBOMLine) {
						isSelectedOk = true;
						TCComponentBOMLine bomLine = (TCComponentBOMLine) selComp;
						String variableCondition = textArea_varibleCondition.getText();
						if (!variableCondition.equals("")) {
							variableCondition = variableCondition.replaceAll("\r|\n|\r\n| ", "");
							variableCondition = variableCondition.replaceAll("!=", "!==");
							try {
								// 判断是否符合，报错就不符合
								if (!MathUtil.PassConditionNotEmpty(variableCondition) && !MathUtil.PassCondition(variableCondition)) {
									if (editVariableConditionService.checkType(variableCondition, paramCodeAndTypeMap)) {
										// 判断条件的长度得填在BOM的几个属性里
										// TODO Jr 要处理汉字长度
//										int count;
										variableCondition = variableCondition.replaceAll("!==", "!=");
										List<String> list = StringUtil.GetStrListByCharacterSize(variableCondition, 160);
										int listSize = list.size();
										if (listSize > 4) {
											MessageBox.post("变量条件过长，无法存储", "提示", com.teamcenter.rac.util.MessageBox.ERROR);
											return;
										}
										for (int i = 0; i < 5; i++) {
											String index = i == 0 ? "" : ""+i;
											String property = "S2_bl_vc" + index;
											bomLine.setProperty(property, "");
										}
										for (int i = 0; i < listSize; i++) {
											String index = i == 0 ? "" : ""+i;
											String property = "S2_bl_vc" + index;
											bomLine.setProperty(property, list.get(i));
										}
										
//										if (variableCondition.length() % 160 == 0) {
//											count = variableCondition.length() / 160;
//										} else {
//											count = variableCondition.length() / 160 + 1;
//										}
//										if (count == 1) {
//											bomLine.setProperty("S2_bl_vc", variableCondition);
//										} else if (count == 2) {
//											String note1 = variableCondition.substring(0, 160);
//											String note2 = variableCondition.substring(160);
//											bomLine.setProperty("S2_bl_vc", note1);
//											bomLine.setProperty("S2_bl_vc1", note2);
//										} else if (count == 3) {
//											String note1 = variableCondition.substring(0, 160);
//											String note2 = variableCondition.substring(160, 320);
//											String note3 = variableCondition.substring(320);
//											bomLine.setProperty("S2_bl_vc", note1);
//											bomLine.setProperty("S2_bl_vc1", note2);
//											bomLine.setProperty("S2_bl_vc2", note3);
//										} else if (count == 4) {
//											String note1 = variableCondition.substring(0, 160);
//											String note2 = variableCondition.substring(160, 320);
//											String note3 = variableCondition.substring(320, 480);
//											String note4 = variableCondition.substring(480);
//											bomLine.setProperty("S2_bl_vc", note1);
//											bomLine.setProperty("S2_bl_vc1", note2);
//											bomLine.setProperty("S2_bl_vc2", note3);
//											bomLine.setProperty("S2_bl_vc3", note4);
//										} else if (count == 5) {
//											String note1 = variableCondition.substring(0, 160);
//											String note2 = variableCondition.substring(160, 320);
//											String note3 = variableCondition.substring(320, 480);
//											String note4 = variableCondition.substring(480, 640);
//											String note5 = variableCondition.substring(640);
//											bomLine.setProperty("S2_bl_vc", note1);
//											bomLine.setProperty("S2_bl_vc1", note2);
//											bomLine.setProperty("S2_bl_vc2", note3);
//											bomLine.setProperty("S2_bl_vc3", note4);
//											bomLine.setProperty("S2_bl_vc4", note5);
//										}
									}
								}
							} catch (CalculateException e1) {
								e1.printStackTrace();
								MessageBox.post("条件出错：" + e1.getCalculatePath(), "编辑变量条件", com.teamcenter.rac.util.MessageBox.ERROR);
								return;
							} catch (TCException e2) {
								e2.printStackTrace();
								MessageBox.post(e2.getMessage(), "编辑变量条件", com.teamcenter.rac.util.MessageBox.ERROR);
								return;
							}
						}else {
							// 如果变量条件为空，提示是否清空
							int n = JOptionPane.showConfirmDialog(panel, "确认清空当前行的变量条件？", "警告", JOptionPane.YES_NO_OPTION);
							if (n == 0) {
								try {
									bomLine.setProperty("S2_bl_vc", "");
									bomLine.setProperty("S2_bl_vc1", "");
									bomLine.setProperty("S2_bl_vc2", "");
									bomLine.setProperty("S2_bl_vc3", "");
									bomLine.setProperty("S2_bl_vc4", "");
								} catch (TCException e1) {
									e1.printStackTrace();
								}
							}
						}
					}
					if (!isSelectedOk) {
						com.teamcenter.rac.util.MessageBox.post("请选择BOM行进行编辑。", "编辑变量条件", com.teamcenter.rac.util.MessageBox.ERROR);
					}
					textArea_varibleCondition.grabFocus();
				}
			});
			// 取消
			button_cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
		
		private void ChangeTextColor() {
//			textArea_varibleCondition.setText("");
			String content = textArea_varibleCondition.getText();
	    	Pattern pattern = Pattern.compile("\\+\\+|==|!=|>=|<=|>|<|\\(|\\)|\\&\\&|\\|\\|");
	    	
	    	List<TextBean> beanList = new ArrayList<TextBean>();
	    	while (!content.equals("")) {
	    		Matcher matcher = pattern.matcher(content);
	    		if (matcher.find()) {
	    			int start = matcher.start();
	    			int end = matcher.end();
	    			int length = content.length();
	    			String str = content.substring(0, start);
	    			String str1 = matcher.group();
	    			if (start != 0 && content.charAt(start-1) != ' ') {
	    				str1 = " " + str1;
	    			}
	    			if (end != length && content.charAt(end) != ' ') {
	    				str1 = str1 + " ";
	    			}
	    			content = content.substring(end, length);
	    			
	    			TextBean bean = new TextBean(str, attrSet);
	    			TextBean bean1 = new TextBean(str1, attrSet1);
	    			beanList.add(bean);
	    			beanList.add(bean1);
	    		} else {
	    			TextBean bean = new TextBean(content, attrSet);
	    			beanList.add(bean);
	    			content = "";
	    		}
	    	}
	    	
	    	textArea_varibleCondition.setText("");
	    	for (TextBean bean : beanList) {
	    		InsertToJTextPane(bean);
	    	}
		}
		
//		private void ResetJTextPane(TextBean bean) {
//			Document doc = textArea_varibleCondition.getDocument();
//			try {
//				doc.insertString(doc.getLength(), bean.str, bean.attributeSet);
//			} catch (BadLocationException e) {
//				System.out.println("BadLocationException: " + e);
//			}
//		}
		
		private void InsertToJTextPane(TextBean bean) {
			Document doc = textArea_varibleCondition.getDocument();
			try {
				doc.insertString(doc.getLength(), bean.str, bean.attributeSet);
			} catch (BadLocationException e) {
				System.out.println("BadLocationException: " + e);
			}
		}
		
		private void InitSimpleAttributeSet() {
			attrSet = new SimpleAttributeSet();
			StyleConstants.setForeground(attrSet, Color.BLACK);
			
			attrSet1 = new SimpleAttributeSet();
			StyleConstants.setForeground(attrSet1, Color.RED);
			StyleConstants.setBold(attrSet1, true);
		}
	}
}

class MyJTextPane extends JTextPane {
	private static final long serialVersionUID = 1L;

	private class WarpEditorKit extends StyledEditorKit {
		private static final long serialVersionUID = 1L;

		private ViewFactory defaultFactory = new WarpColumnFactory();

		@Override
		public ViewFactory getViewFactory() {
			return defaultFactory;
		}
	}

	private class WarpColumnFactory implements ViewFactory {

		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new WarpLabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new ParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}

			// default to text display
			return new LabelView(elem);
		}
	}

	private class WarpLabelView extends LabelView {

		public WarpLabelView(Element elem) {
			super(elem);
		}

		@Override
		public float getMinimumSpan(int axis) {
			switch (axis) {
			case View.X_AXIS:
				return 0;
			case View.Y_AXIS:
				return super.getMinimumSpan(axis);
			default:
				throw new IllegalArgumentException("Invalid axis: " + axis);
			}
		}
	}

	public MyJTextPane() {
		super();
		this.setEditorKit(new WarpEditorKit());
	}
}

class TextBean {
	public String str;
	public AttributeSet attributeSet;
	
	public TextBean(String str, AttributeSet attributeSet) {
		this.str = str;
		this.attributeSet = attributeSet;
	}
}

